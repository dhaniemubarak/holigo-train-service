package id.holigo.services.holigotrainservice.web.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RetrossCancelDto implements Serializable {
    private String action;

    private String app;

    private String rqid;

    private String mmid;

    private String notrx;
}
