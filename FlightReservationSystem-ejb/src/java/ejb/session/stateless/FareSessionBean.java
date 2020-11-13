/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.Fare;
import entity.FlightSchedulePlan;
import java.math.BigDecimal;
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
    public Long createNewFare(Fare newFare, Long flightSchedulePlanId, Long cabinClassId) throws FlightSchedulePlanNotFoundException
    {
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
    
    @Override
    public List<Fare> getFaresByFlightSchedulePlanId(Long flightSchedulePlanId)
    {
        Query query = em.createQuery("SELECT f FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inId");
        query.setParameter("inId", flightSchedulePlanId);
        
        return query.getResultList();
    }
    
    @Override
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassId(Long flightSchedulePlanId, Long cabinClassId)
    {
        Query query = em.createQuery("SELECT f FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inFlightSchedulePlanId AND f.cabinClass.cabinClassId = :inCabinClassId");
        query.setParameter("inFlightSchedulePlanId", flightSchedulePlanId);
        query.setParameter("inCabinClassId", cabinClassId);
        
        return query.getResultList();
    }
    
    @Override
    public BigDecimal getLowestFareByFlightSchedulePlanIdAndCabinClassId(Long flightSchedulePlanId, Long cabinClassId)
    {
        Query query = em.createQuery("SELECT MIN(f.fareAmount) FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inFlightSchedulePlanId AND f.cabinClass.cabinClassId = :inCabinClassId");
        query.setParameter("inFlightSchedulePlanId", flightSchedulePlanId);
        query.setParameter("inCabinClassId", cabinClassId);
        
        return (BigDecimal)query.getSingleResult();
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
