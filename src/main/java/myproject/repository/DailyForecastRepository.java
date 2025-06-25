package myproject.repository;

import myproject.model.City;
import myproject.model.DailyForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DailyForecastRepository extends JpaRepository<DailyForecast, Long> {
    List<DailyForecast> findByCityAndForecastDateBetweenOrderByForecastDateAsc(City city, Date startDate, Date endDate);
    List<DailyForecast> findByCityLocationKeyAndForecastDateBetweenOrderByForecastDateAsc(String locationKey, Date startDate, Date endDate);
}