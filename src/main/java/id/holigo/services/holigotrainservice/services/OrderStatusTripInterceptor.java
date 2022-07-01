package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import id.holigo.services.holigotrainservice.events.OrderStatusTripEvent;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class OrderStatusTripInterceptor
        extends StateMachineInterceptorAdapter<OrderStatusEnum, OrderStatusTripEvent> {

    private final TrainTransactionTripRepository trainTransactionTripRepository;

    @Override
    public void preStateChange(State<OrderStatusEnum, OrderStatusTripEvent> state,
                               Message<OrderStatusTripEvent> message,
                               Transition<OrderStatusEnum, OrderStatusTripEvent> transition,
                               StateMachine<OrderStatusEnum, OrderStatusTripEvent> stateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.of(
                UUID.fromString(Objects.requireNonNull(msg.getHeaders().get(OrderStatusTripServiceImpl.TRANSACTION_TRIP_HEADER))
                        .toString()))).ifPresent(id -> {
            TrainTransactionTrip trainTransactionTrip = trainTransactionTripRepository.getReferenceById(id);
            trainTransactionTrip.setOrderStatus(state.getId());
            trainTransactionTripRepository.save(trainTransactionTrip);
        });
    }
}
