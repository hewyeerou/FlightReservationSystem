/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import entity.Partner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewPartner(Partner newPartner) throws PartnerUsernameExistException, UnknownPersistenceException
    {
        try
        {
            em.persist(newPartner);
            em.flush();
            
            return newPartner.getId();
        }
        catch(PersistenceException ex)
        {
            if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new PartnerUsernameExistException();
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            else
            {
                throw new UnknownPersistenceException(ex.getMessage());
            }
        }
    }
    
    
    @Override
    public Partner retrievePartnerByUsername(String username) throws PartnerNotFoundException
    {
        try
        {
            Query query = em.createQuery("SELECT p FROM Partner p WHERE p.username = :inUsername");
            query.setParameter("inUsername", username);

            return (Partner)query.getSingleResult(); 
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new PartnerNotFoundException("Partner username " + username + " does not exist!");
        }
    }
    
    @Override
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Partner partner = retrievePartnerByUsername(username);

            if(partner.getPassword().equals(password))
            {
                return partner;
            }
            else
            {
                throw new InvalidLoginCredentialException("Partner username " + username + " does not exist or invalid password!");
            }
        } 
        catch (PartnerNotFoundException ex) 
        {
            throw new InvalidLoginCredentialException("Partner username " + username + " does not exist or invalid password!");
        }
        
        
    }
}
