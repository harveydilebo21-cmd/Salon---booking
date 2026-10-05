package com.salon.booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StylistController {

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
}