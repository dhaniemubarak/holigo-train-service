package id.holigo.services.common.model;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainTransactionDtoForUser implements Serializable {
    private Long id;

    private Long userId;

    private String iconUrl;

    private UUID transactionId;

    private ContactPersonDto contactPerson;

    private TripType tripType;

    private List<TrainTransactionTripDtoForUser> trips;

    private Boolean isBookable;

    private Timestamp expiredAt;

    private PaymentStatusEnum paymentStatus;

    private OrderStatusEnum orderStatus;

    private BigDecimal fareAmount;

    private BigDecimal billAmount;

    private BigDecimal adminAmount;

    private BigDecimal discountAmount;

    private BigDecimal hpAmount;

    private BigDecimal hpcAmount;

    private String seatMapUrl;
}
