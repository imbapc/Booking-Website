package asg.concert.service.services;

import asg.concert.service.domain.Performer;
import asg.concert.common.dto.PerformerDTO;

public class PerformerMapper {
	
	static Performer toDomainModel(PerformerDTO performerDTO) {
		Performer performer = new Performer(performerDTO.getName(),
				performerDTO.getImageName(),
				performerDTO.getGenre(),
				performerDTO.getBlurb());
		performer.setId(performerDTO.getId());
		return performer;
	}
	
	static PerformerDTO toDTO(Performer performer) {
		PerformerDTO performerDTO= new PerformerDTO(performer.getId(),
				performer.getName(),
				performer.getImageName(),
				performer.getGenre(),
				performer.getBlurb());
		return performerDTO;
	}

}