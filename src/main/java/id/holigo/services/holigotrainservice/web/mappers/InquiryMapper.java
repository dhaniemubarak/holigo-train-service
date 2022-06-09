package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.Inquiry;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InquiryMapper {

    Inquiry inquiryDtoToInquiry(InquiryDto inquiryDto);

    @Mapping(target = "userId", ignore = true)
    InquiryDto inquiryToInquiryDto(Inquiry inquiry);
}
