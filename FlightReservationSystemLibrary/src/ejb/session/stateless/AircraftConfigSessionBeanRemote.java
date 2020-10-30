/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfig;
import java.util.List;
import javax.ejb.Remote;
import util.exception.AircraftConfigNameExistException;
import util.exception.AircraftConfigNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface AircraftConfigSessionBeanRemote {
    
    public Long createNewAircraftConfig(AircraftConfig newAircraftConfig, Long aircraftTypeId) throws AircraftConfigNameExistException, UnknownPersistenceException;
    
    public List<AircraftConfig> retrieveAllAircraftConfigs();
    
    public AircraftConfig retrieveAircraftConfigByName(String name) throws AircraftConfigNotFoundException;

    public AircraftConfig retrieveAircraftConfigById(Long aircraftId);
}
