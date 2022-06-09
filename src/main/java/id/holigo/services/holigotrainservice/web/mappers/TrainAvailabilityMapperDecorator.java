package id.holigo.services.holigotrainservice.web.mappers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.holigotrainservice.domain.TrainAvailability;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public TrainAvailabilityDto retrossDepartureDtoToTrainAvailabilityDto(RetrossDepartureDto retrossDepartureDto, RetrossFareDto retrossFareDto, Long userId) {
        TrainAvailabilityDto trainAvailabilityDto = trainAvailabilityMapper.retrossDepartureDtoToTrainAvailabilityDto(retrossDepartureDto, retrossFareDto, userId);
        log.info("Decorator -------------");
        trainAvailabilityDto.setId(UUID.randomUUID());
        String TRAIN_IMAGE_URL = "https://ik.imagekit.io/holigo/transportasi/logo-kai-main_SyEqhgYKx.png";
        trainAvailabilityDto.setImageUrl(TRAIN_IMAGE_URL);
        trainAvailabilityDto.setDepartureDate(Date.valueOf(LocalDate.parse(retrossDepartureDto.getEtd().substring(0, 10))));
        trainAvailabilityDto.setDepartureTime(Time.valueOf(LocalTime.parse(retrossDepartureDto.getEtd().substring(11, 16))));
        trainAvailabilityDto.setArrivalDate(Date.valueOf(LocalDate.parse(retrossDepartureDto.getEta().substring(0, 10))));
        trainAvailabilityDto.setArrivalTime(Time.valueOf(LocalTime.parse(retrossDepartureDto.getEta().substring(11, 16))));
        trainAvailabilityDto.setFare(trainAvailabilityFareMapper.retrossFareDtoToTrainAvailabilityFareDto(retrossFareDto, userId));
        return trainAvailabilityDto;
    }

    @Override
    public ListAvailabilityDto retrossResponseScheduleDtoToListAvailabilityDto(RetrossResponseScheduleDto retrossResponseScheduleDto, Long userId) {
        List<TrainAvailabilityDto> trainAvailabilityDtoList = new ArrayList<>();
        for (int i = 0; i < retrossResponseScheduleDto.getSchedule().getDepartures().size(); i++) {
            log.info("For loop -> {}", i);
            RetrossDepartureDto retrossDepartureDto = retrossResponseScheduleDto.getSchedule().getDepartures().get(i);
            retrossDepartureDto.getFares().forEach(fare -> {
                trainAvailabilityDtoList.add(retrossDepartureDtoToTrainAvailabilityDto(retrossDepartureDto, fare, userId));
            });
        }
        ListAvailabilityDto listAvailabilityDto = new ListAvailabilityDto();
        listAvailabilityDto.setDepartures(trainAvailabilityDtoList);
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
}
