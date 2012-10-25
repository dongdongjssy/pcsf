<%@page import="ca.unb.cs.pcsf.web.PCSFWebConstants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Error Page</title>
</head>
<body>
	<h2>Error Page</h2>
	<hr />
	<%
		String errorMsg = (String) session
				.getAttribute(PCSFWebConstants.ATTRIBUTE_ERR_MSG);
	%>
	<h2>
		<font color="red"><%=errorMsg%></font>
	</h2>
	<p>
		<input type="button" name="backBtn" value="back"
			onclick="javascript:history.back(-1);" />
	</p>
</body>
</html>