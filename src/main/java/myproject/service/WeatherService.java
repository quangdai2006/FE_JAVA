package myproject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import myproject.model.*;
import myproject.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherService {
    private static final Logger logger = LogManager.getLogger(WeatherService.class);
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CityRepository cityRepository;
    private final CurrentWeatherRepository currentWeatherRepository;
    private final HourlyForecastRepository hourlyForecastRepository;
    private final DailyForecastRepository dailyForecastRepository;
    private final AstronomyDataRepository astronomyDataRepository;

    @Value("${accuweather.api.key}")
    private String API_KEY;

    private final String LOCATIONS_API = "http://dataservice.accuweather.com/locations/v1/cities/search";
    private final String CURRENT_CONDITIONS_API = "http://dataservice.accuweather.com/currentconditions/v1/";
    private final String HOURLY_FORECAST_API = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/";
    private final String DAILY_FORECAST_API = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/";

    @Autowired
    public WeatherService(RestTemplate restTemplate, ObjectMapper objectMapper,
                          CityRepository cityRepository,
                          CurrentWeatherRepository currentWeatherRepository,
                          HourlyForecastRepository hourlyForecastRepository,
                          DailyForecastRepository dailyForecastRepository,
                          AstronomyDataRepository astronomyDataRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.cityRepository = cityRepository;
        this.currentWeatherRepository = currentWeatherRepository;
        this.hourlyForecastRepository = hourlyForecastRepository;
        this.dailyForecastRepository = dailyForecastRepository;
        this.astronomyDataRepository = astronomyDataRepository;
    }

    @Transactional
    public City getLocationKey(String cityName, String countryId) throws IOException {
        logger.info("Fetching city: {} in country: {}", cityName, countryId);
        City city = cityRepository.findByNameAndCountryId(cityName, countryId).orElse(null);
        if (city != null) {
            logger.debug("Found city in database: {} - {}", cityName, city.getLocationKey());
            return city;
        }

        String encodedCity = URLEncoder.encode(cityName, StandardCharsets.UTF_8.toString());
        String locationUrl = UriComponentsBuilder.fromHttpUrl(LOCATIONS_API)
                .queryParam("q", "{city}")
                .queryParam("apikey", API_KEY)
                .queryParam("language", "en-us")
                .buildAndExpand(encodedCity)
                .toUriString();
        logger.debug("Calling location API: {}", locationUrl);

        ResponseEntity<String> response = executeWithRetry(() -> restTemplate.getForEntity(locationUrl, String.class));
        if (response.getStatusCodeValue() != 200) {
            logger.error("API error for location: HTTP {}", response.getStatusCodeValue());
            throw new IOException("API error for location: " + response.getStatusCode());
        }

        JsonNode jsonArray = objectMapper.readTree(response.getBody());
        if (jsonArray.isEmpty() || !jsonArray.get(0).has("Key")) {
            logger.warn("No location found for city: {}", cityName);
            String nonDiacriticCity = removeDiacritics(cityName);
            if (!nonDiacriticCity.equals(cityName)) {
                logger.info("Retrying with non-diacritic city name: {}", nonDiacriticCity);
                return getLocationKey(nonDiacriticCity, countryId);
            }
            return null;
        }

        JsonNode node = jsonArray.get(0);
        String weatherKey = node.get("IsAlias").asBoolean() && node.has("ParentCity")
                ? node.get("ParentCity").get("Key").asText()
                : node.get("Key").asText();

        city = new City(weatherKey, node.get("LocalizedName").asText(), node.get("Country").get("ID").asText(),
                node.get("AdministrativeArea").get("ID").asText(), node.get("AdministrativeArea").get("LocalizedName").asText());
        synchronized (weatherKey.intern()) {
            Optional<City> existingCity = cityRepository.findById(weatherKey);
            if (!existingCity.isPresent()) {
                city = cityRepository.save(city);
                logger.info("Saved new city: {} - {}", cityName, weatherKey);
            } else {
                city = existingCity.get();
                logger.debug("City already exists: {} - {}", cityName, weatherKey);
            }
        }
        return city;
    }

    public City getCityByLocationKey(String locationKey) {
        logger.info("Fetching city by locationKey: {}", locationKey);
        Optional<City> cityOpt = cityRepository.findById(locationKey);
        if (cityOpt.isPresent()) {
            logger.debug("Found city in database: {}", locationKey);
            return cityOpt.get();
        }
        logger.warn("City not found for locationKey: {}", locationKey);
        return null;
    }

    public CurrentWeather fetchCurrentWeather(City city) throws IOException {
        logger.info("Fetching current weather for city: {}", city.getName());
        String weatherUrl = UriComponentsBuilder.fromHttpUrl(CURRENT_CONDITIONS_API + city.getLocationKey())
                .queryParam("apikey", API_KEY)
                .queryParam("details", true)
                .queryParam("language", "en-us")
                .toUriString();
        logger.debug("Calling current weather API: {}", weatherUrl);

        ResponseEntity<String> response = executeWithRetry(() -> restTemplate.getForEntity(weatherUrl, String.class));
        if (response.getStatusCodeValue() != 200) {
            logger.error("API error for current weather: HTTP {}", response.getStatusCodeValue());
            throw new IOException("API error for current weather: " + response.getStatusCode());
        }

        JsonNode node = objectMapper.readTree(response.getBody()).get(0);
        CurrentWeather weather = new CurrentWeather();
        weather.setCity(city);
        weather.setTemperature(node.get("Temperature").get("Metric").get("Value").asDouble());
        weather.setRealFeelTemperature(node.get("RealFeelTemperature").get("Metric").get("Value").asDouble());
        weather.setHumidity(node.get("RelativeHumidity").asDouble());
        weather.setWindSpeed(node.get("Wind").get("Speed").get("Metric").get("Value").asDouble());
        weather.setWindGustSpeed(node.get("WindGust").get("Speed").get("Metric").get("Value").asDouble());
        weather.setUvIndex(node.get("UVIndex").asInt());
        weather.setWeatherText(node.get("WeatherText").asText());
        weather.setVisibility(node.get("Visibility").get("Metric").get("Value").asDouble());
        weather.setCeiling(node.get("Ceiling").get("Metric").get("Value").asDouble());
        weather.setPrecipitationProbability(node.has("PrecipitationProbability") ? node.get("PrecipitationProbability").asDouble() : 0.0);
        weather.setCloudCover(node.get("CloudCover").asDouble());
        weather.setTimestamp(new Date());

        // Lưu dữ liệu bất đồng bộ trong giao dịch riêng
        try {
            saveCurrentWeatherData(city, weather);
        } catch (Exception e) {
            logger.error("Failed to save current weather for city: {}: {}", city.getName(), e.getMessage(), e);
        }

        return weather;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCurrentWeatherData(City city, CurrentWeather weather) {
        synchronized (city.getLocationKey().intern()) {
            try {
                Optional<City> existingCity = cityRepository.findById(city.getLocationKey());
                if (!existingCity.isPresent()) {
                    cityRepository.save(city);
                    logger.info("Saved new city: {}", city.getName());
                }
                currentWeatherRepository.upsertCurrentWeather(
                        city.getLocationKey(),
                        weather.getTemperature(),
                        weather.getRealFeelTemperature(),
                        weather.getHumidity(),
                        weather.getWindSpeed(),
                        weather.getWindGustSpeed(),
                        weather.getUvIndex(),
                        weather.getWeatherText(),
                        weather.getVisibility(),
                        weather.getCeiling(),
                        weather.getPrecipitationProbability(),
                        weather.getCloudCover(),
                        weather.getTimestamp()
                );
                logger.info("Upserted current weather for city: {}", city.getName());
            } catch (Exception e) {
                logger.error("Error upserting current weather for city: {}: {}", city.getName(), e.getMessage(), e);
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
                }
            }
        }
    }

    public CurrentWeather fetchCurrentWeather(String cityName, String countryId) throws IOException {
        logger.info("Fetching current weather for city: {}, country: {}", cityName, countryId);
        City city = getLocationKey(cityName, countryId);
        if (city == null) {
            logger.error("No city found for name: {}, countryId: {}", cityName, countryId);
            throw new IllegalArgumentException("City not found: " + cityName);
        }
        return fetchCurrentWeather(city);
    }

    public void saveWeatherData(CurrentWeather weather) {
        logger.info("Saving weather data for city: {}", weather.getCity().getName());
        synchronized (weather.getCity().getLocationKey().intern()) {
            try {
                Optional<City> existingCity = cityRepository.findById(weather.getCity().getLocationKey());
                if (!existingCity.isPresent()) {
                    cityRepository.save(weather.getCity());
                    logger.info("Saved new city: {}", weather.getCity().getName());
                }
                currentWeatherRepository.save(weather);
                logger.info("Successfully saved weather data for city: {}", weather.getCity().getName());
            } catch (Exception e) {
                logger.error("Error saving weather data for city: {}: {}", weather.getCity().getName(), e.getMessage(), e);
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
                }
            }
        }
    }

    public List<HourlyForecast> fetchHourlyForecast(City city) throws IOException {
        logger.info("Fetching 12-hour forecast for city: {}", city.getName());
        String forecastUrl = UriComponentsBuilder.fromHttpUrl(HOURLY_FORECAST_API + city.getLocationKey())
                .queryParam("apikey", API_KEY)
                .queryParam("details", true)
                .queryParam("metric", true)
                .queryParam("language", "en-us")
                .toUriString();
        logger.debug("Calling hourly forecast API: {}", forecastUrl);

        ResponseEntity<String> response = executeWithRetry(() -> restTemplate.getForEntity(forecastUrl, String.class));
        if (response.getStatusCodeValue() != 200) {
            logger.error("API error for hourly forecast: HTTP {}", response.getStatusCodeValue());
            throw new IOException("API error for hourly forecast: " + response.getStatusCode());
        }

        JsonNode jsonArray = objectMapper.readTree(response.getBody());
        List<HourlyForecast> forecasts = new ArrayList<>();
        Date now = new Date();

        for (JsonNode node : jsonArray) {
            HourlyForecast forecast = new HourlyForecast();
            forecast.setCity(city);
            forecast.setTemperature(node.get("Temperature").get("Value").asDouble());
            forecast.setRealFeelTemperature(node.get("RealFeelTemperature").get("Value").asDouble());
            forecast.setHumidity(node.get("RelativeHumidity").asDouble());
            forecast.setWindSpeed(node.get("Wind").get("Speed").get("Value").asDouble());
            forecast.setWindGustSpeed(node.get("WindGust").get("Speed").get("Value").asDouble());
            forecast.setUvIndex(node.get("UVIndex").asInt());
            forecast.setWeatherText(node.get("IconPhrase").asText());
            forecast.setVisibility(node.get("Visibility").get("Value").asDouble());
            forecast.setCeiling(node.get("Ceiling").get("Value").asDouble());
            forecast.setPrecipitationProbability(node.get("PrecipitationProbability").asDouble());
            forecast.setCloudCover(node.get("CloudCover").asDouble());
            forecast.setForecastTime(new Date(node.get("EpochDateTime").asLong() * 1000));
            forecast.setTimestamp(now);
            forecasts.add(forecast);
        }

        // Lưu dữ liệu trong giao dịch riêng
        try {
            saveHourlyForecastData(city, forecasts);
        } catch (Exception e) {
            logger.error("Failed to save hourly forecasts for city: {}: {}", city.getName(), e.getMessage(), e);
        }

        logger.info("Processed {} hourly forecasts for city: {}", forecasts.size(), city.getName());
        return forecasts;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveHourlyForecastData(City city, List<HourlyForecast> forecasts) {
        synchronized (city.getLocationKey().intern()) {
            try {
                Optional<City> existingCity = cityRepository.findById(city.getLocationKey());
                if (!existingCity.isPresent()) {
                    cityRepository.save(city);
                    logger.info("Saved new city: {}", city.getName());
                }
                for (HourlyForecast forecast : forecasts) {
                    hourlyForecastRepository.upsertHourlyForecast(
                            city.getLocationKey(),
                            forecast.getForecastTime(),
                            forecast.getTemperature(),
                            forecast.getRealFeelTemperature(),
                            forecast.getHumidity(),
                            forecast.getWindSpeed(),
                            forecast.getWindGustSpeed(),
                            forecast.getUvIndex(),
                            forecast.getWeatherText(),
                            forecast.getVisibility(),
                            forecast.getCeiling(),
                            forecast.getPrecipitationProbability(),
                            forecast.getCloudCover(),
                            forecast.getTimestamp()
                    );
                    logger.debug("Upserted forecast for city: {}, time: {}", city.getName(), forecast.getForecastTime());
                }
            } catch (Exception e) {
                logger.error("Error upserting hourly forecasts for city: {}: {}", city.getName(), e.getMessage(), e);
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
                }
            }
        }
    }

    public List<DailyForecast> fetchDailyForecast(City city) throws IOException {
        logger.info("Fetching 5-day forecast for city: {}", city.getName());
        String forecastUrl = UriComponentsBuilder.fromHttpUrl(DAILY_FORECAST_API + city.getLocationKey())
                .queryParam("apikey", API_KEY)
                .queryParam("details", true)
                .queryParam("metric", true)
                .queryParam("language", "en-us")
                .toUriString();
        logger.debug("Calling daily forecast API: {}", forecastUrl);

        ResponseEntity<String> response = executeWithRetry(() -> restTemplate.getForEntity(forecastUrl, String.class));
        if (response.getStatusCodeValue() != 200) {
            logger.error("API error for daily forecast: HTTP {}", response.getStatusCodeValue());
            throw new IOException("API error for daily forecast: " + response.getStatusCode());
        }

        JsonNode json = objectMapper.readTree(response.getBody());
        JsonNode dailyForecasts = json.get("DailyForecasts");
        List<DailyForecast> forecasts = new ArrayList<>();
        Date now = new Date();

        for (JsonNode node : dailyForecasts) {
            DailyForecast forecast = new DailyForecast();
            forecast.setCity(city);
            forecast.setMinTemperature(node.get("Temperature").get("Minimum").get("Value").asDouble());
            forecast.setMaxTemperature(node.get("Temperature").get("Maximum").get("Value").asDouble());
            forecast.setMinRealFeelTemperature(node.get("RealFeelTemperature").get("Minimum").get("Value").asDouble());
            forecast.setMaxRealFeelTemperature(node.get("RealFeelTemperature").get("Maximum").get("Value").asDouble());
            forecast.setDayWeatherText(node.get("Day").get("IconPhrase").asText());
            forecast.setNightWeatherText(node.get("Night").get("IconPhrase").asText());
            forecast.setDayPrecipitationProbability(node.get("Day").get("PrecipitationProbability").asDouble());
            forecast.setNightPrecipitationProbability(node.get("Night").get("PrecipitationProbability").asDouble());
            forecast.setDayTotalLiquid(node.get("Day").get("TotalLiquid").get("Value").asDouble());
            forecast.setNightTotalLiquid(node.get("Night").get("TotalLiquid").get("Value").asDouble());
            forecast.setHoursOfSun(node.get("HoursOfSun").asDouble());
            forecast.setCloudCoverDay(node.get("Day").get("CloudCover").asDouble());
            forecast.setCloudCoverNight(node.get("Night").get("CloudCover").asDouble());
            forecast.setForecastDate(new Date(node.get("EpochDate").asLong() * 1000));
            forecast.setTimestamp(now);
            forecasts.add(forecast);
        }

        // Lưu dữ liệu trong giao dịch riêng
        try {
            saveDailyForecastData(city, forecasts);
        } catch (Exception e) {
            logger.error("Failed to save daily forecasts for city: {}: {}", city.getName(), e.getMessage(), e);
        }

        return forecasts;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveDailyForecastData(City city, List<DailyForecast> forecasts) {
        synchronized (city.getLocationKey().intern()) {
            try {
                Optional<City> existingCity = cityRepository.findById(city.getLocationKey());
                if (!existingCity.isPresent()) {
                    cityRepository.save(city);
                    logger.info("Saved new city: {}", city.getName());
                }
                dailyForecastRepository.saveAll(forecasts);
                logger.info("Saved {} daily forecasts for city: {}", forecasts.size(), city.getName());
            } catch (Exception e) {
                logger.error("Error saving daily forecasts for city: {}: {}", city.getName(), e.getMessage(), e);
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
                }
            }
        }
    }

    public List<AstronomyData> fetchAstronomyData(City city) throws IOException {
        logger.info("Fetching astronomy data for city: {}", city.getName());
        String forecastUrl = UriComponentsBuilder.fromHttpUrl(DAILY_FORECAST_API + city.getLocationKey())
                .queryParam("apikey", API_KEY)
                .queryParam("details", true)
                .queryParam("metric", true)
                .queryParam("language", "en-us")
                .toUriString();
        logger.debug("Calling astronomy data API: {}", forecastUrl);

        ResponseEntity<String> response = executeWithRetry(() -> restTemplate.getForEntity(forecastUrl, String.class));
        if (response.getStatusCodeValue() != 200) {
            logger.error("API error for astronomy data: HTTP {}", response.getStatusCodeValue());
            throw new IOException("API error for astronomy data: " + response.getStatusCode());
        }

        JsonNode json = objectMapper.readTree(response.getBody());
        JsonNode dailyForecasts = json.get("DailyForecasts");
        List<AstronomyData> astronomyDataList = new ArrayList<>();
        Date now = new Date();

        for (JsonNode node : dailyForecasts) {
            AstronomyData data = new AstronomyData();
            data.setCity(city);
            data.setSunRise(new Date(node.get("Sun").get("EpochRise").asLong() * 1000));
            data.setSunSet(new Date(node.get("Sun").get("EpochSet").asLong() * 1000));
            data.setMoonRise(node.get("Moon").get("EpochRise").isNull() ? null : new Date(node.get("Moon").get("EpochRise").asLong() * 1000));
            data.setMoonSet(node.get("Moon").get("EpochSet").isNull() ? null : new Date(node.get("Moon").get("EpochSet").asLong() * 1000));
            data.setMoonPhase(node.get("Moon").get("Phase").asText());
            data.setMoonAge(node.get("Moon").get("Age").asInt());
            data.setForecastDate(new Date(node.get("EpochDate").asLong() * 1000));
            data.setTimestamp(now);
            astronomyDataList.add(data);
        }

        // Lưu dữ liệu trong giao dịch riêng
        try {
            saveAstronomyData(city, astronomyDataList);
        } catch (Exception e) {
            logger.error("Failed to save astronomy data for city: {}: {}", city.getName(), e.getMessage(), e);
        }

        return astronomyDataList;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAstronomyData(City city, List<AstronomyData> astronomyDataList) {
        synchronized (city.getLocationKey().intern()) {
            try {
                Optional<City> existingCity = cityRepository.findById(city.getLocationKey());
                if (!existingCity.isPresent()) {
                    cityRepository.save(city);
                    logger.info("Saved new city: {}", city.getName());
                }
                astronomyDataRepository.saveAll(astronomyDataList);
                logger.info("Saved {} astronomy data entries for city: {}", astronomyDataList.size(), city.getName());
            } catch (Exception e) {
                logger.error("Error saving astronomy data for city: {}: {}", city.getName(), e.getMessage(), e);
                if (e.getCause() != null) {
                    logger.error("Root cause: {}", e.getCause().getMessage(), e.getCause());
                }
            }
        }
    }

    public void fetchAndSaveAllWeatherData(String cityName, String countryId) throws IOException {
        logger.info("Fetching and saving all weather data for city: {}, country: {}", cityName, countryId);
        City city = getLocationKey(cityName, countryId);
        if (city == null) {
            logger.error("No city found for name: {}, countryId: {}", cityName, countryId);
            throw new IllegalArgumentException("City not found: " + cityName);
        }

        try {
            fetchCurrentWeather(city);
            fetchHourlyForecast(city);
            fetchDailyForecast(city);
            fetchAstronomyData(city);
            logger.info("Successfully fetched and saved all weather data for city: {}", cityName);
        } catch (Exception e) {
            logger.error("Error fetching and saving weather data for city: {}: {}", cityName, e.getMessage(), e);
            throw new IOException("Error fetching and saving weather data", e);
        }
    }

    public CurrentWeather getCurrentWeather(String locationKey) {
        logger.info("Fetching current weather for locationKey: {}", locationKey);
        Optional<CurrentWeather> weatherOpt = currentWeatherRepository.findTopByCityLocationKeyOrderByTimestampDesc(locationKey);
        if (weatherOpt.isPresent()) {
            logger.debug("Found current weather in database for locationKey: {}", locationKey);
            return weatherOpt.get();
        }
        logger.debug("No current weather found in database for locationKey: {}", locationKey);
        return null;
    }

    public List<HourlyForecast> getHourlyForecast(String locationKey, Date startTime, Date endTime) {
        logger.info("Fetching hourly forecast for locationKey: {} from {} to {}", locationKey, startTime, endTime);
        List<HourlyForecast> forecasts = hourlyForecastRepository.findByCityLocationKeyAndForecastTimeBetweenOrderByForecastTimeAsc(locationKey, startTime, endTime);
        logger.debug("Found {} hourly forecasts in database for locationKey: {}", forecasts.size(), locationKey);
        return forecasts;
    }

    public List<DailyForecast> getDailyForecast(String locationKey, Date startDate, Date endDate) {
        logger.info("Fetching daily forecast for locationKey: {} from {} to {}", locationKey, startDate, endDate);
        List<DailyForecast> forecasts = dailyForecastRepository.findByCityLocationKeyAndForecastDateBetweenOrderByForecastDateAsc(locationKey, startDate, endDate);
        logger.debug("Found {} daily forecasts in database for locationKey: {}", forecasts.size(), locationKey);
        return forecasts;
    }

    public List<AstronomyData> getAstronomyData(String locationKey, Date startDate, Date endDate) {
        logger.info("Fetching astronomy data for locationKey: {} from {} to {}", locationKey, startDate, endDate);
        List<AstronomyData> data = astronomyDataRepository.findByCityLocationKeyAndForecastDateBetweenOrderByForecastDateAsc(locationKey, startDate, endDate);
        logger.debug("Found {} astronomy data entries in database for locationKey: {}", data.size(), locationKey);
        return data;
    }

    private String removeDiacritics(String str) {
        if (str == null) return null;
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replace("Ä�", "D")
                .replace("Ä‘", "d");
    }

    private <T> T executeWithRetry(ThrowingSupplier<T> supplier) throws IOException {
        logger.debug("Starting executeWithRetry");
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return supplier.get();
            } catch (IOException e) {
                logger.error("Attempt {} failed: {}", attempt + 1, e.getMessage());
                if (e.getCause() instanceof HttpClientErrorException) {
                    HttpClientErrorException ex = (HttpClientErrorException) e.getCause();
                    if (ex.getStatusCode().value() == 401) {
                        logger.error("Invalid API key: {}", API_KEY);
                        throw e;
                    }
                    if (ex.getStatusCode().value() == 429) {
                        logger.warn("Rate limit exceeded. Retrying after delay...");
                        try {
                            Thread.sleep(RETRY_DELAY_MS * (attempt + 1) * 2);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IOException("Interrupted while retrying", ie);
                        }
                        continue;
                    }
                }
                if (attempt < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS * (attempt + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted while retrying", ie);
                    }
                    continue;
                }
                throw e;
            }
        }
        logger.error("Max retries exceeded");
        throw new IOException("Max retries exceeded");
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws IOException;
    }
}