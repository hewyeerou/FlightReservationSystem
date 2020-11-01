/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftConfigSessionBeanRemote;
import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.AircraftConfig;
import entity.AircraftType;
import entity.Airport;
import entity.CabinClass;
import entity.Employee;
import entity.FlightRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import util.enumeration.CabinClassEnum;
import util.enumeration.UserRoleEnum;
import util.exception.AircraftConfigNameExistException;
import util.exception.AircraftConfigNotFoundException;
import util.exception.AirportNotFoundException;
import util.exception.FlightRouteExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.InvalidAccessRightsException;
import util.exception.InvalidSeatNumberInputException;
import util.exception.UnknownPersistenceException;

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
    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;

    private Employee currentEmployee;
    
    public FlightPlanningModule() {
    }
    
    public FlightPlanningModule(AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, AirportSessionBeanRemote airportSessionBeanRemote, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, Employee currentEmployee) 
    {
        this();
        this.aircraftTypeSessionBeanRemote = aircraftTypeSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.airportSessionBeanRemote = airportSessionBeanRemote;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.currentEmployee = currentEmployee;
    }
    
    
    public void menuFlightPlanning() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.FLEET_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.ROUTE_PLANNER && currentEmployee.getUserRoleEnum() !=  UserRoleEnum.SYSTEM_ADMIN)
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
                    try
                    {
                        doAircraftConfig();
                    }
                    catch(InvalidAccessRightsException ex)
                    {
                        System.out.println(ex.getMessage());
                        break;
                    }
                }
                else if(response == 2)
                {
                    try
                    {
                        doFlightRoute();
                    }
                    catch (InvalidAccessRightsException ex)
                    {
                        System.out.println(ex.getMessage());
                        break;
                    }
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
    
    private void doAircraftConfig() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.FLEET_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightsException("You don't have the rights to access aircraft configuration planning.");
        }
        
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
 
    private void doFlightRoute() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.ROUTE_PLANNER && currentEmployee.getUserRoleEnum() != UserRoleEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightsException("You don't have the rights to access flight route planning.");
        }
        
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
        Scanner scanner = new Scanner(System.in);
        AircraftConfig newAircraftConfig = new AircraftConfig();
        List<AircraftType> aircraftTypes = aircraftTypeSessionBeanRemote.retrieveAllAircraftTypes();
        Long aircraftTypeId;
        Integer aircraftTypeInt = 0;
        Integer maxSeatCapacityAircraft = 0;
        Integer numOfCabinClasses = 0;
        
        System.out.println("*** FRS Management :: Flight Planning :: Aircraft Configuration :: Create New Aircraft Configuration ***\n");
        
        while (true)
        {
            Integer option = 0;
            for (AircraftType aircraftType: aircraftTypes)
            {
                option++;
                System.out.println(option + ": " + aircraftType.getAircraftTypeName());
            }
            
            System.out.println("");
            System.out.print("Select Aircraft Type> ");
            aircraftTypeInt = scanner.nextInt();
            scanner.nextLine();
            
            if (aircraftTypeInt >= 1 && aircraftTypeInt <= option)
            {
                aircraftTypeId = aircraftTypes.get(aircraftTypeInt-1).getAircraftTypeId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.print("Enter Name> ");
        newAircraftConfig.setName(scanner.nextLine().trim());
        
        while (true)
        {
            System.out.print("Enter Number of Cabin Classes> ");
            numOfCabinClasses = scanner.nextInt();
            scanner.nextLine();
            
            if (numOfCabinClasses >= 1 && numOfCabinClasses <= 4)
            {
                newAircraftConfig.setNumOfCabinClasses(numOfCabinClasses);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        while (true)
        {
            List<CabinClass> cabinClasses = new ArrayList<>();
            
            System.out.println("\nConfiguring Cabin Classes:\n");
            maxSeatCapacityAircraft = 0;
            
            for (Integer count = 1; count <= numOfCabinClasses; count++)
            {
                System.out.println("Cabin Class " + count);
                CabinClass newCabinClass = doCreateCabinClass();
                maxSeatCapacityAircraft = maxSeatCapacityAircraft + newCabinClass.getMaxSeatCapacity();
                cabinClasses.add(newCabinClass);

            }
            
            if (maxSeatCapacityAircraft <= aircraftTypes.get(aircraftTypeInt-1).getMaxSeatCapacity())
            {
                newAircraftConfig.setCabinClasses(cabinClasses);
                newAircraftConfig.setMaxSeatCapacity(maxSeatCapacityAircraft);
                break;
            }
            else
            {
                System.out.println("Maximum seat capacity of aircraft type exceeded, please try again!\n");
            }
        }
        
        try
        {
            Long aircraftConfigId = aircraftConfigSessionBeanRemote.createNewAircraftConfig(newAircraftConfig, aircraftTypeId);
            System.out.println("New aircraft configuration created successfully!: " + aircraftConfigId + "\n");
        }
        catch (AircraftConfigNameExistException ex)
        {
            System.out.println("An error has occurred while creating the new aircraft configuration!: The aircraft configuration name already exists!\n");
        }
        catch (UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new aircraft configuraton!: " + ex.getMessage() + "\n");
        }
    }
    
    private CabinClass doCreateCabinClass()
    {
        Scanner scanner = new Scanner (System.in);
        CabinClass newCabinClass = new CabinClass();
            
        while (true)
        {
            System.out.print("Enter Cabin Class Type (1: First Class, 2: Business Class, 3: Premium Economy Class, 4: Economy Class)> ");
            Integer cabinClassTypeInt = scanner.nextInt();
            scanner.nextLine();

            if (cabinClassTypeInt == 1)
            {
                newCabinClass.setCabinClassType(CabinClassEnum.FIRST_CLASS);
                break;
            }
            else if (cabinClassTypeInt == 2)
            {
                newCabinClass.setCabinClassType(CabinClassEnum.BUSINESS_CLASS);
                break;
            }
            else if (cabinClassTypeInt == 3)
            {
                newCabinClass.setCabinClassType(CabinClassEnum.PREMIUM_ECONOMY_CLASS);
                break;
            }
            else if (cabinClassTypeInt == 4)
            {
                newCabinClass.setCabinClassType(CabinClassEnum.ECONOMY_CLASS);
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
            
        while (true)
        {
            System.out.print("Enter Number of Aisles (0-2) > ");
            Integer numOfAisles = scanner.nextInt();
            scanner.nextLine();

            if (numOfAisles >= 0 && numOfAisles <= 2)
            {
                newCabinClass.setNumOfAisle(numOfAisles);
                break;
            }
            else
            {
                System.out.println("Invalid input, please try again!\n");
            }
        }
            
        while (true)
        {
            System.out.print("Enter Number of Rows> ");
            Integer numOfRows = scanner.nextInt();
            scanner.nextLine();

            if (numOfRows >= 1)
            {
                newCabinClass.setNumOfRows(numOfRows);
                break;
            }
            else
            {
                System.out.println("Invalid input, please try again!\n");
            }
        }
            
        while (true)
        {
            System.out.print("Enter Number of Seats Abreast> ");
            Integer numOfSeatsAbreast = scanner.nextInt();
            scanner.nextLine();

            if (numOfSeatsAbreast >= 1 && numOfSeatsAbreast <= 10)
            {
                newCabinClass.setNumOfSeatsAbreast(numOfSeatsAbreast);
                break;
            }
            else
            {
                System.out.println("Invalid input, please try again!\n");
            }
        }
            
        while (true)
        {
            Integer pos;
            Integer aisleCount = 0;
            Integer seatsCountPerRow = 0;
            Boolean validInput = true;
            System.out.print("Enter Actual Seating Configuration Per Column (e.g 3-4-3)> ");
            String seatConfigPerColumnString = scanner.nextLine().trim();
            System.out.println("");
            String seatConfigPerColumn = seatConfigPerColumnString;

            while ((pos = seatConfigPerColumnString.indexOf("-")) != -1)
            {
                try
                {
                    Integer numOfSeatsInColumn = Integer.valueOf(seatConfigPerColumnString.substring(0, pos));
                    
                    if (numOfSeatsInColumn == 0)
                    {
                        throw new InvalidSeatNumberInputException();
                    }
                    
                    seatConfigPerColumnString = seatConfigPerColumnString.substring(pos + 1);
                    seatsCountPerRow = seatsCountPerRow + numOfSeatsInColumn;
                    aisleCount++;
                }
                catch (NumberFormatException ex)
                {
                    System.out.println("Invalid input format, please try again!\n");
                    validInput = false;
                    break;
                }
                catch (InvalidSeatNumberInputException ex)
                {
                    System.out.println("Seat number in each column must be more than zero, please try again!\n");
                    validInput = false;
                    break;
                }
            }
            
            Integer numOfSeatsInColumn = Integer.valueOf(seatConfigPerColumnString);
            seatsCountPerRow = seatsCountPerRow + numOfSeatsInColumn;

            if (validInput)
            {
                if (newCabinClass.getNumOfAisle().equals(aisleCount) && newCabinClass.getNumOfSeatsAbreast().equals(seatsCountPerRow))
                {
                    newCabinClass.setSeatConfigPerColumn(seatConfigPerColumn);
                    break;
                }
                else
                {
                    System.out.println("Invalid input, please try again!\n");
                }
            }
        }
        
        Integer maxSeatCapacityCabin = newCabinClass.getNumOfRows() * newCabinClass.getNumOfSeatsAbreast();
        newCabinClass.setMaxSeatCapacity(maxSeatCapacityCabin);
        
        return newCabinClass;
    }
    
    private void doViewAllAircraftConfig()
    {
        Scanner scanner = new Scanner (System.in);
        System.out.println("*** FRS Management :: Flight Planning :: Aircraft Configuration :: View All Aircraft Configurations ***\n");
        
        List<AircraftConfig> aircraftConfigs = aircraftConfigSessionBeanRemote.retrieveAllAircraftConfigs();
        System.out.printf("%20s%30s\n", "Aircraft Type", "Aircraft Configuration Name");
        
        for (AircraftConfig aircraftConfig: aircraftConfigs)
        {
            System.out.printf("%20s%30s\n", aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getName());
        }
        
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewAircraftConfigDetails()
    {
        Scanner scanner = new Scanner (System.in);
        
        System.out.println("*** FRS Management :: Flight Planning :: Aircraft Configuration :: View Aircraft Configuration Details ***\n");
        System.out.print("Enter Aircraft Configuration Name> ");
        String name = scanner.nextLine().trim();
        
        try
        {
            AircraftConfig aircraftConfig = aircraftConfigSessionBeanRemote.retrieveAircraftConfigByName(name);
            
            if (aircraftConfig.getFlight() != null)
            {
                System.out.printf("%20s%30s%20s%15s%25s\n", "Aircraft Type", "Aircraft Configuration Name", "Max. Seat Capacity", "Flight No.", "No. of Cabin Classes");
                System.out.printf("%20s%30s%20s%15s%25s\n", aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getName(), aircraftConfig.getMaxSeatCapacity(), aircraftConfig.getFlight().getFlightNumber(), aircraftConfig.getNumOfCabinClasses());
            }
            else
            {
                System.out.printf("%20s%30s%20s%15s%25s\n", "Aircraft Type", "Aircraft Configuration Name", "Max. Seat Capacity", "Flight No.", "No. of Cabin Classes");
                System.out.printf("%20s%30s%20s%15s%25s\n", aircraftConfig.getAircraftType().getAircraftTypeName(), aircraftConfig.getName(), aircraftConfig.getMaxSeatCapacity(), "null", aircraftConfig.getNumOfCabinClasses());
            }
            
            System.out.println("\nCabin Classes in " + name + ":\n");
            for (CabinClass cabinClass: aircraftConfig.getCabinClasses())
            {
                System.out.printf("%20s%15s%15s%25s%35s%20s\n", "Cabin Class Type", "No. of Aisles", "No. of Rows", "No. of Seats Abreast", "Seating Configuration per Column", "Max. Seat Capacity");
                System.out.printf("%20s%15s%15s%25s%35s%20s\n", cabinClass.getCabinClassType().toString(), cabinClass.getNumOfAisle(), cabinClass.getNumOfRows(), cabinClass.getNumOfSeatsAbreast(), cabinClass.getSeatConfigPerColumn(), cabinClass.getMaxSeatCapacity());
            }
            
            System.out.println("------------------------------------------");
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        }
        catch (AircraftConfigNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving aircraft configuration details: " + ex.getMessage() + "\n");
        }
    }
    
    private void doCreateFlightRoute()
    {
        Scanner scanner = new Scanner(System.in);
        
        List<Airport> airports = airportSessionBeanRemote.getAllAirports();
        FlightRoute newFlightRoute = new FlightRoute();
        FlightRoute newReturnFlightRoute = new FlightRoute();
        
        Integer originAirportInt = 0;
        Long originAirportId;
        Integer destinationAirportInt = 0;
        Long destinationAirportId;
        
        System.out.println("*** FRS Management :: Flight Planning :: Flight Route :: Create New Flight Route ***\n");

        while(true)
        {
            Integer option = 0;
            
            for(Airport airport: airports)
            {
                option++;
                System.out.println(option + ": " + airport.getIataCode());
            }
            
            System.out.println("");
            System.out.print("Select Origin Airport> ");
            originAirportInt = scanner.nextInt();
            scanner.nextLine();
            
            if(originAirportInt >= 1 && originAirportInt <= option)
            {
                originAirportId = airports.get(originAirportInt-1).getAirportId();
                System.out.println("You have selected " + airports.get(originAirportInt-1).getIataCode());
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
            destinationAirportInt = scanner.nextInt();
            scanner.nextLine();
            
            if(destinationAirportInt >= 1 && destinationAirportInt <= option)
            {
                destinationAirportId = airports.get(destinationAirportInt-1).getAirportId();
                System.out.println("You have selected " + airports.get(destinationAirportInt-1).getIataCode());
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!");
            }
        }
        
        try
        {
            Airport originAirport = airportSessionBeanRemote.getAirportByAirportId(originAirportId);
            Airport destinationAirport = airportSessionBeanRemote.getAirportByAirportId(destinationAirportId);

            newFlightRoute.setOrigin(originAirport);
            newFlightRoute.setDestination(destinationAirport);
            newFlightRoute.setFlightRouteType("OUTBOUND");
            newFlightRoute.setEnabled(true);

            System.out.println("Do you want to create a complementary return route?");
            
            
            while(true)
            {
                Integer option = 0;
                
                System.out.println("1. Yes");
                System.out.println("2. No");
                System.out.print("> ");
                option = scanner.nextInt();
                
                scanner.nextLine();
                if(option >=1 && option <= 2)
                {
                    if(option == 1)
                    {
                        Airport originReturnAirport = destinationAirport;
                        Airport destinationReturnAirport = originAirport;

                        newReturnFlightRoute.setOrigin(originReturnAirport);
                        newReturnFlightRoute.setDestination(destinationReturnAirport);
                        newReturnFlightRoute.setFlightRouteType("RETURN");
                        newReturnFlightRoute.setEnabled(true);
                        newFlightRoute.setReturnFlightRoute(newReturnFlightRoute);
                        
                        Long flightRouteId = flightRouteSessionBeanRemote.createNewFlightRoute(newFlightRoute, originAirportId, destinationAirportId);
                        Long returnFlightRouteId = flightRouteSessionBeanRemote.createNewReturnFlightRoute(newReturnFlightRoute, flightRouteId);
                        
                        System.out.println("New flight route created successfully!: " + flightRouteId + "\n");
                        System.out.println("Return flight route for " + flightRouteId +  " created successfully!: " + returnFlightRouteId + "\n"); 
                        break;
                    }
                    else if(option == 2)
                    {
                        Long flightRouteId = flightRouteSessionBeanRemote.createNewFlightRoute(newFlightRoute, originAirportId, destinationAirportId);
                        System.out.println("New flight route created successfully!: " + flightRouteId + "\n");
                        break;
                    }
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
 
            }
        }
        catch(AirportNotFoundException ex)
        {
            System.out.println("Airport does not exist!");
        }
        catch(FlightRouteExistException ex)
        {
            System.out.println("Duplicate flight route record: The flight route already exists!\n");
        }
        catch(UnknownPersistenceException ex)
        {
            System.out.println("An unknown error has occurred while creating the new flight route!: " + ex.getMessage() + "\n");
        }   
    }
    
    
    private void doViewAllFlightRoute()
    {
        Scanner scanner = new Scanner (System.in);
        System.out.println("*** FRS Management :: Flight Planning :: Flight Route :: View All Flight Routes ***\n");
        
        Integer option = 0;
        
        List<FlightRoute> flightRoutes = flightRouteSessionBeanRemote.getAllFlightRoute();
        List<FlightRoute> outboundFlightRoutes = new ArrayList<>();
        
        System.out.printf("%20s%30s%30s\n", "#" ,"Flight Route", "Route Type");
        
        for(FlightRoute flightRoute: flightRoutes)
        {
            if(flightRoute.getFlightRouteType().equals("OUTBOUND"))
            {
                outboundFlightRoutes.add(flightRoute);
            }
        }
        
        for(FlightRoute outboundFlightRoute: outboundFlightRoutes)
        {
            option++;
            System.out.printf("%20s%30s%30s\n", option, outboundFlightRoute.getOrigin().getIataCode() + " - " + outboundFlightRoute.getDestination().getIataCode() , outboundFlightRoute.getFlightRouteType());
            
            //if flight has return flight
            if(outboundFlightRoute.getReturnFlightRoute().getFlightRouteId() != outboundFlightRoute.getFlightRouteId())
            {
                option++;
                System.out.printf("%20s%30s%30s\n", option, outboundFlightRoute.getReturnFlightRoute().getOrigin().getIataCode() + " - " + outboundFlightRoute.getReturnFlightRoute().getDestination().getIataCode(), outboundFlightRoute.getReturnFlightRoute().getFlightRouteType());
            }       
        }
        
        System.out.println("------------------------------------------");
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doDeleteFlightRoute()
    {    
        try
        {
            Scanner scanner = new Scanner(System.in);
            
            List<FlightRoute> flightRoutes = flightRouteSessionBeanRemote.getAllFlightRoute();
            List<FlightRoute> outboundFlightRoutes = new ArrayList<>();
            
            Integer flightRouteInt = 0;
            Long flightRouteId = 0l;
           

            System.out.println("*** FRS Management :: Flight Planning :: Flight Route :: Delete Flight Route ***\n");

            for(FlightRoute flightRoute: flightRoutes)
            {
                if(flightRoute.getFlightRouteType().equals("OUTBOUND"))
                {
                    outboundFlightRoutes.add(flightRoute);
                }
            }

            while(true)
            {
                Integer option = 0;

                for(FlightRoute outboundFlightRoute: outboundFlightRoutes)
                {
                    option++;
                    System.out.println(option + ": " + outboundFlightRoute.getOrigin().getIataCode() + " - " + outboundFlightRoute.getDestination().getIataCode() + ", outbound flight"); 

                    //if flight has return flight
                    if(outboundFlightRoute.getReturnFlightRoute().getFlightRouteId() != outboundFlightRoute.getFlightRouteId())
                    {
                        option++;
                        System.out.println(option + ": " + outboundFlightRoute.getReturnFlightRoute().getOrigin().getIataCode() + " - " + outboundFlightRoute.getReturnFlightRoute().getDestination().getIataCode() + ", return flight");
                    }       
                }

                System.out.println("");
                System.out.print("Select a Flight Route to remove> ");
                flightRouteInt = scanner.nextInt();
                scanner.nextLine();

                if(flightRouteInt >=1 && flightRouteInt <= option)
                {
                    flightRouteId = outboundFlightRoutes.get(flightRouteInt-1).getFlightRouteId();
                    FlightRoute flightRoute = flightRouteSessionBeanRemote.getFlightRouteById(flightRouteId, false, false);
                
                    if(flightRoute.getFlights() == null)
                    {
                        flightRouteSessionBeanRemote.removeFlightRoute(flightRouteId);
                        System.out.println("Existing flight route removed successfully!\n");
                    }
                    else
                    {
                        flightRouteSessionBeanRemote.setFlightRouteDisabled(flightRouteId);
                        System.out.println("Existing flight route has been set to disabled!\n");
                    }
       
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again! \n");
                }
                
                
            }
        }
        catch(FlightRouteNotFoundException ex)
        {
            System.out.println("An error has occurred while removing flight route record: " + ex.getMessage() + "!\n");
        }
        

    }
    
}
