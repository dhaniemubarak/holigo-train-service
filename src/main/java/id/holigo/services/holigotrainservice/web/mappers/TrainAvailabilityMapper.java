package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainAvailability;
import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.web.model.*;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper
@DecoratedWith(TrainAvailabilityMapperDecorator.class)
public interface TrainAvailabilityMapper {

    @Mapping(target = "originStation", source = "inquiryDto.originStation")
    @Mapping(target = "destinationStation", source = "inquiryDto.destinationStation")
    @Mapping(target = "fare", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "originStationId", source = "retrossDepartureDto.std")
    @Mapping(target = "destinationStationId", source = "retrossDepartureDto.sta")
    @Mapping(target = "departureTime", ignore = true)
    @Mapping(target = "departureDate", ignore = true)
    @Mapping(target = "arrivalTime", ignore = true)
    @Mapping(target = "arrivalDate", ignore = true)
    TrainAvailabilityDto retrossDepartureDtoToTrainAvailabilityDto(RetrossDepartureDto retrossDepartureDto, RetrossFareDto retrossFareDto, InquiryDto inquiryDto);

    @Mapping(target = "returns", ignore = true)
    @Mapping(target = "inquiry", ignore = true)
    @Mapping(target = "departures", ignore = true)
    ListAvailabilityDto retrossResponseScheduleDtoToListAvailabilityDto(RetrossResponseScheduleDto retrossResponseScheduleDto, InquiryDto inquiryDto);

    @Mapping(target = "fare", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TrainAvailability trainAvailabilityDtoToTrainAvailability(TrainAvailabilityDto trainAvailabilityDto);

    @Mapping(target = "segment", ignore = true)
    @Mapping(target = "supplierId", ignore = true)
    @Mapping(target = "prcAmount", ignore = true)
    @Mapping(target = "prAmount", ignore = true)
    @Mapping(target = "ntaAmount", ignore = true)
    @Mapping(target = "nraAmount", ignore = true)
    @Mapping(target = "mpAmount", ignore = true)
    @Mapping(target = "lossAmount", ignore = true)
    @Mapping(target = "ipcAmount", ignore = true)
    @Mapping(target = "ipAmount", ignore = true)
    @Mapping(target = "hvAmount", ignore = true)
    @Mapping(target = "hpcAmount", ignore = true)
    @Mapping(target = "hpAmount", ignore = true)
    @Mapping(target = "finalFare", ignore = true)
    @Mapping(target = "fareAmount", ignore = true)
    @Mapping(target = "cpAmount", ignore = true)
    @Mapping(target = "adminAmount", ignore = true)
    TrainFinalFareTrip trainAvailabilityToTrainFinalFareTrip(TrainAvailability trainAvailability);
}
