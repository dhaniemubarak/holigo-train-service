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
public class RetrossResponseScheduleDto implements Serializable {
    private String error_code;
    private String error_msg;
    private String org;
    private String des;
    private String trip;
    private String tgl_dep;
    private String tgl_ret;
    private RetrossScheduleDto schedule;

}
