package asg.concert.service.services;

import asg.concert.service.domain.Concert;
import asg.concert.common.dto.ConcertDTO;
import asg.concert.service.domain.Performer;
import asg.concert.common.dto.PerformerDTO;
import asg.concert.common.dto.ConcertSummaryDTO;

import asg.concert.service.services.PerformerMapper;

import java.util.List;
import java.util.ArrayList;

public class ConcertMapper {

	static ConcertDTO toDTO(Concert concert){
		List<PerformerDTO> performerDTOs = new ArrayList<>();
		List<Performer> performers = concert.getPerformers();
		for (Performer performer: performers) {
			performerDTOs.add(PerformerMapper.toDTO(performer));
		}
		ConcertDTO concertDTO = new ConcertDTO(concert.getId(),
				concert.getTitle(),
				concert.getImageName(),
				concert.getBlurb());
		concertDTO.setDates(concert.getDates());
		concertDTO.setPerformers(performerDTOs);
		return concertDTO;
	}
	
	static ConcertSummaryDTO toSummaryDTO(Concert concert) {
		ConcertSummaryDTO concertSummaryDTO = new ConcertSummaryDTO(concert.getId(),concert.getTitle(),concert.getImageName());
		return concertSummaryDTO;
	}
}