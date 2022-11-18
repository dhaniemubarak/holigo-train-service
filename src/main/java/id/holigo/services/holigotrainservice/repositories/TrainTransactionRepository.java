package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.TrainTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrainTransactionRepository extends JpaRepository<TrainTransaction, Long> {
}
