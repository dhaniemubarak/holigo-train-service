package id.holigo.services.holigotrainservice.services.retross;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.web.model.RetrossRequestScheduleDto;
import id.holigo.services.holigotrainservice.web.model.RetrossResponseScheduleDto;

public interface RetrossTrainService {

    RetrossResponseScheduleDto getSchedule(RetrossRequestScheduleDto retrossRequestScheduleDto) throws JsonProcessingException;
}
