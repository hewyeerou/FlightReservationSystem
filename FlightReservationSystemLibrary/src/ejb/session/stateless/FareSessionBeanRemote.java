/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Fare;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FareBasisCodeExistException;
import util.exception.FareNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FareSessionBeanRemote {
    
    public Long createNewFare(Fare newFare, Long flightSchedulePlanId, Long cabinClassId) throws FlightSchedulePlanNotFoundException, FareBasisCodeExistException, UnknownPersistenceException;
    
    public List<Fare> getFaresByFlightSchedulePlanId(Long flightSchedulePlanId);

    public void updateFare(Fare fare) throws FareNotFoundException;
}
