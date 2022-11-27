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
@JsonIgnoreProperties
public class RetrossResponseSeatMapDto implements Serializable {

    private String error_code;
    private String error_msg;
    private String mmid;
    private String org;
    private String des;
    private String trip;
    private SeatDto seat;
}
