/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.partner.InvalidLoginCredentialException;
import ws.client.partner.InvalidLoginCredentialException_Exception;
import ws.client.partner.Partner;
import ws.client.partner.PartnerNotFoundException_Exception;

/**
 *
 * @author yeerouhew
 */
public class HolidayReservationSystem {

    public static Partner currentPartner;
    
    public static void main(String[] args) 
    {
       runApp();
    }
    
    private static void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;

        while (true) 
        {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            
            response = 0;
            
            while(response < 1 || response > 2)
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
                        
                        if(currentPartner != null)
                        {
                            partnerMenu();
                        }
                    }
                    catch (InvalidLoginCredentialException_Exception ex) 
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                    catch (PartnerNotFoundException_Exception ex) 
                    {
                        Logger.getLogger(HolidayReservationSystem.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else if(response == 2)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if(response == 2)
            {
                break;
            }
        }
    }
    
    private static void partnerMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("You are login as " + currentPartner.getName() + "\n");
            System.out.println("1: Search for flights");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: View Flight Reservation Details");
            System.out.println("4: Logout\n");
            
            response = 0;
            
            while(response < 1 || response > 4)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                scanner.nextLine();
                
                if(response == 1)
                {
                    //partner search flight
                }
                else if(response == 2)
                {
                    //partner view flight reservation                    
                }
                else if(response == 3)
                {
                    //partner view flight reservation details
                }
                else if(response == 4)
                {
                    doLogout();
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if(response == 4)
            {
                doLogout(); 
                System.out.println("You have logged out successfully!\n");
                break;
            }
        }
    }
    
    private static void doLogin() throws InvalidLoginCredentialException_Exception, PartnerNotFoundException_Exception
    {
        Scanner scanner = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** Holiday Reservation System:: Partner Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentPartner = partnerLogin(username, password);
        }
        else
        {
            System.out.println("Missing login credentials!");
        }
    }
    
    private static void doLogout()
    {
        currentPartner = null;
    }

    
    //web service methods
    private static Partner retrievePartnerByUsername(java.lang.String username) throws PartnerNotFoundException_Exception 
    {
        ws.client.partner.PartnerWebService_Service service = new ws.client.partner.PartnerWebService_Service();
        ws.client.partner.PartnerWebService port = service.getPartnerWebServicePort();
        return port.retrievePartnerByUsername(username);
    }

    private static Partner partnerLogin(java.lang.String username, java.lang.String password) throws InvalidLoginCredentialException_Exception, PartnerNotFoundException_Exception {
        ws.client.partner.PartnerWebService_Service service = new ws.client.partner.PartnerWebService_Service();
        ws.client.partner.PartnerWebService port = service.getPartnerWebServicePort();
        return port.partnerLogin(username, password);
    }
    
    
    
    
    
}
