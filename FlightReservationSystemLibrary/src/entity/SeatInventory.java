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
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author seowtengng
 */
@Entity
public class SeatInventory implements Serializable {

    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatInventoryId;
    @Column(nullable = false)
    private Integer numOfAvailableSeats;
    @Column(nullable = false)
    private Integer numOfReservedSeats;
    @Column(nullable = false)
    private Integer numOfBalanceSeats;

//    @OneToOne(mappedBy = "seatInventory")
//    private FlightSchedule flightSchedule;

    public SeatInventory()
    {
    }

    public SeatInventory(Integer numOfAvailableSeats, Integer numOfReservedSeats, Integer numOfBalanceSeats) {
        this.numOfAvailableSeats = numOfAvailableSeats;
        this.numOfReservedSeats = numOfReservedSeats;
        this.numOfBalanceSeats = numOfBalanceSeats;
    }

    public Long getSeatInventoryId() {
        return seatInventoryId;
    }

    public void setSeatInventoryId(Long seatInventoryId) {
        this.seatInventoryId = seatInventoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (seatInventoryId != null ? seatInventoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SeatInventory)) {
            return false;
        }
        SeatInventory other = (SeatInventory) object;
        if ((this.seatInventoryId == null && other.seatInventoryId != null) || (this.seatInventoryId != null && !this.seatInventoryId.equals(other.seatInventoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.SeatInventory[ id=" + seatInventoryId + " ]";
    }

//    public FlightSchedule getFlightSchedule() {
//        return flightSchedule;
//    }
//
//    public void setFlightSchedule(FlightSchedule flightSchedule) {
//        this.flightSchedule = flightSchedule;
//    }

    public Integer getNumOfAvailableSeats() {
        return numOfAvailableSeats;
    }

    public void setNumOfAvailableSeats(Integer numOfAvailableSeats) {
        this.numOfAvailableSeats = numOfAvailableSeats;
    }

    public Integer getNumOfReservedSeats() {
        return numOfReservedSeats;
    }

    public void setNumOfReservedSeats(Integer numOfReservedSeats) {
        this.numOfReservedSeats = numOfReservedSeats;
    }

    public Integer getNumOfBalanceSeats() {
        return numOfBalanceSeats;
    }

    public void setNumOfBalanceSeats(Integer numOfBalanceSeats) {
        this.numOfBalanceSeats = numOfBalanceSeats;
    }
}
