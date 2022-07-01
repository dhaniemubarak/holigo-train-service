package id.holigo.services.holigotrainservice.actions;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.holigotrainservice.events.OrderStatusTripEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.action.Action;

@Slf4j
public class OrderTrainTransactionAction {
    public Action<OrderStatusEnum, OrderStatusTripEvent> processIssued() {
        return stateContext -> log.info("Process issued is running...");
    }
}
