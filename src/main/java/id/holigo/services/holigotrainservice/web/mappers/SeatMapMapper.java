package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.web.model.RetrossSeatMapDto;
import id.holigo.services.holigotrainservice.web.model.SeatMapDto;
import org.mapstruct.Mapper;

@Mapper
public interface SeatMapMapper {

    SeatMapDto retrossSeatMapDtoToSeatMapDto(RetrossSeatMapDto retrossSeatMapDto);
}
