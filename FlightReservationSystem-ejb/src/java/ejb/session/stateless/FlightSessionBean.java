/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfig;
import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedulePlan;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AircraftConfigNameExistException;
import util.exception.FlightNotFoundException;
import util.exception.FlightNumExistException;
import util.exception.FlightRouteExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;
import util.exception.createOutboundReturnFlightCheckException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FlightSessionBean implements FlightSessionBeanRemote, FlightSessionBeanLocal 
{
    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Resource
    private EJBContext eJBContext;
 
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public Long createFlight(Flight newFlight, Long flightRouteId, Long aircraftConfigId) throws FlightNumExistException, UnknownPersistenceException 
    {
        try
        {
            FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);
            AircraftConfig aircraftConfig = em.find(AircraftConfig.class, aircraftConfigId);

            //flight - flight route 
            newFlight.setFlightRoute(flightRoute);
            flightRoute.getFlights().add(newFlight);
            
            //flight - aircraft config
            newFlight.setAircraftConfig(aircraftConfig);
            aircraftConfig.setFlight(newFlight);
            
            //flight - return flight
            newFlight.setReturnFlight(newFlight);

            //flight - flightSchedulePlan
            for(FlightSchedulePlan flightSchedulePlan: newFlight.getFlightSchedulePlans())
            {
                flightSchedulePlan.setFlight(newFlight);
                newFlight.getFlightSchedulePlans().add(flightSchedulePlan);
            }

            em.persist(newFlight);
            em.flush();

            return newFlight.getFlightId();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new FlightNumExistException();
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }    
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @Override
    public Long createReturnFlight(Flight newReturnFlight, Long flightId) throws FlightNumExistException, UnknownPersistenceException
    {
        try
        {
            Flight flight = em.find(Flight.class, flightId);
        
            FlightRoute returnFlightRoute = flight.getFlightRoute().getReturnFlightRoute();

            //return flight - return flight route
            newReturnFlight.setFlightRoute(returnFlightRoute);
            returnFlightRoute.getFlights().add(newReturnFlight);
            
            //return flight - aircraftConfig
            newReturnFlight.setAircraftConfig(flight.getAircraftConfig());
            flight.getAircraftConfig().setFlight(newReturnFlight);
            
            //flight - return flight
            flight.setReturnFlight(newReturnFlight);
            newReturnFlight.setReturnFlight(newReturnFlight);

            for(FlightSchedulePlan flightSchedulePlan: newReturnFlight.getFlightSchedulePlans())
            {
                flightSchedulePlan.setFlight(newReturnFlight);
                newReturnFlight.getFlightSchedulePlans().add(flightSchedulePlan);
            }   

            em.persist(newReturnFlight);
            em.flush();

            return newReturnFlight.getFlightId();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                  
                    throw new FlightNumExistException();
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Long createOutboundReturnFlightCheck(Flight newFlight, Long flightRouteId, Long aircraftConfigId, Flight newReturnFlight) throws createOutboundReturnFlightCheckException
    {
        try
        {
            Long flightId = createFlight(newFlight, flightRouteId, aircraftConfigId);
            createReturnFlight(newReturnFlight, flightId);
            
            return flightId;
        }
        catch(FlightNumExistException | UnknownPersistenceException ex)
        {
            eJBContext.setRollbackOnly();
            throw new createOutboundReturnFlightCheckException(ex.getMessage());
        } 

    }
    
    @Override
    public List<Flight> getAllFlights()
    {
        Query query = em.createQuery("SELECT f FROM Flight f ORDER BY f.flightNumber ASC");
        
        return query.getResultList();
    }
    
    @Override
    public Flight getFlightByFlightNum(String flightNum) throws FlightNotFoundException
    {
        Query query = em.createQuery("SELECT f FROM Flight f WHERE f.flightNumber = :inFlightNum");
        query.setParameter("inFlightNum", flightNum);
        
        try
        {
            Flight flight = (Flight)query.getSingleResult();
            flight.getAircraftConfig();
            flight.getAircraftConfig().getCabinClasses().size();
            flight.getFlightRoute();
            
            return flight;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new FlightNotFoundException("Flight number " + flightNum + " does not exist!");
        }
    }
    
    @Override
    public Flight getFlightById(Long flightId) throws FlightNotFoundException
    {
        Flight flight = em.find(Flight.class, flightId);
        
        if(flight != null)
        {
            flight.getAircraftConfig();
            flight.getFlightRoute();
            flight.getFlightSchedulePlans().size();
            
            return flight;
        }
        else
        {
            throw new FlightNotFoundException("Flight " + flightId + " does not exist!");
        }
        

        
    }
    
    @Override
    public void updateFlight(Flight flight) throws FlightNotFoundException 
    {
        if(flight != null && flight.getFlightId()!= null)
        {
            Flight flightToUpdate = getFlightById(flight.getFlightId());

            flightToUpdate.setFlightRoute(flight.getFlightRoute());
            flightToUpdate.setAircraftConfig(flight.getAircraftConfig());
            
        }
        else
        {
            throw new FlightNotFoundException("Flight ID not provided for flight to be updated");
        }
    }
    
    @Override
    public void removeFlight(Long flightId) throws FlightNotFoundException
    {
        Flight flightRemove = getFlightById(flightId);
        
        flightRemove.getFlightRoute().getFlights().remove(flightRemove);
        
        flightRemove.setReturnFlight(null);
        
        em.remove(flightRemove);
    }
    
    @Override
    public void removeReturnFlight(Long flightId, Long flightIdAssociatedWithReturnFlight) throws FlightNotFoundException
    {
        Flight flightRemove = getFlightById(flightId);
        
        flightRemove.getFlightRoute().getFlights().remove(flightRemove);
        
        flightRemove.setReturnFlight(null);
        
        //if it is return flight, set the flight associated to this return flight to itself
        //disassociate 
        Flight flightAssociatedToReturnFlight = em.find(Flight.class, flightIdAssociatedWithReturnFlight);
        flightAssociatedToReturnFlight.setReturnFlight(flightAssociatedToReturnFlight);

        em.remove(flightRemove);
    }
    
    @Override
    public void setFlightDisabled(Long flightId)
    {
        Flight flightToUpdate = em.find(Flight.class, flightId);
        
        if(flightToUpdate != null)
        {
            flightToUpdate.setEnabled(false);
        } 
    }
       
}
