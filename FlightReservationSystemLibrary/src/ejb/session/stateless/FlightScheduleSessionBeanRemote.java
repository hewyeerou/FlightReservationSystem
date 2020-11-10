/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightSchedule;
import entity.SeatInventory;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FlightScheduleSessionBeanRemote {
    
    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException ;
    
    public FlightSchedule getFlightScheduleById(Long flightScheduleId) throws FlightScheduleNotFoundException;

    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException;
    
    public List<FlightSchedule> getFlightScheduleByFlightSchedulePlanId(Long flightSchedulePlanId);
    
    public void updateFlightSchedule(FlightSchedule flightSchedule) throws FlightScheduleNotFoundException;
    
    public void removeFlightSchedule(Long flightScheduleId) throws FlightScheduleNotFoundException;
//    public void removeFlightSchedule(Long flightScheduleId, SeatInventory seatInventory) throws FlightScheduleNotFoundException;
    
}
