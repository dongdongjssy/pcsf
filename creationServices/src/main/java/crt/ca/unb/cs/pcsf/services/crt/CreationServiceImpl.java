/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.crt;

import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_ATTRIBUTE_CREATOR_ID;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_ATTRIBUTE_CURRENT_STATE;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_ATTRIBUTE_NAME;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_ATTRIBUTE_PACKAGE;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_ATTRIBUTE_PARTICIPANT;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_STATE_DEPLOYED;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.COLLABORATION_STATE_NEW_CREATED;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.CREATOR_ATTRIBUTE_EMAIL;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.CREATOR_ATTRIBUTE_NAME;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.CREDENTIAL_FILE_PATH;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.DOMAIN_COLLABORATION;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.DOMAIN_CREATOR;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.DOMAIN_PARTICIPANT;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.LINK_CREATOR;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.LINK_PARTICIPANT;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.LOGPRE;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAIL_COMMON_CONTENT_FOR_CREATOR;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAIL_COMMON_CONTENT_FOR_PARTICIPANT;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAIL_FROM;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAIL_SUBJECT;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.MAX;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_ATTRIBUTE_COLLABORATION_ID;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_ATTRIBUTE_EMAIL;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_ATTRIBUTE_GROUP;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_ATTRIBUTE_IS_REG;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_ATTRIBUTE_NAME;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_ATTRIBUTE_ROLE;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.PARTICIPANT_IS_REG_NO;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.SAVE_PATH;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.SERVICES_DEPLOY_LOCATION;
import static ca.unb.cs.pcsf.services.crt.CreationServiceConstants.SERVICES_SOURCE_LOCATION;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.jws.WebService;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.CreateDomainRequest;
import com.amazonaws.services.simpledb.model.DeleteAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.PutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.ListVerifiedEmailAddressesResult;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

