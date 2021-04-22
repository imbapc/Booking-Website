package asg.concert.service.mapper;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.PerformerDTO;
import asg.concert.service.domain.Concert;
import asg.concert.service.domain.Performer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConcertMapper {
    public static ConcertDTO toDto(Concert concert) {
        ConcertDTO concertDTO = new ConcertDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getImageName(),
                concert.getBlurb()
        );

        List<PerformerDTO> dtoPerformers = new ArrayList<>();
        for (Performer performer : concert.getPerformers()) {
            dtoPerformers.add(PerformerMapper.toDto(performer));
        }
        concertDTO.setPerformers(dtoPerformers);

        List<LocalDateTime> dates = new ArrayList<>(concert.getDates());
        concertDTO.setDates(dates);

        return concertDTO;
    }
}
