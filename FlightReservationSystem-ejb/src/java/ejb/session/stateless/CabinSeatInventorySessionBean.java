/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import entity.Passenger;
import entity.SeatInventory;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CabinSeatInventoryExistException;
import util.exception.InputDataValidationException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class CabinSeatInventorySessionBean implements CabinSeatInventorySessionBeanRemote, CabinSeatInventorySessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
     
    public CabinSeatInventorySessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventory(Long seatInventoryId)
    {
        Query query = em.createQuery("SELECT c FROM CabinSeatInventory c WHERE c.seatInventory.seatInventoryId = :inSeatInventoryId ORDER BY c.seatTaken ASC");
        query.setParameter("inSeatInventoryId", seatInventoryId);
        
        return query.getResultList();
    } 
    
    @Override
    public Long createNewCabinSeatInventory(CabinSeatInventory cabinSeatInventory, Long seatInventoryId, Long passengerId) throws CabinSeatInventoryExistException, InputDataValidationException
    {
        Query query = em.createQuery("SELECT c FROM CabinSeatInventory c WHERE c.seatInventory.seatInventoryId = :inSeatInventoryId AND c.seatTaken = :inReserveSeat");
        query.setParameter("inSeatInventoryId", seatInventoryId);
        query.setParameter("inReserveSeat", cabinSeatInventory.getSeatTaken());
        
        try
        {
            query.getSingleResult();
            throw new CabinSeatInventoryExistException("Cabin seat number is already taken, please select another seat!");
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            Set<ConstraintViolation<CabinSeatInventory>> constraintViolations = validator.validate(cabinSeatInventory);
            
            if(constraintViolations.isEmpty())
            {
                SeatInventory seatInventory = em.find(SeatInventory.class, seatInventoryId);
                Passenger passenger = em.find(Passenger.class, passengerId);

                cabinSeatInventory.setSeatInventory(seatInventory);
                seatInventory.getCabinSeatInventories().add(cabinSeatInventory);
                seatInventory.setNumOfBalanceSeats(seatInventory.getNumOfBalanceSeats() - 1);
                seatInventory.setNumOfReservedSeats(seatInventory.getNumOfReservedSeats() + 1);
                passenger.getCabinSeats().add(cabinSeatInventory);
                em.persist(cabinSeatInventory);
                em.flush();
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        
        return cabinSeatInventory.getCabinSeatInventoryId();
    }
    
    private String prepareInputDataValidationErrorsMessage (Set<ConstraintViolation<CabinSeatInventory>> constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
