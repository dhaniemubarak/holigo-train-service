package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties
public class WagonDto implements Serializable {
    private String wagonCode;
    private String wagonNumber;
    private Integer rowAmount;
    private Integer colAmount;
    private List<List<String>> availabilities;
}
