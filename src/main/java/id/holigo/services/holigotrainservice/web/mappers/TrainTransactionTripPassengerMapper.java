package id.holigo.services.holigotrainservice.web.mappers;


import id.holigo.services.holigotrainservice.domain.TrainTransactionTripPassenger;
import id.holigo.services.holigotrainservice.web.model.TrainTransactionTripPassengerDto;
import org.mapstruct.Mapper;

@Mapper(uses = {PassengerMapper.class})
public interface TrainTransactionTripPassengerMapper {
    TrainTransactionTripPassengerDto trainTransactionTripPassengerToTrainTransactionTripPassengerDto(TrainTransactionTripPassenger trainTransactionTripPassenger);
}
