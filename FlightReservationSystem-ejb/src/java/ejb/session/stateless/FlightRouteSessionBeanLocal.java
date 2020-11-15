/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightRoute;
import java.util.List;
import javax.ejb.Local;
import util.exception.FlightRouteExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Local
public interface FlightRouteSessionBeanLocal 
{

    public Long createNewFlightRoute(FlightRoute flightRoute, Long originAirportId, Long destinationAirportId) throws FlightRouteExistException, UnknownPersistenceException, InputDataValidationException;

    public Long createNewReturnFlightRoute(FlightRoute returnFlightRoute, Long flightRouteId) throws FlightRouteExistException, UnknownPersistenceException, InputDataValidationException;

    public List<FlightRoute> getAllFlightRoute();

    public FlightRoute getFlightRouteById(Long flightRouteId, Boolean fetchAirport, Boolean fetchFlights) throws FlightRouteNotFoundException;

    public void removeFlightRoute(Long flightRouteId) throws FlightRouteNotFoundException;

    public void setFlightRouteDisabled(Long flightRouteId);
    
}
