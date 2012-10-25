<%@page import="java.util.List"%>
<%@page import="ca.unb.cs.pcsf.web.client.PCSFWebClientConstants"%>
<%@page import="ca.unb.cs.pcsf.constants.PcsfSimpleDBAccessConstants"%>
<%@page import="ca.unb.cs.pcsf.services.crt.Participant"%>
<%@page import="ca.unb.cs.pcsf.services.crt.Collaboration"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	response.setHeader("refresh", "1800");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Creator Page</title>
<link rel="stylesheet" type="text/css" href="styles/styles.css" />
</head>
<body>
	<%
		List<?> collaborations = (List<?>) session
			.getAttribute(PCSFWebClientConstants.ATTRIBUTE_COLLABORATION_BEAN);
			List<?> participants = (List<?>) session
			.getAttribute(PCSFWebClientConstants.ATTRIBUTE_ADDED_PARTICIPANTS);
	%>
	<jsp:include page="common.jsp" />
	<h2>Collaboration Creator Page</h2>
	Login As Creator:
	<b><%=session.getAttribute(PCSFWebClientConstants.ATTRIBUTE_CREATOR_NAME)%></b>
	<input type="button" name="logoutBtn" value="Logout"
		onclick="window.location.href='index.jsp';" />
	<p><%=session
					.getAttribute(PCSFWebClientConstants.ATTRIBUTE_CREATOR_DISPLAY_MSG)%></p>
	<table class="one">
		<%
			if (collaborations != null && collaborations.size() != 0) {
		%>
		<tr class="one">
			<th class="one">Collaboration ID</th>
			<th class="one">Collaboration Name</th>
			<th class="one">Current State</th>
			<th class="one">Commands</th>
		</tr>
		<%
			for (int i = 0; i < collaborations.size(); i++) {
					Collaboration collaboration = (Collaboration) collaborations.get(i);

					out.println("<tr class=\"one\">");
					out.println("<td class=\"one\">" + collaboration.getId() + "</td>");
					out.println("<td class=\"one\">" + collaboration.getName() + "</td>");
					out.println("<td class=\"one\"><b>" + collaboration.getCurrentState()
							+ "</b></td>");
					out.println("<td class=\"one\"><input type=\"button\" value=\"Delete\" onclick=\"window.location.href='"
							+ request.getContextPath()
							+ "/Delete?collaborationId="
							+ collaboration.getId() + "';\"/>");

					if (collaboration.getCurrentState().equals(
							PcsfSimpleDBAccessConstants.COLLABORATION_STATE_NEW_CREATED)) {
						out.print("<input type=\"button\" value=\"Deploy\"onclick=\"window.location.href='"
								+ request.getContextPath()
								+ "/Deploy?collaborationId="
								+ collaboration.getId() + "';\"/>");
						out.print("<input type=\"button\" value=\"View\" disabled/></td>");
					}

					else {
						out.print("<input type=\"button\" value=\"Deploy\" disabled/>");
						out.print("<input type=\"button\" value=\"View\"onclick=\"window.location.href='"
								+ request.getContextPath()
								+ "/view.jsp?collaborationId="
								+ collaboration.getId() + "';\"/></td>");
					}
				}
				out.println("</tr>");
			}
		%>
	</table>
	<br />
</body>
</html>