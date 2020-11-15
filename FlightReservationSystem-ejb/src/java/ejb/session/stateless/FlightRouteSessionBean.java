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
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.FlightRouteExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.InputDataValidationException;
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
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FlightRouteSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    
    @Override
    public Long createNewFlightRoute(FlightRoute flightRoute, Long originAirportId, Long destinationAirportId) throws FlightRouteExistException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<FlightRoute>>constraintViolations = validator.validate(flightRoute);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                //check duplicate record
                List<FlightRoute> flightRoutes = getAllFlightRoute();
                List<String> flightRoutesPair = new ArrayList<>();

                for(FlightRoute route: flightRoutes)
                {
                    flightRoutesPair.add(route.getOrigin() + "-" + route.getDestination());
                }
                String routePair = flightRoute.getOrigin() + "-" + flightRoute.getDestination();

                if(!flightRoutesPair.contains(routePair))
                {
                    Airport originAirport = em.find(Airport.class, originAirportId);
                    Airport destinationAirport = em.find(Airport.class, destinationAirportId);

                    //flightRoute - origin airport
                    flightRoute.setOrigin(originAirport);
                    originAirport.getDepartureRoutes().add(flightRoute);

                    //flightRoute - destination airport
                    flightRoute.setDestination(destinationAirport);
                    destinationAirport.getArrivalRoutes().add(flightRoute);

                    //flightRoute - returnflightRoute
                    flightRoute.setReturnFlightRoute(flightRoute);

                    for(Flight flight: flightRoute.getFlights())
                    {
                        flight.setFlightRoute(flightRoute);
                        flightRoute.getFlights().add(flight);
                    }

                    em.persist(flightRoute);
                    em.flush();

                    return flightRoute.getFlightRouteId();
                }
                else
                {
                    throw new FlightRouteExistException("Flight route already exist!");
                }  
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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        
    }
    
    @Override
    public Long createNewReturnFlightRoute(FlightRoute returnFlightRoute, Long flightRouteId) throws FlightRouteExistException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<FlightRoute>>constraintViolations = validator.validate(returnFlightRoute);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                    FlightRoute flightRoute = em.find(FlightRoute.class, flightRouteId);

                    Airport originAirport = flightRoute.getDestination();
                    Airport destinationAirport = flightRoute.getOrigin();

                    //returnFlightRoute - originAirport
                    returnFlightRoute.setOrigin(originAirport);
                    originAirport.getDepartureRoutes().add(returnFlightRoute);

                    //returnFlightRoute - destinationAirport
                    returnFlightRoute.setDestination(destinationAirport);
                    destinationAirport.getArrivalRoutes().add(returnFlightRoute);

                    //flightRoute - returnFlightRoute
                    flightRoute.setReturnFlightRoute(returnFlightRoute);
                    returnFlightRoute.setReturnFlightRoute(returnFlightRoute);

                    for(Flight flight: returnFlightRoute.getFlights())
                    {
                        flight.setFlightRoute(returnFlightRoute);
                        returnFlightRoute.getFlights().add(flight);
                    }

                    em.persist(returnFlightRoute);
                    em.flush();

                    return returnFlightRoute.getFlightRouteId();  
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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
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
        FlightRoute flightRouteRemove = getFlightRouteById(flightRouteId, true, true);
        flightRouteRemove.getOrigin().getDepartureRoutes().remove(flightRouteRemove);
        flightRouteRemove.getDestination().getArrivalRoutes().remove(flightRouteRemove);
        
        FlightRoute returnFlightRouteRemove = getFlightRouteById(flightRouteRemove.getReturnFlightRoute().getFlightRouteId(), true, true);
        returnFlightRouteRemove.getOrigin().getDepartureRoutes().remove(returnFlightRouteRemove);
        returnFlightRouteRemove.getDestination().getArrivalRoutes().remove(returnFlightRouteRemove);
       
        flightRouteRemove.setReturnFlightRoute(null);
        returnFlightRouteRemove.setReturnFlightRoute(null);
        
        em.remove(flightRouteRemove);
        em.remove(returnFlightRouteRemove);
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
    
    @Override
    public FlightRoute getFlightRouteByFlightId (Long frId) throws FlightRouteNotFoundException
    {
        Query query = em.createQuery("SELECT fr FROM FlightRoute fr WHERE fr.flightRouteId = :inFlightRouteId");
        query.setParameter("inFlightRouteId", frId);
        
        try
        {
            return (FlightRoute)query.getSingleResult();
        }
        catch (NonUniqueResultException | NoResultException ex)
        {
            throw new FlightRouteNotFoundException("Flight route for the specific flight cannot be found!\n");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightRoute>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
