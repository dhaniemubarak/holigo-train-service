package id.holigo.services.holigotrainservice.services.retross;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestBookDto;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestScheduleDto;
import id.holigo.services.holigotrainservice.web.model.RetrossResponseBookDto;
import id.holigo.services.holigotrainservice.web.model.RetrossResponseScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.io.IOException;

@Slf4j
@Service
public class RetrossTrainServiceImpl implements RetrossTrainService {

    @Value("${retross.credential.mmid}")
    private String RETROSS_ID;

    @Value("${retross.credential.rqid}")
    private String RETROSS_PASSKEY;

    private static final String RETROSS_APP_INFORMATION = "information";

    private static final String RETROSS_ACTION_GET_SCHEDULE = "get_schedule";

    private static final String RETROSS_ACTION_BOOK = "booking";

    private static final String RETROSS_APP_TRANSACTION = "transaction";

    private RetrossTrainServiceFeignClient retrossTrainServiceFeignClient;

    private ObjectMapper objectMapper;

    @Autowired
    public void setRetrossTrainServiceFeignClient(RetrossTrainServiceFeignClient retrossTrainServiceFeignClient) {
        this.retrossTrainServiceFeignClient = retrossTrainServiceFeignClient;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public RetrossResponseScheduleDto getSchedule(RetrossRequestScheduleDto retrossRequestScheduleDto) throws JsonProcessingException {
        retrossRequestScheduleDto.setMmid(RETROSS_ID);
        retrossRequestScheduleDto.setRqid(RETROSS_PASSKEY);
        retrossRequestScheduleDto.setApp(RETROSS_APP_INFORMATION);
        retrossRequestScheduleDto.setAction(RETROSS_ACTION_GET_SCHEDULE);
        log.info("Request body -> {}", objectMapper.writeValueAsString(retrossRequestScheduleDto));
        RetrossResponseScheduleDto retrossResponseScheduleDto;
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.getSchedule(retrossRequestScheduleDto);
        log.info("Response body -> {}", responseEntity.getBody());
        retrossResponseScheduleDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseScheduleDto.class);
        return retrossResponseScheduleDto;
    }

    @Override
    public RetrossResponseBookDto book(RetrossRequestBookDto retrossRequestBookDto) throws JsonProcessingException {
        retrossRequestBookDto.setMmid(RETROSS_ID);
        retrossRequestBookDto.setRqid(RETROSS_PASSKEY);
        retrossRequestBookDto.setApp(RETROSS_APP_TRANSACTION);
        retrossRequestBookDto.setAction(RETROSS_ACTION_BOOK);
        log.info("Request body -> {}", objectMapper.writeValueAsString(retrossRequestBookDto.build()));
        RetrossResponseBookDto retrossResponseBookDto;
//        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.book(retrossRequestBookDto.build().toString());
//        retrossResponseBookDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseBookDto.class);
        String dummy = "{\"error_code\":\"001\",\"error_msg\":\"\",\"mmid\":\"mastersip\",\"notrx\":\"KAI2206167488848\",\"timelimit\":\"2022-06-16 22:04:16\",\"PNRDep\":\"A6R5161\",\"TotalAmountDep\":\"242500\",\"DiskonDep\":\"0\",\"NTADep\":\"237500\",\"PNRRet\":\"GQJ5JI1\",\"TotalAmountRet\":\"387500\",\"DiskonRet\":\"0\",\"NTARet\":\"382500\",\"penumpang\":[{\"jns\":\"A\",\"nama\":\"Antonius Yuwana Pamuji Ha\",\"noid\":\"5171022903620002\",\"nohp\":\"081338392009\",\"seat_dep\":\"EKO-EKO-1-8A\",\"seat_ret\":\"EKS-EKS-1-7B\"}]}";
        retrossResponseBookDto = objectMapper.readValue(dummy, RetrossResponseBookDto.class);
        log.info("Response body -> {}", objectMapper.writeValueAsString(retrossResponseBookDto));
        return retrossResponseBookDto;
    }
}
