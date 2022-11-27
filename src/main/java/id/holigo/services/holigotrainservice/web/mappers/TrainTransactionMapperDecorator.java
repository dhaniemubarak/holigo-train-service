package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.common.model.TransactionDto;
import id.holigo.services.common.model.UserDtoForUser;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripPassengerRepository;
import id.holigo.services.holigotrainservice.services.user.UserService;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Slf4j
public abstract class TrainTransactionMapperDecorator implements TrainTransactionMapper {

    @Value("${train.iconUrl}")
    private String iconUrl;
    private TrainTransactionMapper trainTransactionMapper;

    private UserService userService;

    private PassengerMapper passengerMapper;

    private TrainTransactionTripMapper trainTransactionTripMapper;


    private TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository;

    @Autowired
    public void setTrainTransactionTripPassengerRepository(TrainTransactionTripPassengerRepository trainTransactionTripPassengerRepository) {
        this.trainTransactionTripPassengerRepository = trainTransactionTripPassengerRepository;
    }


    @Autowired
    public void setTrainTransactionTripMapper(TrainTransactionTripMapper trainTransactionTripMapper) {
        this.trainTransactionTripMapper = trainTransactionTripMapper;
    }

    @Autowired
    public void setPassengerMapper(PassengerMapper passengerMapper) {
        this.passengerMapper = passengerMapper;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setTrainTransactionMapper(TrainTransactionMapper trainTransactionMapper) {
        this.trainTransactionMapper = trainTransactionMapper;
    }

    @Override
    public TrainTransactionDtoForUser trainTransactionToTrainTransactionDtoForUser(TrainTransaction trainTransaction) {
        TrainTransactionDtoForUser trainTransactionDtoForUser = this.trainTransactionMapper.trainTransactionToTrainTransactionDtoForUser(trainTransaction);
        trainTransactionDtoForUser.setIconUrl(iconUrl);
        trainTransactionDtoForUser.setSeatMapUrl("/api/v1/train/" + trainTransactionDtoForUser.getId() + "/trips");
        trainTransactionDtoForUser.setTrips(trainTransaction.getTrips().stream().map(trainTransactionTrip -> trainTransactionTripMapper.trainTransactionTripToTrainTransactionTripDtoForUser(trainTransactionTrip)).collect(Collectors.toList()));
        return trainTransactionDtoForUser;
    }

    @Override
    public TransactionDto trainTransactionToTransactionDto(TrainTransaction trainTransaction) {
        TransactionDto transactionDto = trainTransactionMapper.trainTransactionToTransactionDto(trainTransaction);
        UserDtoForUser userDtoForUser = userService.getUser(trainTransaction.getUserId());
        TrainTransactionTrip trainTransactionTrip = trainTransaction.getTrips().get(0);
        transactionDto.setTransactionType("TRAIN");
        transactionDto.setServiceId(3);
        transactionDto.setProductId(3);
        transactionDto.setPointAmount(BigDecimal.valueOf(0.0));
        transactionDto.setTransactionId(trainTransaction.getId().toString());
        transactionDto.setIndexProduct(trainTransaction.getTripType().toString() + "|"
                + trainTransactionTrip.getOriginStation().getCity() + " - "
                + trainTransactionTrip.getDestinationStation().getCity() + "|"
                + trainTransactionTrip.getDepartureDate().toString() + " " + trainTransactionTrip.getDepartureTime());
        transactionDto.setIndexUser(userDtoForUser.getName() + "|" + userDtoForUser.getPhoneNumber() + "|" + userDtoForUser.getEmail());
        return transactionDto;
    }

    @Override
    public RetrossRequestBookDto trainTransactionToRetrossRequestBookDto(TrainTransaction trainTransaction) {
        RetrossRequestBookDto retrossRequestBookDto = new RetrossRequestBookDto();
        retrossRequestBookDto.setAdt(trainTransaction.getTrips().get(0).getAdultAmount());
        retrossRequestBookDto.setChd(trainTransaction.getTrips().get(0).getChildAmount());
        retrossRequestBookDto.setInf(trainTransaction.getTrips().get(0).getInfantAmount());
        retrossRequestBookDto.setCpname(trainTransaction.getContactPerson().getName());
        retrossRequestBookDto.setCpmail(trainTransaction.getContactPerson().getEmail());
        retrossRequestBookDto.setCptlp(trainTransaction.getContactPerson().getPhoneNumber());
        retrossRequestBookDto.setOrg(trainTransaction.getTrips().get(0).getOriginStation().getId());
        retrossRequestBookDto.setDes(trainTransaction.getTrips().get(0).getDestinationStation().getId());
        retrossRequestBookDto.setTrip(trainTransaction.getTripType().toString());
        retrossRequestBookDto.setTgl_dep(trainTransaction.getTrips().get(0).getDepartureDate().toString());
        retrossRequestBookDto.setSelectedIdDep(trainTransaction.getTrips().get(0).getSupplierSelectedId());
        if (retrossRequestBookDto.getTrip().equals("R")) {
            retrossRequestBookDto.setTgl_ret(trainTransaction.getTrips().get(1).getDepartureDate().toString());
            retrossRequestBookDto.setSelectedIdRet(trainTransaction.getTrips().get(1).getSupplierSelectedId());
        }
        retrossRequestBookDto.setPassengers(
                trainTransactionTripPassengerRepository.findAllByTripId(trainTransaction.getTrips().get(0).getId()).stream().map(passengerMapper::trainTransactionTripPassengerToPassengerDto).collect(Collectors.toList()));
        return retrossRequestBookDto;
    }
}
