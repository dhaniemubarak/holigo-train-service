package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.web.model.RetrossFareDto;
import id.holigo.services.holigotrainservice.web.model.TrainAvailabilityFareDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
@DecoratedWith(TrainAvailabilityFareMapperDecorator.class)
public interface TrainAvailabilityFareMapper {

    @Mapping(target = "hpAmount", ignore = true)
    @Mapping(target = "selectedId", ignore = true)
    @Mapping(target = "seatAvailable", source = "retrossFareDto.seatAvb")
    TrainAvailabilityFareDto retrossFareDtoToTrainAvailabilityFareDto(RetrossFareDto retrossFareDto, Long userId);
}
