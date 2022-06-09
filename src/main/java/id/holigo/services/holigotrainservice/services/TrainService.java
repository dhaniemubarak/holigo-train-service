package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import id.holigo.services.holigotrainservice.web.model.ListAvailabilityDto;

public interface TrainService {

    ListAvailabilityDto getAvailabilities(InquiryDto inquiryDto) throws JsonProcessingException;

    void saveAvailabilities(ListAvailabilityDto listAvailabilityDto);

}
