/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightReservationRecord;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author seowtengng
 */
@Local
public interface FlightReservationRecordSessionBeanLocal {

    public Long createNewFlightReservationRecord(FlightReservationRecord flightReservationRecord, Long personId, List<Long> flightSchedules);

    public FlightReservationRecord getFlightReservationRecordByFlightScheduleId(Long flightReservationRecordId);
    
}
