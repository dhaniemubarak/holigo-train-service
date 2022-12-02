package id.holigo.services.holigotrainservice.services.retross;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import id.holigo.services.holigotrainservice.web.model.*;

public interface RetrossTrainService {

    RetrossResponseScheduleDto getSchedule(RetrossRequestScheduleDto retrossRequestScheduleDto, Long userId) throws JsonProcessingException;

    RetrossResponseBookDto book(RetrossRequestBookDto retrossRequestBookDto, Long userId) throws JsonProcessingException;

    RetrossResponseSeatMapDto getSeatMap(RetrossRequestSeatMapDto retrossRequestSeatMapDto, Long userId) throws JsonProcessingException;

    RetrossResponseChangeSeatDto changeSeat(RetrossRequestChangeSeatDto retrossRequestChangeSeatDto, Long userId) throws JsonProcessingException;

    void cancel(RetrossCancelDto retrossCancelDto, Long userId) throws JsonProcessingException;

    Boolean issued(TrainTransaction trainTransaction) throws JsonProcessingException;
}
