/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Fare;
import entity.Flight;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
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
            returnFlightSchedulePlan.getFares().add(fare);
        }
        
        //flight schedule plan - flight schedule
        for(FlightSchedule flightSchedule: returnFlightSchedulePlan.getFlightSchedules())
        {
            flightSchedule.setFlightSchedulePlan(returnFlightSchedulePlan);
            returnFlightSchedulePlan.getFlightSchedules().add(flightSchedule);
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
            flightSchedulePlan.getFares();
            flightSchedulePlan.getFlightSchedules().size();
            flightSchedulePlan.getFlight();
            
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
        Query query = em.createQuery("SELECT f FROM FlightSchedulePlan f");
        
        List<FlightSchedulePlan> flightSchedulePlans = query.getResultList();
        
        for(FlightSchedulePlan flightSchedulePlan: flightSchedulePlans)
        {
            flightSchedulePlan.getFlightSchedules().size();
        }
        return flightSchedulePlans;
    }
    
    
}
