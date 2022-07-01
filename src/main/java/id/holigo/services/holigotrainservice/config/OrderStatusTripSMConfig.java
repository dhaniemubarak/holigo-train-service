package id.holigo.services.holigotrainservice.config;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.actions.OrderTrainTransactionAction;
import id.holigo.services.holigotrainservice.events.OrderStatusTripEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;


@Slf4j
@EnableStateMachineFactory(name = "orderTrainTransactionSMF")
@Configuration
public class OrderStatusTripSMConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusTripEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusTripEvent> states) throws Exception {
        states.withStates().initial(OrderStatusEnum.PROCESS_BOOK)
                .states(EnumSet.allOf(OrderStatusEnum.class))
                .end(OrderStatusEnum.BOOK_FAILED)
                .end(OrderStatusEnum.ISSUED)
                .end(OrderStatusEnum.ISSUED_FAILED)
                .end(OrderStatusEnum.ORDER_CANCELED)
                .end(OrderStatusEnum.ORDER_EXPIRED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusTripEvent> transitions) throws Exception {
        transitions.withExternal().source(OrderStatusEnum.PROCESS_BOOK).target(OrderStatusEnum.BOOKED)
                .event(OrderStatusTripEvent.BOOK_SUCCESS)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_BOOK).target(OrderStatusEnum.BOOK_FAILED)
                .event(OrderStatusTripEvent.BOOK_FAIL)
                .and()
                .withExternal().source(OrderStatusEnum.BOOKED).target(OrderStatusEnum.PROCESS_ISSUED)
                .action(new OrderTrainTransactionAction().processIssued())
                .event(OrderStatusTripEvent.PROCESS_ISSUED)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.ISSUED)
                .event(OrderStatusTripEvent.ISSUED_SUCCESS)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.ISSUED_FAILED)
                .event(OrderStatusTripEvent.ISSUED_FAIL)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.WAITING_ISSUED)
                .event(OrderStatusTripEvent.WAITING_ISSUED)
                .and()
                .withExternal().source(OrderStatusEnum.PROCESS_ISSUED).target(OrderStatusEnum.RETRYING_ISSUED)
                .event(OrderStatusTripEvent.RETRYING_ISSUED)
                .and()
                .withExternal().source(OrderStatusEnum.BOOKED).target(OrderStatusEnum.ORDER_CANCELED)
                .event(OrderStatusTripEvent.BOOK_CANCEL)
                .and()
                .withExternal().source(OrderStatusEnum.BOOKED).target(OrderStatusEnum.ORDER_EXPIRED)
                .event(OrderStatusTripEvent.BOOK_EXPIRE);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatusEnum, OrderStatusTripEvent> config) throws Exception {
        StateMachineListenerAdapter<OrderStatusEnum, OrderStatusTripEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<OrderStatusEnum, OrderStatusTripEvent> from, State<OrderStatusEnum, OrderStatusTripEvent> to) {
                log.info(String.format("stateChange(from: %s, %s", from.getId(), to.getId()));
            }
        };
        config.withConfiguration().listener(adapter);
    }
}
