package com.salon.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

public class BookingForm {

    @NotBlank(message = "Please enter your name")
    @Size(max = 100, message = "Name is too long")
    private String clientName;

    @NotBlank(message = "Please enter your phone number")
    @Pattern(regexp = "^[0-9+ ]{7,15}$", message = "Use 7 to 15 digits, spaces or +")
    private String phone;

    @NotBlank(message = "Please choose a service")
    private String service;

    @NotNull(message = "Please pick a date and time")
    @Future(message = "The time must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime requestedTime;

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public LocalDateTime getRequestedTime() { return requestedTime; }
    public void setRequestedTime(LocalDateTime requestedTime) { this.requestedTime = requestedTime; }
}