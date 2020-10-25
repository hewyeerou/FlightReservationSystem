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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

/**
 *
 * @author seowtengng
 */
@Entity
public class AircraftConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aircraftConfigId;
    @Column(nullable=false, length=32)
    private String name;
    @Column(nullable=false)
    @Size(min = 1, max = 4)
    private Integer numOfCabinClasses;
    @Column(nullable=false)
    private Integer maxSeatCapacity;
    
    @OneToOne(mappedBy="aircraftConfig")
    private Flight flight;
    
    @OneToMany(mappedBy="aircraftConfig")
    private List<CabinClass> cabinClasses;
    
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    private AircraftType aircraftType;

    public AircraftConfig() {
        cabinClasses = new ArrayList<>();
    }    

    public AircraftConfig(String name, Integer numOfCabinClasses, Integer maxSeatCapacity) {
        this();
        
        this.name = name;
        this.numOfCabinClasses = numOfCabinClasses;
        this.maxSeatCapacity = maxSeatCapacity;
    }

    public Long getAircraftConfigId() {
        return aircraftConfigId;
    }

    public void setAircraftConfigId(Long aircraftConfigId) {
        this.aircraftConfigId = aircraftConfigId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (aircraftConfigId != null ? aircraftConfigId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AircraftConfig)) {
            return false;
        }
        AircraftConfig other = (AircraftConfig) object;
        if ((this.aircraftConfigId == null && other.aircraftConfigId != null) || (this.aircraftConfigId != null && !this.aircraftConfigId.equals(other.aircraftConfigId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AircraftConfig[ id=" + aircraftConfigId + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumOfCabinClasses() {
        return numOfCabinClasses;
    }

    public void setNumOfCabinClasses(Integer numOfCabinClasses) {
        this.numOfCabinClasses = numOfCabinClasses;
    }

    public Integer getMaxSeatCapacity() {
        return maxSeatCapacity;
    }

    public void setMaxSeatCapacity(Integer maxSeatCapacity) {
        this.maxSeatCapacity = maxSeatCapacity;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public List<CabinClass> getCabinClasses() {
        return cabinClasses;
    }

    public void setCabinClasses(List<CabinClass> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }

    public AircraftType getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(AircraftType aircraftType) {
        this.aircraftType = aircraftType;
    }
    
}
