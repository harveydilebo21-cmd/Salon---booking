package com.salon.booking;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService {

    private final BookingRepository bookingRepository;

    public AvailabilityService(BookingRepository bookingRepository){
        this.bookingRepository = bookingRepository;
    }

    public boolean isFree(LocalDateTime start, String service, Long ignoreBookingId){
        LocalDateTime end = start.plusMinutes(ServiceCatalog.minutesFor(service));

        LocalDateTime earliestStart = start.minusMinutes(ServiceCatalog.maxMinutes());
        List<Booking> nearby = bookingRepository
                .findByStatusAndRequestedTimeBetween(BookingStatus.CONFIRMED, earliestStart, end);

        for(Booking other : nearby){
            if(other.getId().equals(ignoreBookingId)){
                continue;
            }
            LocalDateTime otherStart = other.getRequestedTime();
            LocalDateTime otherEnd = otherStart.plusMinutes(ServiceCatalog.minutesFor(other.getService()));

            if(start.isBefore(otherEnd) && otherStart.isBefore(end)){
                return false;
            }
        }
        return true;
    }

}
