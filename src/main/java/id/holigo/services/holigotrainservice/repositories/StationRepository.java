package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, String> {
}
