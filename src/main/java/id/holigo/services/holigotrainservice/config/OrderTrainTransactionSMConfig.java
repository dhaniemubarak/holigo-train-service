package id.holigo.services.holigotrainservice.config;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionTripRepository;
import id.holigo.services.holigotrainservice.services.OrderTrainTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@RequiredArgsConstructor
@EnableStateMachineFactory(name = "orderTrainTransactionSMF")
public class OrderTrainTransactionSMConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusEvent> {

    private final TrainTransactionRepository trainTransactionRepository;

    private final TrainTransactionTripRepository trainTransactionTripRepository;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusEvent> states) throws Exception {
        states.withStates().initial(OrderStatusEnum.PROCESS_BOOK)
                .states(EnumSet.allOf(OrderStatusEnum.class))
                .end(OrderStatusEnum.BOOK_FAILED)
                .end(OrderStatusEnum.ISSUED)
                .end(OrderStatusEnum.ISSUED_FAILED)
                .end(OrderStatusEnum.ORDER_CANCELED)
                .end(OrderStatusEnum.ORDER_EXPIRED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusEvent> transitions)
            throws Exception {
        transitions.withExternal().source(OrderStatusEnum.PROCESS_BOOK).target(OrderStatusEnum.BOOKED)
                .event(OrderStatusEvent.BOOK_SUCCESS).action(bookSuccess())
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_BOOK).target(OrderStatusEnum.BOOK_FAILED)
                .event(OrderStatusEvent.BOOK_FAIL).action(bookFailed())
                .and()
                .withExternal().source(OrderStatusEnum.BOOKED).target(OrderStatusEnum.PROCESS_ISSUED)
                .event(OrderStatusEvent.PROCESS_ISSUED).action(processIssued())
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.ISSUED)
                .event(OrderStatusEvent.ISSUED_SUCCESS)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.ISSUED_FAILED)
                .event(OrderStatusEvent.ISSUED_FAIL)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.WAITING_ISSUED)
                .event(OrderStatusEvent.WAITING_ISSUED)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.RETRYING_ISSUED)
                .event(OrderStatusEvent.RETRYING_ISSUED)
                .and()
                .withExternal().source(OrderStatusEnum.BOOKED).target(OrderStatusEnum.ORDER_CANCELED)
                .event(OrderStatusEvent.ORDER_CANCEL).action(orderCanceled())
                .and()
                .withExternal().source(OrderStatusEnum.BOOKED).target(OrderStatusEnum.ORDER_EXPIRED)
                .event(OrderStatusEvent.ORDER_EXPIRE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatusEnum, OrderStatusEvent> config)
            throws Exception {
        StateMachineListenerAdapter<OrderStatusEnum, OrderStatusEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderStatusEnum, OrderStatusEvent> from,
                                     State<OrderStatusEnum, OrderStatusEvent> to) {
                log.info(String.format("stateChange(from: %s, to %s)", from.getId(), to.getId()));
            }
        };
        config.withConfiguration().listener(adapter);
    }

    @Bean
    public Action<OrderStatusEnum, OrderStatusEvent> bookSuccess() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository
                    .getReferenceById(Long.parseLong(stateContext
                            .getMessageHeader(OrderTrainTransactionServiceImpl.TRANSACTION_HEADER).toString()));
            trainTransaction.getTrips().forEach(trainTransactionTrip -> {
                trainTransactionTrip.setOrderStatus(OrderStatusEnum.BOOKED);
                trainTransactionTripRepository.save(trainTransactionTrip);
            });
        };
    }

    @Bean
    public Action<OrderStatusEnum, OrderStatusEvent> processIssued() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository
                    .getReferenceById(Long.parseLong(stateContext
                            .getMessageHeader(OrderTrainTransactionServiceImpl.TRANSACTION_HEADER).toString()));
            trainTransaction.getTrips().forEach(trainTransactionTrip -> {
                trainTransactionTrip.setOrderStatus(OrderStatusEnum.PROCESS_ISSUED);
                trainTransactionTripRepository.save(trainTransactionTrip);
            });
        };
    }

    @Bean
    public Action<OrderStatusEnum, OrderStatusEvent> bookFailed() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository
                    .getReferenceById(Long.parseLong(stateContext
                            .getMessageHeader(OrderTrainTransactionServiceImpl.TRANSACTION_HEADER).toString()));
            trainTransaction.getTrips().forEach(airlinesTransactionTrip -> {
                airlinesTransactionTrip.setOrderStatus(OrderStatusEnum.BOOK_FAILED);
                trainTransactionTripRepository.save(airlinesTransactionTrip);
            });
        };
    }

    @Bean
    public Action<OrderStatusEnum, OrderStatusEvent> orderCanceled() {
        return stateContext -> {
            TrainTransaction trainTransaction = trainTransactionRepository
                    .getReferenceById(Long.parseLong(stateContext
                            .getMessageHeader(OrderTrainTransactionServiceImpl.TRANSACTION_HEADER).toString()));
            trainTransaction.getTrips().forEach(airlinesTransactionTrip -> {
                airlinesTransactionTrip.setOrderStatus(OrderStatusEnum.ORDER_CANCELED);
                trainTransactionTripRepository.save(airlinesTransactionTrip);
            });
        };
    }
}
