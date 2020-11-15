/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import entity.Customer;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.Partner;
import entity.Passenger;
import entity.Person;
import entity.SeatInventory;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.FlightReservationRecordNotFoundException;
import util.exception.InputDataValidationException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class FlightReservationRecordSessionBean implements FlightReservationRecordSessionBeanRemote, FlightReservationRecordSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public FlightReservationRecordSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewFlightReservationRecord(FlightReservationRecord flightReservationRecord, Long personId, List<Long> flightSchedules) throws InputDataValidationException
    {
        Set<ConstraintViolation<FlightReservationRecord>>constraintViolations = validator.validate(flightReservationRecord);
        
        if(constraintViolations.isEmpty())
        {
            Person person = em.find(Person.class, personId);
            flightReservationRecord.setPerson(person);

            if (person instanceof Customer)
            {
                Customer customer = (Customer)person;
                customer.getFlightReservationRecords().add(flightReservationRecord);
            }
            else if (person instanceof Partner)
            {
                Partner partner = (Partner)person;
                partner.getFlightReservationRecords().add(flightReservationRecord);
            }

            for (Long fs: flightSchedules)
            {
                FlightSchedule flightS = em.find(FlightSchedule.class, fs);
                flightReservationRecord.getFlightSchedules().add(flightS);
                flightS.getFlightReservationRecords().add(flightReservationRecord);
            }

            em.persist(flightReservationRecord);
            em.flush();
            return flightReservationRecord.getRecordId();
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<FlightReservationRecord> retrieveReservationRecordsByCustomerId (Long customerId)
    {
        Query query = em.createQuery("SELECT frr FROM FlightReservationRecord frr WHERE frr.person.id = :inCustomerId ORDER BY frr.recordId ASC");
        query.setParameter("inCustomerId", customerId);
        
        List<FlightReservationRecord> reservations = query.getResultList();
        
        for (FlightReservationRecord frr: reservations)
        {
            frr.getFlightSchedules().size();
            frr.getPassengers().size();
        }
        
        return reservations;
    }
    
    @Override
    public FlightReservationRecord retrieveReservationRecordById (Long recordId, Long personId) throws FlightReservationRecordNotFoundException
    {
        FlightReservationRecord record = em.find(FlightReservationRecord.class, recordId);
        Person person = em.find(Person.class, personId);
        
        if (record != null)
        {
            Person customer = record.getPerson();
        
            if (person.equals(customer))
            {
                record.getFlightSchedules().size();
                record.getPassengers().size();

                return record;
            }
            else
            {
                throw new FlightReservationRecordNotFoundException("Flight Reservation Record with ID " + recordId + " cannot be viewed as it is not made by you!");
            }
        }
        else
        {
            throw new FlightReservationRecordNotFoundException("Flight Reservation Record with ID " + recordId + " does not exist!");
        }
    }
    
    @Override
    public FlightReservationRecord getFlightReservationRecordByFlightScheduleId(Long flightReservationRecordId)
    {
        Query query = em.createQuery("SELECT frr FROM FlightReservationRecord frr WHERE frr.recordId = :inId");
        query.setParameter("inId", flightReservationRecordId);
        
        FlightReservationRecord flightReservationRecord = (FlightReservationRecord)query.getSingleResult();
        flightReservationRecord.getFlightSchedules().size();
        flightReservationRecord.getPassengers().size();
        
        for(Passenger passenger: flightReservationRecord.getPassengers())
        {
            passenger.getCabinSeats().size();

            for(CabinSeatInventory cabinSeatInventory: passenger.getCabinSeats())
            {
                SeatInventory seatInventory = cabinSeatInventory.getSeatInventory();
                seatInventory.getCabinClass().getFares().size();
            }
        }
        
        
        return flightReservationRecord;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightReservationRecord>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
