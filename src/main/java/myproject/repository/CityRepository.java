package myproject.repository;

import myproject.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, String> {
    Optional<City> findByNameAndCountryId(String name, String countryId);
    Optional<City> findById(String locationKey);
}