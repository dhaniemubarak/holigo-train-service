package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.IdentityCard;
import id.holigo.services.holigotrainservice.web.model.IdentityCardDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface IdentityCardMapper {
    @Mapping(target = "passenger", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    IdentityCard identityCardDtoToIdentityCard(IdentityCardDto identityCardDto);

    IdentityCardDto identityCardToIdentityCardDto(IdentityCardDto identityCardDto);

}
