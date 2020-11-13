/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.FlightReservationRecordSessionBeanLocal;
import entity.FlightReservationRecord;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "FlightReservationRecordWebService")
@Stateless()
public class FlightReservationRecordWebService {

    @EJB
    private FlightReservationRecordSessionBeanLocal flightReservationRecordSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createNewFlightReservationRecord")
    public Long createNewFlightReservationRecord(@WebParam(name = "flightReservationRecord") FlightReservationRecord flightReservationRecord,
                                                 @WebParam(name = "personId") Long personId,
                                                 @WebParam(name = "flightSchedules") List<Long> flightSchedules)
    {
        Long reservationId = flightReservationRecordSessionBeanLocal.createNewFlightReservationRecord(flightReservationRecord, personId, flightSchedules);
        
        return reservationId;
    }
}
