/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.FlightScheduleSessionBeanLocal;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.SeatInventory;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.enumeration.CabinClassEnum;
import util.exception.FlightScheduleNotFoundException;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "FlightScheduleWebService")
@Stateless()
public class FlightScheduleWebService {

    @EJB
    private FlightScheduleSessionBeanLocal flightScheduleSessionBeanLocal;
    
    /**
     * This is a sample web service operation
     */
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
            fs.getFlightSchedulePlan().getFlightSchedules().remove(fs);
            
            for (SeatInventory si: fs.getSeatInventories())
            {
                si.setFlightSchedule(null);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                fr.getFlightSchedules().remove(fs);
            }
            
            fs.getReturnFlightSchedule().setReturnFlightSchedule(null);
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
            for (SeatInventory si: fs.getSeatInventories())
            {
                si.setFlightSchedule(null);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                fr.getFlightSchedules().remove(fs);
            }
            
            fs.getFlightSchedulePlan().getFlightSchedules().remove(fs);
            fs.getReturnFlightSchedule().setReturnFlightSchedule(fs);
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
            for (SeatInventory si: fs.getSeatInventories())
            {
                si.setFlightSchedule(null);
            }
            
            for (FlightReservationRecord fr: fs.getFlightReservationRecords())
            {
                fr.getFlightSchedules().remove(fs);
            }
            
            fs.getFlightSchedulePlan().getFlightSchedules().remove(fs);
            fs.getReturnFlightSchedule().setReturnFlightSchedule(null);
        }
        
        return flightSchedules;
    }
    
    public FlightSchedule getFlightScheduleByIdUnmanaged(@WebParam(name = "flightScheduleId") Long flightScheduleId) throws FlightScheduleNotFoundException
    {
        FlightSchedule fs = flightScheduleSessionBeanLocal.getFlightScheduleByIdUnmanaged(flightScheduleId);
        
        for (SeatInventory si: fs.getSeatInventories())
        {
            si.setFlightSchedule(null);
        }

        for (FlightReservationRecord fr: fs.getFlightReservationRecords())
        {
            fr.getFlightSchedules().remove(fs);
        }

        fs.getFlightSchedulePlan().getFlightSchedules().remove(fs);
        fs.getReturnFlightSchedule().setReturnFlightSchedule(null);
        
        return fs;
    }
}