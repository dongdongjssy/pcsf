/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.collaboration;

import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_STATE_STOP;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.unb.cs.pcsf.web.PcsfUtils;
import ca.unb.cs.pcsf.web.db.Collaboration;
import ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class StopServlet
 */
@WebServlet("/Stop")
public class StopServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public StopServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
		String collaborationId = request.getParameter("collaborationId");
		Collaboration collaboration = dbAccess.getCollaborationById(collaborationId);

		// change collaboration state to be stop
		dbAccess.updateCollaborationState(collaboration, COLLABORATION_STATE_STOP);

		new PcsfUtils().waitAndGo();
		response.sendRedirect(request.getContextPath() + "/view.jsp?collaborationId=" + collaborationId);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
