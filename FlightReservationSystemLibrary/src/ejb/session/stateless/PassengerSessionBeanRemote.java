/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Passenger;
import javax.ejb.Remote;

/**
 *
 * @author seowtengng
 */
@Remote
public interface PassengerSessionBeanRemote {
    
    public Long createNewPassenger(Passenger passenger, Long flightReservationRecordId, Long cabinSeatId);
    
}
