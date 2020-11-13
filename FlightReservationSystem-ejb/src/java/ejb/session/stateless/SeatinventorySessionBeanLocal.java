/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SeatInventory;
import java.util.List;
import javax.ejb.Local;
import util.exception.FlightScheduleNotFoundException;
import util.exception.SeatInventoryNotFoundException;

/**
 *
 * @author yeerouhew
 */
@Local
public interface SeatInventorySessionBeanLocal {

    public Long createSeatInventory(SeatInventory seatInventory, Long flightScheduleId, Long cabinClassId) throws FlightScheduleNotFoundException;

    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(Long cabinClassId, Long flightScheduleId) throws SeatInventoryNotFoundException;

    public SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(Long cabinClassId, Long flightScheduleId) throws SeatInventoryNotFoundException;
    
}
