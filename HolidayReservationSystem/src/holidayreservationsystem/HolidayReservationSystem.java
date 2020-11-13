/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import ws.client.cabinClass.CabinClassEnum;
import ws.client.flightSchedule.FlightSchedule;
import ws.client.partner.InvalidLoginCredentialException;
import ws.client.partner.InvalidLoginCredentialException_Exception;
import ws.client.partner.Partner;
import ws.client.partner.PartnerNotFoundException_Exception;
import ws.client.airport.Airport;
import ws.client.cabinClass.CabinClass;
import ws.client.cabinClass.CabinClassNotFoundException;
import ws.client.cabinClass.CabinClassNotFoundException_Exception;
import ws.client.cabinSeatInventory.CabinSeatInventory;
import ws.client.cabinSeatInventory.CabinSeatInventoryExistException_Exception;
import ws.client.fare.Fare;
import ws.client.fare.FareNotFoundException_Exception;
import ws.client.flightReservationRecord.FlightReservationRecord;
import ws.client.flightSchedule.FlightScheduleNotFoundException_Exception;
import ws.client.passenger.Passenger;
import ws.client.passenger.PassengerNotFoundException_Exception;
import ws.client.seatInventory.SeatInventory;
import ws.client.seatInventory.SeatInventoryNotFoundException;
import ws.client.seatInventory.SeatInventoryNotFoundException_Exception;

/**
 *
 * @author yeerouhew
 */
public class HolidayReservationSystem {

    public static Partner currentPartner;
    private static BigDecimal totalPrice;
    private static List<Long> reserveFlightSchedules;
    private static HashMap<ws.client.flightSchedule.FlightSchedule, Long> mapping;
    
    private static List<FlightSchedule> outboundFlightSchedules;
    private static List<FlightSchedule> outboundSingleTransit;
    private static List<FlightSchedule> outboundDoubleTransit;
    
    private static List<FlightSchedule> returnFlightSchedules;
    private static List<FlightSchedule> returnSingleTransit;
    private static List<FlightSchedule> returnDoubleTransit;
    
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
            System.out.println("\n*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Login");
            System.out.println("2: Search for flights");
            System.out.println("3: Exit\n");
            
            response = 0;
            
