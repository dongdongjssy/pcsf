/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author dongdong
 * @version 1.0
 * @createDate Aug 8, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebService
public interface ScheduleNCoordinateService {

	/**
	 * Deploy a process
	 * 
	 * @return process deployment id
	 */
	@WebMethod
	public void deployProcess(String collaborationId);

	/**
	 * Run collaboration
	 * 
	 * @param collaborationId
	 */
	@WebMethod
	public void runCollaboration(String collaborationId, String processDefId);

	/**
	 * Get the information of process definition
	 * 
	 * @param collaborationId
	 * @return process definitions information
	 */
	@WebMethod
	public String[] getProcessDefintions(String collaborationId);

	/**
	 * Get the information of process instances
	 * 
	 * @param collaborationId
	 * @return process instances information
	 */
	@WebMethod
	public String[] getProcessInstances(String collaborationId);

	/**
	 * Get task list of certain user
	 * 
	 * @param uesername
	 * @return task of certain user
	 */
	@WebMethod
	public String[] getTask(String uesername, String collaborationId);

	/**
	 * Get current task
	 * 
	 * @param collaborationId
	 * @return current task
	 */
	@WebMethod
	public String[] getCurrentTask(String collaborationId);

	/**
	 * Submit a task
	 * 
	 * @param taskId
	 * @param collaborationId
	 */
	@WebMethod
	public void submitTask(String taskId, String collaborationId);

}
