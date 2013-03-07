<%@page import="java.util.List"%>
<%@page import="ca.unb.cs.pcsf.web.PCSFWebConstants"%>
<%@page import="ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants"%>
<%@page import="ca.unb.cs.pcsf.web.db.Participant"%>
<%@page import="ca.unb.cs.pcsf.web.db.Collaboration"%>
<%@page import="ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl"%>
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
			<th class="one">ID</th>
			<th class="one">Name</th>
			<th class="one">Command</th>
			<th class="one">Instances List</th>
		</tr>
		<%
			PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
				for (int i = 0; i < collaborations.size(); i++) {
					Collaboration collaboration = (Collaboration) collaborations.get(i);

					out.print("<tr class=\"one\">");
					out.print("<td class=\"one\">" + collaboration.getId() + "</td>");
					out.print("<td class=\"one\">" + collaboration.getName() + "</td>");
					out.print("<td class=\"one\"><input type=\"button\" value=\"Delete\" onclick=\"window.location.href='"
							+ request.getContextPath()
							+ "/Delete?collaborationId="
							+ collaboration.getId()
							+ "';\"/><input type=\"button\" value=\"Deploy Instance\"onclick=\"window.location.href='"
							+ request.getContextPath()
							+ "/Deploy?collaborationId="
							+ collaboration.getId()
							+ "';\"/></td>");

					if (collaboration.getCurrentState().equals("0")) {
						out.print("<td class=\"one\">No instance for this collaboration</td>");
					} else {
						out.print("<td class=\"one\">" + collaboration.getCurrentState()
								+ " instance(s) deployed:<br/>");
						for (int k = 1; k <= Integer.parseInt(collaboration.getCurrentState()); k++) {
							Collaboration instance = dbAccess.getCollaborationByName(collaboration.getName() + "-" + k);
							out.print(instance.getName()
									+ "<input type=\"button\" value=\"Delete\" onclick=\"window.location.href='"
									+ request.getContextPath() + "/DeleteInstance?collaborationId=" + instance.getId()
									+ "';\"/>");
							out.print("<input type=\"button\" value=\"View\" onclick=\"window.location.href='"
									+ request.getContextPath() + "/View?collaborationId=" + instance.getId()
									+ "';\"/><br/>");
						}
						out.print("</td>");
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