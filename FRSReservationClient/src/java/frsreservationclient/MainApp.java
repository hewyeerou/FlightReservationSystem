/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsreservationclient;

import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.CabinClassSessionBeanRemote;
import ejb.session.stateless.CabinSeatInventorySessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.FareSessionBeanRemote;
import ejb.session.stateless.FlightReservationRecordSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.PassengerSessionBeanRemote;
import ejb.session.stateless.SeatInventorySessionBeanRemote;
import entity.Airport;
import entity.CabinClass;
import entity.CabinSeatInventory;
import entity.Customer;
import entity.Fare;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.Passenger;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import util.enumeration.CabinClassEnum;
import util.exception.CabinClassNotFoundException;
import util.exception.CabinSeatInventoryExistException;
import util.exception.CustomerUsernameExistException;
import util.exception.FareNotFoundException;
import util.exception.FlightReservationRecordNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PassengerNotFoundException;
import util.exception.SeatInventoryNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
public class MainApp {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private CustomerSessionBeanRemote customerSessionBeanRemote;
    private AirportSessionBeanRemote airportSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    private FareSessionBeanRemote fareSessionBeanRemote;
    private CabinClassSessionBeanRemote cabinClassSessionBeanRemote;
    private SeatInventorySessionBeanRemote seatInventorySessionBeanRemote;
    private CabinSeatInventorySessionBeanRemote cabinSeatInventorySessionBeanRemote;
    private FlightReservationRecordSessionBeanRemote flightReservationRecordSessionBeanRemote;
    private PassengerSessionBeanRemote passengerSessionBeanRemote;
    
    private Customer currentCustomer;
    private BigDecimal totalPrice;
    private List<Long> reserveFlightSchedules;
    private HashMap<FlightSchedule, Long> mapping;
    
    private List<FlightSchedule> outboundFlightSchedules;
    private List<FlightSchedule> outboundSingleTransit;
    private List<FlightSchedule> outboundDoubleTransit;
    
    private List<FlightSchedule> returnFlightSchedules;
    private List<FlightSchedule> returnSingleTransit;
    private List<FlightSchedule> returnDoubleTransit;
    
    public MainApp()
    {
        this.outboundFlightSchedules = new ArrayList<>();
        this.outboundSingleTransit = new ArrayList<>();
        this.outboundDoubleTransit = new ArrayList<>();
        this.returnFlightSchedules = new ArrayList<>();
        this.returnSingleTransit = new ArrayList<>();
        this.returnDoubleTransit = new ArrayList<>();
        this.reserveFlightSchedules = new ArrayList<>();
        this.mapping = new HashMap<>();
        this.totalPrice = new BigDecimal(0);
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public MainApp (CustomerSessionBeanRemote customerSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, FareSessionBeanRemote fareSessionBeanRemote, CabinClassSessionBeanRemote cabinClassSessionBeanRemote, SeatInventorySessionBeanRemote seatInventorySessionBeanRemote, CabinSeatInventorySessionBeanRemote cabinSeatInventorySessionBeanRemote, FlightReservationRecordSessionBeanRemote flightReservationRecordSessionBeanRemote, PassengerSessionBeanRemote passengerSessionBeanRemote)
    {
        this();
        
        this.customerSessionBeanRemote = customerSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.fareSessionBeanRemote = fareSessionBeanRemote;
        this.cabinClassSessionBeanRemote = cabinClassSessionBeanRemote;
        this.seatInventorySessionBeanRemote = seatInventorySessionBeanRemote;
        this.cabinSeatInventorySessionBeanRemote = cabinSeatInventorySessionBeanRemote;
        this.flightReservationRecordSessionBeanRemote = flightReservationRecordSessionBeanRemote;
        this.passengerSessionBeanRemote = passengerSessionBeanRemote;
    }
    
    public void runApp()
    {
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while (true)
        {
            {
                response = 0;
    
                System.out.println("*** Welcome to Flight Reservation System (FRS) Reservation ***\n");
                System.out.println("1: Register Account");
                System.out.println("2: Login");
                System.out.println("3: Search for flights");
                System.out.println("4: Exit\n");

                while (response < 1 || response > 4)
                {
                    try
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
                            if (currentCustomer != null)
                            {
                                customerMenu();
                            }
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
                    catch (InputMismatchException ex)
                    {
                        System.out.println("Invalid input, select an option from 1-4!\n");
                        scanner.next();
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
            System.out.println("\n*** Flight Reservation System (FRS) Reservation ***\n");
            System.out.println("You are login as " + currentCustomer.getFirstName() + " " + currentCustomer.getLastName() + "\n");
            System.out.println("1: Search Flights");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: View Flight Reservation Details");
            System.out.println("4: Logout\n");
            
            response = 0;

            while (response < 1 || response > 4)
            {
                try
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
                catch (InputMismatchException ex)
                {
                    System.out.println("Invalid input, select an option from 1-4!\n");
                    scanner.next();
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
        
        System.out.println("\n*** FRS Reservation :: Register Account ***\n");
        
        while (firstName.length() <= 0)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter first name in text!\n");
                scanner.next();
            }
        }
        
        while (lastName.length() <= 0)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter last name in text!\n");
                scanner.next();
            }
        }
        
        while (email.length() <= 0)
        {
            try
            {
                System.out.print("Enter Email> ");
                email = scanner.nextLine().trim();

                if (email.length() > 0 && email.contains("@"))
                {
                    newCustomer.setEmail(email);
                }
                else
                {
                    System.out.println("Email cannot be empty!");
                }
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter email in text!\n");
                scanner.next();
            }
        }
        
        while (String.valueOf(mobileNumber).length() != 8)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter mobile number in digits!\n");
                scanner.next();
            }
        }
        
        while (address.length() <= 0)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter address in text!\n");
                scanner.next();
            }
        }
        
