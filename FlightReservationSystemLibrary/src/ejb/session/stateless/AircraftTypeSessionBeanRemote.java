/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftType;
import javax.ejb.Remote;
import util.exception.AircraftTypeNameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface AircraftTypeSessionBeanRemote {
    
    public Long createNewAircraftType(AircraftType newAircraftType) throws AircraftTypeNameExistException, UnknownPersistenceException;
    
}
