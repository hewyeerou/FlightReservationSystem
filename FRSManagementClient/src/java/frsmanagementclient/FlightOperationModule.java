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
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.AircraftConfig;
import entity.Employee;
import entity.Flight;
import entity.FlightRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.UserRoleEnum;
import util.exception.FlightNumExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.InvalidAccessRightsException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author yeerouhew
 */
public class FlightOperationModule 
{
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;
    
    private FlightSessionBeanRemote flightSessionBean;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;
    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;

    private Employee currentEmployee;
    
    public FlightOperationModule() 
    {
    }
    
    public FlightOperationModule(Employee currentEmployee, FlightSessionBeanRemote flightSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote )
    {
        this();
        this.currentEmployee  = currentEmployee;
        this.flightSessionBean = flightSessionBean;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
    }

    public void menuFlightOperation() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.SCHEDULE_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightsException("You don't have the rights to access flight operation module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Flight Operation ***\n");
            System.out.println("1: Flight");
            System.out.println("2: Flight Schedule Plan");
            System.out.println("3: Back\n");
            
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    doFlight();
                }
                else if(response == 2)
                {
                    doFlightSchedulePlan();
                }
                else if(response == 3)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if(response == 3)
            {
                break;
            }  
        }
     
    }
    
    private void doFlight()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Flight Operation :: Flight ***\n");
            System.out.println("1: Create Flight");
            System.out.println("2: View All Flights");
            System.out.println("3: View Flight Details");
            System.out.println("4: Update Flight");
            System.out.println("5: Delete Flight");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    doCreateFlight();
                }
                else if(response == 2)
                {
                    doViewAllFlights();
                }
                else if(response == 3)
                {
                    doViewFlightDetails();
                }
                else if(response == 4)
                {
                    doUpdateFlight();
                }
                else if(response == 5)
                {
                    doDeleteFlight();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    private void doFlightSchedulePlan()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Flight Operation :: Flight Schedule Plan ***\n");
            System.out.println("1: Create Flight Schedule Plan");
            System.out.println("2: View All Flight Schedule Plans");
            System.out.println("3: View Flight Schedule Plan Details");
            System.out.println("4: Update Flight Schedule Plan");
            System.out.println("5: Delete Flight Schedule Plan");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    doCreateFlightSchedulePlan();
                }
                else if(response == 2)
                {
                    doViewAllFlightSchedulePlans();
                }
                else if(response == 3)
                {
                    doViewFlightSchedulePlanDetails();
                }
                else if(response == 4)
                {
                    doUpdateFlightSchedulePlan();
                }
                else if(response == 5)
                {
                    doDeleteFlightSchedulePlan();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if(response == 6)
            {
                break;
            }
        }
    }
    
    private void doCreateFlight()
    {
        Scanner scanner = new Scanner(System.in);
        Flight newFlight = new Flight();
        Flight newReturnFlight = new Flight();
        List<FlightRoute> flightRoutes = flightRouteSessionBeanRemote.getAllFlightRoute();
        List<AircraftConfig> aircraftConfigs = aircraftConfigSessionBeanRemote.retrieveAllAircraftConfigs();
        
        Long aircraftConfigId;
        Long flightRouteId;
        Integer aircraftConfigInt = 0;
        Integer flightRouteInt = 0;
       
        
        System.out.println("*** FRS Management :: Flight Operation :: Flight :: Create New Flight ***\n");
        
        //Outbound Flight
        System.out.print("Enter Flight Number> ");
        String flightNumber = scanner.nextLine().trim();
        
        while(true)
        {
            Integer option = 0;
            for(AircraftConfig aircraftConfig: aircraftConfigs)
            {
                option++;
                System.out.println(option + ": " + aircraftConfig.getName());
            }
            
            System.out.println("");
            System.out.print("Select Aicraft Configuration> ");
            aircraftConfigInt = scanner.nextInt();
            scanner.nextLine();
            
            if(aircraftConfigInt >= 1 && aircraftConfigInt <= option)
            {
                aircraftConfigId = aircraftConfigs.get(aircraftConfigInt-1).getAircraftConfigId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }  
        }
                
        while(true)
        {
            Integer option = 0;

            for(FlightRoute flightRoute: flightRoutes)
            {
                option++;
                System.out.println(option + ": " + flightRoute.getOrigin() + " - " + flightRoute.getDestination());
            }
            
            System.out.println("");
            System.out.print("Select Flight Route> ");
            flightRouteInt = scanner.nextInt();
            scanner.nextLine();
            
            if(flightRouteInt >= 1 && flightRouteInt <= option)
            {
                flightRouteId = flightRoutes.get(flightRouteInt-1).getFlightRouteId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        try
        {
            newFlight.setFlightNumber("ML" + flightNumber);
            Long flightId = flightSessionBean.createFlight(newFlight, flightRouteId, aircraftConfigId);

            System.out.println("New flight created successfully!: " + flightId + "\n");

            //Return Flight 
            FlightRoute flightRoute = flightRouteSessionBeanRemote.getFlightRouteById(flightRouteId, true, false);

            if(flightRoute.getReturnFlightRoute().getFlightRouteId() != flightRoute.getFlightRouteId())
            {
                System.out.println("Do you want to create a complementary return flight?");
                while(true)
                {
                    Integer option = 0;

                    System.out.println("1. Yes");
                    System.out.println("2. No");
                    System.out.print("> ");
                    option = scanner.nextInt();

                    scanner.nextLine();
                    if(option >=1 && option <= 2)
                    {
                        if(option == 1)
                        {
                            System.out.print("Enter Return Flight Number> ");
                            String returnFlightNumber = scanner.nextLine().trim();

                            newReturnFlight.setFlightNumber("ML" + returnFlightNumber);
                            Long returnFlightId = flightSessionBean.createReturnFlight(newReturnFlight, flightId);
                        }
                    }
                }
            }
        }
        catch (FlightRouteNotFoundException ex) 
        {
            System.out.println("Flight Route does not exist!");
        }
        catch(FlightNumExistException ex)
        {
            System.out.println("Duplicate flight record: The flight already exists!\n");
        } 
        catch (UnknownPersistenceException ex) 
        {
            System.out.println("An unknown error has occurred while creating the new flight!: " + ex.getMessage() + "\n");
        } 
    }
  
    private void doViewAllFlights()
    {
        
    }
    
    private void doViewFlightDetails()
    {
        
    }
    
    private void doUpdateFlight()
    {
        
    }
    
    private void doDeleteFlight()
    {
        
    }
    
    private void doCreateFlightSchedulePlan()
    {
        
    }
    
    private void doViewAllFlightSchedulePlans()
    {
        
    }
    
    private void doViewFlightSchedulePlanDetails()
    {
        
    }
    
    private void doUpdateFlightSchedulePlan()
    {
        
    }
    
    private void doDeleteFlightSchedulePlan()
    {
        
    }
}
