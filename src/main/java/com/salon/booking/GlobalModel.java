package com.salon.booking;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the salon's public details available to every page as "business",
 * so templates can write ${business.phone} like any other model value.
 */
@ControllerAdvice
public class GlobalModel {

    private final BusinessInfo business;

    public GlobalModel(BusinessInfo business) {
        this.business = business;
    }

    @ModelAttribute("business")
    public BusinessInfo business() {
        return business;
    }
}