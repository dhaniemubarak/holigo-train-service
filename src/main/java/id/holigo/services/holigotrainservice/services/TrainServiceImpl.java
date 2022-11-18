package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.FareDto;
import id.holigo.services.common.model.TripType;
import id.holigo.services.holigotrainservice.components.Fare;
import id.holigo.services.holigotrainservice.config.FeeConfig;
import id.holigo.services.holigotrainservice.domain.*;
import id.holigo.services.holigotrainservice.repositories.TrainAvailabilityRepository;
import id.holigo.services.holigotrainservice.repositories.TrainFinalFareRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripPassengerRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripRepository;
import id.holigo.services.holigotrainservice.services.retross.RetrossTrainService;
import id.holigo.services.holigotrainservice.web.exceptions.FinalFareBadRequestException;
import id.holigo.services.holigotrainservice.web.mappers.InquiryMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainAvailabilityMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainTransactionMapper;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrainServiceImpl implements TrainService {


    private Fare fare;
    private TrainFinalFareRepository trainFinalFareRepository;
    private TrainAvailabilityRepository trainAvailabilityRepository;
    private RetrossTrainService retrossTrainService;

    private TrainAvailabilityMapper trainAvailabilityMapper;

    private final ObjectMapper objectMapper;

    private InquiryMapper inquiryMapper;

    private TrainTransactionMapper trainTransactionMapper;

    private OrderTrainTransactionService orderTrainTransactionService;

    private PaymentTrainTransactionService paymentTrainTransactionService;

    private TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository;

    private TrainTransactionTripRepository trainTransactionTripRepository;

    @Autowired
    public void setOrderTrainTransactionService(OrderTrainTransactionService orderTrainTransactionService) {
        this.orderTrainTransactionService = orderTrainTransactionService;
    }

    @Autowired
    public void setTrainTransactionTripRepository(TrainTransactionTripRepository trainTransactionTripRepository) {
        this.trainTransactionTripRepository = trainTransactionTripRepository;
    }

    @Autowired
    public void setPaymentTrainTransactionService(PaymentTrainTransactionService paymentTrainTransactionService) {
        this.paymentTrainTransactionService = paymentTrainTransactionService;
    }

    @Autowired
    public void setTrainTransactionTripPassengerRepository(TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository) {
        this.trainTransactionTripPassengerRepository = trainTransactionTripPassengerRepository;
    }

    @Autowired
    public void setOrderStatusTripService(OrderTrainTransactionService orderTrainTransactionService) {
        this.orderTrainTransactionService = orderTrainTransactionService;
    }

    @Autowired
    public void setTrainTransactionMapper(TrainTransactionMapper trainTransactionMapper) {
        this.trainTransactionMapper = trainTransactionMapper;
    }

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
        if (retrossResponseScheduleDto.getSchedule() == null) {
            return null;
        }
        ListAvailabilityDto listAvailabilityDto = trainAvailabilityMapper.retrossResponseScheduleDtoToListAvailabilityDto(retrossResponseScheduleDto, inquiryDto);
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

    @Override
    public void createBook(TrainTransaction trainTransaction) {
        RetrossRequestBookDto retrossRequestBookDto = trainTransactionMapper.trainTransactionToRetrossRequestBookDto(trainTransaction);
        RetrossResponseBookDto retrossResponseBookDto;
        try {
            retrossResponseBookDto = retrossTrainService.book(retrossRequestBookDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        int tripCounter = 0;
        for (TrainTransactionTrip trainTransactionTrip : trainTransaction.getTrips()) {
            if (retrossResponseBookDto.getError_code().equals("001")) {
                orderTrainTransactionService.bookingFail(trainTransaction.getId());
                paymentTrainTransactionService.paymentHasCanceled(trainTransaction.getId());
                continue;
            }
            int passengerCounter = 0;
            for (TrainTransactionTripPassenger trainTransactionTripPassenger : trainTransactionTripPassengerRepository.findAllByTripId(trainTransactionTrip.getId())) {
                trainTransactionTripPassenger.setSeatNumber(
                        (tripCounter == 0) ? retrossResponseBookDto.getPassengers().get(passengerCounter).getSeat_dep()
                                : retrossResponseBookDto.getPassengers().get(passengerCounter).getSeat_ret());
                trainTransactionTripPassengerRepository.save(trainTransactionTripPassenger);
                passengerCounter++;
            }
            trainTransactionTrip.setSupplierId(retrossResponseBookDto.getSupplierId());
            trainTransactionTrip.setBookCode(
                    (tripCounter == 0) ? retrossResponseBookDto.getBookCodeDeparture()
                            : retrossResponseBookDto.getBookCodeReturn());
            trainTransactionTripRepository.save(trainTransactionTrip);
            orderTrainTransactionService.bookingSuccess(trainTransaction.getId());
            tripCounter++;
        }
    }

    @Override
    public void issued(TrainTransaction trainTransaction) {
        // TODO issued
    }

    @Override
    public void cancelBook(TrainTransaction trainTransaction) {

        RetrossCancelDto retrossCancelDto = RetrossCancelDto.builder()
                .notrx(trainTransaction.getTrips().get(0).getSupplierId()).build();
        try {
            retrossTrainService.cancel(retrossCancelDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        orderTrainTransactionService.cancelTransaction(trainTransaction.getId());
        paymentTrainTransactionService.paymentHasCanceled(trainTransaction.getId());
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
