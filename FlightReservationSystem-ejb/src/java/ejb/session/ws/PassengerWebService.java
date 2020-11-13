/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PassengerSessionBeanLocal;
import entity.CabinSeatInventory;
import entity.Passenger;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.PassengerNotFoundException;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "PassengerWebService")
@Stateless()
public class PassengerWebService {

    @EJB
    private PassengerSessionBeanLocal passengerSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "retrievePassengerByPassengerIdUnmanaged")
    public Passenger retrievePassengerByPassengerIdUnmanaged(@WebParam(name = "passengerId") Long passengerId) throws PassengerNotFoundException
    {
        Passenger p = passengerSessionBeanLocal.retrievePassengerByPassengerIdUnmanaged(passengerId);
        
        p.getFlightReservationRecord().getPassengers().remove(p);
        
        return p;
    }
    
    public Long createNewPassenger(@WebParam(name = "passenger") Passenger passenger,
                                   @WebParam(name = "flightReservationRecordId") Long flightReservationRecordId)
    {
        Long passengerId = passengerSessionBeanLocal.createNewPassenger(passenger, flightReservationRecordId);
        
        return passengerId;
    }
}
