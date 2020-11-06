/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SeatInventory;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author yeerouhew
 */
@Stateless
public class SeatinventorySessionBean implements SeatinventorySessionBeanRemote, SeatinventorySessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createSeatInventory(SeatInventory seatInventory)
    {   
        em.persist(seatInventory);
        em.flush();
        
        return seatInventory.getSeatInventoryId();
    }
}
