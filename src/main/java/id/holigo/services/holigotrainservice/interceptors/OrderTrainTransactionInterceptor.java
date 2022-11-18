package id.holigo.services.holigotrainservice.interceptors;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.services.OrderTrainTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OrderTrainTransactionInterceptor
        extends StateMachineInterceptorAdapter<OrderStatusEnum, OrderStatusEvent> {

    private final TrainTransactionRepository trainTransactionRepository;

    @Override
    public void preStateChange(State<OrderStatusEnum, OrderStatusEvent> state,
                               Message<OrderStatusEvent> message,
                               Transition<OrderStatusEnum, OrderStatusEvent> transition,
                               StateMachine<OrderStatusEnum, OrderStatusEvent> stateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.of(
                Long.valueOf(Objects.requireNonNull(msg.getHeaders().get(OrderTrainTransactionServiceImpl.TRANSACTION_HEADER))
                        .toString()))).ifPresent(id -> {
            TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(id);
            trainTransaction.setOrderStatus(state.getId());
            trainTransactionRepository.save(trainTransaction);
        });
    }
}
