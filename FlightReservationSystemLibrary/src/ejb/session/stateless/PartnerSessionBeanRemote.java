/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Remote;
import util.exception.PartnerUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author seowtengng
 */
@Remote
public interface PartnerSessionBeanRemote {
    
    public Long createNewPartner(Partner newPartner) throws PartnerUsernameExistException, UnknownPersistenceException;
    
}
