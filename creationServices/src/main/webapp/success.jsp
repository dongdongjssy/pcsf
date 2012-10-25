<%@page import="ca.unb.cs.pcsf.web.PCSFWebConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Success Page</title>
</head>
<body>
	<h2>Information Page</h2>
	<hr />
	<%
		String infoMsg = (String) session
				.getAttribute(PCSFWebConstants.ATTRIBUTE_INFO_MSG);
	%>

	<h2><%=infoMsg%></h2>

	<p>
		<input type="button" name="backBtn" value="back"
			onclick="window.location.href='index.jsp';" />
	</p>
</body>
</html>