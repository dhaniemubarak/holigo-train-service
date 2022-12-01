package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.holigotrainservice.domain.Passenger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainTransactionTripPassengerDto implements Serializable {

    private UUID id;

    private PassengerDto passenger;

    private String seatNumber;

    private Integer sort;

}
