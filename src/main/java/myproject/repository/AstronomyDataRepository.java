package myproject.repository;

import myproject.model.City;
import myproject.model.AstronomyData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AstronomyDataRepository extends JpaRepository<AstronomyData, Long> {
    List<AstronomyData> findByCityAndForecastDateBetweenOrderByForecastDateAsc(City city, Date startDate, Date endDate);
    List<AstronomyData> findByCityLocationKeyAndForecastDateBetweenOrderByForecastDateAsc(String locationKey, Date startDate, Date endDate);
}