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
import java.util.Scanner;

/**
 *
 * @author seowtengng
 */
public class MainApp {
    
    private PartnerSessionBeanRemote partnerSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    private EmployeeSessionBeanRemote employeeSessionBeanRemote;

    public MainApp()
    {
    }

    public MainApp(PartnerSessionBeanRemote partnerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote, EmployeeSessionBeanRemote employeeSessionBeanRemote)
    {
        this.partnerSessionBeanRemote = partnerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
        this.employeeSessionBeanRemote = employeeSessionBeanRemote;
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Welcome to Flight Reservation System Management ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while (response < 1 || response > 2)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                
                if (response == 2)
                {
                    break;
                }
            }
            
            if (response == 2)
            {
                break;
            }
        }
    }
}
