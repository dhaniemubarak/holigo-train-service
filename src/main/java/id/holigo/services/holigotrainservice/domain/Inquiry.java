package id.holigo.services.holigotrainservice.domain;

import id.holigo.services.common.model.TripType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "inquiries")
public class Inquiry {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;


    @Transient
    @Column(columnDefinition = "varchar(10)")
    private String originStationId;


    @Transient
    @Column(columnDefinition = "varchar(10)")
    private String destinationStationId;

    @ManyToOne
    private Station originStation;

    @ManyToOne
    private Station destinationStation;

    @Enumerated(EnumType.STRING)
    private TripType tripType;

    private Date departureDate;

    private Date returnDate;

    @Column(columnDefinition = "tinyint(1)")
    private Integer adultAmount;

    @Column(columnDefinition = "tinyint(1) default 0")
    private Integer childAmount;

    @Column(columnDefinition = "tinyint(1)")
    private Integer infantAmount;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}
