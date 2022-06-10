package id.holigo.services.holigotrainservice.web.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RequestFinalFareDto {
    private List<TripDto> trips;
}
