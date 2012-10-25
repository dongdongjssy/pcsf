<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="styles/styles.css" />
<script type="text/javascript">
	function CheckCreatorLoginForm(objForm) {
		if (objForm.username.value == "") {
			window.alert('Input your name please!');
			objForm.username.focus();
			return false;
		}
		if (objForm.password.value == "") {
			window.alert('Input your password please!');
			objForm.password.focus();
			return false;
		}

		return true;
	}

	function CheckParticipantLoginForm(objForm) {
		if (objForm.collaborationId.value == "") {
			window.alert('Input collaboration id please!');
			objForm.collaborationId.focus();
			return false;
		}
		if (objForm.participantId.value == "") {
			window.alert('Input your id for the collaboration please!');
			objForm.participantId.focus();
			return false;
		}
		return true;
	}

	function CheckCreatorRegForm(objForm) {
		if (objForm.username.value == "") {
			window.alert('Input your name please!');
			objForm.username.focus();
			return false;
		}
		if (objForm.password.value == "") {
			window.alert('Input your password please!');
			objForm.password.focus();
			return false;
		}
		if (objForm.email.value == "") {
			window.alert('Input your email please!');
			objForm.email.focus();
			return false;
		}

		return true;
	}
</script>
</head>
<body>
	<jsp:include page="common.jsp" />
	<h2>Welcome</h2>
	<%
		String action = request.getParameter("action");
	%>
	<ul>
		<li><a href="index.jsp?action=creatorLogin">Creator Login</a><br />
			Login as a creator, you can create, deploy, manipulate a
			collaboration.</li>
		<%
			if ("creatorLogin".equals(action)) {
		%>
		<table>
			<tr>
				<td><form name="loginForm" method="post" action="CreatorLogin">
						<table class="two">
							<tr>
								<td>Creator Name:<br /> <input type="text" name="username" /></td>
							</tr>
							<tr>
								<td>Password:<br /> <input type="password" name="password" /></td>
							</tr>
							<tr>
								<td><input type="submit" name="login" value="Login"
									onClick="javascript:return CheckCreatorLoginForm(loginForm)" /><input
									type="button" name="cancelBtn" value="Cancel"
									onclick="window.location.href='index.jsp';" /></td>
							</tr>
						</table>
					</form></td>
			</tr>
		</table>
		<br />
		<%
			}
		%>

		<li><a href="index.jsp?action=participantLogin">Participant
				Login</a><br /> Login as a participant, you can register into a
			collaboration and submit your task.</li>
		<%
			if ("participantLogin".equals(action)) {
		%>
		<table>
			<tr>
				<td><form name="participantLoginForm" method="post"
						action="ParticipantLogin">
						<table class="two">
							<tr>
								<td>Your ID:<br /> <input type="text" name="participantId" /></td>
							</tr>
							<tr>
								<td><input type="submit" name="login" value="Login"
									onClick="javascript:return CheckParticipantLoginForm(participantLoginForm)" /><input
									type="button" name="cancelBtn" value="Cancel"
									onclick="window.location.href='index.jsp';" /></td>
							</tr>
						</table>
					</form></td>
			</tr>
		</table>
		<br />
		<%
			}
		%>

		<li><a href="index.jsp?action=creatorReg">Register as Creator</a><br />
			Register as a creator</li>
		<%
			if ("creatorReg".equals(action)) {
		%>
		<table>
			<tr>
				<td><form name="creatorRegForm" method="post"
						action="CreatorRegister">
						<table class="two">
							<tr>
								<td>Creator Name:<br /> <input type="text" name="username" /></td>
							</tr>
							<tr>
								<td>Password:<br /> <input type="password" name="password" /></td>
							</tr>
							<tr>
								<td>E-mail:<br /> <input type="text" name="email" /></td>
							</tr>
							<tr>
								<td><input type="submit" name="okBtn" value="Regist"
									onClick="javascript:return CheckCreatorRegForm(creatorRegForm)" /><input
									type="button" name="cancelBtn" value="Cancel"
									onclick="window.location.href='index.jsp';" /></td>
							</tr>
						</table>
					</form></td>
			</tr>
		</table>
		<%
			}
		%>
	</ul>
</body>
</html>