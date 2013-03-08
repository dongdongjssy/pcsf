/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.reg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 10, 2012
 * @email dong.dong@unb.ca
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.reg.RegistrationService")
public class RegistrationServiceImpl implements RegistrationService {
	private static final String LOGPRE = ">>>>";
	private static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";
	private static final String WSURL_PRE = "http://localhost:8080/";
	// mail
	private static final String MAIL_FROM = "pcsf.notification@gmail.com";
	private static final String MAIL_SUBJECT = "Collaboration Notification From PCSF";
	private static final String LINK_CREATOR = "http://ec2-50-16-63-235.compute-1.amazonaws.com/:8080/pcsf/index.jsp?action=creatorLogin";
	private static final String MAIL_COMMON_CONTENT_FOR_CREATOR = "All participants have registered into the collaboration!\nPlease click the following link to run it:\n\n";

	// simple db
	private static final String DOMAIN_CREATOR = "Creator";
	private static final String DOMAIN_PARTICIPANT = "Participant";
	private static final String DOMAIN_COLLABORATION = "Collaboration";

	private static final String PARTICIPANT_ATTRIBUTE_IS_REG = "IsReg";
	private static final String PARTICIPANT_ATTRIBUTE_COLLABORATION_ID = "CollaborationId";
	private static final String PARTICIPANT_ATTRIBUTE_NAME = "Name";

	private static final String COLLABORATION_ATTRIBUTE_NAME = "Name";
	private static final String COLLABORATION_ATTRIBUTE_CREATOR_ID = "CreatorId";
	private static final String COLLABORATION_ATTRIBUTE_PARTICIPANT = "Participant";

	private static final String CREATOR_ATTRIBUTE_NAME = "Name";
	private static final String CREATOR_ATTRIBUTE_EMAIL = "Email";

	private static final String PARTICIPANT_IS_REG_YES = "yes";
	private static final String PARTICIPANT_IS_REG_NO = "no";

	private Logger logger = Logger.getLogger(RegistrationServiceImpl.class.getName());

	private AmazonSimpleDB sdb;
	private AmazonSimpleEmailService ses;
	private PropertiesCredentials credentials;

