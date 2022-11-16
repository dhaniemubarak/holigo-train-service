package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.Inquiry;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestBookDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {StationMapper.class})
public interface InquiryMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Inquiry inquiryDtoToInquiry(InquiryDto inquiryDto);

    @Mapping(target = "userId", ignore = true)
    InquiryDto inquiryToInquiryDto(Inquiry inquiry);

    @Mapping(target = "trip", source = "tripType")
    @Mapping(target = "tgl_ret", ignore = true)
    @Mapping(target = "tgl_dep", ignore = true)
    @Mapping(target = "org", source = "originStation.id")
    @Mapping(target = "inf", source = "infantAmount")
    @Mapping(target = "des", source = "destinationStation.id")
    @Mapping(target = "chd", source = "childAmount")
    @Mapping(target = "adt", source = "adultAmount")
    @Mapping(target = "action", ignore = true)
    @Mapping(target = "selectedIdRet", ignore = true)
    @Mapping(target = "selectedIdDep", ignore = true)
    @Mapping(target = "rqid", ignore = true)
    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "mmid", ignore = true)
    @Mapping(target = "map", ignore = true)
    @Mapping(target = "cptlp", ignore = true)
    @Mapping(target = "cpname", ignore = true)
    @Mapping(target = "cpmail", ignore = true)
    @Mapping(target = "app", ignore = true)
    RetrossRequestBookDto inquiryToRetrossRequestBookDto(Inquiry inquiry);
}
