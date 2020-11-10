/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.Fare;
import entity.Flight;
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
import util.exception.FlightNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FlightSchedulePlanSessionBean implements FlightSchedulePlanSessionBeanRemote, FlightSchedulePlanSessionBeanLocal 
{
    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @EJB(name = "FlightSessionBeanLocal")
    private FlightSessionBeanLocal flightSessionBeanLocal;
    
    @EJB(name = "FareSessionBeanLocal")
    private FareSessionBeanLocal fareSessionBeanLocal;
    
    @Override
    public Long createNewFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan, String flightNum) throws FlightNotFoundException{      
        //flight
        Flight flight = flightSessionBeanLocal.getFlightByFlightNum(flightNum);
        
        //flight schedule plan - flight
        flightSchedulePlan.setFlight(flight);
        flight.getFlightSchedulePlans().add(flightSchedulePlan);
        
        //flight schedule plan - fare
        for(Fare fare: flightSchedulePlan.getFares())
        {
            fare.setFlightSchedulePlan(flightSchedulePlan);
            flightSchedulePlan.getFares().add(fare);
        }
        
        //flight schedule plan - flight schedule
        for(FlightSchedule flightSchedule: flightSchedulePlan.getFlightSchedules()){
            flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
            flightSchedulePlan.getFlightSchedules().add(flightSchedule);
        }
        
        flightSchedulePlan.setReturnFlightSchedulePlan(flightSchedulePlan);
        
        em.persist(flightSchedulePlan);
        em.flush();
        
        return flightSchedulePlan.getFlightSchedulePlanId();
    }
    
     
    @Override
    public Long createNewReturnFlightSchedulePlan(FlightSchedulePlan returnFlightSchedulePlan, Long flightSchedulePlanId) throws FlightNotFoundException
    {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlanId);
        
        Flight returnFlight = flightSchedulePlan.getFlight().getReturnFlight();
        
        //return flight schedule plan - flight
        returnFlightSchedulePlan.setFlight(returnFlight);
        returnFlight.getFlightSchedulePlans().add(returnFlightSchedulePlan);
        
        //return flight schedule plan - fare
        for(Fare fare: returnFlightSchedulePlan.getFares())
        {
            fare.setFlightSchedulePlan(returnFlightSchedulePlan);
            
            em.persist(fare);
        }
        
        //flight schedule plan - flight schedule
        for(FlightSchedule flightSchedule: returnFlightSchedulePlan.getFlightSchedules())
        {
            flightSchedule.setFlightSchedulePlan(returnFlightSchedulePlan);
            
            em.persist(flightSchedule);
        }
        
        //flight schedule plan - return flight schedule plan
        flightSchedulePlan.setReturnFlightSchedulePlan(returnFlightSchedulePlan);
        returnFlightSchedulePlan.setReturnFlightSchedulePlan(returnFlightSchedulePlan);
        
        em.persist(returnFlightSchedulePlan);
        em.flush();
        
        return returnFlightSchedulePlan.getFlightSchedulePlanId();
    }
    
    @Override
    public FlightSchedulePlan getFlightSchedulePlanById(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException
    {
        FlightSchedulePlan flightSchedulePlan = em.find(FlightSchedulePlan.class, flightSchedulePlanId);
        
        if(flightSchedulePlan != null)
        {
            flightSchedulePlan.getFares().size();
            flightSchedulePlan.getFlightSchedules().size();
            flightSchedulePlan.getFlight();
            flightSchedulePlan.getFlight().getAircraftConfig().getCabinClasses().size();
            
            return flightSchedulePlan;
        }
        else
        {
            throw new FlightSchedulePlanNotFoundException("Flight Schedule Plan " + flightSchedulePlanId + " does not exist!");
        }
    }

    @Override
    public List<FlightSchedulePlan> getAllFlightSchedulePlan()
    {
        Query query = em.createQuery("SELECT f FROM FlightSchedulePlan f ORDER BY f.flight.flightNumber ASC");
        
        List<FlightSchedulePlan> flightSchedulePlans = query.getResultList();
        
        for(FlightSchedulePlan flightSchedulePlan: flightSchedulePlans)
        {
           flightSchedulePlan.getFlightSchedules().size();
           flightSchedulePlan.getFlight();
           flightSchedulePlan.getFares().size();
        }
        return flightSchedulePlans;
    }
    
 
    @Override
    public void updateFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) throws FlightSchedulePlanNotFoundException
    {
        if(flightSchedulePlan != null && flightSchedulePlan.getFlightSchedulePlanId() != null)
        {
            FlightSchedulePlan flightSchedulePlanToUpdate = getFlightSchedulePlanById(flightSchedulePlan.getFlightSchedulePlanId());
            
            flightSchedulePlanToUpdate.setFlightSchedules(flightSchedulePlan.getFlightSchedules());
            flightSchedulePlanToUpdate.setFares(flightSchedulePlan.getFares());
            flightSchedulePlanToUpdate.setEndDate(flightSchedulePlan.getEndDate());
            flightSchedulePlanToUpdate.setStartDate(flightSchedulePlan.getStartDate());
            flightSchedulePlanToUpdate.setIntervalDays(flightSchedulePlan.getIntervalDays());
            
        }
        else
        {
            throw new FlightSchedulePlanNotFoundException("Flight Schedule Id is not provided to update flight schedule plan!");
        }
        
    }
    
    @Override
    public void removeFlightSchedulePlan(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException
    {
        FlightSchedulePlan flightSchedulePlanToRemove = getFlightSchedulePlanById(flightSchedulePlanId);
        
        List<FlightSchedule> flightSchedules = new ArrayList<>(flightSchedulePlanToRemove.getFlightSchedules());
        for(FlightSchedule flightSchedule: flightSchedules)
        {
            flightSchedulePlanToRemove.getFlightSchedules().remove(flightSchedule);
            FlightSchedule flightSchedule1 = em.merge(flightSchedule);
            em.remove(flightSchedule1);
            
            List<SeatInventory> seatInventories = new ArrayList<>(flightSchedule1.getSeatInventories());
            for(SeatInventory seatInventory: seatInventories)
            {
                flightSchedule1.getSeatInventories().remove(seatInventory);
                seatInventory.setCabinClass(null);
                em.remove(seatInventory);                
            }
            
            flightSchedule.getSeatInventories().clear();
        }
        
        List<Fare> fareList = fareSessionBeanLocal.getFaresByFlightSchedulePlanId(flightSchedulePlanId);
        List<Fare> fares = new ArrayList<>(fareList);
        for(Fare fare:fares)
        {
            flightSchedulePlanToRemove.getFares().remove(fare);
            Fare fare1 = em.merge(fare);
            em.remove(fare1);

            CabinClass cabinClass = fare1.getCabinClass();
            cabinClass.getFares().remove(cabinClass);
        }
        
        
        flightSchedulePlanToRemove.getFlight().getFlightSchedulePlans().remove(flightSchedulePlanToRemove);
        
        flightSchedulePlanToRemove.getFares().clear();
        em.remove(flightSchedulePlanToRemove);
        
    }
    
    @Override
    public void setFlightSchedulePlanDisabled(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException
    {
        FlightSchedulePlan flightSchedulePlanToUpdate = getFlightSchedulePlanById(flightSchedulePlanId);
        
        if(flightSchedulePlanToUpdate != null)
        {
            flightSchedulePlanToUpdate.setEnabled(false);
            for(FlightSchedule flightSchedule :flightSchedulePlanToUpdate.getFlightSchedules())
            {
                flightSchedule.setEnabled(false);
            }  
        }
        else
        {
            throw new FlightSchedulePlanNotFoundException("Flight Schedule Plan does not exist!");
        }
    }
}
