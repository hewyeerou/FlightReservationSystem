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
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.SeatinventorySessionBeanRemote;
import entity.AircraftConfig;
import entity.CabinClass;
import entity.Employee;
import entity.Flight;
import entity.FlightRoute;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
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
import util.exception.FlightNotFoundException;
import util.exception.FlightNumExistException;
import util.exception.FlightRouteNotFoundException;
import util.exception.FlightScheduleNotFoundException;
import util.exception.FlightSchedulePlanNotFoundException;
import util.exception.InvalidAccessRightsException;
import util.exception.UnknownPersistenceException;
import util.exception.createOutboundReturnFlightCheckException;

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
    private SeatinventorySessionBeanRemote seatinventorySessionBeanRemote;


    private Employee currentEmployee;
    
    public FlightOperationModule() 
    {
    }
    
    public FlightOperationModule(Employee currentEmployee, FlightSessionBeanRemote flightSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote, FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote, FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote, SeatinventorySessionBeanRemote seatinventorySessionBeanRemote)
    {
        this();
        this.currentEmployee  = currentEmployee;
        this.flightSessionBeanRemote = flightSessionBean;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
        this.flightSchedulePlanSessionBeanRemote = flightSchedulePlanSessionBeanRemote;
        this.flightScheduleSessionBeanRemote = flightScheduleSessionBeanRemote;
        this.seatinventorySessionBeanRemote = seatinventorySessionBeanRemote;
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
            else if(flight.getFlightType().equals("RETURN"))                    //return flight that does not have outbound flight associated to it
            {
                if(!outboundFlights.contains(flight))
                {
                    outboundFlights.add(flight);
                }
            }
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

                    if(option >=1 && option <= 3)
                    {
                        if(option == 1)
                        {
                            flightSchedulePlan.setFlightScheduleType("SINGLE");
                            flightSchedulePlan.setIntervalDays(0);
                            flightSchedulePlan.setEndDate(null);

                            flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);
                            System.out.println("Flight Schedule Plan created successfully!: " + flightSchedulePlanId);
                            
                            System.out.println("\n*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Create Single Schedule ***\n");
                            flightScheduleId = doCreateEveryFlightSchedules(flightSchedulePlanId, flight, "SINGLE");  
                            
                            break;
                        }
                        else if(option == 2)
                        {
                            flightSchedulePlan.setFlightScheduleType("MULTIPLE");       
                            flightSchedulePlan.setIntervalDays(0);
                            flightSchedulePlan.setEndDate(null);
                            
                            flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);
                            System.out.println("Flight Schedule Plan created successfully!: " + flightSchedulePlanId);
                            
                            String choice = "";
                            
                            System.out.println("\n*** FRS Management :: Flight Operation :: Flight Schedule Plan :: Create Multiple Schedule ***\n");
                            
                            do
                            {
                                doCreateEveryFlightSchedules(flightSchedulePlanId, flight, "MULTIPLE");
                                
                                System.out.print("More? (Enter 'Q' to complete adding flight schedules)> ");
                                choice = scanner.nextLine().trim();
                                
                            } while(!choice.equals("Q"));
                            
                            break;
                        } 
                        else if (option == 3) 
                        {
                            flightSchedulePlan.setFlightScheduleType("RECURRENT");
                            
                            while(true)
                            {
                                Integer choice = 0;
                                
                                System.out.println("Select Recurrent Interval Type: ");
                                System.out.println("1: Recurrent Every n Days");
                                System.out.println("2: Recurrent Weekly");
                                System.out.print("> ");
                                choice = scanner.nextInt();
                                scanner.nextLine();
                                
                                if (choice >= 1 && choice <= 2) 
                                {
                                    if(choice == 1)
                                    {
                                        try{
                                            System.out.print("Enter day interval> ");
                                            Integer dayInterval = scanner.nextInt();
                                            scanner.nextLine();
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

                                            flightSchedulePlan.setIntervalDays(dayInterval);
                                            flightSchedulePlan.setEndDate(formattedEndDate);
                                            
                                            flightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewFlightSchedulePlan(flightSchedulePlan, flightNum);                                         
                                            
//                                            Long recurrentFlightScheduleId = doCreateEveryFlightSchedules(flightSchedulePlanId, flight, "RECURRENT");
//                                            FlightSchedule recurrentFLightSchedule = flightScheduleSessionBeanRemote.getFlightScheduleById(recurrentFlightScheduleId);                                  
                                            
                                            Long difference_In_Time = formattedEndDate.getTime() - formattedStartDate.getTime();
                                            Long days = TimeUnit.MILLISECONDS.toDays(difference_In_Time) % 365;
                                            Integer numOfTimes = days.intValue()/dayInterval;
                                            
                                            int interval = 0;

                                            for(int i = 0; i < numOfTimes; i++)
                                            {
                                                createRecurrentFlightSchedule(flightSchedulePlanId, flight, startDate,  startTime, flightDuration, interval, i);
                                                interval += dayInterval;
                                            }
                                           
                                            if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) {
                                                System.out.println("\nDo you want to create a complementary return flight schedule plan for recurrent flight schedule?");
                                                while (true) {
                                                    Integer response = 0;

                                                    System.out.println("1. Yes");
                                                    System.out.println("2. No");
                                                    System.out.print("> ");
                                                    response = scanner.nextInt();
                                                    scanner.nextLine();

                                                    if (response >= 1 && response <= 2) 
                                                    {
                                                        if (response == 1) 
                                                        {
                                                            System.out.print(" Enter Layover Duration(hrs)> ");
                                                            Double layoverDuration = scanner.nextDouble();
                                                            
                                                            for(int i = 0; i < numOfTimes; i++)
                                                            {
                                                                createRecurrentReturnFlightSchedule(flightSchedulePlanId, flightScheduleId, layoverDuration);
                                                            }
                                                        }
                                                    }
                                                }   
                                            }
                                            System.out.println("Recurrent Flight Schedules created successfully!\n");
                                        } 
                                        catch (ParseException ex) 
                                        {
                                            System.out.println("End date is in the wrong format");
                                        } 
                                        
                                        break;
                                    }
                                    else if(choice == 2)
                                    {
                                        break;
                                    }
                                }
                                
                            }
                            break;
                        }
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
            System.out.println("An error has occurred while creating the new flight schedule!: The flight does not exist!\n");
        }
        
    }
    
    private Long doCreateEveryFlightSchedules(Long flightSchedulePlanId, Flight flight, String flightSchedulePlanType)
    {
        Scanner scanner = new Scanner(System.in);

        FlightSchedule flightSchedule = new FlightSchedule();
        Long flightScheduleId = 0l;

        while (true) 
        {
            try 
            {
                System.out.print("Enter Departure Date(dd-mm-yyyy)> ");
                String departureDate = scanner.nextLine();
                System.out.print("Enter Departure Time(HH:mm)> ");
                String departureTime = scanner.nextLine();
                System.out.print("Enter Flight Duration(hrs)> ");
                Double flightDuration = scanner.nextDouble();

                FlightSchedulePlan flightSchedulePlanFromDb = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);

                String departureDateTimeInString = departureDate + " " + departureTime;
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date formattedDT = formatter.parse(departureDateTimeInString);

                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(formattedDT);
                //to add timediff to departure time 
                calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlanFromDb.getFlight().getFlightRoute().getOrigin().getTimeZoneDiff());
                Date departureDT = formatter.parse(formatter.format(calendar.getTime()));

                Integer flightDurationInMins = (int)(flightDuration*60);
                Integer flightDurationHours = flightDurationInMins/60;
                Integer flightDurationMins = flightDurationInMins%60;
                
                flightSchedule.setDepartureDateTime(departureDT);
                flightSchedule.setFlightHours(flightDurationHours);
                flightSchedule.setFlightMinutes(flightDurationMins);

                //create seat inventory 
                SeatInventory seatInventory = new SeatInventory();
                //get max seat capacity from aircraft config
                Integer totalMaxSeatCapacity = flightSchedulePlanFromDb.getFlight().getAircraftConfig().getMaxSeatCapacity();
                seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacity);
                seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacity);
                seatInventory.setNumOfReservedSeats(0);

                Long seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory);
                flightScheduleId = flightScheduleSessionBeanRemote.createNewFlightSchedule(flightSchedule, flightSchedulePlanId, seatInventoryId);
                
                //if this flight has return flight
                if (flight.getReturnFlight().getFlightId() != flight.getFlightId()) {
                    System.out.println("Do you want to create a complementary return flight schedule plan?");
                    while (true) {
                        Integer choice = 0;

                        System.out.println("1. Yes");
                        System.out.println("2. No");
                        System.out.print("> ");
                        choice = scanner.nextInt();
                        scanner.nextLine();

                        if (choice >= 1 && choice <= 2) {
                            if (choice == 1) {
                                try {
                                    System.out.print("Enter Layover Duration(hrs)> ");
                                    Double layoverDuration = scanner.nextDouble();
                                    Integer layoverDurationInMins = (int) (layoverDuration * 60);
                                    Integer layoverDurationHours = layoverDurationInMins / 60;
                                    Integer layoverDurationMins = layoverDurationInMins % 60;

                                    FlightSchedule flightScheduleFromDb = flightScheduleSessionBeanRemote.getFlightScheduleById(flightScheduleId);
                                    Date outboundFlightDepartureDT = flightScheduleFromDb.getDepartureDateTime();
                                    Integer outboundFlightDurationHours = flightScheduleFromDb.getFlightHours();
                                    Integer outboundFlightDurationMins = flightScheduleFromDb.getFlightMinutes();

                                    calendar.setTime(outboundFlightDepartureDT);
                                    //to add flight duration to departure date time = arrival time
                                    calendar.add(GregorianCalendar.HOUR_OF_DAY, outboundFlightDurationHours);
                                    calendar.add(GregorianCalendar.MINUTE, outboundFlightDurationMins);
                                    Date outboundFlightArrivalDT = formatter.parse(formatter.format(calendar.getTime()));

                                    //to add in time diff (arrival time zone)
                                    calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlanFromDb.getFlight().getFlightRoute().getDestination().getTimeZoneDiff());
                                    Date arrivalDT = formatter.parse(formatter.format(calendar.getTime()));
                                    calendar.setTime(arrivalDT);

                                    //to add in layover duration
                                    calendar.add(GregorianCalendar.HOUR_OF_DAY, layoverDurationHours);
                                    calendar.add(GregorianCalendar.MINUTE, layoverDurationMins);
                                    Date returnFlightDepartureDT = formatter.parse(formatter.format(calendar.getTime()));

                                    //create seat inventory 
                                    SeatInventory returnSeatInventory = new SeatInventory();
                                    //get max seat capacity from aircraft config
                                    Integer totalMaxSeatCapacityReturn = flightSchedulePlanFromDb.getFlight().getReturnFlight().getAircraftConfig().getMaxSeatCapacity();
                                    returnSeatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityReturn);
                                    returnSeatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityReturn);
                                    returnSeatInventory.setNumOfReservedSeats(0);

                                    Long returnSeatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(returnSeatInventory);

                                    FlightSchedule flightScheduleForReturnFlight = new FlightSchedule();
                                    flightScheduleForReturnFlight.setDepartureDateTime(returnFlightDepartureDT);
                                    flightScheduleForReturnFlight.setFlightHours(flightScheduleFromDb.getFlightHours());
                                    flightScheduleForReturnFlight.setFlightMinutes(flightScheduleFromDb.getFlightMinutes());

                                    FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();
                                    if(flightSchedulePlanType.equals("SINGLE"))
                                    {
                                        flightSchedulePlanForReturnFlight.setFlightScheduleType("SINGLE");
                                    }
                                    else if(flightSchedulePlanType.equals("MULTIPLE"))
                                    {
                                        flightSchedulePlanForReturnFlight.setFlightScheduleType("MULTIPLE");
                                    }
                                    
                                    flightSchedulePlanForReturnFlight.setEndDate(null);
                                    flightSchedulePlanForReturnFlight.setIntervalDays(0);

                                    Long returnFlightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewReturnFlightSchedulePlan(flightSchedulePlanForReturnFlight, flightSchedulePlanId);
                                    Long returnFlightScheduleId = flightScheduleSessionBeanRemote.createNewReturnFlightSchedule(flightScheduleForReturnFlight, flightScheduleId, returnFlightSchedulePlanId, seatInventoryId);
                                    
                                    System.out.println("Return Flight Schedule Plan created successfully!: " + returnFlightSchedulePlanId);
                                    break;
                                } 
                                catch (ParseException ex) {
                                    System.out.println("Arrival Time is in wrong format!\n");
                                } 
                                catch (FlightScheduleNotFoundException | FlightSchedulePlanNotFoundException ex) {
                                    System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
                                }
                                catch (FlightNotFoundException ex) {
                                    System.out.println("An error has occurred while retrieving flight: " + ex.getMessage() + "\n");
                                }
                            } else if (choice == 2) {
                                break;
                            }
                        }
                    }
                }

                break;

            } 
            catch (ParseException ex) 
            {
                System.out.println("Departure Time is in wrong format!\n");
            } 
            catch (FlightSchedulePlanNotFoundException ex) 
            {
                System.out.println("An error has occurred: " + ex.getMessage() + "!\n");
            }
        }
        
        return flightScheduleId;
    }
    
    
    private Long createRecurrentFlightSchedule(Long flightSchedulePlanId, Flight flight, String departureDate, String departureTime, Double flightDuration, Integer interval, Integer i)
    {
        Scanner scanner = new Scanner(System.in);
        Long flightScheduleId = 0l;
        try
        {
            FlightSchedulePlan flightSchedulePlanFromDb = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);
            FlightSchedule flightSchedule = new FlightSchedule();

            String departureDateTimeInString = departureDate + " " + departureTime;
            SimpleDateFormat formatterDeparture = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date formattedDepartureDT = formatterDeparture.parse(departureDateTimeInString);

            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(formattedDepartureDT);
            //to add timediff to departure time 
            calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlanFromDb.getFlight().getFlightRoute().getOrigin().getTimeZoneDiff());
            calendar.add(GregorianCalendar.DATE, interval);
            Date departureDT = formatterDeparture.parse(formatterDeparture.format(calendar.getTime()));

            Integer flightDurationInMins = (int) (flightDuration * 60);
            Integer flightDurationHours = flightDurationInMins / 60;
            Integer flightDurationMins = flightDurationInMins % 60;

            flightSchedule.setDepartureDateTime(departureDT);
            flightSchedule.setFlightHours(flightDurationHours);
            flightSchedule.setFlightMinutes(flightDurationMins);

            //create seat inventory 
            SeatInventory seatInventory = new SeatInventory();
            //get max seat capacity from aircraft config
            Integer totalMaxSeatCapacity = flightSchedulePlanFromDb.getFlight().getAircraftConfig().getMaxSeatCapacity();
            seatInventory.setNumOfAvailableSeats(totalMaxSeatCapacity);
            seatInventory.setNumOfBalanceSeats(totalMaxSeatCapacity);
            seatInventory.setNumOfReservedSeats(0);
            
            Long seatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(seatInventory);
            flightScheduleId = flightScheduleSessionBeanRemote.createNewFlightSchedule(flightSchedule, flightSchedulePlanId, seatInventoryId);
                


        } 
        catch (FlightSchedulePlanNotFoundException ex) 
        {
            System.out.println("An error has occurred while creating flight schedule: flight schedule plan does not exist!\n");
        } 
        catch (ParseException ex) 
        {
            System.out.println("Departure Date Time is in wrong format!\n");
        }
        
        return flightScheduleId;
        
    }
    
    private Long createRecurrentReturnFlightSchedule(Long flightSchedulePlanId, Long flightScheduleId, Double layoverDuration){
                    
        Scanner scanner = new Scanner(System.in);
        Long returnFlightSchedulePlanId = 0l;
        try{
            GregorianCalendar calendar = new GregorianCalendar();
            Integer layoverDurationInMins = (int) (layoverDuration * 60);
            Integer layoverDurationHours = layoverDurationInMins / 60;
            Integer layoverDurationMins = layoverDurationInMins % 60;

            FlightSchedule flightScheduleFromDb = flightScheduleSessionBeanRemote.getFlightScheduleById(flightScheduleId);
            Date outboundFlightDepartureDT = flightScheduleFromDb.getDepartureDateTime();
            Integer outboundFlightDurationHours = flightScheduleFromDb.getFlightHours();
            Integer outboundFlightDurationMins = flightScheduleFromDb.getFlightMinutes();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            calendar.setTime(outboundFlightDepartureDT);
            //to add flight duration to departure date time = arrival time
            calendar.add(GregorianCalendar.HOUR_OF_DAY, outboundFlightDurationHours);
            calendar.add(GregorianCalendar.MINUTE, outboundFlightDurationMins);
            Date outboundFlightArrivalDT = formatter.parse(formatter.format(calendar.getTime()));

            //to add in time diff (arrival time zone)
            FlightSchedulePlan flightSchedulePlanFromDb = flightSchedulePlanSessionBeanRemote.getFlightSchedulePlanById(flightSchedulePlanId);
            calendar.add(GregorianCalendar.HOUR_OF_DAY, flightSchedulePlanFromDb.getReturnFlightSchedulePlan().getFlight().getFlightRoute().getDestination().getTimeZoneDiff());
            Date arrivalDT = formatter.parse(formatter.format(calendar.getTime()));
            calendar.setTime(arrivalDT);

            //to add in layover duration
            calendar.add(GregorianCalendar.HOUR_OF_DAY, layoverDurationHours);
            calendar.add(GregorianCalendar.MINUTE, layoverDurationMins);
            Date returnFlightDepartureDT = formatter.parse(formatter.format(calendar.getTime()));

            //create seat inventory 
            SeatInventory returnSeatInventory = new SeatInventory();
            //get max seat capacity from aircraft config
            Integer totalMaxSeatCapacityReturn = flightSchedulePlanFromDb.getReturnFlightSchedulePlan().getFlight().getReturnFlight().getAircraftConfig().getMaxSeatCapacity();
            returnSeatInventory.setNumOfAvailableSeats(totalMaxSeatCapacityReturn);
            returnSeatInventory.setNumOfBalanceSeats(totalMaxSeatCapacityReturn);
            returnSeatInventory.setNumOfReservedSeats(0);

            Long returnSeatInventoryId = seatinventorySessionBeanRemote.createSeatInventory(returnSeatInventory);

            FlightSchedule flightScheduleForReturnFlight = new FlightSchedule();
            flightScheduleForReturnFlight.setDepartureDateTime(returnFlightDepartureDT);
            flightScheduleForReturnFlight.setFlightHours(flightScheduleFromDb.getFlightHours());
            flightScheduleForReturnFlight.setFlightMinutes(flightScheduleFromDb.getFlightMinutes());

            FlightSchedulePlan flightSchedulePlanForReturnFlight = new FlightSchedulePlan();

            flightSchedulePlanForReturnFlight.setFlightScheduleType("RECURRENT");

            flightSchedulePlanForReturnFlight.setEndDate(flightSchedulePlanFromDb.getEndDate());
            flightSchedulePlanForReturnFlight.setIntervalDays(flightSchedulePlanFromDb.getIntervalDays());

            returnFlightSchedulePlanId = flightSchedulePlanSessionBeanRemote.createNewReturnFlightSchedulePlan(flightSchedulePlanForReturnFlight, flightSchedulePlanId);
            Long returnFlightScheduleId = flightScheduleSessionBeanRemote.createNewReturnFlightSchedule(flightScheduleForReturnFlight, flightScheduleId, returnFlightSchedulePlanId, returnSeatInventoryId);
            
        } catch (ParseException ex) {
            System.out.println("Arrival Time is in wrong format!\n");
        } catch (FlightScheduleNotFoundException | FlightSchedulePlanNotFoundException ex) {
            System.out.println("An error has occurred while retrieving flight schedule: " + ex.getMessage() + "\n");
        } catch (FlightNotFoundException ex) {
            System.out.println("An error has occurred while retrieving flight: " + ex.getMessage() + "\n");
        }

        return returnFlightSchedulePlanId;

       
    }
    
    private void doViewAllFlightSchedulePlans()
    {
        
    }
    
    private void doViewFlightSchedulePlanDetails()
    {
        
    }
    
    private void doUpdateFlightSchedulePlan()
    {
        
    }
    
    private void doDeleteFlightSchedulePlan()
    {
        
    }
}
