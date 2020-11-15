/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CabinClass;
import entity.Fare;
import entity.SeatInventory;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CabinClassEnum;
import util.exception.CabinClassNotFoundException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class CabinClassSessionBean implements CabinClassSessionBeanRemote, CabinClassSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public List<CabinClass> retrieveCabinClassesByAircraftConfigId(Long aircraftConfigId)
    {
        Query query = em.createQuery("SELECT cc FROM CabinClass cc WHERE cc.aircraftConfig.aircraftConfigId = :inAircraftConfigId");
        query.setParameter("inAircraftConfigId", aircraftConfigId);
        
        return query.getResultList();
    }
    
    @Override
    public List<CabinClass> retrieveCabinClassesByAircraftConfigIdUnmanaged(Long aircraftConfigId)
    {
        List<CabinClass> cabinClasses = retrieveCabinClassesByAircraftConfigId(aircraftConfigId);
        
        for (CabinClass cc: cabinClasses)
        {
            em.detach(cc);
            
            em.detach(cc.getAircraftConfig());
            
            for (Fare fare: cc.getFares())
            {
                em.detach(fare);
            }
            
            for (SeatInventory si: cc.getSeatInventories())
            {
                em.detach(si);
            }
        }
        
        return cabinClasses;
    }
    
    @Override
    public CabinClass retrieveCabinClassByAircraftConfigIdAndType(Long aircraftConfigId, CabinClassEnum type) throws CabinClassNotFoundException
    {
        Query query = em.createQuery("SELECT cc FROM CabinClass cc WHERE cc.aircraftConfig.aircraftConfigId = :inAircraftConfigId AND cc.cabinClassType = :type");
        query.setParameter("inAircraftConfigId", aircraftConfigId);
        query.setParameter("type", type);
        
        try
        {
            return (CabinClass)query.getSingleResult();
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new CabinClassNotFoundException("Cabin Class cannot be found does not exist!\n");
        }
    }
    
    @Override
    public CabinClass retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(Long aircraftConfigId, CabinClassEnum type) throws CabinClassNotFoundException
    {
        CabinClass cc = retrieveCabinClassByAircraftConfigIdAndType(aircraftConfigId, type);
        
        em.detach(cc);
            
        em.detach(cc.getAircraftConfig());

        for (Fare fare: cc.getFares())
        {
            em.detach(fare);
        }

        for (SeatInventory si: cc.getSeatInventories())
        {
            em.detach(si);
        }
        
        return cc;
    }
    
    @Override
    public CabinClass retrieveCabinClassById(Long cabinClassId) throws CabinClassNotFoundException
    {
        CabinClass cabinClass = em.find(CabinClass.class, cabinClassId);
        
        if (cabinClass != null)
        {
            cabinClass.getFares().size();
            cabinClass.getSeatInventories().size();
            
            return cabinClass;
        }
        else
        {
            throw new CabinClassNotFoundException("Cabin Class with ID " + cabinClassId + " does not exist!\n");
        }
    }
    
    @Override
    public CabinClass retrieveCabinClassByIdUnmanaged(Long cabinClassId) throws CabinClassNotFoundException
    {
        CabinClass cc = retrieveCabinClassById(cabinClassId);
        
        em.detach(cc);
            
        em.detach(cc.getAircraftConfig());

        for (Fare fare: cc.getFares())
        {
            em.detach(fare);
        }

        for (SeatInventory si: cc.getSeatInventories())
        {
            em.detach(si);
        }
        
        return cc;
    }
}
