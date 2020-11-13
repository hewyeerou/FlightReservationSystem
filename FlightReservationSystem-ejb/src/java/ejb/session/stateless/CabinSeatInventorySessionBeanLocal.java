/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import java.util.List;
import javax.ejb.Local;
import util.exception.CabinSeatInventoryExistException;

/**
 *
 * @author seowtengng
 */
@Local
public interface CabinSeatInventorySessionBeanLocal {

    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventory(Long seatInventoryId);

    public Long createNewCabinSeatInventory(CabinSeatInventory cabinSeatInventory, Long seatInventoryId, Long passengerId) throws CabinSeatInventoryExistException;

    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventoryUnmanaged(Long seatInventoryId);
    
}
