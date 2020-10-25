/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import util.enumeration.CabinClassEnum;

/**
 *
 * @author seowtengng
 */
@Entity
public class CabinClass implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinClassId;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private CabinClassEnum cabinClassType;
    @Column(nullable=false)
    private Integer numOfAisle;
    @Column(nullable=false)
    private Integer numOfRows;
    @Column(nullable=false)
    private List<Integer> numOfSeatsAbreast;
    @Column(nullable=false, length=8)
    private String seatConfigPerColumn;
    @Column(nullable=false)
    private Integer maxSeatCapacity;
    
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    protected AircraftConfig aircraftConfig;

    public CabinClass() {
        
    }

    public CabinClass(CabinClassEnum cabinClassType, Integer numOfAisle, Integer numOfRows, List<Integer> numOfSeatsAbreast, String seatConfigPerColumn, Integer maxSeatCapacity) {
        this.cabinClassType = cabinClassType;
        this.numOfAisle = numOfAisle;
        this.numOfRows = numOfRows;
        this.numOfSeatsAbreast = numOfSeatsAbreast;
        this.seatConfigPerColumn = seatConfigPerColumn;
        this.maxSeatCapacity = maxSeatCapacity;
    }
    
    public Long getCabinClassId() {
        return cabinClassId;
    }

    public void setCabinClassId(Long cabinClassId) {
        this.cabinClassId = cabinClassId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cabinClassId != null ? cabinClassId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CabinClass)) {
            return false;
        }
        CabinClass other = (CabinClass) object;
        if ((this.cabinClassId == null && other.cabinClassId != null) || (this.cabinClassId != null && !this.cabinClassId.equals(other.cabinClassId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CabinClass[ id=" + cabinClassId + " ]";
    }
    
    public Integer getNumOfAisle() {
        return numOfAisle;
    }

    public void setNumOfAisle(Integer numOfAisle) {
        this.numOfAisle = numOfAisle;
    }

    public Integer getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(Integer numOfRows) {
        this.numOfRows = numOfRows;
    }

    public List<Integer> getNumOfSeatsAbreast() {
        return numOfSeatsAbreast;
    }

    public void setNumOfSeatsAbreast(List<Integer> numOfSeatsAbreast) {
        this.numOfSeatsAbreast = numOfSeatsAbreast;
    }

    public String getSeatConfigPerColumn() {
        return seatConfigPerColumn;
    }

    public void setSeatConfigPerColumn(String seatConfigPerColumn) {
        this.seatConfigPerColumn = seatConfigPerColumn;
    }

    public Integer getMaxSeatCapacity() {
        return maxSeatCapacity;
    }

    public void setMaxSeatCapacity(Integer maxSeatCapacity) {
        this.maxSeatCapacity = maxSeatCapacity;
    }

    public AircraftConfig getAircraftConfig() {
        return aircraftConfig;
    }

    public void setAircraftConfig(AircraftConfig aircraftConfig) {
        this.aircraftConfig = aircraftConfig;
    }

    public CabinClassEnum getCabinClassType() {
        return cabinClassType;
    }

    public void setCabinClassType(CabinClassEnum cabinClassType) {
        this.cabinClassType = cabinClassType;
    }
    
}
