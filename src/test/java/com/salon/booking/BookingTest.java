package com.salon.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class BookingTest {

    // A pending booking two days from now: the starting point for most tests
    private Booking pendingBooking() {
        Booking booking = new Booking();
        booking.setClientName("Test Client");
        booking.setPhone("0712345678");
        booking.setService("Haircut");
        booking.setRequestedTime(LocalDateTime.now().plusDays(2));
        return booking;
    }

    @Test
    void newBookingStartsAsPending() {
        assertThat(pendingBooking().getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void confirmingAPendingBookingConfirmsItAndKeepsTheNote() {
        Booking booking = pendingBooking();

        booking.confirm("See you then");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getStylistNote()).isEqualTo("See you then");
    }

    @Test
    void aBookingCannotBeConfirmedTwice() {
        Booking booking = pendingBooking();
        booking.confirm(null);

        assertThatThrownBy(() -> booking.confirm(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no longer pending");
    }

    @Test
    void aConfirmedBookingCannotBeRejected() {
        Booking booking = pendingBooking();
        booking.confirm(null);

        assertThatThrownBy(() -> booking.reject("Changed my mind"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no longer pending");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void rejectingStoresTheReason() {
        Booking booking = pendingBooking();

        booking.reject("Fully booked that day");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
        assertThat(booking.getStylistNote()).isEqualTo("Fully booked that day");
    }

    @Test
    void aBookingInThePastCannotBeConfirmed() {
        Booking booking = pendingBooking();
        booking.setRequestedTime(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> booking.confirm(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already passed");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void proposingANewTimeMovesTheBookingToNewTimeProposed() {
        Booking booking = pendingBooking();
        LocalDateTime newTime = LocalDateTime.now().plusDays(5);

        booking.proposeNewTime(newTime, "I am away that day");

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.NEW_TIME_PROPOSED);
        assertThat(booking.getProposedTime()).isEqualTo(newTime);
        assertThat(booking.getStylistNote()).isEqualTo("I am away that day");
    }

    @Test
    void acceptingTheProposedTimeConfirmsTheBookingAtTheNewTime() {
        Booking booking = pendingBooking();
        LocalDateTime newTime = LocalDateTime.now().plusDays(5);
        booking.proposeNewTime(newTime, "I am away that day");

        booking.acceptProposedTime();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.getRequestedTime()).isEqualTo(newTime);
        assertThat(booking.getProposedTime()).isNull();
    }

    @Test
    void decliningTheProposedTimeCancelsTheBooking() {
        Booking booking = pendingBooking();
        booking.proposeNewTime(LocalDateTime.now().plusDays(5), "I am away that day");

        booking.declineProposedTime();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELED);
    }

    @Test
    void thereIsNothingToAcceptUntilATimeIsProposed() {
        Booking booking = pendingBooking();

        assertThatThrownBy(booking::acceptProposedTime)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("no proposed time");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void aProposedTimeThatHasPassedCannotBeAccepted() {
        Booking booking = pendingBooking();
        booking.proposeNewTime(LocalDateTime.now().minusMinutes(5), "Too late");

        assertThatThrownBy(booking::acceptProposedTime)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already passed");
    }
}