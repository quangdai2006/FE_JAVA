package myproject.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "astronomy_data", indexes = {
        @Index(name = "idx_location_key_forecast_date", columnList = "location_key, forecast_date", unique = true)
})
public class AstronomyData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "location_key", nullable = false)
    private City city;

    @Column(name = "sun_rise")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sunRise;

    @Column(name = "sun_set")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sunSet;

    @Column(name = "moon_rise")
    @Temporal(TemporalType.TIMESTAMP)
    private Date moonRise;

    @Column(name = "moon_set")
    @Temporal(TemporalType.TIMESTAMP)
    private Date moonSet;

    @Column(name = "moon_phase")
    private String moonPhase;

    @Column(name = "moon_age")
    private int moonAge;

    @Column(name = "forecast_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date forecastDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    // Constructors
    public AstronomyData() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public Date getSunRise() { return sunRise; }
    public void setSunRise(Date sunRise) { this.sunRise = sunRise; }
    public Date getSunSet() { return sunSet; }
    public void setSunSet(Date sunSet) { this.sunSet = sunSet; }
    public Date getMoonRise() { return moonRise; }
    public void setMoonRise(Date moonRise) { this.moonRise = moonRise; }
    public Date getMoonSet() { return moonSet; }
    public void setMoonSet(Date moonSet) { this.moonSet = moonSet; }
    public String getMoonPhase() { return moonPhase; }
    public void setMoonPhase(String moonPhase) { this.moonPhase = moonPhase; }
    public int getMoonAge() { return moonAge; }
    public void setMoonAge(int moonAge) { this.moonAge = moonAge; }
    public Date getForecastDate() { return forecastDate; }
    public void setForecastDate(Date forecastDate) { this.forecastDate = forecastDate; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}