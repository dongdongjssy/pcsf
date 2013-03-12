/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.collaboration;

import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.LINK_PARTICIPANT;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAIL_FROM;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAIL_SUBJECT;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
  public static final String MAIL_CONTENT_PARTICIPANT_REG_REQUIRE = "You are asked to participant a collaboration!\nPlease click the"
      + " following link to finish registration using your given id:\n\n";

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
      sendParticipantNotificationMail(p.getEmail(), p.getName());

      try {
        Thread.sleep(3000);
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

    response.sendRedirect(request.getContextPath() + "/View?collaborationId=" + collaborationId);
  }

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  /**
   * Send a notification to creator notifying new collaboration creation.
   * 
   * @param pEmail
   * @param pName
   */
  private void sendParticipantNotificationMail(String pEmail, String pName) {

    Properties props = new Properties();
    props.setProperty("mail.smtp.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.port", "465");
    props.put("mail.smtp.auth", "true");
    Session sendMailSession = Session.getInstance(props, null);

    try {
      Transport transport = sendMailSession.getTransport("smtp");
      transport.connect("smtp.gmail.com", "pcsf.notification@gmail.com", "pcsf123456");
      Message newMessage = new MimeMessage(sendMailSession);

      newMessage.setSubject(MAIL_SUBJECT);
      newMessage.setFrom(new InternetAddress(MAIL_FROM));

      Address addressTo[] = { new InternetAddress(pEmail) };
      newMessage.setRecipients(Message.RecipientType.TO, addressTo);

      newMessage.setSentDate(new java.util.Date());
      newMessage.setText("Dear " + pName + ",\n\n" + MAIL_CONTENT_PARTICIPANT_REG_REQUIRE + LINK_PARTICIPANT);

      newMessage.saveChanges();
      transport.sendMessage(newMessage, newMessage.getRecipients(Message.RecipientType.TO));

      transport.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
