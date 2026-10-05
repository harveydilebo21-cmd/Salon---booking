package com.salon.booking;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByToken(String token);
    List<Booking> findByStatusOrderByRequestedTimeAsc(BookingStatus status);

    List<Booking> findByStatusNotOrderByRequestedTimeAsc(BookingStatus status);
}