package com.salon.booking;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BookingController{

    @ModelAttribute("services")
    public List<String> services(){
        return List.of("Haircut","Colour","Braids","Styling");
    }
    @GetMapping("/book")
    public String showForm(Model model){
        model.addAttribute("bookingForm", new BookingForm());
        return "book";
    }
    @PostMapping("/book")
    public String submit(@Valid @ModelAttribute("bookingForm") BookingForm form,BindingResult result){
        if(result.hasErrors()){
            return "book";
        }
        System.out.println("New request: " + form.getClientName() + ", "
        + form.getPhone() + ", " + form.getService() + ", " + form.getRequestedTime());
        return "redirect:/book/thanks";
    }
    @GetMapping("/book/thanks")
    public String thanks(){
        return "thanks";
    }
}