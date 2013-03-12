/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.img;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author ddong
 * @version 1.0
 * @createDate Dec. 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService
public interface InstanceManagementService {
  /**
   * Run a collaboration instance
   * 
   * @param collaborationId
   */
  @WebMethod
  public void runInstance(String collaborationId);

  /**
   * Stop a collaboration instance
   * 
   * @param collaborationId
   */
  @WebMethod
  public void stopInstance(String collaborationId);
}
