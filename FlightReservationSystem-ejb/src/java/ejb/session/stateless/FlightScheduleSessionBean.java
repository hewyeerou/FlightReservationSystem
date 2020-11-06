/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FlightScheduleSessionBean implements FlightScheduleSessionBeanRemote, FlightScheduleSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @EJB(name = "FlightSchedulePlanSessionBeanLocal")
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBeanLocal;

    @Override
    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId, Long seatInventoryId) throws FlightSchedulePlanNotFoundException 
    {
        FlightSchedulePlan flightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(flightSchedulePlanId);

        flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
        flightSchedulePlan.getFlightSchedules().add(flightSchedule);

        SeatInventory seatInventory = em.find(SeatInventory.class, seatInventoryId);
        flightSchedule.setSeatInventory(seatInventory);

        for (FlightReservationRecord flightReservationRecord : flightSchedule.getFlightReservationRecords()) {
            flightReservationRecord.getFlightSchedules().add(flightSchedule);

            for (FlightSchedule fs : flightReservationRecord.getFlightSchedules()) {
                fs.getFlightReservationRecords().add(flightReservationRecord);
            }
        }
        
        flightSchedule.setReturnFlightSchedule(flightSchedule);

        em.persist(flightSchedule);
        em.flush();

        return flightSchedule.getFlightScheduleId();
    }
    
    @Override
    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId, Long seatInventoryId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException
    {
        FlightSchedulePlan returnFlightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(returnFlightSchedulePlanId);
        
        FlightSchedule flightSchedule = getFlightScheduleById(flightScheduleId);
        
        returnFlightSchedule.setFlightSchedulePlan(returnFlightSchedulePlan);
        returnFlightSchedulePlan.getFlightSchedules().add(returnFlightSchedule);
        
        SeatInventory seatInventory = em.find(SeatInventory.class, seatInventoryId);
        returnFlightSchedule.setSeatInventory(seatInventory);
        
        for(FlightReservationRecord flightReservationRecord: returnFlightSchedule.getFlightReservationRecords())
        {
            flightReservationRecord.getFlightSchedules().add(returnFlightSchedule);
            
            for(FlightSchedule rfs: flightReservationRecord.getFlightSchedules()){
                rfs.getFlightReservationRecords().add(flightReservationRecord);
            }
        }
        
        flightSchedule.setReturnFlightSchedule(returnFlightSchedule);
        returnFlightSchedule.setReturnFlightSchedule(returnFlightSchedule);
        
        em.persist(returnFlightSchedule);
        em.flush();
        
        return returnFlightSchedule.getFlightScheduleId();
    }
    
    
    @Override
    public FlightSchedule getFlightScheduleById(Long flightScheduleId) throws FlightScheduleNotFoundException 
    {
        FlightSchedule flightSchedule = em.find(FlightSchedule.class, flightScheduleId);
        
        if(flightSchedule != null)
        {
            flightSchedule.getFlightReservationRecords().size();
            flightSchedule.getFlightSchedulePlan();
            flightSchedule.getSeatInventory();
            
            return flightSchedule;
        }
        else
        {
            throw new FlightScheduleNotFoundException("Flight Schedule " + flightScheduleId + " does not exist!");
        }
    }

    
}
