package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import id.holigo.services.holigotrainservice.interceptors.OrderTrainTransactionInterceptor;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderTrainTransactionServiceImpl implements OrderTrainTransactionService {


    public static final String TRANSACTION_HEADER = "order_train_transaction_id";

    private final StateMachineFactory<OrderStatusEnum, OrderStatusEvent> orderTrainTransactionStateMachine;
    private final OrderTrainTransactionInterceptor orderTrainTransactionInterceptor;
    private TrainTransactionRepository trainTransactionRepository;

    @Autowired
    public void setTrainTransactionRepository(TrainTransactionRepository trainTransactionRepository) {
        this.trainTransactionRepository = trainTransactionRepository;
    }

    @Override
    public void booked(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.BOOK_SUCCESS);
    }

    @Override
    public void bookingFail(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.BOOK_FAIL);
    }

    @Override
    public void bookingSuccess(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.BOOK_SUCCESS);
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusEvent> processIssued(Long trainTransactionId) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(trainTransactionId);
        sendEvent(trainTransactionId, sm, OrderStatusEvent.PROCESS_ISSUED);
        return sm;
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusEvent> issued(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.ISSUED_SUCCESS);
        return sm;
    }

    @Override
    public void cancelTransaction(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.ORDER_CANCEL);
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusEvent> orderHasExpired(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.ORDER_EXPIRE);
        return sm;
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusEvent> orderHasCanceled(Long id) {
        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = build(id);
        sendEvent(id, sm, OrderStatusEvent.ORDER_CANCEL);
        return sm;
    }

    private void sendEvent(Long trainTransactionTripId, StateMachine<OrderStatusEnum, OrderStatusEvent> sm,
                           OrderStatusEvent event) {
        Message<OrderStatusEvent> message = MessageBuilder.withPayload(event)
                .setHeader(TRANSACTION_HEADER, trainTransactionTripId).build();
        sm.sendEvent(message);

    }

    private StateMachine<OrderStatusEnum, OrderStatusEvent> build(Long id) {
        TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(id);

        StateMachine<OrderStatusEnum, OrderStatusEvent> sm = orderTrainTransactionStateMachine
                .getStateMachine(trainTransaction.getId().toString());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(orderTrainTransactionInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(trainTransaction.getOrderStatus(),
                    null, null, null));
        });
        sm.start();
        return sm;
    }
}
