/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AirportIataCodeExistException;
import util.exception.AirportNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface AirportSessionBeanRemote {
    
    public Long createNewAirport(Airport newAirport) throws AirportIataCodeExistException, UnknownPersistenceException;
    
    public List<Airport> getAllAirports();
    
    public Airport getAirportByAirportId(Long airportId) throws AirportNotFoundException;
}
