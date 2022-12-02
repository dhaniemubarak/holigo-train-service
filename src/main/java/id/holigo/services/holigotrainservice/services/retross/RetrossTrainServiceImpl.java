package id.holigo.services.holigotrainservice.services.retross;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.holigo.services.common.model.SupplierLogDto;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.services.logs.LogService;
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

    private LogService logService;

    @Autowired
    public void setLogService(LogService logService) {
        this.logService = logService;
    }

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
    public RetrossResponseScheduleDto getSchedule(RetrossRequestScheduleDto retrossRequestScheduleDto, Long userId) throws JsonProcessingException {
        retrossRequestScheduleDto.setMmid(RETROSS_ID);
        retrossRequestScheduleDto.setRqid(RETROSS_PASSKEY);
        retrossRequestScheduleDto.setApp(RETROSS_APP_INFORMATION);
        retrossRequestScheduleDto.setAction(RETROSS_ACTION_GET_SCHEDULE);
        RetrossResponseScheduleDto retrossResponseScheduleDto;
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.getSchedule(retrossRequestScheduleDto);
        retrossRequestScheduleDto.setMmid("holivers");
        retrossRequestScheduleDto.setRqid("HOLI**********************GO");
        sentLog(userId, objectMapper.writeValueAsString(retrossRequestScheduleDto), objectMapper.writeValueAsString(responseEntity));
        retrossResponseScheduleDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseScheduleDto.class);
        return retrossResponseScheduleDto;
    }

    @Override
    public RetrossResponseBookDto book(RetrossRequestBookDto retrossRequestBookDto, Long userId) throws JsonProcessingException {
        retrossRequestBookDto.setMmid(RETROSS_ID);
        retrossRequestBookDto.setRqid(RETROSS_PASSKEY);
        retrossRequestBookDto.setApp(RETROSS_APP_TRANSACTION);
        retrossRequestBookDto.setAction(RETROSS_ACTION_BOOK);
        RetrossResponseBookDto retrossResponseBookDto;
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.book(objectMapper.writeValueAsString(retrossRequestBookDto.build()));
        retrossRequestBookDto.setMmid("holivers");
        retrossRequestBookDto.setRqid("HOLI**********************GO");
        sentLog(userId, objectMapper.writeValueAsString(retrossRequestBookDto.build()), objectMapper.writeValueAsString(responseEntity));
        retrossResponseBookDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseBookDto.class);
