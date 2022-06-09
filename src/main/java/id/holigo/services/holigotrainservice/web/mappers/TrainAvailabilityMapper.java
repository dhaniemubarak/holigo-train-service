package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainAvailability;
import id.holigo.services.holigotrainservice.web.model.*;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
@DecoratedWith(TrainAvailabilityMapperDecorator.class)
public interface TrainAvailabilityMapper {

    @Mapping(target = "fare", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originStationId", source = "retrossDepartureDto.std")
    @Mapping(target = "destinationStationId", source = "retrossDepartureDto.sta")
    @Mapping(target = "departureTime", ignore = true)
    @Mapping(target = "departureDate", ignore = true)
    @Mapping(target = "arrivalTime", ignore = true)
    @Mapping(target = "arrivalDate", ignore = true)
    TrainAvailabilityDto retrossDepartureDtoToTrainAvailabilityDto(RetrossDepartureDto retrossDepartureDto, RetrossFareDto retrossFareDto, Long userId);

    @Mapping(target = "returns", ignore = true)
    @Mapping(target = "inquiry", ignore = true)
    @Mapping(target = "departures", ignore = true)
    ListAvailabilityDto retrossResponseScheduleDtoToListAvailabilityDto(RetrossResponseScheduleDto retrossResponseScheduleDto, Long userId);

    @Mapping(target = "fare", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "origin", ignore = true)
    @Mapping(target = "destination", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TrainAvailability trainAvailabilityDtoToTrainAvailability(TrainAvailabilityDto trainAvailabilityDto);
}
