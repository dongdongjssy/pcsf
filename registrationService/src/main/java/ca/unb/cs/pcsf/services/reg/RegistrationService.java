/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.reg;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 13, 2012
 * @email dong.dong@unb.ca
 */
@WebService
public interface RegistrationService {
  /**
   * Register the participant into the collaboration.
   * 
   * @param participantId
   */
  @WebMethod
  public void setAsReg(String participantId);
}
