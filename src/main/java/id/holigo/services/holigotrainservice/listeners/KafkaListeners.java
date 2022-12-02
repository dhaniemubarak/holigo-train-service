package id.holigo.services.holigotrainservice.listeners;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.common.model.TransactionDto;
import id.holigo.services.holigotrainservice.config.KafkaTopicConfig;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.events.OrderStatusEvent;
import id.holigo.services.holigotrainservice.events.PaymentStatusEvent;
import id.holigo.services.holigotrainservice.repositories.TrainTransactionRepository;
import id.holigo.services.holigotrainservice.services.OrderTrainTransactionService;
import id.holigo.services.holigotrainservice.services.PaymentTrainTransactionService;
import id.holigo.services.holigotrainservice.services.TrainService;
import id.holigo.services.holigotrainservice.services.transaction.TransactionService;
import id.holigo.services.holigotrainservice.web.model.TrainTransactionDto;
import id.holigo.services.holigotrainservice.web.model.TrainTransactionDtoForUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.Nullable;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class KafkaListeners {

    private final TrainTransactionRepository trainTransactionRepository;

    private final TrainService trainService;

    private final PaymentTrainTransactionService paymentTrainTransactionService;

    private final OrderTrainTransactionService orderTrainTransactionService;

    private final TransactionService transactionService;

    private final TransactionTemplate transactionTemplate;

    @KafkaListener(topics = KafkaTopicConfig.UPDATE_ORDER_STATUS_TRAIN_TRANSACTION, groupId = "order-status-train-transaction", containerFactory = "trainTransactionListenerFactory")
    void updateOrderStatus(TrainTransactionDto trainTransactionDto) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@Nullable TransactionStatus status) {
                Optional<TrainTransaction> fetchAirlinesTransaction = trainTransactionRepository.findById(trainTransactionDto.getId());
                if (fetchAirlinesTransaction.isPresent()) {
                    TrainTransaction trainTransaction = fetchAirlinesTransaction.get();
                    switch (trainTransactionDto.getOrderStatus()) {
                        case ORDER_EXPIRED -> {
                            StateMachine<OrderStatusEnum, OrderStatusEvent> sm = orderTrainTransactionService.orderHasExpired(trainTransaction.getId());
                            if (sm.getState().getId().equals(OrderStatusEnum.ORDER_EXPIRED)) {
                                paymentTrainTransactionService.paymentHasExpired(trainTransaction.getId());
                            }
                        }
                        case PROCESS_BOOK, BOOKED, BOOK_FAILED, PROCESS_ISSUED, WAITING_ISSUED, ISSUED, RETRYING_ISSUED ->
                                log.info("case PROCESS_BOOK, BOOKED, BOOK_FAILED, PROCESS_ISSUED, WAITING_ISSUED, ISSUED, RETRYING_ISSUED ");
                        case ISSUED_FAILED -> transactionService.updateOrderStatusTransaction(TransactionDto.builder()
                                .id(trainTransaction.getTransactionId())
                                .orderStatus(OrderStatusEnum.ISSUED_FAILED).build());
                        case ORDER_CANCELED -> {
                            StateMachine<OrderStatusEnum, OrderStatusEvent> sm = orderTrainTransactionService.orderHasCanceled(trainTransaction.getId());
                            if (sm.getState().getId().equals(OrderStatusEnum.ORDER_CANCELED)) {
                                paymentTrainTransactionService.paymentHasCanceled(trainTransaction.getId());
                            }
                        }
                    }
                }
            }
        });
    }

    @Transactional
    @KafkaListener(topics = KafkaTopicConfig.UPDATE_PAYMENT_STATUS_TRAIN_TRANSACTION, groupId = "payment-status-airlines-transaction", containerFactory = "airlinesTransactionListenerFactory")
    void updatePaymentStatus(TrainTransactionDtoForUser trainTransactionDtoForUser) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(@Nullable TransactionStatus status) {
                Optional<TrainTransaction> fetchTrainTransaction = trainTransactionRepository.findById(trainTransactionDtoForUser.getId());
                if (fetchTrainTransaction.isPresent()) {
                    TrainTransaction trainTransaction = fetchTrainTransaction.get();
                    switch (trainTransactionDtoForUser.getPaymentStatus()) {
                        case WAITING_PAYMENT ->
                                paymentTrainTransactionService.paymentHasSelected(trainTransaction.getId());
                        case PAYMENT_EXPIRED -> {
                            StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm = paymentTrainTransactionService.paymentHasExpired(trainTransaction.getId());
                            if (sm.getState().getId().equals(PaymentStatusEnum.PAYMENT_EXPIRED)) {
                                orderTrainTransactionService.orderHasExpired(trainTransaction.getId());
                            }
                        }
                        case PAID -> {
                            StateMachine<PaymentStatusEnum, PaymentStatusEvent> sm = paymentTrainTransactionService.paymentHasPaid(trainTransaction.getId());
                            if (sm.getState().getId().equals(PaymentStatusEnum.PAID)) {
                                StateMachine<OrderStatusEnum, OrderStatusEvent> orderStateMachine = orderTrainTransactionService.processIssued(trainTransaction.getId());
                                if (orderStateMachine.getState().getId().equals(OrderStatusEnum.PROCESS_ISSUED)) {
                                    trainService.issued(trainTransaction);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
