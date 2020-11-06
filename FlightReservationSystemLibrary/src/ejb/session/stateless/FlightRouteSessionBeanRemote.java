/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightRoute;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightRouteExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FlightRouteSessionBeanRemote 
{
    public Long createNewFlightRoute(FlightRoute flightRoute, Long originAirportId, Long destinationAirportId) throws FlightRouteExistException, UnknownPersistenceException;  
    
    public Long createNewReturnFlightRoute(FlightRoute returnFlightRoute, Long flightRouteId) throws FlightRouteExistException, UnknownPersistenceException;

    public List<FlightRoute> getAllFlightRoute();
    
    public FlightRoute getFlightRouteById(Long flightRouteId, Boolean fetchAirport, Boolean fetchFlights) throws FlightRouteNotFoundException;

    public void removeFlightRoute(Long flightRouteId) throws FlightRouteNotFoundException;
    
    public void setFlightRouteDisabled(Long flightRouteId);
    
//    public void removeReturnFlightRoute(Long flightRouteId, Long flightRouteIdAssociatedWithReturnFlightRoute) throws FlightRouteNotFoundException;
}
