package com.salon.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StylistController {

    private static final int MAX_NOTE_LENGTH = 255;

    private final BookingRepository bookingRepository;
    private final AvailabilityService availability;

    public StylistController(BookingRepository bookingRepository, AvailabilityService availability) {
        this.bookingRepository = bookingRepository;
        this.availability = availability;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/stylist")
    public String dashboard(Model model) {
        List<Booking> pending =
                bookingRepository.findByStatusOrderByRequestedTimeAsc(BookingStatus.PENDING);

        // Ids of pending requests that overlap a confirmed booking, so the page can warn her
        Set<Long> clashing = pending.stream()
                .filter(b -> !availability.isFree(b.getRequestedTime(), b.getService(), b.getId()))
                .map(Booking::getId)
                .collect(Collectors.toSet());

        model.addAttribute("pending", pending);
        model.addAttribute("clashing", clashing);
        model.addAttribute("others",
                bookingRepository.findByStatusNotOrderByRequestedTimeAsc(BookingStatus.PENDING));
        return "stylist";
    }

    @PostMapping("/stylist/bookings/{id}/confirm")
    public String confirm(@PathVariable Long id,
                          @RequestParam(defaultValue = "") String note,
                          RedirectAttributes redirect) {
        String cleaned = note.trim();
        if (cleaned.length() > MAX_NOTE_LENGTH) {
            redirect.addFlashAttribute("error", "The note is too long (maximum 255 characters).");
            return "redirect:/stylist";
        }
        Booking booking = findBooking(id);
        if (booking.getStatus() == BookingStatus.PENDING
                && !availability.isFree(booking.getRequestedTime(), booking.getService(), booking.getId())) {
            redirect.addFlashAttribute("error",
                    "That time overlaps a confirmed booking. Suggest a new time or reject the request.");
            return "redirect:/stylist";
        }
        try {
            booking.confirm(cleaned.isEmpty() ? null : cleaned);
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message", "Booking confirmed.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/stylist";
    }

    @PostMapping("/stylist/bookings/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(defaultValue = "") String reason,
                         RedirectAttributes redirect) {
        String cleaned = reason.trim();
        if (cleaned.isEmpty()) {
            redirect.addFlashAttribute("error", "Please give a reason when rejecting a booking.");
            return "redirect:/stylist";
        }
        if (cleaned.length() > MAX_NOTE_LENGTH) {
            redirect.addFlashAttribute("error", "The reason is too long (maximum 255 characters).");
            return "redirect:/stylist";
        }
        Booking booking = findBooking(id);
        try {
            booking.reject(cleaned);
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message", "Booking rejected.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/stylist";
    }

    @PostMapping("/stylist/bookings/{id}/propose")
    public String propose(@PathVariable Long id,
                          @RequestParam(required = false)
                          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime proposedTime,
                          @RequestParam(defaultValue = "") String reason,
                          RedirectAttributes redirect) {
        String cleaned = reason.trim();
        if (proposedTime == null || !proposedTime.isAfter(LocalDateTime.now())) {
            redirect.addFlashAttribute("error", "Please choose a new time in the future.");
            return "redirect:/stylist";
        }
        if (cleaned.isEmpty() || cleaned.length() > MAX_NOTE_LENGTH) {
            redirect.addFlashAttribute("error",
                    "Please give a reason (up to 255 characters) for the new time.");
            return "redirect:/stylist";
        }
        Booking booking = findBooking(id);
        if (booking.getStatus() == BookingStatus.PENDING
                && !availability.isFree(proposedTime, booking.getService(), booking.getId())) {
            redirect.addFlashAttribute("error",
                    "You already have a confirmed booking at that time. Choose another time.");
            return "redirect:/stylist";
        }
        try {
            booking.proposeNewTime(proposedTime, cleaned);
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message",
                    "New time proposed. The client can accept or decline on their status page.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/stylist";
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}