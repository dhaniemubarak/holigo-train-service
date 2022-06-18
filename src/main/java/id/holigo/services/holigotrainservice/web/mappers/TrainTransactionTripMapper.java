package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TrainTransactionTripMapper {

    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "bookCode", ignore = true)
    @Mapping(target = "trainTransaction", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "id", ignore = true)
    TrainTransactionTrip trainFinalFareTripToTrainTransactionTrip(TrainFinalFareTrip trainFinalFareTrip);
}
