
<%@page import="java.util.ArrayList"%>
<%@page import="ca.unb.cs.pcsf.web.PcsfUtils"%>
<%@page import="ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants"%>
<%@page import="ca.unb.cs.pcsf.web.db.Participant"%>
<%@page import="ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl"%>
<%@page import="ca.unb.cs.pcsf.web.PCSFWebConstants"%>
<%@page import="ca.unb.cs.pcsf.web.db.Collaboration"%>
<%@page import="java.util.List"%>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Collaboration Details Page</title>
<link rel="stylesheet" type="text/css" href="styles/styles.css" />
</head>
<body>
	<jsp:include page="common.jsp" />
	<h2>Collaboration Details Page</h2>
	<%
		PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
		String collaborationId = request.getParameter("collaborationId");
		session.setAttribute("viewedCollaborationInstanceId", collaborationId);
		List<?> participants = (List<?>) session.getAttribute(PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS);
		Collaboration viewedCollaboration = dbAccess.getCollaborationById(collaborationId);
		if (viewedCollaboration.getParticipants() == null) {

			out.print("<p>Add participants for this instance:</p>");
			out.print("<table class=\"one\">");
			out.print("<tr class=\"one\">");
			out.print("<td class=\"one\"><jsp:include page=\"addParticipant.jsp\" /></td>");
			out.print("<td class=\"one\">");

			if (!participants.isEmpty()) {
				for (int i = 0; i < participants.size(); i++) {
					Participant p = (Participant) participants.get(i);
					out.print(p.getName() + "<input type=\"button\" value=\"-\" onclick=\"window.location.href='"
							+ request.getContextPath() + "/DeleteParticipant?participantName=" + p.getName()
							+ "';\" style=\"background:transparent; border-style:none\"/>" + "<br/>");
				}
			}
			out.print("<hr/>");
			out.print("<input type=\"button\" value=\"Confirm\" onclick=\"window.location.href='"
					+ request.getContextPath() + "/AddParticipantConfirm?collaborationId="
					+ viewedCollaboration.getId() + "';\"/><br/>");
			out.print("</td>");
			out.print("</tr>");
			out.print("</table>");
		} else {
			out.print("<p>Participants State:</p>");
			out.print("<table class=\"one\">");
			out.print("<tr class=\"one\">");
			out.print("<th class=\"one\">Participant Name</th>");
			out.print("<th class=\"one\">Has Registered?</th>");
			out.print("</tr>");

			List<Participant> participantsList = viewedCollaboration.getParticipants();
			for (Participant p : participantsList) {
				out.print("<tr class=\"one\"><td class=\"one\">" + p.getName() + "</td>");
				if (p.getIsReg().equals(PcsfSimpleDBAccessConstants.PARTICIPANT_IS_REG_YES)) {
					out.print("<td class=\"one\">Yes</td></tr>");
				} else {
					out.print("<td class=\"one\">No</td></tr>");
				}
			}
			out.print("</table>");

			if (viewedCollaboration.isAllReg()) {
				PcsfUtils pcsfUtils = new PcsfUtils();
				String wsUrl = pcsfUtils.getSncWSUrl(viewedCollaboration.getName());

				out.print("<p>All participants have done registration, you can run the collaboration instance:</p>");
				out.print("<table class=\"one\">");
				out.print("<tr>");
				out.print("<th class=\"one\">Instance Id</th>");
				out.print("<th class=\"one\">Instance Name</th>");
				out.print("<th class=\"one\">State</th>");
				out.print("<th class=\"one\">Operation</th>");
				out.print("</tr>");
				out.print("<tr>");
				out.print("<td class=\"one\">" + viewedCollaboration.getId() + "</td>");
				out.print("<td class=\"one\">" + viewedCollaboration.getName() + "</td>");
				out.print("<td class=\"one\">" + viewedCollaboration.getCurrentState() + "</td>");

				if (!viewedCollaboration.getCurrentState().equals(
						PcsfSimpleDBAccessConstants.COLLABORATION_STATE_RUNNING)) {
					out.print("<td class=\"one\"><input type=\"button\" value=\"Run\"onclick=\"window.location.href='"
							+ request.getContextPath()
							+ "/Run?collaborationId="
							+ collaborationId
							+ "&&processDeploymentId="
							+ viewedCollaboration.getProcessDefId()
							+ "';\"/><input type=\"button\" value=\"Stop\" disabled/></td>");
				} else {
					out.print("<td class=\"one\"><input type=\"button\" value=\"Run\" disabled/><input type=\"button\" value=\"Stop\"onclick=\"window.location.href='"
							+ request.getContextPath() + "/Stop?collaborationId=" + collaborationId + "';\"/></td>");
				}
				out.print("</tr>");
				out.print("</table>");

				if (viewedCollaboration.getCurrentState().equals(
						PcsfSimpleDBAccessConstants.COLLABORATION_STATE_RUNNING)) {
					out.print("<p>Current Active Activities:</p>");
					out.print("<table class=\"one\">");
					out.print("<tr>");
					out.print("<th class=\"one\">Task Id</th>");
					out.print("<th class=\"one\">Task Name</th>");
					out.print("<th class=\"one\">Assignee</th>");
					out.print("<th class=\"one\">Created Time</th>");
					out.print("</tr>");

					String getTsk = "getCurrentTask";
					Object[] tskResults = pcsfUtils.callService(wsUrl, getTsk, collaborationId);
					List<?> tskResultList = (ArrayList<?>) tskResults[0];
					for (Object o : tskResultList) {
						out.print("<tr class=\"one\">");
						String s = (String) o;
						String[] infos = s.split(",");
						for (String info : infos) {
							out.print("<td class=\"one\">" + info + "</td>");
						}
						out.print("</tr>");
					}
					out.print("</table>");
				}
			} else {
				out.print("<p>Waiting for participants finishing registration.</p>");
			}
		}
	%>

	<p>
		<input type="button" name="backBtn" value="back"
			onclick="window.location.href='/pcsf/CreatorDisplay';" />
	</p>

</body>
</html>