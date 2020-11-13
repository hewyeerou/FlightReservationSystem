/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.FareSessionBeanLocal;
import entity.Fare;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.FareNotFoundException;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "FareWebService")
@Stateless()
public class FareWebService {

    @EJB
    private FareSessionBeanLocal fareSessionBeanLocal;
    
    @WebMethod(operationName = "getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged")
    public List<Fare> getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(@WebParam(name = "name") Long flightSchedulePlanId,
                                                                            @WebParam(name = "cabinClassId") Long cabinClassId)
    {
        List<Fare> fares = fareSessionBeanLocal.getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedulePlanId, cabinClassId);
        
        for (Fare fare: fares)
        {
            fare.getCabinClass().getFares().remove(fare);
            
            fare.getFlightSchedulePlan().getFares().remove(fare);
        }
        
        return fares;
    }
    
    public BigDecimal getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(@WebParam(name = "flightSchedulePlanId") Long flightSchedulePlanId, 
                                                                                   @WebParam(name = "cabinClassId") Long cabinClassId) throws FareNotFoundException
    {
        BigDecimal price = fareSessionBeanLocal.getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedulePlanId, cabinClassId);
        
        return price;
    }
}
