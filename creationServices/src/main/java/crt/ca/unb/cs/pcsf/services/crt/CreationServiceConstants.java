/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.crt;

import java.io.File;

/**
 * @author dongdong
 * @version 1.0
 * @createDate Jul 18, 2012
 * @email dong.dong@unb.ca
 * 
 */
public class CreationServiceConstants {
	public static final String LOGPRE = ">>>>";
	public static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";

	// collaboration save path
	public static final String SAVE_PATH = System.getProperty("user.home") + File.separator + "pcsf";

	// web service url
	public static final String WSURL_PRE = "http://localhost:8080/";

	public static final int MAX = 1000000;

	// path
	public static final String SERVICES_SOURCE_LOCATION = "pcsf" + File.separator + "collaboration services";
	public static final String SERVICES_DEPLOY_LOCATION = System.getenv("COL_SVR_DEPLOY_LOC");

	// email
	public static final String MAIL_FROM = "pcsf.notification@gmail.com";
	public static final String MAIL_SUBJECT = "Collaboration Notification From PCSF";
	public static final String MAIL_COMMON_CONTENT_FOR_PARTICIPANT = "You are asked to participant a collaboration!\nPlease click the following link to register into the collaboration using your given id:\n\n";
	public static final String MAIL_COMMON_CONTENT_FOR_CREATOR = "You have deployed a collaboration, an email has been sent to each participant and you can run the collaboration after all participant done registration.\n\nClick the following link to check the collaboration:\n\n";
	public static final String LINK_CREATOR = "http://ec2-54-242-33-5.compute-1.amazonaws.com:8080/pcsf/index.jsp?action=creatorLogin";
	public static final String LINK_PARTICIPANT = "http://ec2-54-242-33-5.compute-1.amazonaws.com:8080/pcsf/index.jsp?action=participantLogin";

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
}
