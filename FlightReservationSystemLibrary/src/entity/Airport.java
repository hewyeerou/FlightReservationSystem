/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author seowtengng
 */
@Entity
public class Airport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long airportId;
    @Column(nullable = false, unique = true, length = 5)
    @NotNull
    @Size(max = 5)
    private String iataCode;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String name;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String city;
    @Column(length = 32)
    private String province;
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String country;
    @Column(nullable=false)
    @NotNull
    private Integer timeZoneDiff;

    @OneToMany(mappedBy = "origin", fetch=FetchType.LAZY)
    private List<FlightRoute> departureRoutes;
    
    @OneToMany(mappedBy = "destination", fetch=FetchType.LAZY)
    private List<FlightRoute> arrivalRoutes;
    
    public Airport() {
        this.departureRoutes = new ArrayList<>();
        this.arrivalRoutes = new ArrayList<>();
    }

    public Airport(String iataCode, String name, String city, String province, String country, Integer timeZoneDiff) {
        
        this();
        
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.province = province;
        this.country = country;
        this.timeZoneDiff = timeZoneDiff;
    }

    public Long getAirportId() {
        return airportId;
    }

    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (airportId != null ? airportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Airport)) {
            return false;
        }
        Airport other = (Airport) object;
        if ((this.airportId == null && other.airportId != null) || (this.airportId != null && !this.airportId.equals(other.airportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Airport[ id=" + airportId + " ]";
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<FlightRoute> getDepartureRoutes() {
        return departureRoutes;
    }

    public void setDepartureRoutes(List<FlightRoute> departureRoutes) {
        this.departureRoutes = departureRoutes;
    }

    public List<FlightRoute> getArrivalRoutes() {
        return arrivalRoutes;
    }

    public void setArrivalRoutes(List<FlightRoute> arrivalRoutes) {
        this.arrivalRoutes = arrivalRoutes;
    }

    public Integer getTimeZoneDiff() {
        return timeZoneDiff;
    }

    public void setTimeZoneDiff(Integer timeZoneDiff) {
        this.timeZoneDiff = timeZoneDiff;
    }

}
