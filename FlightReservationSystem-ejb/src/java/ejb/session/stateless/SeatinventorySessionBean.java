/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.CabinSeatInventory;
import entity.FlightSchedule;
import entity.SeatInventory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.FlightScheduleNotFoundException;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class SeatInventorySessionBean implements SeatInventorySessionBeanRemote, SeatInventorySessionBeanLocal {

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
        
        seatInventory.setCabinClass(cabinClass);
        cabinClass.getSeatInventories().add(seatInventory);
        
                
        em.persist(seatInventory);
        em.flush();
        
        return seatInventory.getSeatInventoryId();
    }
    
    @Override
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(Long cabinClassId, Long flightScheduleId) throws SeatInventoryNotFoundException
    {
        Query query = em.createQuery("SELECT si FROM SeatInventory si WHERE si.cabinClass.cabinClassId = :inCabinClassId AND si.flightSchedule.flightScheduleId = :inFlightScheduleId");
        query.setParameter("inCabinClassId", cabinClassId);
        query.setParameter("inFlightScheduleId", flightScheduleId);
        
        try
        {
            return (SeatInventory)query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new SeatInventoryNotFoundException();
        }
    }
    
    @Override
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(Long cabinClassId, Long flightScheduleId) throws SeatInventoryNotFoundException
    {
        SeatInventory seatInventory = retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(cabinClassId, flightScheduleId);
        
        em.detach(seatInventory);
        
        em.detach(seatInventory.getFlightSchedule());
        
        em.detach(seatInventory.getCabinClass());
    
        for (CabinSeatInventory cabinSeatInventory: seatInventory.getCabinSeatInventories())
        {
            em.detach(cabinSeatInventory);
        }
        
        return seatInventory;
    }
}

