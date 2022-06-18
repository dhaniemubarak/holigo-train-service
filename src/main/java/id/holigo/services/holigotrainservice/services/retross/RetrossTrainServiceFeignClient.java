package id.holigo.services.holigotrainservice.services.retross;


import id.holigo.services.holigotrainservice.web.model.RetrossRequestScheduleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "retross-train", url = "http://ws.retross.com")
public interface RetrossTrainServiceFeignClient {

    String GET_SCHEDULE = "/kereta/kai/";

    @RequestMapping(method = RequestMethod.POST, value = GET_SCHEDULE)
    ResponseEntity<String> getSchedule(@RequestBody RetrossRequestScheduleDto retrossRequestScheduleDto);

    @RequestMapping(method = RequestMethod.POST, value = GET_SCHEDULE)
    ResponseEntity<String> book(@RequestBody String build);
}
