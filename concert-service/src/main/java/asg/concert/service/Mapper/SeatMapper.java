package asg.concert.service.mapper;

import asg.concert.common.dto.SeatDTO;
import asg.concert.service.domain.Seat;
//jiakai li code
public class SeatMapper {
    public static SeatDTO toSeatDto(Seat seat) {
        return new SeatDTO(seat.getLabel(), seat.getPrice());
    }
}