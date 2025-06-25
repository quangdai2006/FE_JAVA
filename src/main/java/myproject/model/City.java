package myproject.model;

import javax.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @Column(name = "location_key")
    private String locationKey;

    @Column(name = "name")
    private String name;

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "admin_area_id")
    private String adminAreaId;

    @Column(name = "admin_area_name")
    private String adminAreaName;

    // Constructors
    public City() {}

    public City(String locationKey, String name, String countryId, String adminAreaId, String adminAreaName) {
        this.locationKey = locationKey;
        this.name = name;
        this.countryId = countryId;
        this.adminAreaId = adminAreaId;
        this.adminAreaName = adminAreaName;
    }

    // Getters and setters
    public String getLocationKey() { return locationKey; }
    public void setLocationKey(String locationKey) { this.locationKey = locationKey; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountryId() { return countryId; }
    public void setCountryId(String countryId) { this.countryId = countryId; }
    public String getAdminAreaId() { return adminAreaId; }
    public void setAdminAreaId(String adminAreaId) { this.adminAreaId = adminAreaId; }
    public String getAdminAreaName() { return adminAreaName; }
    public void setAdminAreaName(String adminAreaName) { this.adminAreaName = adminAreaName; }
}