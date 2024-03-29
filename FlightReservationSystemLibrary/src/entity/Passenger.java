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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author yeerouhew
 */
@Entity
public class Passenger implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long passengerId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String firstName;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String lastName;
    @Column(nullable = false, length = 9)
    @NotNull
    @Size(max = 9)
    private String passportNum;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightReservationRecord flightReservationRecord;
    
    @OneToMany
    private List<CabinSeatInventory> cabinSeats;
    
    public Passenger() 
    {
        this.cabinSeats = new ArrayList<>();
    }

    public Passenger(String firstName, String lastName, String passportNum) {
        
        this();
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.passportNum = passportNum;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getPassengerId() != null ? getPassengerId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the passengerId fields are not set
        if (!(object instanceof Passenger)) {
            return false;
        }
        Passenger other = (Passenger) object;
        if ((this.getPassengerId() == null && other.getPassengerId() != null) || (this.getPassengerId() != null && !this.passengerId.equals(other.passengerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Passenger[ id=" + getPassengerId() + " ]";
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public void setPassportNum(String passportNum) {
        this.passportNum = passportNum;
    }

    @XmlTransient
    public FlightReservationRecord getFlightReservationRecord() {
        return flightReservationRecord;
    }

    public void setFlightReservationRecord(FlightReservationRecord flightReservationRecord) {
        this.flightReservationRecord = flightReservationRecord;
    }

    public List<CabinSeatInventory> getCabinSeats() {
        return cabinSeats;
    }

    public void setCabinSeats(List<CabinSeatInventory> cabinSeats) {
        this.cabinSeats = cabinSeats;
    }
}
