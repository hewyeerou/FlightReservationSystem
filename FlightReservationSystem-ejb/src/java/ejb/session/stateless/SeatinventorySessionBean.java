/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.CabinSeatInventory;
import entity.FlightSchedule;
import entity.SeatInventory;
import java.util.Set;
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
import util.exception.FlightScheduleNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class SeatInventorySessionBean implements SeatInventorySessionBeanRemote, SeatInventorySessionBeanLocal {

    @EJB(name = "FlightScheduleSessionBeanLocal")
    private FlightScheduleSessionBeanLocal flightScheduleSessionBeanLocal;

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

 
    public SeatInventorySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createSeatInventory(SeatInventory seatInventory, Long flightScheduleId, Long cabinClassId) throws FlightScheduleNotFoundException, InputDataValidationException
    {   
        Set<ConstraintViolation<SeatInventory>>constraintViolations = validator.validate(seatInventory);
        
        if(constraintViolations.isEmpty())
        {
            FlightSchedule flightSchedule = flightScheduleSessionBeanLocal.getFlightScheduleById(flightScheduleId);
            CabinClass cabinClass = em.find(CabinClass.class, cabinClassId);

            seatInventory.setFlightSchedule(flightSchedule);
            flightSchedule.getSeatInventories().add(seatInventory);

            seatInventory.setCabinClass(cabinClass);
            cabinClass.getSeatInventories().add(seatInventory);


            em.persist(seatInventory);
            em.flush();

            return seatInventory.getSeatInventoryId();
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(Long cabinClassId, Long flightScheduleId) throws SeatInventoryNotFoundException
    {
        Query query = em.createQuery("SELECT si FROM SeatInventory si WHERE si.cabinClass.cabinClassId = :inCabinClassId AND si.flightSchedule.flightScheduleId = :inFlightScheduleId");
        query.setParameter("inCabinClassId", cabinClassId);
        query.setParameter("inFlightScheduleId", flightScheduleId);
        
        try
        {
            SeatInventory si = (SeatInventory)query.getSingleResult();
            si.getCabinSeatInventories().size();
            return si;
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new SeatInventoryNotFoundException();
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<SeatInventory>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}

