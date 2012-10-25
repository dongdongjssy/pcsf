/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web;

import java.io.File;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 13, 2012
 * @email dong.dong@unb.ca
 */
public class PCSFWebConstants {
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

	public static final String WSURL_PRE = "http://localhost:8080/";
	public static final String SAVE_PATH = System.getProperty("user.home") + File.separator + "pcsf";
}
