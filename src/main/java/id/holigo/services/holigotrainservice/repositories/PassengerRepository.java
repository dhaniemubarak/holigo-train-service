package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PassengerRepository extends JpaRepository<Passenger, UUID> {
}
