/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SeatInventory;
import javax.ejb.Local;
import util.exception.FlightScheduleNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Local
public interface SeatinventorySessionBeanLocal {

    public Long createSeatInventory(SeatInventory seatInventory, Long flightScheduleId, Long cabinClassId) throws FlightScheduleNotFoundException;
    
}
