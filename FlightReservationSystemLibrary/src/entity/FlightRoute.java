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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author seowtengng
 */
@Entity
public class FlightRoute implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightRouteId;
    
    @OneToOne(optional = true)
    private FlightRoute returnFlightRoute;
    
    @XmlTransient
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Airport origin;
    
    @XmlTransient
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Airport destination;
    
    @Column(nullable = false)
    @NotNull
    private String flightRouteType;
    
    @Column(nullable = false)
    @NotNull
    private Boolean enabled;
    
    @XmlTransient
    @OneToMany(mappedBy = "flightRoute", fetch = FetchType.LAZY)
    private List<Flight> flights;

    public FlightRoute() {
        flights = new ArrayList<>();
    }

    public FlightRoute(FlightRoute returnFlightRoute) {
        this.returnFlightRoute = returnFlightRoute;
    }

    public FlightRoute(Airport origin, Airport destination, String flightRouteType, Boolean enabled) 
    {
        this();
       
        this.origin = origin;
        this.destination = destination;
        this.flightRouteType = flightRouteType;
        this.enabled = enabled;
    }
    
    
   
    public Long getFlightRouteId() {
        return flightRouteId;
    }

    public void setFlightRouteId(Long flightRouteId) {
        this.flightRouteId = flightRouteId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightRouteId != null ? flightRouteId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightRoute)) {
            return false;
        }
        FlightRoute other = (FlightRoute) object;
        if ((this.flightRouteId == null && other.flightRouteId != null) || (this.flightRouteId != null && !this.flightRouteId.equals(other.flightRouteId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightRoute[ id=" + flightRouteId + " ]";
    }

    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public FlightRoute getReturnFlightRoute() {
        return returnFlightRoute;
    }

    public void setReturnFlightRoute(FlightRoute returnFlightRoute) {
        this.returnFlightRoute = returnFlightRoute;
    }

    public String getFlightRouteType() {
        return flightRouteType;
    }

    public void setFlightRouteType(String flightRouteType) {
        this.flightRouteType = flightRouteType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    
    
}
