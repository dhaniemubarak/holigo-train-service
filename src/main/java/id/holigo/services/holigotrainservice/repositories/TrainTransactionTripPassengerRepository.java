package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.TrainTransactionTripPassenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrainTransactionTripPassengerRepository extends JpaRepository<TrainTransactionTripPassenger, UUID> {
    List<TrainTransactionTripPassenger> findAllByTripId(UUID id);
}
