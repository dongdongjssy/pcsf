/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.mnc;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService
public interface MonitorNControlService {
  /**
   * Start monitor and control service
   */
  @WebMethod
  public void startMonitor();

  /**
   * End monitor and control service
   */
  @WebMethod
  public void endMonitor();
}
