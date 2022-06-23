package id.holigo.services.holigotrainservice.web.mappers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.holigotrainservice.domain.TrainAvailability;
import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public abstract class TrainAvailabilityMapperDecorator implements TrainAvailabilityMapper {


    private TrainAvailabilityFareMapper trainAvailabilityFareMapper;
    private TrainAvailabilityMapper trainAvailabilityMapper;

    private ObjectMapper objectMapper;

    @Autowired
    public void setTrainAvailabilityMapper(TrainAvailabilityMapper trainAvailabilityMapper) {
        this.trainAvailabilityMapper = trainAvailabilityMapper;
    }

    @Autowired
    public void setTrainAvailabilityFareMapper(TrainAvailabilityFareMapper trainAvailabilityFareMapper) {
        this.trainAvailabilityFareMapper = trainAvailabilityFareMapper;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainAvailabilityDto retrossDepartureDtoToTrainAvailabilityDto(RetrossDepartureDto retrossDepartureDto, RetrossFareDto retrossFareDto, InquiryDto inquiryDto) {
        TrainAvailabilityDto trainAvailabilityDto = trainAvailabilityMapper.retrossDepartureDtoToTrainAvailabilityDto(retrossDepartureDto, retrossFareDto, inquiryDto);
        log.info("Decorator -------------");
        trainAvailabilityDto.setId(UUID.randomUUID());
        String TRAIN_IMAGE_URL = "https://ik.imagekit.io/holigo/transportasi/logo-kai-main_SyEqhgYKx.png";
        trainAvailabilityDto.setImageUrl(TRAIN_IMAGE_URL);
        trainAvailabilityDto.setDepartureDate(Date.valueOf(LocalDate.parse(retrossDepartureDto.getEtd().substring(0, 10))));
        trainAvailabilityDto.setDepartureTime(Time.valueOf(LocalTime.parse(retrossDepartureDto.getEtd().substring(11, 16))));
        trainAvailabilityDto.setArrivalDate(Date.valueOf(LocalDate.parse(retrossDepartureDto.getEta().substring(0, 10))));
        trainAvailabilityDto.setArrivalTime(Time.valueOf(LocalTime.parse(retrossDepartureDto.getEta().substring(11, 16))));
        trainAvailabilityDto.setFare(trainAvailabilityFareMapper.retrossFareDtoToTrainAvailabilityFareDto(retrossFareDto, inquiryDto.getUserId()));
        if (!Objects.equals(trainAvailabilityDto.getOriginStation().getId(), trainAvailabilityDto.getOriginStationId())) {
            trainAvailabilityDto.setOriginStation(inquiryDto.getDestinationStation());
            trainAvailabilityDto.setDestinationStation(inquiryDto.getOriginStation());
        }
        return trainAvailabilityDto;
    }

    @Override
    public ListAvailabilityDto retrossResponseScheduleDtoToListAvailabilityDto(RetrossResponseScheduleDto retrossResponseScheduleDto, InquiryDto inquiryDto) {
        List<TrainAvailabilityDto> trainAvailabilityDtoList = new ArrayList<>();
        for (int i = 0; i < retrossResponseScheduleDto.getSchedule().getDepartures().size(); i++) {
            RetrossDepartureDto retrossDepartureDto = retrossResponseScheduleDto.getSchedule().getDepartures().get(i);
            retrossDepartureDto.getFares().forEach(fare -> trainAvailabilityDtoList.add(retrossDepartureDtoToTrainAvailabilityDto(retrossDepartureDto, fare, inquiryDto)));
        }
        ListAvailabilityDto listAvailabilityDto = new ListAvailabilityDto();
        listAvailabilityDto.setDepartures(trainAvailabilityDtoList);

        if (retrossResponseScheduleDto.getSchedule().getReturns() != null) {
            List<TrainAvailabilityDto> trainAvailabilityReturnDtoList = new ArrayList<>();
            for (int i = 0; i < retrossResponseScheduleDto.getSchedule().getReturns().size(); i++) {
                RetrossDepartureDto retrossDepartureDto = retrossResponseScheduleDto.getSchedule().getReturns().get(i);
                retrossDepartureDto.getFares().forEach(fare -> trainAvailabilityReturnDtoList.add(retrossDepartureDtoToTrainAvailabilityDto(retrossDepartureDto, fare, inquiryDto)));
            }
            listAvailabilityDto.setReturns(trainAvailabilityReturnDtoList);
        }
        return listAvailabilityDto;
    }

    @Override
    public TrainAvailability trainAvailabilityDtoToTrainAvailability(TrainAvailabilityDto trainAvailabilityDto) {
        TrainAvailability trainAvailability = trainAvailabilityMapper.trainAvailabilityDtoToTrainAvailability(trainAvailabilityDto);
        try {
            trainAvailability.setFare(objectMapper.writeValueAsString(trainAvailabilityDto.getFare()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return trainAvailability;
    }

    @Override
    public TrainFinalFareTrip trainAvailabilityToTrainFinalFareTrip(TrainAvailability trainAvailability) {
        TrainFinalFareTrip trainFinalFareTrip = trainAvailabilityMapper.trainAvailabilityToTrainFinalFareTrip(trainAvailability);
        trainFinalFareTrip.setFareAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setAdminAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setNtaAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setNraAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setCpAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setIpAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setMpAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setHpAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setHvAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setPrAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setIpcAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setHpcAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setPrcAmount(BigDecimal.valueOf(0.00));
        trainFinalFareTrip.setLossAmount(BigDecimal.valueOf(0.00));
        return trainFinalFareTrip;
    }
}
