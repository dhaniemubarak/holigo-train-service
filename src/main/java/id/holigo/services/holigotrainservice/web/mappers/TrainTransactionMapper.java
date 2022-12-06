package id.holigo.services.holigotrainservice.web.mappers;


import id.holigo.services.common.model.TransactionDto;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestBookDto;
import id.holigo.services.common.model.TrainTransactionDtoForUser;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@DecoratedWith(TrainTransactionMapperDecorator.class)
@Mapper
public interface TrainTransactionMapper {

    @Mapping(target = "seatMapUrl", ignore = true)
    @Mapping(target = "iconUrl", ignore = true)
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

    @Mapping(target = "trip", ignore = true)
    @Mapping(target = "tgl_ret", ignore = true)
    @Mapping(target = "tgl_dep", ignore = true)
    @Mapping(target = "selectedIdRet", ignore = true)
    @Mapping(target = "selectedIdDep", ignore = true)
    @Mapping(target = "rqid", ignore = true)
    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "org", ignore = true)
    @Mapping(target = "mmid", ignore = true)
    @Mapping(target = "map", ignore = true)
    @Mapping(target = "inf", ignore = true)
    @Mapping(target = "des", ignore = true)
    @Mapping(target = "cptlp", ignore = true)
    @Mapping(target = "cpname", ignore = true)
    @Mapping(target = "cpmail", ignore = true)
    @Mapping(target = "chd", ignore = true)
    @Mapping(target = "app", ignore = true)
    @Mapping(target = "adt", ignore = true)
    @Mapping(target = "action", ignore = true)
    RetrossRequestBookDto  trainTransactionToRetrossRequestBookDto(TrainTransaction transaction);
}
