package com.salon.booking;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Turns confirmed bookings into an iCalendar (.ics) file that calendar apps can subscribe to.
 * Times are converted to UTC so every calendar shows them correctly in the viewer's own time zone.
 */
public final class CalendarFeed {

    private static final DateTimeFormatter UTC_STAMP = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
    private static final int MAX_LINE_OCTETS = 75;

    private CalendarFeed() {
    }

    public static String build(List<Booking> bookings, ZoneId zone) {
        StringBuilder sb = new StringBuilder();
        line(sb, "BEGIN:VCALENDAR");
        line(sb, "VERSION:2.0");
        line(sb, "PRODID:-//Salon Bookings//Booking feed//EN");
        line(sb, "CALSCALE:GREGORIAN");
        line(sb, "METHOD:PUBLISH");
        line(sb, "X-WR-CALNAME:Salon bookings");

        String stamp = ZonedDateTime.now(ZoneOffset.UTC).format(UTC_STAMP);
        for (Booking b : bookings) {
            LocalDateTime start = b.getRequestedTime();
            LocalDateTime end = start.plusMinutes(ServiceCatalog.minutesFor(b.getService()));

            line(sb, "BEGIN:VEVENT");
            line(sb, "UID:booking-" + b.getId() + "@salon-bookings");
            line(sb, "DTSTAMP:" + stamp);
            line(sb, "DTSTART:" + toUtc(start, zone));
            line(sb, "DTEND:" + toUtc(end, zone));
            line(sb, "SUMMARY:" + escape(b.getService() + " - " + b.getClientName()));
            line(sb, "DESCRIPTION:" + escape(description(b)));
            line(sb, "END:VEVENT");
        }

        line(sb, "END:VCALENDAR");
        return sb.toString();
    }

    private static String description(Booking b) {
        String text = "Phone: " + b.getPhone();
        if (b.getStylistNote() != null && !b.getStylistNote().isBlank()) {
            text += "\nNote: " + b.getStylistNote();
        }
        return text;
    }

    private static String toUtc(LocalDateTime local, ZoneId zone) {
        return local.atZone(zone).withZoneSameInstant(ZoneOffset.UTC).format(UTC_STAMP);
    }

    /**
     * Client input ends up inside the file, so special characters must be escaped.
     * Without this, a name containing a line break could inject extra calendar fields.
     */
    private static String escape(String text) {
        String cleaned = text.replaceAll("[\\p{Cntrl}&&[^\\n]]", "");   // drop control characters, keep \n
        return cleaned
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\n", "\\n");
    }

    /** Appends one logical line, folded so no physical line exceeds 75 bytes, ending in CRLF. */
    private static void line(StringBuilder sb, String text) {
        int octets = 0;
        for (int i = 0; i < text.length(); ) {
            int codePoint = text.codePointAt(i);
            int size = new String(Character.toChars(codePoint)).getBytes(StandardCharsets.UTF_8).length;
            if (octets + size > MAX_LINE_OCTETS) {
                sb.append("\r\n ");     // fold: continuation lines start with one space
                octets = 1;
            }
            sb.appendCodePoint(codePoint);
            octets += size;
            i += Character.charCount(codePoint);
        }
        sb.append("\r\n");
    }
}