//        String dummy = "{\"error_code\":\"000\",\"error_msg\":\"\",\"mmid\":\"mastersip\",\"notrx\":\"KAI2206167488848\",\"timelimit\":\"2022-06-16 22:04:16\",\"PNRDep\":\"A6R5161\",\"TotalAmountDep\":\"242500\",\"DiskonDep\":\"0\",\"NTADep\":\"237500\",\"PNRRet\":\"GQJ5JI1\",\"TotalAmountRet\":\"387500\",\"DiskonRet\":\"0\",\"NTARet\":\"382500\",\"penumpang\":[{\"jns\":\"A\",\"nama\":\"Mochamad Ramdhanie Mubarak\",\"noid\":\"5171022903620002\",\"nohp\":\"081338392009\",\"seat_dep\":\"EKO-EKO-1-7A\",\"seat_ret\":\"EKS-EKS-1-7B\"},{\"jns\":\"A\",\"nama\":\"Anisa Nursantika\",\"noid\":\"5171022903620002\",\"nohp\":\"081338392009\",\"seat_dep\":\"EKO-EKO-1-6A\",\"seat_ret\":\"EKS-EKS-1-6B\"},{\"jns\":\"I\",\"nama\":\"Alvaronizam Syathir Mubarak\",\"noid\":\"5171022903620009\"}]}";
//        retrossResponseBookDto = objectMapper.readValue(dummy, RetrossResponseBookDto.class);
        return retrossResponseBookDto;
    }

    @Override
    public RetrossResponseSeatMapDto getSeatMap(RetrossRequestSeatMapDto retrossRequestSeatMapDto, Long userId) throws JsonProcessingException {
        retrossRequestSeatMapDto.setMmid(RETROSS_ID);
        retrossRequestSeatMapDto.setRqid(RETROSS_PASSKEY);
        retrossRequestSeatMapDto.setApp(RETROSS_APP_INFORMATION);
        retrossRequestSeatMapDto.setAction(RETROSS_ACTION_GET_SEAT);
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.seatMap(objectMapper.writeValueAsString(retrossRequestSeatMapDto));
        retrossRequestSeatMapDto.setMmid("holivers");
        retrossRequestSeatMapDto.setRqid("HOLI**********************GO");
        sentLog(userId, objectMapper.writeValueAsString(retrossRequestSeatMapDto), objectMapper.writeValueAsString(responseEntity));
        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            return objectMapper.readValue(responseEntity.getBody(), RetrossResponseSeatMapDto.class);
        }
        return null;
    }

    @Override
    public RetrossResponseChangeSeatDto changeSeat(RetrossRequestChangeSeatDto retrossRequestChangeSeatDto, Long userId) throws JsonProcessingException {
        retrossRequestChangeSeatDto.setMmid(RETROSS_ID);
        retrossRequestChangeSeatDto.setRqid(RETROSS_PASSKEY);
        retrossRequestChangeSeatDto.setApp(RETROSS_APP_TRANSACTION);
        retrossRequestChangeSeatDto.setAction(RETROSS_ACTION_CHANGE_SEAT);
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.book(objectMapper.writeValueAsString(retrossRequestChangeSeatDto.build()));
        retrossRequestChangeSeatDto.setMmid("holivers");
        retrossRequestChangeSeatDto.setRqid("HOLI**********************GO");
        sentLog(userId, objectMapper.writeValueAsString(retrossRequestChangeSeatDto), objectMapper.writeValueAsString(responseEntity));
        return objectMapper.readValue(responseEntity.getBody(), RetrossResponseChangeSeatDto.class);
    }

    @Override
    public void cancel(RetrossCancelDto retrossCancelDto, Long userId) throws JsonProcessingException {
        retrossCancelDto.setMmid(RETROSS_ID);
        retrossCancelDto.setRqid(RETROSS_PASSKEY);
        retrossCancelDto.setApp(RETROSS_APP_TRANSACTION);
        retrossCancelDto.setAction(RETROSS_ACTION_CANCEL);
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.cancel(objectMapper.writeValueAsString(retrossCancelDto));
        retrossCancelDto.setMmid("holivers");
        retrossCancelDto.setRqid("HOLI**********************GO");
        sentLog(userId, objectMapper.writeValueAsString(retrossCancelDto), objectMapper.writeValueAsString(responseEntity));
    }

    @Override
    public Boolean issued(TrainTransaction trainTransaction) throws JsonProcessingException {
        RetrossRequestIssuesDto retrossRequestIssuesDto = RetrossRequestIssuesDto.builder()
                .action("issued")
                .app("transaction")
                .rqid(RETROSS_PASSKEY)
                .mmid(RETROSS_ID)
                .notrx(trainTransaction.getTrips().get(0).getSupplierTransactionId()).build();
        ResponseEntity<String> responseEntity = retrossTrainServiceFeignClient.issued(objectMapper.writeValueAsString(retrossRequestIssuesDto));
        retrossRequestIssuesDto.setMmid("holivers");
        retrossRequestIssuesDto.setRqid("HOLI**********************GO");
        sentLog(trainTransaction.getUserId(), objectMapper.writeValueAsString(retrossRequestIssuesDto), objectMapper.writeValueAsString(responseEntity));
        RetrossResponseIssuedDto retrossResponseIssuedDto = objectMapper.readValue(responseEntity.getBody(), RetrossResponseIssuedDto.class);
        return retrossResponseIssuedDto.getError_code().equals("000");

    }

    private void sentLog(Long userId, String request, String response) {
        SupplierLogDto supplierLogDto = SupplierLogDto.builder()
                .userId(userId)
                .supplier("retross")
                .code("TRAIN")
                .url("http://ws.retross.com/kereta/kai/")
                .build();
        supplierLogDto.setLogRequest(request);
        supplierLogDto.setLogResponse(response);
        try {
            logService.sendSupplierLog(supplierLogDto);
        } catch (Exception ignored) {

        }


    }
}
