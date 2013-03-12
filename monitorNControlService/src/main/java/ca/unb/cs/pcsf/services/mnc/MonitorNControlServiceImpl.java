/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.mnc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.jws.WebService;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.mnc.MonitorNControlService")
public class MonitorNControlServiceImpl implements MonitorNControlService {
  Logger logger = Logger.getLogger(MonitorNControlServiceImpl.class.getName());

  private static final String LOGPRE = ">>>>";
  private static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";
  private static final String DOMAIN_PARTICIPANT = "Participant";
  private static final String DOMAIN_COLLABORATION = "Collaboration";
  private static final String COLLABORATION_ATTRIBUTE_NAME = "Name";
  private static final String COLLABORATION_ATTRIBUTE_PARTICIPANT = "Participant";
  private static final String PARTICIPANT_ATTRIBUTE_IS_REG = "IsReg";
  private static final String PARTICIPANT_ATTRIBUTE_NAME = "Name";
  private static final String PARTICIPANT_ATTRIBUTE_EMAIL = "Email";
  private static final String PARTICIPANT_ATTRIBUTE_ROLE = "Role";
  private static final String PARTICIPANT_IS_REG_NO = "no";

  private static final String WSURL_PRE = "http://localhost:8080/";
  // mail
  private static final String MAIL_FROM = "pcsf.notification@gmail.com";
  private static final String MAIL_SUBJECT = "Collaboration Notification From PCSF";
  private static final String MAIL_CONTENT_REMIND_PARTICIPANT_TO_REG = "You are asked to participant a collaboration!\nThis is a remind email notifying you to do the registration!\nPlease click the following link to register into the collaboration using your given id:\n\n";
  private static final String MAIL_CONTENT_REMIND_PARTICIPANT_SUBMIT_TASK = "This is a remind email notifying you to submit the task!\nPlease click the following link to view your task using your given id:\n\n";
  private static final String LINK_PARTICIPANT = "http://ec2-50-16-63-235.compute-1.amazonaws.com/:8080/pcsf/index.jsp?action=participantLogin";

  private Timer timer = new Timer();
  private AmazonSimpleDB sdb;
  private AmazonSimpleEmailService ses;
  private PropertiesCredentials credentials;

  private String collaborationName;
  private String collaborationId;
  private String participantNames;

  /**
   * Constructor
   */
  public MonitorNControlServiceImpl() {
    // get credentials
    try {
      credentials = new PropertiesCredentials(
          MonitorNControlServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH));
      sdb = new AmazonSimpleDBClient(credentials);
      ses = new AmazonSimpleEmailServiceClient(credentials);
    } catch (IOException e) {
      e.printStackTrace();
    }

    getCollaboration();
    startMonitor();
    logger.info("===========================================");
    logger.info("Monitor and Control Service has started!");
    logger.info("===========================================");
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.mnc.MonitorNControlService#startService()
   */
  @Override
  public void startMonitor() {
    TimerTask registrationRequest = new RegisterRequest();
    TimerTask taskSubmitReques = new TaskSubmitRequest();

    timer.schedule(registrationRequest, 86400000, 86400000);
    timer.schedule(taskSubmitReques, 86400000, 86400000);
  }

  /*
   * (non-Javadoc)
   * 
   * @see ca.unb.cs.pcsf.services.mnc.MonitorNControlService#endMonitor()
   */
  @Override
  public void endMonitor() {
    timer.cancel();
  }

  /**
   * Get collaboration details from the database
   */
  private void getCollaboration() {
    String curPath = this.getClass().getResource("/").getPath();
    String[] curPaths = curPath.split("/");

    for (String s : curPaths) {
      if (s.contains("monitorNControlService"))
        this.collaborationName = s.split("-")[0];
    }

    String selectExp = "select * from `" + DOMAIN_COLLABORATION + "` where " + COLLABORATION_ATTRIBUTE_NAME + "='"
        + collaborationName + "'";
    Item findItem = sdb.select(new SelectRequest(selectExp)).getItems().get(0);

    if (findItem != null) {
      collaborationId = findItem.getName();
      for (Attribute attribute : findItem.getAttributes()) {
        if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
          participantNames = attribute.getValue();
      }
    }
  }

  /**
   * A general method used to call web service.
   * 
   * @param wsUrl
   *          web service url
   * @param method
   *          method name
   * @param arg
   *          method parameters
   * @return results
   */
  private Object[] callService(String wsUrl, String method, Object... arg) {
    logger.debug(LOGPRE + "callService() start" + LOGPRE);

    Object[] resutls = null;
    JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
    Client client = dcf.createClient(wsUrl);
    try {
      resutls = client.invoke(method, arg);
    } catch (Exception e) {
      e.printStackTrace();
    }

    logger.debug(LOGPRE + "callService() end" + LOGPRE);
    return resutls;
  }

  /**
   * Send a notification to participant notifying new collaboration creation.
   * 
   * @param pEmail
   * @param pName
   */
  private void sendParticipantNotificationMail(String pEmail, String pName, String pId, String mailContent) {
    logger.debug(LOGPRE + "sendParticipantNotificationMail() start" + LOGPRE);

    this.verifyEmailAddress(ses);
    Properties props = new Properties();
    props.setProperty("mail.transport.protocol", "aws");
    props.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
    props.setProperty("mail.aws.password", credentials.getAWSSecretKey());

    Session session = Session.getInstance(props);
    try {
      // Create a new Message
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(MAIL_FROM));
      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(pEmail));
      msg.setSubject(MAIL_SUBJECT);
      msg.setText("Dear " + pName + ",\n\n" + mailContent + "Your id: " + pId + "\n" + LINK_PARTICIPANT);
      msg.saveChanges();

