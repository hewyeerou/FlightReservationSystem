/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.AirportSessionBeanLocal;
import ejb.session.stateless.CabinClassSessionBeanLocal;
import ejb.session.stateless.CabinSeatInventorySessionBeanLocal;
import ejb.session.stateless.FareSessionBeanLocal;
import ejb.session.stateless.FlightReservationRecordSessionBeanLocal;
import ejb.session.stateless.FlightRouteSessionBeanLocal;
import ejb.session.stateless.FlightSchedulePlanSessionBeanLocal;
import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import ejb.session.stateless.FlightSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.PassengerSessionBeanLocal;
import ejb.session.stateless.SeatInventorySessionBeanLocal;
import entity.Airport;
import entity.CabinClass;
import entity.CabinSeatInventory;
import entity.Fare;
import entity.Flight;
import entity.FlightReservationRecord;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.Partner;
import entity.Passenger;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.enumeration.CabinClassEnum;
import util.exception.CabinClassNotFoundException;
import util.exception.CabinSeatInventoryExistException;
import util.exception.FareNotFoundException;
import util.exception.FlightNotFoundException;
import util.exception.FlightReservationRecordNotFoundException;
import util.exception.FlightRouteNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.PassengerNotFoundException;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author yeerouhew
 */
@WebService(serviceName = "PartnerWebService")
@Stateless
public class PartnerWebService {

    @EJB
    private FlightSchedulePlanSessionBeanLocal flightSchedulePlanSessionBeanLocal;
    @EJB
    private FlightRouteSessionBeanLocal flightRouteSessionBeanLocal;
    @EJB
    private FlightSessionBeanLocal flightSessionBeanLocal;
    @EJB
    private CabinSeatInventorySessionBeanLocal cabinSeatInventorySessionBeanLocal;
    @EJB
    private CabinClassSessionBeanLocal cabinClassSessionBeanLocal;
    @EJB
    private AirportSessionBeanLocal airportSessionBeanLocal;
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBeanLocal;
    @EJB
    private FareSessionBeanLocal fareSessionBeanLocal;
    @EJB
    private FlightReservationRecordSessionBeanLocal flightReservationRecordSessionBeanLocal;
    @EJB
    private PassengerSessionBeanLocal passengerSessionBeanLocal;
    @EJB
    private SeatInventorySessionBeanLocal seatInventorySessionBeanLocal;
    

    
    
