/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightSchedule;
import javax.ejb.Local;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Local
public interface FlightScheduleSessionBeanLocal {

    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId, Long seatInventoryId) throws FlightSchedulePlanNotFoundException;

    public FlightSchedule getFlightScheduleById(Long flightScheduleId) throws FlightScheduleNotFoundException;

    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId, Long seatInventoryId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException;
    
}
