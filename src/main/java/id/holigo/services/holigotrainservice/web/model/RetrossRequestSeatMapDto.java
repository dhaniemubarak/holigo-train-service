package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetrossRequestSeatMapDto implements Serializable {

    private String rqid;

    private String mmid;

    private String app;

    private String action;

    private String org;

    private String des;

    private String trip;

    private String tgl_dep;

    private String tgl_ret;

    private Integer adt;

    private Integer chd;

    private Integer inf;

    @JsonProperty("selectedIDdep")
    private String selectedIdDep;

    @JsonProperty("selectedIDret")
    private String selectedIdRet;
}
