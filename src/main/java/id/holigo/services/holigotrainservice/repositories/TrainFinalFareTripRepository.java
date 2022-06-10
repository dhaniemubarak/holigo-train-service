package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.TrainFinalFareTrip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainFinalFareTripRepository extends JpaRepository<TrainFinalFareTrip, UUID> {
}
