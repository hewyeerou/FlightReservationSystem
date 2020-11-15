/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import entity.FlightReservationRecord;
import entity.Passenger;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.PassengerNotFoundException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class PassengerSessionBean implements PassengerSessionBeanRemote, PassengerSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
     
    public PassengerSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Long createNewPassenger(Passenger passenger, Long flightReservationRecordId) throws InputDataValidationException
    {
        Set<ConstraintViolation<Passenger>>constraintViolations = validator.validate(passenger);
        
        if (constraintViolations.isEmpty())
        {
            FlightReservationRecord flightReservationRecord = em.find(FlightReservationRecord.class, flightReservationRecordId);
            passenger.setFlightReservationRecord(flightReservationRecord);
            flightReservationRecord.getPassengers().add(passenger);

            em.persist(passenger);
            em.flush();

            return passenger.getPassengerId();
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Passenger retrievePassengerByPassengerId (Long passengerId) throws PassengerNotFoundException
    {
        Passenger passenger = em.find(Passenger.class, passengerId);
        
        if (passenger != null)
        {
            passenger.getCabinSeats().size();
            return passenger;
        }
        else
        {
            throw new PassengerNotFoundException("Passenger with ID " + passengerId + " does not exist!\n");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Passenger>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
