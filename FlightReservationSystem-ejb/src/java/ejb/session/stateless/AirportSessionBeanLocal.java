/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Airport;
import javax.ejb.Local;
import util.exception.AirportIataCodeExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Local
public interface AirportSessionBeanLocal {

    public Long createNewAirport(Airport newAirport) throws AirportIataCodeExistException, UnknownPersistenceException;
    
}
