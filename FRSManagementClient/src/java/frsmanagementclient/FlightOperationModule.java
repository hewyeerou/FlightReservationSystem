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
import ejb.session.stateless.FareSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.AircraftConfig;
import entity.CabinClass;
import entity.Employee;
import entity.Fare;
import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.UserRoleEnum;
import util.exception.FareBasisCodeExistException;
import util.exception.FareNotFoundException;
import util.exception.FlightNotFoundException;
import util.exception.FlightNumExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InvalidAccessRightsException;
import util.exception.UnknownPersistenceException;
import util.exception.createOutboundReturnFlightCheckException;
import ejb.session.stateless.SeatInventorySessionBeanRemote;

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
    
    private FlightSessionBeanRemote flightSessionBeanRemote;
    private FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;
    private AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    private FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;
    private FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    private SeatInventorySessionBeanRemote seatinventorySessionBeanRemote;
    private FareSessionBeanRemote fareSessionBeanRemote;


    private Employee currentEmployee;
    
    public FlightOperationModule() 
    {
    }
    
    public FlightOperationModule(Employee currentEmployee, FlightSessionBeanRemote flightSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, SeatInventorySessionBeanRemote seatinventorySessionBeanRemote, FareSessionBeanRemote fareSessionBeanRemote)
    {
        this();
        this.currentEmployee  = currentEmployee;
        this.flightSessionBeanRemote = flightSessionBean;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.seatinventorySessionBeanRemote = seatinventorySessionBeanRemote;
        this.fareSessionBeanRemote = fareSessionBeanRemote;
    }

    public void menuFlightOperation() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.SCHEDULE_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.SYSTEM_ADMIN)
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
                System.out.print("> ");                
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
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 3)
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
            System.out.println("4: Back\n");
            response = 0;
            
            while(response < 1 || response > 4)
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
    
    private void doCreateFlight()
    {
        Scanner scanner = new Scanner(System.in);
        Flight newFlight = new Flight();
        Flight newReturnFlight = new Flight();
        
        List<FlightRoute> flightRoutes = flightRouteSessionBeanRemote.getAllFlightRoute();
        List<AircraftConfig> aircraftConfigs = aircraftConfigSessionBeanRemote.retrieveAllAircraftConfigs();
        
        Long aircraftConfigId;
        Long flightRouteId;
        Integer aircraftConfigInt = 0;
        Integer flightRouteInt = 0;
        
        List<FlightRoute> outboundFlightRoutes = new ArrayList<>();
       
        System.out.println("*** FRS Management :: Flight Operation :: Flight :: Create New Flight ***\n");
        
        System.out.print("Enter Flight Number (eg. ML001)> ");
        String flightNumber = scanner.nextLine().trim();
        
        //prompt user a list of aircraft config
        while(true)
        {
            Integer option = 0;
            
            System.out.println("List of aircraft configurations: ");
            for(AircraftConfig aircraftConfig: aircraftConfigs)
            {
                option++;
                System.out.println(option + ": " + aircraftConfig.getName());
            }
            
            System.out.println("");
            System.out.print("Select Aicraft Configuration> ");
            aircraftConfigInt = scanner.nextInt();
            scanner.nextLine();
            
            if(aircraftConfigInt >= 1 && aircraftConfigInt <= option)
            {
                aircraftConfigId = aircraftConfigs.get(aircraftConfigInt-1).getAircraftConfigId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }  
        }           
        
        //prompt user a list of flight routes
        while(true)
        {
            Integer option = 0;
            
            System.out.println("List of main flight routes: ");
            
            for(FlightRoute flightRoute: flightRoutes)
            {
                if(flightRoute.getFlightRouteType().equals("OUTBOUND") && flightRoute.getEnabled() == true)
                {
                    outboundFlightRoutes.add(flightRoute);
                }
            }

            for(FlightRoute flightRoute: outboundFlightRoutes)
            {
                option++;
                System.out.println(option + ": " + flightRoute.getOrigin().getIataCode() + " - " + flightRoute.getDestination().getIataCode());
            }
            
            System.out.println("");
            System.out.print("Select Flight Route> ");
            flightRouteInt = scanner.nextInt();
            scanner.nextLine();
            
            if(flightRouteInt >= 1 && flightRouteInt <= option)
            {
                flightRouteId = outboundFlightRoutes.get(flightRouteInt-1).getFlightRouteId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        try
        {
            if(flightNumber.substring(0,2).equals("ML"))
            {
                FlightRoute flightRoute = flightRouteSessionBeanRemote.getFlightRouteById(flightRouteId, true, false);
                
                if(flightRoute.getFlightRouteType().equals("RETURN") && flightRoute.getReturnFlightRoute().getFlightRouteId() == flightRoute.getFlightRouteId())
                {
                   newReturnFlight.setFlightNumber(flightNumber);
                   newReturnFlight.setEnabled(true);
                   newReturnFlight.setFlightType("RETURN");
                   
                   Long flightId = flightSessionBeanRemote.createFlight(newReturnFlight, flightRouteId, aircraftConfigId);
                                  
                   System.out.println("New flight created successfully!: " + flightId + "\n");
                }
                else if(flightRoute.getFlightRouteType().equals("OUTBOUND") && flightRoute.getReturnFlightRoute().getFlightRouteId() != flightRoute.getFlightRouteId())         //this flight route has complimentary flight route 
                {
                    newFlight.setFlightNumber(flightNumber);
                    newFlight.setEnabled(true);
                    newFlight.setFlightType("OUTBOUND");
                    
                    System.out.println("Do you want to create a complementary return flight?");
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
                                System.out.print("Enter Return Flight Number> ");
                                String returnFlightNumber = scanner.nextLine().trim();

                                
                                if(returnFlightNumber.substring(0,2).equals("ML"))
                                {
                                    newReturnFlight.setFlightNumber(returnFlightNumber);
                                    newReturnFlight.setEnabled(true);
                                    newReturnFlight.setFlightType("RETURN");
                                    
                                    //create both outbound and return in the same transaction
                                    Long flightId = flightSessionBeanRemote.createOutboundReturnFlightCheck(newFlight, flightRouteId, aircraftConfigId, newReturnFlight);
                                  
                                    System.out.println("New flight created successfully!: " + flightId + "\n");
                                    
                                    Flight flight = flightSessionBeanRemote.getFlightById(flightId);
                                    
                                    System.out.println("New return flight created successfully!: " + flight.getReturnFlight().getFlightId() + "\n");
                                    break;
                                }
                                else
                                {
                                    System.out.println("Flight number should start with ML");
                                }

                            }
                            else if(option == 2)
                            {
                                Long flightId = flightSessionBeanRemote.createFlight(newFlight, flightRouteId, aircraftConfigId);
                                System.out.println("New flight created successfully!: " + flightId + "\n");
                                break;
                            }
                        }
                    }
                }
                else
                {
                    newFlight.setFlightNumber(flightNumber);
                    newFlight.setEnabled(true);
                    newFlight.setFlightType("OUTBOUND");
                    
                    Long flightId = flightSessionBeanRemote.createFlight(newFlight, flightRouteId, aircraftConfigId);
                                  
                    System.out.println("New flight created successfully!: " + flightId + "\n");
                }
            }
            else
            {
                System.out.println("Flight number should start with ML");
            }
                    
        }
        catch (FlightRouteNotFoundException ex) 
        {
            System.out.println("Flight Route does not exist!");
        }
        catch(FlightNumExistException | createOutboundReturnFlightCheckException ex)
        {
            System.out.println("Duplicate flight record: The flight already exists!\n");
        } 
        catch (UnknownPersistenceException ex) 
        {
            System.out.println("An unknown error has occurred while creating the new flight!: " + ex.getMessage() + "\n");
        } 
        catch (FlightNotFoundException ex) 
        {
            System.out.println("Flight does not exist!\n");
        } 
       
    }
  
    private void doViewAllFlights()
    {
        Scanner scanner = new Scanner (System.in);
        System.out.println("*** FRS Management :: Flight Operation :: Flight :: View All Flights ***\n");
        
        Integer option = 0;
        List<Flight> flights = flightSessionBeanRemote.getAllFlights();
        List<Flight> outboundFlights = new ArrayList<>();
        
        System.out.printf("%20s%30s%30s\n", "#" ,"Flight Number", "Flight Route");
        
        for(Flight flight: flights)
        {
            if(flight.getFlightType().equals("OUTBOUND"))                       //outbound flight
            {
                outboundFlights.add(flight);
                
                if(flight.getReturnFlight().getFlightId() != flight.getFlightId()) //outbound flight that has return flight 
                {
                    outboundFlights.add(flight.getReturnFlight());
                }
            }
//            else if(flight.getFlightType().equals("RETURN"))                    //return flight that does not have outbound flight associated to it
//            {
//                if(!outboundFlights.contains(flight))
//                {
//                    outboundFlights.add(flight);
//                }
//            }
        }
        
        for(Flight outboundFlight: outboundFlights)
        {
            option++;
            System.out.printf("%20s%30s%30s\n", option, outboundFlight.getFlightNumber(), outboundFlight.getFlightRoute().getOrigin().getIataCode() + " - " + outboundFlight.getFlightRoute().getDestination().getIataCode());
        }
        
        System.out.println("------------------------------------------");
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void doViewFlightDetails()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** FRS Management :: Flight Operation :: Flight :: View Flight Details ***\n");
        System.out.print("Enter Flight Number> ");
        String flightNum = scanner.nextLine().trim();
        
        Integer response = 0;
        
        try
        {
            Flight flight = flightSessionBeanRemote.getFlightByFlightNum(flightNum);
            
            System.out.printf("%40s%40s\n", "Flight Number", "Flight Route");
            
            System.out.printf("%40s%40s\n", flight.getFlightNumber(), flight.getFlightRoute().getOrigin().getIataCode() + " - " + flight.getFlightRoute().getDestination().getIataCode());
            
            System.out.println("\n" + flight.getAircraftConfig().getNumOfCabinClasses() + " Available Cabin Classes in " + flightNum + ":\n");
            for (CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
            {
                System.out.printf("%40s%40s\n", "Cabin Class", "Max. Seat Capacity");
                System.out.printf("%40s%40s\n", cabinClass.getCabinClassType().toString(), cabinClass.getMaxSeatCapacity());
            }
            
            System.out.println("------------------------------------------");
            
            System.out.println("1: Update Flight");
            System.out.println("2: Delete Flight");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                doUpdateFlight(flight);
            }
            else if(response == 2)
            {
                doDeleteFlight(flight);
            }
        }
        catch(FlightNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving flight details: " + ex.getMessage() + "\n");
        }  
    }
    
    private void doUpdateFlight(Flight flight)
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** FRS Management :: Flight Operation :: Flight :: Update Flight ***\n");
            
            List<FlightRoute> flightRoutes = flightRouteSessionBeanRemote.getAllFlightRoute();
            List<AircraftConfig> aircraftConfigs = aircraftConfigSessionBeanRemote.retrieveAllAircraftConfigs();
            
            Long flightRouteId;
            Long aircraftConfigId;

            while(true)
            {
                Integer option = 0;

                System.out.println("Select the option you want to update: ");
                System.out.println("1: Flight Route ");
                System.out.println("2: Aircraft Configuration ");
                System.out.println("3: Back\n ");
                
                System.out.print("> ");
                option = scanner.nextInt();
                scanner.nextLine();

                if(option >=1 && option <= 2)
                {
                    if(option == 1)
                    {
                        System.out.println("Current Flight Route: " + flight.getFlightRoute().getOrigin().getIataCode() + " - " + flight.getFlightRoute().getDestination().getIataCode() + "\n");
                        flightRouteId = updateFlightRoute(flightRoutes);
                        FlightRoute flightRoute = flightRouteSessionBeanRemote.getFlightRouteById(flightRouteId, true, true);
                        flight.setFlightRoute(flightRoute);
                        flightSessionBeanRemote.updateFlight(flight);
                        
                        System.out.println("Flight updated successfully! \n");
                        break;
                    }
                    else if(option == 2)
                    {
                        System.out.println("Current Aircraft Configuration: " + flight.getAircraftConfig()+ "\n");
                        
                        aircraftConfigId = updateAircraftConfig(aircraftConfigs);
                        AircraftConfig aircraftConfig = aircraftConfigSessionBeanRemote.retrieveAircraftConfigById(aircraftConfigId);
                        flight.setAircraftConfig(aircraftConfig);
                        flightSessionBeanRemote.updateFlight(flight);
                        System.out.println("Flight updated successfully! \n");
                        break;
                    }
                    else if(option == 3)
                    {
                        break;        
                    }
                    else
                    {
                        System.out.println("Invalid option, please try again!\n");
                    }   
                } 
                if(option == 3)
                {
                    break;
                }
            }  
        }
        catch(FlightNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving flight details: " + ex.getMessage() + "\n");
        } 
        catch (FlightRouteNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving flight route details: " + ex.getMessage() + "\n");
        }
    }
    
    private Long updateFlightRoute(List<FlightRoute> flightRoutes)
    {
        Scanner scanner = new Scanner(System.in);
        Integer flightRouteInt = 0;
        Long flightRouteId;
        
        while(true)
        {
            Integer numbering = 0;

            for(FlightRoute flightRoute: flightRoutes)
            {
                numbering++;
                System.out.println(numbering + ": " + flightRoute.getOrigin().getIataCode() + " - " + flightRoute.getDestination().getIataCode() + ", " + flightRoute.getFlightRouteType());
            }

            System.out.println("");
            System.out.print("Select Flight Route> ");
            flightRouteInt = scanner.nextInt();
            scanner.nextLine();

            if(flightRouteInt >= 1 && flightRouteInt <= numbering)
            {
                flightRouteId = flightRoutes.get(flightRouteInt-1).getFlightRouteId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        return flightRouteId;
    }
    
    private Long updateAircraftConfig(List<AircraftConfig> aircraftConfigs)
    {
        Scanner scanner = new Scanner(System.in);
        Integer aircraftConfigInt = 0;
        Long aircraftConfigId;
        
        while(true)
        {
            Integer numbering = 0;
            for(AircraftConfig aircraftConfig: aircraftConfigs)
            {
                numbering++;
                System.out.println(numbering + ": " + aircraftConfig.getName());
            }

            System.out.println("");
            System.out.print("Select Aicraft Configuration> ");
            aircraftConfigInt = scanner.nextInt();
            scanner.nextLine();

            if(aircraftConfigInt >= 1 && aircraftConfigInt <= numbering)
            {
                aircraftConfigId = aircraftConfigs.get(aircraftConfigInt-1).getAircraftConfigId();
                break;
            }
            else
            {
                System.out.println("Invalid option, please try again!\n");
            }  
        }
        
        return aircraftConfigId;
    }
    
    private void doDeleteFlight(Flight flight)
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            String input;

            System.out.println("*** FRS Management :: Flight Operation :: Flight :: Delete Flight ***\n");
            System.out.printf("Confirm Delete Flight %s (Flight ID: %d) (Enter 'Y' to Delete)> ", flight.getFlightNumber(), flight.getFlightId());
            input = scanner.nextLine().trim();

            if (input.equals("Y")) 
            {
                if(flight.getFlightSchedulePlans().isEmpty())
                {
   
                    flightSessionBeanRemote.removeFlight(flight.getFlightId());
                    System.out.println("Flight deleted successfully!\n");
                    
                }
                else if(!flight.getFlightSchedulePlans().isEmpty())
                {
                    flightSessionBeanRemote.setFlightDisabled(flight.getFlightId());
                    System.out.println("Existing flight has been set to disabled!\n");
                }
            }
            else
            {
                System.out.println("Flight NOT deleted!\n");
            }
        } 
        catch (FlightNotFoundException ex) 
        {
            System.out.println("An error has occurred while removing flight record: " + ex.getMessage() + "!\n");
        }
        
    }
    
    private void doCreateFlightSchedulePlan()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);

            FlightSchedulePlan flightSchedulePlan = new FlightSchedulePlan();

            System.out.println("*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Create Flight Schedule Plan ***\n");

            System.out.print("Enter Flight Number(eg. ML001)> ");
            String flightNum = scanner.nextLine();
            
            Flight flight = flightSessionBeanRemote.getFlightByFlightNum(flightNum);

            Long flightSchedulePlanId = 0l;
            Long flightScheduleId = 0l;

            if(flightNum.substring(0,2).equals("ML"))
            {
                while(true)
                {
                    Integer option = 0;

                    System.out.println("Please choose schedule type: ");
                    System.out.println("1: Single schedule");
                    System.out.println("2: Multiple schedule");
                    System.out.println("3: Recurrent schedule");
                    System.out.print("> ");

                    option = scanner.nextInt();
                    scanner.nextLine();

                    if(option == 1)
                    {
                        while(true)
                        {
                            try
                            {
                                //create single flight schedule plan (MAIN)
                                flightSchedulePlan.setFlightScheduleType("SINGLE");
                                flightSchedulePlan.setIntervalDays(0);
                                flightSchedulePlan.setEndDate(null);
                                flightSchedulePlan.setFlightSchedulePlanType("OUTBOUND");
                                flightSchedulePlan.setStartDate(null);
                                flightSchedulePlan.setEnabled(true);

                                flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);
                                System.out.println("Flight Schedule Plan created successfully: " + flightSchedulePlanId);

                                //create fare for each cabin class that flight has 
                                Fare newFare = new Fare();
                                List<Fare> createFareList = new ArrayList<>();

                                //create fare for each flight schedule plan
                                for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
                                {
                                    while(true)
                                    {
                                        try
                                        {
                                            System.out.println("\nFares for Flight Schedule Plan " + flightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                            System.out.print("Enter Fare Basis Code> ");
                                            String fareBasisCode = scanner.nextLine().trim();
                                            System.out.print("Enter Fare Amount> ");
                                            BigDecimal fareAmount = scanner.nextBigDecimal();
                                            scanner.nextLine();

                                            newFare.setFareBasisCode(fareBasisCode);
                                            newFare.setFareAmount(fareAmount);

                                            createFareList.add(newFare);
                                            Long fareId = fareSessionBeanRemote.createNewFare(newFare, flightSchedulePlanId, cabinClass.getCabinClassId());  
                                            break;
                                        }
                                        catch (FareBasisCodeExistException ex)
                                        {
                                            System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                        } 
                                    }

                                }


                                System.out.println("\n*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Create Single Schedule ***\n");
                                flightScheduleId = doCreateEveryFlightSchedules(flightSchedulePlanId, flight, "SINGLE");  

                                List<FlightSchedule> flightSchedules = new ArrayList<>();
                                FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();
                                Long returnFlightSchedulePlanId;

                                //if flight has return flight
                                if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) 
                                {
                                    System.out.println("Do you want to create a complementary return flight schedule plan?");
                                    while (true) 
                                    {
                                        Integer choice = 0;

                                        System.out.println("1. Yes");
                                        System.out.println("2. No");
                                        System.out.print("> ");
                                        choice = scanner.nextInt();
                                        scanner.nextLine();


                                        if (choice == 1) 
                                        {
                                            try
                                            {
                                                System.out.print("Enter Layover Duration(hrs)> ");
                                                Double layoverDuration = scanner.nextDouble();
                                                scanner.nextLine();

                                                flightSchedulePlanForReturnFlight.setFlightScheduleType("SINGLE");
                                                flightSchedulePlanForReturnFlight.setEndDate(null);
                                                flightSchedulePlanForReturnFlight.setIntervalDays(0);
                                                flightSchedulePlanForReturnFlight.setFlightSchedulePlanType("RETURN");
                                                flightSchedulePlanForReturnFlight.setStartDate(null);
                                                flightSchedulePlanForReturnFlight.setEnabled(true);

                                                returnFlightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewReturnFlightSchedulePlan(flightSchedulePlanForReturnFlight, flightSchedulePlanId);
                                                
                                               
                                                //create fare for flight schedule plan
                                                for (CabinClass cabinClass : flight.getReturnFlight().getAircraftConfig().getCabinClasses()) 
                                                {
                                                    while (true) 
                                                    {
                                                        try 
                                                        {
                                                            System.out.println("\nFares for Return Flight Schedule Plan " + returnFlightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                                            System.out.print("Enter Fare Basis Code> ");
                                                            String fareBasisCode = scanner.nextLine().trim();
                                                            System.out.print("Enter Fare Amount> ");
                                                            BigDecimal fareAmount = scanner.nextBigDecimal();
                                                            scanner.nextLine();

                                                            newFare.setFareBasisCode(fareBasisCode);
                                                            newFare.setFareAmount(fareAmount);

                                                            Long fareId = fareSessionBeanRemote.createNewFare(newFare, returnFlightSchedulePlanId, cabinClass.getCabinClassId());
                                                            break;
                                                        } 
                                                        catch (FareBasisCodeExistException ex) 
                                                        {
                                                            System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                                        }

                                                    }

                                                }

                                                FlightSchedule flightSchedule = doCreateEveryReturnFlightSchedules(flightScheduleId, flight, layoverDuration, "MULTIPLE");
                                                
                                                
                                                Long returnFlightScheduleId = flightScheduleSessionBeanRemote.createNewReturnFlightSchedule(flightSchedule, flightScheduleId, returnFlightSchedulePlanId);
                                                System.out.println("Single flight schedule created successfully!\n");

                                                //create seat inventory 
                                                SeatInventory seatInventory = new SeatInventory();
                                                Long seatInventoryId = 0l;
                                                //get max seat capacity from the flight's cabin classes
                                                for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
                                                {
                                                    Integer totalMaxSeatCapacityForEachCabinClass = cabinClass.getMaxSeatCapacity();
                                                    seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityForEachCabinClass);
                                                    seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityForEachCabinClass);
                                                    seatInventory.setNumOfReservedSeats(0);

                                                    seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory, returnFlightScheduleId, cabinClass.getCabinClassId());
                                                }
                                                break;
                                            }
                                            catch (FlightSchedulePlanNotFoundException ex) 
                                            {
                                               System.out.println("An error has occurred while retrieving flight schedule plan: " + ex.getMessage() + "\n");
                                            } 
                                            catch (FlightScheduleNotFoundException ex) 
                                            {
                                                System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
                                            }
                                        }
                                        else if(choice == 2)
                                        {
                                            break;
                                        }
                                        else 
                                        {
                                            System.out.println("Invalid option, please try again!\n");
                                        }
                                    }
                                } 
                                break;
                            } 
                            catch (FlightSchedulePlanNotFoundException ex) 
                            {
                                System.out.println("An error has occurred while retrieving flight schedule: The flight schedule plan does not exist! \n");
                            } 
                            catch (UnknownPersistenceException ex) 
                            {
                                System.out.println("An error has occurred while: " + ex.getMessage() + "\n");
                            }
                        }
                        break;
                    }
                    else if(option == 2)
                    {
                        flightSchedulePlan.setFlightScheduleType("MULTIPLE");       
                        flightSchedulePlan.setIntervalDays(0);
                        flightSchedulePlan.setEndDate(null);
                        flightSchedulePlan.setFlightSchedulePlanType("OUTBOUND");
                        flightSchedulePlan.setStartDate(null);
                        flightSchedulePlan.setEnabled(true);
                        
                        //create main flight schedule plan
                        flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);
                        System.out.println("Flight Schedule Plan created successfully!: " + flightSchedulePlanId);

                        Fare newFare = new Fare();
                        //create fare for each flightscheduleplan
                        
                        for (CabinClass cabinClass : flight.getAircraftConfig().getCabinClasses()) 
                        {
                            while (true) 
                            {
                                try 
                                {
                                    System.out.println("\nFares for Flight Schedule Plan " + flightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                    System.out.print("Enter Fare Basis Code> ");
                                    String fareBasisCode = scanner.nextLine().trim();
                                    System.out.print("Enter Fare Amount> ");
                                    BigDecimal fareAmount = scanner.nextBigDecimal();
                                    scanner.nextLine();

                                    newFare.setFareBasisCode(fareBasisCode);
                                    newFare.setFareAmount(fareAmount);

                                    Long fareId = fareSessionBeanRemote.createNewFare(newFare, flightSchedulePlanId, cabinClass.getCabinClassId());
                                    break;
                                } 
                                catch (FareBasisCodeExistException ex) 
                                {
                                    System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                }
                            }
                        }
                        
                        String command = "";

                        List<FlightSchedule> flightSchedules = new ArrayList<>();
                        List<Long> flightScheduleIdList = new ArrayList<>();
                        List<Fare> fares = new ArrayList<>();
                        FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();
                        Long returnFlightSchedulePlanId = 0l;

                        do
                        {
                            flightScheduleId = doCreateEveryFlightSchedules(flightSchedulePlanId, flight, "MULTIPLE");
                            flightScheduleIdList.add(flightScheduleId);

                            if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) 
                            {
                                System.out.println("Do you want to create a complementary return flight schedule plan?");
                                while (true) 
                                {
                                    Integer choice = 0;

                                    System.out.println("1. Yes");
                                    System.out.println("2. No");
                                    System.out.print("> ");
                                    choice = scanner.nextInt();
                                    scanner.nextLine();

                                    if (choice == 1) 
                                    {
                                        System.out.print("Enter Layover Duration(hrs)> ");
                                        Double layoverDuration = scanner.nextDouble();
                                        scanner.nextLine();

                                        flightSchedulePlanForReturnFlight.setFlightScheduleType("MULTIPLE");
                                        flightSchedulePlanForReturnFlight.setEndDate(null);
                                        flightSchedulePlanForReturnFlight.setIntervalDays(0);
                                        flightSchedulePlanForReturnFlight.setFlightSchedulePlanType("RETURN");
                                        flightSchedulePlanForReturnFlight.setStartDate(null);
                                        flightSchedulePlanForReturnFlight.setEnabled(true);

                                        flightSchedules.add(doCreateEveryReturnFlightSchedules(flightScheduleId, flight, layoverDuration, "MULTIPLE"));
                                        System.out.println("Flight schedule created successfully!\n");

                                        break;
                                    }
                                    else if(choice == 2)
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        System.out.println("Invalid option, please try again! \n");
                                    } 
                                }
                            }
                            System.out.print("More? (Enter 'Q' to complete adding flight schedules)>");
                            command = scanner.nextLine().trim();
                        } while(!command.equals("Q"));


                        if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) 
                        {
                            returnFlightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewReturnFlightSchedulePlan(flightSchedulePlanForReturnFlight, flightSchedulePlanId);
                            //create fare for each flightscheduleplan
                            
                           for (CabinClass cabinClass : flight.getReturnFlight().getAircraftConfig().getCabinClasses()) 
                           {
                                while (true) 
                                {
                                    try 
                                    {
                                        System.out.println("\nFares for Return Flight Schedule Plan " + returnFlightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                        System.out.print("Enter Fare Basis Code> ");
                                        String fareBasisCode = scanner.nextLine().trim();
                                        System.out.print("Enter Fare Amount> ");
                                        BigDecimal fareAmount = scanner.nextBigDecimal();
                                        scanner.nextLine();

                                        newFare.setFareBasisCode(fareBasisCode);
                                        newFare.setFareAmount(fareAmount);
                                        Long fareId = fareSessionBeanRemote.createNewFare(newFare, returnFlightSchedulePlanId, cabinClass.getCabinClassId());
                                        break;
                                    } 
                                    catch (FareBasisCodeExistException ex) 
                                    {
                                        System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                    }
                                }
                            }
                        
                        
                            for(int i = 0; i < flightScheduleIdList.size(); i++)
                            {
                                for(int j = i; j < i + 1; j++)
                                {
                                    try
                                    {
                                        Long returnFlightScheduleId = flightScheduleSessionBeanRemote.createNewReturnFlightSchedule(flightSchedules.get(j), flightScheduleIdList.get(j), returnFlightSchedulePlanId);

                                        //create seat inventory 
                                        SeatInventory seatInventory = new SeatInventory();
                                        Long seatInventoryId = 0l;
                                        //get max seat capacity from the flight's cabin classes
                                        for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
                                        {
                                            Integer totalMaxSeatCapacityForEachCabinClass = cabinClass.getMaxSeatCapacity();
                                            seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityForEachCabinClass);
                                            seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityForEachCabinClass);
                                            seatInventory.setNumOfReservedSeats(0);

                                            seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory, returnFlightScheduleId, cabinClass.getCabinClassId());
                                        } 
                                        break;
                                    } 
                                    catch (FlightSchedulePlanNotFoundException ex) 
                                    {
                                       System.out.println("An error has occurred while retrieving flight schedule plan: " + ex.getMessage() + "\n");
                                    } 
                                    catch (FlightScheduleNotFoundException ex) 
                                    {
                                        System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
                                    }
                                }
                            }
                        }
                        break;
                    } 
                    else if (option == 3) 
                    {

                        while(true)
                        {
                            Integer choice = 0;

                            System.out.println("Select Recurrent Interval Type: ");
                            System.out.println("1: Recurrent Every n Days");
                            System.out.println("2: Recurrent Weekly");
                            System.out.print("> ");
                            choice = scanner.nextInt();
                            scanner.nextLine();

                            
                            if(choice == 1)
                            {
                                while(true)
                                {
                                    try
                                    {
                                        System.out.println("\n*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Create Recurrent Schedule ***\n");

                                        flightSchedulePlan.setFlightScheduleType("RECURRENT_DAY");
                                        Date formattedEndDate;
                                        Date formattedStartDate;
                                        Integer dayInterval;
                                        String startDate;
                                        String startTime;
                                        Double flightDuration;
                                        
                                        List<Boolean> isOverlapped = new ArrayList<>();

                                        while(true)
                                        {
                                            System.out.print("Enter Start(Departure) Date (dd-mm-yyyy)> ");
                                            startDate = scanner.nextLine();
                                            System.out.print("Enter Start(Departure) Time (HH:mm)> ");
                                            startTime = scanner.nextLine();
                                            System.out.print("Enter Flight Duration(hrs)> ");
                                            flightDuration = scanner.nextDouble();
                                            scanner.nextLine();
                                            System.out.print("Enter day interval> ");
                                            dayInterval = scanner.nextInt();
                                            scanner.nextLine();
                                            System.out.print("Enter End Date(dd-mm-yyyy)> ");
                                            String endDate = scanner.nextLine();

                                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                            formattedEndDate = formatter.parse(endDate);
                                            formattedStartDate = formatter.parse(startDate);

                                            SimpleDateFormat formatterDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                            String dateTime = startDate + " " + startTime;
                                            Date formattedDateTime = formatterDateTime.parse(dateTime);


                                            List<FlightSchedulePlan> fList = new ArrayList<>();

                                            for(FlightSchedulePlan flightSchedulePlan1: flight.getFlightSchedulePlans())
                                            {
                                                fList.add(flightSchedulePlan1);
                                            }

                                            if(fList.size() == 0)
                                            {
                                                flightSchedulePlan.setIntervalDays(dayInterval);
                                                flightSchedulePlan.setEndDate(formattedEndDate);
                                                flightSchedulePlan.setFlightSchedulePlanType("OUTBOUND");
                                                flightSchedulePlan.setStartDate(formattedDateTime);
                                                flightSchedulePlan.setEnabled(true);

                                                //create flight schedule plan
                                                flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);    
                                                break;
                                            }
                                            else
                                            {
                                                for(FlightSchedulePlan fsp: fList)
                                                {
                                                    GregorianCalendar calendar = new GregorianCalendar();
                                                    calendar.setTime(fsp.getStartDate());
                                                    
                        
                                                    if(fsp.getStartDate().compareTo(formattedDateTime) * formattedDateTime.compareTo(fsp.getEndDate()) >= 0 || fsp.getStartDate().compareTo(formattedEndDate) * formattedEndDate.compareTo(fsp.getEndDate()) >= 0)
                                                    {
                                                        isOverlapped.add(true);
                                                    }
                                                    else
                                                    {
                                                        isOverlapped.add(false);
                                                    }
                                                }
                                            }
                                            
                                            if(isOverlapped.contains(true))
                                            {
                                                System.out.println("An error has occurred while creating flight schedule: flight schedule overlaps with other flight schedule!\n");
                                                isOverlapped.removeAll(isOverlapped);
                                            }
                                            else
                                            {
                                                flightSchedulePlan.setIntervalDays(dayInterval);
                                                flightSchedulePlan.setEndDate(formattedEndDate);
                                                flightSchedulePlan.setFlightSchedulePlanType("OUTBOUND");
                                                flightSchedulePlan.setStartDate(formattedDateTime);
                                                flightSchedulePlan.setEnabled(true);

                                                //create flight schedule plan
                                                flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);    
                                                break;
                                            } 
                                        }
                                        
                                        Fare newFare = new Fare();

                                        //create fare for each flightscheduleplan
                                        for (CabinClass cabinClass : flight.getAircraftConfig().getCabinClasses()) 
                                        {
                                            while (true) 
                                            {
                                                try 
                                                {
                                                    System.out.println("\nFares for Flight Schedule Plan " + flightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                                    System.out.print("Enter Fare Basis Code> ");
                                                    String fareBasisCode = scanner.nextLine().trim();
                                                    System.out.print("Enter Fare Amount> ");
                                                    BigDecimal fareAmount = scanner.nextBigDecimal();
                                                    scanner.nextLine();

                                                    newFare.setFareBasisCode(fareBasisCode);
                                                    newFare.setFareAmount(fareAmount);
                                                    Long fareId = fareSessionBeanRemote.createNewFare(newFare, flightSchedulePlanId, cabinClass.getCabinClassId());
                                                    break;
                                                } 
                                                catch (FareBasisCodeExistException ex) 
                                                {
                                                    System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                                }
                                            }                      
                                        }

                                        Long difference_In_Time = formattedEndDate.getTime() - formattedStartDate.getTime();
                                        Long days = TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365;
                                        Integer numOfTimes = days.intValue()/dayInterval;

                                        int interval = 0;

                                        //create recurrent schedules for the plan
                                        for(int i = 0; i < numOfTimes; i++)
                                        {
                                            createRecurrentFlightSchedule(flightSchedulePlanId, flight, startDate,  startTime, flightDuration, interval);
                                            interval += dayInterval;
                                        }

                                        System.out.println("Recurrent Flight Schedules created successfully!\n");

                                        //create recurrent schedules plan
                                        if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) 
                                        {
                                            System.out.println("Do you want to create a complementary return flight schedule plan for recurrent flight schedules?");
                                            while (true) 
                                            {
                                                Integer response = 0;

                                                System.out.println("1. Yes");
                                                System.out.println("2. No");
                                                System.out.print("> ");
                                                response = scanner.nextInt();
                                                scanner.nextLine();

                                                if (response == 1) 
                                                {
                                                    System.out.print("Enter Layover Duration(hrs)> ");
                                                    Double layoverDuration = scanner.nextDouble();
                                                    scanner.nextLine();

                                                    //retrieve the main schedule plan 
                                                    FlightSchedulePlan flightSchedulePlanMain = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);

                                                    //create a return schedule plan
                                                    FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();
                                                    flightSchedulePlanForReturnFlight.setFlightScheduleType("RECURRENT_DAY");
                                                    flightSchedulePlanForReturnFlight.setEndDate(flightSchedulePlanMain.getEndDate());
                                                    flightSchedulePlanForReturnFlight.setIntervalDays(flightSchedulePlanMain.getIntervalDays());
                                                    flightSchedulePlanForReturnFlight.setFlightSchedulePlanType("RETURN");
                                                    flightSchedulePlanForReturnFlight.setStartDate(flightSchedulePlanMain.getStartDate());
                                                    flightSchedulePlanForReturnFlight.setEnabled(true);
                                                    Long returnFlightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewReturnFlightSchedulePlan(flightSchedulePlanForReturnFlight, flightSchedulePlanId);

                                                    
                                                        
                                                    //create fare for each flightscheduleplan
                                                    for(CabinClass cabinClass: flight.getReturnFlight().getAircraftConfig().getCabinClasses())
                                                    {
                                                        while(true)
                                                        {
                                                            try
                                                            {
                                                                System.out.println("\nFares for Return Flight Schedule Plan " + returnFlightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                                                System.out.print("Enter Fare Basis Code> ");
                                                                String fareBasisCode = scanner.nextLine().trim();
                                                                System.out.print("Enter Fare Amount> ");
                                                                BigDecimal fareAmount = scanner.nextBigDecimal();
                                                                scanner.nextLine();

                                                                newFare.setFareBasisCode(fareBasisCode);
                                                                newFare.setFareAmount(fareAmount);

                                                                Long fareId = fareSessionBeanRemote.createNewFare(newFare, returnFlightSchedulePlanId, cabinClass.getCabinClassId());  
                                                                break;
                                                            }
                                                            catch (FareBasisCodeExistException ex)
                                                            {
                                                                System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                                            }
                                                        }
                                                    } 
                                                           
                                                        
                     
                                                    //loop through the main flight schedules to associate with every return flight schedules
                                                    for(FlightSchedule flightSchedule: flightSchedulePlanMain.getFlightSchedules())
                                                    {
                                                        Long returnFlightScheduleId = createRecurrentReturnFlightSchedule(flightSchedule, flight, layoverDuration, returnFlightSchedulePlanId);
                                                    }
                                                    System.out.println("Return Recurrent Flight Schedules created successfully!\n");
                                                    break;
                                                }
                                                else if(response == 2)
                                                {
                                                    break;
                                                }
                                            }   
                                        }
                                        break;
                                    } 
                                    catch (ParseException ex) 
                                    {
                                        System.out.println("Date is in the wrong format");
                                    } 
                                    catch (FlightSchedulePlanNotFoundException ex) 
                                    { 
                                        System.out.println("An error has occurred while retrieving flight schedule plan: " + ex.getMessage() + "\n");
                                    }
                                }
                                break;
                            }
                            else if(choice == 2)
                            {
                                while(true)
                                {
                                    try
                                    {
                                        flightSchedulePlan.setFlightScheduleType("RECURRENT_WEEKLY");
                                        
                                        System.out.print("Enter Start(Departure) Date (dd-mm-yyyy)> ");
                                        String startDate = scanner.nextLine();
                                        System.out.print("Enter Start(Departure) Time (HH:mm)> ");
                                        String startTime = scanner.nextLine();
                                        System.out.print("Enter Flight Duration(hrs)> ");
                                        Double flightDuration = scanner.nextDouble();
                                        scanner.nextLine();
                                        System.out.print("Enter End Date(dd-mm-yyyy)> ");
                                        String endDate = scanner.nextLine();

                                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                                        Date formattedEndDate = formatter.parse(endDate);
                                        Date formattedStartDate = formatter.parse(startDate);
                                        
                                        SimpleDateFormat formatterDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                                        String dateTime = startDate + " " + startTime;
                                        Date formattedDateTime = formatterDateTime.parse(dateTime);

                                        flightSchedulePlan.setIntervalDays(7);
                                        flightSchedulePlan.setEndDate(formattedEndDate);
                                        flightSchedulePlan.setFlightSchedulePlanType("OUTBOUND");
                                        flightSchedulePlan.setStartDate(formattedDateTime);
                                        flightSchedulePlan.setEnabled(true);

                                        //create flight schedule plan
                                        flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);                                         

                                        Fare newFare = new Fare();

                                        //create fare for each flightscheduleplan
                                        
                                        for (CabinClass cabinClass : flight.getAircraftConfig().getCabinClasses()) 
                                        {
                                            while (true) 
                                            {
                                                try 
                                                {
                                                    System.out.println("\nFares for Flight Schedule Plan " + flightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                                    System.out.print("Enter Fare Basis Code> ");
                                                    String fareBasisCode = scanner.nextLine().trim();
                                                    System.out.print("Enter Fare Amount> ");
                                                    BigDecimal fareAmount = scanner.nextBigDecimal();
                                                    scanner.nextLine();

                                                    newFare.setFareBasisCode(fareBasisCode);
                                                    newFare.setFareAmount(fareAmount);
                                                    Long fareId = fareSessionBeanRemote.createNewFare(newFare, flightSchedulePlanId, cabinClass.getCabinClassId());
                                                    break;
                                                } 
                                                catch (FareBasisCodeExistException ex) 
                                                {
                                                    System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                                }
                                            }                      
                                        }

                                        Long difference_In_Time = formattedEndDate.getTime() - formattedStartDate.getTime();
                                        Long days = TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365;
                                        Integer numOfTimes = days.intValue()/7;

                                        if(numOfTimes > 0)
                                        {
                                            int interval = 0;

                                            //create recurrent schedules for the plan
                                            for(int i = 0; i < numOfTimes; i++)
                                            {
                                                createRecurrentFlightSchedule(flightSchedulePlanId, flight, startDate,  startTime, flightDuration, interval);
                                                interval += 7;
                                            }

                                            System.out.println("Recurrent Flight Schedules created successfully!\n");

                                            //create recurrent schedules plan
                                            if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) {
                                                System.out.println("Do you want to create a complementary return flight schedule plan for recurrent flight schedule?");
                                                while (true) 
                                                {
                                                    Integer response = 0;

                                                    System.out.println("1. Yes");
                                                    System.out.println("2. No");
                                                    System.out.print("> ");
                                                    response = scanner.nextInt();
                                                    scanner.nextLine();


                                                    if (response == 1) 
                                                    {
                                                        System.out.print("Enter Layover Duration(hrs)> ");
                                                        Double layoverDuration = scanner.nextDouble();
                                                        scanner.nextLine();

                                                        //retrieve the main schedule plan 
                                                        FlightSchedulePlan flightSchedulePlanMain = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);

                                                        //create a return schedule plan
                                                        FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();
                                                        flightSchedulePlanForReturnFlight.setFlightScheduleType("RECURRENT_WEEKLY");
                                                        flightSchedulePlanForReturnFlight.setEndDate(flightSchedulePlanMain.getEndDate());
                                                        flightSchedulePlanForReturnFlight.setIntervalDays(flightSchedulePlanMain.getIntervalDays());
                                                        flightSchedulePlanForReturnFlight.setFlightSchedulePlanType("RETURN");
                                                        flightSchedulePlanForReturnFlight.setStartDate(flightSchedulePlanMain.getStartDate());
                                                        flightSchedulePlanForReturnFlight.setEnabled(true);
                                                        Long returnFlightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewReturnFlightSchedulePlan(flightSchedulePlanForReturnFlight, flightSchedulePlanId);
                                                        
                                                        //create fare for each flightscheduleplan
                                                        for(CabinClass cabinClass: flight.getReturnFlight().getAircraftConfig().getCabinClasses())
                                                        {
                                                            while(true)
                                                            {
                                                                try
                                                                {
                                                                    System.out.println("\nFares for Return Flight Schedule Plan " + returnFlightSchedulePlanId + " :: " + cabinClass.getCabinClassType().toString());
                                                                    System.out.print("Enter Fare Basis Code> ");
                                                                    String fareBasisCode = scanner.nextLine().trim();
                                                                    System.out.print("Enter Fare Amount> ");
                                                                    BigDecimal fareAmount = scanner.nextBigDecimal();
                                                                    scanner.nextLine();

                                                                    newFare.setFareBasisCode(fareBasisCode);
                                                                    newFare.setFareAmount(fareAmount);

                                                                    Long fareId = fareSessionBeanRemote.createNewFare(newFare, returnFlightSchedulePlanId, cabinClass.getCabinClassId());  
                                                                    break;
                                                                }
                                                                catch (FareBasisCodeExistException ex)
                                                                {
                                                                    System.out.println("An error has occurred while creating flight schedule: The fare basis code exist! \n");
                                                                }
                                                            }
                                                        } 

                                                        //loop through the main flight schedules to associate with every return flight schedules
                                                        for(FlightSchedule flightSchedule: flightSchedulePlanMain.getFlightSchedules())
                                                        {
                                                            Long returnFlightScheduleId = createRecurrentReturnFlightSchedule(flightSchedule, flight, layoverDuration, returnFlightSchedulePlanId);
                                                        }
                                                        System.out.println("Return Recurrent Flight Schedules created successfully!\n");
                                                        break;
                                                    }
                                                    else if(response == 2)
                                                    {
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                        else
                                        {
                                            System.out.println("Schedule cannot be created!\n");
                                        }  
                                        break;
                                    } 
                                    catch (ParseException ex) 
                                    {
                                        System.out.println("Date is in the wrong format!\n");
                                    } 
                                    catch (FlightSchedulePlanNotFoundException ex) 
                                    {
                                        System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
                                    }
                                }
                                break;
                            }
                        }
                        break;
                    }
                    else
                    {
                        System.out.println("Invalid option, please try again!\n");
                    }
                }
            }
            else
            {
                System.out.println("Flight number should start with ML");
            }
      
        } 
        catch (FlightNotFoundException ex) 
        {
            System.out.println("An error has occurred while creating the new flight schedule: The flight does not exist!\n");
        } 
        catch (FlightSchedulePlanNotFoundException ex) 
        {
            System.out.println("An error has occurred while creating the new flight schedule: The flight schedule plan does not exist!\n");
        }  
        catch (UnknownPersistenceException ex) 
        {
            System.out.println("An error has occurred while creating the new flight schedule: " + ex.getMessage() + "\n");
        }
        
    }
    
    private Long doCreateEveryFlightSchedules(Long flightSchedulePlanId, Flight flight, String flightSchedulePlanType)
    {
        Scanner scanner = new Scanner(System.in);
        
        List<Boolean> isOverlapped = new ArrayList<>();

        Long flightScheduleId = 0l;
        
        List<FlightSchedule> fsList = new ArrayList<>();

        for(FlightSchedulePlan flightSchedulePlan: flight.getFlightSchedulePlans())
        {
            for(FlightSchedule flightSchedule: flightSchedulePlan.getFlightSchedules())
            {
                fsList.add(flightSchedule);
            }
        }
        
        while (true) 
        {
            try 
            {
                FlightSchedule flightSchedule = new FlightSchedule();
                System.out.println("");
                System.out.print("Enter Departure Date(dd-mm-yyyy)> ");
                String departureDate = scanner.nextLine();
                System.out.print("Enter Departure Time(HH:mm)> ");
                String departureTime = scanner.nextLine();
                System.out.print("Enter Flight Duration(hrs)> ");
                Double flightDuration = scanner.nextDouble();
                scanner.nextLine();

                FlightSchedulePlan flightSchedulePlanFromDb = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);

                String departureDateTimeInString = departureDate + " " + departureTime;
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date formattedDT = formatter.parse(departureDateTimeInString);
                
                Integer flightDurationInMins = (int)(flightDuration*60);
                Integer flightDurationHours = flightDurationInMins/60;
                Integer flightDurationMins = flightDurationInMins%60;

                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(formattedDT);
                //to add timediff to departure time 
//                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlanFromDb.getFlight().getFlightRoute().getOrigin().getTimeZoneDiff());
                Date departureDT = formatter.parse(formatter.format(calendar.getTime()));
                calendar.setTime(departureDT);
                //to add flight duration to the departure time = arrival time
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightDurationHours);
                calendar.add(GregorianCalendar.MINUTE, flightDurationMins);
                //to add timezone to arrival date time
                Date arrivalDT = calendar.getTime();
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flight.getFlightRoute().getDestination().getTimeZoneDiff());
                Date arrivalDTTimeZone = calendar.getTime();
                
                
                  
                if(fsList.size() == 0)
                {
                    flightSchedule.setDepartureDateTime(departureDT);
                    flightSchedule.setFlightHours(flightDurationHours);
                    flightSchedule.setFlightMinutes(flightDurationMins);
                    flightSchedule.setFlightScheduleType("OUTBOUND");
                    flightSchedule.setEnabled(true);

                    flightScheduleId = flightScheduleSessionBeanRemote.createNewFlightSchedule(flightSchedule, flightSchedulePlanId);

                    //create seat inventory 
                    SeatInventory seatInventory = new SeatInventory();
                    Long seatInventoryId = 0l;
                    //get max seat capacity from the flight's cabin classes
                    for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
                    {
                        Integer totalMaxSeatCapacityForEachCabinClass = cabinClass.getMaxSeatCapacity();
                        seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityForEachCabinClass);
                        seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityForEachCabinClass);
                        seatInventory.setNumOfReservedSeats(0);

                        seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory, flightScheduleId, cabinClass.getCabinClassId());
                    }
                    break;
                }
                else
                {
                    for(FlightSchedule flightSchedule1: fsList)
                    {
                        calendar.setTime(flightSchedule1.getDepartureDateTime());
                        calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedule1.getFlightHours());
                        calendar.add(GregorianCalendar.MINUTE, flightSchedule1.getFlightMinutes());
                        calendar.add(GregorianCalendar.HOUR_OF_DAY, flight.getFlightRoute().getDestination().getTimeZoneDiff());
                        Date arrivalDateTimeZone = calendar.getTime();

                        //to check if departure date entered overlap with other flight schedules
                        if(flightSchedule1.getDepartureDateTime().compareTo(departureDT) * departureDT.compareTo(arrivalDateTimeZone) >= 0 || flightSchedule1.getDepartureDateTime().compareTo(arrivalDTTimeZone) * arrivalDTTimeZone.compareTo(arrivalDateTimeZone) >= 0)
                        {
                            isOverlapped.add(true);
                        }
                        else
                        {
                            isOverlapped.add(false);
                        }
                    }
                }
                if(isOverlapped.contains(true))
                {
                    System.out.println("An error has occurred while creating flight schedule: flight schedule overlaps with other flight schedule!\n");
                    isOverlapped.removeAll(isOverlapped);
                }
                else
                {
                    flightSchedule.setDepartureDateTime(departureDT);
                    flightSchedule.setFlightHours(flightDurationHours);
                    flightSchedule.setFlightMinutes(flightDurationMins);
                    flightSchedule.setFlightScheduleType("OUTBOUND");
                    flightSchedule.setEnabled(true);

                    flightScheduleId = flightScheduleSessionBeanRemote.createNewFlightSchedule(flightSchedule, flightSchedulePlanId);

                    //create seat inventory 
                    SeatInventory seatInventory = new SeatInventory();
                    Long seatInventoryId = 0l;
                    //get max seat capacity from the flight's cabin classes
                    for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
                    {
                        Integer totalMaxSeatCapacityForEachCabinClass = cabinClass.getMaxSeatCapacity();
                        seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityForEachCabinClass);
                        seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityForEachCabinClass);
                        seatInventory.setNumOfReservedSeats(0);

                        seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory, flightScheduleId, cabinClass.getCabinClassId());
                    }
                           
                    break;
                }
            } 
            catch (ParseException ex) 
            {
                System.out.println("Departure Time is in wrong format!\n");
            } 
            catch (FlightSchedulePlanNotFoundException ex) 
            {
                System.out.println("An error has occurred while retrieving flight schedule: flight schedule plan does not exist!\n");
            } 
            catch (FlightScheduleNotFoundException ex) 
            {
                System.out.println("An error has occurred while retrieving flight schedule: flight schedule does not exist!\n");
            } 
        }
        
        
        return flightScheduleId;
    }
    
    private FlightSchedule doCreateEveryReturnFlightSchedules(Long flightScheduleId, Flight flight, Double layoverDuration, String flightSchedulePlanType)
    {
        FlightSchedule flightScheduleForReturnFlight = new FlightSchedule();
        try
        {
            Integer layoverDurationInMins = (int) (layoverDuration * 60);
            Integer layoverDurationHours = layoverDurationInMins / 60;
            Integer layoverDurationMins = layoverDurationInMins % 60;

            //retrieve the main flight schedule
            FlightSchedule flightSchedule = flightScheduleSessionBeanRemote.getFlightScheduleById(flightScheduleId);
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(flightSchedule.getDepartureDateTime());
            //to add flight duration to departure date time = arrival time
            calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedule.getFlightHours());
            calendar.add(GregorianCalendar.MINUTE, flightSchedule.getFlightMinutes());
            Date arrivalDatetime = calendar.getTime();
            //to add in time diff (arrival time zone)
            calendar.add(GregorianCalendar.HOUR_OF_DAY, flight.getFlightRoute().getDestination().getTimeZoneDiff());
            Date arrivalDatetimeTimezone = calendar.getTime();
            //to add in layover duration
            calendar.add(GregorianCalendar.HOUR_OF_DAY, layoverDurationHours);
            calendar.add(GregorianCalendar.MINUTE, layoverDurationMins);
            Date departureDateTimeforReturn = calendar.getTime();

            flightScheduleForReturnFlight.setDepartureDateTime(departureDateTimeforReturn);
            flightScheduleForReturnFlight.setFlightHours(flightSchedule.getFlightHours());
            flightScheduleForReturnFlight.setFlightMinutes(flightSchedule.getFlightMinutes());
            flightScheduleForReturnFlight.setFlightScheduleType("RETURN");
            flightScheduleForReturnFlight.setEnabled(true);

            FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();
            if (flightSchedulePlanType.equals("SINGLE")) {
                flightSchedulePlanForReturnFlight.setFlightScheduleType("SINGLE");
            } else if (flightSchedulePlanType.equals("MULTIPLE")) {
                flightSchedulePlanForReturnFlight.setFlightScheduleType("MULTIPLE");
            }
            flightSchedulePlanForReturnFlight.setEndDate(null);
            flightSchedulePlanForReturnFlight.setIntervalDays(0);
            flightSchedulePlanForReturnFlight.setFlightSchedulePlanType("RETURN");
            flightSchedulePlanForReturnFlight.setEnabled(true);
        } 
        catch (FlightScheduleNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
        }
        
        return flightScheduleForReturnFlight;
    
    }
    
    
    private Long createRecurrentFlightSchedule(Long flightSchedulePlanId, Flight flight, String departureDate, String departureTime, Double flightDuration, Integer interval)
    {
        Scanner scanner = new Scanner(System.in);
        Long flightScheduleId = 0l;
        try
        {
            //retrieve the flight schedule plan for recurrent
            FlightSchedulePlan flightSchedulePlanFromDb = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);
            FlightSchedule flightSchedule = new FlightSchedule();

            //concat and format the departure date and time
            String departureDateTimeInString = departureDate + " " + departureTime;
            SimpleDateFormat formatterDeparture = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date formattedDepartureDT = formatterDeparture.parse(departureDateTimeInString);

            //add it to calendar so that it can be incremented
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(formattedDepartureDT);
            //to add timediff to departure time 
//            calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlanFromDb.getFlight().getFlightRoute().getOrigin().getTimeZoneDiff());
            //to add the recurrent interval day to the departure date every loop
            calendar.add(GregorianCalendar.DATE, interval);
            Date departureDT = formatterDeparture.parse(formatterDeparture.format(calendar.getTime()));

            //convert decimal flight duration to hrs and mins
            Integer flightDurationInMins = (int) (flightDuration * 60);
            Integer flightDurationHours = flightDurationInMins / 60;
            Integer flightDurationMins = flightDurationInMins % 60;

            flightSchedule.setDepartureDateTime(departureDT);
            flightSchedule.setFlightHours(flightDurationHours);
            flightSchedule.setFlightMinutes(flightDurationMins);
            flightSchedule.setFlightScheduleType("OUTBOUND");
            flightSchedule.setEnabled(true);
            
            //create one new recurrent flight schedule
            flightScheduleId = flightScheduleSessionBeanRemote.createNewFlightSchedule(flightSchedule, flightSchedulePlanId);

            //create seat inventory 
            SeatInventory seatInventory = new SeatInventory();
            Long seatInventoryId = 0l;
            //get max seat capacity from the flight's cabin classes
            for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
            {
                Integer totalMaxSeatCapacityForEachCabinClass = cabinClass.getMaxSeatCapacity();
                seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityForEachCabinClass);
                seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityForEachCabinClass);
                seatInventory.setNumOfReservedSeats(0);

                seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory, flightScheduleId, cabinClass.getCabinClassId());
            }  
 
        } 
        catch (FlightSchedulePlanNotFoundException ex) 
        {
            System.out.println("An error has occurred while creating flight schedule: flight schedule plan does not exist!\n");
        } 
        catch (ParseException ex) 
        {
            System.out.println("Departure Date Time is in wrong format!\n");
        } 
        catch (FlightScheduleNotFoundException ex) 
        {
            System.out.println("An error has occurred while creating flight schedule: flight schedule does not exist!\n");
        }
        
        return flightScheduleId;
        
    }
    
    private Long createRecurrentReturnFlightSchedule(FlightSchedule flightSchedule, Flight flight, Double layoverDuration, Long returnFlightSchedulePlanId){
                    
        Scanner scanner = new Scanner(System.in);
        Long returnFlightScheduleId = 0l;
        try{
            //convert decimal flight duration to hrs and mins
            Integer layoverDurationInMins = (int) (layoverDuration * 60);
            Integer layoverDurationHours = layoverDurationInMins / 60;
            Integer layoverDurationMins = layoverDurationInMins % 60;
                                             
          
            GregorianCalendar calendar = new GregorianCalendar();
            //to add in flight duration to departure datetime = arrival time
            calendar.setTime(flightSchedule.getDepartureDateTime());
            calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedule.getFlightHours());
            calendar.add(GregorianCalendar.MINUTE, flightSchedule.getFlightMinutes());
            Date arrivalDateTimeReturn = calendar.getTime();
            //to add in time diff (arrival timezone)
            calendar.add(GregorianCalendar.HOUR_OF_DAY, flight.getFlightRoute().getDestination().getTimeZoneDiff());
            Date arrivalDateTimeReturnTimezone = calendar.getTime();
            //to add in layover duration
            calendar.add(GregorianCalendar.HOUR_OF_DAY, layoverDurationHours);
            calendar.add(GregorianCalendar.MINUTE, layoverDurationMins);
            Date departureDateTimeforReturn = calendar.getTime();
            
            FlightSchedule flightScheduleForReturnFlight = new FlightSchedule();
            flightScheduleForReturnFlight.setDepartureDateTime(departureDateTimeforReturn);
            flightScheduleForReturnFlight.setFlightHours(flightSchedule.getFlightHours());
            flightScheduleForReturnFlight.setFlightMinutes(flightSchedule.getFlightMinutes());
            flightScheduleForReturnFlight.setFlightScheduleType("RETURN");
            flightScheduleForReturnFlight.setEnabled(true);

            returnFlightScheduleId = flightScheduleSessionBeanRemote.createNewReturnFlightSchedule(flightScheduleForReturnFlight, flightSchedule.getFlightScheduleId(), returnFlightSchedulePlanId);

            //create seat inventory 
            SeatInventory seatInventory = new SeatInventory();
            Long seatInventoryId = 0l;
            //get max seat capacity from the flight's cabin classes
            for(CabinClass cabinClass: flight.getAircraftConfig().getCabinClasses())
            {
                Integer totalMaxSeatCapacityForEachCabinClass = cabinClass.getMaxSeatCapacity();
                seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityForEachCabinClass);
                seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityForEachCabinClass);
                seatInventory.setNumOfReservedSeats(0);

                seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory, returnFlightScheduleId, cabinClass.getCabinClassId());
            }  
            
        } catch (FlightScheduleNotFoundException | FlightSchedulePlanNotFoundException ex) {
            System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
        }

        return returnFlightScheduleId;       
    }
    
    private void doViewAllFlightSchedulePlans()
    {
        Scanner scanner = new Scanner (System.in);
        System.out.println("*** FRS Management :: Flight Operation :: Flight Schedule Plan :: View All Flight Schedule Plans ***\n");
        
        Integer option = 0;
        
        List<FlightSchedulePlan> flightSchedulePlans = flightSchedulePlanSessionBeanRemote.getAllFlightSchedulePlan();
        List<FlightSchedulePlan> outboundFlightSchedulePlans = new ArrayList<>();
        
        System.out.printf("%20s%30s%20s%20s\n", "#" ,"Flight Schedule Plan Id", "Type", "Flight Number");
        
        for(FlightSchedulePlan flightSchedulePlan: flightSchedulePlans)
        {
            if(flightSchedulePlan.getFlightSchedulePlanType().equals("OUTBOUND"))
            {
                outboundFlightSchedulePlans.add(flightSchedulePlan);
                
                if(flightSchedulePlan.getReturnFlightSchedulePlan().getFlightSchedulePlanId() != flightSchedulePlan.getFlightSchedulePlanId())
                {
                    outboundFlightSchedulePlans.add(flightSchedulePlan.getReturnFlightSchedulePlan());
                }
            }
        }
        
        for(FlightSchedulePlan flightSchedulePlan: outboundFlightSchedulePlans)
        {
            option++;
            System.out.printf("%20s%30s%20s%20s\n", option ,flightSchedulePlan.getFlightSchedulePlanId(), flightSchedulePlan.getFlightScheduleType(), flightSchedulePlan.getFlight().getFlightNumber());
        }
        
        System.out.println("------------------------------------------");
        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
        
    }
    
    private void doViewFlightSchedulePlanDetails()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** FRS Management :: Flight Operation :: Flight Schedule :: View Flight Schedule Plan Details ***\n");
        System.out.print("Enter Flight Schedule Plan ID> ");
        Long flightSchedulePlanID = scanner.nextLong();
        
        Integer response = 0;
        
        try 
        {
            
            FlightSchedulePlan flightSchedulePlan = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanID);
            System.out.println("");
            System.out.printf("%40s%40s\n", "Flight Schedule Plan Type", "Flight Route");
            System.out.printf("%40s%40s\n", flightSchedulePlan.getFlightScheduleType(), flightSchedulePlan.getFlight().getFlightRoute().getOrigin().getIataCode() + " - " + flightSchedulePlan.getFlight().getFlightRoute().getDestination().getIataCode());
            
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("Flight Schedules for Flight Schedule Plan " + flightSchedulePlan.getFlightSchedulePlanId());
            Integer option = 0;
            
            System.out.printf("%20s%30s%30s%30s\n","#", "Departure Date Time", "Arrival Date Time", "Duration");
            for(FlightSchedule flightSchedule: flightSchedulePlan.getFlightSchedules())
            {
                option++;
                
                //to calculate the arrival time
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(flightSchedule.getDepartureDateTime());
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedule.getFlightHours());
                calendar.add(GregorianCalendar.MINUTE, flightSchedule.getFlightMinutes());
                Date arrivalDateTime = calendar.getTime();
                //to add in time diff (arrival timezone)
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlan.getFlight().getFlightRoute().getDestination().getTimeZoneDiff());
                Date arrivalDateTimeTimezone = calendar.getTime();

                
                System.out.printf("%20s%30s%30s%30s\n", option, formatter.format(flightSchedule.getDepartureDateTime()), formatter.format(arrivalDateTimeTimezone), flightSchedule.getFlightHours() + " hrs " + flightSchedule.getFlightMinutes() + " mins");
                
            }
            
            System.out.println("-------------------------------------------------------------------------------------------");
            System.out.println("Fares for Flight Schedule Plan " + flightSchedulePlan.getFlightSchedulePlanId());
            System.out.printf("%20s%30s%30s%30s\n","#", "Fare Basis Code", "Cabin Class", "Fare Amount");
            option = 0;
           
            for(Fare fare: fareSessionBeanRemote.getFaresByFlightSchedulePlanId(flightSchedulePlanID))
            {
                option++;
                System.out.printf("%20s%30s%30s%30s\n", option, fare.getFareBasisCode(), fare.getCabinClass().getCabinClassType(), fare.getFareAmount());
            }
            
            System.out.println("");
            System.out.println("-------------------------------------------------------------------------------------------");
            
            System.out.println("1: Update Flight Schedule Plan");
            System.out.println("2: Delete Flight Schedule Plan");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = scanner.nextInt();
            
            if(response == 1)
            {
                doUpdateFlightSchedulePlan(flightSchedulePlan);
                
            }
            else if(response == 2)
            {
                doDeleteFlightSchedulePlan(flightSchedulePlan);
            }

        } 
        catch (FlightSchedulePlanNotFoundException ex) 
        {
            System.out.println("An error has occurred while retrieving flight schedule plan record: flight schedule plan does not exist!\n");
        }
    }
    
    private void doUpdateFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan)
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Update Flight Schedule Plan ***\n");
        
        String flightScheduleType = flightSchedulePlan.getFlightScheduleType();
        
        List<Fare> fares = fareSessionBeanRemote.getFaresByFlightSchedulePlanId(flightSchedulePlan.getFlightSchedulePlanId());
        List<Fare> storeFares = new ArrayList<>();
        List<FlightSchedule> flightSchedules = flightScheduleSessionBeanRemote.getFlightScheduleByFlightSchedulePlanId(flightSchedulePlan.getFlightSchedulePlanId());
        List<FlightSchedule> storeFlightSchedules = new ArrayList<>();
        
        if(flightScheduleType.equals("SINGLE") || flightScheduleType.equals("MULTIPLE"))
        {
            //prompt user to change fare for each cabin classes
            for(Fare fare: fares)
            {
                System.out.println("\nFares for Flight Schedule Plan " + flightSchedulePlan.getFlightSchedulePlanId() + " :: " + fare.getCabinClass().getCabinClassType().toString());
                System.out.print("Enter Fare Amount(0 if no change)> ");
                BigDecimal fareAmount = scanner.nextBigDecimal();
                scanner.nextLine();

                if(fareAmount.compareTo(BigDecimal.ZERO) != 0)
                {
                    fare.setFareAmount(fareAmount);
                }

                storeFares.add(fare);
                
            }
            
            
            //prompt user to change flight schedule
            for(FlightSchedule flightSchedule: flightSchedules)
            {
                System.out.println("\n" + flightScheduleType + " Flight Schedule: " + flightSchedule.getFlightScheduleId());
                while(true)
                {
                    try
                    {
                        System.out.print("Enter Departure Date(dd-mm-yyyy)(blank if no change)> ");
                        String departureDate = scanner.nextLine().trim();
                        System.out.print("Enter Departure Time(HH:mm)(blank if no change)> ");
                        String departureTime = scanner.nextLine().trim();
                        
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

                        if(departureDate.length() > 0 && departureTime.length() > 0)
                        {
                            String departureDateTimeInString = departureDate + " " + departureTime;
                            Date formattedDepartureDateTime = formatter.parse(departureDateTimeInString);
                            flightSchedule.setDepartureDateTime(formattedDepartureDateTime);
                            break;
                        }
                        else if(departureDate.length() > 0 && departureTime.length() <= 0)
                        {
                            String dateTime = formatter.format(flightSchedule.getDepartureDateTime());
                            String[] array = dateTime.split(" ");
                            String time = array[1];
                            
                            String departureDateTimeInString = departureDate + " " + time;
                            Date formattedDepartureDateTime = formatter.parse(departureDateTimeInString);
                            flightSchedule.setDepartureDateTime(formattedDepartureDateTime);
                            break;
                        }
                        else if(departureDate.length() <= 0 && departureTime.length() > 0)
                        {
                            String dateTime = formatter.format(flightSchedule.getDepartureDateTime());
                            String[] array = dateTime.split(" ");
                            String date = array[0];
                            
                            String departureDateTimeInString = date + " " + departureTime;
                            Date formattedDepartureDateTime = formatter.parse(departureDateTimeInString);
                            flightSchedule.setDepartureDateTime(formattedDepartureDateTime);
                            break;
                        }
                        else if(departureDate.length() <= 0 && departureTime.length() <= 0 )
                        {
                            break;
                        }
                        else{
                            System.out.println("Wrong date input!\n");
                        }
                    } 
                    catch (ParseException ex) 
                    {
                        System.out.println("Date is in the wrong format!\n");
                    }
                }
                
                System.out.print("Enter Flight Duration(hrs)(0 if no change)> ");
                Double flightDuration = scanner.nextDouble();
                scanner.nextLine();
                
                Integer flightDurationInMins = (int) (flightDuration * 60);
                Integer flightDurationHours = flightDurationInMins / 60;
                Integer flightDurationMins = flightDurationHours % 60;
                
                if(flightDuration != 0)
                {
                    flightSchedule.setFlightHours(flightDurationHours);
                    flightSchedule.setFlightMinutes(flightDurationMins);
                }

                storeFlightSchedules.add(flightSchedule);
            }
            
            //do the final storage
            for (Fare fare : storeFares) 
            {
                try 
                {
                    fareSessionBeanRemote.updateFare(fare);
                } 
                catch (FareNotFoundException ex) 
                {
                    System.out.println("An error has occurred while updating flight schedule plan record: fare does not exist!\n");
                }
            }
            for(FlightSchedule flightSchedule: storeFlightSchedules)
            {
                try
                {
                    flightScheduleSessionBeanRemote.updateFlightSchedule(flightSchedule);
                } 
                catch (FlightScheduleNotFoundException ex) 
                {
                    System.out.println("An error has occurred while updating flight schedule plan record: flight schedule does not exist!\n");
                }
            }
            flightSchedulePlan.setFares(storeFares);
            flightSchedulePlan.setFlightSchedules(flightSchedules);
            
            try 
            {
                flightSchedulePlanSessionBeanRemote.updateFlightSchedulePlan(flightSchedulePlan);
                System.out.println("Flight Schedule Plan updated successfully!\n");
            } 
            catch (FlightSchedulePlanNotFoundException ex) 
            {
                System.out.println("An error has occurred while updating flight schedule plan record: flight schedule plan does not exist!\n");
            }
            
        }
        else if(flightScheduleType.equals("RECURRENT_DAY") || flightScheduleType.equals("RECURRENT_WEEKLY"))
        {
            for(Fare fare: fares)
            {
                System.out.println("\nFares for Flight Schedule Plan " + flightSchedulePlan.getFlightSchedulePlanId() + " :: " + fare.getCabinClass().getCabinClassType().toString());
                System.out.print("Enter Fare Amount(0 if no change)> ");
                BigDecimal fareAmount = scanner.nextBigDecimal();
                scanner.nextLine();

                if(fareAmount.compareTo(BigDecimal.ZERO) != 0)
                {
                    fare.setFareAmount(fareAmount);
                }
                storeFares.add(fare);
            }
            
            if(flightScheduleType.equals("RECURRENT_DAY") || flightScheduleType.equals("RECURRENT_WEEKLY"))
            {
                while(true)
                {
                    System.out.print("\nEnter Start Date(dd-MM-yyyy HH:mm)(blank if no change)> ");
                    String startDate = scanner.nextLine().trim();
                    System.out.print("Enter Flight Duration(hrs)(0 if no change)> ");
                    Double flightDuration = scanner.nextDouble();
                    
                    Integer dayInterval = 0;
                    if(flightScheduleType.equals("RECURRENT_DAY"))
                    {
                        System.out.print("Enter Day Interval(0 if no change)> ");
                        dayInterval = scanner.nextInt();
                    }
                    scanner.nextLine();
                    System.out.print("Enter End Date(dd-MM-yyyy)(blank if no change)> ");
                    String endDate = scanner.nextLine().trim();

                    //initialization
                    Date startDateFromDb = new Date();
 
                    Double flightDurationFromDb = 0.0;
                    Integer dayIntervalFromDb = 0;
                    Date endDateFromDb = new Date();
                    
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");                   
                    SimpleDateFormat formatterEndDate = new SimpleDateFormat("dd-MM-yyyy");
                    
                    if(startDate.length() > 0)
                    {
                        try 
                        {
                            startDateFromDb = formatter.parse(startDate);
                        } 
                        catch (ParseException ex) 
                        {
                            System.out.println("Date is in the wrong format!\n");
                        }
                        
                    }
                    else
                    {
                        startDateFromDb = flightSchedulePlan.getStartDate();
                    }
 
                    if(flightDuration != 0.0)
                    {
                        flightDurationFromDb = flightDuration;
                    }
                    else
                    {
                        Integer flightHoursFromDb = 0;
                        Integer flightMinsFromDb = 0;
                        //to get the flight duration, they have the same flight hours and mins since they are recurrent
                        for(FlightSchedule flightSchedule: flightSchedules)
                        {
                            flightHoursFromDb = flightSchedule.getFlightHours();
                            flightMinsFromDb = flightSchedule.getFlightMinutes();
                        }
     
                        flightDurationFromDb = Double.valueOf(flightHoursFromDb) + Double.valueOf(flightMinsFromDb);
                    }
                    
                    if(flightScheduleType.equals("RECURRENT_DAY"))
                    {
                        if(dayInterval != 0)
                        {
                            dayIntervalFromDb = dayInterval;
                        }
                        else
                        {
                            dayIntervalFromDb = flightSchedulePlan.getIntervalDays();
                        }
                    }
                    else
                    {
                        dayIntervalFromDb = 7;
                    }
                    
                    if(endDate.length() > 0)
                    {
                        try 
                        {
                            endDateFromDb = formatterEndDate.parse(endDate);
                        } 
                        catch (ParseException ex) 
                        {
                            System.out.println("Date is in the wrong format!\n");
                        }  
                    }
                    else
                    {
                        endDateFromDb = flightSchedulePlan.getEndDate();
                    }
                    
                    //separate date and time from datetime
                    String[] array = formatter.format(startDateFromDb).split(" ");
                    String startDateSplit = array[0];
                    String startTimeSplit = array[1];
                    

                    int interval = 0;
                    
                    //update current schedule plan
                    flightSchedulePlan.setStartDate(startDateFromDb);
                    flightSchedulePlan.setIntervalDays(dayIntervalFromDb);
                    flightSchedulePlan.setEndDate(endDateFromDb);
                    
                    try 
                    {
                        flightSchedulePlanSessionBeanRemote.updateFlightSchedulePlan(flightSchedulePlan);
                        System.out.println("Flight Schedule Plan updated successfully!\n");
                    } 
                    catch (FlightSchedulePlanNotFoundException ex) 
                    {
                        System.out.println("An error has occurred while updating flight schedule plan record: flight schedule plan does not exist!\n");
                    }
                    
                    Boolean isEmpty = true;
                    
                    List<FlightSchedule> flightSchedulesList = flightScheduleSessionBeanRemote.getFlightScheduleByFlightSchedulePlanId(flightSchedulePlan.getFlightSchedulePlanId());
                    
                    //if there is any change in the plan parameter, check if flight reservation record
                    if(startDate.length() > 0 || dayInterval != 0 || endDate.length() > 0)
                    {
                        for (FlightSchedule flightSchedule : flightSchedulesList) 
                        {
                            try
                            {
                                if(flightSchedule.getFlightReservationRecords().isEmpty())
                                {
                                    //delete recurrent schedules and seatinventory associated to it
                                    flightScheduleSessionBeanRemote.removeFlightSchedule(flightSchedule.getFlightScheduleId());

                                }
                                else
                                {
                                    isEmpty = false;
                                    System.out.println("An error has occurred while updating flight schedule:This record is associated to flight reservation record!\n");
                                    break;
                                }
                            } 
                            catch (FlightScheduleNotFoundException ex) 
                            {
                                System.out.println("An error has occurred while updating flight schedule: flight schedule does not exist!\n");
                            }
                            
                        }
                    }
                    
                    //to get the num of flight schedules 
                    Long difference_In_Time = endDateFromDb.getTime() - startDateFromDb.getTime();
                    Long days = TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365;
                    Integer numOfTimes = days.intValue() / dayIntervalFromDb;
                    
                    if(isEmpty == true)
                    {
                        //create recurrent schedules for the plan
                        for (int i = 0; i < numOfTimes; i++) 
                        {
                            createRecurrentFlightSchedule(flightSchedulePlan.getFlightSchedulePlanId(), flightSchedulePlan.getFlight(), startDateSplit, startTimeSplit, flightDurationFromDb, interval);
                            interval += dayInterval;
                        }
                    }
                    
                    break;
                }
            } 
        }
    }
    
    private void doDeleteFlightSchedulePlan(FlightSchedulePlan flightSchedulePlan)
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Delete Flight Schedule Plan ***\n");

            System.out.printf("Confirm Delete Flight Schedule Plan %s (Flight Schedule Plan Type: %s) (Enter 'Y' to Delete)> ", flightSchedulePlan.getFlightSchedulePlanId(), flightSchedulePlan.getFlightScheduleType());
            String input = scanner.nextLine().trim();

            List<Boolean> isEmptyList = new ArrayList<>();

            if(input.equals("Y"))
            {
                List<FlightSchedule> flightSchedules = flightScheduleSessionBeanRemote.getFlightScheduleByFlightSchedulePlanId(flightSchedulePlan.getFlightSchedulePlanId());

                //to check if all the flightschedules contains reservation
                for(FlightSchedule flightSchedule: flightSchedules)
                {
                    if(flightSchedule.getFlightReservationRecords().isEmpty())
                    {
                        isEmptyList.add(true);
                    }
                    else
                    {
                        isEmptyList.add(false);
                    }
                }


                if(isEmptyList.contains(false))
                {
                    flightSchedulePlanSessionBeanRemote.setFlightSchedulePlanDisabled(flightSchedulePlan.getFlightSchedulePlanId());
                    System.out.println("Flight Schedule Plan is set to disabled!\n");
                }
                else
                {
                    flightSchedulePlanSessionBeanRemote.removeFlightSchedulePlan(flightSchedulePlan.getFlightSchedulePlanId());
                    System.out.println("Flight Schedule Plan removed successfully!\n");
                }

            }
        } 
        catch (FlightSchedulePlanNotFoundException ex) 
        {
            System.out.println("An error has occurred while removing flight schedule plan: flight schedule plan does not exist!\n");
        }
        
        
    }
}