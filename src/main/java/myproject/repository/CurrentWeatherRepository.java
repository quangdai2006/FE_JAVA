package myproject.repository;

import myproject.model.City;
import myproject.model.CurrentWeather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface CurrentWeatherRepository extends JpaRepository<CurrentWeather, Long> {
    Optional<CurrentWeather> findTopByCityOrderByTimestampDesc(City city);
    Optional<CurrentWeather> findTopByCityLocationKeyOrderByTimestampDesc(String locationKey);
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO current_weather (city_location_key, temperature, real_feel_temperature, " +
            "humidity, wind_speed, wind_gust_speed, uv_index, weather_text, visibility, ceiling, " +
            "precipitation_probability, cloud_cover, timestamp) " +
            "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13) " +
            "ON DUPLICATE KEY UPDATE " +
            "temperature = VALUES(temperature), real_feel_temperature = VALUES(real_feel_temperature), " +
            "humidity = VALUES(humidity), wind_speed = VALUES(wind_speed), wind_gust_speed = VALUES(wind_gust_speed), " +
            "uv_index = VALUES(uv_index), weather_text = VALUES(weather_text), visibility = VALUES(visibility), " +
            "ceiling = VALUES(ceiling), precipitation_probability = VALUES(precipitation_probability), " +
            "cloud_cover = VALUES(cloud_cover), timestamp = VALUES(timestamp)",
            nativeQuery = true)
    void upsertCurrentWeather(
            String cityLocationKey,
            Double temperature,
            Double realFeelTemperature,
            Double humidity,
            Double windSpeed,
            Double windGustSpeed,
            Integer uvIndex,
            String weatherText,
            Double visibility,
            Double ceiling,
            Double precipitationProbability,
            Double cloudCover,
            Date timestamp
    );
}