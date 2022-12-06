package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import id.holigo.services.holigotrainservice.web.model.SeatMapDto;
import id.holigo.services.common.model.TrainTransactionTripDtoForUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

public class TrainTransactionTripMapperDecorator implements TrainTransactionTripMapper {

    private TrainTransactionTripMapper trainTransactionTripMapper;

    private TrainTransactionTripPassengerMapper trainTransactionTripPassengerMapper;

    @Autowired
    public void setTrainTransactionTripMapper(TrainTransactionTripMapper trainTransactionTripMapper) {
        this.trainTransactionTripMapper = trainTransactionTripMapper;
    }

    @Autowired
    public void setTrainTransactionTripPassengerMapper(TrainTransactionTripPassengerMapper trainTransactionTripPassengerMapper) {
        this.trainTransactionTripPassengerMapper = trainTransactionTripPassengerMapper;
    }

    @Override
    public TrainTransactionTrip trainFinalFareTripToTrainTransactionTrip(TrainFinalFareTrip trainFinalFareTrip) {
        return this.trainTransactionTripMapper.trainFinalFareTripToTrainTransactionTrip(trainFinalFareTrip);
    }

    @Override
    public TrainTransactionTripDtoForUser trainTransactionTripToTrainTransactionTripDtoForUser(TrainTransactionTrip trainTransactionTrip) {
        TrainTransactionTripDtoForUser trainTransactionTripDtoForUser = this.trainTransactionTripMapper.trainTransactionTripToTrainTransactionTripDtoForUser(trainTransactionTrip);
        trainTransactionTripDtoForUser.setPassengers(trainTransactionTrip.getPassengers()
                .stream().map(trainTransactionTripPassengerMapper::trainTransactionTripPassengerToTrainTransactionTripPassengerDto).collect(Collectors.toList()));
        return trainTransactionTripDtoForUser;
    }

    @Override
    public TrainTransactionTripDtoForUser trainTransactionTripToTrainTransactionTripDtoForUser(TrainTransactionTrip trainTransactionTrip, SeatMapDto seatMapDto) {
        TrainTransactionTripDtoForUser trainTransactionTripDtoForUser = this.trainTransactionTripMapper.trainTransactionTripToTrainTransactionTripDtoForUser(trainTransactionTrip, seatMapDto);
        trainTransactionTripDtoForUser.setPassengers(trainTransactionTrip.getPassengers()
                .stream().map(trainTransactionTripPassengerMapper::trainTransactionTripPassengerToTrainTransactionTripPassengerDto).collect(Collectors.toList()));
        trainTransactionTripDtoForUser.setSeatMap(seatMapDto);
        return trainTransactionTripDtoForUser;
    }
}
