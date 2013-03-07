/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.participant;

import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ERR_MSG;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.COMMON_PAGE_ERROR;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ERR_MSG_USER_ALREADY_EXIST;

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
 * Servlet implementation class AddParticipantServlet
 * 
 * @author dongdong
 * @version 1.0
 * @createDate Jul 19, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebServlet(description = "Add a participant into a collaboration", urlPatterns = { "/AddParticipant" })
public class AddParticipantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AddParticipantServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		Participant participant = new Participant();
		participant.setName(request.getParameter("username"));
		participant.setEmail(request.getParameter("email"));
		participant.setRole(request.getParameter("role"));
		participant.setGroup(request.getParameter("group"));

		@SuppressWarnings("unchecked")
		List<Participant> participants = (List<Participant>) session.getAttribute(ATTRIBUTE_ADDED_PARTICIPANTS);
		boolean isAdded = addParticipant(participant, participants);

		if (!isAdded) {
			session.setAttribute(ATTRIBUTE_ERR_MSG, ERR_MSG_USER_ALREADY_EXIST);
			response.sendRedirect(COMMON_PAGE_ERROR);
		} else {
			session.setAttribute(ATTRIBUTE_ADDED_PARTICIPANTS, null);
			session.setAttribute(ATTRIBUTE_ADDED_PARTICIPANTS, participants);
			response.sendRedirect("view.jsp");
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
	 * Add a participant into the list
	 * 
	 * @param participant
	 * @return is success or not
	 */
	private boolean addParticipant(Participant participant, List<Participant> participants) {
		boolean isAdded = true;

		for (Participant p : participants) {
			if (participant.getName().equals(p.getName())) {
				isAdded = false;
				break;
			}
		}

		if (isAdded) {
			participants.add(participant);
		}

		return isAdded;
	}

}
