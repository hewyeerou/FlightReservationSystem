/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author seowtengng
 */
@Entity
public class FlightSchedule implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightScheduleId;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    @Future
    private Date departureDateTime;
    @Column(nullable = false)
    private Integer flightHours;
    @Column(nullable = false)
    private Integer flightMinutes;
    @Column(nullable = false)
    @NotNull
    private Boolean enabled;
    
    @Column(nullable = false)
    @NotNull
    private String flightScheduleType;
    
    @OneToOne
    private FlightSchedule returnFlightSchedule;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private FlightSchedulePlan flightSchedulePlan;
    
    @OneToMany(mappedBy = "flightSchedule", orphanRemoval = true)
    private List<SeatInventory> seatInventories;
    
    @ManyToMany
    private List<FlightReservationRecord> flightReservationRecords;

    public FlightSchedule() {
        this.flightReservationRecords = new ArrayList<>();
        this.seatInventories = new ArrayList<>();
    }

    public FlightSchedule(Date departure, Integer flightHours, Integer flightMinutes,  Boolean enabled) {
        this();
        
        this.departureDateTime = departure;
        this.flightHours = flightHours;
        this.flightMinutes = flightMinutes;
        this.enabled = enabled;
    }

    @XmlTransient
    public FlightSchedule getReturnFlightSchedule() {
        return returnFlightSchedule;
    }

    public void setReturnFlightSchedule(FlightSchedule returnFlightSchedule) {
        this.returnFlightSchedule = returnFlightSchedule;
    }
    
    public Long getFlightScheduleId() {
        return flightScheduleId;
    }

    public void setFlightScheduleId(Long flightScheduleId) {
        this.flightScheduleId = flightScheduleId;
    }

    public String getFlightScheduleType() {
        return flightScheduleType;
    }

    public void setFlightScheduleType(String flightScheduleType) {
        this.flightScheduleType = flightScheduleType;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightScheduleId != null ? flightScheduleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightSchedule)) {
            return false;
        }
        FlightSchedule other = (FlightSchedule) object;
        if ((this.flightScheduleId == null && other.flightScheduleId != null) || (this.flightScheduleId != null && !this.flightScheduleId.equals(other.flightScheduleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedule[ id=" + flightScheduleId + " ]";
    }

    public Date getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(Date departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public Integer getFlightHours() {
        return flightHours;
    }

    public void setFlightHours(Integer flightHours) {
        this.flightHours = flightHours;
    }

    public FlightSchedulePlan getFlightSchedulePlan() {
        return flightSchedulePlan;
    }

    public void setFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) {
        this.flightSchedulePlan = flightSchedulePlan;
    }

    @XmlTransient
    public List<SeatInventory> getSeatInventories() {
        return seatInventories;
    }

    public void setSeatInventories(List<SeatInventory> seatInventories) {
        this.seatInventories = seatInventories;
    }

    @XmlTransient
    public List<FlightReservationRecord> getFlightReservationRecords() {
        return flightReservationRecords;
    }

    public void setFlightReservationRecords(List<FlightReservationRecord> flightReservationRecords) {
        this.flightReservationRecords = flightReservationRecords;
    }

    public Integer getFlightMinutes() {
        return flightMinutes;
    }

    public void setFlightMinutes(Integer flightMinutes) {
        this.flightMinutes = flightMinutes;
    }
    
}
