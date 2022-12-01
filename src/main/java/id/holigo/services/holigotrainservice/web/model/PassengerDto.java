package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.common.model.PassengerTitle;
import id.holigo.services.common.model.PassengerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto implements Serializable {

    private PassengerType type;

    private PassengerTitle title;

    private String name;

    private IdentityCardDto identityCard;

    private PassportDto passport;

    private Integer sort;
}
