<%@page import="ca.unb.cs.pcsf.web.PcsfUtils"%>
<%@page import="ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl"%>
<%@page import="ca.unb.cs.pcsf.web.db.Collaboration"%>
<%@page import="java.io.File"%>
<%@page import="java.io.OutputStream"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.FileOutputStream"%>
<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<%
		String collaborationId = request.getParameter("collaborationId");
		Collaboration collaboration = new PcsfSimpleDBAccessImpl().getCollaborationById(collaborationId);

		PcsfUtils utils = new PcsfUtils();
		String bucket = "";
		String key = "";

		String[] strings = collaboration.getPackageLocation().split(",");
		bucket = strings[0];
		key = strings[1];

		String wsUrl = utils.getDtrWSUrl(collaboration.getName());
		String method = "downloadFile";
		File serverFile = new File(key);
		Object[] resultsObjects = utils.callService(wsUrl, method, bucket, key);

		byte[] bs = (byte[]) resultsObjects[0];
		FileOutputStream fileOutputStream = new FileOutputStream(serverFile);
		fileOutputStream.write(bs);

		fileOutputStream.close();

		response.setHeader("Content-disposition", "attachment;filename=" + serverFile.getName());
		response.setContentType("application/x-tar");
		String length = String.valueOf(serverFile.length());
		response.setHeader("content_Length", length);

		OutputStream outputStream = response.getOutputStream();
		FileInputStream inputStream = new FileInputStream(serverFile);

		byte bytes[] = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, len);
		}

		outputStream.flush();

		out.clear();
		out = pageContext.pushBody();

		outputStream.close();
		inputStream.close();
	%>
</body>
</html>