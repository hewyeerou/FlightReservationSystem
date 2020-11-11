/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.CabinClass;
import entity.Employee;
import entity.Flight;
import entity.FlightReservationRecord;
import entity.FlightSchedule;
import entity.FlightSchedulePlan;
import entity.SeatInventory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.enumeration.UserRoleEnum;
import util.exception.FlightNotFoundException;
import util.exception.InvalidAccessRightsException;

/**
 *
 * @author yeerouhew
 */
public class SalesManagementModule {
    

    private Employee currentEmployee;
    private FlightSessionBeanRemote flightSessionBeanRemote;
    

    public SalesManagementModule() {
    }
    
    public SalesManagementModule(Employee currentEmployee, FlightSessionBeanRemote flightSessionBeanRemote) 
    {
        this();
        this.currentEmployee = currentEmployee;
        this.flightSessionBeanRemote = flightSessionBeanRemote;
    }
    
    public void menuSalesManagement() throws InvalidAccessRightsException
    {
        if(currentEmployee.getUserRoleEnum() != UserRoleEnum.SALES_MANAGER && currentEmployee.getUserRoleEnum() != UserRoleEnum.SYSTEM_ADMIN)
        {
            throw new InvalidAccessRightsException("You don't have the rights to access sales management module.");
        }
        
        Scanner scanner = new Scanner(System.in);
        Integer response = 0;
        
        while(true)
        {
            System.out.println("*** FRS Management :: Sales Management ***\n");
            System.out.println("1: View Seat Inventory");
            System.out.println("2: View Flight Reservations");
            System.out.println("3: Back\n");
            response = 0;
            
            while(response < 1 || response > 2)
            {
                System.out.print("> ");

                response = scanner.nextInt();

                if(response == 1)
                {
                    doViewSeatInventory();
                }
                else if(response == 2)
                {
                    doViewFlightReservations();
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
    
    private void doViewSeatInventory()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** FRS Management :: Sales Management :: List of Flight Schedules ***\n");
        System.out.print("Enter Flight Number> ");
        String flightNum = scanner.nextLine();
        
        Integer option = 0;
        Integer flightScheduleInt = 0;
        
        SimpleDateFormat formatterDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        
        while(true)
        {
            try 
            {
                Flight flight = flightSessionBeanRemote.getFlightByFlightNum(flightNum);

                System.out.println("List of Flight Schedules: ");

                List<FlightSchedule> flightSchedulesList = new ArrayList<>();

                for(FlightSchedulePlan flightSchedulePlan: flight.getFlightSchedulePlans())
                {
                    for(FlightSchedule flightSchedule: flightSchedulePlan.getFlightSchedules())
                    {
                        if(flightSchedule.getEnabled() == true)
                        {
                            flightSchedulesList.add(flightSchedule);
                        }
                    }
                }

                for(FlightSchedule flightSchedule: flightSchedulesList)
                {
                    option++;
                    System.out.println(option + "ID" + flightSchedule.getFlightScheduleId() + " - " + formatterDateTime.format(flightSchedule.getDepartureDateTime()));
                }

                System.out.println("");
                System.out.print("Select Flight Schedule to View Seat> ");
                flightScheduleInt = scanner.nextInt();
                scanner.nextLine();

                if(flightScheduleInt >= 1 && flightScheduleInt <= option)
                {
                    FlightSchedule flightSchedule = flightSchedulesList.get(flightScheduleInt-1);
                    doViewFlightScheduleSeat(flightSchedule, flight);
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again! \n");
                }

            } 
            catch (FlightNotFoundException ex) 
            {
                System.out.println("An error has occurred while creating flight: flight does not exist!\n");
            } 
        }
    }
    
    private void doViewFlightScheduleSeat(FlightSchedule flightSchedule, Flight flight)
    {
        Scanner scanner = new Scanner(System.in);
        Integer availableSeat = 0;
        Integer reservedSeat = 0;
        Integer balanceSeat = 0;
        System.out.println("*** FRS Management :: Sales Management :: View Seat Inventory ***\n");
        System.out.println("*** Flight Schedule: " + flightSchedule.getFlightScheduleId() + " ***\n");
        System.out.println("");
        System.out.printf("%25s%25s%25s%25s\n", "Cabin Class", "Available Seat", "Reserved Seat", "Balance Seat");
        
        for(SeatInventory seatInventory: flightSchedule.getSeatInventories())
        {
            System.out.printf("%25s%25s%25s%25s\n", seatInventory.getCabinClass().getCabinClassType().toString(), seatInventory.getNumOfAvailableSeats(), seatInventory.getNumOfReservedSeats(), seatInventory.getNumOfBalanceSeats());
            availableSeat += seatInventory.getNumOfAvailableSeats();
            reservedSeat += seatInventory.getNumOfReservedSeats();
            balanceSeat += seatInventory.getNumOfBalanceSeats();
        }
        
        System.out.printf("\nTotal Number of Available Seats: %d, Total Number of Reserved Seats: %d, Total Number of Balance Seats: %d\n", availableSeat, reservedSeat, balanceSeat);
        System.out.println("------------------------"); 
    }
    
    private void doViewFlightReservations()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("*** FRS Management :: Sales Management :: List of Flight Schedules ***\n");
        System.out.print("Enter Flight Number> ");
        String flightNum = scanner.nextLine();
        
        Integer option = 0;
        Integer flightScheduleInt = 0;
        String fareBasisCode = "";
        
        while(true)
        {
            try 
            {
                Flight flight = flightSessionBeanRemote.getFlightByFlightNum(flightNum);

                System.out.println("List of Flight Schedules: ");

                List<FlightSchedule> flightSchedulesList = new ArrayList<>();

                for(FlightSchedulePlan flightSchedulePlan: flight.getFlightSchedulePlans())
                {
                    for(FlightSchedule flightSchedule: flightSchedulePlan.getFlightSchedules())
                    {
                        if(flightSchedule.getEnabled() == true)
                        {
                            flightSchedulesList.add(flightSchedule);
                        }
                    }
                }
                
                for(FlightSchedule flightSchedule: flightSchedulesList)
                {
                    option++;
                    System.out.println(option + "ID" );
                }

                System.out.println("");
                System.out.print("Select Flight Schedule to View Flight Reservation> ");
                flightScheduleInt = scanner.nextInt();
                scanner.nextLine();
                
                if(flightScheduleInt >= 1 && flightScheduleInt <= option)
                {
                    FlightSchedule flightSchedule = flightSchedulesList.get(flightScheduleInt-1);
                    doViewFlightScheduleReservation(flightSchedule, flight);
                    break;
                }
                else
                {
                    System.out.println("Invalid option, please try again! \n");
                }
                
            }
            catch (FlightNotFoundException ex) 
            {
                System.out.println("An error has occurred while creating flight: flight does not exist!\n");
            }
        }
    }
    
    private void doViewFlightScheduleReservation(FlightSchedule flightSchedule, Flight flight)
    {
        System.out.println("*** FRS Management :: Sales Management :: View Flight Reservation Record ***\n");
        System.out.println("*** Flight Schedule: " + flightSchedule.getFlightScheduleId() + " ***\n");
        System.out.println("");
        
        List<CabinClass> cabinClasses = new ArrayList<>();
        
        for(SeatInventory seatInventory: flightSchedule.getSeatInventories())
        {
            cabinClasses.add(seatInventory.getCabinClass());
        }
        
        for(CabinClass cabinClass: cabinClasses)
        {
            System.out.printf("%25s%25s%25s%25s\n", "Seat Number", "Passenger Name", "Fare Basis Code");
            
            for(FlightReservationRecord flightReservationRecord: flightSchedule.getFlightReservationRecords())
            {
                //for(loop through fare)??
                System.out.printf("%25s%25s%25s%25s\n", "SEAT NUMBER", flightReservationRecord.getPassengers(), "FARE BASIS CODE");
            }
        }
        
        
    }
}
