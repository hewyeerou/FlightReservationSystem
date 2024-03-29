/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AircraftConfig;
import entity.AircraftType;
import entity.CabinClass;
import entity.Flight;
import entity.FlightReservationRecord;
import java.util.List;
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
import util.exception.AircraftConfigNameExistException;
import util.exception.AircraftConfigNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Stateless
public class AircraftConfigSessionBean implements AircraftConfigSessionBeanRemote, AircraftConfigSessionBeanLocal {

    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AircraftConfigSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    } 
    
    @Override
    public Long createNewAircraftConfig(AircraftConfig newAircraftConfig, Long aircraftTypeId) throws AircraftConfigNameExistException, UnknownPersistenceException, InputDataValidationException
    {
        Set<ConstraintViolation<AircraftConfig>>constraintViolations = validator.validate(newAircraftConfig);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                AircraftType aircraftType = em.find(AircraftType.class, aircraftTypeId);
                newAircraftConfig.setAircraftType(aircraftType); 
                aircraftType.getAircraftConfigs().add(newAircraftConfig);

                for (CabinClass cabinClass: newAircraftConfig.getCabinClasses())
                {
                    cabinClass.setAircraftConfig(newAircraftConfig); 
                    em.persist(cabinClass);
                }

                em.persist(newAircraftConfig);
                em.flush();
                return newAircraftConfig.getAircraftConfigId();
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new AircraftConfigNameExistException();
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
    public List<AircraftConfig> retrieveAllAircraftConfigs()
    {
        Query query = em.createQuery("SELECT a FROM AircraftConfig a ORDER BY a.aircraftType ASC, a.name ASC");

        return query.getResultList();
    }
    
    @Override
    public AircraftConfig retrieveAircraftConfigByName(String name) throws AircraftConfigNotFoundException
    {
        Query query = em.createQuery(("SELECT a FROM AircraftConfig a WHERE a.name = :inName"));
        query.setParameter("inName", name);
        
        try
        {
            AircraftConfig aircraftConfig = (AircraftConfig)query.getSingleResult();
            aircraftConfig.getCabinClasses().size();
            
            return aircraftConfig;
        }
        catch (NoResultException | NonUniqueResultException ex)
        {
            throw new AircraftConfigNotFoundException("Name " + name + " does not exist!");
        }
    }
    

    @Override
    public AircraftConfig retrieveAircraftConfigById(Long aircraftId) 
    {
        AircraftConfig aircraftConfig = em.find(AircraftConfig.class, aircraftId);
        
        aircraftConfig.getCabinClasses().size();
        aircraftConfig.getAircraftType();
       
        return aircraftConfig;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AircraftConfig>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
