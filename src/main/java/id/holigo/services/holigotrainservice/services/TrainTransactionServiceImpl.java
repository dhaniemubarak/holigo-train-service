package id.holigo.services.holigotrainservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.holigotrainservice.domain.*;
import id.holigo.services.holigotrainservice.repositories.*;
import id.holigo.services.holigotrainservice.services.retross.RetrossTrainService;
import id.holigo.services.holigotrainservice.web.exceptions.NotFoundException;
import id.holigo.services.holigotrainservice.web.mappers.*;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestBookDto;
import id.holigo.services.holigotrainservice.web.model.RetrossResponseBookDto;
import id.holigo.services.holigotrainservice.web.model.TrainBookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TrainTransactionServiceImpl implements TrainTransactionService {

    private OrderStatusTripService orderStatusTripService;

    private TrainFinalFareRepository trainFinalFareRepository;

    private ContactPersonRepository contactPersonRepository;

    private ContactPersonMapper contactPersonMapper;

    private TrainFinalFareMapper trainFinalFareMapper;

    private TrainTransactionRepository trainTransactionRepository;

    private PassengerMapper passengerMapper;

    private PassengerRepository passengerRepository;

    private TrainTransactionTripMapper trainTransactionTripMapper;


    private TrainTransactionTripRepository trainTransactionTripRepository;

    private TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository;

    private InquiryMapper inquiryMapper;

    private RetrossTrainService retrossTrainService;

    private PaymentStatusTransactionService paymentStatusTransactionService;

    @Autowired
    public void setPaymentStatusTripService(PaymentStatusTransactionService paymentStatusTransactionService) {
        this.paymentStatusTransactionService = paymentStatusTransactionService;
    }

    @Autowired
    public void setTrainFinalFareRepository(TrainFinalFareRepository trainFinalFareRepository) {
        this.trainFinalFareRepository = trainFinalFareRepository;
    }

    @Autowired
    public void setContactPersonRepository(ContactPersonRepository contactPersonRepository) {
        this.contactPersonRepository = contactPersonRepository;
    }

    @Autowired
    public void setContactPersonMapper(ContactPersonMapper contactPersonMapper) {
        this.contactPersonMapper = contactPersonMapper;
    }

    @Autowired
    public void setTrainFinalFareMapper(TrainFinalFareMapper trainFinalFareMapper) {
        this.trainFinalFareMapper = trainFinalFareMapper;
    }

    @Autowired
    public void setTrainTransactionRepository(TrainTransactionRepository trainTransactionRepository) {
        this.trainTransactionRepository = trainTransactionRepository;
    }

    @Autowired
    public void setPassengerMapper(PassengerMapper passengerMapper) {
        this.passengerMapper = passengerMapper;
    }

    @Autowired
    public void setPassengerRepository(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Autowired
    public void setTrainTransactionTripMapper(TrainTransactionTripMapper trainTransactionTripMapper) {
        this.trainTransactionTripMapper = trainTransactionTripMapper;
    }

    @Autowired
    public void setTrainTransactionTripPassengerRepository(TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository) {
        this.trainTransactionTripPassengerRepository = trainTransactionTripPassengerRepository;
    }

    @Autowired
    public void setTrainTransactionTripRepository(TrainTransactionTripRepository trainTransactionTripRepository) {
        this.trainTransactionTripRepository = trainTransactionTripRepository;
    }

    @Autowired
    public void setInquiryMapper(InquiryMapper inquiryMapper) {
        this.inquiryMapper = inquiryMapper;
    }

    @Autowired
    public void setRetrossTrainService(RetrossTrainService retrossTrainService) {
        this.retrossTrainService = retrossTrainService;
    }

    @Autowired
    public void setOrderStatusTripService(OrderStatusTripService orderStatusTripService) {
        this.orderStatusTripService = orderStatusTripService;
    }

    @Transactional
    @Override
    public TrainTransaction createTransaction(TrainBookDto trainBookDto, Long userId) {

        TrainFinalFare trainFinalFare;
        Optional<TrainFinalFare> fetchTrainFinalFare = trainFinalFareRepository.findById(trainBookDto.getFareId());
        if (fetchTrainFinalFare.isPresent()) {
            trainFinalFare = fetchTrainFinalFare.get();
        } else {
            throw new NotFoundException("Train not available");
        }

        ContactPerson savedContactPerson = contactPersonRepository.save(
                contactPersonMapper.contactPersonDtoToContactPerson(trainBookDto.getContactPerson()));

//        // Create train transaction
        TrainTransaction trainTransaction = trainFinalFareMapper.trainFinalFareToTrainTransaction(trainFinalFare);
        trainTransaction.setContactPerson(savedContactPerson);


        // Set expired
        trainTransaction.setTripType(trainFinalFare.getInquiry().getTripType());
        trainTransaction.setExpiredAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(40L)));
        trainTransaction.setDiscountAmount(BigDecimal.valueOf(0.00));
        trainTransaction.setPaymentStatus(PaymentStatusEnum.SELECTING_PAYMENT);
        TrainTransaction savedTrainTransaction = trainTransactionRepository.save(trainTransaction);


        // Create passenger
        List<Passenger> passengers = trainBookDto.getPassengers().stream()
                .map(passengerMapper::passengerDtoToPassenger).toList();
        Iterable<Passenger> savedPassengers = passengerRepository.saveAll(passengers);
        // End of create passenger

        // Create trip
        List<TrainTransactionTrip> trips = new ArrayList<>();

        trainFinalFare.getTrips().forEach(trainFinalFareTrip -> {
            TrainTransactionTrip trainTransactionTrip = trainTransactionTripMapper.trainFinalFareTripToTrainTransactionTrip(trainFinalFareTrip);
            trainTransactionTrip.setPaymentStatus(PaymentStatusEnum.SELECTING_PAYMENT);
            trainTransactionTrip.setOrderStatus(OrderStatusEnum.PROCESS_BOOK);
            trainTransactionTrip.setTrainTransaction(savedTrainTransaction);
            trips.add(trainTransactionTrip);
        });
        Iterable<TrainTransactionTrip> savedTrainTransactionTrip = trainTransactionTripRepository.saveAll(trips);
        savedTrainTransactionTrip.forEach(trainTransactionTrip -> savedPassengers.forEach(passenger -> {
            TrainTransactionTripPassenger trainTransactionTripPassenger = new TrainTransactionTripPassenger();
            trainTransactionTripPassenger.setPassenger(passenger);
            trainTransactionTripPassenger.setTrip(trainTransactionTrip);
            trainTransactionTrip.addToPassengers(trainTransactionTripPassenger);
        }));

        // create master transaction


        // Book to retross
        // Build request param
        RetrossRequestBookDto retrossRequestBookDto = inquiryMapper.inquiryToRetrossRequestBookDto(trainFinalFare.getInquiry());
        retrossRequestBookDto.setTgl_dep(trainFinalFare.getInquiry().getDepartureDate().toString());
        retrossRequestBookDto.setSelectedIdDep(trainFinalFare.getTrips().get(0).getSupplierId());
        if (retrossRequestBookDto.getTrip().equals("R")) {
            retrossRequestBookDto.setTgl_ret(trainFinalFare.getInquiry().getReturnDate().toString());
            retrossRequestBookDto.setSelectedIdRet(trainFinalFare.getTrips().get(1).getSupplierId());
        }
        retrossRequestBookDto.setCpname(trainBookDto.getContactPerson().getName());
        retrossRequestBookDto.setCptlp(trainBookDto.getContactPerson().getPhoneNumber());
        retrossRequestBookDto.setCpmail(trainBookDto.getContactPerson().getEmail());
        retrossRequestBookDto.setPassengers(trainBookDto.getPassengers());
        RetrossResponseBookDto retrossResponseBookDto;
        try {
            retrossResponseBookDto = retrossTrainService.book(retrossRequestBookDto);
            log.info(new ObjectMapper().writeValueAsString(retrossResponseBookDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        int tripCounter = 0;
        for (TrainTransactionTrip trainTransactionTrip : savedTrainTransactionTrip) {
            if (retrossResponseBookDto.getError_code().equals("001")) {
                orderStatusTripService.bookingFail(trainTransactionTrip.getId());
                continue;
            }
            int passengerCounter = 0;
            for (TrainTransactionTripPassenger trainTransactionTripPassenger : trainTransactionTrip.getPassengers()) {
                log.info("Passenger counter -> {}", passengerCounter);
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
            orderStatusTripService.bookingSuccess(trainTransactionTrip.getId());
            tripCounter++;
        }

        return savedTrainTransaction;
    }
}
