/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.img;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;

/**
 * @author ddong
 * @version 1.0
 * @createDate Dec. 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.img.InstanceManagementService")
public class InstanceManagementServiceImpl implements InstanceManagementService {
  private static final String LOGPRE = ">>>>";
  private static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";
  private static final String WSURL_PRE = "http://localhost:8080/";
  private static final String DOMAIN_COLLABORATION = "Collaboration";
  private static final String COLLABORATION_ATTRIBUTE_NAME = "Name";
  private static final String COLLABORATION_ATTRIBUTE_CURRENT_STATE = "CurrentState";
  private static final String COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID = "ProcessDefId";
  private static final String COLLABORATION_STATE_RUNNING = "running";
  private static final String COLLABORATION_STATE_STOP = "stop";

  private Logger logger = Logger.getLogger(InstanceManagementServiceImpl.class.getName());

  private AmazonSimpleDB sdb;
  private PropertiesCredentials credentials;

  public InstanceManagementServiceImpl() {
    // get credentials
    try {
      credentials = new PropertiesCredentials(
          InstanceManagementServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH));
      sdb = new AmazonSimpleDBClient(credentials);
    } catch (IOException e) {
      e.printStackTrace();
    }

    logger.info("===========================================");
    logger.info("Instance Management Service has started!");
    logger.info("===========================================");
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.img.InstanceManagementService#runInstance(java.lang.String)
   */
  @Override
  public void runInstance(String collaborationId) {
    // get collaboration
    String collaborationName = "";
    String collaborationState = "";
    String processDeploymentId = "";

    String request = "select * from `" + DOMAIN_COLLABORATION + "`";
    List<Item> items = sdb.select(new SelectRequest(request)).getItems();
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
        if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME))
          collaborationName = attribute.getValue();
        if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CURRENT_STATE))
          collaborationState = attribute.getValue();
        if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID))
          processDeploymentId = attribute.getValue();
      }
    }

    if (!collaborationState.equals(COLLABORATION_STATE_STOP)) {
      logger.info("Ready to run collaboration <" + collaborationName + ">...");

      String url = WSURL_PRE + collaborationName + "-scheduleNCoordinateService/ScheduleNCoordinateService?wsdl";
      String method = "runCollaboration";

      callService(url, method, collaborationId, processDeploymentId);
    }

    // change collaboration state
    List<ReplaceableItem> changeStateitems = new ArrayList<ReplaceableItem>();
    ReplaceableItem changeStateItem = new ReplaceableItem(collaborationId);
    changeStateItem.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE,
        COLLABORATION_STATE_RUNNING, true));
    changeStateitems.add(changeStateItem);
    sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, changeStateitems));
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.img.InstanceManagementService#stopInstance(java.lang.String)
   */
  @Override
  public void stopInstance(String collaborationId) {
    // change collaboration state
    List<ReplaceableItem> changeStateitems = new ArrayList<ReplaceableItem>();
    ReplaceableItem changeStateItem = new ReplaceableItem(collaborationId);
    changeStateItem.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE,
        COLLABORATION_STATE_STOP, true));
    changeStateitems.add(changeStateItem);
    sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, changeStateitems));
  }

  /**
   * A general method used to call web service.
   * 
   * @param wsUrl
   *          web service url
   * @param method
   *          method name
   * @param arg
   *          method parameters
   * @return results
   */
  private Object[] callService(String wsUrl, String method, Object... arg) {
    logger.debug(LOGPRE + "callService() start" + LOGPRE);

    Object[] resutls = null;
    JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
    Client client = dcf.createClient(wsUrl);
    try {
      resutls = client.invoke(method, arg);
    } catch (Exception e) {
      e.printStackTrace();
    }

    logger.debug(LOGPRE + "callService() end" + LOGPRE);
    return resutls;
  }

}
