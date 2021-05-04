package asg.concert.service.services;

import asg.concert.domain.Concert;

public class ConcertMapper {
	
	static Concert toDomainModel(asg.concert.common.dto.ConcertDTO concertDTO) {
		Concert concert = new Concert(concertDTO.getTitle(),
				concertDTO.getImageName(),
				concertDTO.getBlurb(),
				concertDTO.getDates(),
				concertDTO.getPerformers());
		concert.setId(concertDTO.getId);
		return concert;
	}
	
	static asg.concert.common.dto.ConcertDTO toDTO(Concert concert){
		asg.concert.common.dto.ConcertDTO concertDTO = new asg.concert.common.dto.ConcertDTO(concert.getId(),
				concert.getTitle(),
				concert.getImageName(),
				concert.getBlurb());
		concertDTO.setDates(concert.getDates);
		concertDTO.setPerformers(concert.getPerformers);
		return concertDTO;
	}
}