/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinSeatInventory;
import entity.SeatInventory;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author seowtengng
 */
@Stateless
public class CabinSeatInventorySessionBean implements CabinSeatInventorySessionBeanRemote, CabinSeatInventorySessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public List<CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventory(Long seatInventoryId)
    {
        Query query = em.createQuery("SELECT c FROM CabinSeatInventory c WHERE c.seatInventory.seatInventoryId = :inSeatInventoryId ORDER BY c.seatTaken ASC");
        query.setParameter("inSeatInventoryId", seatInventoryId);
        
        return query.getResultList();
    } 
    
    @Override
    public Long createNewCabinSeatInventory(CabinSeatInventory cabinSeatInventory, Long seatInventoryId)
    {
        SeatInventory seatInventory = em.find(SeatInventory.class, seatInventoryId);
        
        cabinSeatInventory.setSeatInventory(seatInventory);
        seatInventory.getCabinSeatInventories().add(cabinSeatInventory);
        seatInventory.setNumOfBalanceSeats(seatInventory.getNumOfBalanceSeats() - 1);
        seatInventory.setNumOfReservedSeats(seatInventory.getNumOfReservedSeats() + 1);
        em.persist(cabinSeatInventory);
        em.flush();
        
        return cabinSeatInventory.getCabinSeatInventoryId();
    }
}
