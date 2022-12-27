package id.holigo.services.holigotrainservice.web.model;

import id.holigo.services.common.model.PassengerType;
import id.holigo.services.common.model.TrainTransactionTripDtoForUser;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetrossRequestChangeSeatDto implements Serializable {

    /**
     * {
     * "rqid":"sSm4ajndIdanf2k274hKSNjshfjhqkej1nRTt",
     * "mmid":"mastersip",
     * "app":"transaction",
     * "action":"change_seat",
     * "notrx":"KAI2211273768913",
     * "seatadt_1":"EKO-EKO-1-4E,EKO-EKO-1-4D",
     * "seatadt_2":"EKO-EKO-1-4D,EKO-EKO-1-4E",
     * "seatadt_3":"EKO-EKO-1-5E,EKO-EKO-1-5D",
     * "seatadt_4":"EKO-EKO-1-5D,EKO-EKO-1-5E"
     * }
     */
    private String rqid;
    private String mmid;
    private String app;
    private String action;
    private String notrx;
    private List<TrainTransactionTripDtoForUser> trips = new ArrayList<>();
    HashMap<String, Object> map = new HashMap<>();

    public Map<String, Object> build() {

        map.put("rqid", getRqid());
        map.put("mmid", getMmid());
        map.put("app", getApp());
        map.put("action", getAction());
        map.put("notrx", getNotrx());
        AtomicInteger indexTrip = new AtomicInteger();
        getTrips().forEach(trainTransactionTripDtoForUser -> {
            AtomicInteger passengerIndex = new AtomicInteger(1);
            trainTransactionTripDtoForUser.getPassengers().forEach(trainTransactionTripPassengerDto -> {
                if (trainTransactionTripPassengerDto.getPassenger().getType().equals(PassengerType.ADULT)) {
                    if (indexTrip.get() == 0) {
                        map.put("seatadt_" + passengerIndex, trainTransactionTripPassengerDto.getSeatNumber());
                    } else {
                        map.put("seatadt_" + passengerIndex, map.get("seatadt_" + passengerIndex) + "," + trainTransactionTripPassengerDto.getSeatNumber());
                    }
                    passengerIndex.getAndIncrement();
                }

            });
            indexTrip.getAndIncrement();
        });
        return map;
    }
}