            while(response < 1 || response > 3)
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
                    doPartnerSearchFlight();
                }
                else if (response == 3)
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
            System.out.println("\n*** Welcome to Holiday Reservation System ***\n");
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
                    doPartnerSearchFlight();
                }
                else if(response == 2)
                {
                    doViewPartnerFlightReservations();
                }
                else if(response == 3)
                {
                    doViewPartnerFlightReservationDetails();
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
        
        System.out.println("\n*** Holiday Reservation System:: Partner Login ***\n");
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
    
    private static void doPartnerSearchFlight() 
    {
        outboundDoubleTransit = new ArrayList<>();
        outboundFlightSchedules = new ArrayList<>();
        outboundSingleTransit = new ArrayList<>();
        returnDoubleTransit = new ArrayList<>();
        returnFlightSchedules = new ArrayList<>();
        returnSingleTransit = new ArrayList<>();
        
        Scanner scanner = new Scanner (System.in);
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");
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
        
        System.out.println("\n*** Holiday Reservation System :: Partner Search Flight ***\n");

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
        
        List<Airport> airports = getAllAirportsUnmanaged();
        
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
                        doPartnerSearchFlight();
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
        
        if (reserveFlight)
        {
            if (currentPartner == null)
            {
                Integer result = 0;
                System.out.println("To reserve flight tickets, please login first!\n");
                
                try
                {
                    doLogin();
                // doPartnerReserveFlight(tripType, numPassengers, outboundFlightType, returnFlightType, outboundCabinClass, returnCabinClass, outboundOptions, returnOptions);
                }
                catch (InvalidLoginCredentialException_Exception | PartnerNotFoundException_Exception ex)
                {
                    System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                }
            }
            else
            {
                // doPartnerReserveFlight(tripType, numPassengers, outboundFlightType, returnFlightType, outboundCabinClass, returnCabinClass, outboundOptions, returnOptions);
            }
        }
    }
    
    public static List<Integer> doSearchAllFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers, Boolean isReturn)
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
        List<FlightSchedule> flightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> singleTransit = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> doubleTransit = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        
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
                        outboundFlightSchedules.add(fs1);
                    }
                    else
                    {
                        returnFlightSchedules.add(fs1);
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
        flightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedBeforeDepartureDate, formattedDepartureDate, preferredCabinClass, numPassengers);
        
        //Connecting Flight Schedules 3 days before chosen date
        beforeDepartureDateCalendar.setTime(formattedDepartureDate);
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, -3);
        Date dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        Date dateEnd = beforeDepartureDateCalendar.getTime();
        singleTransit = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Connecting Flight Schedules 2 days before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Connecting Flight Schedules 1 day before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
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
                    doPrintDirectFlightSchedule(fs1, preferredCabinClass , numPassengers);
                    
                    if (!isReturn)
                    {
                        outboundFlightSchedules.add(fs1);
                    }
                    else
                    {
                        returnFlightSchedules.add(fs1);
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
        flightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDateEnd, formattedAfterDepartureDate, preferredCabinClass, numPassengers);
        
        // Connecting Flight schedules 1 day after chosen date
        afterDepartureDateCalendar.setTime(formattedDepartureDate);
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        singleTransit = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Connecting Flight schedules 2 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Connecting Flight schedules 3 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
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
                        outboundFlightSchedules.add(fs1);
                    }
                    else
                    {
                        returnFlightSchedules.add(fs1);
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
    
    public static List<Integer> doSearchDirectFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers, Boolean isReturn)
    {
        Integer option = 0;
        System.out.println("Direct Flights Available: \n");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        GregorianCalendar departureDateCalendar = new GregorianCalendar();
        departureDateCalendar.setTime(formattedDepartureDate);
        departureDateCalendar.add(GregorianCalendar.HOUR_OF_DAY, +24);
        Date formattedDepartureDateEnd = departureDateCalendar.getTime();
        
        List<FlightSchedule> flightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        if (!isReturn)
        {
            outboundFlightSchedules = flightSchedules;
        }
        else
        {
            returnFlightSchedules = flightSchedules;
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
        List<FlightSchedule> beforeDepartureFlightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedBeforeDepartureDate, formattedDepartureDate, preferredCabinClass, numPassengers);
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
                    outboundFlightSchedules.add(fs2);
                }
                else
                {
                    returnFlightSchedules.add(fs2);
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
        List<FlightSchedule> afterDepartureFlightSchedules = searchDirectFlightSchedules(departureAirportId, destinationAirportId, formattedDepartureDateEnd, formattedAfterDepartureDate, preferredCabinClass, numPassengers);
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
                    outboundFlightSchedules.add(fs3);
                }
                else
                {
                    returnFlightSchedules.add(fs3);
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
    
    public static List<Integer> doSearchConnectingFlights(Long departureAirportId, Long destinationAirportId, Date formattedDepartureDate, CabinClassEnum preferredCabinClass, Integer numPassengers, Boolean isReturn)
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
        List<FlightSchedule> singleTransit = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        List<FlightSchedule> doubleTransit = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, formattedDepartureDate, formattedDepartureDateEnd, preferredCabinClass, numPassengers);
        
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
        singleTransit = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Flight Schedules 2 days before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Flight Schedules 1 day before chosen date
        dateStart = beforeDepartureDateCalendar.getTime();
        beforeDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Print out flight schedules 1-3 days before chosen date
        singleBeforeOptions = doubleOptions;
        doubleBeforeOptions = doubleOptions;
        
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
        singleTransit = searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        doubleTransit = searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
        
        // Flight schedules 2 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            doubleTransit.add(fs2);
        }
        
        // Flight schedules 3 days after chosen date
        dateStart = afterDepartureDateCalendar.getTime();
        afterDepartureDateCalendar.add(GregorianCalendar.DAY_OF_MONTH, +1);
        dateEnd = beforeDepartureDateCalendar.getTime();
        for (FlightSchedule fs1: searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
        {
            singleTransit.add(fs1);
        }
        for (FlightSchedule fs2: searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers))
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
    
    public static void doPrintDirectFlightSchedule(FlightSchedule flightSchedule, CabinClassEnum preferredCabinClass, Integer numPassengers)
    {
        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");      
        System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
        System.out.printf("%10s%18s%30s%20s\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + flightSchedule.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(flightSchedule.getDepartureDateTime()), flightSchedule.getFlightHours().toString() + "h " + flightSchedule.getFlightMinutes().toString() + "min");
        List<CabinClass> cabinClasses = retrieveCabinClassesByAircraftConfigIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
        if (preferredCabinClass == null)
        {
            System.out.println("Price of cabin class(es):\n");
            System.out.printf("%40s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
            for (CabinClass cc: cabinClasses)
            {
                try
                {
                    SeatInventory seatInventory = retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(cc.getCabinClassId(), flightSchedule.getFlightScheduleId());
                    if (seatInventory.getNumOfBalanceSeats() < numPassengers)
                    {
                        continue;
                    }

                    java.util.List<ws.client.fare.Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
                    BigDecimal lowestFare = fares.get(0).getFareAmount();
                    for (ws.client.fare.Fare fare: fares)
                    {
                        if (fare.getFareAmount().compareTo(lowestFare) < 0)
                        {
                            lowestFare = fare.getFareAmount();
                        }
                    }

                    System.out.printf("%40s%25s%30s\n", cc.getCabinClassType().toString(), lowestFare.toString() , (lowestFare.multiply(new BigDecimal(numPassengers))).toString());
                }
                catch (SeatInventoryNotFoundException_Exception ex)
                {
                    continue;
                }
            }
        }
        else
        {
            System.out.printf("%40s%25s%30s\n", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
            for (CabinClass cc: cabinClasses)
            {
                if (cc.getCabinClassType().equals(preferredCabinClass))
                {
                    List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
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
    
    public static Integer doPrintSingleTransitFlightSchedule(List<FlightSchedule> flightSchedules, CabinClassEnum preferredCabinClass, Integer numPassengers, Integer options, Boolean isReturn)
    {
        while (!flightSchedules.isEmpty())
        {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");
            BigDecimal pricePerPassenger = new BigDecimal(0);
            FlightSchedule fs1 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesOne = retrieveCabinClassesByAircraftConfigIdUnmanaged(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs2 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesTwo = retrieveCabinClassesByAircraftConfigIdUnmanaged(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            
            if (!isReturn)
            {
                outboundSingleTransit.add(fs1);
                outboundSingleTransit.add(fs2);
            }
            else
            {
                returnSingleTransit.add(fs1);
                returnSingleTransit.add(fs2);
            }

            options++;
            System.out.println(options + ":");
            
            if (preferredCabinClass != null)
            {
                System.out.printf("%10s%18s%30s%20s\n", "Flight No.", "Itinerary", "Departure Date and Time", "Flight Duration");
                System.out.printf("%10s%18s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs1.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs1.getDepartureDateTime()), fs1.getFlightHours().toString() + "h " + fs1.getFlightMinutes().toString() + "min");
                System.out.printf("%10s%18s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getOrigin().getIataCode() + "-" + fs2.getFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getIataCode(), dateTimeFormatter.format(fs2.getDepartureDateTime()), fs2.getFlightHours().toString() + "h " + fs2.getFlightMinutes().toString() + "min");
                for (CabinClass cc: cabinClassesOne)
                {
                    if (cc.getCabinClassType().equals(preferredCabinClass))
                    {

                        List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
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

                        List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
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
    
    public static Integer doPrintDoubleTransitFlightSchedules(List<FlightSchedule> flightSchedules, CabinClassEnum preferredCabinClass, Integer numPassengers, Integer options, Boolean isReturn)
    {
        while (!flightSchedules.isEmpty())
        {
            SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");
            BigDecimal pricePerPassenger = new BigDecimal(0);
            FlightSchedule fs1 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesOne = retrieveCabinClassesByAircraftConfigIdUnmanaged(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs2 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesTwo = retrieveCabinClassesByAircraftConfigIdUnmanaged(fs2.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            FlightSchedule fs3 = flightSchedules.remove(0);
            List<CabinClass> cabinClassesThree = retrieveCabinClassesByAircraftConfigIdUnmanaged(fs3.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());
            
            if (!isReturn)
            {
                outboundDoubleTransit.add(fs1);
                outboundDoubleTransit.add(fs2);
                outboundDoubleTransit.add(fs3);
            }
            else
            {
                returnDoubleTransit.add(fs1);
                returnDoubleTransit.add(fs2);
                returnDoubleTransit.add(fs3);
            }
            
            options++;
            System.out.println(options + ":");
            
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
                        List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
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

                        List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
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
                        List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs3.getFlightSchedulePlan().getFlightSchedulePlanId(), cc.getCabinClassId());
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
    
    public static void doPartnerReserveFlight(Integer tripType, Integer numPassengers, Integer outboundFlightType, Integer returnFlightType, CabinClassEnum outboundCabinClass, CabinClassEnum returnCabinClass, List<Integer> outboundOptions, List<Integer> returnOptions)
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

        if (outboundFlightType == 1)
        {
            FlightSchedule flightSchedule = outboundFlightSchedules.get(outboundChoice - 1);
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
                        FlightSchedule fs1 = outboundSingleTransit.get(outboundChoice * 2 - 2);
                        FlightSchedule fs2 = outboundSingleTransit.get(outboundChoice * 2 - 1);
                        flightSchedules.add(fs1);
                        flightSchedules.add(fs2);
                        doReserveSingleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
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

                            FlightSchedule fs1 = outboundSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = outboundSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
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

                            FlightSchedule fs1 = outboundDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = outboundDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = outboundDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
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
                        FlightSchedule fs = outboundFlightSchedules.get(0);
                        doReserveDirectFlight(fs, outboundCabinClass, numPassengers);
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

                            FlightSchedule fs = outboundFlightSchedules.get(numOfFlightsBefore);
                            doReserveDirectFlight(fs, outboundCabinClass, numPassengers);
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

                            FlightSchedule fs1 = outboundSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = outboundSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
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

                            FlightSchedule fs1 = outboundDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = outboundDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = outboundDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, outboundCabinClass, numPassengers);
                        }                           
                    }
                }   
            }
            if (tripType == 2)
            {
                doReserveReturnFlight(returnFlightType, numPassengers, returnCabinClass, returnOptions);
            }
        }
        
        FlightReservationRecord reservation = new FlightReservationRecord(numPassengers, totalPrice);
        Long reservationId = createNewFlightReservationRecord(reservation, currentPartner.getId(), reserveFlightSchedules);

        List<Long> passengers = doEnterPassengersDetails(numPassengers, reservationId);
        
        for (Long fsId: reserveFlightSchedules)
        {
            try
            {
                FlightSchedule fs = getFlightScheduleByIdUnmanaged(fsId);
                Long ccId = mapping.get(fs);
                CabinClass cc = retrieveCabinClassByIdUnmanaged(ccId);
                doSelectSeat(reservationId, cc, fs, numPassengers, passengers);
            } 
            catch (FlightScheduleNotFoundException_Exception | CabinClassNotFoundException_Exception ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        
        System.out.println("Seat numbers have been successfully recorded!\n");
        
        System.out.println("\nCheckout: ");
        System.out.println("Total amount to be paid:" + totalPrice);

        while (true)
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
    }
    
    public static void doReserveReturnFlight(Integer returnFlightType, Integer numPassengers, CabinClassEnum returnCabinClass, List<Integer> returnOptions)
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
        
        if (returnFlightType == 1)
        {
            FlightSchedule flightSchedule = returnFlightSchedules.get(returnChoice - 1);
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
                        FlightSchedule fs1 = returnSingleTransit.get(returnChoice * 2 - 2);
                        FlightSchedule fs2 = returnSingleTransit.get(returnChoice * 2 - 1);
                        flightSchedules.add(fs1);
                        flightSchedules.add(fs2);
                        doReserveSingleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
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

                            FlightSchedule fs1 = returnSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = returnSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
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

                            FlightSchedule fs1 = returnDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = returnDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = returnDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
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
                        FlightSchedule fs = returnFlightSchedules.get(0);
                        doReserveDirectFlight(fs, returnCabinClass, numPassengers);
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

                            FlightSchedule fs = returnFlightSchedules.get(numOfFlightsBefore);
                            doReserveDirectFlight(fs, returnCabinClass, numPassengers);
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

                            FlightSchedule fs1 = returnSingleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = returnSingleTransit.get(numOfFlightsBefore + 1);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            doReserveSingleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
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

                            FlightSchedule fs1 = returnDoubleTransit.get(numOfFlightsBefore);
                            FlightSchedule fs2 = returnDoubleTransit.get(numOfFlightsBefore + 1);
                            FlightSchedule fs3 = returnDoubleTransit.get(numOfFlightsBefore + 2);
                            flightSchedules.add(fs1);
                            flightSchedules.add(fs2);
                            flightSchedules.add(fs3);
                            doReserveDoubleTransitFlight(flightSchedules, returnCabinClass, numPassengers);
                        }                           
                    }
                }   
            }
        }
    }
    
    public static List<Long> doEnterPassengersDetails(Integer numPassengers, Long reservationId)
    {
        Scanner scanner = new Scanner(System.in);
        List<Long> passengers = new ArrayList<>();
        Integer p = 1;
        System.out.println("\nCollecting Passenger(s) Information Now: \n");
        while (true)
        {
            System.out.print("Enter first name of passenger " + p + " > ");
            String firstName = scanner.nextLine().trim();
            System.out.print("Enter last name of passenger " + p + " > ");
            String lastName = scanner.nextLine().trim();
            System.out.print("Enter passport number of passenger " + p + " > ");
            String passportNum = scanner.nextLine().trim();

            if (firstName.length() > 0 && lastName.length() > 0 && passportNum.length() > 0)
            {
                Passenger passenger = new Passenger(firstName, lastName, passportNum);
                Long passengerId = createNewPassenger(passenger, reservationId);
                passengers.add(passengerId);

                p++;
                if (p > numPassengers)
                {
                    break;
                }
            }
        }
        
        return passengers;
    }
    
    public static void doReserveDirectFlight(FlightSchedule flightSchedule, CabinClassEnum cabinClassType, Integer numPassengers)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");

        reserveFlightSchedules.add(flightSchedule.getFlightScheduleId());
        Integer ccOption = 0;
        Integer ccChoice = 0;
        System.out.println("Flight schedule to be booked: \n");
        System.out.printf("%10s%30s%20s\n\n", flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber(), formatter.format(flightSchedule.getDepartureDateTime()), flightSchedule.getFlightHours() + "h " + flightSchedule.getFlightMinutes() + "min");
        if (cabinClassType == null)
        {
            try
            {
                Long cabinClassOneId = doSelectCabinClass(flightSchedule, numPassengers);
                BigDecimal price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassOneId);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                mapping.put(flightSchedule, cabinClassOneId);
            }
            catch(FareNotFoundException_Exception ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            try
            {
                CabinClass cabinClass = retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                BigDecimal price = fares.get(0).getFareAmount();
                for (Fare f: fares)
                {
                    if (f.getFareAmount().compareTo(price) < 0)
                    {
                        price = f.getFareAmount();
                    }
                }

                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
               
                mapping.put(flightSchedule, cabinClass.getCabinClassId());
            }
            catch (CabinClassNotFoundException_Exception ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
    }
    
    public static void doReserveSingleTransitFlight(List<FlightSchedule> flightSchedules, CabinClassEnum cabinClassType, Integer numPassengers)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");
        
        FlightSchedule fs1 = flightSchedules.get(0);
        FlightSchedule fs2 = flightSchedules.get(1);
        reserveFlightSchedules.add(fs1.getFlightScheduleId());
        reserveFlightSchedules.add(fs2.getFlightScheduleId());
        
        System.out.println("Flight schedule to be booked: \n");
        System.out.printf("%10s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), formatter.format(fs1.getDepartureDateTime()), fs1.getFlightHours() + "h " + fs1.getFlightMinutes() + "min");
        System.out.printf("%10s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), formatter.format(fs2.getDepartureDateTime()), fs2.getFlightHours() + "h " + fs2.getFlightMinutes() + "min");
        
        if (cabinClassType == null)
        {
            try {
                Long cabinClassOneId = doSelectCabinClass(fs1, numPassengers);
                BigDecimal price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassOneId);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                Long cabinClassTwoId = doSelectCabinClass(fs2, numPassengers);
                price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassTwoId);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                mapping.put(fs1, cabinClassOneId);
                mapping.put(fs2, cabinClassTwoId);
            } catch (FareNotFoundException_Exception ex) {
                Logger.getLogger(HolidayReservationSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            try
            {
                CabinClass cabinClass = retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                BigDecimal price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                mapping.put(fs1, cabinClass.getCabinClassId());
                
                cabinClass = retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(fs2.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                mapping.put(fs2, cabinClass.getCabinClassId());
            }
            catch (CabinClassNotFoundException_Exception | FareNotFoundException_Exception ex)
            {
                System.out.println("An error has occurred while retrieving cabin class for reservation!\n");
            }
        }
    }
    
    public static void doReserveDoubleTransitFlight(List<FlightSchedule> flightSchedules, CabinClassEnum cabinClassType, Integer numPassengers)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm'Z'");
        
        FlightSchedule fs1 = flightSchedules.get(0);
        FlightSchedule fs2 = flightSchedules.get(1);
        FlightSchedule fs3 = flightSchedules.get(2);
        reserveFlightSchedules.add(fs1.getFlightScheduleId());
        reserveFlightSchedules.add(fs2.getFlightScheduleId());
        reserveFlightSchedules.add(fs3.getFlightScheduleId());
        
        System.out.println("Flight schedule to be booked: \n");
        System.out.printf("%10s%30s%20s\n", fs1.getFlightSchedulePlan().getFlight().getFlightNumber(), formatter.format(fs1.getDepartureDateTime()), fs1.getFlightHours() + "h " + fs1.getFlightMinutes() + "min");
        System.out.printf("%10s%30s%20s\n", fs2.getFlightSchedulePlan().getFlight().getFlightNumber(), formatter.format(fs2.getDepartureDateTime()), fs2.getFlightHours() + "h " + fs2.getFlightMinutes() + "min");
        System.out.printf("%10s%30s%20s\n", fs3.getFlightSchedulePlan().getFlight().getFlightNumber(), formatter.format(fs3.getDepartureDateTime()), fs3.getFlightHours() + "h " + fs3.getFlightMinutes() + "min");
        
        if (cabinClassType == null)
        {
            try
            {
                Long cabinClassOneId = doSelectCabinClass(fs1, numPassengers);
                BigDecimal price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassOneId);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                Long cabinClassTwoId = doSelectCabinClass(fs2, numPassengers);
                price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassTwoId);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                Long cabinClassThreeId = doSelectCabinClass(fs3, numPassengers);
                price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs3.getFlightSchedulePlan().getFlightSchedulePlanId(), cabinClassThreeId);
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                mapping.put(fs1, cabinClassOneId);
                mapping.put(fs2, cabinClassTwoId);
                mapping.put(fs3, cabinClassThreeId);
            }
            catch (FareNotFoundException_Exception ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
        else
        {
            try
            {
                CabinClass cabinClass = retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(fs1.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                BigDecimal price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs1.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                mapping.put(fs1, cabinClass.getCabinClassId());
                
                cabinClass = retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(fs2.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs2.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));
                
                mapping.put(fs2, cabinClass.getCabinClassId());
                
                cabinClass = retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(fs3.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId(), cabinClassType);
                price = getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(fs3.getFlightSchedulePlan().getFlightSchedulePlanId(),cabinClass.getCabinClassId());
                totalPrice = totalPrice.add(price.multiply(new BigDecimal(numPassengers)));

                mapping.put(fs3, cabinClass.getCabinClassId());
            }
            catch (CabinClassNotFoundException_Exception  | FareNotFoundException_Exception ex)
            {
                System.out.println(ex.getMessage() + "\n");
            }
        }
    }
    
    public static Long doSelectCabinClass(FlightSchedule flightSchedule, Integer numPassengers)
    {
        Scanner scanner = new Scanner (System.in);
        Integer ccOption = 0;
        Integer ccChoice = 0;
        List<CabinClass> cc = retrieveCabinClassesByAircraftConfigIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlight().getAircraftConfig().getAircraftConfigId());

        System.out.println("Cabin classes available in " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber() + ": \n");
        System.out.printf("%10s40s%25s%30s\n", "No.", "Cabin Class Type", "Price Per Passenger", "Price for All Passengers");
        HashMap<Integer, Integer> cClasses = new HashMap<>();

        for (CabinClass c: cc)
        {
            try
            {   
                // Display only cabin classes that have sufficient number of balance seat for number of passengers
                SeatInventory si = retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(c.getCabinClassId(), flightSchedule.getFlightScheduleId());
                List<Fare> fares = getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedule.getFlightSchedulePlan().getFlightSchedulePlanId(), c.getCabinClassId());

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
            catch (SeatInventoryNotFoundException_Exception ex)
            {
                continue;
            }
        }

        while (ccChoice < 1 || ccChoice > ccOption)
        {
            System.out.print("Enter cabin class choice for " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber() + "> ");
            ccChoice = 0;
            ccChoice = scanner.nextInt();
            scanner.nextLine();
        }

        CabinClass cabinClass = cc.get(cClasses.get(ccChoice));
        
        return cabinClass.getCabinClassId();
    }
    
    public static void doSelectSeat(Long reservationId, CabinClass cabinClass, FlightSchedule flightSchedule, Integer numPassengers, List<Long> passengers)
    {
        Scanner scanner = new Scanner(System.in);
        List<String> takenSeatNumbers = new ArrayList<>();
        
        System.out.println("\nSelecting seat for " + flightSchedule.getFlightSchedulePlan().getFlight().getFlightNumber() + ":\n");
        System.out.println("O represents a seat that is available");
        System.out.println("X represents a seat that has already been reserved\n");
        try
        {
            SeatInventory si = retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(cabinClass.getCabinClassId(), flightSchedule.getFlightScheduleId());
            List<CabinSeatInventory> takenSeats = retrieveCabinSeatInventoryInSeatInventoryUnmanaged(si.getSeatInventoryId());

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
                    Passenger p = retrievePassengerByPassengerIdUnmanaged(passenger);
                    System.out.print("Please select a seat for passenger " + p.getFirstName() + " " + p.getLastName() + " > ");
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
                                    try
                                    {
                                        Long cabinSeatId = createNewCabinSeatInventory(seat, si.getSeatInventoryId(), passenger); 
                                        createSuccess = true;

                                    } 
                                    catch(CabinSeatInventoryExistException_Exception ex)
                                    {
                                        System.out.println(ex.getMessage() + "\n");
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
                } catch (PassengerNotFoundException_Exception ex)
                {
                    System.out.println(ex.getMessage() + "\n");
                }
            }
        }
        catch (SeatInventoryNotFoundException_Exception ex)
        {
            System.out.println("An error has occurred while trying to retrieve the seat inventory of cabin class!\n");
        }
    }
    
    private static void doViewPartnerFlightReservations()
    {
        
    }
    
    private static void doViewPartnerFlightReservationDetails()
    {
        
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

    private static java.util.List<ws.client.airport.Airport> getAllAirportsUnmanaged() {
        ws.client.airport.AirportWebService_Service service = new ws.client.airport.AirportWebService_Service();
        ws.client.airport.AirportWebService port = service.getAirportWebServicePort();
        return port.getAllAirportsUnmanaged();
    }

    private static java.util.List<ws.client.cabinClass.CabinClass> retrieveCabinClassesByAircraftConfigIdUnmanaged(java.lang.Long aircraftConfigId) {
        ws.client.cabinClass.CabinClassWebService_Service service = new ws.client.cabinClass.CabinClassWebService_Service();
        ws.client.cabinClass.CabinClassWebService port = service.getCabinClassWebServicePort();
        return port.retrieveCabinClassesByAircraftConfigIdUnmanaged(aircraftConfigId);
    }

    private static java.util.List<ws.client.fare.Fare> getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(java.lang.Long name, java.lang.Long cabinClassId) {
        ws.client.fare.FareWebService_Service service = new ws.client.fare.FareWebService_Service();
        ws.client.fare.FareWebService port = service.getFareWebServicePort();
        return port.getFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(name, cabinClassId);
    }

    private static SeatInventory retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(java.lang.Long cabinClassId, java.lang.Long flightScheduleId) throws SeatInventoryNotFoundException_Exception {
        ws.client.seatInventory.SeatInventoryWebService_Service service = new ws.client.seatInventory.SeatInventoryWebService_Service();
        ws.client.seatInventory.SeatInventoryWebService port = service.getSeatInventoryWebServicePort();
        return port.retrieveSeatInventoryByCabinClassIdAndFlightScheduleIdUnmanaged(cabinClassId, flightScheduleId);
    }

    private static BigDecimal getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(java.lang.Long flightSchedulePlanId, java.lang.Long cabinClassId) throws FareNotFoundException_Exception {
        ws.client.fare.FareWebService_Service service = new ws.client.fare.FareWebService_Service();
        ws.client.fare.FareWebService port = service.getFareWebServicePort();
        return port.getHighestFareByFlightSchedulePlanIdAndCabinClassIdUnmanaged(flightSchedulePlanId, cabinClassId);
    }

    private static Long createNewFlightReservationRecord(ws.client.flightReservationRecord.FlightReservationRecord flightReservationRecord, java.lang.Long personId, java.util.List<java.lang.Long> flightSchedules) {
        ws.client.flightReservationRecord.FlightReservationRecordWebService_Service service = new ws.client.flightReservationRecord.FlightReservationRecordWebService_Service();
        ws.client.flightReservationRecord.FlightReservationRecordWebService port = service.getFlightReservationRecordWebServicePort();
        return port.createNewFlightReservationRecord(flightReservationRecord, personId, flightSchedules);
    }

    private static FlightSchedule getFlightScheduleByIdUnmanaged(java.lang.Long flightScheduleId) throws FlightScheduleNotFoundException_Exception {
        ws.client.flightSchedule.FlightScheduleWebService_Service service = new ws.client.flightSchedule.FlightScheduleWebService_Service();
        ws.client.flightSchedule.FlightScheduleWebService port = service.getFlightScheduleWebServicePort();
        return port.getFlightScheduleByIdUnmanaged(flightScheduleId);
    }

    private static CabinClass retrieveCabinClassByIdUnmanaged(java.lang.Long cabinClassId) throws CabinClassNotFoundException_Exception {
        ws.client.cabinClass.CabinClassWebService_Service service = new ws.client.cabinClass.CabinClassWebService_Service();
        ws.client.cabinClass.CabinClassWebService port = service.getCabinClassWebServicePort();
        return port.retrieveCabinClassByIdUnmanaged(cabinClassId);
    }

    private static CabinClass retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(java.lang.Long aircraftConfigId, ws.client.cabinClass.CabinClassEnum type) throws CabinClassNotFoundException_Exception {
        ws.client.cabinClass.CabinClassWebService_Service service = new ws.client.cabinClass.CabinClassWebService_Service();
        ws.client.cabinClass.CabinClassWebService port = service.getCabinClassWebServicePort();
        return port.retrieveCabinClassByAircraftConfigIdAndTypeUnmanaged(aircraftConfigId, type);
    }

    private static Long createNewPassenger(ws.client.passenger.Passenger passenger, java.lang.Long flightReservationRecordId) {
        ws.client.passenger.PassengerWebService_Service service = new ws.client.passenger.PassengerWebService_Service();
        ws.client.passenger.PassengerWebService port = service.getPassengerWebServicePort();
        return port.createNewPassenger(passenger, flightReservationRecordId);
    }

    private static Passenger retrievePassengerByPassengerIdUnmanaged(java.lang.Long passengerId) throws PassengerNotFoundException_Exception {
        ws.client.passenger.PassengerWebService_Service service = new ws.client.passenger.PassengerWebService_Service();
        ws.client.passenger.PassengerWebService port = service.getPassengerWebServicePort();
        return port.retrievePassengerByPassengerIdUnmanaged(passengerId);
    }

    private static Long createNewCabinSeatInventory(ws.client.cabinSeatInventory.CabinSeatInventory cabinSeatInventory, java.lang.Long seatInventoryId, java.lang.Long passengerId) throws CabinSeatInventoryExistException_Exception {
        ws.client.cabinSeatInventory.CabinSeatInventoryWebService_Service service = new ws.client.cabinSeatInventory.CabinSeatInventoryWebService_Service();
        ws.client.cabinSeatInventory.CabinSeatInventoryWebService port = service.getCabinSeatInventoryWebServicePort();
        return port.createNewCabinSeatInventory(cabinSeatInventory, seatInventoryId, passengerId);
    }

    private static java.util.List<ws.client.cabinSeatInventory.CabinSeatInventory> retrieveCabinSeatInventoryInSeatInventoryUnmanaged(java.lang.Long seatInventoryId) {
        ws.client.cabinSeatInventory.CabinSeatInventoryWebService_Service service = new ws.client.cabinSeatInventory.CabinSeatInventoryWebService_Service();
        ws.client.cabinSeatInventory.CabinSeatInventoryWebService port = service.getCabinSeatInventoryWebServicePort();
        return port.retrieveCabinSeatInventoryInSeatInventoryUnmanaged(seatInventoryId);
    }

    private static java.util.List<ws.client.flightSchedule.FlightSchedule> searchDirectFlightSchedules(java.lang.Long departureAirportId, java.lang.Long destinationAirportId, javax.xml.datatype.XMLGregorianCalendar dateStart, javax.xml.datatype.XMLGregorianCalendar dateEnd, ws.client.flightSchedule.CabinClassEnum preferredCabinClass, java.lang.Integer numPassengers) {
        ws.client.flightSchedule.FlightScheduleWebService_Service service = new ws.client.flightSchedule.FlightScheduleWebService_Service();
        ws.client.flightSchedule.FlightScheduleWebService port = service.getFlightScheduleWebServicePort();
        return port.searchDirectFlightSchedules(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
    }

    private static java.util.List<ws.client.flightSchedule.FlightSchedule> searchDoubleTransitConnectingFlightSchedule(java.lang.Long departureAirportId, java.lang.Long destinationAirportId, javax.xml.datatype.XMLGregorianCalendar dateStart, javax.xml.datatype.XMLGregorianCalendar dateEnd, ws.client.flightSchedule.CabinClassEnum preferredCabinClass, java.lang.Integer numPassengers) {
        ws.client.flightSchedule.FlightScheduleWebService_Service service = new ws.client.flightSchedule.FlightScheduleWebService_Service();
        ws.client.flightSchedule.FlightScheduleWebService port = service.getFlightScheduleWebServicePort();
        return port.searchDoubleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
    }

    private static java.util.List<ws.client.flightSchedule.FlightSchedule> searchSingleTransitConnectingFlightSchedule(java.lang.Long departureAirportId, java.lang.Long destinationAirportId, javax.xml.datatype.XMLGregorianCalendar dateStart, javax.xml.datatype.XMLGregorianCalendar dateEnd, ws.client.flightSchedule.CabinClassEnum preferredCabinClass, java.lang.Integer numPassengers) {
        ws.client.flightSchedule.FlightScheduleWebService_Service service = new ws.client.flightSchedule.FlightScheduleWebService_Service();
        ws.client.flightSchedule.FlightScheduleWebService port = service.getFlightScheduleWebServicePort();
        return port.searchSingleTransitConnectingFlightSchedule(departureAirportId, destinationAirportId, dateStart, dateEnd, preferredCabinClass, numPassengers);
    }
}
