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
import ejb.session.stateless.FlightScheduleSessionBeanLocal;
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
import util.exception.FlightReservationRecordNotFoundException;
import util.exception.FlightScheduleNotFoundException;
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
    
    @WebMethod(operationName = "getAllAirportsUnmanaged") // cycle between aircraftType and AircraftConfig
    public List<Airport> getAllAirportsUnmanaged () {
        
        List<Airport> airports = airportSessionBeanLocal.getAllAirportsUnmanaged();
        
        for (Airport airport: airports)
        {
            airport.setDepartureRoutes(null);
            airport.setArrivalRoutes(null);
        }
        
        return airports;
    }
    
    @WebMethod(operationName = "retrieveCabinClassesByAircraftConfigIdUnmanaged") // cannot set cabin class in fare as null??
    public List<CabinClass> retrieveCabinClassesByAircraftConfigIdUnmanaged(@WebParam(name = "aircraftConfigId") Long aircraftConfigId) {
        
        List<CabinClass> cabinClasses = cabinClassSessionBeanLocal.retrieveCabinClassesByAircraftConfigIdUnmanaged(aircraftConfigId);
        
        for (CabinClass cc: cabinClasses)
        {
            for (Fare fare: cc.getFares())
            {
                fare.setCabinClass(null);
            }
            
            for (SeatInventory si: cc.getSeatInventories())
            {
                si.setCabinClass(null);
            }
        }
        
        return cabinClasses;
    }
    
    @WebMethod(operationName = "retrieveCabinClassByIdUnmanaged")
    public CabinClass retrieveCabinClassByIdUnmanaged(@WebParam(name = "cabinClassId") Long cabinClassId) throws CabinClassNotFoundException
    {
        CabinClass cc = cabinClassSessionBeanLocal.retrieveCabinClassById(cabinClassId);
        
        for (Fare fare: cc.getFares())
        {
            fare.setCabinClass(null);
        }

        for (SeatInventory si: cc.getSeatInventories())
        {
            si.setCabinClass(null);
        }
        
        return cc;
    }
    
    @WebMethod(operationName = "retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged")
    public CabinClass retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(@WebParam(name = "aircraftConfigId") Long aircraftConfigId, 
                                                                           @WebParam(name = "type") CabinClassEnum type) throws CabinClassNotFoundException
    {
        CabinClass cc = cabinClassSessionBeanLocal.retrieveCabinClassByAircraftConfigIdAndType(aircraftConfigId, type);

        for (Fare fare: cc.getFares())
        {
            fare.setCabinClass(null);
        }

        for (SeatInventory si: cc.getSeatInventories())
        {
            si.setCabinClass(null);
        }
        
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
    
    @WebMethod(operationName = "retrieveCabinSeatInventoryInSeatInventoryUnmanaged")
    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventoryUnmanaged(@WebParam(name = "seatInventoryId") Long seatInventoryId)
    {
        List<CabinSeatInventory> seats = cabinSeatInventorySessionBeanLocal.retrieveCabinSeatInventoryInSeatInventoryUnmanaged(seatInventoryId);
        
        for (CabinSeatInventory csi: seats)
        {
            csi.getSeatInventory().setCabinSeatInventories(null);
        }
        
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
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanLocal.searchDirectFlightSchedulesUnmanaged(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
     
        for (FlightSchedule fs: flightSchedules)
        {
            fs.getFlightSchedulePlan().setFlightSchedules(null);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                si.setFlightSchedule(null);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                fr.setFlightSchedules(null);
            }
            
            fs.setReturnFlightSchedule(null);
        }
        
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
     
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanLocal.searchSingleTransitConnectingFlightScheduleUnmanaged(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        for (FlightSchedule fs: flightSchedules)
        {
            fs.getFlightSchedulePlan().setFlightSchedules(null);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                si.setFlightSchedule(null);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                fr.setFlightSchedules(null);
            }
            
            fs.setReturnFlightSchedule(null);
        }
        
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
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanLocal.searchDoubleTransitConnectingFlightScheduleUnmanaged(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        for (FlightSchedule fs: flightSchedules)
        {
            fs.getFlightSchedulePlan().setFlightSchedules(null);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                si.setFlightSchedule(null);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                fr.setFlightSchedules(null);
            }
            
            fs.setReturnFlightSchedule(null);
        }
        
        return flightSchedules;
    }
    
    @WebMethod(operationName = "getFlightScheduleByIdUnmanaged")
    public FlightSchedule getFlightScheduleByIdUnmanaged(@WebParam(name = "flightScheduleId") Long flightScheduleId) throws FlightScheduleNotFoundException
    {
        FlightSchedule fs = flightScheduleSessionBeanLocal.getFlightScheduleByIdUnmanaged(flightScheduleId);
        
        for (SeatInventory si: fs.getSeatInventories())
        {
            si.setFlightSchedule(null);
        }

        for (FlightReservationRecord fr: fs.getFlightReservationRecords())
        {
            fr.setFlightSchedules(null);
        }

        fs.getFlightSchedulePlan().setFlightSchedules(null);
        fs.getReturnFlightSchedule().setReturnFlightSchedule(null);
        
        return fs;
    }
    
    @WebMethod(operationName = "getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged")
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(@WebParam(name = "name") Long flightSchedulePlanId,
                                                                            @WebParam(name = "cabinClassId") Long cabinClassId)
    {
        List<Fare> fares = fareSessionBeanLocal.getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedulePlanId, cabinClassId);
        
        for (Fare fare: fares)
        {
            fare.getCabinClass().setFares(null);
            
            fare.getFlightSchedulePlan().setFares(null);
        }
        
        return fares;
    }
    
    @WebMethod(operationName = "getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged")
    public BigDecimal getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(@WebParam(name = "flightSchedulePlanId") Long flightSchedulePlanId, 
                                                                                   @WebParam(name = "cabinClassId") Long cabinClassId) throws FareNotFoundException
    {
        BigDecimal price = fareSessionBeanLocal.getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedulePlanId, cabinClassId);
        
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
    public List<FlightReservationRecord> retrieveReservationRecordsByCustomerIdUnmanaged (@WebParam(name = "customerId") Long customerId)
    {
        List<FlightReservationRecord> reservations = flightReservationRecordSessionBeanLocal.retrieveReservationRecordsByCustomerId(customerId);
        
        for (FlightReservationRecord reservation: reservations)
        {
            for (FlightSchedule fs: reservation.getFlightSchedules())
            {
                fs.setFlightReservationRecords(null);
            }
            
            for (Passenger p: reservation.getPassengers())
            {
                p.setFlightReservationRecord(null);
            }
        }
        
        return reservations;
    }
    
    @WebMethod(operationName = "retrieveReservationRecordById")
    public FlightReservationRecord retrieveReservationRecordById (@WebParam(name = "recordId") Long recordId,
                                                                  @WebParam(name = "personId") Long personId) throws FlightReservationRecordNotFoundException
    {
        FlightReservationRecord reservation = flightReservationRecordSessionBeanLocal.retrieveReservationRecordById(recordId, personId);
        
        for (FlightSchedule fs: reservation.getFlightSchedules())
        {
            fs.setFlightReservationRecords(null);
        }

        for (Passenger p: reservation.getPassengers())
        {
            p.setFlightReservationRecord(null);
        }
        
        return reservation;
    }
    
    @WebMethod(operationName = "retrievePassengerByPassengerIdUnmanaged")
    public Passenger retrievePassengerByPassengerIdUnmanaged(@WebParam(name = "passengerId") Long passengerId) throws PassengerNotFoundException
    {
        Passenger p = passengerSessionBeanLocal.retrievePassengerByPassengerIdUnmanaged(passengerId);
        
        p.getFlightReservationRecord().setPassengers(null);
        
        return p;
    }
    
    @WebMethod(operationName = "createNewPassenger")
    public Long createNewPassenger (@WebParam(name = "passenger") Passenger passenger,
                                    @WebParam(name = "flightReservationRecordId") Long flightReservationRecordId) throws InputDataValidationException
    {
        Long passengerId = passengerSessionBeanLocal.createNewPassenger(passenger, flightReservationRecordId);
        
        return passengerId;
    }
    
    @WebMethod(operationName = "retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged")
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(@WebParam(name = "cabinClassId") Long cabinClassId, 
                                                                                         @WebParam(name = "flightScheduleId") Long flightScheduleId) throws SeatInventoryNotFoundException
    {
        SeatInventory si = seatInventorySessionBeanLocal.retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(cabinClassId, flightScheduleId);
        
        si.getCabinClass().setSeatInventories(null);
        
        si.getFlightSchedule().setSeatInventories(null);
        
        for (CabinSeatInventory csi: si.getCabinSeatInventories())
        {
            csi.setSeatInventory(null);
        }
        
        return si;
    }    
}
