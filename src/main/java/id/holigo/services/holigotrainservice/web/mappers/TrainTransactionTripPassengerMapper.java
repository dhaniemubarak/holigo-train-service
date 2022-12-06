package id.holigo.services.holigotrainservice.web.mappers;


import id.holigo.services.holigotrainservice.domain.TrainTransactionTripPassenger;
import id.holigo.services.common.model.TrainTransactionTripPassengerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {PassengerMapper.class})
public interface TrainTransactionTripPassengerMapper {
    TrainTransactionTripPassengerDto trainTransactionTripPassengerToTrainTransactionTripPassengerDto(TrainTransactionTripPassenger trainTransactionTripPassenger);

    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TrainTransactionTripPassenger trainTransactionTripPassengerDtoToTrainTransactionTripPassenger(TrainTransactionTripPassengerDto trainTransactionTripPassenger);
}
