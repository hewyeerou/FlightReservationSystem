/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

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
    @Column(nullable = false, unique = true, length = 8)
    private String iataCode;
    @Column(nullable = false, length = 32)
    private String name;
    @Column(nullable = false, unique = true, length = 32)
    private String city;
    @Column(length = 32)
    private String state;
    @Column(nullable = false, length = 32)
    private String country;

    @OneToOne(mappedBy = "origin")
    private FlightRoute departure;
    @OneToOne(mappedBy = "destination")
    private FlightRoute arrival;
    
    

    public Airport() {
        
    }

    public Airport(String iataCode, String name, String city, String state, String country) {
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.state = state;
        this.country = country;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public FlightRoute getDeparture() {
        return departure;
    }

    public void setDeparture(FlightRoute departure) {
        this.departure = departure;
    }

    public FlightRoute getArrival() {
        return arrival;
    }

    public void setArrival(FlightRoute arrival) {
        this.arrival = arrival;
    }

}
