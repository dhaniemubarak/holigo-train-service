package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.*;
import id.holigo.services.holigotrainservice.components.Fare;
import id.holigo.services.holigotrainservice.config.FeeConfig;
import id.holigo.services.holigotrainservice.domain.*;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import id.holigo.services.holigotrainservice.repositories.TrainAvailabilityRepository;
import id.holigo.services.holigotrainservice.repositories.TrainFinalFareRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripPassengerRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripRepository;
import id.holigo.services.holigotrainservice.services.retross.RetrossTrainService;
import id.holigo.services.holigotrainservice.services.transaction.TransactionService;
import id.holigo.services.holigotrainservice.web.exceptions.BookException;
import id.holigo.services.holigotrainservice.web.exceptions.FinalFareBadRequestException;
import id.holigo.services.holigotrainservice.web.mappers.InquiryMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainAvailabilityMapper;
import id.holigo.services.holigotrainservice.web.mappers.TrainTransactionMapper;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    private TransactionService transactionService;

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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
        RetrossResponseScheduleDto retrossResponseScheduleDto = retrossTrainService.getSchedule(requestScheduleDto, inquiryDto.getUserId());
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
            retrossResponseBookDto = retrossTrainService.book(retrossRequestBookDto, trainTransaction.getUserId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        AtomicInteger tripCounter = new AtomicInteger();
        if (retrossResponseBookDto.getError_code().equals("001")) {
            orderTrainTransactionService.bookingFail(trainTransaction.getId());
            paymentTrainTransactionService.paymentHasCanceled(trainTransaction.getId());
            throw new BookException();
        } else {
            orderTrainTransactionService.booked(trainTransaction.getId());
        }
        for (TrainTransactionTrip trainTransactionTrip : trainTransaction.getTrips()) {
            AtomicInteger passengerCounter = new AtomicInteger();
            for (TrainTransactionTripPassenger trainTransactionTripPassenger : trainTransactionTripPassengerRepository.findAllByTripIdOrderBySortAsc(trainTransactionTrip.getId())) {
                if (trainTransactionTripPassenger.getPassenger().getType().equals(PassengerType.ADULT)) {
                    retrossResponseBookDto.getPassengers().forEach(retrossPassengerDto -> {
                        if (retrossPassengerDto.getNama().split(" ")[0].contains(trainTransactionTripPassenger.getPassenger().getName().split(" ")[0])) {
                            trainTransactionTripPassenger.setSeatNumber(
                                    (tripCounter.get() == 0) ? retrossPassengerDto.getSeat_dep()
                                            : retrossPassengerDto.getSeat_ret());
                            trainTransactionTripPassengerRepository.save(trainTransactionTripPassenger);
                        }
                    });
                }
                passengerCounter.getAndIncrement();
            }

            trainTransactionTrip.setSupplierTransactionId(retrossResponseBookDto.getSupplierId());
            trainTransactionTrip.setBookCode(
                    (tripCounter.get() == 0) ? retrossResponseBookDto.getBookCodeDeparture()
                            : retrossResponseBookDto.getBookCodeReturn());
            trainTransactionTripRepository.save(trainTransactionTrip);
            tripCounter.getAndIncrement();
        }
    }

    @Override
    public void issued(TrainTransaction trainTransaction) {
        Boolean isIssued;
        try {
            isIssued = retrossTrainService.issued(trainTransaction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (isIssued) {
            StateMachine<OrderStatusEnum, OrderStatusEvent> sm = orderTrainTransactionService.issued(trainTransaction.getId());
            if (sm.getState().getId().equals(OrderStatusEnum.ISSUED)) {
                transactionService.updateOrderStatusTransaction(TransactionDto.builder()
                        .orderStatus(OrderStatusEnum.ISSUED)
                        .id(trainTransaction.getTransactionId()).build());
            }
        }
    }

    @Override
    public void cancelBook(TrainTransaction trainTransaction) {

        RetrossCancelDto retrossCancelDto = RetrossCancelDto.builder()
                .notrx(trainTransaction.getTrips().get(0).getSupplierTransactionId()).build();
        try {
            retrossTrainService.cancel(retrossCancelDto, trainTransaction.getUserId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        orderTrainTransactionService.cancelTransaction(trainTransaction.getId());
        paymentTrainTransactionService.paymentHasCanceled(trainTransaction.getId());
    }

    @Transactional
    @Override
    public TrainFinalFare createFinalFare(RequestFinalFareDto requestFinalFareDto, Long userId) {
        FareDto fareDto = fare.getFare(userId, requestFinalFareDto.getTrips().size() != 1);
        TrainFinalFare trainFinalFare = new TrainFinalFare();
        trainFinalFare.setId(UUID.randomUUID());
        trainFinalFare.setInquiry(inquiryMapper.inquiryDtoToInquiry(requestFinalFareDto.getTrips().get(0).getInquiry()));
        trainFinalFare.setUserId(userId);
        trainFinalFare.setIsBookable(true);
        trainFinalFare.setNtaAmount(BigDecimal.valueOf(0.00));
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
        AtomicInteger segment = new AtomicInteger(1);
        for (TripDto tripDto : requestFinalFareDto.getTrips()) {
            Optional<TrainAvailability> fetchTrainAvailability = trainAvailabilityRepository.findById(tripDto.getTrip().getId());
            if (fetchTrainAvailability.isEmpty()) {
                throw new FinalFareBadRequestException();
            }
            TrainAvailability trainAvailability = fetchTrainAvailability.get();
            TrainFinalFareTrip trainFinalFareTrip = trainAvailabilityMapper.trainAvailabilityToTrainFinalFareTrip(trainAvailability);
            trainFinalFareTrip.setSegment(segment.get());
            TrainAvailabilityFareDto trainAvailabilityFareDto;
            try {
                trainAvailabilityFareDto = objectMapper.readValue(trainAvailability.getFare(), TrainAvailabilityFareDto.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            trainFinalFareTrip.setSupplierId(trainAvailabilityFareDto.getSelectedId());
            trainFinalFareTrip.setFareAmount(trainAvailabilityFareDto.getFareAmount());
            trainFinalFareTrip.setAdminAmount(FeeConfig.ADMIN_AMOUNT);
            trainFinalFareTrip.setNraAmount(FeeConfig.NRA_AMOUNT);
            trainFinalFareTrip.setNtaAmount(trainAvailabilityFareDto.getFareAmount());
            BigDecimal cpAmount = trainFinalFare.getCpAmount();
            BigDecimal mpAmount = trainFinalFare.getMpAmount();
            BigDecimal ipAmount = trainFinalFare.getIpAmount();
            BigDecimal hpAmount = trainFinalFare.getHpAmount();
            BigDecimal hvAmount = trainFinalFare.getHvAmount();
            BigDecimal prAmount = trainFinalFare.getPrAmount();
            BigDecimal ipcAmount = trainFinalFare.getIpcAmount();
            BigDecimal hpcAmount = trainFinalFare.getHpcAmount();
            BigDecimal prcAmount = trainFinalFare.getPrcAmount();
            BigDecimal lossAmount = trainFinalFare.getLossAmount();
            trainFinalFareTrip.setCpAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? cpAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getCpAmount());
            trainFinalFareTrip.setMpAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? mpAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getMpAmount());
            trainFinalFareTrip.setIpAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? ipAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getIpAmount());
            trainFinalFareTrip.setHpAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? hpAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getHpAmount());
            trainFinalFareTrip.setHvAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? hvAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getHvAmount());
            trainFinalFareTrip.setPrAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? prAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getPrAmount());
            trainFinalFareTrip.setIpcAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? ipcAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getIpcAmount());
            trainFinalFareTrip.setHpcAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? hpcAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getHpcAmount());
            trainFinalFareTrip.setPrcAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? prcAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getPrcAmount());
            trainFinalFareTrip.setLossAmount(trainFinalFare.getInquiry().getTripType().equals(TripType.R) ? lossAmount.divide(BigDecimal.valueOf(2), RoundingMode.UP) : trainFinalFare.getLossAmount());
            trainFinalFare.setNtaAmount(trainFinalFare.getNtaAmount().add(trainFinalFareTrip.getFareAmount()));
            trainFinalFare.setFareAmount(trainFinalFare.getFareAmount().add(trainFinalFareTrip.getFareAmount()));
            trainFinalFare.addToTrips(trainFinalFareTrip);
            segment.getAndIncrement();
        }
        return trainFinalFareRepository.save(trainFinalFare);
    }
}
