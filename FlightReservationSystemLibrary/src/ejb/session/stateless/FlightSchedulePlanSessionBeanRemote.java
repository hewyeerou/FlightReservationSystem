/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.FlightSchedulePlan;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface FlightSchedulePlanSessionBeanRemote {
    
    public Long createNewFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan, String flightNum) throws FlightNotFoundException;
    
    public Long createNewReturnFlightSchedulePlan(FlightSchedulePlan returnFlightSchedulePlan, Long flightSchedulePlanId) throws FlightNotFoundException;

    public FlightSchedulePlan getFlightSchedulePlanById(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException;
    
    public List<FlightSchedulePlan> getAllFlightSchedulePlan();
}
