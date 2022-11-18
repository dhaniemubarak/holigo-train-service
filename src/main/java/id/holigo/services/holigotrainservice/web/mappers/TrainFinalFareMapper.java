package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.web.model.TrainFinalFareDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TrainFinalFareTripMapper.class})
public interface TrainFinalFareMapper {
    @Mapping(target = "note", ignore = true)
    TrainFinalFareDto trainFinalFareToTrainFinalFareDto(TrainFinalFare trainFinalFare);

    @Mapping(target = "tripType", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "contactPerson", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trips", ignore = true)
    TrainTransaction trainFinalFareToTrainTransaction(TrainFinalFare trainFinalFare);
}
