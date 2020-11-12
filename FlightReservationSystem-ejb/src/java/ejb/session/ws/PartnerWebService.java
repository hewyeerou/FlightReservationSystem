/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author yeerouhew
 */
@WebService(serviceName = "PartnerWebService")
@Stateless
public class PartnerWebService {

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    
    @WebMethod(operationName = "retrievePartnerByUsername")
    public Partner retrievePartnerByUsername(@WebParam(name = "username") String username) throws PartnerNotFoundException
    {
        Partner partner = partnerSessionBeanLocal.retrievePartnerByUsername(username);
        return partner;
    }
    
    @WebMethod(operationName = "")
    public Partner partnerLogin(@WebParam(name = "username") String username,
                                @WebParam(name = "password") String password) 
                                throws PartnerNotFoundException, InvalidLoginCredentialException
    {
        Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
        return partner;
    }
    
   
}
