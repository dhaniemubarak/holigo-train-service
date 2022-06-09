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
public class RetrossScheduleDto implements Serializable {

    @JsonProperty(value = "departure")
    List<RetrossDepartureDto> departures;

    @JsonProperty(value = "return")
    List<RetrossDepartureDto> returns;
}
