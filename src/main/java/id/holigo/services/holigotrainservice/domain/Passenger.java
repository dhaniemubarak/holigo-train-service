package id.holigo.services.holigotrainservice.domain;

import id.holigo.services.common.model.PassengerTitle;
import id.holigo.services.common.model.PassengerType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PassengerType type;

    @Enumerated(EnumType.STRING)
    private PassengerTitle title;

    private String name;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    private IdentityCard identityCard;

    @OneToOne(cascade = CascadeType.ALL)
    private Passport passport;

    @OneToMany(mappedBy = "passenger")
    private Set<TrainTransactionTripPassenger> trips;

    private Integer sort;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
