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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.AircraftConfigNameExistException;
import util.exception.FlightNumExistException;
import util.exception.FlightRouteExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FlightSessionBean implements FlightSessionBeanRemote, FlightSessionBeanLocal 
{
    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createFlight(Flight newFlight, Long flightRouteId, Long aircraftConfigId) throws FlightNumExistException, UnknownPersistenceException 
    {
        try
        {
            FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);
            AircraftConfig aircraftConfig = em.find(AircraftConfig.class, aircraftConfigId);

            newFlight.setFlightRoute(flightRoute);
            newFlight.setAircraftConfig(aircraftConfig);

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
    
    @Override
    public Long createReturnFlight(Flight newReturnFlight, Long flightId) throws FlightNumExistException, UnknownPersistenceException
    {
        try
        {
            Flight flight = em.find(Flight.class, flightId);
        
            FlightRoute returnFlightRoute = flight.getFlightRoute().getReturnFlightRoute();

            newReturnFlight.setFlightRoute(returnFlightRoute);
            newReturnFlight.setAircraftConfig(flight.getAircraftConfig());

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
}
