package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.Passport;
import id.holigo.services.holigotrainservice.web.model.PassportDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PassportMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Passport passportDtoToPassport(PassportDto passportDto);

    PassportDto passportToPassportDto(Passport passport);
}
