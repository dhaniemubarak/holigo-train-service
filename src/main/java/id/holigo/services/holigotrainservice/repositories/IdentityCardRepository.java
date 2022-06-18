package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.IdentityCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdentityCardRepository extends JpaRepository<IdentityCard, UUID> {
}
