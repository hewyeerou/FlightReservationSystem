/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CabinClassSessionBeanLocal;
import entity.CabinClass;
import entity.Fare;
import entity.SeatInventory;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.enumeration.CabinClassEnum;
import util.exception.CabinClassNotFoundException;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "CabinClassWebService")
@Stateless()
public class CabinClassWebService {

    @EJB
    private CabinClassSessionBeanLocal cabinClassSessionBeanLocal;

    
    @WebMethod(operationName = "retrieveCabinClassesByAircraftConfigIdUnmanaged")
    public List<CabinClass> retrieveCabinClassesByAircraftConfigIdUnmanaged(@WebParam(name = "aircraftConfigId") Long aircraftConfigId) {
        
        List<CabinClass> cabinClasses = cabinClassSessionBeanLocal.retrieveCabinClassesByAircraftConfigIdUnmanaged(aircraftConfigId);
        
        for (CabinClass cc: cabinClasses)
        {
            for (Fare fare: cc.getFares())
            {
                fare.setCabinClass(null);
            }
            
            for (SeatInventory si: cc.getSeatInventories())
            {
                si.setCabinClass(null);
            }
        }
        
        return cabinClasses;
    }
    
    public CabinClass retrieveCabinClassByIdUnmanaged(@WebParam(name = "cabinClassId") Long cabinClassId) throws CabinClassNotFoundException
    {
        CabinClass cc = cabinClassSessionBeanLocal.retrieveCabinClassById(cabinClassId);
        
        for (Fare fare: cc.getFares())
        {
            fare.setCabinClass(null);
        }

        for (SeatInventory si: cc.getSeatInventories())
        {
            si.setCabinClass(null);
        }
        
        return cc;
    }
    
    public CabinClass retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(@WebParam(name = "aircraftConfigId") Long aircraftConfigId, 
                                                                           @WebParam(name = "type") CabinClassEnum type) throws CabinClassNotFoundException
    {
        CabinClass cc = cabinClassSessionBeanLocal.retrieveCabinClassByAircraftConfigIdAndType(aircraftConfigId, type);
        
        for (Fare fare: cc.getFares())
        {
            fare.setCabinClass(null);
        }

        for (SeatInventory si: cc.getSeatInventories())
        {
            si.setCabinClass(null);
        }
        
        return cc;
    }
}
