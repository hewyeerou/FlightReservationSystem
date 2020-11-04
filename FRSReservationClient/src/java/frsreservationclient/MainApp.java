/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import entity.Customer;
import java.util.Scanner;
import util.exception.CustomerUsernameExistException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
public class MainApp {
    
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    
    private Customer currentCustomer;

    public MainApp()
    {
    }
    
    public MainApp (CustomerSessionBeanRemote customerSessionBeanRemote)
    {
        this.customerSessionBeanRemote = customerSessionBeanRemote;
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            response = 0;
    
            {
                System.out.println("*** Welcome to Flight Reservation System (FRS) Reservation ***\n");
                System.out.println("1: Register Account");
                System.out.println("2: Login");
                System.out.println("3: Search for flights");
                System.out.println("4: Exit\n");

                while (response < 1 || response > 4)
                {
                    System.out.print("> ");
                    response = scanner.nextInt();
                    scanner.nextLine();

                    if (response == 1)
                    {
                        doRegisterCustomer();
                        
                        if (currentCustomer != null)
                        {
                            customerMenu();
                        }
                    }
                    else if (response == 2)
                    {
                        try{
                            doLogin();
                            System.out.println("Login successful! \n");
                            
                            if (currentCustomer != null)
                            {
                                customerMenu();
                            }
                        }
                        catch (InvalidLoginCredentialException ex)
                        {
                            System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                        }  
                    }
                    else if (response == 3)
                    {
                        doSearchFlight();
                    }
                    else if (response == 4)
                    {
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
            }
            
            if (response == 4)
            {
                break;
            }
        }
    }
    
    public void customerMenu()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            System.out.println("*** Flight Reservation System (FRS) Reservation ***\n");
            System.out.println("You are login as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "\n");
            System.out.println("1: Search for flights");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: View Flight Reservation Details");
            System.out.println("4: Logout\n");
            
            response = 0;

            while (response < 1 || response > 4)
            {
                System.out.print("> ");
                response = scanner.nextInt();
                scanner.nextLine();

                if (response == 1)
                {
                    doSearchFlight();
                }
                else if (response == 2)
                {
                    doViewAllFlightReservations();
                }
                else if (response == 3)
                {
                    doViewFlightReservationDetails();
                }
                else if (response == 4)
                {
                    doLogout();
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if (response == 4)
            {
                doLogout();
                System.out.println("You have logged out successfully!\n");
                break;
            }
        }
    }
    
    public void doRegisterCustomer()
    {
        Scanner scanner = new Scanner (System.in);
        Customer newCustomer = new Customer();
        String firstName = "";
        String lastName = "";
        String email = "";
        Integer mobileNumber = 0;
        String address = "";
        String username = "";
        String password = "";
        
        System.out.println("*** FRS Reservation :: Register Account ***\n");
        
        while (firstName.length() <= 0)
        {
            System.out.print("Enter First Name> ");
            firstName = scanner.nextLine().trim();
            
            if (firstName.length() > 0)
            {
                newCustomer.setFirstName(firstName);
            }
            else
            {
                System.out.println("First Name cannot be empty!");
            }
        }
        
        while (lastName.length() <= 0)
        {
            System.out.print("Enter Last Name> ");
            lastName = scanner.nextLine().trim();
            
            if (lastName.length() > 0)
            {
                newCustomer.setLastName(lastName);
            }
            else
            {
                System.out.println("Last Name cannot be empty!");
            }
        }
        
        while (email.length() <= 0)
        {
            System.out.print("Enter Email> ");
            email = scanner.nextLine().trim();
            
            if (email.length() > 0)
            {
                newCustomer.setEmail(email);
            }
            else
            {
                System.out.println("Email cannot be empty!");
            }
        }
        
        while (String.valueOf(mobileNumber).length() != 8)
        {
            System.out.print("Enter Mobile Number> ");
            mobileNumber = scanner.nextInt();
            scanner.nextLine();
            
            if (String.valueOf(mobileNumber).length() == 8)
            {
                if (mobileNumber / 10000000 == 8 || mobileNumber / 10000000 == 9 )
                {
                    newCustomer.setMobileNumber(String.valueOf(mobileNumber));
                }
                else
                {
                    System.out.println("Mobile Number should start with '8' or '9'!");
                }
            }
            else
            {
                System.out.println("Mobile number must be 8 characters long!");
            }
        }
        
        while (address.length() <= 0)
        {
            System.out.print("Enter Address> ");
            address = scanner.nextLine().trim();
            
            if (address.length() > 0)
            {
                newCustomer.setAddress(address);
            }
            else
            {
                System.out.println("Address cannot be empty!");
            }
        }
        
        while (username.length() <= 0)
        {
            System.out.print("Enter Username> ");
            username = scanner.nextLine().trim();
            
            if (username.length() > 0)
            {
                newCustomer.setUsername(username);
            }
            else
            {
                System.out.println("Username cannot be empty!");
            }
        }
        
        while (password.length() <= 0)
        {
            System.out.print("Enter Password> ");
            password = scanner.nextLine().trim();
            
            if (password.length() > 0)
            {
                newCustomer.setPassword(password);
            }
            else
            {
                System.out.println("Password cannot be empty!");
            }
        }
        
        try
        {
            Customer customer = customerSessionBeanRemote.createNewCustomer(newCustomer);
            System.out.println("You have successfully registered as a customer with Flight Reservation System!: " + customer.getId() + "\n");
            currentCustomer = customer;
        }
        catch (CustomerUsernameExistException ex)
        {
            System.out.println("An error has occurred while registering the new customer!: The username already exists!\n");
        }
        catch (UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while registering the new customer!: " + ex.getMessage() + "\n");
        }
    }
    
    public void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner (System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** FRS Reservation :: Customer Login ***\n");
        System.out.print("Enter username> ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password> ");
        password = scanner.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0)
        {
            currentCustomer = customerSessionBeanRemote.customerLogin(username, password);
        }
        else
        {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
    public void doSearchFlight()
    {
        
    }
    
    public void doViewAllFlightReservations()
    {
        
    }
    
    public void doViewFlightReservationDetails()
    {
        
    }
    
    public void doLogout()
    {
        currentCustomer = null;
    }
}