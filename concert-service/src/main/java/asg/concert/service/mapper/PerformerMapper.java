package asg.concert.service.mapper;

import asg.concert.common.dto.PerformerDTO;
import asg.concert.service.domain.Performer;

public class PerformerMapper {
    public static PerformerDTO toPerformerDto(Performer performer) {
        return new PerformerDTO(
                performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                performer.getBlurb()
        );
    }
}
