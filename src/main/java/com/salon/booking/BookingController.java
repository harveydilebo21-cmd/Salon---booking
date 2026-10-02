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
        Booking booking = bookingRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("booking", booking);
        return "status";
    }
}