package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.TrainTransactionTripPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

public interface TrainTransactionTripPassengerRepository extends JpaRepository<TrainTransactionTripPassenger, UUID> {
    List<TrainTransactionTripPassenger> findAllByTripIdOrderBySortAsc(UUID id);

    @Modifying
    @Query("UPDATE train_transaction_trip_passengers p SET p.seatNumber=:seatNumber where p.id=:id")
    void updateSeatPassenger(@Param("id") UUID originStationId,
                             @Param("seatNumber") String seatNumber);
}
