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
public class SalesManagementModule {
    

    private Employee currentEmployee;

    public SalesManagementModule() {
    }
    
    public SalesManagementModule(Employee currentEmployee) 
    {
        this();
        this.currentEmployee = currentEmployee;
    }
    
    public void menuSalesManagement() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.SALES_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightsException("You don't have the rights to access sales management module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Sales Management ***\n");
            System.out.println("1: View Seat Inventory");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: Back\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doViewSeatInventory();
                }
                else if(response == 2)
                {
                    doViewFlightReservations();
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
    
    private void doViewSeatInventory(){
        
    }
    
    private void doViewFlightReservations(){
        
    }
}
