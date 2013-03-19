/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc.engine;

import java.util.List;

import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.task.Task;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 6, 2012
 * @email dong.dong@unb.ca
 */
public interface Engine {
  public static final String LOGPRE = ">>>>";
  public static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";

  public static final String DOMAIN_COLLABORATION = "Collaboration";
  public static final String COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID = "ProcessDefId";
  public static final String COLLABORATION_ATTRIBUTE_CURRENT_STATE = "CurrentState";
  public static final String COLLABORATION_STATE_RUNNING = "running";
  public static final String COLLABORATION_STATE_STOP = "stop";
  public static final String COLLABORATION_STATE_FINISH = "finish";

  /**
   * Deploy a process
   * 
   * @param collaborationId
   */
  public void deployProcess(String collaborationId);

  /**
   * Start a process
   * 
   * @param collaborationId
   */
  public void startProcess(String collaborationId, String processDefId);

  /**
   * Delete a process
   * 
   * @param deploymentId
   */
  public void deleteProcess(String deployemntId);

  /**
   * List all process definitions.
   * 
   * @return a list of process definition
   */
  public List<ProcessDefinition> getProcessDefinitions();

  /**
   * List all process instances
   * 
   * @return a list of process instances
   */
  public List<ProcessInstance> getProcessInstances();

  /**
   * List the tasks for a certain user
   * 
   * @param username
   * @return a list of task for a certain user
   */
  public List<Task> getTasks(String username);

  /**
   * Get current task
   * 
   * @param processInstanceId
   * @return current task
   */
  public List<Task> getCurrentTask(String processInstanceId);

  /**
   * A user takes a task
   * 
   * @param taskId
   * @param username
   */
  public void takeTask(String taskId, String username);

  /**
   * Submit task
   * 
   * @param taskId
   */
  public void submitTask(String taskId);
}
