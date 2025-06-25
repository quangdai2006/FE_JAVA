package myproject.controller;

import myproject.model.City;
import myproject.model.CurrentWeather;
import myproject.model.HourlyForecast;
import myproject.model.DailyForecast;
import myproject.model.AstronomyData;
import myproject.service.WeatherService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private static final Logger logger = LogManager.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Fetch location key for a city.
     */
    @GetMapping("/location")
    public ResponseEntity<City> getLocation(
            @RequestParam String city,
            @RequestParam(defaultValue = "VN") String countryId) {
        logger.info("Fetching location for city: {}, country: {}", city, countryId);
        try {
            City cityObj = weatherService.getLocationKey(city, countryId);
            if (cityObj == null) {
                logger.warn("City not found: {}", city);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(cityObj);
        } catch (IOException e) {
            logger.error("Error fetching location for city: {}: {}", city, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Fetch current weather data for a city without saving it.
     */
    @GetMapping("/fetch-and-save-current")
    public ResponseEntity<CurrentWeather> fetchCurrentWeather(
            @RequestParam String city,
            @RequestParam(defaultValue = "VN") String countryId) {
        logger.info("Request to fetch current weather for city: {}, country: {}", city, countryId);
        try {
            City cityObj = weatherService.getLocationKey(city, countryId);
            if (cityObj == null) {
                logger.error("No city found for name: {}, countryId: {}", city, countryId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            CurrentWeather weather = weatherService.fetchCurrentWeather(cityObj);
            logger.info("Successfully fetched current weather data for city: {}", city);
            return ResponseEntity.ok(weather);
        } catch (IOException e) {
            logger.error("Error fetching current weather data for city: {}: {}", city, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Fetch and save all weather data for a city.
     */
    @GetMapping("/fetch-and-save")
    public ResponseEntity<String> fetchAndSaveWeather(
            @RequestParam String city,
            @RequestParam(defaultValue = "VN") String countryId) {
        logger.info("Request to fetch and save weather for city: {}, country: {}", city, countryId);
        try {
            weatherService.fetchAndSaveAllWeatherData(city, countryId);
            logger.info("Successfully fetched and saved weather data for city: {}", city);
            return ResponseEntity.ok("Successfully fetched and saved weather data for city: " + city);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid city: {}: {}", city, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid city: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error fetching weather data for city: {}: {}", city, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching weather data: " + e.getMessage());
        }
    }

    /**
     * Get current weather for a city, fallback to cache if available.
     */
    @GetMapping("/current")
    public ResponseEntity<CurrentWeather> getCurrentWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String countryId,
            @RequestParam(required = false) String locationKey) {
        logger.info("Fetching current weather for city: {}, country: {}, locationKey: {}", city, countryId, locationKey);
        try {
            CurrentWeather weather;
            if (locationKey != null) {
                weather = weatherService.getCurrentWeather(locationKey);
                if (weather != null) return ResponseEntity.ok(weather);
            } else if (city != null) {
                City cityObj = weatherService.getLocationKey(city, countryId != null ? countryId : "VN");
                if (cityObj == null) {
                    logger.warn("City not found: {}", city);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                weather = weatherService.getCurrentWeather(cityObj.getLocationKey());
                if (weather != null) return ResponseEntity.ok(weather);
                weather = weatherService.fetchCurrentWeather(cityObj);
                return ResponseEntity.ok(weather);
            }
            logger.warn("No valid parameters provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IOException e) {
            logger.error("Error fetching current weather: {}", e.getMessage(), e);
            CurrentWeather errorWeather = new CurrentWeather();
            if (city != null) {
                try {
                    City cityObj = weatherService.getLocationKey(city, countryId != null ? countryId : "VN");
                    if (cityObj != null) errorWeather.setCity(cityObj);
                } catch (IOException ignored) {}
            }
            errorWeather.setWeatherText("Server error: " + e.getMessage());
            errorWeather.setTimestamp(new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorWeather);
        }
    }

    /**
     * Get hourly forecast for a city, fallback to cache if available.
     */
    @GetMapping("/hourly-forecast")
    public ResponseEntity<List<HourlyForecast>> getHourlyForecast(
            @RequestParam String city,
            @RequestParam(defaultValue = "VN") String countryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endTime) {
        logger.info("Fetching hourly forecast for city: {}, country: {}", city, countryId);
        try {
            City cityObj = weatherService.getLocationKey(city, countryId);
            if (cityObj == null) {
                logger.warn("City not found: {}", city);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Date defaultStart = startTime != null ? startTime : new Date();
            Date defaultEnd = endTime != null ? endTime : new Date(defaultStart.getTime() + 12 * 60 * 60 * 1000);
            List<HourlyForecast> forecasts = weatherService.getHourlyForecast(cityObj.getLocationKey(), defaultStart, defaultEnd);
            if (!forecasts.isEmpty()) {
                logger.info("Returning cached hourly forecast for city: {}", city);
                return ResponseEntity.ok(forecasts);
            }
            logger.info("No cached data, fetching hourly forecast for city: {}", city);
            forecasts = weatherService.fetchHourlyForecast(cityObj);
            return ResponseEntity.ok(forecasts);
        } catch (IOException e) {
            logger.error("Error fetching hourly forecast for city: {}: {}", city, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get daily forecast for a city, fallback to cache if available.
     */
    @GetMapping("/daily-forecast")
    public ResponseEntity<List<DailyForecast>> getDailyForecast(
            @RequestParam String city,
            @RequestParam(defaultValue = "VN") String countryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        logger.info("Fetching daily forecast for city: {}, country: {}", city, countryId);
        try {
            City cityObj = weatherService.getLocationKey(city, countryId);
            if (cityObj == null) {
                logger.warn("City not found: {}", city);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Date defaultStart = startDate != null ? startDate : new Date();
            Date defaultEnd = endDate != null ? endDate : new Date(defaultStart.getTime() + 5 * 24 * 60 * 60 * 1000);
            List<DailyForecast> forecasts = weatherService.getDailyForecast(cityObj.getLocationKey(), defaultStart, defaultEnd);
            if (!forecasts.isEmpty()) {
                logger.info("Returning cached daily forecast for city: {}", city);
                return ResponseEntity.ok(forecasts);
            }
            logger.info("No cached data, fetching daily forecast for city: {}", city);
            forecasts = weatherService.fetchDailyForecast(cityObj);
            return ResponseEntity.ok(forecasts);
        } catch (IOException e) {
            logger.error("Error fetching daily forecast for city: {}: {}", city, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Get astronomy data for a city, fallback to cache if available.
     */
    @GetMapping("/astronomy")
    public ResponseEntity<List<AstronomyData>> getAstronomyData(
            @RequestParam String city,
            @RequestParam(defaultValue = "VN") String countryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        logger.info("Fetching astronomy data for city: {}, country: {}", city, countryId);
        try {
            City cityObj = weatherService.getLocationKey(city, countryId);
            if (cityObj == null) {
                logger.warn("City not found: {}", city);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            Date defaultStart = startDate != null ? startDate : new Date();
            Date defaultEnd = endDate != null ? endDate : new Date(defaultStart.getTime() + 5 * 24 * 60 * 60 * 1000);
            List<AstronomyData> astronomyData = weatherService.getAstronomyData(cityObj.getLocationKey(), defaultStart, endDate);
            if (!astronomyData.isEmpty()) {
                logger.info("Returning cached astronomy data for city: {}", city);
                return ResponseEntity.ok(astronomyData);
            }
            logger.info("No cached data, fetching astronomy data for city: {}", city);
            astronomyData = weatherService.fetchAstronomyData(cityObj);
            return ResponseEntity.ok(astronomyData);
        } catch (IOException e) {
            logger.error("Error fetching astronomy data for city: {}: {}", city, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Save weather data received from client.
     */
    @PostMapping("/save")
    public ResponseEntity<String> saveWeatherData(@RequestBody CurrentWeather weather) {
        logger.info("Saving weather data for city: {}", weather.getCity().getName());
        try {
            weatherService.saveWeatherData(weather); // Giả sử WeatherService có phương thức này
            return ResponseEntity.ok("Weather data saved successfully for " + weather.getCity().getName());
        } catch (Exception e) {
            logger.error("Error saving weather data for city: {}: {}", weather.getCity().getName(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving weather data: " + e.getMessage());
        }
    }
}