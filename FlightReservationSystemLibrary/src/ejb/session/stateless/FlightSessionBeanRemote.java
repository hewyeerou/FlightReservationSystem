/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Flight;
import javax.ejb.Remote;
import util.exception.FlightNumExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FlightSessionBeanRemote 
{
    public Long createFlight(Flight newFlight, Long flightRouteId, Long aircraftConfigId) throws FlightNumExistException, UnknownPersistenceException;
    
    public Long createReturnFlight(Flight newReturnFlight, Long flightId) throws FlightNumExistException, UnknownPersistenceException;
}
