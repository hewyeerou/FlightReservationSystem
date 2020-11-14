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
import ejb.session.stateless.FlightReservationRecordSessionBeanRemote;
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightSchedulePlanSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import ejb.session.stateless.FlightSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import javax.ejb.EJB;
import ejb.session.stateless.SeatInventorySessionBeanRemote;

/**
 *
 * @author seowtengng
 */
public class Main {

    @EJB
    private static FlightReservationRecordSessionBeanRemote flightReservationRecordSessionBeanRemote;
    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;
    @EJB
    private static AircraftConfigSessionBeanRemote aircraftConfigSessionBeanRemote;
    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;
    @EJB
    private static AirportSessionBeanRemote airportSessionBeanRemote;
    @EJB
    private static AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    @EJB
    private static FlightRouteSessionBeanRemote flightRouteSessionBeanRemote;
    @EJB
    private static FlightSessionBeanRemote flightSessionBeanRemote;
    @EJB
    private static FlightSchedulePlanSessionBeanRemote flightSchedulePlanSessionBeanRemote;
    @EJB
    private static FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    @EJB
    private static SeatInventorySessionBeanRemote seatinventorySessionBeanRemote;
    @EJB
    private static FareSessionBeanRemote fareSessionBeanRemote;
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(partnerSessionBeanRemote, airportSessionBeanRemote, aircraftTypeSessionBeanRemote, employeeSessionBeanRemote, aircraftConfigSessionBeanRemote, flightRouteSessionBeanRemote, flightSessionBeanRemote, flightSchedulePlanSessionBeanRemote, flightScheduleSessionBeanRemote, seatinventorySessionBeanRemote, fareSessionBeanRemote, flightReservationRecordSessionBeanRemote);
        mainApp.runApp();
    }
    
}