/**
 * @author dongdong
 * @version 0.0.1
 * @date Jul 8, 2012
 * 
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.crt.CreationService")
public class CreationServiceImpl implements CreationService {

	private Logger logger = Logger.getLogger(CreationServiceImpl.class.getName());
	private AmazonS3 s3;
	private AmazonSimpleDB sdb;
	private AmazonSimpleEmailService ses;
	private PropertiesCredentials credentials;

	/**
	 * Constructor: create workspaces for pcsf
	 */
	public CreationServiceImpl() {
		try {
			credentials = new PropertiesCredentials(CreationServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH));
			s3 = new AmazonS3Client(credentials);
			sdb = new AmazonSimpleDBClient(credentials);
			ses = new AmazonSimpleEmailServiceClient(credentials);

			createDomain(DOMAIN_CREATOR); // create domain "Creator"
			createDomain(DOMAIN_COLLABORATION);// create domain "Collaboration"
			createDomain(DOMAIN_PARTICIPANT); // create domain "Participant"
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("===========================================");
		logger.info("Creation service has started!");
		logger.info("===========================================");

		logger.info("Creating temporary workspace folders for pcsf...");
		File savePath = new File(SAVE_PATH);
		if (!savePath.exists() || !savePath.isDirectory()) {
			logger.info(" - creating collaboration home folder <pcsf>...");
			savePath.mkdir();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.crtservice.CreationService#createCollaboration(java.lang.String, java.util.List,
	 * java.lang.String, java.lang.String)
	 */
	public boolean createCollaboration(String collaborationName, List<String> participants, String creatorId,
			File workflowFile) {
		logger.debug(LOGPRE + "createCollaboration() start" + LOGPRE);

		// create bucket if not exist
		logger.info("checking Amazon S3 bucket...");
		String bucketName = "pcsf-s3-bucket-" + collaborationName;

		boolean isBucketExist = false;
		List<Bucket> bucketList = s3.listBuckets();
		for (Bucket bucket : bucketList) {
			if (bucket.getName().equals(bucketName))
				isBucketExist = true;
		}

		if (!isBucketExist) {
			logger.info("bucket <" + bucketName + "> not exist, creating the bucket...");
			s3.createBucket(bucketName);
		}

		// upload file to bucket
		logger.info("uploading file into S3 bucket...");
		String key = workflowFile.getName();
		s3.putObject(new PutObjectRequest(bucketName, key, workflowFile));

		logger.info("file <" + workflowFile.getName() + "> has been uploaded to bucket <" + bucketName + "> with key <"
				+ key + ">");

		// create collaboration
		String collaborationId = this.idGenerator(DOMAIN_COLLABORATION);

		List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
		ReplaceableItem item = new ReplaceableItem(collaborationId);
		item.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_NAME, collaborationName, true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CREATOR_ID, creatorId, true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE, COLLABORATION_STATE_NEW_CREATED, true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL, workflowFile.getAbsolutePath(), true));

		for (String p : participants) {
			String[] infos = p.split(",");
			if (infos.length == 4) {
				createParticipant(infos[0], infos[1], infos[2], infos[3], collaborationId);
				item.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, infos[0], true));
			}
		}

		items.add(item);
		logger.info("Putting collaboration <" + collaborationName + "> into domain...");
		sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, items));
		
		logger.info("Packing collaboration services...");
		packServices(collaborationName, collaborationId);
		
		logger.info("Collaboration <" + collaborationName + "> has been created successfully!");

		logger.debug(LOGPRE + "createCollaboration() end" + LOGPRE);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.crtservice.CreationService#deployCollaboration(java.lang.String)
	 */
	public boolean deployCollaboration(String collaborationId) {
		// query collaboration from db
		String collaborationName = "";
		String creatorId = "";
		List<String> participantNames = new ArrayList<String>();

		Item findItem = new Item();

		String getCollaborationRequest = "select * from `" + DOMAIN_COLLABORATION + "`";
		findItem = findItem(collaborationId, getCollaborationRequest);

		if (findItem != null) {
			for (Attribute attribute : findItem.getAttributes()) {
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME))
					collaborationName = attribute.getValue();
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CREATOR_ID))
					creatorId = attribute.getValue();
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
					participantNames.add(attribute.getValue());
			}

			logger.info("Deploying collaboration...");
			// generate collaboration services
			logger.info("Generating collaboration services...");
			generateServices(collaborationName);

			// change the state of this collaboration.
			logger.info("Updating collaboration <" + collaborationName + "> state to be" + COLLABORATION_STATE_DEPLOYED);
			List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();
			replaceableAttributes.add(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE,
					COLLABORATION_STATE_DEPLOYED, true));
			sdb.putAttributes(new PutAttributesRequest(DOMAIN_COLLABORATION, collaborationId, replaceableAttributes));

			// send notification to each participant
			logger.info("Sending an email to each participant...");
			for (String p : participantNames) {
				String email = "";
				String id = "";
				String getParticipantRequest = "select * from `" + DOMAIN_PARTICIPANT + "` where "
						+ PARTICIPANT_ATTRIBUTE_NAME + " = '" + p + "'";
				List<Item> items = sdb.select(new SelectRequest(getParticipantRequest)).getItems();

				if (items != null) {
					Item item = items.get(0);
					id = item.getName();
					for (Attribute attribute : item.getAttributes()) {
						if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_EMAIL)) {
							email = attribute.getValue();
							break;
						}
					}
				}
				sendParticipantNotificationMail(email, p, id);
			}

			// send notification email to creator
			logger.info("Sending an email to creator...");

			String creatorEmail = "";
			String creatorName = "";

			String getCreatorRequest = "select * from `" + DOMAIN_CREATOR + "`";
			findItem = findItem(creatorId, getCreatorRequest);
			if (findItem != null) {
				for (Attribute attribute : findItem.getAttributes()) {
					if (attribute.getName().equals(CREATOR_ATTRIBUTE_NAME))
						creatorName = attribute.getValue();
					if (attribute.getName().equals(CREATOR_ATTRIBUTE_EMAIL))
						creatorEmail = attribute.getValue();
				}
			}
			sendCreatorNotificationMail(creatorEmail, creatorName);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.crtservice.CreationService#updateCollaboration(java.lang.String, java.util.List,
	 * java.lang.String)
	 */
	public boolean updateCollaboration(String collaborationId, String collaborationName, List<String> participants,
			String workflow) {
		logger.debug(LOGPRE + "updateCollaboration() start" + LOGPRE);

		logger.info("Collaboration <" + collaborationName + "> has been updated Successfully!");
		logger.debug(LOGPRE + "updateCollaboration() end" + LOGPRE);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.crtservice.CreationService#deleteCollaboration(java.lang.String)
	 */
	public boolean deleteCollaboration(String collaborationId) {
		logger.debug(LOGPRE + "deleteCollaboration() start" + LOGPRE);

		// find collaboration name and work flow file path
		String collaborationName = "";
		String workflowFilePath = "";

		Item findItem = new Item();

		String getCollaborationRequest = "select * from `" + DOMAIN_COLLABORATION + "`";
		findItem = findItem(collaborationId, getCollaborationRequest);
		if (findItem != null) {
			for (Attribute attribute : findItem.getAttributes()) {
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME))
					collaborationName = attribute.getValue();
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL))
					workflowFilePath = attribute.getValue();
			}
		}

		logger.info("Deleting collaboration <" + collaborationName + ">...");
		File collaborationFolder = new File(workflowFilePath).getParentFile();

		// delete collaboration services
		logger.info("Deleting collaboration services...");
		File[] services = new File(SERVICES_DEPLOY_LOCATION).listFiles();
		if (null != services) {
			for (File service : services) {
				if (service.isFile() && service.getName().startsWith(collaborationName)) {
					logger.info(" - deleting service <"
							+ service.getName().substring(0, service.getName().length() - 4) + ">");
					delFile(service);
				}
				if (service.isDirectory() && service.getName().startsWith(collaborationName)) {
					delDirectory(service);
				}
			}
		}

		// delete work flow file
		logger.info("Deleting workflow file...");
		delDirectory(collaborationFolder);

		// delete collaboration from db
		if (isCollaborationExist(collaborationName)) {
			logger.info("Deleting participants...");
			List<String> participantNames = new ArrayList<String>();

			String selectRequest = "select * from `" + DOMAIN_COLLABORATION + "`";
			findItem = findItem(collaborationId, selectRequest);

			if (findItem != null) {
				for (Attribute attribute : findItem.getAttributes()) {
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
						participantNames.add(attribute.getValue());
				}
			}

			for (String p : participantNames) {
				logger.info(" - deleting participant <" + p + ">...");
				String pid = "";
				String getParticipantRequest = "select * from `" + DOMAIN_PARTICIPANT + "` where "
						+ PARTICIPANT_ATTRIBUTE_NAME + " = '" + p + "'";
				List<Item> items = sdb.select(new SelectRequest(getParticipantRequest)).getItems();

				if (items != null) {
					Item item = items.get(0);
					pid = item.getName();
				}
				sdb.deleteAttributes(new DeleteAttributesRequest(DOMAIN_PARTICIPANT, pid));
			}
			sdb.deleteAttributes(new DeleteAttributesRequest(DOMAIN_COLLABORATION, collaborationId));

			logger.debug(LOGPRE + "deleteCollaboration() end" + LOGPRE);
		}

		// delete bucket from s3
		logger.info("Deleting bucket from s3...");
		String bucketName = "pcsf-s3-bucket-" + collaborationName;
		s3.deleteObject(bucketName, collaborationName + ".zip");
		s3.deleteObject(bucketName, new File(workflowFilePath).getName());
		s3.deleteBucket(bucketName);

		logger.info("Delete Done!");
		logger.info("Collaboration <" + collaborationName + "> has been deleted successfully!");
		logger.debug(LOGPRE + "deleteCollaboration() end" + LOGPRE);
		return true;
	}

	/**
	 * Create a new participant.
	 * 
	 * @param name
	 * @param email
	 * @param collaborationId
	 * @return a new participant
	 */
	private void createParticipant(String name, String email, String role, String group, String collaborationId) {
		logger.debug(LOGPRE + "createParticipant() start" + LOGPRE);

		String id = this.idGenerator(DOMAIN_PARTICIPANT);

		List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
		items.add(new ReplaceableItem(id).withAttributes(new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_NAME, name,
				true), new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_EMAIL, email, true), new ReplaceableAttribute(
				PARTICIPANT_ATTRIBUTE_COLLABORATION_ID, collaborationId, false), new ReplaceableAttribute(
				PARTICIPANT_ATTRIBUTE_IS_REG, PARTICIPANT_IS_REG_NO, true), new ReplaceableAttribute(
				PARTICIPANT_ATTRIBUTE_ROLE, role, true), new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_GROUP, group,
				true)));

		logger.info("Adding participant <" + name + "> into domain...");
		sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_PARTICIPANT, items));

		logger.debug(LOGPRE + "createParticipant() end" + LOGPRE);
	}

	/**
	 * Generate collaboration services.
	 * 
	 * @param collaborationName
	 */
	private void generateServices(String collaborationName) {
		logger.debug(LOGPRE + "generateServices() start" + LOGPRE);

		String serviceLocation = "";
		String pcsfEnv = System.getenv("COL_SVR_DEPLOY_LOC");
		if (pcsfEnv.endsWith(File.separator)) {
			serviceLocation = pcsfEnv + SERVICES_SOURCE_LOCATION;
		} else {
			serviceLocation = pcsfEnv + File.separator + SERVICES_SOURCE_LOCATION;
		}
		// get all files and folders in source folder
		File[] files = (new File(serviceLocation)).listFiles();

		if (files != null && files.length > 0) {
			for (File file : files) {
				if (file.isFile() && !(file.getName().startsWith(".")) && !(file.getName().contains("pcsfWeb"))) {
					logger.info(" - generating collaboration service <" + file.getName() + ">...");
					try {
						String deployedName = SERVICES_DEPLOY_LOCATION + File.separator + collaborationName + "-"
								+ file.getName();
						copyFile(file, new File(deployedName));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (file.isDirectory() && !(file.getName().startsWith(".")) && !(file.getName().contains("pcsfWeb"))) {
					logger.info(" - generating collaboration service <" + file.getName() + ">...");
					String sourceDir = serviceLocation + File.separator + file.getName();
					String targetDir = SERVICES_DEPLOY_LOCATION + File.separator + collaborationName + "-"
							+ file.getName();
					try {
						copyDirectory(sourceDir, targetDir);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		logger.debug(LOGPRE + "generateServices() end" + LOGPRE);
	}

	/**
	 * Pack services to be a deployable package
	 * 
	 * @param collaborationName
	 * @param collaborationId
	 */
	private void packServices(String collaborationName, String collaborationId) {
		logger.debug(LOGPRE + "packServices() start" + LOGPRE);

		try {
			String serviceLocation = "";
			String pcsfEnv = System.getenv("COL_SVR_DEPLOY_LOC");
			if (pcsfEnv.endsWith(File.separator)) {
				serviceLocation = pcsfEnv + SERVICES_SOURCE_LOCATION;
			} else {
				serviceLocation = pcsfEnv + File.separator + SERVICES_SOURCE_LOCATION;
			}
			// get all files and folders in source folder and compress them into a zip file
			File[] files = (new File(serviceLocation)).listFiles();

			// temp folder
			String destPath = SAVE_PATH + File.separator + "tmp";
			File destFolder = new File(destPath);
			if (!destFolder.exists() || !destFolder.isDirectory())
				destFolder.mkdir();

			// compress files
			File zipFile = new File(SAVE_PATH + File.separator + collaborationName + File.separator + collaborationName
					+ ".zip");
			ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));

			if (files.length > 0 && files != null) {
				FileInputStream inputStream = null;
				for (File file : files) {
					if (file.isFile() && !(file.getName().startsWith("."))) {
						logger.info(" - adding <" + file.getName() + "> into <" + collaborationName + ".zip> ...");

						String newServiceName = destPath + File.separator + collaborationName + "-" + file.getName();
						File tmpFile = new File(newServiceName);
						copyFile(file, tmpFile);

						inputStream = new FileInputStream(tmpFile);
						zipOutputStream.putNextEntry(new ZipEntry(tmpFile.getName()));
						int len;
						while ((len = inputStream.read()) != -1) {
							zipOutputStream.write(len);
						}
					}
				}
				inputStream.close();
			}

			zipOutputStream.close();
			delDirectory(destFolder);

			// upload file to bucket
			logger.info("uploading zip file into S3 bucket...");
			String bucketName = "pcsf-s3-bucket-" + collaborationName;
			String key = zipFile.getName();
			s3.putObject(new PutObjectRequest(bucketName, key, zipFile));

			logger.info("file <" + zipFile.getName() + "> has been uploaded to bucket <" + bucketName + "> with key <"
					+ key + ">");

			List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
			ReplaceableItem item = new ReplaceableItem(collaborationId);
			item.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PACKAGE, bucketName + "," + key, true));
			items.add(item);
			sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, items));

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		logger.debug(LOGPRE + "packServices() end" + LOGPRE);
	}

	/**
	 * Copy file
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws IOException
	 */
	private void copyFile(File sourceFile, File targetFile) throws IOException {
		logger.debug(LOGPRE + "copyFile() start" + LOGPRE);

		logger.debug("generating file: " + sourceFile);

		FileInputStream input = new FileInputStream(sourceFile);
		BufferedInputStream inBuff = new BufferedInputStream(input);

		FileOutputStream output = new FileOutputStream(targetFile);
		BufferedOutputStream outBuff = new BufferedOutputStream(output);

		byte[] b = new byte[1024 * 5];
		int len;
		while ((len = inBuff.read(b)) != -1) {
			outBuff.write(b, 0, len);
		}
		outBuff.flush();

		inBuff.close();
		outBuff.close();
		output.close();
		input.close();

		logger.debug(LOGPRE + "copyFile() end" + LOGPRE);
	}

	/**
	 * Copy directory
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	private void copyDirectory(String sourceDir, String targetDir) throws IOException {
		logger.debug(LOGPRE + "copyDirectory() start" + LOGPRE);

		logger.debug("generating folder " + targetDir);

		(new File(targetDir)).mkdirs();
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				File sourceFile = file[i];
				File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
				copyFile(sourceFile, targetFile);
			}
			if (file[i].isDirectory()) {
				String dir1 = sourceDir + File.separator + file[i].getName();
				String dir2 = targetDir + File.separator + file[i].getName();
				copyDirectory(dir1, dir2);
			}
		}

		logger.debug(LOGPRE + "copyDirectory() start" + LOGPRE);
	}

	/**
	 * Delete file
	 * 
	 * @param file
	 */
	private void delFile(File file) {
		logger.debug(LOGPRE + "delFile() start" + LOGPRE);

		if (file.exists() && file.isFile())
			file.delete();

		logger.debug(LOGPRE + "delFile() start" + LOGPRE);
	}

	/**
	 * Delete directory
	 * 
	 * @param directory
	 */
	private void delDirectory(File directory) {
		logger.debug(LOGPRE + "delDirectory() start" + LOGPRE);

		if (directory.exists()) {
			File[] children = directory.listFiles();
			if (children == null || children.length <= 0)
				directory.delete();
			else {
				for (File child : children) {
					if (child.exists() && child.isFile())
						child.delete();
					if (child.exists() && child.isDirectory())
						delDirectory(child);
				}
				directory.delete();
			}
		}

		logger.debug(LOGPRE + "delDirectory() end" + LOGPRE);
	}

	/**
	 * Send a notification to creator notifying new collaboration creation.
	 * 
	 * @param creatorEmail
	 * @param creatorName
	 */
	private void sendCreatorNotificationMail(String creatorEmail, String creatorName) {
		logger.debug(LOGPRE + "sendCreatorNotificationMail() start" + LOGPRE);

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
			logger.info("one mail sent to creator notifying the new created collaboration!");

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

		logger.debug(LOGPRE + "sendCreatorNotificationMail() end" + LOGPRE);
	}

	/**
	 * Send a notification to participant notifying new collaboration creation.
	 * 
	 * @param pEmail
	 * @param pName
	 */
	private void sendParticipantNotificationMail(String pEmail, String pName, String pId) {
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
			msg.setText("Dear " + pName + ",\n\n" + MAIL_COMMON_CONTENT_FOR_PARTICIPANT + "Your id: " + pId + "\n"
					+ LINK_PARTICIPANT);
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
					+ "problem sending your message to Amazon's E-mail Service check the "
					+ "stack trace for more information.");
		}

		logger.debug(LOGPRE + "sendParticipantNotificationMail() end" + LOGPRE);
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

	/**
	 * Generate a random id for new user or collaboration.
	 * 
	 * @param domainName
	 * @return random id
	 */
	private String idGenerator(String domainName) {
		logger.debug(LOGPRE + "idGenerator() start" + LOGPRE);

		Random random = new Random(System.currentTimeMillis());
		String id = String.valueOf(((int) (random.nextDouble() * MAX)) + 1);
		boolean isDuplicated = true;

		String selectExpression = "";
		if (domainName.equals(DOMAIN_PARTICIPANT)) {
			logger.info("generating id for participant ...");
			selectExpression = "select * from `" + DOMAIN_PARTICIPANT + "`";
		}
		if (domainName.equals(DOMAIN_COLLABORATION)) {
			logger.info("generating id for collaboration ...");
			selectExpression = "select * from `" + DOMAIN_COLLABORATION + "`";
		}
		if (domainName.equals(DOMAIN_CREATOR)) {
			logger.info("generating id for creator ...");
			selectExpression = "select * from `" + DOMAIN_CREATOR + "`";
		}

		List<String> ids = new ArrayList<String>();
		List<Item> items = sdb.select(new SelectRequest(selectExpression)).getItems();
		if (!items.isEmpty()) {
			for (Item item : items) {
				ids.add(item.getName());
			}
		}
		while (isDuplicated) {
			isDuplicated = false;
			for (String s : ids) {
				if (s.equals(id)) {
					isDuplicated = true;
					id = String.valueOf(((int) (random.nextDouble() * MAX)) + 1);
					break;
				}
			}
		}

		logger.debug(LOGPRE + "idGenerator() end" + LOGPRE);
		return id;
	}

	/**
	 * create a domain
	 * 
	 * @param domainName
	 * @return if the domain been created successfully
	 */
	private void createDomain(String domainName) {
		logger.debug(LOGPRE + "createDomain() start" + LOGPRE);

		boolean isDomainExist = false;
		for (String dn : sdb.listDomains().getDomainNames()) {
			if (dn.equals(domainName)) {
				logger.debug("Domain <" + domainName + "> exists");
				isDomainExist = true;
			}
		}

		if (!isDomainExist) {
			logger.info("Creating domain called <" + domainName + ">...");
			sdb.createDomain(new CreateDomainRequest(domainName));
			logger.info("Domain <" + domainName + "> has been created");
		}

		logger.debug(LOGPRE + "createDomain() end" + LOGPRE);
	}

	/**
	 * find a record by id from db
	 * 
	 * @param id
	 * @param selectRequest
	 * @return the find record
	 */
	private Item findItem(String id, String selectRequest) {
		logger.debug(LOGPRE + "findItem() start" + LOGPRE);

		logger.debug("Selecting: " + selectRequest);
		logger.debug("Getting data...");
		List<Item> items = sdb.select(new SelectRequest(selectRequest)).getItems();
		Item findItem = new Item();
		if (!items.isEmpty()) {
			for (Item item : items) {
				if (item.getName().equals(id)) {
					findItem = item;
					break;
				}
			}
		}

		logger.debug(LOGPRE + "findItem() end" + LOGPRE);
		return findItem;
	}

	/**
	 * check if collaboration exist
	 * 
	 * @param collaborationName
	 * @return
	 */
	private boolean isCollaborationExist(String collaborationName) {
		logger.debug(LOGPRE + "isCollaborationExist() start" + LOGPRE);

		String selectRequest = "select * from `" + DOMAIN_COLLABORATION + "` where " + COLLABORATION_ATTRIBUTE_NAME
				+ " = '" + collaborationName + "'";
		List<Item> items = sdb.select(new SelectRequest(selectRequest)).getItems();
		if (items.isEmpty()) {
			logger.debug("Collaboration <" + collaborationName + "> doesn't exist");
			logger.debug(LOGPRE + "isCollaborationExist() end" + LOGPRE);
			return false;
		} else {
			logger.debug("Collaboration <" + collaborationName + "> exists");
			logger.debug(LOGPRE + "isCollaborationExist() end" + LOGPRE);
			return true;
		}
	}
}
