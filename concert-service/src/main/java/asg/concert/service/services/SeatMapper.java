package asg.concert.service.services;

import asg.concert.common.dto.SeatDTO;
import asg.concert.service.domain.Seat;

public class SeatMapper {
    static SeatDTO toDTO(Seat seat){
        return new SeatDTO(seat.getLabel(), seat.getPrice());
    }
}
