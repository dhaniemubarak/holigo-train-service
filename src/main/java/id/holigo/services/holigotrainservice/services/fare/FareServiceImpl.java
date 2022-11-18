package id.holigo.services.holigotrainservice.services.fare;

import id.holigo.services.common.model.FareDetailDto;
import id.holigo.services.common.model.FareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FareServiceImpl implements FareService {

    private final FareServiceFeignClient fareServiceFeignClient;

    @Override
    public FareDto getFareDetail(FareDetailDto fareDetailDto) {
        ResponseEntity<FareDto> responseEntity = fareServiceFeignClient.getFare(
                fareDetailDto.getUserId(),
                fareDetailDto.getProductId(),
                fareDetailDto.getNraAmount(),
                fareDetailDto.getNtaAmount());
        return responseEntity.getBody();
    }
}