        while (username.length() <= 0)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter username in text!\n");
                scanner.next();
            }
        }
        
        while (password.length() <= 0)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter password in text!\n");
                scanner.next();
            }
        }
        
        Set<ConstraintViolation<Customer>>constraintViolations = validator.validate(newCustomer);
        
        if(constraintViolations.isEmpty())
        {
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
            catch (InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForCustomer(constraintViolations);
        }
    }
    
    public void doLogin() throws InvalidLoginCredentialException
    {
        Scanner scanner = new Scanner (System.in);
        String username = "";
        String password = "";
        
        try
        {
            System.out.println("\n*** FRS Reservation :: Customer Login ***\n");
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
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input, enter username and password in text!\n");
            scanner.next();
        }
    }
    
    public void doSearchFlight()
    {
        this.outboundDoubleTransit = new ArrayList<>();
        this.outboundFlightSchedules = new ArrayList<>();
        this.outboundSingleTransit = new ArrayList<>();
        this.returnDoubleTransit = new ArrayList<>();
        this.returnFlightSchedules = new ArrayList<>();
        this.returnSingleTransit = new ArrayList<>();
        
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
        Boolean reserveFlight = false;
        Boolean canReserveOutbound = false;
        Boolean canReserveReturn = false;
        List<Integer> outboundOptions = new ArrayList<>();
        List<Integer> returnOptions = new ArrayList<>();
        
        System.out.println("\n*** FRS Reservation :: Search Flights ***\n");

        while (true)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-2!\n");
                scanner.next();
            }
        }
        
        List<Airport> airports = airportSessionBeanRemote.getAllAirports();
        
        while(true)
        {
            Integer option = 0;
            
            try
            {
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-" + option + " !\n");
                scanner.next();
            }
        }
        
        while(true)
        {
            Integer option = 0;
            
            try
            {
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-" + option + " !\n");
                scanner.next();
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
                
                if (formattedReturnDate.compareTo(formattedDepartureDate) < 0)
                {
                    System.out.println("Return date has to be after departure date, please try again!\n");
                    continue;
                }
                
                break;
            }
            catch(ParseException ex) 
            {
                System.out.println("Date is in the wrong format, please try again!\n");
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter date in the required format!\n");
                scanner.next();
            }
        }
        
        while (true)
        {
            try
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter number of passengers in digits!\n");
                scanner.next();
            }
        }
        
        while (true)
        {
            try
            {
                System.out.print("Enter your preference (1: Direct Flight, 2: Connecting Flight, 3: No Preference) for outbound flight> ");
                outboundFlightType = 0;
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
                returnFlightType = 0;
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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-3!\n");
                scanner.next();
            }
        }
        
        while (true)
        {
            try
            {
                System.out.print("\nEnter you preference for (F: First Class, J: Business Class, W: Premiumn Economy Class, Y: Economy Class, NA: No Preference) for outbound flight> ");
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
            }   
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter 'F', 'J', 'W', 'Y' or 'NA' !\n");
                scanner.next();
            }

            try
            {
                System.out.print("\nEnter you preference for (F: First Class, J: Business Class, W: Premiumn Economy Class, Y: Economy Class, NA: No Preference) for return flight> ");
                String cabinClassPreference = scanner.nextLine().trim();

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
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter 'F', 'J', 'W', 'Y' or 'NA' !\n");
                scanner.next();
            }
        }
        
        System.out.println("\nOutbound Flight: \n");
        if (outboundFlightType == 1)
        {
            outboundOptions = doSearchDirectFlights(departureAirportId, destinationAirportId, formattedDepartureDate, outboundCabinClass, numPassengers, false);
            if (outboundOptions.get(0) != 0)
            {
                canReserveOutbound = true;
            }
        }
        else if (outboundFlightType == 2)
        {
            outboundOptions = doSearchConnectingFlights(departureAirportId, destinationAirportId, formattedDepartureDate, outboundCabinClass, numPassengers, false);
            if (outboundOptions.get(5) != 0)
            {
                canReserveOutbound = true;
            }
        }
        else if (outboundFlightType == 3)
        {
            outboundOptions = doSearchAllFlights(departureAirportId, destinationAirportId, formattedDepartureDate, outboundCabinClass, numPassengers, false);
            if (outboundOptions.get(8) != 0)
            {
                canReserveOutbound = true;
            }
        }
        
        if (tripType == 2)
        {
            System.out.println("\nReturn Flight:\n");
            if (returnFlightType == 1)
            {
                returnOptions = doSearchDirectFlights(destinationAirportId, departureAirportId, formattedReturnDate, returnCabinClass, numPassengers, true);
                if (returnOptions.get(0) != 0)
                {
                    canReserveReturn = true;
                }
            }
            else if (returnFlightType == 2)
            {
                returnOptions = doSearchConnectingFlights(destinationAirportId, departureAirportId, formattedReturnDate, returnCabinClass, numPassengers, true);
                if (returnOptions.get(5) != 0)
                {
                    canReserveReturn = true;
                }
            }
            else if (returnFlightType == 3)
            {
                returnOptions = doSearchAllFlights(destinationAirportId, departureAirportId, formattedReturnDate, returnCabinClass, numPassengers, true);
                if (returnOptions.get(8) != 0)
                {
                    canReserveReturn = true;
                }
            }
        }
        
        while (true)
        {
            try
            {
                String response = "";
                // If there are no search results then customer will not be able to reserve any flights
                if (tripType == 1 && canReserveOutbound || tripType == 2 && canReserveOutbound && canReserveReturn)
                {
                    System.out.print("Would you like to reserve a flight(Y/N)? > ");
                    response = scanner.nextLine().trim();

                    if (response.equals("Y"))
                    {
                        reserveFlight = true;
                        break;
                    }
                    else if (response.equals("N"))
                    {
                        reserveFlight = false;
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid input, please try again!\n");
                    }
                }
                else
                {
                    while (true)
                    {
                        System.out.print("Would you like to perform another search(Y/N)? > ");
                        response = scanner.nextLine().trim();

                        if (response.equals("Y"))
                        {
                            doSearchFlight();
                        }
                        else if (response.equals("N"))
                        {
                            break;
                        }
                        else
                        {
                            System.out.println("Invalid input, please try again!\n");
                        }
                    }
                }
                if (response.equals("N"))
                {
                    break;
                }
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter 'Y' or 'N'!\n");
                scanner.next();
            }
        }
        
        if (reserveFlight)
        {
            if (this.currentCustomer == null)
            {
                Integer result = 0;
                System.out.println("To reserve flight tickets, please register or login first!\n");
                
                while (true)
                {
                    try
                    {
                        System.out.println("1: Register Account");
                        System.out.println("2: Login\n");
                        System.out.print("> ");

                        result = 0;
                        result = scanner.nextInt();
                        scanner.nextLine();

                        if (result != 1 && result !=2)
                        {
                            System.out.println("Invalid option, please try again!\n");
                        }
                        else
                        {
                            break;
                        }
                    }
                    catch (InputMismatchException ex)
                    {
                        System.out.println("Invalid input, select an option 1 or 2!\n");
                        scanner.next();
                    }
                }
                
                if (result == 1)
                {
                    doRegisterCustomer();
                    doReserveFlight(tripType, numPassengers, outboundFlightType, returnFlightType, outboundCabinClass, returnCabinClass, outboundOptions, returnOptions);
                }
                else if (result == 2)
                {
                    try
                    {
                        doLogin();
                        doReserveFlight(tripType, numPassengers, outboundFlightType, returnFlightType, outboundCabinClass, returnCabinClass, outboundOptions, returnOptions);
                    }
                    catch (InvalidLoginCredentialException ex)
                    {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                }
            }
            else
            {
                doReserveFlight(tripType, numPassengers, outboundFlightType, returnFlightType, outboundCabinClass, returnCabinClass, outboundOptions, returnOptions);
            }
        }
    }
    
    public List<Integer> doSearchAllFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers, Boolean isReturn)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        List<Integer> options = new ArrayList<>();
        Integer directOptions = 0;
        Integer singleOptions = 0;
        Integer doubleOptions = 0;
        Integer directBeforeOptions = 0;
        Integer singleBeforeOptions = 0;
        Integer doubleBeforeOptions = 0;
        Integer directAfterOptions = 0;
        Integer singleAfterOptions = 0;
        Integer doubleAfterOptions = 0;
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
                System.out.println("Direct Flights Available: \n");
                for (FlightSchedule fs1: flightSchedules)
                {
                    directOptions++;
                    System.out.println(directOptions + ":");
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
                    
                    if (!isReturn)
                    {
                        this.outboundFlightSchedules.add(fs1);
                    }
                    else
                    {
                        this.returnFlightSchedules.add(fs1);
                    }
                }
            }
            
            singleOptions = directOptions;
            doubleOptions = directOptions;
            
            if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
            {
                System.out.println("Connecting Flights Available: \n");
                if (!singleTransit.isEmpty())
                {
                    singleOptions = doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers, singleOptions, isReturn);
                }

                doubleOptions = singleOptions;
                
                if (!doubleTransit.isEmpty())
                {
                    doubleOptions = doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers, doubleOptions, isReturn);
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
        
        directBeforeOptions = doubleOptions;
        singleBeforeOptions = doubleOptions;
        doubleBeforeOptions = doubleOptions;
        
        if (!flightSchedules.isEmpty() || !singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + ":\n");
            if (!flightSchedules.isEmpty())
            {
                System.out.println("Direct Flights Available: \n");
                for (FlightSchedule fs1: flightSchedules)
                {
                    directBeforeOptions++;
                    System.out.println(directBeforeOptions + ":");
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass, numPassengers);
                    
                    if (!isReturn)
                    {
                        this.outboundFlightSchedules.add(fs1);
                    }
                    else
                    {
                        this.returnFlightSchedules.add(fs1);
                    }
                }
            }
            
            singleBeforeOptions = directBeforeOptions;
            doubleBeforeOptions = directBeforeOptions;
            
            if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
            {
                System.out.println("Connecting Flights Available: \n");
                if (!singleTransit.isEmpty())
                {
                    singleBeforeOptions = doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers, singleBeforeOptions, isReturn);
                }
                
                doubleBeforeOptions = singleBeforeOptions;
                
                if (!doubleTransit.isEmpty())
                {
                    doubleBeforeOptions = doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers, doubleBeforeOptions, isReturn);
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
        
        directAfterOptions = doubleBeforeOptions;
        singleAfterOptions = doubleBeforeOptions;
        doubleAfterOptions = doubleBeforeOptions;
        
        if (!flightSchedules.isEmpty() || !singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + ":\n");
            if (!flightSchedules.isEmpty())
            {
                System.out.println("Direct Flights Available: \n");
                for (FlightSchedule fs1: flightSchedules)
                {
                    directAfterOptions++;
                    System.out.println(directAfterOptions + ":");
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
                    
                    if (!isReturn)
                    {
                        this.outboundFlightSchedules.add(fs1);
                    }
                    else
                    {
                        this.returnFlightSchedules.add(fs1);
                    }
                }
            }
            
            singleAfterOptions = directAfterOptions;
            doubleAfterOptions = directAfterOptions;
            
            if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
            {
                System.out.println("Connecting Flights Available: \n");
                if (!singleTransit.isEmpty())
                {
                    singleAfterOptions = doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers, singleAfterOptions, isReturn);
                }
                
                doubleAfterOptions = singleAfterOptions;
                
                if (!doubleTransit.isEmpty())
                {
                    doubleAfterOptions = doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers, doubleAfterOptions, isReturn);
                }
            }
        }
        else
        {
            System.out.println("There are no flight schedules available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
        
        options.add(directOptions);
        options.add(singleOptions);
        options.add(doubleOptions);
        options.add(directBeforeOptions);
        options.add(singleBeforeOptions);
        options.add(doubleBeforeOptions);
        options.add(directAfterOptions);
        options.add(singleAfterOptions);
        options.add(doubleAfterOptions);
        return options;
    }
    
    public List<Integer> doSearchDirectFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers, Boolean isReturn)
    {
        Integer option = 0;
        System.out.println("Direct Flights Available: \n");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanRemote.searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        if (!isReturn)
        {
            this.outboundFlightSchedules = flightSchedules;
        }
        else
        {
            this.returnFlightSchedules = flightSchedules;
        }
        
        if (!flightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            for (FlightSchedule fs1: flightSchedules)
            {
                option++;
                System.out.println(option + ":");
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
                option++;
                System.out.println(option + ":");
                doPrintDirectFlightSchedule(fs2, preferredCabinClass, numPassengers);
                
                if (!isReturn)
                {
                    this.outboundFlightSchedules.add(fs2);
                }
                else
                {
                    this.returnFlightSchedules.add(fs2);
                }
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
        if (!afterDepartureFlightSchedules.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + ":\n");
            for (FlightSchedule fs3: afterDepartureFlightSchedules)
            {
                option++;
                System.out.println(option + ":");
                doPrintDirectFlightSchedule(fs3, preferredCabinClass, numPassengers);
                
                if (!isReturn)
                {
                    this.outboundFlightSchedules.add(fs3);
                }
                else
                {
                    this.returnFlightSchedules.add(fs3);
                }
            }
        }
        else
        {
            System.out.println("There are no direct flight schedules available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + "!\n");
        }
        
        List<Integer> options = new ArrayList<>();
        options.add(option);
        
        return options;
    }
    
    public List<Integer> doSearchConnectingFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers, Boolean isReturn)
    {
        List<Integer> options = new ArrayList<>();
        Integer singleOptions = 0;
        Integer doubleOptions = 0;
        Integer singleBeforeOptions = 0;
        Integer doubleBeforeOptions = 0;
        Integer singleAfterOptions = 0;
        Integer doubleAfterOptions = 0;
        System.out.println("Connecting Flights Available: \n");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        List<FlightSchedule> singleTransit = flightScheduleSessionBeanRemote.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> doubleTransit = flightScheduleSessionBeanRemote.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        
        if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available on " + dateFormatter.format(formattedDepartureDate) + ":\n");
            singleOptions = doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers, singleOptions, isReturn);
            
            doubleOptions = singleOptions;
            
            if (!doubleTransit.isEmpty())
            {
                doubleOptions = doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers, doubleOptions, isReturn);
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
        
        singleBeforeOptions = doubleOptions;
        doubleBeforeOptions = doubleOptions;
        
        // Print out connecting flight schedules 1-3 days before chosen date
        if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days before " + dateFormatter.format(formattedDepartureDate) + ":\n");
            singleBeforeOptions = doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers, singleBeforeOptions, isReturn);
            
            doubleBeforeOptions = doubleOptions;
            
            if (!doubleTransit.isEmpty())
            {
                doubleBeforeOptions = doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers, doubleBeforeOptions, isReturn);
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
        singleAfterOptions = doubleBeforeOptions;
        doubleAfterOptions = doubleBeforeOptions;
        
        if (!singleTransit.isEmpty() || !doubleTransit.isEmpty())
        {
            System.out.println("Flight Schedules Available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + ":\n");
            singleAfterOptions = doPrintSingleTransitFlightSchedule(singleTransit, preferredCabinClass, numPassengers, singleAfterOptions, isReturn);
            
            doubleAfterOptions = doubleBeforeOptions;
            
            if (!doubleTransit.isEmpty())
            {
                doubleAfterOptions = doPrintDoubleTransitFlightSchedules(doubleTransit, preferredCabinClass, numPassengers, doubleAfterOptions, isReturn);
            }
        }
        else
        {
            System.out.println("There are no connecting flight schedules available 1-3 days after " + dateFormatter.format(formattedDepartureDate) + "!\n");
        } 
        
        options.add(singleOptions);
        options.add(doubleOptions);
        options.add(singleBeforeOptions);
        options.add(doubleBeforeOptions);
        options.add(singleAfterOptions);
        options.add(doubleAfterOptions);
        
        return options;
    }
    
    public void doPrintDirectFlightSchedule(FlightSchedule flightSchedule, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");  
        List<CabinClass> printed = new ArrayList<>();
        
        // find out arrival time
        Integer flightHours = flightSchedule.getFlightHours();
        Integer flightMins = flightSchedule.getFlightMinutes();
        Integer timeZoneDiff = flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(flightSchedule.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        Date arrivalDateTime = calendar.getTime();
        
        System.out.printf("%10s%18s%30s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Arrival Date and Time", "Flight Duration");
        System.out.printf("%10s%18s%30s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(flightSchedule.getDepartureDateTime()), dateTimeFormatter.format(arrivalDateTime), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
        List<CabinClass> cabinClasses = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
        
        if (preferredCabinClass == null)
        {
            System.out.println("Price of cabin class(es):\n");
            System.out.printf("%40s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
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

                    System.out.printf("%40s%25s%30s\n", cc.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                    printed.add(cc);
                }
                catch (SeatInventoryNotFoundException ex)
                {
                    continue;
                }
            }
            
            if (printed.isEmpty())
            {
                System.out.println("\tThere are insufficient seats in the cabin classes for this reservation!\n");
            }
        }
        else
        {
            System.out.printf("%40s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
            for (CabinClass cc: cabinClasses)
            {
                if (cc.getCabinClassType().equals(preferredCabinClass))
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

                    System.out.printf("\t%30s%25s%30s\n", cc.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                } 
            }
        }
        System.out.println("");
    }
    
    public Integer doPrintSingleTransitFlightSchedule(List<FlightSchedule> flightSchedules, CabinClassEnum preferredCabinClass, Integer numPassengers, Integer options, Boolean isReturn)
    {
        while (!flightSchedules.isEmpty())
        {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            BigDecimal pricePerPassenger = new BigDecimal(0);
            FlightSchedule fs1 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesOne = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs2 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesTwo = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            
            if (!isReturn)
            {
                this.outboundSingleTransit.add(fs1);
                this.outboundSingleTransit.add(fs2);
            }
            else
            {
                this.returnSingleTransit.add(fs1);
                this.returnSingleTransit.add(fs2);
            }

            options++;
            System.out.println(options + ":");
            
            if (preferredCabinClass != null)
            {
                System.out.printf("%10s%18s%30s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Arrival Date and Time", "Flight Duration");
                
                Integer flightHours = fs1.getFlightHours();
                Integer flightMins = fs1.getFlightMinutes();
                Integer timeZoneDiff = fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(fs1.getDepartureDateTime());
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
                calendar.add(GregorianCalendar.MINUTE, flightMins);
                calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
                Date arrivalDateTime = calendar.getTime();
                
                System.out.printf("%10s%18s%30s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs1.getDepartureDateTime()), dateTimeFormatter.format(arrivalDateTime), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
                
                flightHours = fs2.getFlightHours();
                flightMins = fs2.getFlightMinutes();
                timeZoneDiff = fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
                calendar.setTime(fs2.getDepartureDateTime());
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
                calendar.add(GregorianCalendar.MINUTE, flightMins);
                calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
                arrivalDateTime = calendar.getTime();
                
                System.out.printf("%10s%18s%30s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs2.getDepartureDateTime()), dateTimeFormatter.format(arrivalDateTime), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
                for (CabinClass cc: cabinClassesOne)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
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
                }
                
                for (CabinClass cc: cabinClassesTwo)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
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
                }
                System.out.println("");
                // Print out price per passenger and price for all passengers for the connecting flight
                System.out.printf("%40s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
                System.out.printf("%40s%25s%30s\n", preferredCabinClass.toString(), pricePerPassenger.toString() , (pricePerPassenger.multiply(new BigDecimal(numPassengers))).toString());
                System.out.println("");
            }
            else
            {
                doPrintDirectFlightSchedule(fs1, preferredCabinClass, numPassengers);
                doPrintDirectFlightSchedule(fs2, preferredCabinClass, numPassengers);
                System.out.println("\n");
            }
        }
        return options;
    }
    
    public Integer doPrintDoubleTransitFlightSchedules(List<FlightSchedule> flightSchedules, CabinClassEnum preferredCabinClass, Integer numPassengers, Integer options, Boolean isReturn)
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
            
            if (!isReturn)
            {
                this.outboundDoubleTransit.add(fs1);
                this.outboundDoubleTransit.add(fs2);
                this.outboundDoubleTransit.add(fs3);
            }
            else
            {
                this.returnDoubleTransit.add(fs1);
                this.returnDoubleTransit.add(fs2);
                this.returnDoubleTransit.add(fs3);
            }
            
            options++;
            System.out.println(options + ":");
            
            if (preferredCabinClass != null)
            {
                System.out.printf("%10s%18s%30s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Arrival Date and Time", "Flight Duration");
                
                Integer flightHours = fs1.getFlightHours();
                Integer flightMins = fs1.getFlightMinutes();
                Integer timeZoneDiff = fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(fs1.getDepartureDateTime());
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
                calendar.add(GregorianCalendar.MINUTE, flightMins);
                calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
                Date arrivalDateTime = calendar.getTime();
                
                System.out.printf("%10s%18s%30s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs1.getDepartureDateTime()), dateTimeFormatter.format(arrivalDateTime), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
                
                flightHours = fs2.getFlightHours();
                flightMins = fs2.getFlightMinutes();
                timeZoneDiff = fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
                calendar.setTime(fs2.getDepartureDateTime());
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
                calendar.add(GregorianCalendar.MINUTE, flightMins);
                calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
                arrivalDateTime = calendar.getTime();
                
                System.out.printf("%10s%18s%30s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs2.getDepartureDateTime()), dateTimeFormatter.format(arrivalDateTime), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
                
                flightHours = fs3.getFlightHours();
                flightMins = fs3.getFlightMinutes();
                timeZoneDiff = fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
                calendar.setTime(fs3.getDepartureDateTime());
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
                calendar.add(GregorianCalendar.MINUTE, flightMins);
                calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
                arrivalDateTime = calendar.getTime();
                
                System.out.printf("%10s%18s%30s%30s%20s\n", fs3.getFlightSchedulePlan().getFlight().getFlightNumber(), fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs3.getDepartureDateTime()), dateTimeFormatter.format(arrivalDateTime), fs3.getFlightHours().toString() + "h " + fs3.getFlightMinutes().toString() + "min");
                
                for (CabinClass cc: cabinClassesOne)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
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
                }
                
                for (CabinClass cc: cabinClassesTwo)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
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
                }
                
                for (CabinClass cc: cabinClassesThree)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
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
                }
                System.out.println("");
                // Print out price per passenger and price for all passengers for the connecting flight
                System.out.printf("%40s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
                System.out.printf("%40s%25s%30s\n", preferredCabinClass.toString(), pricePerPassenger.toString() , (pricePerPassenger.multiply(new BigDecimal(numPassengers))).toString());
                System.out.println("");
            }
            else
            {
                doPrintDirectFlightSchedule(fs1, preferredCabinClass, numPassengers);
                doPrintDirectFlightSchedule(fs2, preferredCabinClass, numPassengers);
                doPrintDirectFlightSchedule(fs3, preferredCabinClass, numPassengers);
                System.out.println("\n");
            }
        }
        return options;
    }
        
    public void doReserveFlight(Integer tripType, Integer numPassengers, Integer outboundFlightType, Integer returnFlightType, CabinClassEnum outboundCabinClass, CabinClassEnum returnCabinClass, List<Integer> outboundOptions, List<Integer> returnOptions)
    {
        Scanner scanner = new Scanner (System.in);
        Integer options = 0;
        Integer outboundChoice = 0;
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        
        System.out.println("\n*** FRS Reservation :: Search Flights :: Reserve Flight ***\n");
        
        while (true)
        {
            if (outboundFlightType == 1)
            {
                options = outboundOptions.get(0);
            }
            else if (outboundFlightType == 2)
            {
                options = outboundOptions.get(5);
            }
            else
            {
                options = outboundOptions.get(8);
            }

            if (options == 1)
            {
                System.out.print("Enter the flight schedule (1) you would like to reserve for outbound flight> ");
            }
            else
            {
                System.out.print("Enter the flight schedule (1-" + options + ") you would like to reserve for outbound flight> ");
            }
            
            try
            {
                outboundChoice = 0;
                outboundChoice = scanner.nextInt();
                scanner.nextLine();

                if (outboundChoice < 1 || outboundChoice > options)
                {
                    System.out.println("Invalid option, please try again!\n");
                }
                else
                {
                    break;
                }
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-" + options + "!\n");
                scanner.next();
            }
        }

        if (outboundFlightType == 1)
        {
            FlightSchedule flightSchedule = this.outboundFlightSchedules.get(outboundChoice - 1);
            doReserveDirectFlight(flightSchedule, outboundCabinClass, numPassengers);
            
            if (tripType == 2)
            {
                doReserveReturnFlight(returnFlightType, numPassengers, returnCabinClass, returnOptions);
            }
        }
        else if (outboundFlightType == 2)
        {
            for (Integer index = 0; index < 6; index++)
            {
                if (index == 0)
                {
                    if (outboundChoice <= outboundOptions.get(index))
                    {
                        FlightSchedule fs1 = this.outboundSingleTransit.get(outboundChoice * 2 - 2);
                        FlightSchedule fs2 = this.outboundSingleTransit.get(outboundChoice * 2 - 1);
                        flightSchedules.add(fs1);
                        flightSchedules.add(fs2);
                        doReserveSingleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
                        break;
                    }
                }
                else
                {
                    if (outboundChoice > outboundOptions.get(index-1) && outboundChoice <= outboundOptions.get(index))
                    {
                        if (index % 2 == 0)
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + (outboundChoice - outboundOptions.get(index-1) - 1) * 2;
                            while (!(index - 2 < 0))
                            {
                                index = index - 2;
                                if (index != 0)
                                {
                                    numOfFlightsBefore = numOfFlightsBefore +(outboundOptions.get(index) - outboundOptions.get(index - 1)) * 2;
                                }
                                else
                                {
                                    numOfFlightsBefore = numOfFlightsBefore + outboundOptions.get(index) * 2;
                                }
                            }

                            FlightSchedule fs1 = this.outboundSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.outboundSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
                            break;
                        }
                        else
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + (outboundChoice - outboundOptions.get(index-1) - 1) * 3;
                            while (!(index - 2 < 0))
                            {
                                index = index - 2;
                                numOfFlightsBefore = numOfFlightsBefore +(outboundOptions.get(index) - outboundOptions.get(index - 1)) * 3;
                            }

                            FlightSchedule fs1 = this.outboundDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.outboundDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = this.outboundDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
                            break;
                        }                           
                    }
                }   
            }
            
            if (tripType == 2)
            {
                doReserveReturnFlight(returnFlightType, numPassengers, returnCabinClass, returnOptions);
            }
        }
        else if (outboundFlightType == 3)
        {
            for (Integer index = 0; index < 9; index++)
            {
                if (index == 0)
                {
                    if (outboundChoice <= outboundOptions.get(index))
                    {
                        FlightSchedule fs = this.outboundFlightSchedules.get(0);
                        doReserveDirectFlight(fs, outboundCabinClass, numPassengers);
                        break;
                    }  
                }
                else
                {
                    if (outboundChoice > outboundOptions.get(index-1) && outboundChoice <= outboundOptions.get(index))
                    {
                        if (index % 3 == 0)
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + outboundChoice - outboundOptions.get(index - 1) - 1;
                            while (!(index - 3 < 0))
                            {
                                index = index - 3;
                                if (index != 0)
                                {
                                    numOfFlightsBefore = numOfFlightsBefore + outboundOptions.get(index) - outboundOptions.get(index - 1);
                                }
                                else
                                {
                                    numOfFlightsBefore = numOfFlightsBefore + outboundOptions.get(index);
                                }
                            }                                

                            FlightSchedule fs = this.outboundFlightSchedules.get(numOfFlightsBefore);
                            doReserveDirectFlight(fs, outboundCabinClass, numPassengers);
                            break;
                        }
                        else if (index % 3 == 1)
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundSingleFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + (outboundChoice - outboundOptions.get(index-1) - 1) * 2;
                            while (!(index - 3 < 0))
                            {
                                index = index - 3;
                                numOfFlightsBefore = numOfFlightsBefore + (outboundOptions.get(index) - outboundOptions.get(index-1)) * 2;
                            }

                            FlightSchedule fs1 = this.outboundSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.outboundSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
                            break;
                        }
                        else
                        {
                            Integer numOfFlightsBefore = 0;

                            numOfFlightsBefore = numOfFlightsBefore + (outboundChoice - outboundOptions.get(index-1) - 1) * 3;
                            while(!(index - 3 < 0))
                            {
                                index = index - 3;
                                numOfFlightsBefore = numOfFlightsBefore + (outboundOptions.get(index) - outboundOptions.get(index-1)) * 3;
                            }

                            FlightSchedule fs1 = this.outboundDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.outboundDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = this.outboundDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
                            break;
                        }                           
                    }
                }   
            }
            if (tripType == 2)
            {
                doReserveReturnFlight(returnFlightType, numPassengers, returnCabinClass, returnOptions);
            }
        }
        
        FlightReservationRecord reservation = new FlightReservationRecord(numPassengers, this.totalPrice);
        
        Set<ConstraintViolation<FlightReservationRecord>>constraintViolations = validator.validate(reservation);
        if(constraintViolations.isEmpty())
        {
            try
            {
                Long reservationId = flightReservationRecordSessionBeanRemote.createNewFlightReservationRecord(reservation, this.currentCustomer.getId(), this.reserveFlightSchedules);
                List<Long> passengers = doEnterPassengersDetails(numPassengers, reservationId);

                for (Long fsId: this.reserveFlightSchedules)
                {
                    List<Long> ps = new ArrayList<>();
                    for (Long p: passengers)
                    {
                        ps.add(p);
                    }

                    try
                    {
                        FlightSchedule fs = flightScheduleSessionBeanRemote.getFlightScheduleById(fsId);
                        Long ccId = this.mapping.get(fs);
                        CabinClass cc = cabinClassSessionBeanRemote.retrieveCabinClassById(ccId);
                        doSelectSeat(reservationId, cc, fs, numPassengers, ps);
                    } 
                    catch (FlightScheduleNotFoundException | CabinClassNotFoundException ex)
                    {
                        System.out.println(ex.getMessage());
                    }
                }
            }
            catch (InputDataValidationException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            showInputDataValidationErrorsForFlightReservationRecord(constraintViolations);
        }

        System.out.println("Seat numbers have been successfully recorded!\n");
        
        System.out.println("\nCheckout: ");
        System.out.println("Total amount to be paid: $" + totalPrice);

        while (true)
        {
            try
            {
                System.out.print("Enter credit card number> ");
                String ccNum = scanner.nextLine().trim();
                System.out.print("Enter name on credit card> ");
                String name = scanner.nextLine().trim();
                System.out.print("Enter CVV number> ");
                Integer cvv = scanner.nextInt();
                scanner.nextLine();

                if (ccNum.length() > 0 && name.length() > 0 && cvv > 100 & cvv < 1000)
                {
                    System.out.println("Your reservation has been successfully processed! Thank you!\n");
                    break;
                }
                else
                {
                    System.out.println("Invalid credit card details, please try again!\n");
                }
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter name on credit card in text and credit card number and CVV number in digits!\n");
                scanner.next();
            }
        }
    }
    
    public void doReserveReturnFlight(Integer returnFlightType, Integer numPassengers, CabinClassEnum returnCabinClass, List<Integer> returnOptions)
    {
        Scanner scanner = new Scanner(System.in);
        Integer options = 0;
        Integer returnChoice = 0;
        List<FlightSchedule> flightSchedules = new ArrayList<>();
        
        while (true)
        {
            if (returnFlightType == 1)
            {
                options = returnOptions.get(0);
            }
            else if (returnFlightType == 2)
            {
                options = returnOptions.get(5);
            }
            else
            {
                options = returnOptions.get(8);
            }

            if (options == 1)
            {
                System.out.print("Enter the flight schedule (1) you would like to reserve for return flight> ");
            }
            else
            {
                System.out.print("Enter the flight schedule (1-" + options + ") you would like to reserve for return flight> ");
            }

            try
            {
                returnChoice = 0;
                returnChoice = scanner.nextInt();
                scanner.nextLine(); 

                if (returnChoice < 1 || returnChoice > options)
                {
                    System.out.println("Invalid option, please try again!\n");
                }
                else
                {
                    break;
                }
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-" + options + "!\n");
                scanner.next();
            }
        }
        
        if (returnFlightType == 1)
        {
            FlightSchedule flightSchedule = this.returnFlightSchedules.get(returnChoice - 1);
            doReserveDirectFlight(flightSchedule, returnCabinClass, numPassengers);
        }
        else if (returnFlightType == 2)
        {
            for (Integer index = 0; index < 6; index++)
            {
                if (index == 0)
                {
                    if (returnChoice <= returnOptions.get(index))
                    {
                        FlightSchedule fs1 = this.returnSingleTransit.get(returnChoice * 2 - 2);
                        FlightSchedule fs2 = this.returnSingleTransit.get(returnChoice * 2 - 1);
                        flightSchedules.add(fs1);
                        flightSchedules.add(fs2);
                        doReserveSingleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
                        break;
                    }
                }
                else
                {
                    if (returnChoice > returnOptions.get(index-1) && returnChoice <= returnOptions.get(index))
                    {
                        if (index % 2 == 0)
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + (returnChoice - returnOptions.get(index-1) - 1) * 2;
                            while (!(index - 2 < 0))
                            {
                                index = index - 2;
                                if (index != 0)
                                {
                                    numOfFlightsBefore = numOfFlightsBefore +(returnOptions.get(index) - returnOptions.get(index - 1)) * 2;
                                }
                                else
                                {
                                    numOfFlightsBefore = numOfFlightsBefore + returnOptions.get(index) * 2;
                                }
                            }

                            FlightSchedule fs1 = this.returnSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.returnSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
                            break;
                        }
                        else
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + (returnChoice - returnOptions.get(index-1) - 1) * 3;
                            while (!(index - 2 < 0))
                            {
                                index = index - 2;
                                numOfFlightsBefore = numOfFlightsBefore +(returnOptions.get(index) - returnOptions.get(index - 1)) * 3;
                            }

                            FlightSchedule fs1 = this.returnDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.returnDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = this.returnDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
                            break;
                        }                           
                    }
                }   
            }
        }
        else if (returnFlightType == 3)
        {
            for (Integer index = 0; index < 9; index++)
            {
                if (index == 0)
                {
                    if (returnChoice <= returnOptions.get(index))
                    {
                        FlightSchedule fs = this.returnFlightSchedules.get(0);
                        doReserveDirectFlight(fs, returnCabinClass, numPassengers);
                        break;
                    }  
                }
                else
                {
                    if (returnChoice > returnOptions.get(index-1) && returnChoice <= returnOptions.get(index))
                    {
                        if (index % 3 == 0)
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + returnChoice - returnOptions.get(index - 1) - 1;
                            while (!(index - 3 < 0))
                            {
                                index = index - 3;
                                if (index != 0)
                                {
                                    numOfFlightsBefore = numOfFlightsBefore + returnOptions.get(index) - returnOptions.get(index - 1);
                                }
                                else
                                {
                                    numOfFlightsBefore = numOfFlightsBefore + returnOptions.get(index);
                                }
                            }                                

                            FlightSchedule fs = this.returnFlightSchedules.get(numOfFlightsBefore);
                            doReserveDirectFlight(fs, returnCabinClass, numPassengers);
                            break;
                        }
                        else if (index % 3 == 1)
                        {
                            Integer numOfFlightsBefore = 0;
                            // get index of wanted flight in outboundSingleFlightSchedules
                            numOfFlightsBefore = numOfFlightsBefore + (returnChoice - returnOptions.get(index-1) - 1) * 2;
                            while (!(index - 3 < 0))
                            {
                                index = index - 3;
                                numOfFlightsBefore = numOfFlightsBefore + (returnOptions.get(index) - returnOptions.get(index-1)) * 2;
                            }

                            FlightSchedule fs1 = this.returnSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.returnSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
                            break;
                        }
                        else
                        {
                            Integer numOfFlightsBefore = 0;

                            numOfFlightsBefore = numOfFlightsBefore + (returnChoice - returnOptions.get(index-1) - 1) * 3;
                            while(!(index - 3 < 0))
                            {
                                index = index - 3;
                                numOfFlightsBefore = numOfFlightsBefore + (returnOptions.get(index) - returnOptions.get(index-1)) * 3;
                            }

                            FlightSchedule fs1 = this.returnDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = this.returnDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = this.returnDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
                            break;
                        }                           
                    }
                }   
            }
        }
    }
    
    public List<Long> doEnterPassengersDetails(Integer numPassengers, Long reservationId)
    {
        Scanner scanner = new Scanner(System.in);
        List<Long> passengers = new ArrayList<>();
        Integer p = 1;
        String firstName = "";
        String lastName = "";
        String passportNum = "";
        System.out.println("\nCollecting Passenger(s) Information Now: \n");
        while (true)
        {
            try
            {
                System.out.print("Enter first name of passenger " + p + " > ");
                firstName = scanner.nextLine().trim();
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter first name of passenger in text!\n");
                scanner.next();
            }
            
            try
            {
                System.out.print("Enter last name of passenger " + p + " > ");
                lastName = scanner.nextLine().trim();
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter last name of passenger in text!\n");
                scanner.next();
            }
            
            try
            {
                System.out.print("Enter passport number of passenger " + p + " > ");
                passportNum = scanner.nextLine().trim();
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, enter passport number of passenger in text!\n");
                scanner.next();
            }

            if (firstName.length() > 0 && lastName.length() > 0 && passportNum.length() > 0)
            {
                Passenger passenger = new Passenger(firstName, lastName, passportNum);

                Set<ConstraintViolation<Passenger>>constraintViolations = validator.validate(passenger);

                if(constraintViolations.isEmpty())
                {
                    try
                    {
                        Long passengerId = passengerSessionBeanRemote.createNewPassenger(passenger, reservationId);
                        passengers.add(passengerId);

                        p++;
                        if (p > numPassengers)
                        {
                            break;
                        }
                    }
                    catch (InputDataValidationException ex)
                    {
                        System.out.println(ex.getMessage() + "\n");
                    }
                }
            }
        }
        
        return passengers;
    }
    
    public void doReserveDirectFlight(FlightSchedule flightSchedule, CabinClassEnum cabinClassType, Integer numPassengers)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        reserveFlightSchedules.add(flightSchedule.getFlightScheduleId());
        Integer ccOption = 0;
        Integer ccChoice = 0;
        
        // find out arrival time
        Integer flightHours = flightSchedule.getFlightHours();
        Integer flightMins = flightSchedule.getFlightMinutes();
        Integer timeZoneDiff = flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(flightSchedule.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        Date arrivalDateTime = calendar.getTime();
        
        System.out.println("Flight schedule to be booked: \n");
        System.out.printf("%10s%18s%30s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(flightSchedule.getDepartureDateTime()), formatter.format(arrivalDateTime), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
        if (cabinClassType == null)
        {
            try
            {
                Long cabinClassOneId = doSelectCabinClass(flightSchedule, numPassengers);
                BigDecimal price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassOneId);
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                this.mapping.put(flightSchedule, cabinClassOneId);
            }
            catch (FareNotFoundException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            try
            {
                CabinClass cabinClass = cabinClassSessionBeanRemote.retrieveCabinClassByAircraftConfigIdAndType(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                BigDecimal price = fares.get(0).getFareAmount();
                for (Fare f: fares)
                {
                    if (f.getFareAmount().compareTo(price) < 0)
                    {
                        price = f.getFareAmount();
                    }
                }

                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
               
                this.mapping.put(flightSchedule, cabinClass.getCabinClassId());
            }
            catch (CabinClassNotFoundException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
    }
    
    public void doReserveSingleTransitFlight(List<FlightSchedule> flightSchedules, CabinClassEnum cabinClassType, Integer numPassengers)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        
        FlightSchedule fs1 = flightSchedules.get(0);
        FlightSchedule fs2 = flightSchedules.get(1);
        this.reserveFlightSchedules.add(fs1.getFlightScheduleId());
        this.reserveFlightSchedules.add(fs2.getFlightScheduleId());
        
        // find out arrival time of fs1
        Integer flightHours = fs1.getFlightHours();
        Integer flightMins = fs1.getFlightMinutes();
        Integer timeZoneDiff = fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fs1.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        Date arrivalDateTime = calendar.getTime();
        
        System.out.println("Flight schedule to be booked: \n");
        System.out.printf("%10s%18s%30s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(fs1.getDepartureDateTime()), formatter.format(arrivalDateTime), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
        
        // find out arrival time of fs2
        flightHours = fs2.getFlightHours();
        flightMins = fs2.getFlightMinutes();
        timeZoneDiff = fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        calendar.setTime(fs2.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        arrivalDateTime = calendar.getTime();
        
        System.out.printf("%10s%18s%30s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(fs2.getDepartureDateTime()), formatter.format(arrivalDateTime), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
        
        if (cabinClassType == null)
        {
            try
            {
                Long cabinClassOneId = doSelectCabinClass(fs1, numPassengers);
                BigDecimal price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassOneId);
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                Long cabinClassTwoId = doSelectCabinClass(fs2, numPassengers);
                price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassTwoId);
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                this.mapping.put(fs1, cabinClassOneId);
                this.mapping.put(fs2, cabinClassTwoId);
            }
            catch (FareNotFoundException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            try
            {
                CabinClass cabinClass = cabinClassSessionBeanRemote.retrieveCabinClassByAircraftConfigIdAndType(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                BigDecimal price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                this.mapping.put(fs1, cabinClass.getCabinClassId());
                
                cabinClass = cabinClassSessionBeanRemote.retrieveCabinClassByAircraftConfigIdAndType(fs2.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                this.mapping.put(fs2, cabinClass.getCabinClassId());
            }
            catch (CabinClassNotFoundException | FareNotFoundException  ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
    }
    
    public void doReserveDoubleTransitFlight(List<FlightSchedule> flightSchedules, CabinClassEnum cabinClassType, Integer numPassengers)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        
        FlightSchedule fs1 = flightSchedules.get(0);
        FlightSchedule fs2 = flightSchedules.get(1);
        FlightSchedule fs3 = flightSchedules.get(2);
        this.reserveFlightSchedules.add(fs1.getFlightScheduleId());
        this.reserveFlightSchedules.add(fs2.getFlightScheduleId());
        this.reserveFlightSchedules.add(fs3.getFlightScheduleId());
        
        // find out arrival time of fs1
        Integer flightHours = fs1.getFlightHours();
        Integer flightMins = fs1.getFlightMinutes();
        Integer timeZoneDiff = fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(fs1.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        Date arrivalDateTime = calendar.getTime();
        
        System.out.println("Flight schedule to be booked: \n");
        System.out.printf("%10s%18s%30s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(fs1.getDepartureDateTime()), formatter.format(arrivalDateTime), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
        
        // find out arrival time of fs2
        flightHours = fs2.getFlightHours();
        flightMins = fs2.getFlightMinutes();
        timeZoneDiff = fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        calendar.setTime(fs2.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        arrivalDateTime = calendar.getTime();
        
        System.out.printf("%10s%18s%30s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(fs2.getDepartureDateTime()), formatter.format(arrivalDateTime), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
        
        // find out arrival time of fs3
        flightHours = fs3.getFlightHours();
        flightMins = fs3.getFlightMinutes();
        timeZoneDiff = fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff() - fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getTimeZoneDiff();  
        calendar.setTime(fs3.getDepartureDateTime());
        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightHours);
        calendar.add(GregorianCalendar.MINUTE, flightMins);
        calendar.add(GregorianCalendar.HOUR_OF_DAY, timeZoneDiff);
        arrivalDateTime = calendar.getTime();
        
        System.out.printf("%10s%18s%30s%30s%20s\n", fs3.getFlightSchedulePlan().getFlight().getFlightNumber(), fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs3.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(fs3.getDepartureDateTime()), formatter.format(arrivalDateTime), fs3.getFlightHours().toString() + "h " + fs3.getFlightMinutes().toString() + "min");
        
        if (cabinClassType == null)
        {
            try
            {
                Long cabinClassOneId = doSelectCabinClass(fs1, numPassengers);
                BigDecimal price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassOneId);
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                Long cabinClassTwoId = doSelectCabinClass(fs2, numPassengers);
                price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassTwoId);
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                Long cabinClassThreeId = doSelectCabinClass(fs3, numPassengers);
                price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs3.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassThreeId);
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                this.mapping.put(fs1, cabinClassOneId);
                this.mapping.put(fs2, cabinClassTwoId);
                this.mapping.put(fs3, cabinClassThreeId);
            }
            catch (FareNotFoundException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            try
            {
                CabinClass cabinClass = cabinClassSessionBeanRemote.retrieveCabinClassByAircraftConfigIdAndType(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                BigDecimal price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                this.mapping.put(fs1, cabinClass.getCabinClassId());
                
                cabinClass = cabinClassSessionBeanRemote.retrieveCabinClassByAircraftConfigIdAndType(fs2.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                this.mapping.put(fs2, cabinClass.getCabinClassId());
                
                cabinClass = cabinClassSessionBeanRemote.retrieveCabinClassByAircraftConfigIdAndType(fs3.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                price = fareSessionBeanRemote.getLowestFareByFlightSchedulePlanIdAndCabinClassId(fs3.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                this.totalPrice = this.totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                this.mapping.put(fs3, cabinClass.getCabinClassId());
            }
            catch (FareNotFoundException | CabinClassNotFoundException ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
    }
    
    public Long doSelectCabinClass(FlightSchedule flightSchedule, Integer numPassengers)
    {
        Scanner scanner = new Scanner(System.in);
        Integer ccOption = 0;
        Integer ccChoice = 0;
        List<CabinClass> cc = cabinClassSessionBeanRemote.retrieveCabinClassesByAircraftConfigId(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());

        System.out.println("Cabin classes available in " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber() + ": \n");
        System.out.printf("%10s%40s%25s%30s\n", "No.", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
        HashMap<Integer, Integer> cClasses = new HashMap<>();

        for (CabinClass c: cc)
        {
            try
            {   
                // Display only cabin classes that have sufficient number of balance seat for number of passengers
                SeatInventory si = seatInventorySessionBeanRemote.retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(c.getCabinClassId(), flightSchedule.getFlightScheduleId());
                List<Fare> fares = fareSessionBeanRemote.getFareByFlightSchedulePlanIdAndCabinClassId(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), c.getCabinClassId());

                BigDecimal fare = fares.get(0).getFareAmount();
                for (Fare f: fares)
                {
                    if (fare.compareTo(f.getFareAmount()) > 0)
                    {
                        fare = f.getFareAmount();
                    }
                }

                if (si.getNumOfBalanceSeats() >= numPassengers)
                {
                    ccOption++;
                    cClasses.put(ccOption, cc.indexOf(c));
                    System.out.printf("%10s%40s%25s%30s\n", ccOption.toString(), c.getCabinClassType().toString(), fare.toString(), (fare.multiply(new BigDecimal(numPassengers))).toString());
                }
            }
            catch (SeatInventoryNotFoundException ex)
            {
                continue;
            }
        }

        while(true)
        {
            try
            {
                System.out.print("Enter cabin class choice for " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber() + "> ");
                ccChoice = 0;
                ccChoice = scanner.nextInt();
                scanner.nextLine();

                if (ccChoice < 1 || ccChoice > ccOption)
                {
                    System.out.println("Invalid option, please try again!\n");
                }
                else
                {
                    break;
                }
            }
            catch (InputMismatchException ex)
            {
                System.out.println("Invalid input, select an option from 1-" + ccOption + "!\n");
            }
        }
        
        CabinClass cabinClass = cc.get(cClasses.get(ccChoice));
        return cabinClass.getCabinClassId();
    }
    
    public void doSelectSeat(Long reservationId, CabinClass cabinClass, FlightSchedule flightSchedule, Integer numPassengers, List<Long> passengers)
    {
        Scanner scanner = new Scanner(System.in);
        List<String> takenSeatNumbers = new ArrayList<>();
        
        System.out.println("\nSelecting seat for " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber() + ":\n");
        System.out.println("O represents a seat that is available");
        System.out.println("X represents a seat that has already been reserved\n");
        try
        {
            SeatInventory si = seatInventorySessionBeanRemote.retrieveSeatInventoryByCabinClassIdAndFlightScheduleId(cabinClass.getCabinClassId(), flightSchedule.getFlightScheduleId());
            List<CabinSeatInventory> takenSeats = cabinSeatInventorySessionBeanRemote.retrieveCabinSeatInventoryInSeatInventory(si.getSeatInventoryId());

            Integer numAisle = cabinClass.getNumOfAisle();
            Integer numSeatsAbreast = cabinClass.getNumOfSeatsAbreast();
            Integer numRows = cabinClass.getNumOfRows();
            String seatConfig = cabinClass.getSeatConfigPerColumn();
            Integer firstCol = 0;
            Integer secCol = 0;

            if (numAisle == 0)
            {
                firstCol = Integer.parseInt(seatConfig);
            }
            else if (numAisle == 1)
            {
                firstCol = Integer.parseInt(seatConfig.substring(0, 1));
                secCol = Integer.parseInt(seatConfig.substring(2));
            }
            else
            {
                firstCol = Integer.parseInt(seatConfig.substring(0, 1));
                secCol = Integer.parseInt(seatConfig.substring(2,3));
            }

            Boolean[][] seats = new Boolean[numRows][numSeatsAbreast];
            for (Integer row = 0; row < numRows; row++)
            {
                for (Integer col = 0; col < numSeatsAbreast; col++)
                {
                    seats[row][col] = true;
                }
            }

            if (!takenSeats.isEmpty())
            {
                for (CabinSeatInventory csi: takenSeats)
                {
                    takenSeatNumbers.add(csi.getSeatTaken());
                }
                
                for (String seatNum: takenSeatNumbers)
                {
                    Integer length = seatNum.length();
                    Integer takenCol = (int)seatNum.charAt(length - 1) - 65;
                    Integer takenRow = Integer.parseInt(seatNum.substring(0, length-1)) - 1;

                    seats[takenRow][takenCol] = false;
                }
            }

            if (numAisle == 0)
            {
                System.out.print("   ");
                for (Character col = 'A'; col < 65 + numSeatsAbreast; col++)
                {
                    System.out.print(col);
                }
                System.out.println("");
                
                for (Integer row = 0; row < numRows; row++)
                {
                    if (row > 8)
                    {
                        System.out.println(row+1 + "  ");
                    }
                    else
                    {
                        System.out.println(row+1 + " ");
                    }

                    for (Integer col = 0; col < numSeatsAbreast; col++)
                    {
                        if (seats[row][col])
                        {
                            System.out.print("O");
                        }
                        else
                        {
                            System.out.print("X");
                        }
                    }
                    System.out.println("");
                }
            }

            if (numAisle == 1)
            {
                Integer aisleCol = firstCol;
                System.out.print("   ");
                for (Character col = 'A'; col < 65 + numSeatsAbreast; col++)
                {
                    if ((col - 'A') == aisleCol)
                    {
                        System.out.print(" ");
                    }
                    System.out.print(col);
                }
                System.out.println("");

                System.out.println("");

                for (Integer row = 0; row < numRows; row++)
                {
                    if (row > 8)
                    {
                        System.out.print(row+1 + " ");
                    }
                    else
                    {
                        System.out.print(row+1 + "  ");
                    }

                    for (Integer col = 0; col < numSeatsAbreast+1; col++)
                    {
                        if (col == aisleCol)
                        {
                            System.out.print(" ");
                        }

                        if (col < aisleCol)
                        {
                            if (seats[row][col])
                            {
                                System.out.print("O");
                            }
                            else
                            {
                                System.out.print("X");
                            }
                        }

                        if (col > aisleCol)
                        {
                            if (seats[row][col-1])
                            {
                                System.out.print("O");
                            }
                            else
                            {
                                System.out.print("X");
                            }
                        }   
                    }
                    System.out.println("");
                }
            }

            if (numAisle == 2)
            {
                Integer firstAisle = firstCol + 1;
                Integer secAisle = firstCol + 1 + secCol;
                System.out.print("   ");
                for (Character col = 'A'; col < 65 + numSeatsAbreast; col++)
                {
                    if ((col - 'A' + 1) == firstAisle || (col - 'A' + 1) == secAisle)
                    {
                        System.out.print(" ");
                    }
                    System.out.print(col);
                }
                System.out.println("");

                for (Integer row = 0; row < numRows; row++)
                {
                    if (row > 8)
                    {
                        System.out.print(row+1 + " ");
                    }
                    else
                    {
                        System.out.print(row+1 + "  ");
                    }

                    for (Integer col = 0; col < numSeatsAbreast+2; col++)
                    {
                        if (col == firstAisle - 1|| col == secAisle)
                        {
                            System.out.print(" ");
                        }
                           
                        if (col < firstAisle)
                        {
                            if (seats[row][col])
                            {
                                System.out.print("O");
                            }
                            else
                            {
                                System.out.print("X");
                            }
                        }

                        if (col > firstAisle && col < secAisle)
                        {
                            if (seats[row][col-1])
                            {
                                System.out.print("O");
                            }
                            else
                            {
                                System.out.print("X");
                            }
                        }

                        if (col > secAisle)
                        {
                            if (seats[row][col-2])
                            {
                                System.out.print("O");
                            }
                            else
                            {
                                System.out.print("X");
                            }
                        }
                    }
                    System.out.println("");
                }
            }

            while (!passengers.isEmpty())
            {
                try
                {
                    Boolean createSuccess = false;
                    Long passenger = passengers.remove(0);
                    Passenger p = passengerSessionBeanRemote.retrievePassengerByPassengerId(passenger);
                    System.out.print("Please select a seat for passenger " + p.getFirstName() + " " + p.getLastName() + " (eg.1A) > ");
                    String reserveSeat = scanner.nextLine().trim();

                    while (!createSuccess)
                    {
                        if (!takenSeatNumbers.contains(reserveSeat))
                        {
                            Integer length = reserveSeat.length();
                            Integer reserveCol = (int)reserveSeat.charAt(length - 1) - 65;
                            try
                            {
                                Integer reserveRow = Integer.parseInt(reserveSeat.substring(0, length-1)) - 1;
                                if (reserveRow <= numRows && reserveCol <= numSeatsAbreast)
                                {
                                    CabinSeatInventory seat = new CabinSeatInventory(reserveSeat);
                                    Set<ConstraintViolation<CabinSeatInventory>>constraintViolations = validator.validate(seat);
                                    
                                    if(constraintViolations.isEmpty())
                                    {
                                        try
                                        {
                                            Long cabinSeatId = cabinSeatInventorySessionBeanRemote.createNewCabinSeatInventory(seat, si.getSeatInventoryId(), passenger); 
                                            createSuccess = true;
                                        } 
                                        catch(CabinSeatInventoryExistException | InputDataValidationException ex)
                                        {
                                            System.out.println(ex.getMessage() + "\n");
                                        }
                                    }
                                    else
                                    {
                                        showInputDataValidationErrorsForCabinSeatInventory(constraintViolations);
                                    }
                                }
                                else
                                {
                                    System.out.println("Invalid seat number entered, please try again!\n");
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                System.out.println("Invalid seat number entered, please try again!\n");
                            }
                        }
                        else
                        {
                            System.out.println("Cabin seat number is already taken, please select another seat!\n");
                        }    
                    }
                }
                catch (PassengerNotFoundException ex)
                {
                    System.out.println(ex.getMessage() + "\n");
                }
                catch (InputMismatchException ex)
                {
                    System.out.println("Invalid input, enter the chosen seat in the given format!\n");
                }
            }
        }
        catch (SeatInventoryNotFoundException ex)
        {
            System.out.println("An error has occurred while trying to retrieve the seat inventory of cabin class!\n");
        }
    }
    
    public void doViewAllFlightReservations()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        System.out.println("\n*** FRS Reservation :: View All Flight Reservations ***\n");
        
        List<FlightReservationRecord> records = flightReservationRecordSessionBeanRemote.retrieveReservationRecordsByCustomerId(currentCustomer.getId());
        
        if (records.isEmpty())
        {
            System.out.println("You have yet to make any reservations!\n");
        }
        else
        {
            Integer listing = 0;
        
            System.out.printf("%13s%20s%20s%15s\n", "Record ID", "Departure Date", "No. of Passengers", "Total Amount");
            for (FlightReservationRecord frr: records)
            {
                listing++;
                System.out.printf("%5s%20s%15s\n", frr.getRecordId().toString(), formatter.format(frr.getFlightSchedules().get(0).getDepartureDateTime()), frr.getNumOfPassengers().toString(), frr.getTotalAmount());
            } 
        }
    }
    
    public void doViewFlightReservationDetails()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Long recordId = 0l;
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\n*** FRS Reservation :: View My Flight Reservation Details ***\n");
        
        try
        {
            System.out.print("Enter ID of record to be viewed> ");
            recordId = scanner.nextLong();
            scanner.nextLine();
        
            FlightReservationRecord record = flightReservationRecordSessionBeanRemote.retrieveReservationRecordById(recordId, this.currentCustomer.getId());
            List<Passenger> passengers = record.getPassengers();
            HashMap<FlightSchedule, List<CabinSeatInventory>> seating = new HashMap<>();
            HashMap<FlightSchedule, CabinClass> mapping = new HashMap<>();
            Passenger p = passengerSessionBeanRemote.retrievePassengerByPassengerId(passengers.get(0).getPassengerId());

            for (CabinSeatInventory csi: p.getCabinSeats())
            {
                FlightSchedule fs = csi.getSeatInventory().getFlightSchedule();
                CabinClass cc = csi.getSeatInventory().getCabinClass();
                mapping.put(fs, cc);
            }

            for (FlightSchedule fs: record.getFlightSchedules())
            {
                List<CabinSeatInventory> cc = new ArrayList<>();
                seating.put(fs, cc);
            }

            for (Passenger passenger: passengers)
            {
                Passenger ps = passengerSessionBeanRemote.retrievePassengerByPassengerId(passenger.getPassengerId());
                for (CabinSeatInventory csi: ps.getCabinSeats())
                {
                    FlightSchedule f = csi.getSeatInventory().getFlightSchedule();
                    List<CabinSeatInventory> cabinSeats = seating.get(f);
                    cabinSeats.add(csi);
                    seating.put(f, cabinSeats);
                }
            }

            System.out.println("\nReservation Details of Reservation Record ID " + recordId + ": \n");
            System.out.println("Flight Schedule(s):");
            for (FlightSchedule fs: record.getFlightSchedules())
            {
                System.out.printf("%10s%15s%30s%40s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Cabin Class");
                System.out.printf("%10s%15s%30s%40s\n", fs.getFlightSchedulePlan().getFlight().getFlightNumber(), fs.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), formatter.format(fs.getDepartureDateTime()), mapping.get(fs).getCabinClassType().toString());
                System.out.println("Seats Taken in this flight schedule:");
                for (CabinSeatInventory cabinSeat: seating.get(fs))
                {
                    System.out.println(cabinSeat.getSeatTaken());
                }
                System.out.println("");
            }

            System.out.println("Passengers: \n");
            System.out.printf("%20s%20s%20s\n", "First Name", "First Name", "Passport No.");
            for (Passenger passenger: passengers)
            {
                Passenger ps = passengerSessionBeanRemote.retrievePassengerByPassengerId(p.getPassengerId());
                System.out.printf("%20s%20s%20s\n", ps.getFirstName(), ps.getLastName(), ps.getPassportNum());
            }

            System.out.println("Total Amount Paid for Reservation: $" + record.getTotalAmount() + "\n");
        }
        catch (InputMismatchException ex)
        {
            System.out.println("Invalid input, enter the record ID in digits!\n");
        }
        catch (FlightReservationRecordNotFoundException | PassengerNotFoundException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public void doLogout()
    {
        currentCustomer = null;
        this.outboundFlightSchedules.clear();
        this.outboundSingleTransit.clear();
        this.outboundDoubleTransit.clear();
        this.returnFlightSchedules.clear();
        this.returnSingleTransit.clear();
        this.returnDoubleTransit.clear();
        this.totalPrice = new BigDecimal(0);
        this.mapping.clear();
    }
    
    private void showInputDataValidationErrorsForCustomer (Set<ConstraintViolation<Customer>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForFlightReservationRecord (Set<ConstraintViolation<FlightReservationRecord>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForCabinSeatInventory (Set<ConstraintViolation<CabinSeatInventory>>constraintViolations)
    {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}