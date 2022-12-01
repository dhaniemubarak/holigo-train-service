package id.holigo.services.holigotrainservice.domain;

import id.holigo.services.common.model.OrderStatusEnum;
import id.holigo.services.common.model.PaymentStatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

@Getter
@Setter
@Entity(name = "train_transaction_trips")
public class TrainTransactionTrip {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @ManyToOne
    private TrainTransaction transaction;

    @OneToMany(mappedBy = "trip")
    @OrderBy("sort")
    private List<TrainTransactionTripPassenger> passengers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum paymentStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatus;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String trainName;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String trainNumber;

    @ManyToOne
    private Station originStation;

    @ManyToOne
    private Station destinationStation;

    @Column(length = 2, columnDefinition = "tinyint(2) default 1", nullable = false)
    private Integer adultAmount;

    @Column(length = 2, columnDefinition = "tinyint(2) default 0", nullable = false)
    private Integer childAmount;

    @Column(length = 2, columnDefinition = "tinyint(2) default 0", nullable = false)
    private Integer infantAmount;

    private Date departureDate;

    private Time departureTime;

    private Date arrivalDate;

    private Time arrivalTime;

    private String imageUrl;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String trainClass;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String trainSubClass;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal fareAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal adminAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal ntaAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal nraAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cpAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal mpAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal ipAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal hpAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal hvAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal ipcAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal hpcAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prcAmount;

    @Column(columnDefinition = "decimal(10,2) default 0")
    private BigDecimal lossAmount;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String supplierId;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String supplierSelectedId;


    @Column(length = 20, columnDefinition = "varchar(20)")
    private String bookCode;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    private Integer segment;

    public void setPassengers(List<TrainTransactionTripPassenger> passengers) {
        this.passengers = passengers;
    }
}
