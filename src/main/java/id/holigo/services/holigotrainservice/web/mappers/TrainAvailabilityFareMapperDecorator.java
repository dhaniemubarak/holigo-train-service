package id.holigo.services.holigotrainservice.web.mappers;

import id.holigo.services.common.model.FareDto;
import id.holigo.services.holigotrainservice.components.Fare;
import id.holigo.services.holigotrainservice.web.model.RetrossFareDto;
import id.holigo.services.holigotrainservice.web.model.TrainAvailabilityFareDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.RoundingMode;

public abstract class TrainAvailabilityFareMapperDecorator implements TrainAvailabilityFareMapper {

    @Autowired
    private Fare fare;

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
        FareDto fareDto = fare.getFare(userId);
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
