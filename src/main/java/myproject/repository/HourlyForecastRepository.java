package myproject.repository;

import myproject.model.City;
import myproject.model.HourlyForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface HourlyForecastRepository extends JpaRepository<HourlyForecast, Long> {
    List<HourlyForecast> findByCityAndForecastTimeBetweenOrderByForecastTimeAsc(City city, Date startTime, Date endTime);
    List<HourlyForecast> findByCityLocationKeyAndForecastTimeBetweenOrderByForecastTimeAsc(String locationKey, Date startTime, Date endTime);
    boolean existsByCityLocationKeyAndForecastTime(String locationKey, Date forecastTime);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO hourly_forecast (city_location_key, forecast_time, temperature, real_feel_temperature, " +
            "humidity, wind_speed, wind_gust_speed, uv_index, weather_text, visibility, ceiling, " +
            "precipitation_probability, cloud_cover, timestamp) " +
            "VALUES (:cityLocationKey, :forecastTime, :temperature, :realFeelTemperature, :humidity, :windSpeed, " +
            ":windGustSpeed, :uvIndex, :weatherText, :visibility, :ceiling, :precipitationProbability, :cloudCover, :timestamp) " +
            "ON DUPLICATE KEY UPDATE " +
            "temperature = VALUES(temperature), real_feel_temperature = VALUES(real_feel_temperature), " +
            "humidity = VALUES(humidity), wind_speed = VALUES(wind_speed), wind_gust_speed = VALUES(wind_gust_speed), " +
            "uv_index = VALUES(uv_index), weather_text = VALUES(weather_text), visibility = VALUES(visibility), " +
            "ceiling = VALUES(ceiling), precipitation_probability = VALUES(precipitation_probability), " +
            "cloud_cover = VALUES(cloud_cover), timestamp = VALUES(timestamp)",
            nativeQuery = true)
    void upsertHourlyForecast(
            String cityLocationKey,
            Date forecastTime,
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