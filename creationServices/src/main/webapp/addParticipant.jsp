<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add New Participant</title>
<script language="javascript">
	function CheckForm(objForm) {
		if (objForm.username.value == "") {
			window.alert('Input participant name please!');
			objForm.username.focus();
			return false;
		}
		if (objForm.email.value == "") {
			window.alert('Input participant email please!');
			objForm.email.focus();
			return false;
		}
		if (objForm.role.value == "") {
			window.alert('Input participant role please!');
			objForm.role.focus();
			return false;
		}

		return true;
	}
</script>
</head>
<body>
	<h2>Add a new participant</h2>
	<hr>

	<form name="addParticipantForm" method="post" action="AddParticipant">
		<table>
			<tr>
				<td>User Name:</td>
				<td><input type="text" name="username" /></td>
			</tr>
			<tr>
				<td>E-mail:</td>
				<td><input type="text" name="email" /></td>
			</tr>
			<tr>
				<td>Role:</td>
				<td><input type="text" name="role" /></td>
			</tr>
			<tr>
				<td>Group:</td>
				<td><input type="text" name="group" /></td>
			</tr>

			<tr>
				<td><input type="submit" name="okBtn" value="OK"
					onclick="javascript:return CheckForm(addParticipantForm);" /></td>
				<td><input type="button" name="cancelBtn" value="Cancel"
					onclick="javascript:history.back(-1);" /></td>
			</tr>
		</table>
	</form>
</body>
</html>