package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.TrainFinalFare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainFinalFareRepository extends JpaRepository<TrainFinalFare, UUID> {
}
