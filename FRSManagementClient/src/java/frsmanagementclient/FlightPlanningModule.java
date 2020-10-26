/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.enumeration.UserRoleEnum;
import util.exception.InvalidAccessRightsException;

/**
 *
 * @author yeerouhew
 */
public class FlightPlanningModule 
{

    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;

    private Employee currentEmployee;
    
    public FlightPlanningModule() {
    }
    
    public FlightPlanningModule(Employee currentEmployee) 
    {
        this();
        this.currentEmployee = currentEmployee;
    }
    
    
    public void menuFlightPlanning() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.FLEET_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.ROUTE_PLANNER)
        {
            throw new InvalidAccessRightsException("You don't have the rights to access flight planning module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Flight Planning ***\n");
            System.out.println("1: Aircraft Configuration");
            System.out.println("2: Flight Route");
            System.out.println("3: Back\n");
            
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    doAircraftConfig();
                }
                else if(response == 2)
                {
                    doFlightRoute();
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
    
    private void doAircraftConfig()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Flight Planning :: Aircraft Configuration ***\n");
            System.out.println("1: Create Aircraft Configuration");
            System.out.println("2: View All Aircraft Configurations");
            System.out.println("3: View Aircraft Configuration Details");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                    doCreateAircraftConfig();
                }
                else if(response == 2)
                {
                    doViewAllAircraftConfig();
                }
                else if(response == 3)
                {
                    doViewAircraftConfigDetails();
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
 
    private void doFlightRoute()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Flight Planning :: Flight Route ***\n");
            System.out.println("1: Create Flight Route");
            System.out.println("2: View All Flight Routes");
            System.out.println("3: Delete Flight Route");
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
            {
                System.out.print("> ");
                
                response = scanner.nextInt();
                
                if(response == 1)
                {
                   doCreateFlightRoute();
                }
                else if(response == 2)
                {
                    doViewAllFlightRoute();
                }
                else if(response == 3)
                {
                    doDeleteFlightRoute();
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
    
    private void doCreateAircraftConfig()
    {
        
    }
    
    private void doViewAllAircraftConfig()
    {
        
    }
    
    private void doViewAircraftConfigDetails()
    {
        
    }
    
    private void doCreateFlightRoute()
    {
    }
    
    private void doViewAllFlightRoute()
    {
        
    }
    
    private void doDeleteFlightRoute()
    {
        
    }
    
}
