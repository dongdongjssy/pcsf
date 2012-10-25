
<%@page import="java.util.ArrayList"%>
<%@page import="ca.unb.cs.pcsf.utils.PcsfUtils"%>
<%@page import="ca.unb.cs.pcsf.constants.PcsfSimpleDBAccessConstants"%>
<%@page import="ca.unb.cs.pcsf.services.crt.Participant"%>
<%@page import="ca.unb.cs.pcsf.services.crt.PcsfSimpleDBAccessImpl"%>
<%@page import="ca.unb.cs.pcsf.web.client.PCSFWebClientConstants"%>
<%@page import="ca.unb.cs.pcsf.services.crt.Collaboration"%>
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
	<h2>Collaboration Details Page</h2>
	<hr>
	<%
		PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
		String collaborationId = request.getParameter("collaborationId");
		Collaboration viewedCollaboration = dbAccess.getCollaborationById(collaborationId);
		if (viewedCollaboration.getCurrentState().equals(
				PcsfSimpleDBAccessConstants.COLLABORATION_STATE_NEW_CREATED)) {
	%>
	<p>This collaboration hasn't been deployed yet!</p>
	<%
		} else {
	%>
	<p>Participants State:</p>
	<table class="one">
		<tr class="one">
			<th class="one">Participant Name</th>
			<th class="one">Has Registered?</th>
		</tr>
		<%
			List<Participant> participants = viewedCollaboration.getParticipants();
				for (Participant p : participants) {
					out.print("<tr class=\"one\"><td class=\"one\">" + p.getName() + "</td>");
					if (p.getIsReg().equals(PcsfSimpleDBAccessConstants.PARTICIPANT_IS_REG_YES)) {
						out.print("<td class=\"one\">Yes</td></tr>");
					} else {
						out.print("<td class=\"one\">No</td></tr>");
					}
				}
		%>
	</table>
	<%
		if (viewedCollaboration.isAllReg()) {
				PcsfUtils pcsfUtils = new PcsfUtils();
				String wsUrl = pcsfUtils.getSncWSUrl(viewedCollaboration.getName());
	%>

	<p>All participants have done registration, you can run the
		collaboration process:</p>
	<table class="one">
		<tr>
			<th class="one">Process Definition Id</th>
			<th class="one">Process Definition Name</th>
			<th class="one">Process Definition Version</th>
			<th class="one">State</th>
			<th class="one">Operation</th>
		</tr>
		<%
			String getPd = "getProcessDefintions";
					Object[] pdResults = pcsfUtils.callService(wsUrl, getPd, collaborationId);
					List<?> resultList = (ArrayList<?>) pdResults[0];
					for (Object o : resultList) {
						out.print("<tr class=\"one\">");
						String s = (String) o;
						String[] infos = s.split(",");
						for (String info : infos) {
							out.print("<td class=\"one\">" + info + "</td>");
						}
						out.print("<td class=\"one\">" + viewedCollaboration.getCurrentState() + "</td>");
						if (!viewedCollaboration.getCurrentState().equals(
								PcsfSimpleDBAccessConstants.COLLABORATION_STATE_RUNNING)) {
							out.print("<td class=\"one\"><input type=\"button\" value=\"Run\"onclick=\"window.location.href='"
									+ request.getContextPath()
									+ "/Run?collaborationId="
									+ collaborationId
									+ "';\"/><input type=\"button\" value=\"Stop\" disabled/></td>");
						} else {
							out.print("<td class=\"one\"><input type=\"button\" value=\"Run\" disabled/><input type=\"button\" value=\"Stop\"onclick=\"window.location.href='"
									+ request.getContextPath()
									+ "/Stop?collaborationId="
									+ collaborationId
									+ "';\"/></td>");
						}
						out.print("</tr>");
					}
		%>
	</table>

	<p>Current Activity:</p>
	<table class="one">
		<tr>
			<th class="one">Task Id</th>
			<th class="one">Task Name</th>
			<th class="one">Assignee</th>
			<th class="one">Created Time</th>
		</tr>
		<%
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
		%>
	</table>

	<%
		} else {
				out.print("<p>Waiting for participants finishing registration.</p>");
			}
		}
	%>

	<p>
		<input type="button" name="backBtn" value="back"
			onclick="javascript:history.back(-1);" />
	</p>

</body>
</html>