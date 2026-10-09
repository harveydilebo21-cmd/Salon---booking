package com.salon.booking;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BookingController {

    private final BookingRepository bookingRepository;

    public BookingController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @ModelAttribute("services")
    public List<String> services() {
        return List.of("Haircut", "Colour", "Braids", "Styling");
    }

    @GetMapping("/book")
    public String showForm(Model model) {
        model.addAttribute("bookingForm", new BookingForm());
        return "book";
    }

    @PostMapping("/book")
    public String submit(@Valid @ModelAttribute("bookingForm") BookingForm form,
                         BindingResult result) {
        if (result.hasErrors()) {
            return "book";
        }

        Booking booking = new Booking();
        booking.setClientName(form.getClientName());
        booking.setPhone(form.getPhone());
        booking.setService(form.getService());
        booking.setRequestedTime(form.getRequestedTime());
        bookingRepository.save(booking);

        return "redirect:/status/" + booking.getToken();
    }

    @GetMapping("/status/{token}")
    public String status(@PathVariable String token, Model model) {
        model.addAttribute("booking", findByToken(token));
        return "status";
    }

    @PostMapping("/status/{token}/accept")
    public String accept(@PathVariable String token, RedirectAttributes redirect) {
        Booking booking = findByToken(token);
        try {
            booking.acceptProposedTime();
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message", "Thank you, your booking is confirmed for the new time.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/status/" + token;
    }

    @PostMapping("/status/{token}/decline")
    public String decline(@PathVariable String token, RedirectAttributes redirect) {
        Booking booking = findByToken(token);
        try {
            booking.declineProposedTime();
            bookingRepository.save(booking);
            redirect.addFlashAttribute("message", "You have declined the new time. The booking is canceled.");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/status/" + token;
    }

    private Booking findByToken(String token) {
        return bookingRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}