/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import entity.Customer;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.Partner;
import entity.Passenger;
import entity.Person;
import entity.SeatInventory;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author seowtengng
 */
@Stateless
public class FlightReservationRecordSessionBean implements FlightReservationRecordSessionBeanRemote, FlightReservationRecordSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewFlightReservationRecord(FlightReservationRecord flightReservationRecord, Long personId, List<Long> flightSchedules)
    {            
        Person person = em.find(Person.class, personId);
        flightReservationRecord.setPerson(person);
        
        if (person instanceof Customer)
        {
            Customer customer = (Customer)person;
            customer.getFlightReservationRecords().add(flightReservationRecord);
        }
        else if (person instanceof Partner)
        {
            Partner partner = (Partner)person;
            partner.getFlightReservationRecords().add(flightReservationRecord);
        }
        
        for (Long fs: flightSchedules)
        {
            FlightSchedule flightS = em.find(FlightSchedule.class, fs);
            flightReservationRecord.getFlightSchedules().add(flightS);
            flightS.getFlightReservationRecords().add(flightReservationRecord);
        }
        
        em.persist(flightReservationRecord);
        em.flush();
        return flightReservationRecord.getRecordId();
    }
    
    @Override
    public FlightReservationRecord getFlightReservationRecordByFlightScheduleId(Long flightReservationRecordId)
    {
        Query query = em.createQuery("SELECT frr FROM FlightReservationRecord frr WHERE frr.recordId = :inId");
        query.setParameter("inId", flightReservationRecordId);
        
        FlightReservationRecord flightReservationRecord = (FlightReservationRecord)query.getSingleResult();
        flightReservationRecord.getFlightSchedules().size();
        flightReservationRecord.getPassengers().size();
        
        for(Passenger passenger: flightReservationRecord.getPassengers())
        {
            passenger.getCabinSeats().size();

            for(CabinSeatInventory cabinSeatInventory: passenger.getCabinSeats())
            {
                SeatInventory seatInventory = cabinSeatInventory.getSeatInventory();
                seatInventory.getCabinClass().getFares().size();
            }
        }
        
        
        return flightReservationRecord;
        
    }
}
