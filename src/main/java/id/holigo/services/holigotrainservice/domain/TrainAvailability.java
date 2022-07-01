package id.holigo.services.holigotrainservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "train_availabilities")
public class TrainAvailability {

    @Id
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @Column(length = 50, columnDefinition = "varchar(50)")
    private String trainName;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String trainNumber;

    @ManyToOne
    private Station originStation;

    @ManyToOne
    private Station destinationStation;

    private Date departureDate;

    private Time departureTime;

    private Date arrivalDate;

    private Time arrivalTime;

    private String imageUrl;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String trainClass;

    @Column(length = 20, columnDefinition = "varchar(20)")
    private String trainSubClass;

    @Lob
    private String fare;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
}
