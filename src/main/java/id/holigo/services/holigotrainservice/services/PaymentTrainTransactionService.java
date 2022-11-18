package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.holigotrainservice.events.PaymentStatusEvent;
import org.springframework.statemachine.StateMachine;

public interface PaymentTrainTransactionService {
    StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasPaid(Long id);

    void paymentHasCanceled(Long id);

    StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasExpired(Long id);
}
