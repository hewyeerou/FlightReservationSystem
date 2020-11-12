/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FareSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.UserRoleEnum;
import util.exception.InvalidAccessRightsException;
import util.exception.InvalidLoginCredentialException;
import ejb.session.stateless.SeatInventorySessionBeanRemote;

/**
 *
 * @author seowtengng
 */
public class MainApp {
    
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;
    private FlightSessionBeanRemote flightSessionBeanRemote;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    private SeatInventorySessionBeanRemote seatinventorySessionBeanRemote;
    private FareSessionBeanRemote fareSessionBeanRemote;

    private Employee currentEmployee;
    
    private FlightPlanningModule flightPlanningModule;
    private FlightOperationModule flightOperationModule;
    private SalesManagementModule salesManagementModule;
   
            
    public MainApp()
    {
    }

    public MainApp(PartnerSessionBeanRemote partnerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, FlightSessionBeanRemote flightSessionBean, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, SeatInventorySessionBeanRemote seatinventorySessionBeanRemote, FareSessionBeanRemote fareSessionBeanRemote)
    {
        this();
        
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.flightSessionBeanRemote = flightSessionBean;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.seatinventorySessionBeanRemote = seatinventorySessionBeanRemote;
        this.fareSessionBeanRemote = fareSessionBeanRemote;
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Welcome to Flight Reservation System (FRS) Management ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while (response < 1 || response > 2)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                scanner.nextLine();
                
                if(response == 1)
                {
                    try 
                    {
                        doLogin();
                        System.out.println("Login successful! \n");
                        
                        flightPlanningModule = new FlightPlanningModule(aircraftTypeSessionBeanRemote, aircraftConfigSessionBeanRemote, airportSessionBeanRemote, flightRouteSessionBeanRemote,currentEmployee);
                        flightOperationModule = new FlightOperationModule(currentEmployee, flightSessionBeanRemote, flightRouteSessionBeanRemote, aircraftConfigSessionBeanRemote, flightSchedulePlanSessionBeanRemote, flightScheduleSessionBeanRemote, seatinventorySessionBeanRemote, fareSessionBeanRemote);
                        salesManagementModule = new SalesManagementModule(currentEmployee, flightSessionBeanRemote);
                        
                        mainMenu();
                    } 
                    catch (InvalidLoginCredentialException ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
                else if (response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if (response == 2)
            {
                break;
            }
        }
    }
    
    private void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** FRS Management :: Management Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentEmployee = employeeSessionBeanRemote.employeeLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    private void mainMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Flight Reservation System (FRS) Management ***\n");
            String userRole = "";
            
            if(currentEmployee.getUserRoleEnum() == UserRoleEnum.EMPLOYEE)
            {
                userRole = "employee";
            }
            else if(currentEmployee.getUserRoleEnum() == UserRoleEnum.FLEET_MANAGER)
            {
                userRole = "fleet manager";
            }
            else if(currentEmployee.getUserRoleEnum() == UserRoleEnum.ROUTE_PLANNER)
            {
                userRole = "route planner";
            }
            else if(currentEmployee.getUserRoleEnum() == UserRoleEnum.SCHEDULE_MANAGER)
            {
                userRole = "schedule manager";
            }
            else if(currentEmployee.getUserRoleEnum() == UserRoleEnum.SALES_MANAGER)
            {
                userRole = "sales manager";
            }
            else
            {
                userRole = "administrator";
            }
            
            System.out.println("You are login as " + currentEmployee.getFirstName() + " " + currentEmployee.getLastName() + " with " + userRole + " rights\n");
            System.out.println("1: Flight Planning");
            System.out.println("2: Flight Operation");
            System.out.println("3: Sales Management");
            System.out.println("4: Logout\n");
            
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                scanner.nextLine();
                
                if(response == 1)
                {
                    try 
                    {
                        flightPlanningModule.menuFlightPlanning();
                        
                    } 
                    catch (InvalidAccessRightsException ex) 
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 2)
                {
                    try 
                    {
                        flightOperationModule.menuFlightOperation();
                    } 
                    catch (InvalidAccessRightsException ex) 
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                    
                }
                else if(response == 3)
                {
                    try 
                    {
                        salesManagementModule.menuSalesManagement();
                    } 
                    catch (InvalidAccessRightsException ex) 
                    {
                        System.out.println("Invalid option, please try again!: " + ex.getMessage() + "\n");
                    }
                }
                else if(response == 4)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if(response == 4)
            {
                break;
            }
            
        }
    }
}