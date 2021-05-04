package asg.concert.service.services;

import asg.concert.service.domain.Concert;
import asg.concert.service.domain.Booking;
import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.BookingDTO;

public class ConcertMapper {
	
	static Concert toDomainModel(ConcertDTO concertDTO) {
		Concert concert = new Concert(concertDTO.getTitle(),
				concertDTO.getImageName(),
				concertDTO.getBlurb(),
				concertDTO.getDates(),
				concertDTO.getPerformers(),null);
		concert.setId(concertDTO.getId);
		return concert;
	}
	
	static ConcertDTO toDTO(Concert concert){
		asg.concert.common.dto.ConcertDTO concertDTO = new asg.concert.common.dto.ConcertDTO(concert.getId(),
				concert.getTitle(),
				concert.getImageName(),
				concert.getBlurb());
		concertDTO.setDates(concert.getDates);
		concertDTO.setPerformers(concert.getPerformers);
		return concertDTO;
	}
}