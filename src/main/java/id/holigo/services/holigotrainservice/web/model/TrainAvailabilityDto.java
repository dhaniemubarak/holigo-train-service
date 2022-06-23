package id.holigo.services.holigotrainservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainAvailabilityDto implements Serializable {

    private UUID id;

    private String trainName;

    private String trainNumber;

    private String originStationId;

    private StationDto originStation;

    private String destinationStationId;

    private StationDto destinationStation;

    private Date departureDate;

    private Time departureTime;

    private Date arrivalDate;

    private Time arrivalTime;

    private String imageUrl;

    private String trainClass;

    private String trainSubClass;

    private TrainAvailabilityFareDto fare;
}
