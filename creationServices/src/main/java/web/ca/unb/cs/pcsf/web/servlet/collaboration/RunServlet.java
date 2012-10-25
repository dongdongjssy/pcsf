/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.collaboration;

import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_STATE_RUNNING;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.PcsfUtils;
import ca.unb.cs.pcsf.web.db.Collaboration;
import ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class RunServlet
 */
@WebServlet(description = "Run Collaboration Servlet", urlPatterns = { "/Run" })
public class RunServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(RunServlet.class.getName());
	private PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
	private PcsfUtils pcsfUtils = new PcsfUtils();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RunServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String collaborationId = request.getParameter("collaborationId");
		String processDeploymentId = request.getParameter("processDeploymentId");
		Collaboration collaboration = dbAccess.getCollaborationById(collaborationId);

		String collaborationName = collaboration.getName();
		logger.info("Ready to run collaboration <" + collaborationName + ">...");

		String url = pcsfUtils.getSncWSUrl(collaborationName);
		String method = "runCollaboration";

		pcsfUtils.callService(url, method, collaborationId, processDeploymentId);
		dbAccess.updateCollaborationState(collaboration, COLLABORATION_STATE_RUNNING);

		pcsfUtils.waitAndGo();
		response.sendRedirect(request.getContextPath() + "/view.jsp?collaborationId=" + collaborationId);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
