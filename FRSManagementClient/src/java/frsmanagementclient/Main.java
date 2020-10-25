/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsmanagementclient;

import ejb.session.stateless.AircraftTypeSessionBeanRemote;
import ejb.session.stateless.AirportSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author seowtengng
 */
public class Main {

    @EJB
    private static PartnerSessionBeanRemote partnerSessionBeanRemote;
    @EJB
    private static AirportSessionBeanRemote airportSessionBeanRemote;
    @EJB
    private static AircraftTypeSessionBeanRemote aircraftTypeSessionBeanRemote;
    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Testing");
        MainApp mainApp = new MainApp(partnerSessionBeanRemote, airportSessionBeanRemote, aircraftTypeSessionBeanRemote, employeeSessionBeanRemote);
        mainApp.runApp();
    }
    
}
