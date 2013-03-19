/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.jbpm.api.ProcessDefinition;
import org.jbpm.api.task.Task;

import ca.unb.cs.pcsf.services.snc.engine.Engine;
import ca.unb.cs.pcsf.services.snc.engine.EngineFactory;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;

/**
 * @author dongdong
 * @version 1.0
 * @createDate Aug 8, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService")
public class ScheduleNCoordinateServiceImpl implements ScheduleNCoordinateService {
  private static final String LOGPRE = ">>>>";
  private static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";

  private static final String DOMAIN_COLLABORATION = "Collaboration";
  private static final String COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL = "WorkflowModel";

  private Logger logger = Logger.getLogger(ScheduleNCoordinateServiceImpl.class.getName());

  private AmazonSimpleDB sdb;
  private Engine engine;

  /**
   * Constructor
   */
  public ScheduleNCoordinateServiceImpl() {
    try {
      sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
          ScheduleNCoordinateServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH)));
    } catch (IOException e) {
      e.printStackTrace();
    }

    logger.info("===========================================");
    logger.info("Schedule and Coordinate service has started!");
    logger.info("===========================================");
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#deployProcess( java.lang.String)
   */
  @Override
  public void deployProcess(String collaborationId) {
    logger.debug(LOGPRE + "deployProcess() start" + LOGPRE);

    getEngine(collaborationId);
    engine.deployProcess(collaborationId);

    logger.debug(LOGPRE + "deployProcess() end" + LOGPRE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.mnc.ScheduleNCoordinateService#runCollaboration (java.lang.String)
   */
  @Override
  public void runCollaboration(String collaborationId, String processDefId) {
    logger.debug(LOGPRE + "runCollaboration() start" + LOGPRE);

    getEngine(collaborationId);
    engine.startProcess(collaborationId, processDefId);

    logger.debug(LOGPRE + "runCollaboration() end" + LOGPRE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#getProcessInstances (java.lang.String)
   */
  @Override
  public String[] getProcessInstances(String collaborationId) {
    logger.debug(LOGPRE + "getProcessInstances() start" + LOGPRE);
    getEngine(collaborationId);

    logger.debug(LOGPRE + "getProcessInstances() end" + LOGPRE);
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#getProcessDefintions (java.lang.String)
   */
  @Override
  public String[] getProcessDefintions(String collaborationId) {
    logger.debug(LOGPRE + "getProcessDefintions() start" + LOGPRE);
    getEngine(collaborationId);
    List<ProcessDefinition> pdList = engine.getProcessDefinitions();

    List<String> resultsList = new ArrayList<String>();
    for (ProcessDefinition pd : pdList) {
      String pdInfo = "";
      pdInfo += pd.getId() + ",";
      pdInfo += pd.getName() + ",";
      pdInfo += pd.getVersion();

      resultsList.add(pdInfo);
    }

    String[] strings = new String[resultsList.size()];
    for (int i = 0; i < strings.length; i++)
      strings[i] = resultsList.get(i);

    logger.debug(LOGPRE + "getProcessDefintions() end" + LOGPRE);
    return strings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#getCurrentTask (java.lang.String)
   */
  @Override
  public String[] getCurrentTask(String collaborationId, String processInstanceId) {
    logger.debug(LOGPRE + "getCurrentTask() start" + LOGPRE);
    getEngine(collaborationId);
    List<Task> taskList = engine.getCurrentTask(processInstanceId);

    List<String> resultList = new ArrayList<String>();
    for (Task task : taskList) {
      String taskInfo = "";
      taskInfo += task.getId() + ",";
      taskInfo += task.getName() + ",";
      taskInfo += task.getAssignee() + ",";
      taskInfo += task.getCreateTime();

      resultList.add(taskInfo);
    }

    String[] strings = new String[resultList.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = resultList.get(i);
    }

    logger.debug(LOGPRE + "getCurrentTask() end" + LOGPRE);
    return strings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#getTask(java.lang .String)
   */
  @Override
  public String[] getTask(String username, String collaborationId) {
    logger.debug(LOGPRE + "getTask() start" + LOGPRE);
    getEngine(collaborationId);
    List<Task> taskList = engine.getTasks(username);

    List<String> taskStrings = new ArrayList<String>();
    for (Task task : taskList) {
      String taskInfo = "";
      taskInfo += task.getId() + ",";
      taskInfo += task.getName() + ",";
      taskInfo += task.getCreateTime();

      taskStrings.add(taskInfo);
    }

    String[] strings = new String[taskStrings.size()];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = taskStrings.get(i);
    }

    logger.debug(LOGPRE + "getTask() end" + LOGPRE);
    return strings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#submitTask(java.lang.String)
   */
  @Override
  public void submitTask(String taskId, String collaborationId) {
    logger.debug(LOGPRE + "submitTask() start" + LOGPRE);

    getEngine(collaborationId);
    engine.submitTask(taskId);

    logger.debug(LOGPRE + "submitTask() end" + LOGPRE);
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateService#takeTask(java.lang.String, java.lang.String,
   * java.lang.String)
   */
  @Override
  public void takeTask(String taskId, String username, String collaborationId) {
    logger.debug(LOGPRE + "takeTask() start" + LOGPRE);

    getEngine(collaborationId);
    engine.takeTask(taskId, username);

    logger.debug(LOGPRE + "takeTask() end" + LOGPRE);
  }

  /**
   * Check if the engine is initilized or not
   * 
   * @param collaborationId
   */
  private void getEngine(String collaborationId) {
    if (this.engine == null) {
      // String bucket = "";
      // String key = "";
      String workflowModel = "";

      String getCollaborationRequest = "select * from `" + DOMAIN_COLLABORATION + "`";
      List<Item> items = sdb.select(new SelectRequest(getCollaborationRequest)).getItems();
      Item findItem = new Item();

      if (!items.isEmpty()) {
        for (Item item : items) {
          if (item.getName().equals(collaborationId)) {
            findItem = item;
            break;
          }
        }
      }

      if (findItem != null) {
        for (Attribute attribute : findItem.getAttributes()) {
          if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL)) {
            workflowModel = attribute.getValue();
            break;
          }
        }
      }

      this.engine = EngineFactory.newEngine(new File(workflowModel));
    }
  }
}
