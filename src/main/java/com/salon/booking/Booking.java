package com.salon.booking;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token = UUID.randomUUID().toString();

    private String clientName;
    private String phone;
    private String service;
    private LocalDateTime requestedTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    private String stylistNote;
    private LocalDateTime proposedTime;

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public LocalDateTime getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(LocalDateTime requestedTime) {
        this.requestedTime = requestedTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getStylistNote() {
        return stylistNote;
    }

    public void setStylistNote(String stylistNote) {
        this.stylistNote = stylistNote;
    }

    public LocalDateTime getProposedTime() {
        return proposedTime;
    }

    public void setProposedTime(LocalDateTime proposedTime) {
        this.proposedTime = proposedTime;
    }
    public void confirm(String note){
        requirePending();
        this.status = BookingStatus.CONFIRMED;
        this.stylistNote = note;
    }
    public void reject(String reason){
        requirePending();
        this.status = BookingStatus.REJECTED;
        this.stylistNote = reason;
    }
    private void requirePending(){
        if(status != BookingStatus.PENDING){
            throw new IllegalArgumentException("This booking is no longer pending");
        }
    }
    public void proposeNewTime(LocalDateTime newTime, String reason){
        requirePending();
        this.requestedTime =this.proposedTime;
        this.proposedTime = null;
        this.status = BookingStatus.CONFIRMED;
    }
    public void acceptProposedTime(){
        requireProposed();
        this.requestedTime = this.proposedTime;
        this.proposedTime = null;
        this.status = BookingStatus.CONFIRMED;
    }
    public void declineProposedTime(){
        requireProposed();
        this.status = BookingStatus.CANCELED;
    }
    private void requireProposed(){
        if(status != BookingStatus.NEW_TIME_PROPOSED) {
            throw new IllegalArgumentException("There is no proposed time to respond to.");
        }
    }

}