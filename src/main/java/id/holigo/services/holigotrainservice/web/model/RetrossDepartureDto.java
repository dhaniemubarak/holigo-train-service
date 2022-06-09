package id.holigo.services.holigotrainservice.web.model;

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
public class RetrossDepartureDto implements Serializable {
    @JsonProperty(value = "TrainNo")
    private String trainNumber;

    @JsonProperty(value = "TrainName")
    private String trainName;

    @JsonProperty(value = "STD")
    private String std;

    @JsonProperty(value = "STA")
    private String sta;

    @JsonProperty(value = "ETD")
    private String etd;

    @JsonProperty(value = "ETA")
    private String eta;

    @JsonProperty(value = "Fares")
    List<RetrossFareDto> fares;

}
