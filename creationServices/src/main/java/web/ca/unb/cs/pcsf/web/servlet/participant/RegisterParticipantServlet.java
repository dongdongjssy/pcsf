package ca.unb.cs.pcsf.web.servlet.participant;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.PcsfUtils;

/**
 * Servlet implementation class RegisterParticipantServlet
 */
@WebServlet(description = "Register a participant for a collaboration", urlPatterns = { "/RegisterParticipant" })
public class RegisterParticipantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(RegisterParticipantServlet.class.getName());

	private PcsfUtils pcsfUtils = new PcsfUtils();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterParticipantServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String participantId = request.getParameter("participantId");
		String collaborationName = request.getParameter("collaborationName");

		logger.info("participant " + participantId
				+ " is registering into collaboration...");

		String url = pcsfUtils.getRegWSUrl(collaborationName);
		String method = "setAsReg";
		pcsfUtils.callService(url, method, participantId);

		logger.debug("redirecting to ParticipantDisplay servlet...");

		pcsfUtils.waitAndGo();
		response.sendRedirect(request.getContextPath() + "/ParticipantDisplay");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
