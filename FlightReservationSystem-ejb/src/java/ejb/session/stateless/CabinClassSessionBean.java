/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author seowtengng
 */
@Stateless
public class CabinClassSessionBean implements CabinClassSessionBeanRemote, CabinClassSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    public List<CabinClass> retrieveCabinClassesByAircraftConfigId(Long aircraftConfigId)
    {
        Query query = em.createQuery("SELECT cc FROM CabinClass cc WHERE cc.aircraftConfig.aircraftConfigId = :inAircraftConfigId");
        query.setParameter("inAircraftConfigId", aircraftConfigId);
        
        return query.getResultList();
    }
}
