package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties
public class RetrossSeatMapDto implements Serializable {

    @JsonProperty("TrainNo")
    private String trainNumber;

    @JsonProperty("SubClass")
    private String subClass;

    @JsonProperty("seat_map")
    private List<RetrossWagonDto> seatMap;
}
