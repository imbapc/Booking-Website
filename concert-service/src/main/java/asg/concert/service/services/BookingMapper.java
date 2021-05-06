package asg.concert.service.services;

import asg.concert.common.dto.BookingDTO;
import asg.concert.common.dto.SeatDTO;
import asg.concert.service.domain.Booking;
import asg.concert.service.domain.Seat;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    static BookingDTO toDTO (Booking booking) {
        List<Seat> seatList = booking.getSeats();
        List<SeatDTO> seatDTOList = new ArrayList<>();
        for (Seat seat: seatList){
            SeatDTO seatDTO = SeatMapper.toDTO(seat);
            seatDTOList.add(seatDTO);
        }
        return new BookingDTO(booking.getConcertId(), booking.getDate(), seatDTOList);
    }

}
