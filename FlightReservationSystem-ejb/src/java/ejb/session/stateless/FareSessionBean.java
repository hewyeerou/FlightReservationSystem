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
import java.util.Set;
import javax.ejb.EJB;
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
import util.exception.FareBasisCodeExistException;
import util.exception.FareNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InputDataValidationException;
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
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FareSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewFare(Fare newFare, Long flightSchedulePlanId, Long cabinClassId) throws FlightSchedulePlanNotFoundException, InputDataValidationException
    {
        Set<ConstraintViolation<Fare>>constraintViolations = validator.validate(newFare);
        
        if(constraintViolations.isEmpty())
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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
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
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassId(Long flightSchedulePlanId, Long cabinClassId)
    {
        Query query = em.createQuery("SELECT f FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inFlightSchedulePlanId AND f.cabinClass.cabinClassId = :inCabinClassId");
        query.setParameter("inFlightSchedulePlanId", flightSchedulePlanId);
        query.setParameter("inCabinClassId", cabinClassId);
        
        return query.getResultList();
    }
    
    @Override
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(Long flightSchedulePlanId, Long cabinClassId)
    {
        List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassId(flightSchedulePlanId, cabinClassId);
        
        for (Fare fare: fares)
        {
            em.detach(fare);
            
            em.detach(fare.getCabinClass());
            
            em.detach(fare.getFlightSchedulePlan());
        }
        
        return fares;
    }
    
    @Override
    public BigDecimal getLowestFareByFlightSchedulePlanIdAndCabinClassId(Long flightSchedulePlanId, Long cabinClassId) throws FareNotFoundException
    {
        Query query = em.createQuery("SELECT MIN(f.fareAmount) FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inFlightSchedulePlanId AND f.cabinClass.cabinClassId = :inCabinClassId");
        query.setParameter("inFlightSchedulePlanId", flightSchedulePlanId);
        query.setParameter("inCabinClassId", cabinClassId);
        
        try
        {
            return (BigDecimal)query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new FareNotFoundException("Lowest fare for current flight schedule plan and cabin class is not available!");
        }
    }
    
    @Override
    public BigDecimal getHighestFareByFlightSchedulePlanIdAndCabinClassId(Long flightSchedulePlanId, Long cabinClassId) throws FareNotFoundException
    {
        Query query = em.createQuery("SELECT MAX(f.fareAmount) FROM Fare f WHERE f.flightSchedulePlan.flightSchedulePlanId = :inFlightSchedulePlanId AND f.cabinClass.cabinClassId = :inCabinClassId");
        query.setParameter("inFlightSchedulePlanId", flightSchedulePlanId);
        query.setParameter("inCabinClassId", cabinClassId);
        
        try
        {
            return (BigDecimal)query.getSingleResult();
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new FareNotFoundException("Highest fare for current flight schedule plan and cabin class is not available!");
        }
    }
  
    @Override
    public BigDecimal getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(Long flightSchedulePlanId, Long cabinClassId) throws FareNotFoundException
    {
        BigDecimal price = getHighestFareByFlightSchedulePlanIdAndCabinClassId(flightSchedulePlanId, cabinClassId);
        
        return price;
    }
    
    @Override
    public void updateFare(Fare fare) throws FareNotFoundException, InputDataValidationException
    {
        if(fare != null)
        {
            Set<ConstraintViolation<Fare>>constraintViolations = validator.validate(fare);
            
            if(constraintViolations.isEmpty())
            {
                Fare fareToUpdate = em.find(Fare.class, fare.getFareId());
                fareToUpdate.setFareAmount(fare.getFareAmount());
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new FareNotFoundException("Fare ID not provided for fare to be updated");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Fare>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
