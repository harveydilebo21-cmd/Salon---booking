package com.salon.booking;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StylistController {

    @GetMapping("/login")
    public String login() {
        return "login";

    }

    @GetMapping("/stylist")
    public String dashboard() {
        return "stylist";
    }
}
