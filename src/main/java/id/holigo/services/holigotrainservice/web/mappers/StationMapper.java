package id.holigo.services.holigotrainservice.web.mappers;


import id.holigo.services.holigotrainservice.domain.Station;
import id.holigo.services.holigotrainservice.web.model.StationDto;
import org.mapstruct.Mapper;

@Mapper
public interface StationMapper {
    
    StationDto stationToStationDto(Station station);

    Station stationDtoToStation(StationDto stationDto);
}
