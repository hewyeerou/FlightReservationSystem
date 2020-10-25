/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import javax.ejb.Remote;
import util.exception.AirportIataCodeExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface AirportSessionBeanRemote {
    
    public Long createNewAirport(Airport newAirport) throws AirportIataCodeExistException, UnknownPersistenceException;
    
}
