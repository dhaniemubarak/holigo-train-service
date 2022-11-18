package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import id.holigo.services.holigotrainservice.web.model.TrainTransactionTripDtoForUser;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@DecoratedWith(TrainTransactionTripMapperDecorator.class)
@Mapper(uses = PassengerMapper.class)
public interface TrainTransactionTripMapper {

    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "bookCode", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "adultAmount", source = "finalFare.inquiry.adultAmount")
    @Mapping(target = "childAmount", source = "finalFare.inquiry.childAmount")
    @Mapping(target = "infantAmount", source = "finalFare.inquiry.infantAmount")
    @Mapping(target = "supplierSelectedId", source = "supplierId")
    @Mapping(target = "id", ignore = true)
    TrainTransactionTrip trainFinalFareTripToTrainTransactionTrip(TrainFinalFareTrip trainFinalFareTrip);

    TrainTransactionTripDtoForUser trainTransactionTripToTrainTransactionTripDtoForUser(TrainTransactionTrip trainTransactionTrip);
}
