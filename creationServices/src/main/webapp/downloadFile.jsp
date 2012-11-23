<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Download File Page</title>
<script language="javascript">
	function CheckForm(objForm) {
		if (objForm.bucketName.value == "") {
			window.alert('Input the bucket name please!');
			objForm.bucketName.focus();
			return false;
		}

		if (objForm.key.value == "") {
			window.alert('Input the key please!');
			objForm.key.focus();
			return false;
		}
		return true;
	}
</script>
</head>
<body>
	<%
		session.setAttribute("DownloadPage_CollaborationName", request.getParameter("collaborationName"));
	%>
	<form name="downloadFileForm" action="download2.jsp" method="post">
		<table>
			<tr>
				<th>Bucket Name:</th>
				<td><input type="text" name="bucketName" /></td>
			</tr>

			<tr>
				<th>Key:</th>
				<td><input type="text" name="key" /></td>
			</tr>
			<tr>
				<td><input type="submit" name="downloadBtn" value="Download"
					onClick="javascript:return CheckForm(downloadFileForm)" /></td>
				<td><input type="reset" name="resetBtn" value="Reset" /><input
					type="button" name="cancelBtn" value="Cancel"
					onclick="window.location.href='participantPage.jsp';" /></td>
			</tr>
		</table>
	</form>
</body>
</html>