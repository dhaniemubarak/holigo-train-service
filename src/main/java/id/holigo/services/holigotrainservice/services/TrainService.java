package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import id.holigo.services.holigotrainservice.web.model.ListAvailabilityDto;
import id.holigo.services.holigotrainservice.web.model.RequestFinalFareDto;

public interface TrainService {

    ListAvailabilityDto getAvailabilities(InquiryDto inquiryDto) throws JsonProcessingException;

    void saveAvailabilities(ListAvailabilityDto listAvailabilityDto);

    void createBook(TrainTransaction trainTransaction);

    void issued(TrainTransaction trainTransaction);

    void cancelBook(TrainTransaction trainTransaction);

    TrainFinalFare createFinalFare(RequestFinalFareDto requestFinalFareDto, Long userId);

}