      // Reuse one Transport object for sending all your messages
      // for better performance
      Transport t = new AWSJavaMailTransport(session, null);
      t.connect();
      t.sendMessage(msg, null);
      logger.info("one mail sent to participant notifying the new created collaboration!");

      t.close();
    } catch (AddressException e) {
      e.printStackTrace();
      logger.info("Caught an AddressException, which means one or more of your "
          + "addresses are improperly formatted.");
    } catch (MessagingException e) {
      e.printStackTrace();
      logger.info("Caught a MessagingException, which means that there was a "
          + "problem sending your message to Amazon's E-mail Service check the " + "stack trace for more information.");
    }

    logger.debug(LOGPRE + "sendParticipantNotificationMail() end" + LOGPRE);
  }

  /**
   * Sends a request to Amazon Simple Email Service to verify the specified email address. This triggers a verification
   * email, which will contain a link that you can click on to complete the verification process.
   * 
   * @param ses
   *          The Amazon Simple Email Service client to use when making requests to Amazon SES.
   * @param address
   *          The email address to verify.
   */
  private void verifyEmailAddress(AmazonSimpleEmailService ses) {
    logger.debug(LOGPRE + "verifyEmailAddress() start" + LOGPRE);

    ListVerifiedEmailAddressesResult verifiedEmails = ses.listVerifiedEmailAddresses();
    if (verifiedEmails.getVerifiedEmailAddresses().contains(MAIL_FROM)) {
      logger.debug(LOGPRE + "verifyEmailAddress() end" + LOGPRE);
      return;
    }

    ses.verifyEmailAddress(new VerifyEmailAddressRequest().withEmailAddress(MAIL_FROM));
    logger.info("Please check the email address " + MAIL_FROM + " to verify it");

    logger.debug(LOGPRE + "verifyEmailAddress() end" + LOGPRE);
  }

  /**
   * Request the participant to do registration after a long time interval
   */
  class RegisterRequest extends TimerTask {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
      // get the registration state of all participants
      String[] participants = participantNames.split(":");
      for (String participant : participants) {
        String selectRequest = "select * from `" + DOMAIN_PARTICIPANT + "`";
        List<Item> items = sdb.select(new SelectRequest(selectRequest)).getItems();
        Item findItem = new Item();
        String participantIsReg = "";
        String participantEmail = "";
        String participantName = "";
        String participantId = "";

        if (!items.isEmpty()) {
          for (Item item : items) {
            if (item.getName().equals(participant)) {
              findItem = item;
              break;
            }
          }
        }

        if (findItem != null) {
          participantId = findItem.getName();
          for (Attribute attribute : findItem.getAttributes()) {
            if (attribute.getValue().equals(PARTICIPANT_ATTRIBUTE_IS_REG))
              participantIsReg = attribute.getValue();
            if (attribute.getValue().equals(PARTICIPANT_ATTRIBUTE_EMAIL))
              participantEmail = attribute.getValue();
            if (attribute.getValue().equals(PARTICIPANT_ATTRIBUTE_NAME))
              participantName = attribute.getValue();
          }
        }

        // send an email to those who has not done registration
        if (participantIsReg.equals(PARTICIPANT_IS_REG_NO))
          sendParticipantNotificationMail(participantEmail, participantName, participantId,
              MAIL_CONTENT_REMIND_PARTICIPANT_TO_REG);
      }
    }
  }

  /**
   * Request the user to submit task after a long time interval
   */
  class TaskSubmitRequest extends TimerTask {
    /*
     * (non-Javadoc)
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
      // get the assignee of the current task
      logger.info("get current task...");
      String url = WSURL_PRE + collaborationName + "-scheduleNCoordinateService/ScheduleNCoordinateService?wsdl";
      String method = "getCurrentTask";
      Object[] tskResults = callService(url, method, collaborationId);
      List<?> tskResultList = (ArrayList<?>) tskResults[0];
      for (Object o : tskResultList) {
        String s = (String) o;
        String[] infos = s.split(",");
        String assignee = infos[2];

        // send an email to the assignee if the task is running over time
        String participantEmail = "";
        String participantName = "";
        String participantId = "";
        String selectReq = "select * from `" + DOMAIN_PARTICIPANT + "` where " + PARTICIPANT_ATTRIBUTE_ROLE + " = '"
            + assignee + "'";
        List<Item> items = sdb.select(new SelectRequest(selectReq)).getItems();
        if (items != null) {
          for (Item findItem : items) {
            participantId = findItem.getName();
            for (Attribute attribute : findItem.getAttributes()) {
              if (attribute.getValue().equals(PARTICIPANT_ATTRIBUTE_EMAIL))
                participantEmail = attribute.getValue();
              if (attribute.getValue().equals(PARTICIPANT_ATTRIBUTE_NAME))
                participantName = attribute.getValue();
            }
            sendParticipantNotificationMail(participantEmail, participantName, participantId,
                MAIL_CONTENT_REMIND_PARTICIPANT_SUBMIT_TASK);
          }
        }
      }
    }
  }
}
