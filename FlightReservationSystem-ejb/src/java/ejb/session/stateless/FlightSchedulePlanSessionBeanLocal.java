/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Fare;
import entity.FlightSchedulePlan;
import java.util.List;
import javax.ejb.Local;
import util.exception.FlightNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InputDataValidationException;

/**
 *
 * @author yeerouhew
 */
@Local
public interface FlightSchedulePlanSessionBeanLocal {

    public Long createNewFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan, String flightNum) throws FlightNotFoundException, InputDataValidationException;

    public Long createNewReturnFlightSchedulePlan(FlightSchedulePlan returnFlightSchedulePlan, Long flightSchedulePlanId) throws FlightNotFoundException, InputDataValidationException;

    public FlightSchedulePlan getFlightSchedulePlanById(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException;

    public List<FlightSchedulePlan> getAllFlightSchedulePlan();

    public void updateFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan) throws FlightSchedulePlanNotFoundException, InputDataValidationException;

    public void removeFlightSchedulePlan(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException;

    public void setFlightSchedulePlanDisabled(Long flightSchedulePlanId) throws FlightSchedulePlanNotFoundException;

    
}
