/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.CabinClassEnum;
import util.exception.CabinClassNotFoundException;

/**
 *
 * @author seowtengng
 */
@Local
public interface CabinClassSessionBeanLocal {

    public List<CabinClass> retrieveCabinClassesByAircraftConfigId(Long aircraftConfigId);

    public CabinClass retrieveCabinClassByAircraftConfigIdAndType(Long aircraftConfigId, CabinClassEnum type) throws CabinClassNotFoundException;

    public CabinClass retrieveCabinClassById(Long cabinClassId);
    
}
