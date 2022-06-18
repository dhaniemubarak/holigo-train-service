package id.holigo.services.holigotrainservice.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainFinalFareDto implements Serializable {

    private UUID id;

    private String note;

    private Boolean isBookable;

    private BigDecimal fareAmount;

    private BigDecimal adminAmount;

    private BigDecimal hpAmount;

    private List<TrainFinalFareTripDto> trips;
}
