<%@page import="java.util.ArrayList"%>
<%@page import="ca.unb.cs.pcsf.utils.PcsfUtils"%>
<%@page import="java.util.List"%>
<%@page import="ca.unb.cs.pcsf.web.client.PCSFWebClientConstants"%>
<%@page import="ca.unb.cs.pcsf.constants.PcsfSimpleDBAccessConstants"%>
<%@page import="ca.unb.cs.pcsf.services.crt.Collaboration"%>
<%@page import="ca.unb.cs.pcsf.services.crt.Participant"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	response.setHeader("refresh", "60");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Participant Page</title>
<link rel="stylesheet" type="text/css" href="styles/styles.css" />

</head>
<body>
	<jsp:include page="common.jsp" />
	<%
		Collaboration collaboration = (Collaboration) session
			.getAttribute(PCSFWebClientConstants.ATTRIBUTE_COLLABORATION_BEAN);
			Participant participant = (Participant) session
			.getAttribute(PCSFWebClientConstants.ATTRIBUTE_PARTICIPANT_BEAN);
			String username = participant.getName();
			String userId = participant.getId();
	%>
	<h2>Participant Page</h2>
	Login As Participant:
	<b><%=username%></b>
	<br /> Your ID:
	<b><%=userId%></b>
	<input type="button" name="logoutBtn" value="Logout"
		onclick="window.location.href='index.jsp';" />
	<p>Your state in this collaboration:</p>
	<table class="one">
		<%
			if (collaboration != null) {
		%>
		<tr class="one">
			<th class="one">Collaboration ID</th>
			<th class="one">Collaboration Name</th>
			<th class="one">Collaboration State</th>
			<th class="one">Has Registered?</th>
		</tr>
		<%
			out.print("<tr class=\"one\">");
				out.print("<td class=\"one\">" + collaboration.getId() + "</td>");
				out.print("<td class=\"one\">" + collaboration.getName() + "</td>");
				out.print("<td class=\"one\">" + collaboration.getCurrentState()
						+ "</td>");
				if (participant.getIsReg().equals(
						PcsfSimpleDBAccessConstants.PARTICIPANT_IS_REG_NO)) {
					out.print("<td class=\"one\">No <input type=\"button\" value=\"register\" onclick=\"window.location.href='"
							+ request.getContextPath()
							+ "/RegisterParticipant?participantId="
							+ participant.getId()
							+ "&&collaborationName="
							+ collaboration.getName()
							+ "';\"/></td>");
				} else {
					out.print("<td class=\"one\">Yes</td>");
				}
			}
			out.print("</tr>");
		%>
	</table>
	<p />
	<p>Your Task:</p>
	<table class="one">
		<%
			if (collaboration != null
					&& collaboration.getCurrentState().equals(
							PcsfSimpleDBAccessConstants.COLLABORATION_STATE_RUNNING)) {
		%>
		<tr class="one">
			<th class="one">Task Id</th>
			<th class="one">Task Name</th>
			<th class="one">Created Time</th>
			<th class="one">View</th>
		</tr>
		<%
			PcsfUtils pcsfUtils = new PcsfUtils();
				String wsUrl = pcsfUtils.getSncWSUrl(collaboration.getName());
				String getTsk = "getTask";
				Object[] tskResults = pcsfUtils.callService(wsUrl, getTsk, username,
						collaboration.getId());
				List<?> tskResultList = (ArrayList<?>) tskResults[0];
				for (Object o : tskResultList) {
					out.print("<tr class=\"one\">");
					String s = (String) o;
					String[] infos = s.split(",");
					for (String info : infos) {
						out.print("<td class=\"one\">" + info + "</td>");
					}
					out.print("<td class=\"one\"><input type=\"button\" value=\"view\" disable=true");
					out.print("</tr>");
				}
			}
		%>
	</table>
</body>
</html>