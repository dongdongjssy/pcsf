/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.crt;

import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author dongdong
 * @version 1.0
 * @createDate Jul 12, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebService
public interface CreationService {
  /**
   * Create a new collaboration.
   * 
   * @param collaborationName
   * @param participants
   * @param creatorId
   * @param workflow
   * @return a new collaboration
   */
  @WebMethod
  public boolean createCollaboration(String collaborationName, String creatorId, File workflowFile);

  /**
   * Deploy a collaboration and generate a set of services for it.
   * 
   * @param collaborationId
   */
  @WebMethod
  public boolean deployCollaboration(String collaborationId);

  /**
   * Delete a collaboration.
   * 
   * @param collaborationId
   */
  @WebMethod
  public boolean deleteCollaboration(String collaborationId);

  /**
   * Delete a collaboration instance.
   * 
   * @param collaborationId
   */
  @WebMethod
  public boolean deleteInstance(String collaborationId);
}
