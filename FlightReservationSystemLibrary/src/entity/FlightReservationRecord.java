package entity;

import java.io.Serializable;
import java.math.BigDecimal;
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

/**
 *
 * @author yeerouhew
 */
@Entity
public class FlightReservationRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;
    @Column(nullable = false)
    private Integer numOfPassengers;
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Person person;
    
    @OneToMany(mappedBy = "flightReservationRecord")
    private List<Passenger> passengers;

    public FlightReservationRecord() 
    {
        this.passengers = new ArrayList<>();
    }

    public FlightReservationRecord(Integer numOfPassengers, BigDecimal totalAmount, Person person, List<Passenger> passengers) 
    {
        this();
        
        this.numOfPassengers = numOfPassengers;
        this.totalAmount = totalAmount;
        this.person = person;
        this.passengers = passengers;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getRecordId() != null ? getRecordId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the recordId fields are not set
        if (!(object instanceof FlightReservationRecord)) {
            return false;
        }
        FlightReservationRecord other = (FlightReservationRecord) object;
        if ((this.getRecordId() == null && other.getRecordId() != null) || (this.getRecordId() != null && !this.recordId.equals(other.recordId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightReservationRecord[ id=" + getRecordId() + " ]";
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Integer getNumOfPassengers() {
        return numOfPassengers;
    }

    public void setNumOfPassengers(Integer numOfPassengers) {
        this.numOfPassengers = numOfPassengers;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }
    
    
    
}
