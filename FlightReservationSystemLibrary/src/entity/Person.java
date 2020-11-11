package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yeerouhew
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(max = 32)
    private String username;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String password;
    
    @OneToMany(mappedBy = "person")
    protected List<FlightReservationRecord> flightReservationRecords;

    public Person() 
    {
        this.flightReservationRecords = new ArrayList<>();
    }

    public Person(String username, String password) 
    {
        this(); 
        
        this.username = username;
        this.password = password;
    }
   
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Person[ id=" + id + " ]";
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<FlightReservationRecord> getFlightReservationRecords() {
        return flightReservationRecords;
    }

    public void setFlightReservationRecords(List<FlightReservationRecord> flightReservationRecords) {
        this.flightReservationRecords = flightReservationRecords;
    }
    
    
    
}
