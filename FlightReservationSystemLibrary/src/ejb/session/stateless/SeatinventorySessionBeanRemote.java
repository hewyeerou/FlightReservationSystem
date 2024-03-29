/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SeatInventory;
import java.util.List;
import javax.ejb.Remote;
import util.exception.FlightScheduleNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Remote
public interface SeatInventorySessionBeanRemote {
    
    public Long createSeatInventory(SeatInventory seatInventory, Long flightScheduleId, Long cabinClassId) throws FlightScheduleNotFoundException, InputDataValidationException;
    
    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(Long cabinClassId, Long flightScheduleId) throws SeatInventoryNotFoundException;
    
}
