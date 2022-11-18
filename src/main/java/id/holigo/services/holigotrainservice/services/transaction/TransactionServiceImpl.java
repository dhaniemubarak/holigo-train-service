package id.holigo.services.holigotrainservice.services.transaction;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.TransactionDto;
import id.holigo.services.holigotrainservice.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;

@RequiredArgsConstructor
@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final ObjectMapper objectMapper;

    private final JmsTemplate jmsTemplate;

    @Override
    public TransactionDto createNewTransaction(TransactionDto transactionDto) throws JMSException, JsonProcessingException {
        Message received = jmsTemplate.sendAndReceive(JmsConfig.CREATE_NEW_TRANSACTION, session -> {
            Message data;
            try {
                data = session.createTextMessage(objectMapper.writeValueAsString(transactionDto));
                data.setStringProperty("_type", "id.holigo.services.common.model.TransactionDto");
            } catch (JsonProcessingException e) {
                throw new JMSException(e.getMessage());
            }
            return data;
        });
        assert received != null;
        return objectMapper.readValue(received.getBody(String.class), TransactionDto.class);
    }
}
