<%@page import="java.util.*,org.jbpm.api.*,java.util.zip.*"%>
<%
	ProcessEngine processEngine = Configuration.getProcessEngine();
	RepositoryService repositoryService = processEngine.getRepositoryService();

	IdentityService identityService = processEngine.getIdentityService();
	identityService.createGroup("developers");

	identityService.createUser("peter", "Peter", "Pan");
	identityService.createMembership("peter", "developers");

	identityService.createUser("mary", "Mary", "LittleLamb");
	identityService.createMembership("mary", "developers");

	repositoryService.createDeployment().addResourceFromClasspath("collaborativeTesting.bpmn.xml").deploy();
	//ZipInputStream zis = new ZipInputStream(this.getClass().getResourceAsStream("/leave.zip"));
	//repositoryService.createDeployment().addResourcesFromZipInputStream(zis).deploy();
	response.sendRedirect("index.jsp");
%>