    @WebMethod(operationName = "retrievePartnerByUsername")
    public Partner retrievePartnerByUsername(@WebParam(name = "username") String username) throws PartnerNotFoundException
    {
        Partner partner = partnerSessionBeanLocal.retrievePartnerByUsername(username);
        return partner;
    }
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username,
                                @WebParam(name = "password") String password) 
                                throws PartnerNotFoundException, InvalidLoginCredentialException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
        return partner;
    }
    
    @WebMethod(operationName = "getAllAirports")
    public List<Airport> getAllAirports () {
        
        List<Airport> airports = airportSessionBeanLocal.getAllAirports();
                
        return airports;
    }
    
    @WebMethod(operationName = "retrieveCabinClassesByAircraftConfigId")
    public List<CabinClass> retrieveCabinClassesByAircraftConfigId(@WebParam(name = "aircraftConfigId") Long aircraftConfigId) {
        
        List<CabinClass> cabinClasses = cabinClassSessionBeanLocal.retrieveCabinClassesByAircraftConfigId(aircraftConfigId);
        
        return cabinClasses;
    }
    
    @WebMethod(operationName = "retrieveCabinClassById")
    public CabinClass retrieveCabinClassById(@WebParam(name = "cabinClassId") Long cabinClassId) throws CabinClassNotFoundException
    {
        CabinClass cc = cabinClassSessionBeanLocal.retrieveCabinClassById(cabinClassId);
        
        return cc;
    }
    
    @WebMethod(operationName = "retrieveCabinClassByAircraftConfigIdAndType")
    public CabinClass retrieveCabinClassByAircraftConfigIdAndType(@WebParam(name = "aircraftConfigId") Long aircraftConfigId, 
                                                                  @WebParam(name = "type") CabinClassEnum type) throws CabinClassNotFoundException
    {
        CabinClass cc = cabinClassSessionBeanLocal.retrieveCabinClassByAircraftConfigIdAndType(aircraftConfigId, type);

        return cc;
    }
    
    @WebMethod(operationName = "createNewCabinSeatInventory")
    public Long createNewCabinSeatInventory(@WebParam(name = "cabinSeatInventory") CabinSeatInventory cabinSeatInventory, 
                                            @WebParam(name = "seatInventoryId") Long seatInventoryId,
                                            @WebParam(name = "passengerId") Long passengerId) throws CabinSeatInventoryExistException, InputDataValidationException
    {
        Long csiId = cabinSeatInventorySessionBeanLocal.createNewCabinSeatInventory(cabinSeatInventory, seatInventoryId, passengerId);
        
        return csiId;
    }
    
    @WebMethod(operationName = "retrieveCabinSeatInventoryInSeatInventory")
    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventory(@WebParam(name = "seatInventoryId") Long seatInventoryId)
    {
        List<CabinSeatInventory> seats = cabinSeatInventorySessionBeanLocal.retrieveCabinSeatInventoryInSeatInventory(seatInventoryId);
                
        return seats;
    }
    
    @WebMethod(operationName = "searchDirectFlightSchedules")
    public List<FlightSchedule> searchDirectFlightSchedules(@WebParam(name = "departureAirportId") Long departureAirportId,
                                                            @WebParam(name = "destinationAirportId") Long destinationAirportId,
                                                            @WebParam(name = "dateStart") Date dateStart,
                                                            @WebParam(name = "dateEnd") Date dateEnd,
                                                            @WebParam(name = "preferredCabinClass") CabinClassEnum preferredCabinClass,
                                                            @WebParam(name = "numPassengers") Integer numPassengers) 
    {
        List<FlightSchedule > flightSchedules = flightScheduleSessionBeanLocal.searchDirectFlightSchedules(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        return flightSchedules;
    }
    
    @WebMethod(operationName = "searchSingleTransitConnectingFlightSchedule")
    public List<FlightSchedule> searchSingleTransitConnectingFlightSchedule(@WebParam(name = "departureAirportId") Long departureAirportId,
                                                            @WebParam(name = "destinationAirportId") Long destinationAirportId,
                                                            @WebParam(name = "dateStart") Date dateStart,
                                                            @WebParam(name = "dateEnd") Date dateEnd,
                                                            @WebParam(name = "preferredCabinClass") CabinClassEnum preferredCabinClass,
                                                            @WebParam(name = "numPassengers") Integer numPassengers)
    {
     
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanLocal.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        return flightSchedules;
    }
    
    
    @WebMethod(operationName = "searchDoubleTransitConnectingFlightSchedule")
    public List<FlightSchedule> searchDoubleTransitConnectingFlightSchedule(@WebParam(name = "departureAirportId") Long departureAirportId,
                                                            @WebParam(name = "destinationAirportId") Long destinationAirportId,
                                                            @WebParam(name = "dateStart") Date dateStart,
                                                            @WebParam(name = "dateEnd") Date dateEnd,
                                                            @WebParam(name = "preferredCabinClass") CabinClassEnum preferredCabinClass,
                                                            @WebParam(name = "numPassengers") Integer numPassengers)
    {
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanLocal.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        return flightSchedules;
    }
    
    @WebMethod(operationName = "getFlightScheduleById")
    public FlightSchedule getFlightScheduleById(@WebParam(name = "flightScheduleId") Long flightScheduleId) throws FlightScheduleNotFoundException
    {
        FlightSchedule fs = flightScheduleSessionBeanLocal.getFlightScheduleById(flightScheduleId); 
        
        return fs;
    }
    
    @WebMethod(operationName = "getFareByFlightSchedulePlanIdAndCabinClassId")
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassId(@WebParam(name = "name") Long flightSchedulePlanId,
                                                                            @WebParam(name = "cabinClassId") Long cabinClassId)
    {
        List<Fare> fares = fareSessionBeanLocal.getFareByFlightSchedulePlanIdAndCabinClassId(flightSchedulePlanId, cabinClassId);
        
        return fares;
    }
    
    @WebMethod(operationName = "getHighestFareByFlightSchedulePlanIdAndCabinClassId")
    public BigDecimal getHighestFareByFlightSchedulePlanIdAndCabinClassId(@WebParam(name = "flightSchedulePlanId") Long flightSchedulePlanId, 
                                                                                   @WebParam(name = "cabinClassId") Long cabinClassId) throws FareNotFoundException
    {
        BigDecimal price = fareSessionBeanLocal.getHighestFareByFlightSchedulePlanIdAndCabinClassId(flightSchedulePlanId, cabinClassId);
        
        return price;
    }
    
    @WebMethod(operationName = "createNewFlightReservationRecord")
    public Long createNewFlightReservationRecord(@WebParam(name = "flightReservationRecord") FlightReservationRecord flightReservationRecord,
                                                 @WebParam(name = "personId") Long personId,
                                                 @WebParam(name = "flightSchedules") List<Long> flightSchedules) throws InputDataValidationException
    {
        Long reservationId = flightReservationRecordSessionBeanLocal.createNewFlightReservationRecord(flightReservationRecord, personId, flightSchedules);
        
        return reservationId;
    }
    
    @WebMethod(operationName = "retrieveReservationRecordsByCustomerId")
    public List<FlightReservationRecord> retrieveReservationRecordsByCustomerId (@WebParam(name = "customerId") Long customerId)
    {
        List<FlightReservationRecord> reservations = flightReservationRecordSessionBeanLocal.retrieveReservationRecordsByCustomerId(customerId);
        
        return reservations;
    }
    
    @WebMethod(operationName = "retrieveReservationRecordById")
    public FlightReservationRecord retrieveReservationRecordById (@WebParam(name = "recordId") Long recordId,
                                                                  @WebParam(name = "personId") Long personId) throws FlightReservationRecordNotFoundException
    {
        FlightReservationRecord reservation = flightReservationRecordSessionBeanLocal.retrieveReservationRecordById(recordId, personId);
        
        return reservation;
    }
    
    @WebMethod(operationName = "retrievePassengerByPassengerId")
    public Passenger retrievePassengerByPassengerId(@WebParam(name = "passengerId") Long passengerId) throws PassengerNotFoundException
    {
        Passenger p = passengerSessionBeanLocal.retrievePassengerByPassengerId(passengerId);
        
        return p;
    }
    
    @WebMethod(operationName = "createNewPassenger")
    public Long createNewPassenger (@WebParam(name = "passenger") Passenger passenger,
                                    @WebParam(name = "flightReservationRecordId") Long flightReservationRecordId) throws InputDataValidationException
    {
        Long passengerId = passengerSessionBeanLocal.createNewPassenger(passenger, flightReservationRecordId);
        
        return passengerId;
    }
    
    @WebMethod(operationName = "retrieveSeatInventoryByCabinClassIdAndFlightScheduleId")
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(@WebParam(name = "cabinClassId") Long cabinClassId, 
                                                                                @WebParam(name = "flightScheduleId") Long flightScheduleId) throws SeatInventoryNotFoundException
    {
        SeatInventory si = seatInventorySessionBeanLocal.retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(cabinClassId, flightScheduleId);
        
        return si;
    }    
    
    @WebMethod(operationName = "getFlightSchedulePlanById")
    public FlightSchedulePlan getFlightSchedulePlanById(@WebParam(name = "flightSchedulePlanId") Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException
    {
        FlightSchedulePlan fsp = flightSchedulePlanSessionBeanLocal.getFlightSchedulePlanById(flightSchedulePlanId);
        
        return fsp;
    }
    
    @WebMethod(operationName = "getFlightById")
    public Flight getFlightById(@WebParam(name = "flightSchedulePlanId") Long flightId) throws FlightNotFoundException
    {
        Flight f = flightSessionBeanLocal.getFlightById(flightId);

        return f;
    }
    
    @WebMethod(operationName = "getFlightRouteById")
    public FlightRoute getFlightRouteById(@WebParam(name = "flightRouteId") Long flightRouteId, 
                                          @WebParam(name = "fetchAirport")Boolean fetchAirport, 
                                          @WebParam(name = "fetchFlights")Boolean fetchFlights) throws FlightRouteNotFoundException
    {
        FlightRoute fr = flightRouteSessionBeanLocal.getFlightRouteById(flightRouteId, fetchAirport, fetchFlights);
  
        return fr;
    }
}
