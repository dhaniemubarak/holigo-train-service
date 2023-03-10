package id.holigo.services.holigotrainservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainFinalFareTripDto implements Serializable {

    private String trainName;

    private String trainNumber;

    private String trainClass;

    private String trainSubClass;

    private StationDto originStation;

    private StationDto destinationStation;

    private Date departureDate;

    private Time departureTime;

    private Date arrivalDate;

    private Time arrivalTime;

    private String imageUrl;

    private BigDecimal fareAmount;

    private BigDecimal adminAmount;

    private BigDecimal hpAmount;

    private BigDecimal hpcAmount;

    private Integer segment;
}
