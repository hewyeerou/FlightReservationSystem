/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.Fare;
import entity.FlightSchedulePlan;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.AircraftTypeNameExistException;
import util.exception.FareBasisCodeExistException;
import util.exception.FareNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FareSessionBean implements FareSessionBeanRemote, FareSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @EJB(name = "FlightSchedulePlanSessionBeanLocal")
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBeanLocal;

    @Override
    public Long createNewFare(Fare newFare, Long flightSchedulePlanId, Long cabinClassId) throws FlightSchedulePlanNotFoundException, FareBasisCodeExistException, UnknownPersistenceException
    {
        try{
            FlightSchedulePlan flightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(flightSchedulePlanId);
            CabinClass cabinClass = em.find(CabinClass.class, cabinClassId);

            //set cabinclass
            newFare.setCabinClass(cabinClass);
            //set flightscheduleplan
            newFare.setFlightSchedulePlan(flightSchedulePlan);

            em.persist(newFare);
            em.flush();

            return newFare.getFareId();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new FareBasisCodeExistException("Fare basis code exist!");
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
    public List<Fare> getFaresByFlightSchedulePlanId(Long flightSchedulePlanId)
    {
        Query query = em.createQuery("SELECT f FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inId");
        query.setParameter("inId", flightSchedulePlanId);
        
        return query.getResultList();
    }
    
    @Override
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassId(Long flightSchedulePlanId, Long cabinClassId) throws FareNotFoundException
    {
        Query query = em.createQuery("SELECT f FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inFlightSchedulePlanId AND f.cabinClass.cabinClassId = :inCabinClassId");
        query.setParameter("inFlightSchedulePlanId", flightSchedulePlanId);
        query.setParameter("inCabinClassId", cabinClassId);
        
        return query.getResultList();
    }
  
    @Override
    public void updateFare(Fare fare) throws FareNotFoundException
    {
        if(fare != null)
        {
            Fare fareToUpdate = em.find(Fare.class, fare.getFareId());
            fareToUpdate.setFareAmount(fare.getFareAmount());
        }
        else
        {
            throw new FareNotFoundException("Fare ID not provided for fare to be updated");
        }
    }
}
