package id.holigo.services.holigotrainservice.web.mappers;


import id.holigo.services.common.model.TransactionDto;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestBookDto;
import id.holigo.services.holigotrainservice.web.model.TrainTransactionDtoForUser;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@DecoratedWith(TrainTransactionMapperDecorator.class)
@Mapper
public interface TrainTransactionMapper {

    TrainTransactionDtoForUser trainTransactionToTrainTransactionDtoForUser(TrainTransaction trainTransaction);

    @Mapping(target = "voucherCode", ignore = true)
    @Mapping(target = "transactionType", ignore = true)
    @Mapping(target = "serviceId", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "pointAmount", ignore = true)
    @Mapping(target = "paymentServiceId", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "parentId", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "note", ignore = true)
    @Mapping(target = "invoiceNumber", ignore = true)
    @Mapping(target = "indexUser", ignore = true)
    @Mapping(target = "indexProduct", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "id", ignore = true)
    TransactionDto trainTransactionToTransactionDto(TrainTransaction trainTransaction);

    RetrossRequestBookDto trainTransactionToRetrossRequestBookDto(TrainTransaction transaction);
}