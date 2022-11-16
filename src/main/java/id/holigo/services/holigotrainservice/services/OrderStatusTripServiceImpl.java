package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import id.holigo.services.holigotrainservice.events.OrderStatusTripEvent;
import id.holigo.services.holigotrainservice.interceptors.OrderStatusTripInterceptor;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderStatusTripServiceImpl implements OrderStatusTripService {


    public static final String TRANSACTION_TRIP_HEADER = "train_transaction_trip_id";

    private final StateMachineFactory<OrderStatusEnum, OrderStatusTripEvent> orderStatusTripStateMachine;
    private final OrderStatusTripInterceptor orderStatusTripInterceptor;
    private TrainTransactionTripRepository trainTransactionTripRepository;

    @Autowired
    public void setTrainTransactionTripRepository(TrainTransactionTripRepository trainTransactionTripRepository) {
        this.trainTransactionTripRepository = trainTransactionTripRepository;
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusTripEvent> bookingFail(UUID trainTransactionTrioId) {
        StateMachine<OrderStatusEnum, OrderStatusTripEvent> sm = build(trainTransactionTrioId);
        sendEvent(trainTransactionTrioId, sm, OrderStatusTripEvent.BOOK_FAIL);
        return sm;
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusTripEvent> bookingSuccess(UUID trainTransactionTrioId) {
        StateMachine<OrderStatusEnum, OrderStatusTripEvent> sm = build(trainTransactionTrioId);
        sendEvent(trainTransactionTrioId, sm, OrderStatusTripEvent.BOOK_SUCCESS);
        return sm;
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusTripEvent> processIssued(UUID trainTransactionId) {
        StateMachine<OrderStatusEnum, OrderStatusTripEvent> sm = build(trainTransactionId);
        sendEvent(trainTransactionId, sm, OrderStatusTripEvent.PROCESS_ISSUED);
        return sm;
    }

    @Override
    public StateMachine<OrderStatusEnum, OrderStatusTripEvent> cancelTransaction(UUID trainTransactionTrioId) {
        StateMachine<OrderStatusEnum, OrderStatusTripEvent> sm = build(trainTransactionTrioId);
        sendEvent(trainTransactionTrioId, sm, OrderStatusTripEvent.BOOK_CANCEL);
        return sm;
    }

    private void sendEvent(UUID trainTransactionTripId, StateMachine<OrderStatusEnum, OrderStatusTripEvent> sm,
                           OrderStatusTripEvent event) {
        Message<OrderStatusTripEvent> message = MessageBuilder.withPayload(event)
                .setHeader(TRANSACTION_TRIP_HEADER, trainTransactionTripId).build();
        sm.sendEvent(message);

    }

    private StateMachine<OrderStatusEnum, OrderStatusTripEvent> build(UUID trainTransactionId) {
        TrainTransactionTrip trainTransactionTrip = trainTransactionTripRepository.getReferenceById(trainTransactionId);

        StateMachine<OrderStatusEnum, OrderStatusTripEvent> sm = orderStatusTripStateMachine
                .getStateMachine(trainTransactionTrip.getId().toString());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(orderStatusTripInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(trainTransactionTrip.getOrderStatus(),
                    null, null, null));
        });
        sm.start();
        return sm;
    }
}
