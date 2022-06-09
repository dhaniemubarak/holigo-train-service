package id.holigo.services.holigotrainservice.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.domain.Inquiry;
import id.holigo.services.holigotrainservice.repositories.InquiryRepository;
import id.holigo.services.holigotrainservice.services.TrainService;
import id.holigo.services.holigotrainservice.web.exceptions.AvailabilitiesException;
import id.holigo.services.holigotrainservice.web.mappers.InquiryMapper;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import id.holigo.services.holigotrainservice.web.model.ListAvailabilityDto;
import id.holigo.services.holigotrainservice.web.model.TrainAvailabilityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class TrainAvailabilityController {

    private TrainService trainService;

    private InquiryRepository inquiryRepository;

    private InquiryMapper inquiryMapper;

    @Autowired
    public void setInquiryRepository(InquiryRepository inquiryRepository) {
        this.inquiryRepository = inquiryRepository;
    }

    @Autowired
    public void setInquiryMapper(InquiryMapper inquiryMapper) {
        this.inquiryMapper = inquiryMapper;
    }

    @Autowired
    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("/api/v1/train/availabilities")
    public ResponseEntity<ListAvailabilityDto> getAvailabilities(InquiryDto inquiryDto, @RequestHeader("user-id") Long userId) {
        inquiryDto.setUserId(userId);
        Inquiry inquiry;
        Optional<Inquiry> fetchInquiry = inquiryRepository.getInquiry(
                inquiryDto.getOriginStationId(),
                inquiryDto.getDestinationStationId(),
                inquiryDto.getDepartureDate().toString(),
                inquiryDto.getReturnDate() != null ? inquiryDto.getReturnDate().toString() : null,
                inquiryDto.getTripType().toString(),
                inquiryDto.getAdultAmount(),
                inquiryDto.getInfantAmount()
        );
        if (fetchInquiry.isEmpty()) {
            try {
                inquiry = inquiryRepository.save(inquiryMapper.inquiryDtoToInquiry(inquiryDto));
                inquiryDto.setId(inquiry.getId());
            } catch (AvailabilitiesException e) {
                throw new AvailabilitiesException();
            }
        } else {
            inquiry = fetchInquiry.get();
            inquiryDto.setId(inquiry.getId());
        }
        ListAvailabilityDto listAvailabilityDto;
        try {
            listAvailabilityDto = trainService.getAvailabilities(inquiryDto);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (listAvailabilityDto.getDepartures() == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(listAvailabilityDto, HttpStatus.OK);
    }
}
