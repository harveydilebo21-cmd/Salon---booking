package com.salon.booking;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data retention: bookings are only kept for a short time.
 * Once a booking's appointment time is older than the retention period, the whole
 * booking (including the client's name and phone number) is deleted permanently.
 */
@Component
public class RetentionJob {

    private static final Logger log = LoggerFactory.getLogger(RetentionJob.class);

    private final BookingRepository bookingRepository;
    private final int retentionDays;

    public RetentionJob(BookingRepository bookingRepository,
                        @Value("${salon.retention.days:10}") int retentionDays) {
        this.bookingRepository = bookingRepository;
        this.retentionDays = retentionDays;
    }

    @Scheduled(cron = "${salon.retention.cron:0 30 3 * * *}")
    @Transactional
    public void deleteOldBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        long deleted = bookingRepository.deleteByRequestedTimeBefore(cutoff);

        if (deleted > 0) {

            log.info("Retention job deleted {} booking(s) older than {} days", deleted, retentionDays);
        }
    }
}