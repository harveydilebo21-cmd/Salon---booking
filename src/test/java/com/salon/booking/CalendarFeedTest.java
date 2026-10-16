package com.salon.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class CalendarFeedTest {

    private static final ZoneId JOHANNESBURG = ZoneId.of("Africa/Johannesburg");   // UTC+2, no daylight saving
    private static final DateTimeFormatter UTC_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    private Booking booking(long id, String name, String service, LocalDateTime start, String note) {
        Booking booking = new Booking();
        ReflectionTestUtils.setField(booking, "id", id);   // ids are normally set by the database
        booking.setClientName(name);
        booking.setPhone("0712345678");
        booking.setService(service);
        booking.setRequestedTime(start);
        booking.setStylistNote(note);
        booking.setStatus(BookingStatus.CONFIRMED);
        return booking;
    }

    private String feedFor(Booking... bookings) {
        return CalendarFeed.build(List.of(bookings), JOHANNESBURG);
    }

    @Test
    void anEmptyFeedIsStillAValidCalendar() {
        String ics = CalendarFeed.build(List.of(), JOHANNESBURG);

        assertThat(ics).startsWith("BEGIN:VCALENDAR\r\n");
        assertThat(ics).endsWith("END:VCALENDAR\r\n");
        assertThat(ics).doesNotContain("BEGIN:VEVENT");
    }

    @Test
    void eachBookingBecomesOneEventWithAStableId() {
        String ics = feedFor(booking(7, "Jo", "Haircut", LocalDateTime.of(2030, 3, 4, 10, 0), null));

        assertThat(ics.split("BEGIN:VEVENT", -1).length - 1).isEqualTo(1);
        assertThat(ics).contains("UID:booking-7@salon-bookings");
    }

    @Test
    void localTimesAreConvertedToUtc() {
        LocalDateTime start = LocalDateTime.of(2030, 3, 4, 10, 0);   // 10:00 in Johannesburg = 08:00 UTC
        String ics = feedFor(booking(7, "Jo", "Haircut", start, null));

        LocalDateTime end = start.plusMinutes(ServiceCatalog.minutesFor("Haircut"));
        String expectedEnd = end.atZone(JOHANNESBURG).withZoneSameInstant(ZoneOffset.UTC).format(UTC_STAMP);

        assertThat(ics).contains("DTSTART:20300304T080000Z");
        assertThat(ics).contains("DTEND:" + expectedEnd);
    }

    @Test
    void phoneAndNoteAppearInTheDescription() {
        String ics = feedFor(booking(7, "Jo", "Haircut", LocalDateTime.of(2030, 3, 4, 10, 0), "Bring photos"));

        // A line break inside text is written as the two characters backslash and n
        assertThat(ics).contains("DESCRIPTION:Phone: 0712345678\\nNote: Bring photos");
    }

    @Test
    void commasAndSemicolonsInNamesAreEscaped() {
        String ics = feedFor(booking(7, "Smith, Jo; Test", "Haircut", LocalDateTime.of(2030, 3, 4, 10, 0), null));

        assertThat(ics).contains("SUMMARY:Haircut - Smith\\, Jo\\; Test");
    }

    @Test
    void lineBreaksInANameCannotInjectExtraCalendarFields() {
        String evilName = "Evil\r\nATTENDEE:mailto:attacker@example.com";
        String ics = feedFor(booking(7, evilName, "Haircut", LocalDateTime.of(2030, 3, 4, 10, 0), null));

        for (String line : ics.split("\r\n")) {
            assertThat(line).doesNotStartWith("ATTENDEE");
        }
        // The only real line breaks in the file are the CRLF endings the format requires
        assertThat(ics.replace("\r\n", "")).doesNotContain("\n").doesNotContain("\r");
    }

    @Test
    void longLinesAreFoldedToAtMost75Bytes() {
        String longNote = "x".repeat(200);
        String ics = feedFor(booking(7, "Jo", "Haircut", LocalDateTime.of(2030, 3, 4, 10, 0), longNote));

        for (String line : ics.split("\r\n")) {
            assertThat(line.getBytes(StandardCharsets.UTF_8).length).isLessThanOrEqualTo(75);
        }
    }
}