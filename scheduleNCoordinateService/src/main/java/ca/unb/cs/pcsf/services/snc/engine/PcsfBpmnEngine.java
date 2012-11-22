/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc.engine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.api.Configuration;
import org.jbpm.api.ExecutionService;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.ProcessEngine;
import org.jbpm.api.ProcessInstance;
import org.jbpm.api.RepositoryService;
import org.jbpm.api.TaskService;
import org.jbpm.api.task.Task;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 6, 2012
 * @email dong.dong@unb.ca
 */
public class PcsfBpmnEngine implements Engine {
	private Logger logger = Logger.getLogger(PcsfBpmnEngine.class.getName());

	private AmazonSimpleDB sdb;
	private File processFile;

	private ProcessEngine processEngine;
	private RepositoryService repositoryService;
	private ExecutionService executionService;
	private TaskService taskService;

	/**
	 * Constructor
	 */
	public PcsfBpmnEngine(File processFile) {
		this.processFile = processFile;

		try {
			sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
					PcsfBpmnEngine.class.getResourceAsStream(CREDENTIAL_FILE_PATH)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		processEngine = Configuration.getProcessEngine();
		repositoryService = processEngine.getRepositoryService();
		executionService = processEngine.getExecutionService();
		taskService = processEngine.getTaskService();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#deployProcess(java.lang.String)
	 */
	@Override
	public void deployProcess(String collaborationId) {
		logger.debug(LOGPRE + "deployProcess() start" + LOGPRE);

		// deploy a process definition and put it into database
		String processDeploymentId = repositoryService.createDeployment().addResourceFromFile(processFile).deploy();

		List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
		ReplaceableItem item = new ReplaceableItem(collaborationId);
		item.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID, processDeploymentId, true));

		items.add(item);

		sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, items));

		logger.debug(LOGPRE + "deployProcess() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#startProcess(java.lang.String)
	 */
	@Override
	public void startProcess(String collaborationId, String processDepId) {
		logger.debug(LOGPRE + "startProcess() start" + LOGPRE);

		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().deploymentId(processDepId).list().get(0);

		// start a process instance
		logger.info("starting a process instance...");
		executionService.startProcessInstanceById(pd.getId());

		// change collaboration state to be running
		List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
		ReplaceableItem item = new ReplaceableItem(collaborationId);
		item.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE, COLLABORATION_STATE_RUNNING, true));
		items.add(item);
		sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, items));

		logger.debug(LOGPRE + "startProcess() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#deleteProcess(java.lang.String)
	 */
	@Override
	public void deleteProcess(String deploymentId) {
		logger.debug(LOGPRE + "deleteProcess() start" + LOGPRE);

		repositoryService.deleteDeploymentCascade(deploymentId);

		logger.debug(LOGPRE + "deleteProcess() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#getProcessDefinitions()
	 */
	@Override
	public List<ProcessDefinition> getProcessDefinitions() {
		logger.debug(LOGPRE + "getProcessDefinitions() start" + LOGPRE);
		logger.debug(LOGPRE + "getProcessDefinitions() end" + LOGPRE);
		return repositoryService.createProcessDefinitionQuery().list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#getProcessInstances()
	 */
	@Override
	public List<ProcessInstance> getProcessInstances() {
		logger.debug(LOGPRE + "getProcessInstances() start" + LOGPRE);
		logger.debug(LOGPRE + "getProcessInstances() end" + LOGPRE);
		return executionService.createProcessInstanceQuery().list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#getTasks(java.lang.String)
	 */
	@Override
	public List<Task> getTasks(String username) {
		logger.debug(LOGPRE + "getTasks() start" + LOGPRE);
		List<Task> taskList = new LinkedList<Task>();
		List<Task> userTaskList = taskService.findPersonalTasks(username);
		List<Task> groupTaskList = taskService.findGroupTasks(username);

		for (Task task : userTaskList)
			taskList.add(task);
		for (Task task : groupTaskList)
			taskList.add(task);

		logger.debug(LOGPRE + "getTasks() end" + LOGPRE);
		return taskList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#getCurrentTask()
	 */
	@Override
	public List<Task> getCurrentTask() {
		logger.debug(LOGPRE + "getCurrentTask() start" + LOGPRE);
		logger.debug(LOGPRE + "getCurrentTask() end" + LOGPRE);
		return taskService.createTaskQuery().list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#submitTask(java.lang.String)
	 */
	@Override
	public void submitTask(String taskId) {
		logger.debug(LOGPRE + "submitTask() start" + LOGPRE);
		taskService.completeTask(taskId);
		logger.debug(LOGPRE + "submitTask() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.snc.engine.Engine#takeTask(java.lang.String, java.lang.String)
	 */
	@Override
	public void takeTask(String taskId, String username) {
		logger.debug(LOGPRE + "takeTask() start" + LOGPRE);
		taskService.takeTask(taskId, username);
		logger.debug(LOGPRE + "takeTask() end" + LOGPRE);
	}
}
