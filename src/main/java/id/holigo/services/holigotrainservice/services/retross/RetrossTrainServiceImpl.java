package id.holigo.services.holigotrainservice.services.retross;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.holigotrainservice.web.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RetrossTrainServiceImpl implements RetrossTrainService {

    @Value("${retross.credential.mmid}")
    private String RETROSS_ID;

    @Value("${retross.credential.rqid}")
    private String RETROSS_PASSKEY;

    private static final String RETROSS_APP_INFORMATION = "information";

    private static final String RETROSS_ACTION_GET_SCHEDULE = "get_schedule";

    private static final String RETROSS_ACTION_BOOK = "booking";

    private static final String RETROSS_ACTION_CANCEL = "cancel";

    private static final String RETROSS_ACTION_GET_SEAT = "get_seat";

    private static final String RETROSS_ACTION_CHANGE_SEAT = "change_seat";


    private static final String RETROSS_APP_TRANSACTION = "transaction";

    private RetrossTrainServiceFeignClient retrossTrainServiceFeignClient;

    private final ObjectMapper objectMapper;

    @Autowired
    public void setRetrossTrainServiceFeignClient(RetrossTrainServiceFeignClient retrossTrainServiceFeignClient) {
        this.retrossTrainServiceFeignClient = retrossTrainServiceFeignClient;
    }


    @Override
    public RetrossResponseScheduleDto getSchedule(RetrossRequestScheduleDto retrossRequestScheduleDto) throws JsonProcessingException {
        retrossRequestScheduleDto.setMmid(RETROSS_ID);
        retrossRequestScheduleDto.setRqid(RETROSS_PASSKEY);
        retrossRequestScheduleDto.setApp(RETROSS_APP_INFORMATION);
        retrossRequestScheduleDto.setAction(RETROSS_ACTION_GET_SCHEDULE);
        RetrossResponseScheduleDto retrossResponseScheduleDto;
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.getSchedule(retrossRequestScheduleDto);
        retrossResponseScheduleDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseScheduleDto.class);
        return retrossResponseScheduleDto;
    }

    @Override
    public RetrossResponseBookDto book(RetrossRequestBookDto retrossRequestBookDto) throws JsonProcessingException {
        retrossRequestBookDto.setMmid(RETROSS_ID);
        retrossRequestBookDto.setRqid(RETROSS_PASSKEY);
        retrossRequestBookDto.setApp(RETROSS_APP_TRANSACTION);
        retrossRequestBookDto.setAction(RETROSS_ACTION_BOOK);
        RetrossResponseBookDto retrossResponseBookDto;
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.book(objectMapper.writeValueAsString(retrossRequestBookDto.build()));
        retrossResponseBookDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseBookDto.class);
//        String dummy = "{\"error_code\":\"000\",\"error_msg\":\"\",\"mmid\":\"mastersip\",\"notrx\":\"KAI2206167488848\",\"timelimit\":\"2022-06-16 22:04:16\",\"PNRDep\":\"A6R5161\",\"TotalAmountDep\":\"242500\",\"DiskonDep\":\"0\",\"NTADep\":\"237500\",\"PNRRet\":\"GQJ5JI1\",\"TotalAmountRet\":\"387500\",\"DiskonRet\":\"0\",\"NTARet\":\"382500\",\"penumpang\":[{\"jns\":\"A\",\"nama\":\"Antonius Yuwana Pamuji Ha\",\"noid\":\"5171022903620002\",\"nohp\":\"081338392009\",\"seat_dep\":\"EKO-EKO-1-8A\",\"seat_ret\":\"EKS-EKS-1-7B\"}]}";
//        retrossResponseBookDto = objectMapper.readValue(dummy, RetrossResponseBookDto.class);
        return retrossResponseBookDto;
    }

    @Override
    public RetrossResponseSeatMapDto getSeatMap(RetrossRequestSeatMapDto retrossRequestSeatMapDto) throws JsonProcessingException {
        retrossRequestSeatMapDto.setMmid(RETROSS_ID);
        retrossRequestSeatMapDto.setRqid(RETROSS_PASSKEY);
        retrossRequestSeatMapDto.setApp(RETROSS_APP_INFORMATION);
        retrossRequestSeatMapDto.setAction(RETROSS_ACTION_GET_SEAT);
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.seatMap(objectMapper.writeValueAsString(retrossRequestSeatMapDto));
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return objectMapper.readValue(responseEntity.getBody(), RetrossResponseSeatMapDto.class);
        }
        return null;
    }

    @Override
    public RetrossResponseChangeSeatDto changeSeat(RetrossRequestChangeSeatDto retrossRequestChangeSeatDto) throws JsonProcessingException {
        retrossRequestChangeSeatDto.setMmid(RETROSS_ID);
        retrossRequestChangeSeatDto.setRqid(RETROSS_PASSKEY);
        retrossRequestChangeSeatDto.setApp(RETROSS_APP_TRANSACTION);
        retrossRequestChangeSeatDto.setAction(RETROSS_ACTION_CHANGE_SEAT);
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.book(objectMapper.writeValueAsString(retrossRequestChangeSeatDto.build()));
        return objectMapper.readValue(responseEntity.getBody(), RetrossResponseChangeSeatDto.class);
    }

    @Override
    public void cancel(RetrossCancelDto retrossCancelDto) throws JsonProcessingException {
        retrossCancelDto.setMmid(RETROSS_ID);
        retrossCancelDto.setRqid(RETROSS_PASSKEY);
        retrossCancelDto.setApp(RETROSS_APP_TRANSACTION);
        retrossCancelDto.setAction(RETROSS_ACTION_CANCEL);
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.cancel(objectMapper.writeValueAsString(retrossCancelDto));
        objectMapper.readValue(responseEntity.getBody(), RetrossCancelDto.class);
    }
}
