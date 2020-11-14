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
import util.exception.InputDataValidationException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface FlightReservationRecordSessionBeanRemote {
    
    public Long createNewFlightReservationRecord(FlightReservationRecord flightReservationRecord, Long personId, List<Long> flightSchedules) throws InputDataValidationException;
    
    public List<FlightReservationRecord> retrieveReservationRecordsByCustomerId(Long personId);
    
    public FlightReservationRecord retrieveReservationRecordById(Long recordId, Long personId) throws FlightReservationRecordNotFoundException;
    
    public FlightReservationRecord getFlightReservationRecordByFlightScheduleId(Long flightReservationRecordId);
}
