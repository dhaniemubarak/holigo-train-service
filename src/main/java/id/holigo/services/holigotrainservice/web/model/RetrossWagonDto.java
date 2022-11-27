package id.holigo.services.holigotrainservice.web.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties
public class RetrossWagonDto implements Serializable {

    @JsonProperty("kode_wagon")
    private String wagonCode;

    @JsonProperty("no_wagon")
    private String wagonNumber;

    @JsonProperty("jml_row")
    private Integer rowAmount;

    @JsonProperty("jml_col")
    private Integer colAmount;
    //
    @JsonProperty("avl")
    private List<List<String>> availabilities;
}
