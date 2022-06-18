package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.Passenger;
import id.holigo.services.holigotrainservice.web.model.PassengerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {IdentityCardMapper.class, PassportMapper.class})
public interface PassengerMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Passenger passengerDtoToPassenger(PassengerDto passengerDto);

    PassengerDto passengerToPassengerDto(Passenger passenger);
}
