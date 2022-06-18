package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RetrossPassengerDto implements Serializable {
    private String jns;
    private String nama;
    private String noid;
    private String nohp;
    private String seat_dep;
    private String seat_ret;
}
