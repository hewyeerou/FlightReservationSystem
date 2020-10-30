/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import entity.Flight;
import entity.FlightRoute;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.FlightRouteExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FlightRouteSessionBean implements FlightRouteSessionBeanRemote, FlightRouteSessionBeanLocal 
{

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    
    @Override
    public Long createNewFlightRoute(FlightRoute flightRoute, Long originAirportId, Long destinationAirportId) throws FlightRouteExistException, UnknownPersistenceException
    {
        try
        {
            //check duplicate record
//            List<FlightRoute> flightRoutes = getAllFlightRoute();
//            
//            List<Long> originAirports = new ArrayList<>();
//            List<Long> destinationAirports = new ArrayList<>();
//            
//            for(FlightRoute fr: flightRoutes)
//            {
//                originAirports.add(fr.getOrigin().getAirportId());
//                destinationAirports.add(fr.getDestination().getAirportId());
//            }
            
//            if(!originAirports.contains(flightRoute.getOrigin().getAirportId()) || !destinationAirports.contains(flightRoute.getDestination().getAirportId()))
//            {
                Airport originAirport = em.find(Airport.class, originAirportId);
                Airport destinationAirport = em.find(Airport.class, destinationAirportId);

                flightRoute.setOrigin(originAirport);
                originAirport.getDepartureRoutes().add(flightRoute);
                flightRoute.setDestination(destinationAirport);
                destinationAirport.getArrivalRoutes().add(flightRoute);
                
                flightRoute.setReturnFlightRoute(flightRoute);

                for(Flight flight: flightRoute.getFlights())
                {
                    flight.setFlightRoute(flightRoute);
                    flightRoute.getFlights().add(flight);
                    em.persist(flight);
                }

                em.persist(flightRoute);
                em.flush();
                
                return flightRoute.getFlightRouteId();
//            }
//            else
//            {
//                throw new FlightRouteExistException("Flight route already exist!");
//            }  
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new FlightRouteExistException();
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
    public Long createNewReturnFlightRoute(FlightRoute returnFlightRoute, Long flightRouteId) throws FlightRouteExistException, UnknownPersistenceException
    {
        try
        {
//            List<FlightRoute> flightRoutes = getAllFlightRoute();
            
//            if(!flightRoutes.contains(returnFlightRoute))
//            {
                FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);

                Airport originAirport = flightRoute.getDestination();
                Airport destinationAirport = flightRoute.getOrigin();

                returnFlightRoute.setOrigin(originAirport);
                returnFlightRoute.setDestination(destinationAirport);

                originAirport.getDepartureRoutes().add(returnFlightRoute);
                destinationAirport.getArrivalRoutes().add(returnFlightRoute);

                flightRoute.setReturnFlightRoute(returnFlightRoute);
                returnFlightRoute.setReturnFlightRoute(returnFlightRoute);

                for(Flight flight: returnFlightRoute.getFlights())
                {
                    flight.setFlightRoute(returnFlightRoute);
                    returnFlightRoute.getFlights().add(flight);
                    em.persist(flight);
                }

                em.persist(returnFlightRoute);
                em.flush();

                return returnFlightRoute.getFlightRouteId();  
//            }
//            else
//            {
//                throw new FlightRouteExistException("Flight route already exist!");
//            }  
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new FlightRouteExistException();
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
    public List<FlightRoute> getAllFlightRoute()
    {
        Query query = em.createQuery("SELECT f FROM FlightRoute f ORDER BY f.origin ASC");
        
        return query.getResultList();
    }
    
    @Override
    public FlightRoute getFlightRouteById(Long flightRouteId, Boolean fetchAirport, Boolean fetchFlights) throws FlightRouteNotFoundException
    {
        FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);
        
        if(flightRoute != null)
        {
            if(fetchAirport)
            {
                flightRoute.getOrigin();
                flightRoute.getDestination();
            }
            
            if(fetchFlights)
            {
                flightRoute.getFlights().size();
            }
            
            return flightRoute;
        }
        else
        {
            throw new FlightRouteNotFoundException("Flight Route " + flightRouteId + " does not exist!");
        }
       
    }
    
    @Override
    public void removeFlightRoute(Long flightRouteId) throws FlightRouteNotFoundException
    {
        FlightRoute flightRouteRemove = getFlightRouteById(flightRouteId, false, false);
        
        //outbound
        flightRouteRemove.setOrigin(null);
        flightRouteRemove.setDestination(null);
                
        //return
        flightRouteRemove.getReturnFlightRoute().setOrigin(null);
        flightRouteRemove.getReturnFlightRoute().setDestination(null);
        
        flightRouteRemove.setReturnFlightRoute(null);
            
        for(Flight flight: flightRouteRemove.getFlights())
        {
            flight.setFlightRoute(null);
        }
            
        flightRouteRemove.getFlights().clear();
            
        em.remove(flightRouteRemove);

    }
    
    
    @Override
    public void setFlightRouteDisabled(Long flightRouteId)
    {
        FlightRoute flightRouteToUpdate = em.find(FlightRoute.class, flightRouteId);
        
        if(flightRouteToUpdate != null)
        {
            flightRouteToUpdate.setEnabled(false);
        }       
    }
    
    
    
}
