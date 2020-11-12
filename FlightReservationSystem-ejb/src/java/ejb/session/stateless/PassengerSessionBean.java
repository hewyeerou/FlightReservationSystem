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

/**
 *
 * @author seowtengng
 */
@Stateless
public class PassengerSessionBean implements PassengerSessionBeanRemote, PassengerSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewPassenger(Passenger passenger, Long flightReservationRecordId, Long cabinSeatId)
    {
        FlightReservationRecord flightReservationRecord = em.find(FlightReservationRecord.class, flightReservationRecordId);
        passenger.setFlightReservationRecord(flightReservationRecord);
        flightReservationRecord.getPassengers().add(passenger);
        
        CabinSeatInventory cabinSeat = em.find(CabinSeatInventory.class, cabinSeatId);
        passenger.getCabinSeats().add(cabinSeat);
        
        em.persist(passenger);
        em.flush();
        
        return passenger.getPassengerId();
    }
}
