/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FareSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.SeatInventorySessionBeanRemote;
import entity.Airport;
import entity.CabinClass;
import entity.Customer;
import entity.Fare;
import entity.FlightSchedule;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import util.enumeration.CabinClassEnum;
import util.exception.CustomerUsernameExistException;
import util.exception.FareNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.SeatInventoryNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
public class MainApp {
    
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    private FareSessionBeanRemote fareSessionBeanRemote;
    private CabinClassSessionBeanRemote cabinClassSessionBeanRemote;
    private SeatInventorySessionBeanRemote seatInventorySessionBeanRemote;
    
    private Customer currentCustomer;

    public MainApp()
    {
    }
    
    public MainApp (CustomerSessionBeanRemote customerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, FareSessionBeanRemote fareSessionBeanRemote, CabinClassSessionBeanRemote cabinClassSessionBeanRemote, SeatInventorySessionBeanRemote seatInventorySessionBeanRemote)
    {
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.fareSessionBeanRemote = fareSessionBeanRemote;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
        this.seatInventorySessionBeanRemote = seatInventorySessionBeanRemote;
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
        Integer outboundFlightType = 0;
        Integer returnFlightType = 0;
        CabinClassEnum outboundCabinClass = null;
        CabinClassEnum returnCabinClass = null;
        
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
            System.out.print("Enter your preference (1: Direct Flight, 2: Connecting Flight, 3: No Preference) for outbound flight> ");
            outboundFlightType = scanner.nextInt();
            scanner.nextLine();
            
            if (outboundFlightType < 1 || outboundFlightType > 3)
            {
                System.out.println("Invalid option, please try again!\n");
            }
            else
            {
                if (tripType == 1)
                {
                    break;
                }
            }
            
            System.out.print("Enter your preference (1: Direct Flight, 2: Connecting Flight, 3: No Preference) for return flight> ");
            returnFlightType = scanner.nextInt();
            scanner.nextLine();
            
            if (returnFlightType < 1 || returnFlightType > 3)
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
            System.out.print("Enter you preference for (F: First Class, J: Business Class, W: Premiumn Economy Class, Y: Economy Class, NA: No Preference) for outbound flight> ");
            String cabinClassPreference = scanner.nextLine().trim();
            
            if (!cabinClassPreference.equals("F") && !cabinClassPreference.equals("J") && !cabinClassPreference.equals("W") && !cabinClassPreference.equals("Y") && !cabinClassPreference.equals("NA"))
            {
                System.out.println("Invalid option, please try again!\n");
            }
            else
            {
                if (cabinClassPreference.equals("F"))
                {
                    outboundCabinClass = CabinClassEnum.FIRST_CLASS;
                }
                else if (cabinClassPreference.equals("J"))
                {
                    outboundCabinClass = CabinClassEnum.BUSINESS_CLASS;
                }
                else if (cabinClassPreference.equals("W"))
                {
                    outboundCabinClass = CabinClassEnum.PREMIUM_ECONOMY_CLASS;
                }
                else if (cabinClassPreference.equals("Y"))
                {
                    outboundCabinClass = CabinClassEnum.ECONOMY_CLASS;
                }
                
                if (tripType == 1)
                {
                    break;
                }
            }
            
            System.out.print("Enter you preference for (F: First Class, J: Business Class, W: Premiumn Economy Class, Y: Economy Class, NA: No Preference) for return flight> ");
            cabinClassPreference = scanner.nextLine().trim();
            
            if (!cabinClassPreference.equals("F") && !cabinClassPreference.equals("J") && !cabinClassPreference.equals("W") && !cabinClassPreference.equals("Y") && !cabinClassPreference.equals("NA"))
            {
                System.out.println("Invalid option, please try again!\n");
            }
            else
            {
                if (cabinClassPreference.equals("F"))
                {
                    returnCabinClass = CabinClassEnum.FIRST_CLASS;
                }
                else if (cabinClassPreference.equals("J"))
                {
                    returnCabinClass = CabinClassEnum.BUSINESS_CLASS;
                }
                else if (cabinClassPreference.equals("W"))
                {
                    returnCabinClass = CabinClassEnum.PREMIUM_ECONOMY_CLASS;
                }
                else if (cabinClassPreference.equals("Y"))
                {
                    returnCabinClass = CabinClassEnum.ECONOMY_CLASS;
                }
                break;
            }
        }
        
