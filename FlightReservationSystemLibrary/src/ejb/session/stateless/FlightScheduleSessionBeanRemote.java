/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightSchedule;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InvalidDateTimeException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FlightScheduleSessionBeanRemote {
    
    public Long createNewFlightSchedule(FlightSchedule flightSchedule, Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException ;
    
    public FlightSchedule getFlightScheduleById(Long flightScheduleId) throws FlightScheduleNotFoundException;
    
    public List<FlightSchedule> searchDirectFlightSchedules(Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, String cabinClassPreference, Integer numPassengers);
    
    public List<FlightSchedule> searchSingleTransitConnectingFlightSchedule(Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, String cabinClassPreference, Integer numPassengers);
    
    public List<FlightSchedule> searchDoubleTransitConnectingFlightSchedule(Long departureAirportId, Long destinationAirportId, Date dateStart, Date dateEnd, String cabinClassPreference, Integer numPassengers);
    
    public Long createNewReturnFlightSchedule(FlightSchedule returnFlightSchedule, Long flightScheduleId, Long returnFlightSchedulePlanId) throws FlightSchedulePlanNotFoundException, FlightScheduleNotFoundException;

}
