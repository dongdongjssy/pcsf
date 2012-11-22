<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Upload Page</title>
</head>
<body>
	<%
		session.setAttribute("UploadPage_CollaborationName", request.getParameter("collaborationName"));
	%>
	<form name="uploadForm" action="UploadFile" method="post"
		enctype="multipart/form-data">

		<input type="file" name="uploadFile" id="uploadFile" />
		<p>
			<input type="submit" name="okBtn" value="Upload" /> <input
				type="reset" name="resetBtn" value="Reset" /> <input type="button"
				name="cancelBtn" value="Cancel"
				onclick="window.location.href='participantPage.jsp';" />
		</p>
	</form>
</body>
</html>