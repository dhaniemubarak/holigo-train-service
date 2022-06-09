package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.common.model.TripType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto implements Serializable {

    private UUID id;

    private Long userId;

    private String originStationId;

    private String destinationStationId;

    private TripType tripType;

    private Date departureDate;

    private Date returnDate;

    private Integer adultAmount;

    private Integer infantAmount;
}
