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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author seowtengng
 */
@Entity
public class FlightSchedulePlan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long flightSchedulePlanId;
    @Column(nullable=false, length=32)
    @NotNull
    private String flightScheduleType;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column
    @Future
    private Date endDate;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column
    @Future
    private Date startDate;
    @Column
    @Min(0)
    private Integer intervalDays;
    @Column(nullable = false)
    @NotNull
    private Boolean enabled;
    
    @Column(nullable = false)
    @NotNull
    private String flightSchedulePlanType;
    
    @OneToOne
    private FlightSchedulePlan returnFlightSchedulePlan;
    
    @ManyToOne(optional=false)
    @JoinColumn(nullable=false)
    private Flight flight;
    
    @OneToMany(mappedBy = "flightSchedulePlan")
    private List<Fare> fares;
    
    @OneToMany(mappedBy = "flightSchedulePlan")
    private List<FlightSchedule> flightSchedules;

    public FlightSchedulePlan() {
        this.fares = new ArrayList<>();
        this.flightSchedules = new ArrayList<>();
    }

    public FlightSchedulePlan(String flightScheduleType, Date endDate, Date startDate, Integer intervalDays, Boolean enabled) {
        this();
        
        this.flightScheduleType = flightScheduleType;
        this.endDate = endDate;
        this.startDate = startDate;
        this.intervalDays = intervalDays;
        this.enabled = enabled;
    }

    public Long getFlightSchedulePlanId() {
        return flightSchedulePlanId;
    }

    public void setFlightSchedulePlanId(Long flightSchedulePlanId) {
        this.flightSchedulePlanId = flightSchedulePlanId;
    }

    public String getFlightSchedulePlanType() {
        return flightSchedulePlanType;
    }

    public void setFlightSchedulePlanType(String flightSchedulePlanType) {
        this.flightSchedulePlanType = flightSchedulePlanType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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
        hash += (flightSchedulePlanId != null ? flightSchedulePlanId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightSchedulePlan)) {
            return false;
        }
        FlightSchedulePlan other = (FlightSchedulePlan) object;
        if ((this.flightSchedulePlanId == null && other.flightSchedulePlanId != null) || (this.flightSchedulePlanId != null && !this.flightSchedulePlanId.equals(other.flightSchedulePlanId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightSchedulePlan[ id=" + flightSchedulePlanId + " ]";
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public List<Fare> getFares() {
        return fares;
    }

    public void setFares(List<Fare> fares) {
        this.fares = fares;
    }

    public String getFlightScheduleType() {
        return flightScheduleType;
    }

    public void setFlightScheduleType(String flightScheduleType) {
        this.flightScheduleType = flightScheduleType;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<FlightSchedule> getFlightSchedules() {
        return flightSchedules;
    }

    public void setFlightSchedules(List<FlightSchedule> flightSchedules) {
        this.flightSchedules = flightSchedules;
    }

    public Integer getIntervalDays() {
        return intervalDays;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public FlightSchedulePlan getReturnFlightSchedulePlan() {
        return returnFlightSchedulePlan;
    }

    public void setReturnFlightSchedulePlan(FlightSchedulePlan returnFlightSchedulePlan) {
        this.returnFlightSchedulePlan = returnFlightSchedulePlan;
    }
    
    
}
