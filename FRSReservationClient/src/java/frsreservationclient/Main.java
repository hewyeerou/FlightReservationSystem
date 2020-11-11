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
import ejb.session.stateless.FlightRouteSessionBeanRemote;
import ejb.session.stateless.FlightScheduleSessionBeanRemote;
import javax.ejb.EJB;
import ejb.session.stateless.SeatInventorySessionBeanRemote;

/**
 *
 * @author seowtengng
 */
public class Main {

    @EJB
    private static SeatInventorySessionBeanRemote seatInventorySessionBeanRemote;
    @EJB
    private static CabinClassSessionBeanRemote cabinClassSessionBeanRemote;
    @EJB
    private static FareSessionBeanRemote fareSessionBeanRemote;
    @EJB
    private static FlightScheduleSessionBeanRemote flightScheduleSessionBeanRemote;
    @EJB
    private static AirportSessionBeanRemote airportSessionBeanRemote;
    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    
    
    
    
    public static void main(String[] args) {
        
        MainApp mainApp = new MainApp(customerSessionBeanRemote, airportSessionBeanRemote, flightScheduleSessionBeanRemote, fareSessionBeanRemote, cabinClassSessionBeanRemote, seatInventorySessionBeanRemote);
        mainApp.runApp();
    }    
}
