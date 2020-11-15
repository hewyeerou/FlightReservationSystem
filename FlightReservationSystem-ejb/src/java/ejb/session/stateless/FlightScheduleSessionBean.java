/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import entity.CabinClass;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.CabinClassEnum;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InputDataValidationException;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class FlightScheduleSessionBean implements FlightScheduleSessionBeanRemote, FlightScheduleSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBeanLocal;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public FlightScheduleSessionBean() 
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    

    @Override
    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException, InputDataValidationException
    {
        Set<ConstraintViolation<FlightSchedule>>constraintViolations = validator.validate(flightSchedule);
        
        if(constraintViolations.isEmpty())
        {
            FlightSchedulePlan flightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(flightSchedulePlanId);

            flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
            flightSchedulePlan.getFlightSchedules().add(flightSchedule);

            for (FlightReservationRecord flightReservationRecord : flightSchedule.getFlightReservationRecords())
            {
                flightReservationRecord.getFlightSchedules().add(flightSchedule);

                for (FlightSchedule fs : flightReservationRecord.getFlightSchedules()) {
                    fs.getFlightReservationRecords().add(flightReservationRecord);
                }
            }

            flightSchedule.setReturnFlightSchedule(flightSchedule);

            em.persist(flightSchedule);
            em.flush();

            return flightSchedule.getFlightScheduleId();
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException, InputDataValidationException
    {
        Set<ConstraintViolation<FlightSchedule>>constraintViolations = validator.validate(returnFlightSchedule);
        
        if(constraintViolations.isEmpty())
        {
            FlightSchedulePlan returnFlightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(returnFlightSchedulePlanId);

            FlightSchedule flightSchedule = getFlightScheduleById(flightScheduleId);

            returnFlightSchedule.setFlightSchedulePlan(returnFlightSchedulePlan);
            returnFlightSchedulePlan.getFlightSchedules().add(returnFlightSchedule);


            for(FlightReservationRecord flightReservationRecord: returnFlightSchedule.getFlightReservationRecords())
            {
                flightReservationRecord.getFlightSchedules().add(returnFlightSchedule);

                for(FlightSchedule rfs: flightReservationRecord.getFlightSchedules()){
                    rfs.getFlightReservationRecords().add(flightReservationRecord);
                }
            }

            flightSchedule.setReturnFlightSchedule(returnFlightSchedule);
            returnFlightSchedule.setReturnFlightSchedule(returnFlightSchedule);

            em.persist(returnFlightSchedule);
            em.flush();

            return returnFlightSchedule.getFlightScheduleId();
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    
    @Override
    public FlightSchedule getFlightScheduleById(Long flightScheduleId) throws FlightScheduleNotFoundException 
    {
        FlightSchedule flightSchedule = em.find(FlightSchedule.class, flightScheduleId);
        
        if(flightSchedule != null)
        {
            flightSchedule.getFlightReservationRecords().size();
            flightSchedule.getFlightSchedulePlan();
            flightSchedule.getSeatInventories().size();
            
            return flightSchedule;
        }
        else
        {
            throw new FlightScheduleNotFoundException("Flight Schedule " + flightScheduleId + " does not exist!");
        }
    }
    
    @Override
    public FlightSchedule getFlightScheduleByIdUnmanaged(Long flightScheduleId) throws FlightScheduleNotFoundException
    {
        FlightSchedule fs = getFlightScheduleById(flightScheduleId);
        
        em.detach(fs);
        
        em.detach(fs.getFlightSchedulePlan());
        
        em.detach(fs.getReturnFlightSchedule());
        
        for (SeatInventory si: fs.getSeatInventories())
        {
            em.detach(si);
        }
        
        for (FlightReservationRecord record: fs.getFlightReservationRecords())
        {
            em.detach(record);
        }
        
        return fs;
    }
    
    @Override
    public List<FlightSchedule> getFlightScheduleByFlightSchedulePlanId(Long flightSchedulePlanId)
    {
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flightSchedulePlanId = :inId");
        query.setParameter("inId", flightSchedulePlanId);
        List<FlightSchedule> flightSchedules = query.getResultList();
        
        for(FlightSchedule flightSchedule: flightSchedules)
        {
            flightSchedule.getFlightReservationRecords().size();
            flightSchedule.getSeatInventories().size();
            flightSchedule.getFlightSchedulePlan();
        }
        
        return flightSchedules;
    }
    
    @Override
    public void updateFlightSchedule(FlightSchedule flightSchedule) throws FlightScheduleNotFoundException, InputDataValidationException
    {
        if(flightSchedule != null)
        {
            Set<ConstraintViolation<FlightSchedule>>constraintViolations = validator.validate(flightSchedule);
            
            if(constraintViolations.isEmpty())
            {
                FlightSchedule flightScheduleToUpdate = getFlightScheduleById(flightSchedule.getFlightScheduleId());
                flightScheduleToUpdate.setDepartureDateTime(flightSchedule.getDepartureDateTime());
                flightScheduleToUpdate.setFlightHours(flightSchedule.getFlightHours());
                flightScheduleToUpdate.setFlightMinutes(flightSchedule.getFlightMinutes());
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new FlightScheduleNotFoundException("Flight Schedule Plan does not exist!");
        }
    }
    
    @Override
    public void removeFlightSchedule(Long flightScheduleId) throws FlightScheduleNotFoundException
    {
        FlightSchedule flightScheduleToRemove = em.find(FlightSchedule.class, flightScheduleId);
        
        List<SeatInventory> seatInventories = new ArrayList<>(flightScheduleToRemove.getSeatInventories());
        for(SeatInventory seatInventory: seatInventories)
        {
            flightScheduleToRemove.getSeatInventories().remove(seatInventory);
            SeatInventory seatInventory1 = em.merge(seatInventory);
            seatInventory1.setCabinClass(null);
            em.remove(seatInventory1);
            
//            List<CabinClass> cabinClasses = new ArrayList<>(seatInventory1.getCabinClasses());
//            for(CabinClass cabinClass: cabinClasses)
//            {
//                seatInventory1.getCabinClasses().remove(cabinClass);
//                cabinClass.getSeatInventories().remove(seatInventory1);
//                CabinClass cabinClass1 = em.merge(cabinClass);
//            }
        }

       
        flightScheduleToRemove.getSeatInventories().clear();
        em.remove(flightScheduleToRemove);
        
    }

    @Override
    public List<FlightSchedule> searchDirectFlightSchedules(Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Airport departureAirport = em.find(Airport.class, departureAirportId);
        Airport destinationAirport = em.find(Airport.class, destinationAirportId);
        
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :inDateStart AND fs.departureDateTime < :inDateEnd AND fs.flightSchedulePlan.flight.flightRoute.origin = :inDepartureAirport AND fs.flightSchedulePlan.flight.flightRoute.destination = :inDestinationAirport ORDER BY fs.departureDateTime ASC");
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        query.setParameter("inDepartureAirport", departureAirport);
        query.setParameter("inDestinationAirport", destinationAirport);
        
        List<FlightSchedule> flightSchedules = query.getResultList();
        // System.out.println(flightSchedules.size());
        List<FlightSchedule> finalFlightSchedules = new ArrayList<>();
        for (FlightSchedule fs: flightSchedules)
        {
            fs.getSeatInventories().size();
        }
        
        for (FlightSchedule fs: flightSchedules)
        {
            if (filterFlightSchedule(fs, preferredCabinClass, numPassengers))
            {
                finalFlightSchedules.add(fs);
            }
        }
        
        for (FlightSchedule fs1: flightSchedules)
        {
            fs1.getSeatInventories().size();
            fs1.getFlightSchedulePlan().getFares().size();
        }
      
        return finalFlightSchedules;
    }
    
    @Override
    public List<FlightSchedule> searchDirectFlightSchedulesUnmanaged(Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        List<FlightSchedule> flightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        for (FlightSchedule fs: flightSchedules)
        {
            em.detach(fs);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                em.detach(si);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                em.detach(fr);
            }
            
            em.detach(fs.getFlightSchedulePlan());
            em.detach(fs.getReturnFlightSchedule());
        }
        
        return flightSchedules;
    }
    
    @Override
    public List<FlightSchedule> searchSingleTransitConnectingFlightSchedule (Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        List<FlightSchedule> connectingFlights = new ArrayList<>();
        
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        System.out.println(format.format(dateStart));
        System.out.println(format.format(dateEnd));
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :inDateStart AND fs.departureDateTime < :inDateEnd AND fs.flightSchedulePlan.flight.flightRoute.origin.airportId = :inDepartureAirportId AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId <> :inDestinationAirportId ORDER BY fs.departureDateTime ASC");
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        query.setParameter("inDepartureAirportId", departureAirportId);
        query.setParameter("inDestinationAirportId", destinationAirportId);
        List<FlightSchedule> firstLeg = query.getResultList();
        System.out.println(firstLeg.size());
        
        if (!firstLeg.isEmpty())
        {
            List<Long> transitAirportIds = new ArrayList<>();
            for (FlightSchedule flightSchedule: firstLeg)
            {
                transitAirportIds.add(flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportId());
            }

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(dateEnd);
            calendar.add(GregorianCalendar.HOUR_OF_DAY, 10);
            dateEnd = calendar.getTime();
            System.out.println(format.format(dateStart));
            System.out.println(format.format(dateEnd));

            query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight.flightRoute.origin.airportId IN :inAirportIds AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId = :inDestinationAirportId AND fs.departureDateTime > :inDateStart AND fs.departureDateTime < :inDateEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("inAirportIds", transitAirportIds);
            query.setParameter("inDestinationAirportId", destinationAirportId);
            query.setParameter("inDateStart", dateStart);
            query.setParameter("inDateEnd", dateEnd);
            List<FlightSchedule> secondLeg = query.getResultList();

            for (FlightSchedule firstFlightSchedule: firstLeg)
            {
                Airport transitAirport = firstFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination();
                Airport originAirport = em.find(Airport.class, departureAirportId);
                Integer timeZoneDiff = transitAirport.getTimeZoneDiff() - originAirport.getTimeZoneDiff();

                for (FlightSchedule secondFlightSchedule: secondLeg)
                {
                    if (secondFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().equals(transitAirport))
                    {
                        calendar = new GregorianCalendar();
                        calendar.setTime(firstFlightSchedule.getDepartureDateTime());
                        calendar.add(GregorianCalendar.HOUR_OF_DAY, firstFlightSchedule.getFlightHours()+2);
                        calendar.add(GregorianCalendar.MINUTE, firstFlightSchedule.getFlightMinutes());
                        // Account for difference in time zone
                        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);

                        if (secondFlightSchedule.getDepartureDateTime().compareTo(calendar.getTime()) >= 0)
                        {
                            if (filterFlightSchedule(firstFlightSchedule, preferredCabinClass, numPassengers) &&
                                filterFlightSchedule(secondFlightSchedule, preferredCabinClass, numPassengers) &&
                                firstFlightSchedule.getEnabled() && secondFlightSchedule.getEnabled())
                            {
                                connectingFlights.add(firstFlightSchedule);
                                connectingFlights.add(secondFlightSchedule);
                            }
                        }
                    }
                }
            }

            for (FlightSchedule fs:connectingFlights)
            {
                fs.getSeatInventories().size();
                fs.getFlightSchedulePlan().getFares().size();
            }
        }
        return connectingFlights;
    }
    
    @Override
    public List<FlightSchedule> searchSingleTransitConnectingFlightScheduleUnmanaged (Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        List<FlightSchedule> flightSchedules = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        for (FlightSchedule fs: flightSchedules)
        {
            em.detach(fs);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                em.detach(si);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                em.detach(fr);
            }
            
            em.detach(fs.getFlightSchedulePlan());
            em.detach(fs.getReturnFlightSchedule());
        }
        
        return flightSchedules;
    }
    
    @Override
    public List<FlightSchedule> searchDoubleTransitConnectingFlightSchedule (Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        List<FlightSchedule> connectingFlights = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :inDateStart AND fs.departureDateTime < :inDateEnd AND fs.flightSchedulePlan.flight.flightRoute.origin.airportId = :inDepartureAirportId AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId <> :inDestinationAirportId ORDER BY fs.departureDateTime ASC");
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        query.setParameter("inDepartureAirportId", departureAirportId);
        query.setParameter("inDestinationAirportId", destinationAirportId);
        List<FlightSchedule> firstLeg = query.getResultList();
        
        if (!firstLeg.isEmpty())
        {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(dateEnd);
            calendar.add(GregorianCalendar.HOUR_OF_DAY, 10);
            dateEnd = calendar.getTime();

            List<Long> transitAirportIds = new ArrayList<>();
            for (FlightSchedule flightScheduleOne: firstLeg)
            {
                transitAirportIds.add(flightScheduleOne.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportId());
            }

            query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight.flightRoute.origin.airportId IN :inAirportIds AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId <> :inDestinationAirportId AND fs.departureDateTime > :inDateStart AND fs.departureDateTime <= :inDateEnd ORDER BY fs.departureDateTime ASC");
            query.setParameter("inDestinationAirportId", destinationAirportId);
            query.setParameter("inAirportIds", transitAirportIds);
            query.setParameter("inDateStart", dateStart);
            query.setParameter("inDateEnd", dateEnd);
            List<FlightSchedule> secondLeg = query.getResultList();
            
            if (!secondLeg.isEmpty())
            {
                calendar = new GregorianCalendar();
                calendar.setTime(dateEnd);
                calendar.add(GregorianCalendar.HOUR_OF_DAY, 10);
                dateEnd = calendar.getTime();

                transitAirportIds = new ArrayList<>();
                for (FlightSchedule flightScheduleTwo: secondLeg)
                {
                    transitAirportIds.add(flightScheduleTwo.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportId());
                }

                query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight.flightRoute.origin.airportId IN :inAirportIds AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId = :inDestinationAirportId AND fs.departureDateTime > :inDateStart AND fs.departureDateTime <= :inDateEnd ORDER BY fs.departureDateTime ASC");
                query.setParameter("inAirportIds", transitAirportIds);
                query.setParameter("inDestinationAirportId", destinationAirportId);
                query.setParameter("inDateStart", dateStart);
                query.setParameter("inDateEnd", dateEnd);
                List<FlightSchedule> thirdLeg = query.getResultList();

                for (FlightSchedule firstFlightSchedule: firstLeg)
                {
                    Airport originAirport = em.find(Airport.class, departureAirportId);
                    Airport firstTransitAirport = firstFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination();
                    Integer timeZoneDiff = firstTransitAirport.getTimeZoneDiff() - originAirport.getTimeZoneDiff();
                    Integer firstFlightHours = firstFlightSchedule.getFlightHours();
                    Integer firstFlightMins = firstFlightSchedule.getFlightMinutes();

                    for (FlightSchedule secondFlightSchedule: secondLeg)
                    {
                        if (firstTransitAirport.equals(secondFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin()))
                        {
                            calendar = new GregorianCalendar();
                            calendar.setTime(firstFlightSchedule.getDepartureDateTime());
                            calendar.add(GregorianCalendar.HOUR_OF_DAY, firstFlightHours+2);
                            calendar.add(GregorianCalendar.MINUTE, firstFlightMins);
                            // Account for time zone difference
                            calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);

                            if (secondFlightSchedule.getDepartureDateTime().compareTo(calendar.getTime()) >= 0)
                            {
                                Airport secondTransitAirport = secondFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination();
                                timeZoneDiff = secondTransitAirport.getTimeZoneDiff() - firstTransitAirport.getTimeZoneDiff();

                                for (FlightSchedule thirdFlightSchedule: thirdLeg)
                                {   
                                    if (thirdFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().equals(secondTransitAirport))
                                    {
                                        calendar = new GregorianCalendar();
                                        calendar.setTime(firstFlightSchedule.getDepartureDateTime());
                                        calendar.setTime(secondFlightSchedule.getDepartureDateTime());
                                        calendar.add(GregorianCalendar.HOUR_OF_DAY, secondFlightSchedule.getFlightHours()+2);
                                        calendar.add(GregorianCalendar.MINUTE, secondFlightSchedule.getFlightMinutes());
                                        // Account for time zone difference
                                        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);

                                        if (thirdFlightSchedule.getDepartureDateTime().compareTo(calendar.getTime()) >= 0)
                                        {
                                            if (filterFlightSchedule(firstFlightSchedule, preferredCabinClass, numPassengers) &&
                                                filterFlightSchedule(secondFlightSchedule, preferredCabinClass, numPassengers) &&
                                                filterFlightSchedule(thirdFlightSchedule, preferredCabinClass, numPassengers) &&
                                                firstFlightSchedule.getEnabled() && secondFlightSchedule.getEnabled() && thirdFlightSchedule.getEnabled())
                                            {
                                                connectingFlights.add(firstFlightSchedule);
                                                connectingFlights.add(secondFlightSchedule);
                                                connectingFlights.add(thirdFlightSchedule);
                                            }    
                                        }
                                    }
                                }
                            } 
                        }
                    }
                }
            }
        }

        for (FlightSchedule fs:connectingFlights)
        {
            fs.getSeatInventories().size();
            fs.getFlightSchedulePlan().getFares().size();
        }
        return connectingFlights;
    }
    
    @Override
    public List<FlightSchedule> searchDoubleTransitConnectingFlightScheduleUnmanaged (Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        List<FlightSchedule> flightSchedules = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        for (FlightSchedule fs: flightSchedules)
        {
            em.detach(fs);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                em.detach(si);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                em.detach(fr);
            }
            
            em.detach(fs.getFlightSchedulePlan());
            em.detach(fs.getReturnFlightSchedule());
        }
        
        return flightSchedules;
    }
    
    @Override
    public Boolean filterFlightSchedule(FlightSchedule flightSchedule, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        if (!flightSchedule.getEnabled())
        {
            return false;
        }
        
        if (preferredCabinClass == null)
        {
            return true;
        }
        
        Boolean preferredCabinClassAvailable = false;
        for (SeatInventory si: flightSchedule.getSeatInventories())
        {
            if (si.getCabinClass().getCabinClassType().equals(preferredCabinClass))
            {
                preferredCabinClassAvailable = hasSufficientBalanceSeats(si, numPassengers);
                break;
            }
        }
        return preferredCabinClassAvailable;
    }
    
    @Override
    public Boolean hasSufficientBalanceSeats(SeatInventory seatInventory, Integer numPassengers)
    {
        if (seatInventory.getNumOfBalanceSeats() < numPassengers)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<FlightSchedule>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