        System.out.println("\nOutbound Flight: \n");
        if (outboundFlightType == 1)
        {
            doSearchDirectFlights(departureAirportId, destinationAirportId, formattedDepartureDate, outboundCabinClass, numPassengers);
        }
        else if (outboundFlightType == 2)
        {
            doSearchConnectingFlights(departureAirportId, destinationAirportId, formattedDepartureDate, outboundCabinClass, numPassengers);
        }
        else if (outboundFlightType == 3)
        {
            doSearchAllFlights(departureAirportId, destinationAirportId, formattedDepartureDate, outboundCabinClass, numPassengers);
            
        }
        
        if (tripType == 2)
        {
            System.out.println("\nReturn Flight:\n");
            if (returnFlightType == 1)
            {
                doSearchDirectFlights(destinationAirportId, departureAirportId, formattedReturnDate, returnCabinClass, numPassengers);
            }
            else if (returnFlightType == 2)
            {
                doSearchConnectingFlights(destinationAirportId, departureAirportId, formattedReturnDate, returnCabinClass, numPassengers);
            }
            else if (returnFlightType == 3)
            {
                doSearchAllFlights(destinationAirportId, departureAirportId, formattedReturnDate, returnCabinClass, numPassengers);
            }
        }
    }
    
    public void doSearchAllFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        
        if (!flightSchedules.isEmpty() || !singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            if (!flightSchedules.isEmpty())
            {
                System.out.println("Direct Flights Available: ");
                for (FlightSchedule fs1: flightSchedules)
                {
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
                }
            }
            
            if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
            {
                System.out.println("Connecting Flights Available: ");
                if (!singleTransit.isEmpty())
                {
                    doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers);
                }
                if (!doubleTransit.isEmpty())
                {
                    doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers);
                }
            }
        }
        else
        {
            System.out.println("There are no flight schedules available on " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
        
        GregorianCalendar beforeDepartureDateCalendar = new GregorianCalendar();
        beforeDepartureDateCalendar.setTime(formattedDepartureDate);
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, -3);
        Date formattedBeforeDepartureDate = beforeDepartureDateCalendar.getTime();
        flightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedBeforeDepartureDate, formattedDepartureDate, preferredCabinClass, numPassengers);
        
        //Connecting Flight Schedules 3 days before chosen date
        beforeDepartureDateCalendar.setTime(formattedDepartureDate);
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, -3);
        Date dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        Date dateEnd = beforeDepartureDateCalendar.getTime();
        singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Connecting Flight Schedules 2 days before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Connecting Flight Schedules 1 day before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        if (!flightSchedules.isEmpty() || !singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            if (!flightSchedules.isEmpty())
            {
                System.out.println("Direct Flights Available: ");
                for (FlightSchedule fs1: flightSchedules)
                {
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
                }
            }
            
            if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
            {
                System.out.println("Connecting Flights Available: ");
                if (!singleTransit.isEmpty())
                {
                    doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers);
                }
                if (!doubleTransit.isEmpty())
                {
                    doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers);
                }
            }
        }
        else
        {
            System.out.println("There are no flight schedules available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
        
        GregorianCalendar afterDepartureDateCalendar = new GregorianCalendar();
        afterDepartureDateCalendar.setTime(formattedDepartureDateEnd);
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +3);
        Date formattedAfterDepartureDate = afterDepartureDateCalendar.getTime();
        flightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDateEnd, formattedAfterDepartureDate, preferredCabinClass, numPassengers);
        
        // Connecting Flight schedules 1 day after chosen date
        afterDepartureDateCalendar.setTime(formattedDepartureDate);
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Connecting Flight schedules 2 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Connecting Flight schedules 3 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        if (!flightSchedules.isEmpty() || !singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            if (!flightSchedules.isEmpty())
            {
                System.out.println("Direct Flights Available: ");
                for (FlightSchedule fs1: flightSchedules)
                {
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
                }
            }
            
            if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
            {
                System.out.println("Connecting Flights Available: ");
                if (!singleTransit.isEmpty())
                {
                    doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers);
                }
                if (!doubleTransit.isEmpty())
                {
                    doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers);
                }
            }
        }
        else
        {
            System.out.println("There are no flight schedules available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
    }
    
    public void doSearchDirectFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        System.out.println("Direct Flights Available: ");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        if (!flightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            for (FlightSchedule fs1: flightSchedules)
            {
                doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
            }
        }
        else
        {
            System.out.println("There are no direct flight schedules available on " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }       

        GregorianCalendar beforeDepartureDateCalendar = new GregorianCalendar();
        beforeDepartureDateCalendar.setTime(formattedDepartureDate);
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, -3);
        Date formattedBeforeDepartureDate = beforeDepartureDateCalendar.getTime();
        List<FlightSchedule> beforeDepartureFlightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedBeforeDepartureDate, formattedDepartureDate, preferredCabinClass, numPassengers);
        if (!beforeDepartureFlightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + ":\n");
            for (FlightSchedule fs2: beforeDepartureFlightSchedules)
            {
                doPrintDirectFlightSchedule(fs2, preferredCabinClass, numPassengers);
            }
        }
        else
        {
            System.out.println("There are no direct flight schedules available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + "!\n");
        } 

        GregorianCalendar afterDepartureDateCalendar = new GregorianCalendar();
        afterDepartureDateCalendar.setTime(formattedDepartureDateEnd);
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +3);
        Date formattedAfterDepartureDate = afterDepartureDateCalendar.getTime();
        List<FlightSchedule> afterDepartureFlightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDateEnd, formattedAfterDepartureDate, preferredCabinClass, numPassengers);
        if (!beforeDepartureFlightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + ":\n");
            for (FlightSchedule fs3: afterDepartureFlightSchedules)
            {
                doPrintDirectFlightSchedule(fs3, preferredCabinClass, numPassengers);
            }
        }
        else
        {
            System.out.println("There are no direct flight schedules available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
    }
    
    public void doSearchConnectingFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        System.out.println("\nConnecting Flights Available: ");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        List<FlightSchedule> singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers);
            if (!doubleTransit.isEmpty())
            {
                doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers);
            }
        }
        else
        {
            System.out.println("There are no connecting flight schedules available on " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
        
        // Flight Schedules 3 days before chosen date
        GregorianCalendar beforeDepartureDateCalendar = new GregorianCalendar();
        beforeDepartureDateCalendar.setTime(formattedDepartureDate);
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, -3);
        Date dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        Date dateEnd = beforeDepartureDateCalendar.getTime();
        singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Flight Schedules 2 days before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Flight Schedules 1 day before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Print out flight schedules 1-3 days before chosen date
        if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + ":\n");
            doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers);
            if (!doubleTransit.isEmpty())
            {
                doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers);
            }
        }
        else
        {
            System.out.println("There are no connecting flight schedules available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
        
        // Flight schedules 1 day after chosen date
        GregorianCalendar afterDepartureDateCalendar = new GregorianCalendar();
        afterDepartureDateCalendar.setTime(formattedDepartureDate);
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Flight schedules 2 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Flight schedules 3 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Print out flight schedules 1-3 days after chosen date
        if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + ":\n");
            doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers);
            if (!doubleTransit.isEmpty())
            {
                doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers);
            }
        }
        else
        {
            System.out.println("There are no connecting flight schedules available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + "!\n");
        } 
    }
    
    public void doPrintDirectFlightSchedule(FlightSchedule flightSchedule, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");      
        
        System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
        System.out.printf("%10s%18s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(flightSchedule.getDepartureDateTime()), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
        System.out.println("Price of cabin class(es):");
        List<CabinClass> cabinClasses = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
        System.out.printf("\t%20s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
        if (preferredCabinClass == null)
        {
            for (CabinClass cc: cabinClasses)
            {
                try
                {
                    SeatInventory seatInventory = seatInventorySessionBeanRemote.retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(cc.getCabinClassId(), flightSchedule.getFlightScheduleId());
                    if (seatInventory.getNumOfBalanceSeats() < numPassengers)
                    {
                        continue;
                    }

                    List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                    BigDecimal lowestFare = fares.get(0).getFareAmount();
                    for (Fare fare: fares)
                    {
                        if (fare.getFareAmount().compareTo(lowestFare) < 0)
                        {
                            lowestFare = fare.getFareAmount();
                        }
                    }

                    System.out.printf("\t%20s%25s%30s\n", cc.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                }
                catch (FareNotFoundException | SeatInventoryNotFoundException ex)
                {
                    continue;
                }
            }
        }
        else
        {
            for (CabinClass cc: cabinClasses)
            {
                if (cc.getCabinClassType().equals(preferredCabinClass))
                {
                    try
                    {
                        List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                        BigDecimal lowestFare = fares.get(0).getFareAmount();
                        for (Fare fare: fares)
                        {
                            if (fare.getFareAmount().compareTo(lowestFare) < 0)
                            {
                                lowestFare = fare.getFareAmount();
                            }
                        }

                        System.out.printf("\t%20s%25s%30s\n", cc.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                    }
                    catch (FareNotFoundException ex)
                    {
                        continue;
                    }
                } 
            }
        }
        System.out.println("");
    }
    
    public void doPrintSingleTransitFlightSchedule(List<FlightSchedule> flightSchedules, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        // SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
       
        while (!flightSchedules.isEmpty())
        {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            BigDecimal pricePerPassenger = new BigDecimal(0);
            FlightSchedule fs1 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesOne = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs2 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesTwo = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            
            if (preferredCabinClass != null)
            {
                System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
                System.out.printf("%10s%18s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs1.getDepartureDateTime()), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
                System.out.printf("%10s%18s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs2.getDepartureDateTime()), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
                for (CabinClass cc: cabinClassesOne)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {
                        try
                        {
                            List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                            BigDecimal lowestFare = fares.get(0).getFareAmount();
                            for (Fare fare: fares)
                            {
                                if (fare.getFareAmount().compareTo(lowestFare) < 0)
                                {
                                    lowestFare = fare.getFareAmount();
                                }
                            }
                            
                            pricePerPassenger = pricePerPassenger.add(lowestFare);
                        }
                        catch (FareNotFoundException ex)
                        {
                            continue;
                        }
                    } 
                }
                
                for (CabinClass cc: cabinClassesTwo)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {
                        try
                        {
                            List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                            BigDecimal lowestFare = fares.get(0).getFareAmount();
                            for (Fare fare: fares)
                            {
                                if (fare.getFareAmount().compareTo(lowestFare) < 0)
                                {
                                    lowestFare = fare.getFareAmount();
                                }
                            }
                            
                            pricePerPassenger = pricePerPassenger.add(lowestFare);
                        }
                        catch (FareNotFoundException ex)
                        {
                            continue;
                        }
                    } 
                }
                System.out.println("");
                // Print out price per passenger and price for all passengers for the connecting flight
                System.out.printf("\t%20s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
                System.out.printf("\t%20s%25s%30s\n", preferredCabinClass.toString(), pricePerPassenger.toString() , (pricePerPassenger.multiply(new BigDecimal(numPassengers))).toString());
                System.out.println("");
            }
            else
            {
                doPrintDirectFlightSchedule(fs1, preferredCabinClass, numPassengers);
                doPrintDirectFlightSchedule(fs2, preferredCabinClass, numPassengers);
                System.out.println("\n");
            }
            
            /*
            System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
            System.out.printf("%10s%18s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs1.getDepartureDateTime()), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
            System.out.printf("%10s%18s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs2.getDepartureDateTime()), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
            
            System.out.println("Price of cabin class(es) in " + fs1.getFlightSchedulePlan().getFlight().getFlightNumber() + ":");
            List<CabinClass> cabinClassesOne = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            List<CabinClass> cabinClassesTwo = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            System.out.printf("\t%20s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
            if (preferredCabinClass == null)
            {
                for (CabinClass cc1: cabinClassesOne)
                {
                    try
                    {
                        SeatInventory seatInventory = seatInventorySessionBeanRemote.retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(cc1.getCabinClassId(), fs1.getFlightScheduleId());
                        if (seatInventory.getNumOfBalanceSeats() < numPassengers)
                        {
                            continue;
                        }

                        List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cc1.getCabinClassId());
                        BigDecimal lowestFare = fares.get(0).getFareAmount();
                        for (Fare fare: fares)
                        {
                            if (fare.getFareAmount().compareTo(lowestFare) < 0)
                            {
                                lowestFare = fare.getFareAmount();
                            }
                        }

                        System.out.printf("\t%20s%25s%30s\n", cc1.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                    }
                    catch (FareNotFoundException | SeatInventoryNotFoundException ex)
                    {
                        continue;
                    }
                }
                
                for (CabinClass cc2: cabinClassesTwo)
                {
                    try
                    {
                        SeatInventory seatInventory = seatInventorySessionBeanRemote.retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(cc2.getCabinClassId(), fs2.getFlightScheduleId());
                        if (seatInventory.getNumOfBalanceSeats() < numPassengers)
                        {
                            continue;
                        }

                        List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cc2.getCabinClassId());
                        BigDecimal lowestFare = fares.get(0).getFareAmount();
                        for (Fare fare: fares)
                        {
                            if (fare.getFareAmount().compareTo(lowestFare) < 0)
                            {
                                lowestFare = fare.getFareAmount();
                            }
                        }

                        System.out.printf("\t%20s%25s%30s\n", cc2.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                    }
                    catch (FareNotFoundException | SeatInventoryNotFoundException ex)
                    {
                        continue;
                    }
                }
            }
            else
            {
                for (CabinClass cc: cabinClassesOne)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {
                        try
                        {
                            List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                            BigDecimal lowestFare = fares.get(0).getFareAmount();
                            for (Fare fare: fares)
                            {
                                if (fare.getFareAmount().compareTo(lowestFare) < 0)
                                {
                                    lowestFare = fare.getFareAmount();
                                }
                            }

                            System.out.printf("\t%20s%25s%30s\n", cc.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                        }
                        catch (FareNotFoundException ex)
                        {
                            continue;
                        }
                    }
                }
            }
            */
        }
    }
    
    public void doPrintDoubleTransitFlightSchedules(List<FlightSchedule> flightSchedules, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        while (!flightSchedules.isEmpty())
        {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            BigDecimal pricePerPassenger = new BigDecimal(0);
            FlightSchedule fs1 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesOne = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs2 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesTwo = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs2.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs3 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesThree = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs3.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            
            if (preferredCabinClass != null)
            {
                System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
                System.out.printf("%10s%18s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs1.getDepartureDateTime()), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
                System.out.printf("%10s%18s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs2.getDepartureDateTime()), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
                System.out.printf("%10s%18s%30s%20s\n", fs3.getFlightSchedulePlan().getFlight().getFlightNumber(), fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs3.getDepartureDateTime()), fs3.getFlightHours().toString() + "h " + fs3.getFlightMinutes().toString() + "min");
                for (CabinClass cc: cabinClassesOne)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {
                        try
                        {
                            List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                            BigDecimal lowestFare = fares.get(0).getFareAmount();
                            for (Fare fare: fares)
                            {
                                if (fare.getFareAmount().compareTo(lowestFare) < 0)
                                {
                                    lowestFare = fare.getFareAmount();
                                }
                            }
                            
                            pricePerPassenger = pricePerPassenger.add(lowestFare);
                        }
                        catch (FareNotFoundException ex)
                        {
                            continue;
                        }
                    } 
                }
                
                for (CabinClass cc: cabinClassesTwo)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {
                        try
                        {
                            List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                            BigDecimal lowestFare = fares.get(0).getFareAmount();
                            for (Fare fare: fares)
                            {
                                if (fare.getFareAmount().compareTo(lowestFare) < 0)
                                {
                                    lowestFare = fare.getFareAmount();
                                }
                            }
                            
                            pricePerPassenger = pricePerPassenger.add(lowestFare);
                        }
                        catch (FareNotFoundException ex)
                        {
                            continue;
                        }
                    } 
                }
                
                for (CabinClass cc: cabinClassesThree)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {
                        try
                        {
                            List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(fs3.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                            BigDecimal lowestFare = fares.get(0).getFareAmount();
                            for (Fare fare: fares)
                            {
                                if (fare.getFareAmount().compareTo(lowestFare) < 0)
                                {
                                    lowestFare = fare.getFareAmount();
                                }
                            }
                            
                            pricePerPassenger = pricePerPassenger.add(lowestFare);
                        }
                        catch (FareNotFoundException ex)
                        {
                            continue;
                        }
                    } 
                }
                System.out.println("");
                // Print out price per passenger and price for all passengers for the connecting flight
                System.out.printf("\t%20s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
                System.out.printf("\t%20s%25s%30s\n", preferredCabinClass.toString(), pricePerPassenger.toString() , (pricePerPassenger.multiply(new BigDecimal(numPassengers))).toString());
                System.out.println("");
            }
            else
            {
                doPrintDirectFlightSchedule(fs1, preferredCabinClass, numPassengers);
                doPrintDirectFlightSchedule(fs2, preferredCabinClass, numPassengers);
                doPrintDirectFlightSchedule(fs3, preferredCabinClass, numPassengers);
                System.out.println("\n");
            }
          
        /*
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Boolean isFirstConnectingFlight = true;
        Boolean isSecondConnectingFlight = true;
        for (FlightSchedule fs: flightSchedules)
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
            */  
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