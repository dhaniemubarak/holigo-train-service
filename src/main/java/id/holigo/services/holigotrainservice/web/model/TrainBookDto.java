package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.common.model.ContactPersonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainBookDto implements Serializable {
    private UUID fareId;
    private ContactPersonDto contactPerson;
    private List<PassengerDto> passengers;

}
