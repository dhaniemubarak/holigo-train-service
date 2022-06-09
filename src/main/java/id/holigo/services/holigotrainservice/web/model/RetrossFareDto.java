package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetrossFareDto implements Serializable {

    @JsonProperty(value = "SubClass")
    private String trainSubClass;

    @JsonProperty(value = "Class")
    private String trainClass;

    @JsonProperty(value = "SeatAvb")
    private Integer seatAvb;

    @JsonProperty(value = "TotalFare")
    private BigDecimal fareAmount;

    @JsonProperty(value = "priceAdt")
    private BigDecimal priceAdult;

    @JsonProperty(value = "priceChd")
    private BigDecimal priceChild;

    @JsonProperty(value = "priceInf")
    private BigDecimal priceInfant;

    @JsonProperty(value = "selectedIDdep")
    private String selectedIdDep;

    @JsonProperty(value = "selectedIDret")
    private String selectedIdRet;
}
