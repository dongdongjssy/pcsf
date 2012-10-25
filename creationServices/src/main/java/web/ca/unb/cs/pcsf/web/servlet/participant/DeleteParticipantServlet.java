/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.participant;

import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ca.unb.cs.pcsf.web.db.Participant;

/**
 * Servlet implementation class DeleteParticipantServlet
 * 
 * @author dongdong
 * @version 1.0
 * @createDate Jul 19, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebServlet(description = "Delete a participant from a collaboration", urlPatterns = { "/DeleteParticipant" })
public class DeleteParticipantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeleteParticipantServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		String userName = request.getParameter("participantName");

		@SuppressWarnings("unchecked")
		List<Participant> participants = (List<Participant>) session
				.getAttribute(ATTRIBUTE_ADDED_PARTICIPANTS);

		deletParticipant(userName, participants);

		session.setAttribute(ATTRIBUTE_ADDED_PARTICIPANTS, null);
		session.setAttribute(ATTRIBUTE_ADDED_PARTICIPANTS, participants);
		response.sendRedirect("creatorPage.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * Remove a participant from the participant list.
	 * 
	 * @param participantName
	 */
	private void deletParticipant(String participantName, List<Participant> participants) {
		for (int i = 0; i < participants.size(); i++) {
			if (participants.get(i).getName().equals(participantName))
				participants.remove(i);
		}
	}

}
