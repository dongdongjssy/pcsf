/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.creator;

import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_COLLABORATION_BEAN;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_CREATOR_BEAN;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_CREATOR_DISPLAY_MSG;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_CREATOR_NAME;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.MSG_COLLABORATION_CREATED;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.MSG_NO_COLLABORATION_CREATED;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ca.unb.cs.pcsf.web.db.Collaboration;
import ca.unb.cs.pcsf.web.db.Creator;
import ca.unb.cs.pcsf.web.db.Participant;
import ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class CreatorDisplayServlet
 * 
 * @author dongdong
 * @version 1.0
 * @createDate Jul 16, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebServlet(description = "Creator Display Servlet", urlPatterns = { "/CreatorDisplay" })
public class CreatorDisplayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreatorDisplayServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
		List<Participant> addedParticipants = new LinkedList<Participant>();

		// clear the session
		session.setAttribute(ATTRIBUTE_CREATOR_DISPLAY_MSG, null);
		session.setAttribute(ATTRIBUTE_COLLABORATION_BEAN, null);
		session.setAttribute(ATTRIBUTE_ADDED_PARTICIPANTS, null);

		Creator creator = (Creator) session.getAttribute(ATTRIBUTE_CREATOR_BEAN);

		// get all the collaborations been created by this creator
		List<Collaboration> collaborations = dbAccess
				.getCollaborationsByCreatorId(creator.getId());

		session.setAttribute(ATTRIBUTE_CREATOR_NAME, creator.getName());
		if (collaborations == null) {
			session.setAttribute(ATTRIBUTE_CREATOR_DISPLAY_MSG,
					MSG_NO_COLLABORATION_CREATED);
		} else {
			session.setAttribute(ATTRIBUTE_CREATOR_DISPLAY_MSG, MSG_COLLABORATION_CREATED);
			session.setAttribute(ATTRIBUTE_COLLABORATION_BEAN, collaborations);
		}

		session.setAttribute(ATTRIBUTE_ADDED_PARTICIPANTS, addedParticipants);
		response.sendRedirect("creatorPage.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
