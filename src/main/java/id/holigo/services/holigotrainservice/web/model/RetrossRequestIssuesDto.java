package id.holigo.services.holigotrainservice.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class RetrossRequestIssuesDto implements Serializable {
    private String action;

    private String app;

    private String rqid;

    private String mmid;

    private String notrx;
}
