package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import id.holigo.services.holigotrainservice.web.model.ListAvailabilityDto;
import id.holigo.services.holigotrainservice.web.model.RequestFinalFareDto;

public interface TrainService {

    ListAvailabilityDto getAvailabilities(InquiryDto inquiryDto) throws JsonProcessingException;

    void saveAvailabilities(ListAvailabilityDto listAvailabilityDto);

    TrainFinalFare createFinalFare(RequestFinalFareDto requestFinalFareDto, Long userId);

}
