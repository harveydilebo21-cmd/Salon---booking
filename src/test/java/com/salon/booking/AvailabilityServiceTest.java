package com.salon.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The repository is replaced by a mock, so these tests check only the overlap logic,
 * with no database. The mock returns whatever confirmed bookings each test sets up.
 */
class AvailabilityServiceTest {

    private final BookingRepository repository = mock(BookingRepository.class);
    private final AvailabilityService availability = new AvailabilityService(repository);

    private final LocalDateTime tenAm = LocalDateTime.of(2030, 1, 15, 10, 0);

    private Booking confirmed(long id, String service, LocalDateTime start) {
        Booking booking = new Booking();
        ReflectionTestUtils.setField(booking, "id", id);   // ids are normally set by the database
        booking.setService(service);
        booking.setRequestedTime(start);
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    private void givenConfirmed(Booking... bookings) {
        when(repository.findByStatusAndRequestedTimeBetween(eq(BookingStatus.CONFIRMED), any(), any()))
                .thenReturn(List.of(bookings));
    }

    private LocalDateTime endOf(Booking booking) {
        return booking.getRequestedTime().plusMinutes(ServiceCatalog.minutesFor(booking.getService()));
    }

    @Test
    void aSlotIsFreeWhenThereAreNoConfirmedBookings() {
        assertThat(availability.isFree(tenAm, "Haircut", 99L)).isTrue();
    }

    @Test
    void aSlotIsTakenWhenItStartsDuringAConfirmedBooking() {
        Booking existing = confirmed(1, "Colour", tenAm);
        givenConfirmed(existing);

        assertThat(availability.isFree(tenAm.plusMinutes(30), "Haircut", 99L)).isFalse();
    }

    @Test
    void aSlotIsTakenWhenItRunsIntoTheStartOfAConfirmedBooking() {
        Booking existing = confirmed(1, "Haircut", tenAm);
        givenConfirmed(existing);

        // A Colour that starts 30 minutes earlier runs past the existing start
        assertThat(availability.isFree(tenAm.minusMinutes(30), "Colour", 99L)).isFalse();
    }

    @Test
    void aSlotIsFreeWhenItStartsExactlyWhenAConfirmedBookingEnds() {
        Booking existing = confirmed(1, "Colour", tenAm);
        givenConfirmed(existing);

        assertThat(availability.isFree(endOf(existing), "Haircut", 99L)).isTrue();
    }

    @Test
    void aSlotIsTakenWhenItStartsOneMinuteBeforeAConfirmedBookingEnds() {
        Booking existing = confirmed(1, "Colour", tenAm);
        givenConfirmed(existing);

        assertThat(availability.isFree(endOf(existing).minusMinutes(1), "Haircut", 99L)).isFalse();
    }

    @Test
    void aSlotIsFreeWhenItEndsExactlyWhenAConfirmedBookingStarts() {
        Booking existing = confirmed(1, "Haircut", tenAm);
        givenConfirmed(existing);

        LocalDateTime start = tenAm.minusMinutes(ServiceCatalog.minutesFor("Haircut"));
        assertThat(availability.isFree(start, "Haircut", 99L)).isTrue();
    }

    @Test
    void aSlotIsTakenWhenItEndsOneMinuteAfterAConfirmedBookingStarts() {
        Booking existing = confirmed(1, "Haircut", tenAm);
        givenConfirmed(existing);

        LocalDateTime start = tenAm.minusMinutes(ServiceCatalog.minutesFor("Haircut")).plusMinutes(1);
        assertThat(availability.isFree(start, "Haircut", 99L)).isFalse();
    }

    @Test
    void aBookingNeverClashesWithItself() {
        Booking existing = confirmed(1, "Colour", tenAm);
        givenConfirmed(existing);

        assertThat(availability.isFree(tenAm, "Colour", 1L)).isTrue();
    }
}