package id.holigo.services.common.model;

import id.holigo.services.holigotrainservice.web.model.SeatMapDto;
import id.holigo.services.holigotrainservice.web.model.StationDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainTransactionTripDtoForUser implements Serializable {

    private UUID id;

    private List<TrainTransactionTripPassengerDto> passengers;

    private PaymentStatusEnum paymentStatus;

    private OrderStatusEnum orderStatus;

    private String trainName;

    private String trainNumber;

    private StationDto originStation;

    private StationDto destinationStation;

    private Integer adultAmount;

    private Integer childAmount;

    private Integer infantAmount;

    private Date departureDate;

    private Time departureTime;

    private Date arrivalDate;

    private Time arrivalTime;

    private String imageUrl;

    private String trainClass;

    private String trainSubClass;

    private BigDecimal fareAmount;

    private BigDecimal adminAmount;

    private BigDecimal hpAmount;

    private BigDecimal hpcAmount;

    private String bookCode;

    private Integer segment;

    private SeatMapDto seatMap;
}
