package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.Passenger;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTripPassenger;
import id.holigo.services.holigotrainservice.web.model.PassengerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {IdentityCardMapper.class, PassportMapper.class})
public interface PassengerMapper {

    @Mapping(target = "trips", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Passenger passengerDtoToPassenger(PassengerDto passengerDto);

    PassengerDto passengerToPassengerDto(Passenger passenger);

    @Mapping(target = "type", source = "passenger.type")
    @Mapping(target = "title", source = "passenger.title")
    @Mapping(target = "name", source = "passenger.name")
    @Mapping(target = "identityCard", source = "passenger.identityCard")
    @Mapping(target = "passport", source = "passenger.passport")
    PassengerDto trainTransactionTripPassengerToPassengerDto(TrainTransactionTripPassenger trainTransactionTripPassenger);
}
