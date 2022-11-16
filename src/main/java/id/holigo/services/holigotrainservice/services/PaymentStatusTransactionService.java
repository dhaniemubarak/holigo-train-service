package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.holigotrainservice.events.PaymentStatusEvent;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public interface PaymentStatusTransactionService {
    StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasPaid(UUID id);

    StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasCanceled(UUID id);

    StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasExpired(UUID id);
}
