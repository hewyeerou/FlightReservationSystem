/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import entity.FlightReservationRecord;
import entity.Passenger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.PassengerNotFoundException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class PassengerSessionBean implements PassengerSessionBeanRemote, PassengerSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewPassenger(Passenger passenger, Long flightReservationRecordId)
    {
        FlightReservationRecord flightReservationRecord = em.find(FlightReservationRecord.class, flightReservationRecordId);
        passenger.setFlightReservationRecord(flightReservationRecord);
        flightReservationRecord.getPassengers().add(passenger);
        
        em.persist(passenger);
        em.flush();
        
        return passenger.getPassengerId();
    }
    
    @Override
    public Passenger retrievePassengerByPassengerId (Long passengerId) throws PassengerNotFoundException
    {
        Passenger passenger = em.find(Passenger.class, passengerId);
        
        if (passenger != null)
        {
            passenger.getCabinSeats().size();
            return passenger;
        }
        else
        {
            throw new PassengerNotFoundException("Passenger with ID " + passengerId + " does not exist!\n");
        }
    }
    
    @Override
    public Passenger retrievePassengerByPassengerIdUnmanaged (Long passengerId) throws PassengerNotFoundException
    {
        Passenger p = retrievePassengerByPassengerId(passengerId);
        
        em.detach(p);
        em.detach(p.getFlightReservationRecord());
      
        return p;
    }
}
