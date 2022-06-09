package id.holigo.services.holigotrainservice.repositories;

import id.holigo.services.holigotrainservice.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InquiryRepository extends JpaRepository<Inquiry, UUID> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM inquiries " +
                    "WHERE origin_station_id = :originStationId " +
                    "AND destination_station_id= :destinationStationId " +
                    "AND departure_date=:departureDate " +
                    "AND (:returnDate is null OR return_date=:returnDate) " +
                    "AND trip_type=:tripType " +
                    "AND adult_amount=:adultAmount " +
                    "AND infant_amount=:infantAmount LIMIT 1")
    Optional<Inquiry> getInquiry(
            @Param("originStationId") String originStationId,
            @Param("destinationStationId") String destinationStationId,
            @Param("departureDate") String departureDate,
            @Param("returnDate") String returnDate,
            @Param("tripType") String tripType,
            @Param("adultAmount") Integer adultAmount,
            @Param("infantAmount") Integer infantAmount);

}
