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
 * @author seowtengng
 */
@Singleton
@LocalBean
//@Startup

public class DataInitSessionBean {

    
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

    public DataInitSessionBean() 
    {
    }

    @PostConstruct
    public void postConstruct()
    {
        try
        {
            employeeSessionBeanLocal.retrieveEmployeeByUsername("fleet manager");
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
                employeeSessionBeanLocal.createNewEmployee(new Employee("Admin", "01", UserRoleEnum.SYSTEM_ADMIN, "admin", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Alice", "Maison", UserRoleEnum.EMPLOYEE, "employee", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Jane", "Kwa", UserRoleEnum.FLEET_MANAGER, "fleet manager", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("John", "Doe", UserRoleEnum.ROUTE_PLANNER, "route planner", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Laura", "Tan", UserRoleEnum.SCHEDULE_MANAGER, "schedule manager", "password"));
                employeeSessionBeanLocal.createNewEmployee(new Employee("Tom", "Tang", UserRoleEnum.SALES_MANAGER, "sales manager", "password"));

                partnerSessionBeanLocal.createNewPartner(new Partner("Holiday.com", "holiday.com", "password"));

                airportSessionBeanLocal.createNewAirport(new Airport("SIN", "Singapore Changi Airport", "Singapore", null , "Singapore", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("TPE", "Taoyuan International Airport", "Taoyuan", null , "Taiwan", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("MEP", "Mersing Airport", "Mersing", "Johor" , "Malaysia", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("MKZ", "Malacca International Airport", "Malacca City", "Malacca" , "Malaysia", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("MBE", "Monbetsu Airport", "Monbetsu", "Hokkaido" , "Japan", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("HND", "Tokyo International Airport", "Tokyo", "Honshu" , "Japan", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("ITM", "Osaka International Airport", "Osaka", "Honshu" , "Japan", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("ICN", "Incheon International Airport", "Seoul", null , "South Korea", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("GMP", "Gimpo International Airport", "Seoul", null , "South Korea", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("CJU", "Jeju International Airport", "Jeju", null , "South Korea", 1));
                airportSessionBeanLocal.createNewAirport(new Airport("BKK", "Suvarnabhumi Airport", "Bangkok", null , "Thailand", -1));
                airportSessionBeanLocal.createNewAirport(new Airport("DMK", "Don Mueang International Airport", "Bangkok", null , "Thailand", -1));
                airportSessionBeanLocal.createNewAirport(new Airport("HKT", "Phuket International Airport", "Phuket", null , "Thailand", -1));
                airportSessionBeanLocal.createNewAirport(new Airport("CGK", "Soekarno-Hatta International Airport", "Jakarta", null , "Indonesia", -1));
                airportSessionBeanLocal.createNewAirport(new Airport("DPS", "Ngurah Rai International Airport", "Bali", null , "Indonesia", -1));
                airportSessionBeanLocal.createNewAirport(new Airport("PEK", "Beijing Capital International Airport", "Beijing", null , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("FOC", "Fuzhou Changle International Airport", "Fuzhou", "Fujian" , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("CAN", "Guangzhou Baiyun International Airport", "Guangzhou", "Guangdong" , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("HRB", "Harbin Taiping International Airport", "Harbin", "Heilongjiang" , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("PVG", "Shanghai Pudong International Airport", "Shanghai", null , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("SHA", "Shanghai Hongqiao International Airport", "Shanghai", null , "China", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("HKG", "Hong Kong International Airport", "Hong Kong", null , "Hong Kong", 0));
                airportSessionBeanLocal.createNewAirport(new Airport("BNE", "Brisbane Airport", "Brisbane", "Queensland" , "Australia", 2));
                airportSessionBeanLocal.createNewAirport(new Airport("MEL", "Melbourne Airport", "Melbourne", "Victoria" , "Australia", 3));
                airportSessionBeanLocal.createNewAirport(new Airport("SYD", "Sydney Airport", "Sydney", "New South Wales" , "Australia", 3));
                airportSessionBeanLocal.createNewAirport(new Airport("SGN", "Tan Son Nhat International Airport", "Ho Chi Minh City", null , "Vietnam", -1));
                airportSessionBeanLocal.createNewAirport(new Airport("HAN", "Noi Bai International Airport", "Hanoi", null , "Vietnam", -1));

                aircraftTypeSessionBeanLocal.createNewAircraftType(new AircraftType("Boeing 737", 189));
                aircraftTypeSessionBeanLocal.createNewAircraftType(new AircraftType("Boeing 747", 366));
            }
        }
        catch(EmployeeUsernameExistException | PartnerUsernameExistException | AirportIataCodeExistException | AircraftTypeNameExistException | UnknownPersistenceException ex)
        {
            ex.printStackTrace();
        }
    }
}
