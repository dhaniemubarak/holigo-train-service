package id.holigo.services.holigotrainservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String UPDATE_ORDER_STATUS_TRAIN_TRANSACTION = "update-order-status-train-transaction";

    public static final String UPDATE_PAYMENT_STATUS_TRAIN_TRANSACTION = "update-payment-status-train-transaction";

    @Bean
    public NewTopic updateOrderStatusTrainTransaction() {
        return TopicBuilder.name(UPDATE_ORDER_STATUS_TRAIN_TRANSACTION).build();
    }

    @Bean
    public NewTopic updateStatusPaymentTrainTransaction() {
        return TopicBuilder.name(UPDATE_PAYMENT_STATUS_TRAIN_TRANSACTION).build();
    }
}
