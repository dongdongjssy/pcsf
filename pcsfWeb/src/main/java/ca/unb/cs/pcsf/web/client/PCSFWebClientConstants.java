/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.client;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 13, 2012
 * @email dong.dong@unb.ca
 */
public class PCSFWebClientConstants {
	// common page
	public static final String COMMON_PAGE_ERROR = "error.jsp";
	public static final String COMMON_PAGE_SUCCESS = "success.jsp";

	// session attributes
	public static final String ATTRIBUTE_ERR_MSG = "errorMsg";
	public static final String ATTRIBUTE_INFO_MSG = "infoMsg";

	// messages
	public static final String MSG_NO_COLLABORATION_CREATED = "You haven't created or deployed any collaboration yet.";
	public static final String MSG_COLLABORATION_CREATED = "The collaborations that you've created and deployed:";
	public static final String MSG_PARTICIPANT_INVOLVED_COLLABORATION = "You have joined the following collaborations";

	// error messages
	public static final String ERR_MSG_NO_COLLABORATION_FOUND = "You haven't got involved in this collaboration";
	public static final String ERR_MSG_FILE_EXIST = "This file already exsits, please rename it and upload again!";
	public static final String ERR_MSG_WORKFLOW_NOT_MATCH = "The participants list in the workflow file doesn't match the list you choose!";
	public static final String ERR_MSG_USER_ALREADY_EXIST = "User already exist, choose another name please!";
	public static final String ERR_MSG_COLLABORATION_ALREADY_EXIST = "Collaboration already exist, choose another name please!";

	public static final String ATTRIBUTE_USER_BEAN = "userBean";
	public static final String ATTRIBUTE_CREATOR_BEAN = "creatorBean";
	public static final String ATTRIBUTE_COLLABORATION_BEAN = "collaborationBean";
	public static final String ATTRIBUTE_COLLABORATION_DETAILS_BEAN = "collaborationDetailsBean";
	public static final String ATTRIBUTE_PARTICIPANT_BEAN = "participantBean";

	public static final String ATTRIBUTE_CREATOR_DISPLAY_MSG = "collaborationsMsg";
	public static final String ATTRIBUTE_PARTICIPANT_MSG = "participantsMsg";
	public static final String ATTRIBUTE_ADDED_PARTICIPANTS = "participants";
	public static final String ATTRIBUTE_COLLABORATION_ID = "collaborationId";

	public static final String ATTRIBUTE_PARTICIPANT_NAME = "participantName";
	public static final String ATTRIBUTE_CREATOR_NAME = "creatorName";

	public static final String ATTRIBUTE_WORKFLOW_STRING = "workflowStr";

	public static final String MESSAGE_RESULT_OK = "ok";
	public static final String MESSAGE_RESULT_FAIL = "fail";

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
	public static final String WSURL_PRE = "http://localhost:8080";
}
