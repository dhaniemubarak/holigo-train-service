package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.ContactPerson;
import id.holigo.services.common.model.ContactPersonDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ContactPersonMapper {

    ContactPersonDto contactPersonToContactPersonDto(ContactPerson contactPerson);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ContactPerson contactPersonDtoToContactPerson(ContactPersonDto contactPersonDto);
}
