package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.PaymentStatusEvent;
import id.holigo.services.holigotrainservice.interceptors.PaymentTrainTransactionInterceptor;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentTrainTransactionServiceImpl implements PaymentTrainTransactionService {

    public static final String TRAIN_TRANSACTION_HEADER = "train-transaction-id";

    private final StateMachineFactory<PaymentStatusEnum, PaymentStatusEvent> stateMachineFactory;

    private final PaymentTrainTransactionInterceptor paymentStatusTransactionInterceptor;

    private TrainTransactionRepository trainTransactionRepository;

    @Autowired
    public void setTrainTransactionRepository(TrainTransactionRepository trainTransactionRepository) {
        this.trainTransactionRepository = trainTransactionRepository;
    }

    @Override
    public StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasPaid(Long id) {
        StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm = build(id);
        sendEvent(id, sm, PaymentStatusEvent.PAYMENT_PAID);
        return sm;
    }

    @Override
    public void paymentHasCanceled(Long id) {
        StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm = build(id);
        sendEvent(id, sm, PaymentStatusEvent.PAYMENT_CANCEL);
    }

    @Override
    public StateMachine<PaymentStatusEnum, PaymentStatusEvent> paymentHasExpired(Long id) {
        StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm = build(id);
        sendEvent(id, sm, PaymentStatusEvent.PAYMENT_EXPIRED);
        return sm;
    }

    private void sendEvent(Long trainTransactionId, StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm,
                           PaymentStatusEvent event) {
        Message<PaymentStatusEvent> message = MessageBuilder.withPayload(event)
                .setHeader(TRAIN_TRANSACTION_HEADER, trainTransactionId).build();
        sm.sendEvent(message);

    }

    private StateMachine<PaymentStatusEnum, PaymentStatusEvent> build(Long trainTransactionId) {
        TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(trainTransactionId);

        StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm = stateMachineFactory
                .getStateMachine(trainTransaction.getId().toString());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(paymentStatusTransactionInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(trainTransaction.getPaymentStatus(),
                    null, null, null));
        });
        sm.start();
        return sm;
    }
}
