/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.collaboration;

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
@WebServlet(description = "Add Participant Confirm Servlet", urlPatterns = { "/AddParticipantConfirm" })
public class AddParticipantConfirmServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#HttpServlet()
   */
  public AddParticipantConfirmServlet() {
    super();
  }

  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession(true);
    String collaborationId = request.getParameter("collaborationId");
    PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
    String participantList = "";

    @SuppressWarnings("unchecked")
    List<Participant> participants = (List<Participant>) session.getAttribute(ATTRIBUTE_ADDED_PARTICIPANTS);
    for (int i = 0; i < participants.size(); i++) {
      Participant p = participants.get(i);
      dbAccess.putDataIntoDomain(p);

      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      if (i == participants.size() - 1)
        participantList += dbAccess.getParticipantByNameAndCollaborationId(p.getName(), p.getCollaborationId()).getId();
      else
        participantList += dbAccess.getParticipantByNameAndCollaborationId(p.getName(), p.getCollaborationId()).getId()
            + ":";
    }

    dbAccess.updateInstanceParticipantList(collaborationId, participantList);

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    response.sendRedirect(request.getContextPath() + "/View?collaborationId=" + collaborationId);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