	public RegistrationServiceImpl() {
		// get credentials
		try {
			credentials = new PropertiesCredentials(
					RegistrationServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH));
			sdb = new AmazonSimpleDBClient(credentials);
			ses = new AmazonSimpleEmailServiceClient(credentials);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("===========================================");
		logger.info("Registration Service has started!");
		logger.info("===========================================");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.reg.RegistrationService#setAsReg(java.lang.String )
	 */
	public void setAsReg(String participantId) {
		logger.debug(LOGPRE + "setAsReg() start" + LOGPRE);

		// get participant
		String isReg = "";
		String participantName = null;
		String collaborationId = null;

		String getParticipantRequest = "select * from `" + DOMAIN_PARTICIPANT + "`";
		List<Item> items = sdb.select(new SelectRequest(getParticipantRequest)).getItems();
		Item findItem = new Item();

		if (!items.isEmpty()) {
			for (Item item : items) {
				if (item.getName().equals(participantId)) {
					findItem = item;
					break;
				}
			}
		}

		if (findItem != null) {
			for (Attribute attribute : findItem.getAttributes()) {
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_IS_REG)) {
					isReg = attribute.getValue();
				}
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_COLLABORATION_ID)) {
					if (null == collaborationId)
						collaborationId = attribute.getValue();
				}
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_NAME)) {
					participantName = attribute.getValue();
				}
			}
		}

		// set participant as registered if not.
		if (isReg.equals(PARTICIPANT_IS_REG_NO)) {
			logger.info("registering participant <" + participantName + ">");
			List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
			replaceableAttributes.add(new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_IS_REG, PARTICIPANT_IS_REG_YES,
					true));

			sdb.putAttributes(new PutAttributesRequest(DOMAIN_PARTICIPANT, participantId, replaceableAttributes));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("done registeration!");

			// get collaboration
			String collaborationName = null;
			String participantNames = "";
			String creatorId = null;

			String getCollaborationRequest = "select * from `" + DOMAIN_COLLABORATION + "`";
			items = sdb.select(new SelectRequest(getCollaborationRequest)).getItems();

			if (!items.isEmpty()) {
				for (Item item : items) {
					if (item.getName().equals(collaborationId)) {
						findItem = item;
						break;
					}
				}
			}

			if (findItem != null) {
				for (Attribute attribute : findItem.getAttributes()) {
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME)) {
						if (collaborationName == null)
							collaborationName = attribute.getValue();
					}
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CREATOR_ID)) {
						if (creatorId == null)
							creatorId = attribute.getValue();
					}
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
						participantNames = attribute.getValue();
				}
			}

			// check if all participants have done registration.
			boolean isAllReg = true;
			String[] participants = participantNames.split(":");
			for (String participant : participants) {
				String selectRequest = "select * from `" + DOMAIN_PARTICIPANT + "`";
				items = sdb.select(new SelectRequest(selectRequest)).getItems();

				if (!items.isEmpty()) {
					for (Item item : items) {
						if (item.getName().equals(participant)) {
							findItem = item;
							break;
						}
					}
				}

				if (findItem != null) {
					for (Attribute attribute : findItem.getAttributes()) {
						if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_IS_REG)) {
							if (!attribute.getValue().equals(PARTICIPANT_IS_REG_YES)) {
								isAllReg = false;
								break;
							}
						}
					}
				}
			}

			if (isAllReg) {
				logger.info("all participants have done registration");

				// get creator
				String creatorEmail = null;
				String creatorName = null;
				String getCreatorRequest = "select * from `" + DOMAIN_CREATOR + "`";
				items = sdb.select(new SelectRequest(getCreatorRequest)).getItems();

				if (!items.isEmpty()) {
					for (Item item : items) {
						if (item.getName().equals(creatorId)) {
							findItem = item;
							break;
						}
					}
				}

				if (findItem != null) {
					for (Attribute attribute : findItem.getAttributes()) {
						if (attribute.getName().equals(CREATOR_ATTRIBUTE_NAME)) {
							if (creatorName == null)
								creatorName = attribute.getValue();
						}
						if (attribute.getName().equals(CREATOR_ATTRIBUTE_EMAIL)) {
							if (creatorEmail == null)
								creatorEmail = attribute.getValue();
						}
					}
				}

				// deploy a process.
				logger.info("preparing to start the process...");
				String url = WSURL_PRE + collaborationName
						+ "-scheduleNCoordinateService/ScheduleNCoordinateService?wsdl";
				String method = "deployProcess";
				callService(url, method, collaborationId);

				logger.info("sending an email to notify creator...");
				sendNotificationMail(creatorEmail, creatorName);
			}

		}

		logger.debug(LOGPRE + "setAsReg() end" + LOGPRE);
	}

	/**
	 * A general method used to call web service.
	 * 
	 * @param wsUrl
	 *            web service url
	 * @param method
	 *            method name
	 * @param arg
	 *            method parameters
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
	 * Send a notification to creator when all participants have done the registration.
	 * 
	 * @param role
	 * @param link
	 */
	private void sendNotificationMail(String creatorEmail, String creatorName) {
		logger.debug(LOGPRE + " sendNotificationMail() start " + LOGPRE);

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
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(creatorEmail));
			msg.setSubject(MAIL_SUBJECT);
			msg.setText("Dear " + creatorName + ",\n\n" + MAIL_COMMON_CONTENT_FOR_CREATOR + LINK_CREATOR);
			msg.saveChanges();

			// Reuse one Transport object for sending all your messages
			// for better performance
			Transport t = new AWSJavaMailTransport(session, null);
			t.connect();
			t.sendMessage(msg, null);
			logger.info("one mail sent to creator!");

			t.close();
		} catch (AddressException e) {
			e.printStackTrace();
			logger.info("Caught an AddressException, which means one or more of your "
					+ "addresses are improperly formatted.");
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.info("Caught a MessagingException, which means that there was a "
					+ "problem sending your message to Amazon's E-mail Service check the "
					+ "stack trace for more information.");
		}

		logger.debug(LOGPRE + " sendNotificationMail() end " + LOGPRE);
	}

	/**
	 * Sends a request to Amazon Simple Email Service to verify the specified email address. This triggers a
	 * verification email, which will contain a link that you can click on to complete the verification process.
	 * 
	 * @param ses
	 *            The Amazon Simple Email Service client to use when making requests to Amazon SES.
	 * @param address
	 *            The email address to verify.
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
}
