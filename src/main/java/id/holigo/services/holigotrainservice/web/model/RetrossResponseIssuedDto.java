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
public class RetrossResponseIssuedDto implements Serializable {
    private String error_code;
    private String error_msg;
    private String mmid;

    @JsonProperty("notrx")
    private String supplierId;

    @JsonProperty("PNRDep")
    private String bookCodeDeparture;

    @JsonProperty("PNRRet")
    private String bookCodeReturn;


    @JsonProperty("TotalAmountDep")
    private BigDecimal fareAmountDeparture;

    @JsonProperty("NTADep")
    private BigDecimal ntaDeparture;

    @JsonProperty("NTARet")
    private BigDecimal ntaReturn;

    @JsonProperty("TotalAmountRet")
    private BigDecimal fareAmountReturn;
}
