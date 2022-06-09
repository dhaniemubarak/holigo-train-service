package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.holigotrainservice.repositories.TrainAvailabilityRepository;
import id.holigo.services.holigotrainservice.services.retross.RetrossTrainService;
import id.holigo.services.holigotrainservice.web.mappers.TrainAvailabilityMapper;
import id.holigo.services.holigotrainservice.web.model.InquiryDto;
import id.holigo.services.holigotrainservice.web.model.ListAvailabilityDto;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestScheduleDto;
import id.holigo.services.holigotrainservice.web.model.RetrossResponseScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
public class TrainServiceImpl implements TrainService {


    private TrainAvailabilityRepository trainAvailabilityRepository;
    private RetrossTrainService retrossTrainService;

    private TrainAvailabilityMapper trainAvailabilityMapper;

    @Autowired
    public void setRetrossTrainService(RetrossTrainService retrossTrainService) {
        this.retrossTrainService = retrossTrainService;
    }

    @Autowired
    public void setTrainAvailabilityMapper(TrainAvailabilityMapper trainAvailabilityMapper) {
        this.trainAvailabilityMapper = trainAvailabilityMapper;
    }

    @Autowired
    public void setTrainAvailabilityRepository(TrainAvailabilityRepository trainAvailabilityRepository) {
        this.trainAvailabilityRepository = trainAvailabilityRepository;
    }

    @Override
    public ListAvailabilityDto getAvailabilities(InquiryDto inquiryDto) throws JsonProcessingException {
        RetrossRequestScheduleDto requestScheduleDto = RetrossRequestScheduleDto.builder()
                .org(inquiryDto.getOriginStationId())
                .des(inquiryDto.getDestinationStationId())
                .tgl_dep(inquiryDto.getDepartureDate().toString())
                .tgl_ret(inquiryDto.getReturnDate() != null ? inquiryDto.getReturnDate().toString() : null)
                .trip(inquiryDto.getTripType().toString())
                .adt(inquiryDto.getAdultAmount())
                .inf(inquiryDto.getInfantAmount())
                .build();
        RetrossResponseScheduleDto retrossResponseScheduleDto = retrossTrainService.getSchedule(requestScheduleDto);
        log.info("retrossResponseScheduleDto -> {}", new ObjectMapper().writeValueAsString(retrossResponseScheduleDto));
        if (retrossResponseScheduleDto.getSchedule() == null) {
            return null;
        }
        ListAvailabilityDto listAvailabilityDto = trainAvailabilityMapper.retrossResponseScheduleDtoToListAvailabilityDto(retrossResponseScheduleDto, inquiryDto.getUserId());
        listAvailabilityDto.setInquiry(inquiryDto);
        // Save availabilities
        saveAvailabilities(listAvailabilityDto);
        return listAvailabilityDto;
    }

    @Transactional
    @Override
    public void saveAvailabilities(ListAvailabilityDto listAvailabilityDto) {
        trainAvailabilityRepository.saveAll(listAvailabilityDto.getDepartures().stream()
                .map(trainAvailabilityMapper::trainAvailabilityDtoToTrainAvailability).collect(Collectors.toList()));
        if (listAvailabilityDto.getReturns() != null) {
            trainAvailabilityRepository.saveAll(listAvailabilityDto.getReturns().stream()
                    .map(trainAvailabilityMapper::trainAvailabilityDtoToTrainAvailability).collect(Collectors.toList()));
        }
    }
}
