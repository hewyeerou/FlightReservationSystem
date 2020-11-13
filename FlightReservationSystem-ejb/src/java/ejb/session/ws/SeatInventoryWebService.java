/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.SeatInventorySessionBeanLocal;
import entity.CabinSeatInventory;
import entity.SeatInventory;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "SeatInventoryWebService")
@Stateless()
public class SeatInventoryWebService {

    @EJB
    private SeatInventorySessionBeanLocal seatInventorySessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged")
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(@WebParam(name = "cabinClassId") Long cabinClassId, 
                                                                                         @WebParam(name = "flightScheduleId") Long flightScheduleId) throws SeatInventoryNotFoundException
    {
        SeatInventory si = seatInventorySessionBeanLocal.retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(cabinClassId, flightScheduleId);
        
        si.getCabinClass().getSeatInventories().remove(si);
        
        si.getFlightSchedule().getSeatInventories().remove(si);
        
        for (CabinSeatInventory csi: si.getCabinSeatInventories())
        {
            csi.setSeatInventory(null);
        }
        
        return si;
    }
}
