package id.holigo.services.holigotrainservice.services.retross;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.web.model.*;

public interface RetrossTrainService {

    RetrossResponseScheduleDto getSchedule(RetrossRequestScheduleDto retrossRequestScheduleDto) throws JsonProcessingException;

    RetrossResponseBookDto book(RetrossRequestBookDto retrossRequestBookDto) throws JsonProcessingException;

    RetrossResponseSeatMapDto getSeatMap(RetrossRequestSeatMapDto retrossRequestSeatMapDto) throws JsonProcessingException;

    RetrossResponseChangeSeatDto changeSeat(RetrossRequestChangeSeatDto retrossRequestChangeSeatDto) throws JsonProcessingException;

    void cancel(RetrossCancelDto retrossCancelDto) throws JsonProcessingException;
}
