package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.common.model.PassengerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrossRequestBookDto implements Serializable {

    private String rqid;
    private String mmid;
    private String app;
    private String action;
    private String cpname;
    private String cpmail;
    private String cptlp;
    private String org;
    private String des;
    private String trip;
    private Integer adt;
    private Integer chd;
    private Integer inf;
    private String tgl_dep;
    private String selectedIdDep;
    private String tgl_ret;
    private String selectedIdRet;
    private List<PassengerDto> passengers;
    HashMap<String, Object> map = new HashMap<>();

    public Map<String, Object> build() {

        map.put("rqid", getRqid());
        map.put("mmid", getMmid());
        map.put("app", getApp());
        map.put("action", getAction());
        map.put("cpname", getCpname());
        map.put("cpmail", getCpmail());
        map.put("cptlp", getCptlp());
        map.put("org", getOrg());
        map.put("des", getDes());
        map.put("trip", getTrip());
        map.put("adt", getAdt());
        map.put("chd", getChd());
        map.put("inf", getInf());
        map.put("tgl_dep", getTgl_dep());
        map.put("selectedIDdep", getSelectedIdDep());
        if (getTrip().equals("R")) {
            map.put("tgl_ret", getTgl_ret());
            map.put("selectedIDret", getSelectedIdRet());
        }
        for (int i = 0; i < getPassengers().size(); i++) {
            System.out.println("Index -> " + i);
            PassengerDto passengerDto = getPassengers().get(i);
            switch (passengerDto.getType()) {
                case ADULT -> {
                    map.put("nmadt_" + (i + 1), passengerDto.getName());
                    map.put("hpadt_" + (i + 1), "08123456789");
                    if (passengerDto.getIdentityCard() != null) {
                        map.put("idadt_" + (i + 1), passengerDto.getIdentityCard().getIdCardNumber());
                        map.put("idtypeadt_" + (i + 1), "1");
                    }
                    if (passengerDto.getPassport() != null) {
                        map.put("idadt_" + (i + 1), passengerDto.getIdentityCard().getIdCardNumber());
                        map.put("idtypeadt_" + (i + 1), "2");
                    }
                }
                case CHILD -> {
                    map.put("nmchd_" + (i + 1), passengerDto.getName());
                    map.put("hpchd_" + (i + 1), "08123456789");
                    if (passengerDto.getIdentityCard() != null) {
                        map.put("idchd_" + (i + 1), passengerDto.getIdentityCard().getIdCardNumber());
                        map.put("idtypechd_" + (i + 1), "1");
                    }
                    if (passengerDto.getPassport() != null) {
                        map.put("idadt_" + (i + 1), passengerDto.getIdentityCard().getIdCardNumber());
                        map.put("idtypechd_" + (i + 1), "2");
                    }
                }
                case INFANT -> {
                    map.put("nminf_" + (i + 1), passengerDto.getName());
                    map.put("hpinf_" + (i + 1), "08123456789");
                    if (passengerDto.getIdentityCard() != null) {
                        map.put("idinf_" + (i + 1), passengerDto.getIdentityCard().getIdCardNumber());
                        map.put("idtypeinf_" + (i + 1), "1");
                    }
                    if (passengerDto.getPassport() != null) {
                        map.put("idinf_" + (i + 1), passengerDto.getIdentityCard().getIdCardNumber());
                        map.put("idtypeinf_" + (i + 1), "2");
                    }
                }
            }
        }
        return map;
    }
}
