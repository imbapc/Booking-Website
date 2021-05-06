package asg.concert.service.mapper;

import asg.concert.common.dto.ConcertDTO;
import asg.concert.common.dto.ConcertSummaryDTO;
import asg.concert.common.dto.PerformerDTO;
import asg.concert.service.domain.Concert;
import asg.concert.service.domain.Performer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
//jiakai li code
public class ConcertMapper {
    public static ConcertDTO toConcertDto(Concert concert) {
        ConcertDTO concertDTO = new ConcertDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getImageName(),
                concert.getBlurb()
        );

        List<PerformerDTO> dtoPerformers = new ArrayList<>();
        for (Performer performer : concert.getPerformers()) {
            dtoPerformers.add(PerformerMapper.toPerformerDto(performer));
        }
        concertDTO.setPerformers(dtoPerformers);

        List<LocalDateTime> dates = new ArrayList<>(concert.getDates());
        concertDTO.setDates(dates);

        return concertDTO;
    }

    public static ConcertSummaryDTO toConcertSummaryDto(Concert concert) {
        return new ConcertSummaryDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getImageName()
        );
    }
}