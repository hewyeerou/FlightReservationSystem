/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Fare;
import javax.ejb.Local;
import util.exception.FareBasisCodeExistException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Local
public interface FareSessionBeanLocal {

    public Long createNewFare(Fare newFare, Long flightSchedulePlanId, Long cabinClassId) throws FlightSchedulePlanNotFoundException, FareBasisCodeExistException, UnknownPersistenceException;
    
}
