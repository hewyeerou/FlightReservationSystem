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
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FlightReservationRecordNotFoundException;

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
    public List<FlightReservationRecord> retrieveReservationRecordsByCustomerId (Long customerId)
    {
        Query query = em.createQuery("SELECT frr FROM FlightReservationRecord frr WHERE frr.person.id = :inCustomerId ORDER BY frr.recordId ASC");
        query.setParameter("inCustomerId", customerId);
        
        return query.getResultList();
    }
    
    @Override
    public FlightReservationRecord retrieveReservationRecordById (Long recordId) throws FlightReservationRecordNotFoundException
    {
        FlightReservationRecord record = em.find(FlightReservationRecord.class, recordId);
        
        if (record != null)
        {
            record.getFlightSchedules().size();
            record.getPassengers().size();
            
            return record;
        }
        else
        {
            throw new FlightReservationRecordNotFoundException("Flight Reservation Record with ID " + recordId + " does not exist!");
        }
    }
}
