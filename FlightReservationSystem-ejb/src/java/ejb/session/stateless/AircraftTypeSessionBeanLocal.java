/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftType;
import java.util.List;
import javax.ejb.Local;
import util.exception.AircraftTypeNameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Local
public interface AircraftTypeSessionBeanLocal {

    public Long createNewAircraftType(AircraftType newAircraftType) throws AircraftTypeNameExistException, UnknownPersistenceException;
    
    public List<AircraftType> retrieveAllAircraftTypes();
    
}
