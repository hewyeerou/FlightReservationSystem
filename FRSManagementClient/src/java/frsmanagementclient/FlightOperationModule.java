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
import util.enumeration.UserRoleEnum;
import util.exception.InvalidAccessRightsException;

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

    private Employee currentEmployee;
    
    public FlightOperationModule() {
    }
    
    public FlightOperationModule(Employee currentEmployee)
    {
        this();
        this.currentEmployee  = currentEmployee;
    }

    public void menuFlightOperation() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.SCHEDULE_MANAGER)
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
                System.out.println("> ");
                
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
