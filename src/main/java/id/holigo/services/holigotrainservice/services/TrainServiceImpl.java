package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.FareDto;
import id.holigo.services.common.model.TripType;
import id.holigo.services.holigotrainservice.components.Fare;
import id.holigo.services.holigotrainservice.config.FeeConfig;
import id.holigo.services.holigotrainservice.domain.Inquiry;
import id.holigo.services.holigotrainservice.domain.TrainAvailability;
import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.repositories.TrainAvailabilityRepository;
import id.holigo.services.holigotrainservice.repositories.TrainFinalFareRepository;
import id.holigo.services.holigotrainservice.services.retross.RetrossTrainService;
import id.holigo.services.holigotrainservice.web.exceptions.FinalFareBadRequestException;
import id.holigo.services.holigotrainservice.web.mappers.InquiryMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainAvailabilityMapper;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrainServiceImpl implements TrainService {


    private Fare fare;
    private TrainFinalFareRepository trainFinalFareRepository;
    private TrainAvailabilityRepository trainAvailabilityRepository;
    private RetrossTrainService retrossTrainService;

    private TrainAvailabilityMapper trainAvailabilityMapper;

    private ObjectMapper objectMapper;

    private InquiryMapper inquiryMapper;

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

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setFare(Fare fare) {
        this.fare = fare;
    }

    @Autowired
    public void setTrainFinalFareRepository(TrainFinalFareRepository trainFinalFareRepository) {
        this.trainFinalFareRepository = trainFinalFareRepository;
    }

    @Autowired
    public void setInquiryMapper(InquiryMapper inquiryMapper) {
        this.inquiryMapper = inquiryMapper;
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

    @Transactional
    @Override
    public TrainFinalFare createFinalFare(RequestFinalFareDto requestFinalFareDto, Long userId) {
        int i = 0;
        FareDto fareDto = fare.getFare(userId);
        TrainFinalFare trainFinalFare = new TrainFinalFare();
        trainFinalFare.setId(UUID.randomUUID());
        trainFinalFare.setInquiry(inquiryMapper.inquiryDtoToInquiry(requestFinalFareDto.getTrips().get(0).getInquiry()));
        trainFinalFare.setUserId(userId);
        trainFinalFare.setIsBookable(true);
        trainFinalFare.setNtaAmount(FeeConfig.ADMIN_AMOUNT.subtract(FeeConfig.NRA_AMOUNT));
        trainFinalFare.setNraAmount(FeeConfig.NRA_AMOUNT);
        trainFinalFare.setCpAmount(fareDto.getCpAmount());
        trainFinalFare.setMpAmount(fareDto.getMpAmount());
        trainFinalFare.setIpAmount(fareDto.getIpAmount());
        trainFinalFare.setHpAmount(fareDto.getHpAmount());
        trainFinalFare.setHvAmount(fareDto.getHvAmount());
        trainFinalFare.setPrAmount(fareDto.getPrAmount());
        trainFinalFare.setIpcAmount(fareDto.getIpcAmount());
        trainFinalFare.setHpcAmount(fareDto.getHpcAmount());
        trainFinalFare.setPrcAmount(fareDto.getPrcAmount());
        trainFinalFare.setLossAmount(fareDto.getLossAmount());
        trainFinalFare.setAdminAmount(trainFinalFare.getNtaAmount()
                .add(fareDto.getCpAmount())
                .add(fareDto.getMpAmount())
                .add(fareDto.getIpAmount())
                .add(fareDto.getHpAmount())
                .add(fareDto.getHvAmount())
                .add(fareDto.getPrAmount())
                .add(fareDto.getIpcAmount())
                .add(fareDto.getHpcAmount())
                .add(fareDto.getPrcAmount()));
        trainFinalFare.setFareAmount(trainFinalFare.getAdminAmount());
        for (TripDto tripDto : requestFinalFareDto.getTrips()) {
            Optional<TrainAvailability> fetchTrainAvailability = trainAvailabilityRepository.findById(tripDto.getTrip().getId());
            if (fetchTrainAvailability.isEmpty()) {
                throw new FinalFareBadRequestException();
            }
            TrainAvailability trainAvailability = fetchTrainAvailability.get();
            TrainFinalFareTrip trainFinalFareTrip = trainAvailabilityMapper.trainAvailabilityToTrainFinalFareTrip(trainAvailability);
            TrainAvailabilityFareDto trainAvailabilityFareDto;
            try {
                trainAvailabilityFareDto = objectMapper.readValue(trainAvailability.getFare(), TrainAvailabilityFareDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            trainFinalFareTrip.setSupplierId(trainAvailabilityFareDto.getSelectedId());
            trainFinalFareTrip.setFareAmount(trainAvailabilityFareDto.getFareAmount());
            if (tripDto.getInquiry().getTripType() != TripType.R && i != 1) {
                trainFinalFareTrip.setAdminAmount(trainFinalFare.getAdminAmount());
                trainFinalFareTrip.setNraAmount(trainFinalFare.getNraAmount());
                trainFinalFareTrip.setNtaAmount(trainFinalFare.getFareAmount()
                        .add(trainFinalFareTrip.getAdminAmount().subtract(trainFinalFare.getNraAmount())));
                trainFinalFareTrip.setCpAmount(trainFinalFare.getCpAmount());
                trainFinalFareTrip.setMpAmount(trainFinalFare.getMpAmount());
                trainFinalFareTrip.setIpAmount(trainFinalFare.getIpAmount());
                trainFinalFareTrip.setHpcAmount(trainFinalFare.getHpAmount());
                trainFinalFareTrip.setHvAmount(trainFinalFare.getHvAmount());
                trainFinalFareTrip.setPrAmount(trainFinalFare.getPrAmount());
                trainFinalFareTrip.setIpcAmount(trainFinalFare.getIpcAmount());
                trainFinalFareTrip.setHpcAmount(trainFinalFare.getHpcAmount());
                trainFinalFareTrip.setPrcAmount(trainFinalFare.getPrcAmount());
                trainFinalFareTrip.setLossAmount(trainFinalFare.getLossAmount());
            }
            trainFinalFare.setNtaAmount(trainFinalFare.getNtaAmount().add(trainFinalFareTrip.getFareAmount()));
            trainFinalFare.setFareAmount(trainFinalFare.getFareAmount().add(trainFinalFareTrip.getFareAmount()));
            trainFinalFare.addToTrips(trainFinalFareTrip);
            i++;
        }
        return trainFinalFareRepository.save(trainFinalFare);
    }
}
