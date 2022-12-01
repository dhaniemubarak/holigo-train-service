package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.common.model.TransactionDto;
import id.holigo.services.holigotrainservice.domain.*;
import id.holigo.services.holigotrainservice.repositories.*;
import id.holigo.services.holigotrainservice.services.transaction.TransactionService;
import id.holigo.services.holigotrainservice.web.exceptions.NotFoundException;
import id.holigo.services.holigotrainservice.web.mappers.*;
import id.holigo.services.holigotrainservice.web.model.TrainBookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class TrainTransactionServiceImpl implements TrainTransactionService {

    private TrainFinalFareRepository trainFinalFareRepository;

    private ContactPersonRepository contactPersonRepository;

    private ContactPersonMapper contactPersonMapper;

    private TrainFinalFareMapper trainFinalFareMapper;

    private TrainTransactionRepository trainTransactionRepository;

    private PassengerMapper passengerMapper;

    private PassengerRepository passengerRepository;

    private TrainTransactionTripMapper trainTransactionTripMapper;

    private TrainService trainService;

    private TrainTransactionTripRepository trainTransactionTripRepository;

    private TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository;

    private TransactionService transactionService;

    private TrainTransactionMapper trainTransactionMapper;

    @Autowired
    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    @Autowired
    public void setTrainTransactionMapper(TrainTransactionMapper trainTransactionMapper) {
        this.trainTransactionMapper = trainTransactionMapper;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
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
        trainTransaction.setOrderStatus(OrderStatusEnum.PROCESS_BOOK);
        trainTransaction.setPaymentStatus(PaymentStatusEnum.SELECTING_PAYMENT);
        TrainTransaction savedTrainTransaction = trainTransactionRepository.save(trainTransaction);

        // Create passenger
        List<Passenger> passengers = trainBookDto.getPassengers().stream()
                .map(passengerMapper::passengerDtoToPassenger).toList();
        Iterable<Passenger> savedPassengers = passengerRepository.saveAll(passengers);
//        // End of create passenger
//
//        // Create trip
        List<TrainTransactionTrip> trips = new ArrayList<>();
//
        trainFinalFare.getTrips().forEach(trainFinalFareTrip -> {
            TrainTransactionTrip trainTransactionTrip = trainTransactionTripMapper.trainFinalFareTripToTrainTransactionTrip(trainFinalFareTrip);
            trainTransactionTrip.setPaymentStatus(PaymentStatusEnum.SELECTING_PAYMENT);
            trainTransactionTrip.setOrderStatus(OrderStatusEnum.PROCESS_BOOK);
            trainTransactionTrip.setTransaction(savedTrainTransaction);
            trips.add(trainTransactionTrip);
        });
        Iterable<TrainTransactionTrip> savedTrainTransactionTrip = trainTransactionTripRepository.saveAll(trips);

        List<TrainTransactionTripPassenger> trainTransactionTripPassengers = new ArrayList<>();

        savedTrainTransactionTrip.forEach(trainTransactionTrip -> {
            savedTrainTransaction.addTrip(trainTransactionTrip);
            savedPassengers.forEach(passenger -> {
                TrainTransactionTripPassenger trainTransactionTripPassenger = new TrainTransactionTripPassenger();
                trainTransactionTripPassenger.setTrip(trainTransactionTrip);
                trainTransactionTripPassenger.setPassenger(passenger);
                trainTransactionTripPassenger.setSort(passenger.getSort());
                trainTransactionTripPassengers.add(trainTransactionTripPassenger);
            });
        });
        trainTransactionTripPassengerRepository.saveAll(trainTransactionTripPassengers);
        // create master transaction
        TransactionDto transactionDto = trainTransactionMapper.trainTransactionToTransactionDto(savedTrainTransaction);
        try {
            TransactionDto newTransaction = transactionService.createNewTransaction(transactionDto);
            savedTrainTransaction.setTransactionId(newTransaction.getId());
            trainTransactionRepository.save(savedTrainTransaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        trainService.createBook(savedTrainTransaction);
        return savedTrainTransaction;
    }

    @Override
    public void cancelTransaction(TrainTransaction trainTransaction) {
        trainService.cancelBook(trainTransaction);
    }
}
