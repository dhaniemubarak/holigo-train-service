package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.TrainTransactionTrip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainTransactionTripRepository extends JpaRepository<TrainTransactionTrip, UUID> {
}
