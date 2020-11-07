/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.FlightSchedule;
import entity.SeatInventory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FlightScheduleNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class SeatinventorySessionBean implements SeatinventorySessionBeanRemote, SeatinventorySessionBeanLocal {

    @EJB(name = "FlightScheduleSessionBeanLocal")
    private FlightScheduleSessionBeanLocal flightScheduleSessionBeanLocal;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    
    @Override
    public Long createSeatInventory(SeatInventory seatInventory, Long flightScheduleId, Long cabinClassId) throws FlightScheduleNotFoundException
    {   
        FlightSchedule flightSchedule = flightScheduleSessionBeanLocal.getFlightScheduleById(flightScheduleId);
        CabinClass cabinClass = em.find(CabinClass.class, cabinClassId);
        
        seatInventory.setFlightSchedule(flightSchedule);
        flightSchedule.getSeatInventories().add(seatInventory);
        
        seatInventory.getCabinClasses().add(cabinClass);
        cabinClass.getSeatInventories().add(seatInventory);
        
                
        em.persist(seatInventory);
        em.flush();
        
        return seatInventory.getSeatInventoryId();
    }
}
