package myproject.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "daily_forecast", indexes = {
        @Index(name = "idx_location_key_forecast_date", columnList = "location_key, forecast_date", unique = true)
})
public class DailyForecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_key", nullable = false)
    private City city;

    @Column(name = "min_temperature", nullable = false)
    private double minTemperature;

    @Column(name = "max_temperature", nullable = false)
    private double maxTemperature;

    @Column(name = "min_real_feel_temperature")
    private double minRealFeelTemperature;

    @Column(name = "max_real_feel_temperature")
    private double maxRealFeelTemperature;

    @Column(name = "day_weather_text")
    private String dayWeatherText;

    @Column(name = "night_weather_text")
    private String nightWeatherText;

    @Column(name = "day_precipitation_probability")
    private double dayPrecipitationProbability;

    @Column(name = "night_precipitation_probability")
    private double nightPrecipitationProbability;

    @Column(name = "day_total_liquid")
    private double dayTotalLiquid;

    @Column(name = "night_total_liquid")
    private double nightTotalLiquid;

    @Column(name = "hours_of_sun")
    private double hoursOfSun;

    @Column(name = "cloud_cover_day")
    private double cloudCoverDay;

    @Column(name = "cloud_cover_night")
    private double cloudCoverNight;

    @Column(name = "forecast_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date forecastDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Constructors
    public DailyForecast() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public double getMinTemperature() { return minTemperature; }
    public void setMinTemperature(double minTemperature) { this.minTemperature = minTemperature; }
    public double getMaxTemperature() { return maxTemperature; }
    public void setMaxTemperature(double maxTemperature) { this.maxTemperature = maxTemperature; }
    public double getMinRealFeelTemperature() { return minRealFeelTemperature; }
    public void setMinRealFeelTemperature(double minRealFeelTemperature) { this.minRealFeelTemperature = minRealFeelTemperature; }
    public double getMaxRealFeelTemperature() { return maxRealFeelTemperature; }
    public void setMaxRealFeelTemperature(double maxRealFeelTemperature) { this.maxRealFeelTemperature = maxRealFeelTemperature; }
    public String getDayWeatherText() { return dayWeatherText; }
    public void setDayWeatherText(String dayWeatherText) { this.dayWeatherText = dayWeatherText; }
    public String getNightWeatherText() { return nightWeatherText; }
    public void setNightWeatherText(String nightWeatherText) { this.nightWeatherText = nightWeatherText; }
    public double getDayPrecipitationProbability() { return dayPrecipitationProbability; }
    public void setDayPrecipitationProbability(double dayPrecipitationProbability) { this.dayPrecipitationProbability = dayPrecipitationProbability; }
    public double getNightPrecipitationProbability() { return nightPrecipitationProbability; }
    public void setNightPrecipitationProbability(double nightPrecipitationProbability) { this.nightPrecipitationProbability = nightPrecipitationProbability; }
    public double getDayTotalLiquid() { return dayTotalLiquid; }
    public void setDayTotalLiquid(double dayTotalLiquid) { this.dayTotalLiquid = dayTotalLiquid; }
    public double getNightTotalLiquid() { return nightTotalLiquid; }
    public void setNightTotalLiquid(double nightTotalLiquid) { this.nightTotalLiquid = nightTotalLiquid; }
    public double getHoursOfSun() { return hoursOfSun; }
    public void setHoursOfSun(double hoursOfSun) { this.hoursOfSun = hoursOfSun; }
    public double getCloudCoverDay() { return cloudCoverDay; }
    public void setCloudCoverDay(double cloudCoverDay) { this.cloudCoverDay = cloudCoverDay; }
    public double getCloudCoverNight() { return cloudCoverNight; }
    public void setCloudCoverNight(double cloudCoverNight) { this.cloudCoverNight = cloudCoverNight; }
    public Date getForecastDate() { return forecastDate; }
    public void setForecastDate(Date forecastDate) { this.forecastDate = forecastDate; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}