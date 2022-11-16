package id.holigo.services.holigotrainservice.interceptors;

import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.PaymentStatusEvent;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.services.PaymentStatusTransactionServiceImpl;
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
public class PaymentStatusTransactionInterceptor extends StateMachineInterceptorAdapter<PaymentStatusEnum, PaymentStatusEvent> {

    private final TrainTransactionRepository trainTransactionRepository;

    @Override
    public void preStateChange(State<PaymentStatusEnum, PaymentStatusEvent> state, Message<PaymentStatusEvent> message,
                               Transition<PaymentStatusEnum, PaymentStatusEvent> transition,
                               StateMachine<PaymentStatusEnum, PaymentStatusEvent> stateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.of(
                UUID.fromString(Objects.requireNonNull(msg.getHeaders().get(PaymentStatusTransactionServiceImpl.TRAIN_TRANSACTION_HEADER))
                        .toString()))).ifPresent(id -> {
            TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(id);
            trainTransaction.setPaymentStatus(state.getId());
            trainTransactionRepository.save(trainTransaction);
        });
    }
}
