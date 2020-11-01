/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frsreservationclient;

import ejb.session.stateless.CustomerSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author seowtengng
 */
public class Main {

    @EJB
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    
    
    public static void main(String[] args) {
        
        MainApp mainApp = new MainApp(customerSessionBeanRemote);
        mainApp.runApp();
    }    
}
