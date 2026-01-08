package com.venue.sports_venue_booking.service.impl;

import com.venue.sports_venue_booking.dto.request.BookingRequest;
import com.venue.sports_venue_booking.dto.response.BookingResponse;
import com.venue.sports_venue_booking.entity.Booking;
import com.venue.sports_venue_booking.entity.BookingStatus;
import com.venue.sports_venue_booking.entity.Slot;
import com.venue.sports_venue_booking.exception.BookingCancellationException;
import com.venue.sports_venue_booking.exception.ResourceNotFoundException;
import com.venue.sports_venue_booking.exception.SlotAlreadyBookedException;
import com.venue.sports_venue_booking.repository.BookingRepository;
import com.venue.sports_venue_booking.repository.SlotRepository;
import com.venue.sports_venue_booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for slot {} by customer {}",
                request.getSlotId(), request.getCustomerEmail());

        Slot slot = slotRepository.findByIdForUpdate(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", request.getSlotId()));

        if (slot.getIsBooked()) {
            throw new SlotAlreadyBookedException(
                    String.format("Slot %d is already booked", slot.getId())
            );
        }

        if (slot.getStartTime().isBefore(LocalDateTime.now())) {
            throw new SlotAlreadyBookedException(
                    "Cannot book a slot that has already started or passed"
            );
        }

        slot.setIsBooked(true);
        slotRepository.save(slot);

        Booking booking = Booking.builder()
                .slot(slot)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));
        return mapToResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerEmail(String email) {
        log.info("Fetching bookings for customer: {}", email);
        return bookingRepository.findByCustomerEmailOrderByBookingDateDesc(email)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", id));

        // Check if booking can be cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingCancellationException("Booking is already cancelled");
        }

        if (booking.getSlot().getStartTime().isBefore(LocalDateTime.now())) {
            throw new BookingCancellationException(
                    "Cannot cancel a booking for a slot that has already started or passed"
            );
        }

        // Cancel booking and free the slot
        booking.cancel();  // Uses the business logic method
        bookingRepository.save(booking);

        log.info("Booking cancelled successfully with id: {}", id);
        return mapToResponse(booking);
    }

    /**
     * Map Booking entity to BookingResponse DTO
     */
    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .customerName(booking.getCustomerName())
                .customerEmail(booking.getCustomerEmail())
                .customerPhone(booking.getCustomerPhone())
                .bookingDate(booking.getBookingDate())
                .status(booking.getStatus())
                .slotId(booking.getSlot().getId())
                .slotStartTime(booking.getSlot().getStartTime())
                .slotEndTime(booking.getSlot().getEndTime())
                .slotPrice(booking.getSlot().getPrice())
                .venueId(booking.getSlot().getVenue().getId())
                .venueName(booking.getSlot().getVenue().getName())
                .venueLocation(booking.getSlot().getVenue().getLocation())
                .sportId(booking.getSlot().getSport().getId())
                .sportName(booking.getSlot().getSport().getSportName())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}