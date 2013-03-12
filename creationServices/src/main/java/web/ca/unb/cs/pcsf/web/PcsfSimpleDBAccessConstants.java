/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web;

/**
 * @author dongdong
 * @version 1.0
 * @creteDate Jul 6, 2012
 * @email dong.dong@unb.ca
 * 
 */
public class PcsfSimpleDBAccessConstants {
  // domain names
  public static final String DOMAIN_CREATOR = "Creator";
  public static final String DOMAIN_PARTICIPANT = "Participant";
  public static final String DOMAIN_COLLABORATION = "Collaboration";
  public static final String DOMAIN_TASK = "Task";

  // domain creator attributes
  public static final String CREATOR_ATTRIBUTE_NAME = "Name";
  public static final String CREATOR_ATTRIBUTE_PASSWORD = "Password";
  public static final String CREATOR_ATTRIBUTE_EMAIL = "Email";

  // domain collaboration attributes
  public static final String COLLABORATION_ATTRIBUTE_NAME = "Name";
  public static final String COLLABORATION_ATTRIBUTE_CREATOR_ID = "CreatorId";
  public static final String COLLABORATION_ATTRIBUTE_CURRENT_STATE = "CurrentState";
  public static final String COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL = "WorkflowModel";
  public static final String COLLABORATION_ATTRIBUTE_PARTICIPANT = "Participant";
  public static final String COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID = "ProcessDefId";
  public static final String COLLABORATION_ATTRIBUTE_PACKAGE = "Package";

  // domain participant attributes
  public static final String PARTICIPANT_ATTRIBUTE_NAME = "Name";
  public static final String PARTICIPANT_ATTRIBUTE_EMAIL = "Email";
  public static final String PARTICIPANT_ATTRIBUTE_COLLABORATION_ID = "CollaborationId";
  public static final String PARTICIPANT_ATTRIBUTE_IS_REG = "IsReg";
  public static final String PARTICIPANT_ATTRIBUTE_ROLE = "Role";
  public static final String PARTICIPANT_ATTRIBUTE_GROUP = "Group";

  public static final String PARTICIPANT_IS_REG_NO = "no";
  public static final String PARTICIPANT_IS_REG_YES = "yes";
  public static final String PARTICIPANT_NO_GROUP = "No Group";

  // collaboration state
  public static final String COLLABORATION_STATE_NEW_CREATED = "new created";
  public static final String COLLABORATION_STATE_DEPLOYED = "deployed";
  public static final String COLLABORATION_STATE_RUNNING = "running";
  public static final String COLLABORATION_STATE_STOP = "stop";
  public static final String COLLABORATION_STATE_FINISH = "finish";

  public static final String LOGPRE = "";
  public static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";
  public static final int MAX = 1000000;
}
