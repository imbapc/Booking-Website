package asg.concert.service.mapper;

import asg.concert.common.dto.BookingDTO;
import asg.concert.common.dto.SeatDTO;
import asg.concert.service.domain.Booking;
import asg.concert.service.domain.Seat;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {
    public static BookingDTO toBookingDto(Booking booking) {
        List<SeatDTO> seatDtos = new ArrayList<>();
        for (Seat seat : booking.getSeats()) {
            seatDtos.add(new SeatDTO(seat.getLabel(), seat.getPrice()));
        }
        return new BookingDTO(booking.getConcertId(), booking.getDate(), seatDtos);
    }
}