package id.holigo.services.holigotrainservice.web.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetrossRequestScheduleDto {

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
}
