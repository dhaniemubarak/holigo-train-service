package id.holigo.services.holigotrainservice.web.model;

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
public class ListAvailabilityDto implements Serializable {

    private InquiryDto inquiry;

    private List<TrainAvailabilityDto> departures;

    private List<TrainAvailabilityDto> returns;
}
