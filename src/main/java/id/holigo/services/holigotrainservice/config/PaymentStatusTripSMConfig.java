package id.holigo.services.holigotrainservice.config;

import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.PaymentStatusEvent;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripRepository;
import id.holigo.services.holigotrainservice.services.OrderStatusTripService;
import id.holigo.services.holigotrainservice.services.PaymentStatusTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory(name = "paymentTrainTransactionSMF")
@Configuration
public class PaymentStatusTripSMConfig extends StateMachineConfigurerAdapter<PaymentStatusEnum, PaymentStatusEvent> {

    private final TrainTransactionRepository trainTransactionRepository;

    private final TrainTransactionTripRepository trainTransactionTripRepository;

    private final OrderStatusTripService orderStatusTripService;


    @Override
    public void configure(StateMachineStateConfigurer<PaymentStatusEnum, PaymentStatusEvent> states) throws Exception {
        states.withStates().initial(PaymentStatusEnum.SELECTING_PAYMENT)
                .states(EnumSet.allOf(PaymentStatusEnum.class))
                .end(PaymentStatusEnum.PAYMENT_FAILED)
                .end(PaymentStatusEnum.PAYMENT_CANCELED)
                .end(PaymentStatusEnum.PAYMENT_EXPIRED)
                .end(PaymentStatusEnum.REFUNDED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentStatusEnum, PaymentStatusEvent> transitions)
            throws Exception {
        transitions.withExternal().source(PaymentStatusEnum.WAITING_PAYMENT).target(PaymentStatusEnum.PAID)
                .event(PaymentStatusEvent.PAYMENT_PAID).action(paymentHasPaid())
                .and().withExternal().source(PaymentStatusEnum.SELECTING_PAYMENT).target(PaymentStatusEnum.PAYMENT_EXPIRED)
                .event(PaymentStatusEvent.PAYMENT_EXPIRED).action(paymentHasExpired())
                .and().withExternal().source(PaymentStatusEnum.WAITING_PAYMENT).target(PaymentStatusEnum.PAYMENT_EXPIRED)
                .event(PaymentStatusEvent.PAYMENT_EXPIRED).action(paymentHasExpired())
                .and().withExternal().source(PaymentStatusEnum.SELECTING_PAYMENT).target(PaymentStatusEnum.PAYMENT_CANCELED)
                .event(PaymentStatusEvent.PAYMENT_CANCEL).action(paymentHasCanceled())
                .and().withExternal().source(PaymentStatusEnum.WAITING_PAYMENT).target(PaymentStatusEnum.PAYMENT_CANCELED)
                .event(PaymentStatusEvent.PAYMENT_CANCEL).action(paymentHasCanceled());
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentStatusEnum, PaymentStatusEvent> config)
            throws Exception {
        StateMachineListenerAdapter<PaymentStatusEnum, PaymentStatusEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentStatusEnum, PaymentStatusEvent> from,
                                     State<PaymentStatusEnum, PaymentStatusEvent> to) {
                log.info(String.format("stateChange(from: %s, to %s)", from.getId(), to.getId()));
            }
        };
        config.withConfiguration().listener(adapter);
    }

    @Bean
    public Action<PaymentStatusEnum, PaymentStatusEvent> paymentHasPaid() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(UUID.fromString(
                    stateContext
                            .getMessageHeader(PaymentStatusTransactionServiceImpl.TRAIN_TRANSACTION_HEADER).toString()
            ));
            trainTransaction.getTrips().forEach(trainTransactionTrip -> {
                trainTransactionTrip.setPaymentStatus(PaymentStatusEnum.PAID);
                trainTransactionTripRepository.save(trainTransactionTrip);
            });
            orderStatusTripService.processIssued(trainTransaction.getId());
        };
    }

    @Bean
    public Action<PaymentStatusEnum, PaymentStatusEvent> paymentHasExpired() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(
                    UUID.fromString(stateContext.getMessageHeader(
                            PaymentStatusTransactionServiceImpl.TRAIN_TRANSACTION_HEADER).toString()));
            trainTransaction.getTrips().forEach(trainTransactionTrip -> {
                trainTransactionTrip.setPaymentStatus(PaymentStatusEnum.PAYMENT_EXPIRED);
                trainTransactionTripRepository.save(trainTransactionTrip);
            });
        };
    }

    @Bean
    public Action<PaymentStatusEnum, PaymentStatusEvent> paymentHasCanceled() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository.getReferenceById(
                    UUID.fromString(stateContext.getMessageHeader(
                            PaymentStatusTransactionServiceImpl.TRAIN_TRANSACTION_HEADER).toString()));
            trainTransaction.getTrips().forEach(trainTransactionTrip -> {
                trainTransactionTrip.setPaymentStatus(PaymentStatusEnum.PAYMENT_CANCELED);
                trainTransactionTripRepository.save(trainTransactionTrip);
            });
        };
    }
}
