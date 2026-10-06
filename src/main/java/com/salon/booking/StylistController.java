package com.salon.booking;

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

    private static final int MAX_NOTE_LENGTH =255;
    private final BookingRepository bookingRepository;

    public StylistController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/stylist")
    public String dashboard(Model model) {
        model.addAttribute("pending",
                bookingRepository.findByStatusOrderByRequestedTimeAsc(BookingStatus.PENDING));
        model.addAttribute("others",
                bookingRepository.findByStatusNotOrderByRequestedTimeAsc(BookingStatus.PENDING));
        return "stylist";
    }
    @PostMapping("/stylist/bookings/{id}/confirm")
    public String Confirm(@PathVariable Long id,
                          @RequestParam(defaultValue = "") String note,
                          RedirectAttributes redirect){
        String cleaned = note.trim();
        if(cleaned.length() > MAX_NOTE_LENGTH){
            redirect.addFlashAttribute("error","The note is too long (maximum 255 characters).");
            return "redirect:/stylist";
        }
        Booking booking = findBooking(id);
        try{
            booking.confirm(cleaned.isEmpty() ? null : cleaned);
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message","Booking confirmed");
        } catch (IllegalStateException e){
            redirect.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/stylist";
    }
    @PostMapping("/stylist/bookings/{id}/reject")
    public String reject(@PathVariable Long id,
                         @RequestParam(defaultValue = "") String reason,
                         RedirectAttributes redirect){
        String cleaned = reason.trim();
        if(cleaned.isEmpty()){
            redirect.addFlashAttribute("error","The reason is too long (maximum 255 characters).");
            return "redirect:/stylist";
        }
        Booking booking = findBooking(id);
        try{
            booking.reject(cleaned);
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message","Booking rejected.");
        }catch (IllegalStateException e){
            redirect.addFlashAttribute("error",e.getMessage());
        }
        return "redirect:/stylist";
    }

    private Booking findBooking(Long id){
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}