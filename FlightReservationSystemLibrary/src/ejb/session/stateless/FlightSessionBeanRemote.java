/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Flight;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightNotFoundException;
import util.exception.FlightNumExistException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;
import util.exception.createOutboundReturnFlightCheckException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FlightSessionBeanRemote 
{
    public Long createFlight(Flight newFlight, Long flightRouteId, Long aircraftConfigId) throws FlightNumExistException, UnknownPersistenceException, InputDataValidationException;
    
    public Long createReturnFlight(Flight newReturnFlight, Long flightId) throws FlightNumExistException, UnknownPersistenceException, InputDataValidationException;

    public List<Flight> getAllFlights();
    
    public Flight getFlightByFlightNum(String flightNum) throws FlightNotFoundException;
    
    public void updateFlight(Flight flight) throws FlightNotFoundException, FlightNumExistException, InputDataValidationException;
    
    public Flight getFlightById(Long flightId) throws FlightNotFoundException;
    
    public Long createOutboundReturnFlightCheck(Flight newFlight, Long flightRouteId, Long aircraftConfigId, Flight newReturnFlight) throws createOutboundReturnFlightCheckException, InputDataValidationException;

    public void removeFlight(Long flightId) throws FlightNotFoundException;
    
    public void setFlightDisabled(Long flightId);

}
