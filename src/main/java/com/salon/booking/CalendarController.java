package com.salon.booking;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarController {

    private static final int DAYS_OF_HISTORY = 15;

    private final BookingRepository bookingRepository;
    private final String expectedToken;

    public CalendarController(BookingRepository bookingRepository,
                              @Value("${salon.calendar.token}") String expectedToken) {
        this.bookingRepository = bookingRepository;
        this.expectedToken = expectedToken;
    }

    @GetMapping("/calendar/{token}/bookings.ics")
    public ResponseEntity<String> feed(@PathVariable String token) {
        // Compare in constant time, and answer 404 (not 403) so a wrong guess reveals nothing
        boolean valid = MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                expectedToken.getBytes(StandardCharsets.UTF_8));
        if (!valid) {
            return ResponseEntity.notFound().build();
        }

        // Only the recent past and the future, so the feed stays small
        LocalDateTime from = LocalDateTime.now().minusDays(DAYS_OF_HISTORY);
        List<Booking> confirmed = bookingRepository
                .findByStatusAndRequestedTimeGreaterThanEqualOrderByRequestedTimeAsc(
                        BookingStatus.CONFIRMED, from);
        String body = CalendarFeed.build(confirmed, ZoneId.systemDefault());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/calendar; charset=utf-8")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .body(body);
    }
}