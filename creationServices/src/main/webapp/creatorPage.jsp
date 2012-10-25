<%@page import="java.util.List"%>
<%@page import="ca.unb.cs.pcsf.web.PCSFWebConstants"%>
<%@page import="ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants"%>
<%@page import="ca.unb.cs.pcsf.web.db.Participant"%>
<%@page import="ca.unb.cs.pcsf.web.db.Collaboration"%>
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
<script language="javascript">
	function CheckForm(objForm) {
		if (objForm.collaborationName.value == "") {
			window.alert('Input the collaboration name please!');
			objForm.collaborationName.focus();
			return false;
		}

		if (objForm.uploadFile.value == "") {
			window.alert('Choose work flow file please!');
			objForm.collaborationName.focus();
			return false;
		}
		return true;
	}
</script>
</head>
<body>
	<%
		List<?> collaborations = (List<?>) session.getAttribute(PCSFWebConstants.ATTRIBUTE_COLLABORATION_BEAN);
		List<?> participants = (List<?>) session.getAttribute(PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS);
	%>
	<jsp:include page="common.jsp" />
	<h2>Collaboration Creator Page</h2>
	Login As Creator:
	<b><%=session.getAttribute(PCSFWebConstants.ATTRIBUTE_CREATOR_NAME)%></b>
	<input type="button" name="logoutBtn" value="Logout"
		onclick="window.location.href='index.jsp';" />
	<p><%=session.getAttribute(PCSFWebConstants.ATTRIBUTE_CREATOR_DISPLAY_MSG)%></p>
	<table class="one">
		<%
			if (collaborations != null && collaborations.size() != 0) {
		%>
		<tr class="one">
			<th class="one">Collaboration ID</th>
			<th class="one">Collaboration Name</th>
			<th class="one">Current State</th>
			<th class="one">Commands</th>
			<th class="one">Deployable Pack</th>
		</tr>
		<%
			for (int i = 0; i < collaborations.size(); i++) {
					Collaboration collaboration = (Collaboration) collaborations.get(i);

					out.print("<tr class=\"one\">");
					out.print("<td class=\"one\">" + collaboration.getId() + "</td>");
					out.print("<td class=\"one\">" + collaboration.getName() + "</td>");
					out.print("<td class=\"one\"><b>" + collaboration.getCurrentState() + "</b></td>");
					out.print("<td class=\"one\"><input type=\"button\" value=\"Delete\" onclick=\"window.location.href='"
							+ request.getContextPath() + "/Delete?collaborationId=" + collaboration.getId() + "';\"/>");

					if (collaboration.getCurrentState().equals(
							PcsfSimpleDBAccessConstants.COLLABORATION_STATE_NEW_CREATED)) {
						out.print("<input type=\"button\" value=\"Deploy\"onclick=\"window.location.href='"
								+ request.getContextPath() + "/Deploy?collaborationId=" + collaboration.getId()
								+ "';\"/>");
						out.print("<input type=\"button\" value=\"View\" disabled/></td>");
						out.print("<td class=\"one\">Not Deployed Yet</td>");
					}

					else {
						out.print("<input type=\"button\" value=\"Deploy\" disabled/>");
						out.print("<input type=\"button\" value=\"View\"onclick=\"window.location.href='"
								+ request.getContextPath() + "/view.jsp?collaborationId=" + collaboration.getId()
								+ "';\"/></td>");
						out.print("<td class=\"one\"><a href=\"download.jsp?collaborationId=" + collaboration.getId()
								+ "\">download</a></td>");
					}
				}
				out.println("</tr>");
			}
		%>
	</table>
	<br />
	<hr>
	<p>Create a new collaboration:</p>
	<form name="createForm" action="Create" method="post"
		enctype="multipart/form-data">
		<table class="one">
			<tr class="one">
				<th align="left" class="one">Collaboration Name:</th>
				<td class="one"><input type="text" name="collaborationName" /></td>
			</tr>
			<tr class="one">
				<th class="one" align="left">Participants:</th>
				<td class="one">
					<%
						if (!participants.isEmpty()) {
							for (int i = 0; i < participants.size(); i++) {
								Participant p = (Participant) participants.get(i);
								out.print(p.getName() + "<input type=\"button\" value=\"-\" onclick=\"window.location.href='"
										+ request.getContextPath() + "/DeleteParticipant?participantName=" + p.getName()
										+ "';\" style=\"background:transparent; border-style:none\"/>" + "<br/>");
							}
						}
					%>
					<hr /> <input type="button" name="addParticipantBtn"
					value="+ Add Participant"
					onclick="window.location.href='addParticipant.jsp';" />
				</td>
			</tr>
			<tr class="one">
				<th class="one" align="left">Work Flow Definition:</th>
				<td class="one"><input type="file" name="uploadFile"
					id="uploadFile" /></td>
			</tr>
		</table>
		<p>
			<input type="submit" name="okBtn" value="Create"
				onClick="javascript:return CheckForm(createForm)" /> <input
				type="reset" name="resetBtn" value="Reset" />
		</p>
	</form>
</body>
</html>