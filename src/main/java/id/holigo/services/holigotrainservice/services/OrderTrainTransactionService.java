package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import org.springframework.statemachine.StateMachine;

public interface OrderTrainTransactionService {

    StateMachine<OrderStatusEnum, OrderStatusEvent> booked(Long id);

    void bookingFail(Long id);

    void bookingSuccess(Long id);

    void processIssued(Long id);

    void cancelTransaction(Long id);
}
