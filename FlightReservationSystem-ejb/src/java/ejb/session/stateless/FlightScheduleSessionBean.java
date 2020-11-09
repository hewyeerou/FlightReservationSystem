/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import entity.CabinClass;
import entity.FlightReservationRecord;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CabinClassEnum;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;

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
    

    @Override
    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId, Long seatInventoryId) throws FlightSchedulePlanNotFoundException 
    {
        FlightSchedulePlan flightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(flightSchedulePlanId);

        flightSchedule.setFlightSchedulePlan(flightSchedulePlan);
        flightSchedulePlan.getFlightSchedules().add(flightSchedule);

        SeatInventory seatInventory = em.find(SeatInventory.class, seatInventoryId);
        flightSchedule.setSeatInventory(seatInventory);

        for (FlightReservationRecord flightReservationRecord : flightSchedule.getFlightReservationRecords()) {
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
    
    @Override
    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId, Long seatInventoryId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException
    {
        FlightSchedulePlan returnFlightSchedulePlan = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(returnFlightSchedulePlanId);
        
        FlightSchedule flightSchedule = getFlightScheduleById(flightScheduleId);
        
        returnFlightSchedule.setFlightSchedulePlan(returnFlightSchedulePlan);
        returnFlightSchedulePlan.getFlightSchedules().add(returnFlightSchedule);
        
        SeatInventory seatInventory = em.find(SeatInventory.class, seatInventoryId);
        returnFlightSchedule.setSeatInventory(seatInventory);
        
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
    
    
    @Override
    public FlightSchedule getFlightScheduleById(Long flightScheduleId) throws FlightScheduleNotFoundException 
    {
        FlightSchedule flightSchedule = em.find(FlightSchedule.class, flightScheduleId);
        
        if(flightSchedule != null)
        {
            flightSchedule.getFlightReservationRecords().size();
            flightSchedule.getFlightSchedulePlan();
            flightSchedule.getSeatInventory();
            
            return flightSchedule;
        }
        else
        {
            throw new FlightScheduleNotFoundException("Flight Schedule " + flightScheduleId + " does not exist!");
        }
    }

    @Override
    public List<FlightSchedule> searchDirectFlightSchedules(Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, String cabinClassPreference, Integer numPassengers)
    {
        Airport departureAirport = em.find(Airport.class, departureAirportId);
        Airport destinationAirport = em.find(Airport.class, destinationAirportId);
        
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :inDateStart AND fs.departureDateTime < :inDateEnd AND fs.flightSchedulePlan.flight.flightRoute.origin = :inDepartureAirport AND fs.flightSchedulePlan.flight.flightRoute.destination = :inDestinationAirport ORDER BY fs.departureDateTime ASC");
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        query.setParameter("inDepartureAirport", departureAirport);
        query.setParameter("inDestinationAirport", destinationAirport);
        
        List<FlightSchedule> flightSchedules = query.getResultList();
      
        return flightSchedules;
    }
    
    @Override
    public List<FlightSchedule> searchSingleTransitConnectingFlightSchedule (Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, String cabinClassPreference, Integer numPassengers)
    {
        List<FlightSchedule> connectingFlights = new ArrayList<>();
        
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :inDateStart AND fs.departureDateTime < :inDateEnd AND fs.flightSchedulePlan.flight.flightRoute.origin.airportId = :inDepartureAirportId AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId <> :inDestinationAirportId ORDER BY fs.departureDateTime ASC");
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        query.setParameter("inDepartureAirportId", departureAirportId);
        query.setParameter("inDestinationAirportId", destinationAirportId);
        List<FlightSchedule> firstLeg = query.getResultList();
        
        List<Long> transitAirportIds = new ArrayList<>();
        for (FlightSchedule flightSchedule: firstLeg)
        {
            transitAirportIds.add(flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportId());
        }
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dateEnd);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 10);
        Date endDateTime = calendar.getTime();
        
        query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight.flightRoute.origin.airportId IN :inAirportIds AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId = :inDestinationAirportId AND fs.departureDateTime > :inDateStart AND fs.departureDateTime <= :inDateEnd");
        query.setParameter("inDestinationAirportId", destinationAirportId);
        query.setParameter("inAirportIds", transitAirportIds);
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", endDateTime);
        List<FlightSchedule> secondLeg = query.getResultList();
        
        for (FlightSchedule firstFlightSchedule: firstLeg)
        {
            Airport transitAirport = firstFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination();
            
            for (FlightSchedule secondFlightSchedule: secondLeg)
            {
                if (secondFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().equals(transitAirport))
                {
                    calendar = new GregorianCalendar();
                    calendar.setTime(firstFlightSchedule.getDepartureDateTime());
                    calendar.add(GregorianCalendar.HOUR_OF_DAY, firstFlightSchedule.getFlightHours()+2);
                    calendar.add(GregorianCalendar.MINUTE, firstFlightSchedule.getFlightMinutes());
                    
                    if (secondFlightSchedule.getDepartureDateTime().compareTo(calendar.getTime()) >= 0)
                    {
                        connectingFlights.add(firstFlightSchedule);
                        connectingFlights.add(secondFlightSchedule);
                    }
                }
            }
        }
        return connectingFlights;
    }
    
    @Override
    public List<FlightSchedule> searchDoubleTransitConnectingFlightSchedule (Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, String cabinClassPreference, Integer numPassengers)
    {
        List<FlightSchedule> connectingFlights = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Query query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.departureDateTime >= :inDateStart AND fs.departureDateTime < :inDateEnd AND fs.flightSchedulePlan.flight.flightRoute.origin.airportId = :inDepartureAirportId AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId <> :inDestinationAirportId ORDER BY fs.departureDateTime ASC");
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        query.setParameter("inDepartureAirportId", departureAirportId);
        query.setParameter("inDestinationAirportId", destinationAirportId);
        List<FlightSchedule> firstLeg = query.getResultList();
        System.out.println(firstLeg.size());
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(dateEnd);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 10);
        dateEnd = calendar.getTime();
        
        List<Long> transitAirportIds = new ArrayList<>();
        for (FlightSchedule flightScheduleOne: firstLeg)
        {
            transitAirportIds.add(flightScheduleOne.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportId());
        }
        
        query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight.flightRoute.origin.airportId IN :inAirportIds AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId <> :inDestinationAirportId AND fs.departureDateTime > :inDateStart AND fs.departureDateTime <= :inDateEnd");
        query.setParameter("inDestinationAirportId", destinationAirportId);
        query.setParameter("inAirportIds", transitAirportIds);
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        List<FlightSchedule> secondLeg = query.getResultList();
        System.out.println(secondLeg.size());
        
        calendar = new GregorianCalendar();
        calendar.setTime(dateEnd);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, 10);
        dateEnd = calendar.getTime();
        
        transitAirportIds = new ArrayList<>();
        for (FlightSchedule flightScheduleTwo: secondLeg)
        {
            transitAirportIds.add(flightScheduleTwo.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getAirportId());
        }
        
        query = em.createQuery("SELECT fs FROM FlightSchedule fs WHERE fs.flightSchedulePlan.flight.flightRoute.origin.airportId IN :inAirportIds AND fs.flightSchedulePlan.flight.flightRoute.destination.airportId = :inDestinationAirportId AND fs.departureDateTime > :inDateStart AND fs.departureDateTime <= :inDateEnd");
        query.setParameter("inAirportIds", transitAirportIds);
        query.setParameter("inDestinationAirportId", destinationAirportId);
        query.setParameter("inDateStart", dateStart);
        query.setParameter("inDateEnd", dateEnd);
        List<FlightSchedule> thirdLeg = query.getResultList();
        System.out.println(thirdLeg.size());
        
        for (FlightSchedule firstFlightSchedule: firstLeg)
        {
            Airport firstTransitAirport = firstFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination();
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
                    
                    if (secondFlightSchedule.getDepartureDateTime().compareTo(calendar.getTime()) >= 0)
                    {
                        Airport secondTransitAirport = secondFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination();
                        
                        for (FlightSchedule thirdFlightSchedule: thirdLeg)
                        {   
                            System.out.println(secondTransitAirport.getIataCode());
                            System.out.println(thirdFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode());
                            if (thirdFlightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().equals(secondTransitAirport))
                            {
                                calendar = new GregorianCalendar();
                                calendar.setTime(firstFlightSchedule.getDepartureDateTime());
                                calendar.setTime(secondFlightSchedule.getDepartureDateTime());
                                calendar.add(GregorianCalendar.HOUR_OF_DAY, secondFlightSchedule.getFlightHours()+2);
                                calendar.add(GregorianCalendar.MINUTE, secondFlightSchedule.getFlightMinutes());
                                
                                if (thirdFlightSchedule.getDepartureDateTime().compareTo(calendar.getTime()) >= 0)
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
        return connectingFlights;
    }
    
    /*
    public List<FlightSchedule> filterFlightSchedulesByCabinClasses(List<FlightSchedule> flightSchedules, String cabinClassPreference, Integer numPassengers)
    {
        for(FlightSchedule fs: flightSchedules)
        {
            if (cabinClassPreference.equals("NA"))
            {
                break;
            }
            
            List<CabinClass> cabinClasses = fs.getFlightSchedulePlan().getFlight().getAircraftConfig().getCabinClasses();
            for (CabinClass cc: cabinClasses)
            {
                if (cabinClassPreference.equals("F") && cc.getCabinClassType().equals(CabinClassEnum.FIRST_CLASS))
                {
                    fs.getSeatInventory().getNumOfAvailableSeats()
                }
            }
        }
        return flightSchedules;
    }
    */
}
