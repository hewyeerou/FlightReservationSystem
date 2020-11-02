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
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.AircraftConfig;
import entity.CabinClass;
import entity.Employee;
import entity.Flight;
import entity.FlightRoute;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.UserRoleEnum;
import util.exception.FlightNotFoundException;
import util.exception.FlightNumExistException;
import util.exception.FlightRouteNotFoundException;
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

    private Employee currentEmployee;
    
    public FlightOperationModule() 
    {
    }
    
    public FlightOperationModule(Employee currentEmployee, FlightSessionBeanRemote flightSessionBean, FlightRouteSessionBeanRemote flightRouteSessionBeanRemote, AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote )
    {
        this();
        this.currentEmployee  = currentEmployee;
        this.flightSessionBeanRemote = flightSessionBean;
        this.flightRouteSessionBeanRemote = flightRouteSessionBeanRemote;
        this.aircraftConfigSessionBeanRemote = aircraftConfigSessionBeanRemote;
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
            System.out.println("4: Update Flight");
            System.out.println("5: Delete Flight");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
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
                    doUpdateFlight();
                }
                else if(response == 5)
                {
                    doDeleteFlight();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if(response == 6)
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
            System.out.println("4: Update Flight Schedule Plan");
            System.out.println("5: Delete Flight Schedule Plan");
            System.out.println("6: Back\n");
            response = 0;
            
            while(response < 1 || response > 5)
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
                    doUpdateFlightSchedulePlan();
                }
                else if(response == 5)
                {
                    doDeleteFlightSchedulePlan();
                }
                else if(response == 6)
                {
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if(response == 6)
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
        
        List<FlightRoute> filteredFlightRoutes = new ArrayList<>();
       
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
            
            System.out.println("List of flight routes: ");
            for(FlightRoute flightRoute: flightRoutes)
            {
                if(flightRoute.getEnabled() == true)
                {
                    option++;
                    System.out.println(option + ": " + flightRoute.getOrigin().getIataCode() + " - " + flightRoute.getDestination().getIataCode());
                    filteredFlightRoutes.add(flightRoute);
                }
            }
            
            System.out.println("");
            System.out.print("Select Flight Route> ");
            flightRouteInt = scanner.nextInt();
            scanner.nextLine();
            
            if(flightRouteInt >= 1 && flightRouteInt <= option)
            {
                flightRouteId = filteredFlightRoutes.get(flightRouteInt-1).getFlightRouteId();
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
            
            System.out.print("Press any key to continue...> ");
            scanner.nextLine();
        }
        catch(FlightNotFoundException ex)
        {
            System.out.println("An error has occurred while retrieving flight details: " + ex.getMessage() + "\n");
        }  
    }
    
    private void doUpdateFlight()
    {
        try
        {
            Scanner scanner = new Scanner(System.in);
            System.out.println("*** FRS Management :: Flight Operation :: Flight :: Update Flight ***\n");
            System.out.print("Enter Flight Number> ");
            String flightNumber = scanner.nextLine();

            System.out.println("");

            Flight flight = flightSessionBeanRemote.getFlightByFlightNum(flightNumber);
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
                    }
                    else if(option == 2)
                    {
                        System.out.println("Current Aircraft Configuration: " + flight.getAircraftConfig()+ "\n");
                        
                        aircraftConfigId = updateAircraftConfig(aircraftConfigs);
                        AircraftConfig aircraftConfig = aircraftConfigSessionBeanRemote.retrieveAircraftConfigById(aircraftConfigId);
                        flight.setAircraftConfig(aircraftConfig);
                        flightSessionBeanRemote.updateFlight(flight);
                        System.out.println("Flight updated successfully! \n");
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
    
    private void doDeleteFlight()
    {
        try{
            Scanner scanner = new Scanner(System.in);

            List<Flight> flights = flightSessionBeanRemote.getAllFlights();
            List<Flight> sortedFlights = new ArrayList<>();

            Integer flightInt = 0;
            Long flightId = 0l;
            Long flightIdAssociatedWithReturnFlight = 0l;

            System.out.println("*** FRS Management :: Flight Operation :: Flight :: Delete Flight ***\n");


            for(Flight flight: flights)
            {
                if(flight.getFlightType().equals("OUTBOUND"))
                {
                    sortedFlights.add(flight);

                    if(flight.getReturnFlight().getFlightId() != flight.getFlightId())
                    {
                        sortedFlights.add(flight.getReturnFlight());
                    }
                }
                else if(flight.getFlightType().equals("RETURN"))
                {
                    if(!sortedFlights.contains(flight))
                    {
                        sortedFlights.add(flight);
                    }
                }
            }

            while(true)
            {
                Integer option = 0;

                System.out.println("List of Flights: ");
                for(Flight sortedFlight: sortedFlights)
                {
                    option++;
                    System.out.println(option + ": " + sortedFlight.getFlightNumber());
                }

                System.out.println("");
                System.out.print("Select a Flight to remove> ");
                flightInt = scanner.nextInt();
                scanner.nextLine();

                if(flightInt >=1 && flightInt <= option)
                {
                    flightId = sortedFlights.get(flightInt-1).getFlightId();
                    Flight flight = flightSessionBeanRemote.getFlightById(flightId);

                    if(flight.getFlightSchedulePlans().isEmpty())
                    {
                        if(flight.getFlightType().equals("OUTBOUND"))
                        {
                            flightSessionBeanRemote.removeFlight(flightId);
                            System.out.println("Existing flight removed successfully!\n");
                        }

                        else if(flight.getFlightType().equals("RETURN"))
                        {
                            for(Flight flightWithReturnFlightToRemove: sortedFlights)
                            {
                                //if return flight route and there is outbound flight route associated to the return flight route   
                                if(flightWithReturnFlightToRemove.getFlightType().equals("OUTBOUND") && flightWithReturnFlightToRemove.getReturnFlight().getFlightId().equals(flightId))
                                {
                                    flightIdAssociatedWithReturnFlight = flightWithReturnFlightToRemove.getFlightId();
                                }
                            }
                            flightSessionBeanRemote.removeReturnFlight(flightId, flightIdAssociatedWithReturnFlight);
                            System.out.println("Existing flight removed successfully!\n");
                        }   
                    }
                    else if(!flight.getFlightSchedulePlans().isEmpty())
                    {
                        flightSessionBeanRemote.setFlightDisabled(flightId);
                        System.out.println("Existing flight has been set to disabled!\n");
                    }
                    break;
                }  
                else
                {
                    System.out.println("Invalid option, please try again! \n");
                }
            }
        } 
        catch (FlightNotFoundException ex) 
        {
            System.out.println("An error has occurred while removing flight record: " + ex.getMessage() + "!\n");
        }
        
    }
    
    private void doCreateFlightSchedulePlan()
    {
        
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
