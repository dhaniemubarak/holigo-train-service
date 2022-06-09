package id.holigo.services.holigotrainservice.web.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import id.holigo.services.common.model.FareDetailDto;
import id.holigo.services.common.model.FareDto;
import id.holigo.services.holigotrainservice.services.fare.FareService;
import id.holigo.services.holigotrainservice.web.model.RetrossFareDto;
import id.holigo.services.holigotrainservice.web.model.TrainAvailabilityFareDto;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class TrainAvailabilityFareMapperDecorator implements TrainAvailabilityFareMapper {

    @Autowired
    private FareService fareService;

    private TrainAvailabilityFareMapper trainAvailabilityFareMapper;

    @Autowired
    public void setTrainAvailabilityFareMapper(TrainAvailabilityFareMapper trainAvailabilityFareMapper) {
        this.trainAvailabilityFareMapper = trainAvailabilityFareMapper;
    }

    public TrainAvailabilityFareDto retrossFareDtoToTrainAvailabilityFareDto(RetrossFareDto retrossFareDto, Long userId) {
        TrainAvailabilityFareDto trainAvailabilityFareDto = trainAvailabilityFareMapper.retrossFareDtoToTrainAvailabilityFareDto(retrossFareDto, userId);
        trainAvailabilityFareDto.setFareAmount(trainAvailabilityFareDto.getFareAmount().setScale(2, RoundingMode.UP));
        trainAvailabilityFareDto.setPriceAdult(trainAvailabilityFareDto.getPriceAdult().setScale(2, RoundingMode.UP));
        trainAvailabilityFareDto.setPriceChild(trainAvailabilityFareDto.getPriceChild().setScale(2, RoundingMode.UP));
        trainAvailabilityFareDto.setPriceInfant(trainAvailabilityFareDto.getPriceInfant().setScale(2, RoundingMode.UP));
        FareDetailDto fareDetailDto = FareDetailDto.builder()
                .userId(userId)
                .productId(3)
                .nraAmount(BigDecimal.valueOf(5000.00))
                .ntaAmount(BigDecimal.valueOf(0.00)).build();
        FareDto fareDto;
        try {
            fareDto = fareService.getFareDetail(fareDetailDto);
        } catch (JMSException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        assert fareDto != null;
        trainAvailabilityFareDto.setHpAmount(fareDto.getHpAmount());
        if (retrossFareDto.getSelectedIdDep() != null) {
            trainAvailabilityFareDto.setSelectedId(retrossFareDto.getSelectedIdDep());
        }
        if (retrossFareDto.getSelectedIdRet() != null) {
            trainAvailabilityFareDto.setSelectedId(retrossFareDto.getSelectedIdRet());
        }
        return trainAvailabilityFareDto;
    }
}
