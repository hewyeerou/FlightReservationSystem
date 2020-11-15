/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Passenger;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.PassengerNotFoundException;

/**
 *
 * @author seowtengng
 */
@Local
public interface PassengerSessionBeanLocal {

    public Long createNewPassenger(Passenger passenger, Long flightReservationRecordId) throws InputDataValidationException;

    public Passenger retrievePassengerByPassengerId(Long passengerId) throws PassengerNotFoundException;

    public Passenger retrievePassengerByPassengerIdUnmanaged(Long passengerId) throws PassengerNotFoundException;
    
}
