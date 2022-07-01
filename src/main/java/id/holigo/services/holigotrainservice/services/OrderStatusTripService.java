package id.holigo.services.holigotrainservice.services;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.events.OrderStatusTripEvent;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public interface OrderStatusTripService {

    StateMachine<OrderStatusEnum, OrderStatusTripEvent> bookingFail(UUID trainTransactionId);

    StateMachine<OrderStatusEnum, OrderStatusTripEvent> bookingSuccess(UUID trainTransactionId);

    StateMachine<OrderStatusEnum, OrderStatusTripEvent> cancelTransaction(UUID transactionId);
}
