package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import org.springframework.statemachine.StateMachine;

public interface OrderTrainTransactionService {

    void booked(Long id);

    void bookingFail(Long id);

    void bookingSuccess(Long id);

    StateMachine<OrderStatusEnum, OrderStatusEvent> processIssued(Long id);

    StateMachine<OrderStatusEnum, OrderStatusEvent> issued(Long id);

    void cancelTransaction(Long id);

    StateMachine<OrderStatusEnum, OrderStatusEvent> orderHasExpired(Long id);

    StateMachine<OrderStatusEnum, OrderStatusEvent> orderHasCanceled(Long id);
}
