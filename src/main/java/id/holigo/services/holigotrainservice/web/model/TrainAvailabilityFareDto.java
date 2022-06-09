package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainAvailabilityFareDto implements Serializable {

    private String trainClass;

    private String trainSubClass;

    private Integer seatAvailable;

    private BigDecimal hpAmount;

    private BigDecimal fareAmount;

    private BigDecimal priceAdult;

    private BigDecimal priceChild;

    private BigDecimal priceInfant;

    private String selectedId;

}
