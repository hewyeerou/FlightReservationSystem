/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException 
    {
        FlightSchedulePlan flightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(flightSchedulePlanId);

        flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
        flightSchedulePlan.getFlightSchedules().add(flightSchedule);

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
    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException
    {
        FlightSchedulePlan returnFlightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(returnFlightSchedulePlanId);
        
        FlightSchedule flightSchedule = getFlightScheduleById(flightScheduleId);
        
        returnFlightSchedule.setFlightSchedulePlan(returnFlightSchedulePlan);
        returnFlightSchedulePlan.getFlightSchedules().add(returnFlightSchedule);
        
        
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
            flightSchedule.getSeatInventories().size();
            
            return flightSchedule;
        }
        else
        {
            throw new FlightScheduleNotFoundException("Flight Schedule " + flightScheduleId + " does not exist!");
        }
    }
    
    @Override
    public List<FlightSchedule> getFlightScheduleByFlightSchedulePlanId(Long flightSchedulePlanId)
    {
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flightSchedulePlanId = :inId");
        query.setParameter("inId", flightSchedulePlanId);
        List<FlightSchedule> flightSchedules = query.getResultList();
        
        for(FlightSchedule flightSchedule: flightSchedules)
        {
            flightSchedule.getFlightReservationRecords().size();
            flightSchedule.getSeatInventories().size();
            flightSchedule.getFlightSchedulePlan();
        }
        
        return flightSchedules;
    }
    
    @Override
    public void updateFlightSchedule(FlightSchedule flightSchedule) throws FlightScheduleNotFoundException
    {
        if(flightSchedule != null)
        {
            FlightSchedule flightScheduleToUpdate = getFlightScheduleById(flightSchedule.getFlightScheduleId());
            flightScheduleToUpdate.setDepartureDateTime(flightSchedule.getDepartureDateTime());
            flightScheduleToUpdate.setFlightHours(flightSchedule.getFlightHours());
            flightScheduleToUpdate.setFlightMinutes(flightSchedule.getFlightMinutes());
        }
        else
        {
            throw new FlightScheduleNotFoundException("Flight Schedule Plan does not exist!");
        }
    }
    
    @Override
    public void removeFlightSchedule(Long flightScheduleId) throws FlightScheduleNotFoundException
    {
        FlightSchedule flightScheduleToRemove = em.find(FlightSchedule.class, flightScheduleId);
        
        List<SeatInventory> seatInventories = new ArrayList<>(flightScheduleToRemove.getSeatInventories());
        for(SeatInventory seatInventory: seatInventories)
        {
            flightScheduleToRemove.getSeatInventories().remove(seatInventory);
            SeatInventory seatInventory1 = em.merge(seatInventory);
            seatInventory1.setCabinClass(null);
            em.remove(seatInventory1);
            
//            List<CabinClass> cabinClasses = new ArrayList<>(seatInventory1.getCabinClasses());
//            for(CabinClass cabinClass: cabinClasses)
//            {
//                seatInventory1.getCabinClasses().remove(cabinClass);
//                cabinClass.getSeatInventories().remove(seatInventory1);
//                CabinClass cabinClass1 = em.merge(cabinClass);
//            }
        }

       
        flightScheduleToRemove.getSeatInventories().clear();
        em.remove(flightScheduleToRemove);
        
    }

    
}
