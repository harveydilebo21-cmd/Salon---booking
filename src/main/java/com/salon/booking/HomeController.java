package com.salon.booking;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /** A service as shown on the home page: its name and a friendly duration. */
    public record ServiceInfo(String name, int minutes) {
        public String duration() {
            int hours = minutes / 60;
            int rest = minutes % 60;
            if (hours == 0) {
                return rest + " min";
            }
            String text = hours + (hours == 1 ? " hour" : " hours");
            return rest == 0 ? text : text + " " + rest + " min";
        }
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("services", ServiceCatalog.names().stream()
                .map(name -> new ServiceInfo(name, ServiceCatalog.minutesFor(name)))
                .toList());
        return "home";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }
}