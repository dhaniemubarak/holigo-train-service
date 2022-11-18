package id.holigo.services.holigotrainservice.services;

import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.web.model.TrainBookDto;

public interface TrainTransactionService {
    TrainTransaction createTransaction(TrainBookDto trainBookDto, Long userId);

    void cancelTransaction(TrainTransaction trainTransaction);
}
