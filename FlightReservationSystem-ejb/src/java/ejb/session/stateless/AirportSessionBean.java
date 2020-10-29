/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AirportIataCodeExistException;
import util.exception.AirportNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class AirportSessionBean implements AirportSessionBeanRemote, AirportSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewAirport(Airport newAirport) throws AirportIataCodeExistException, UnknownPersistenceException
    {
        try
        {
            em.persist(newAirport);
            em.flush();
            
            return newAirport.getAirportId();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new AirportIataCodeExistException();
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
    public List<Airport> getAllAirports()
    {
        Query query = em.createQuery("SELECT a FROM Airport a ORDER BY a.airportId ASC");
        
        return query.getResultList();
    }
    
    @Override
    public Airport getAirportByAirportId(Long airportId) throws AirportNotFoundException
    {
        Airport airport = em.find(Airport.class, airportId);
        
        if(airport != null)
        {
            return airport;
        }
        else
        {
            throw new AirportNotFoundException("Airport Id " + airportId + " does not exist!");
        }
    }
}
