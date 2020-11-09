/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import entity.Airport;
import entity.Customer;
import entity.FlightSchedule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    
    private Customer currentCustomer;

    public MainApp()
    {
    }
    
    public MainApp (CustomerSessionBeanRemote customerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote)
    {
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
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
            System.out.println("1: Search Flights");
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
        Scanner scanner = new Scanner (System.in);
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Integer tripType = 0;
        Integer departureAirport = 0;
        Integer destinationAirport = 0;
        Long departureAirportId;
        Long destinationAirportId;
        String departureDate = "";
        String returnDate = "";
        Date formattedDepartureDate;
        Date formattedReturnDate;
        Integer numPassengers = 0;
        Integer flightTypePreference = 0;
        String cabinClassPreference = "";
        
        System.out.println("*** FRS Reservation :: Search Flights ***\n");

        while (true)
        {
            System.out.print("Enter trip type (1: One Way, 2: Round Trip)> ");
            tripType = scanner.nextInt();
            scanner.nextLine();
            
            if (tripType < 1 || tripType > 2)
            {
                System.out.println("Invalid option, please try again!\n");
            }
            else
            {
                break;
            }
        }
        
        List<Airport> airports = airportSessionBeanRemote.getAllAirports();
        
        while(true)
        {
            Integer option = 0;
            
            for(Airport airport: airports)
            {
                option++;
                System.out.println(option + ": " + airport.getIataCode());
            }
            
            System.out.println("");
            System.out.print("Select Departure Airport> ");
            departureAirport = scanner.nextInt();
            scanner.nextLine();
            
            if(departureAirport >= 1 && departureAirport <= option)
            {
                departureAirportId = airports.get(departureAirport-1).getAirportId();
                System.out.println("You have selected " + airports.get(departureAirport-1).getIataCode() + " as your departure airport.");
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!");
            }
        }
        
        while(true)
        {
            Integer option = 0;
            
            for(Airport airport: airports)
            {
                option++;
                System.out.println(option + ": " + airport.getIataCode());
            }
            
            System.out.println("");
            System.out.print("Select Destination Airport> ");
            destinationAirport = scanner.nextInt();
            scanner.nextLine();
            
            if(!destinationAirport.equals(departureAirport))
            {
                if(destinationAirport >= 1 && destinationAirport <= option)
                {
                    destinationAirportId = airports.get(destinationAirport-1).getAirportId();
                    System.out.println("You have selected " + airports.get(destinationAirport-1).getIataCode() + " as your destination airport.");
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!");
                }
            }
            else
            {
                System.out.println("You are not allowed to select the departure airport!");
            } 
        }
        
        while (true)
        {
            try
            {
                System.out.print("Enter Departure Date (dd-mm-yyyy)> ");
                departureDate = scanner.nextLine().trim();
                formattedDepartureDate = dateTimeFormatter.parse(departureDate + " 00:00");
                formattedReturnDate = dateTimeFormatter.parse("12-12-2020 00:00");

                if (tripType == 2)
                {
                    System.out.print("Enter Return Date (dd-mm-yyyy)> ");
                    returnDate = scanner.nextLine().trim();
                    formattedReturnDate = dateTimeFormatter.parse(returnDate + " 00:00");
                }
                
                break;
            }
            catch(ParseException ex) 
            {
                System.out.println("Date is in the wrong format, please try again!\n");
            }
        }
        
        while (true)
        {
            System.out.print("Enter Number of Passengers> ");
            numPassengers = scanner.nextInt();
            scanner.nextLine();
            
            if (numPassengers <= 0)
            {
                System.out.println("Number of passengers must be more than zero!\n");
            }
            else
            {
                break;
            }
        }
        
        while (true)
        {
            System.out.print("Enter your preference for (1: Direct Flight, 2: Connecting Flight, 3: No Preference)> ");
            flightTypePreference = scanner.nextInt();
            scanner.nextLine();
            
            if (flightTypePreference < 1 || flightTypePreference > 3)
            {
                System.out.println("Invalid option, please try again!\n");
            }
            else
            {
                break;
            }
        }
        
        while (true)
        {
            System.out.print("Enter you preference for (F: First Class, J: Business Class, W: Premiumn Economy Class, Y: Economy Class, NA: No Preference)> ");
            cabinClassPreference = scanner.nextLine().trim();
            
            if (!cabinClassPreference.equals("F") ||!cabinClassPreference.equals("J") || !cabinClassPreference.equals("W") || !cabinClassPreference.equals("Y") || !cabinClassPreference.equals("NA"))
            {
                System.out.println("Invalid option, please try again!\n");
            }
            else
            {
                break;
            }
        }
        
        if (flightTypePreference == 1 || flightTypePreference == 3)
        {
            doSearchDirectFlights(departureAirportId, destinationAirportId, formattedDepartureDate, cabinClassPreference, numPassengers);
            if (tripType == 2)
            {
                System.out.println("Return Flight:\n");
                doSearchDirectFlights(destinationAirportId, departureAirportId, formattedReturnDate, cabinClassPreference, numPassengers);
            }
        }
        else if (flightTypePreference == 2 || flightTypePreference == 3)
        {
            doSearchConnectingFlights(departureAirportId, destinationAirportId, formattedDepartureDate, cabinClassPreference, numPassengers);
            if (tripType == 2)
            {
                System.out.println("Return Flight:\n");
                doSearchConnectingFlights(destinationAirportId, departureAirportId, formattedReturnDate, cabinClassPreference, numPassengers);
            }
        }
    }
    
    public void doSearchDirectFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, String cabinClassPreference, Integer numPassengers)
    {
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, cabinClassPreference, numPassengers);

        GregorianCalendar beforeDepartureDateCalendar = new GregorianCalendar();
        beforeDepartureDateCalendar.setTime(formattedDepartureDate);
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, -3);
        Date formattedBeforeDepartureDate = beforeDepartureDateCalendar.getTime();
        List<FlightSchedule> beforeDepartureFlightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedBeforeDepartureDate, formattedDepartureDate, cabinClassPreference, numPassengers);

        GregorianCalendar afterDepartureDateCalendar = new GregorianCalendar();
        afterDepartureDateCalendar.setTime(formattedDepartureDateEnd);
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +3);
        Date formattedAfterDepartureDate = afterDepartureDateCalendar.getTime();
        List<FlightSchedule> afterDepartureFlightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDateEnd, formattedAfterDepartureDate, cabinClassPreference, numPassengers);
        
        doPrintDirectFlightSchedules(formattedDepartureDate, beforeDepartureFlightSchedules, flightSchedules, afterDepartureFlightSchedules); 
    }
    
    public void doSearchConnectingFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, String cabinClassPreference, Integer numPassengers)
    {
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        
        List<FlightSchedule> singleTransitFlightSchedules = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, cabinClassPreference, numPassengers);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Boolean firstLeg = true;
        
        for (FlightSchedule fs: singleTransitFlightSchedules)
        {
            if (firstLeg)
            {
                System.out.println(formatter.format(fs.getDepartureDateTime()) + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode());
                firstLeg = !firstLeg;
            }
            else
            {
                System.out.println(formatter.format(fs.getDepartureDateTime()) + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode());
                firstLeg = !firstLeg;
            }
        }
        
        List<FlightSchedule> doubleTransitFlightSchedules = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, cabinClassPreference, numPassengers);
        Boolean isFirstConnectingFlight = true;
        Boolean isSecondConnectingFlight = true;
        for (FlightSchedule fs: doubleTransitFlightSchedules)
        {
            if (isFirstConnectingFlight)
            {
                System.out.println(formatter.format(fs.getDepartureDateTime()) + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode());
                isFirstConnectingFlight = false;
            }
            else if (isSecondConnectingFlight)
            {
                System.out.println(formatter.format(fs.getDepartureDateTime()) + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode());
                isSecondConnectingFlight = false;
            }
            else
            {
                System.out.println(formatter.format(fs.getDepartureDateTime()) + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode());
                isFirstConnectingFlight = true;
                isSecondConnectingFlight = true;
            }
        }
    }
    
    public void doPrintDirectFlightSchedules(Date departureDate, List<FlightSchedule> beforeDepartureFlightSchedules, List<FlightSchedule> flightSchedules, List<FlightSchedule> afterDepartureFlightSchedules)
    {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        
        System.out.println("Flight Schedules Available on " + dateFormatter.format(departureDate) + ":\n");
        if (!flightSchedules.isEmpty())
        {
            System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
            for (FlightSchedule flightSchedule: flightSchedules)
            {
                System.out.printf("%10s%18s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(flightSchedule.getDepartureDateTime()), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
            }
            System.out.println("");
        }
        else
        {
            System.out.println("There are no flights available on " + dateFormatter.format(departureDate) + "!\n");
        }
        
        if (!flightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days before " + dateFormatter.format(departureDate) + ":\n");
            System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
            for (FlightSchedule flightSchedule: flightSchedules)
            {
                System.out.printf("%10s%18s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(flightSchedule.getDepartureDateTime()), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
            }
            System.out.println("");
        }
        else
        {
            System.out.println("There are no flights available 1-3 days before " + dateFormatter.format(departureDate) + "!\n");
        }
        
        if (!afterDepartureFlightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days after " + dateFormatter.format(departureDate) + ":\n");
            System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
            for (FlightSchedule flightSchedule: flightSchedules)
            {
                System.out.printf("%10s%18s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(flightSchedule.getDepartureDateTime()), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
            }
            System.out.println("");
        }
        else
        {
            System.out.println("There are no flights available 1-3 days after " + dateFormatter.format(departureDate) + "!\n");
        }
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