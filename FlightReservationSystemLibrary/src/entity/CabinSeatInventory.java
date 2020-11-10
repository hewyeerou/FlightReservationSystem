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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author yeerouhew
 */
@Entity
public class CabinSeatInventory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cabinSeatInventoryId;

    @Column
    private String seatTaken;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private SeatInventory seatInventory;

    public CabinSeatInventory() {
        
    }

    public CabinSeatInventory(String seatTaken) {
        this.seatTaken = seatTaken;
    }
    
    
    
    public Long getCabinSeatInventoryId() {
        return cabinSeatInventoryId;
    }

    public void setCabinSeatInventoryId(Long cabinSeatInventoryId) {
        this.cabinSeatInventoryId = cabinSeatInventoryId;
    }

    public String getSeatTaken() {
        return seatTaken;
    }

    public void setSeatTaken(String seatTaken) {
        this.seatTaken = seatTaken;
    }

    public SeatInventory getSeatInventory() {
        return seatInventory;
    }

    public void setSeatInventory(SeatInventory seatInventory) {
        this.seatInventory = seatInventory;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cabinSeatInventoryId != null ? cabinSeatInventoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the cabinSeatInventoryId fields are not set
        if (!(object instanceof CabinSeatInventory)) {
            return false;
        }
        CabinSeatInventory other = (CabinSeatInventory) object;
        if ((this.cabinSeatInventoryId == null && other.cabinSeatInventoryId != null) || (this.cabinSeatInventoryId != null && !this.cabinSeatInventoryId.equals(other.cabinSeatInventoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.CabinSeatInventory[ id=" + cabinSeatInventoryId + " ]";
    }
    
}
