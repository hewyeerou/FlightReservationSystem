/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightReservationRecord;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightReservationRecordNotFoundException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface FlightReservationRecordSessionBeanRemote {
    
    public Long createNewFlightReservationRecord(FlightReservationRecord flightReservationRecord, Long personId, List<Long> flightSchedules);
    
    public List<FlightReservationRecord> retrieveReservationRecordsByCustomerId(Long customerId);
    
    public FlightReservationRecord retrieveReservationRecordById(Long recordId) throws FlightReservationRecordNotFoundException;
    
}
