/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CabinSeatInventorySessionBeanLocal;
import entity.CabinSeatInventory;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.CabinSeatInventoryExistException;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "CabinSeatInventoryWebService")
@Stateless()
public class CabinSeatInventoryWebService {

    @EJB
    private CabinSeatInventorySessionBeanLocal cabinSeatInventorySessionBeanLocal;

    
    @WebMethod(operationName = "createNewCabinSeatInventory")
    public Long createNewCabinSeatInventory(@WebParam(name = "cabinSeatInventory") CabinSeatInventory cabinSeatInventory, 
                                            @WebParam(name = "seatInventoryId") Long seatInventoryId,
                                            @WebParam(name = "passengerId") Long passengerId) throws CabinSeatInventoryExistException
    {
        Long csiId = cabinSeatInventorySessionBeanLocal.createNewCabinSeatInventory(cabinSeatInventory, seatInventoryId, passengerId);
        
        return csiId;
    }
    
    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventoryUnmanaged(@WebParam(name = "seatInventoryId") Long seatInventoryId)
    {
        List<CabinSeatInventory> seats = cabinSeatInventorySessionBeanLocal.retrieveCabinSeatInventoryInSeatInventoryUnmanaged(seatInventoryId);
        
        for (CabinSeatInventory csi: seats)
        {
            csi.getSeatInventory().getCabinSeatInventories().remove(csi);
        }
        
        return seats;
    }
}
