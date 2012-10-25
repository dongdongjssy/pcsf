package ca.unb.cs.pcsf.web.client.servlet.participant;

import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_PARTICIPANT_BEAN;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.client.db.Participant;
import ca.unb.cs.pcsf.web.client.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class ParticipantDisplayServlet
 */
@WebServlet(description = "Participant display servlet", urlPatterns = { "/ParticipantDisplay" })
public class ParticipantDisplayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(ParticipantDisplayServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ParticipantDisplayServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		logger.info("reset session...");
		Participant participant = (Participant) session.getAttribute(ATTRIBUTE_PARTICIPANT_BEAN);
		String participantId = participant.getId();
		session.setAttribute(ATTRIBUTE_PARTICIPANT_BEAN, null);

		PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
		Participant newParticipant = dbAccess.getParticipantById(participantId);

		session.setAttribute(ATTRIBUTE_PARTICIPANT_BEAN, newParticipant);

		response.sendRedirect("participantPage.jsp");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
