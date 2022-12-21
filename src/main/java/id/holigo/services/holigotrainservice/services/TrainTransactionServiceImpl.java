package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.*;
import id.holigo.services.holigotrainservice.domain.*;
import id.holigo.services.holigotrainservice.repositories.*;
import id.holigo.services.holigotrainservice.services.transaction.TransactionService;
import id.holigo.services.holigotrainservice.services.user.UserService;
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

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

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
        String indexUser;
        String indexPassenger;
        String indexProduct;
        TrainFinalFare trainFinalFare;
        Optional<TrainFinalFare> fetchTrainFinalFare = trainFinalFareRepository.findById(trainBookDto.getFareId());
        if (fetchTrainFinalFare.isPresent()) {
            trainFinalFare = fetchTrainFinalFare.get();
        } else {
            throw new NotFoundException("Train not available");
        }
        UserDtoForUser userDtoForUser = userService.getUser(userId);
        String userGroup;
        switch (userDtoForUser.getUserGroup()) {
            case MEMBER -> userGroup = "Member";
            case NETIZEN -> userGroup = "Netizen";
            case BOSSQIU -> userGroup = "BossQiu";
            case SOELTAN -> userGroup = "Soeltan";
            case CRAZY_RICH -> userGroup = "Crazy Rich";
            default -> userGroup = "Guest";
        }
        indexUser = userDtoForUser.getName() + "|" + userDtoForUser.getPhoneNumber() + "|" + userGroup;
        ContactPerson savedContactPerson = contactPersonRepository.save(
                contactPersonMapper.contactPersonDtoToContactPerson(trainBookDto.getContactPerson()));
        // Create passenger
        List<Passenger> passengers = trainBookDto.getPassengers().stream()
                .map(passengerMapper::passengerDtoToPassenger).toList();
        Iterable<Passenger> savedPassengers = passengerRepository.saveAll(passengers);
        // End of create passenger
        List<String> stringPassenger = new ArrayList<>();
        passengers.forEach(passenger -> {
            String type;
            switch (passenger.getType()) {
                case CHILD -> type = "Anak";
                case INFANT -> type = "Bayi";
                default -> type = "Dewasa";
            }
            String title = passenger.getTitle().toString().toLowerCase();
            stringPassenger.add(title.substring(0, 1).toUpperCase() + title.substring(1) + ". " + passenger.getName() + " (" + type + ")");
        });
        indexPassenger = String.join("|", stringPassenger);
//        // Create train transaction
        TrainTransaction trainTransaction = trainFinalFareMapper.trainFinalFareToTrainTransaction(trainFinalFare);
        trainTransaction.setContactPerson(savedContactPerson);
        // Set expired
        trainTransaction.setTripType(trainFinalFare.getInquiry().getTripType());
        trainTransaction.setExpiredAt(Timestamp.valueOf(LocalDateTime.now().plusMinutes(40L)));
        trainTransaction.setDiscountAmount(BigDecimal.valueOf(0.00));
        trainTransaction.setOrderStatus(OrderStatusEnum.PROCESS_BOOK);
        trainTransaction.setPaymentStatus(PaymentStatusEnum.SELECTING_PAYMENT);
        // CREATE INDEX PRODUCT
        if (trainTransaction.getTripType().equals(TripType.O)) {
            String trainClass = getTrainClass(trainFinalFare.getTrips().get(0).getTrainClass());
            indexProduct = trainTransaction.getTripType().toString() + "|"
                    + trainFinalFare.getTrips().get(0).getOriginStation().getCity() + " - " + trainFinalFare.getTrips().get(0).getDestinationStation().getCity() + "|"
                    + trainFinalFare.getTrips().get(0).getDepartureDate().toString() + " " + trainFinalFare.getTrips().get(0).getDepartureTime().toString() + "|"
                    + trainFinalFare.getTrips().get(0).getTrainName() + " (" + trainFinalFare.getTrips().get(0).getTrainNumber() + ")" + " - " + trainClass + "|"
                    + trainFinalFare.getTrips().get(0).getAdultAmount() + "," + trainFinalFare.getTrips().get(0).getChildAmount() + "," + trainFinalFare.getTrips().get(0).getInfantAmount() + "|";
        } else {
            String departureTrainClass = getTrainClass(trainFinalFare.getTrips().get(0).getTrainClass());
            String returnTrainClass = getTrainClass(trainFinalFare.getTrips().get(1).getTrainClass());
            indexProduct = trainTransaction.getTripType().toString() + "|"
                    + trainFinalFare.getTrips().get(0).getOriginStation().getCity() + " - " + trainFinalFare.getTrips().get(0).getDestinationStation().getCity() + "|"
                    + trainFinalFare.getTrips().get(0).getDepartureDate().toString() + " " + trainFinalFare.getTrips().get(0).getDepartureTime().toString() + "," + trainFinalFare.getTrips().get(1).getDepartureDate().toString() + " " + trainFinalFare.getTrips().get(1).getDepartureTime().toString() + "|"
                    + trainFinalFare.getTrips().get(0).getTrainName() + " (" + trainFinalFare.getTrips().get(0).getTrainNumber() + ")" + " - " + departureTrainClass + "," + trainFinalFare.getTrips().get(1).getTrainName() + " (" + trainFinalFare.getTrips().get(1).getTrainNumber() + ")" + " - " + returnTrainClass + "|"
                    + trainFinalFare.getTrips().get(0).getAdultAmount() + "," + trainFinalFare.getTrips().get(0).getChildAmount() + "," + trainFinalFare.getTrips().get(0).getInfantAmount() + "|";
        }
        trainTransaction.setIndexProduct(indexProduct);
        trainTransaction.setIndexUser(indexUser);
        trainTransaction.setIndexPassenger(indexPassenger);
        TrainTransaction savedTrainTransaction = trainTransactionRepository.save(trainTransaction);
        // Create trip
        List<TrainTransactionTrip> trips = new ArrayList<>();
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
        trainService.createBook(savedTrainTransaction);
        // create master transaction
        TransactionDto transactionDto = trainTransactionMapper.trainTransactionToTransactionDto(savedTrainTransaction);
        try {
            TransactionDto newTransaction = transactionService.createNewTransaction(transactionDto);
            savedTrainTransaction.setTransactionId(newTransaction.getId());
            savedTrainTransaction.setInvoiceNumber(newTransaction.getInvoiceNumber());
            trainTransactionRepository.save(savedTrainTransaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return savedTrainTransaction;
    }

    private String getTrainClass(String classCode) {
        String trainClass;
        switch (classCode) {
            case "EKO" -> trainClass = "Ekonomi";
            case "BIS" -> trainClass = "Bisnis";
            case "EKS" -> trainClass = "Eksekutif";
            case "LUX" -> trainClass = "Luxury";
            default -> trainClass = classCode;
        }
        return trainClass;
    }

    @Override
    public void cancelTransaction(TrainTransaction trainTransaction) {
        trainService.cancelBook(trainTransaction);
    }
}
