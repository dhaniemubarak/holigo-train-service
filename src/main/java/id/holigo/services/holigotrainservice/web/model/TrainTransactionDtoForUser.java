package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.common.model.PaymentStatusEnum;
import id.holigo.services.common.model.TripType;
import id.holigo.services.holigotrainservice.domain.ContactPerson;
import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    private BigDecimal adminAmount;

    private BigDecimal discountAmount;

    private BigDecimal hpAmount;

    private BigDecimal hpcAmount;
}
