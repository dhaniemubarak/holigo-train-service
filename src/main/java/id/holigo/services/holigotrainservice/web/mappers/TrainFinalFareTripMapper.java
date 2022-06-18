package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.web.model.TrainFinalFareTripDto;
import org.mapstruct.Mapper;

@Mapper
public interface TrainFinalFareTripMapper {
    TrainFinalFareTripDto trainFinalFareTripToTrainFinalFareTripDto(TrainFinalFareTrip trainFinalFareTrip);
}
