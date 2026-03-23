package com.lankatransit.backend.repository;

import com.lankatransit.backend.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserEmailOrderByBookingTimeDesc(String userEmail);
    List<Booking> findByUserEmail(String userEmail);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.routeId = :routeId AND b.status = 'USED'")
    Long countUsedTicketsByRoute(@Param("routeId") Long routeId);

    @Query("SELECT COALESCE(SUM(b.fare), 0) FROM Booking b WHERE b.routeId = :routeId AND b.status = 'USED'")
    Double calculateTotalRevenueByRoute(@Param("routeId") Long routeId);
}