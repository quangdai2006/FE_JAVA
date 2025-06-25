package myproject.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "current_weather", indexes = {
        @Index(name = "idx_location_key_timestamp", columnList = "location_key, timestamp", unique = true)
})
public class CurrentWeather {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_key", nullable = false)
    private City city;

    @Column(nullable = false)
    private double temperature;

    @Column(name = "real_feel_temperature")
    private double realFeelTemperature;

    @Column
    private double humidity;

    @Column(name = "wind_speed")
    private double windSpeed;

    @Column(name = "wind_gust_speed")
    private double windGustSpeed;

    @Column(name = "uv_index")
    private int uvIndex;

    @Column(name = "weather_text")
    private String weatherText;

    @Column
    private double visibility;

    @Column
    private double ceiling;

    @Column(name = "precipitation_probability")
    private double precipitationProbability;

    @Column(name = "cloud_cover")
    private double cloudCover;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Constructors
    public CurrentWeather() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getRealFeelTemperature() { return realFeelTemperature; }
    public void setRealFeelTemperature(double realFeelTemperature) { this.realFeelTemperature = realFeelTemperature; }
    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    public double getWindGustSpeed() { return windGustSpeed; }
    public void setWindGustSpeed(double windGustSpeed) { this.windGustSpeed = windGustSpeed; }
    public int getUvIndex() { return uvIndex; }
    public void setUvIndex(int uvIndex) { this.uvIndex = uvIndex; }
    public String getWeatherText() { return weatherText; }
    public void setWeatherText(String weatherText) { this.weatherText = weatherText; }
    public double getVisibility() { return visibility; }
    public void setVisibility(double visibility) { this.visibility = visibility; }
    public double getCeiling() { return ceiling; }
    public void setCeiling(double ceiling) { this.ceiling = ceiling; }
    public double getPrecipitationProbability() { return precipitationProbability; }
    public void setPrecipitationProbability(double precipitationProbability) { this.precipitationProbability = precipitationProbability; }
    public double getCloudCover() { return cloudCover; }
    public void setCloudCover(double cloudCover) { this.cloudCover = cloudCover; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}