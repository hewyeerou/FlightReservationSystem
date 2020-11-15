/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Customer;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class CustomerSessionBean implements CustomerSessionBeanRemote, CustomerSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public CustomerSessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Customer createNewCustomer (Customer newCustomer) throws CustomerUsernameExistException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<Customer>>constraintViolations = validator.validate(newCustomer);
        
        if (constraintViolations.isEmpty())
        { 
            try
            {
                em.persist(newCustomer);
                em.flush();

                return newCustomer;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new CustomerUsernameExistException();
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
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public Customer retrieveCustomerByUsername (String username) throws CustomerNotFoundException
    {
        try
        {
           Query query = em.createQuery("SELECT c FROM Customer c WHERE c.username = :inUsername");
            query.setParameter("inUsername", username);
        
            return (Customer)query.getSingleResult(); 
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new CustomerNotFoundException("Customer username " + username + " does not exist!");
        }
    }
    
    @Override
    public Customer customerLogin (String username, String password) throws InvalidLoginCredentialException
    {
        try
        {
            Customer customer = retrieveCustomerByUsername(username);
            
            if (customer.getPassword().equals(password))
            {
                return customer;
            }
            else
            {
                throw new InvalidLoginCredentialException("Customer username " + username + " does not exist or invalid password!");
            }
        }
        catch (CustomerNotFoundException ex)
        {
            throw new InvalidLoginCredentialException("Customer username " + username + " does not exist or invalid password!");
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Customer>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
