package ca.unb.cs.pcsf.web.client.servlet.participant;

import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_COLLABORATION_BEAN;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_ERR_MSG;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_PARTICIPANT_BEAN;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_PARTICIPANT_MSG;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.COMMON_PAGE_ERROR;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.LOGPRE;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.MESSAGE_RESULT_FAIL;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.MESSAGE_RESULT_OK;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.MSG_PARTICIPANT_INVOLVED_COLLABORATION;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.client.PCSFMessage;
import ca.unb.cs.pcsf.web.client.db.Collaboration;
import ca.unb.cs.pcsf.web.client.db.Participant;
import ca.unb.cs.pcsf.web.client.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class ParticipantLoginServlet
 */
@WebServlet(description = "Participant login servlet", urlPatterns = { "/ParticipantLogin" })
public class ParticipantLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PcsfSimpleDBAccessImpl dbAccessor = new PcsfSimpleDBAccessImpl();
	Logger logger = Logger.getLogger(ParticipantLoginServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ParticipantLoginServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		String participantId = request.getParameter("participantId");
		PCSFMessage message = loginAsParticipant(participantId);

		if (message.getObject() == null) {
			session.setAttribute(ATTRIBUTE_ERR_MSG, message.getMsg());
			response.sendRedirect(COMMON_PAGE_ERROR);
		} else {
			Participant participant = (Participant) message.getObject();
			Collaboration collaboration = dbAccessor.getCollaborationById(participant.getCollaborationId());

			session.setAttribute(ATTRIBUTE_PARTICIPANT_MSG, MSG_PARTICIPANT_INVOLVED_COLLABORATION);
			session.setAttribute(ATTRIBUTE_COLLABORATION_BEAN, collaboration);
			session.setAttribute(ATTRIBUTE_PARTICIPANT_BEAN, participant);
			response.sendRedirect(request.getContextPath() + "/ParticipantDisplay");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	/**
	 * Login as participant
	 * 
	 * @param participantId
	 * @return result message
	 */
	private PCSFMessage loginAsParticipant(String participantId) {
		logger.debug(LOGPRE + "loginAsParticipant() start" + LOGPRE);

		if (dbAccessor.isParticipantExist(participantId)) {
			Participant participant = dbAccessor.getParticipantById(participantId);

			logger.info("Participant " + participant.getName() + " login successfully!");
			logger.debug(LOGPRE + "loginAsParticipant() end" + LOGPRE);
			return new PCSFMessage(MESSAGE_RESULT_OK, "Participant " + participant.getName() + " login successfully!",
					participant);
		} else {
			logger.info("Participant doesn't exist!");
			logger.debug(LOGPRE + "loginAsParticipant() end" + LOGPRE);
			return new PCSFMessage(MESSAGE_RESULT_FAIL, "Participant doesn't exist!", null);
		}
	}
}
