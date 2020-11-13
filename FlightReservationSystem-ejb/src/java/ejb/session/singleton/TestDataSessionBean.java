/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AircraftTypeSessionBeanLocal;
import ejb.session.stateless.AirportSessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.AircraftType;
import entity.Airport;
import entity.Employee;
import entity.Partner;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.UserRoleEnum;
import util.exception.AircraftTypeNameExistException;
import util.exception.AirportIataCodeExistException;
import util.exception.EmployeeNotFoundException;
import util.exception.EmployeeUsernameExistException;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
@Singleton
@LocalBean
@Startup
public class TestDataSessionBean {

    @EJB
    private AircraftTypeSessionBeanLocal aircraftTypeSessionBeanLocal;
    @EJB
    private AirportSessionBeanLocal airportSessionBeanLocal;
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    
    @PersistenceContext(unitName = "FlightReservationSystem-ejbPU")
    private EntityManager em;

    
    @PostConstruct
    public void postConstruct()
    {
        try
        {
            employeeSessionBeanLocal.retrieveEmployeeByUsername("fleetmanager");
        }
        catch (EmployeeNotFoundException ex)
        {
            initialiseData();
        }
    }
    
    public void initialiseData()
    {
        try
        {
            if(em.find(Employee.class, 1l) == null && em.find(Partner.class, 1l) == null && em.find(Airport.class, 1l) == null && em.find(AircraftType.class, 1l) == null)
            {
                employeeSessionBeanLocal.createNewEmployee(new Employee("Fleet", "Manager", UserRoleEnum.FLEET_MANAGER, "fleetmanager", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Route", "Planner", UserRoleEnum.ROUTE_PLANNER, "routeplanner", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Schedule", "Manager", UserRoleEnum.SCHEDULE_MANAGER, "schedulemanager", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Sales", "Manager", UserRoleEnum.SALES_MANAGER, "salesmanager", "password"));

                partnerSessionBeanLocal.createNewPartner(new Partner("Holiday.com", "holidaydotcom", "password"));

                airportSessionBeanLocal.createNewAirport(new Airport("SIN", "Changi", "Singapore", "Singapore" , "Singapore", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("HKG", "Hong Kong", "Chek Lap Kok", "Hong Kong" , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("TPE", "Taoyuan", "Taoyuan", "Taipei" , "Taiwan R.O.C.", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("NRT", "Narita", "Narita", "Chiba" , "Japan", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("SYD", "Sydney", "Sydney", "New South Wales" , "Australia", 3));

                aircraftTypeSessionBeanLocal.createNewAircraftType(new AircraftType("Boeing 737", 200));
                aircraftTypeSessionBeanLocal.createNewAircraftType(new AircraftType("Boeing 747", 400));
            }
        }
        catch(EmployeeUsernameExistException | PartnerUsernameExistException | AirportIataCodeExistException | AircraftTypeNameExistException | UnknownPersistenceException ex)
        {
            ex.printStackTrace();
        }
    }
}
