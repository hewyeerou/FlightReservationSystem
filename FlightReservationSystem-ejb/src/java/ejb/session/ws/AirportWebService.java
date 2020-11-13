/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.AirportSessionBeanLocal;
import entity.Airport;
import entity.FlightRoute;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author seowtengng
 */
@WebService(serviceName = "AirportWebService")
@Stateless()
public class AirportWebService {

    @EJB
    private AirportSessionBeanLocal airportSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "getAllAirportsUnmanaged")
    public List<Airport> getAllAirportsUnmanaged () {
        
        List<Airport> airports = airportSessionBeanLocal.getAllAirportsUnmanaged();
        
        for (Airport airport: airports)
        {
            for (FlightRoute fr: airport.getDepartureRoutes())
            {
                fr.setOrigin(null);
            }
            
            for (FlightRoute fr: airport.getArrivalRoutes())
            {
                fr.setDestination(null);
            }
        }
        
        return airports;
    }
}
