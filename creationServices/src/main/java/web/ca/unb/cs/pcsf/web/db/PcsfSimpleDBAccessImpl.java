/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.db;

import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.*;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_ATTRIBUTE_CURRENT_STATE;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_ATTRIBUTE_NAME;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_ATTRIBUTE_PARTICIPANT;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.CREATOR_ATTRIBUTE_EMAIL;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.CREATOR_ATTRIBUTE_NAME;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.CREATOR_ATTRIBUTE_PASSWORD;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.CREDENTIAL_FILE_PATH;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.DOMAIN_COLLABORATION;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.DOMAIN_CREATOR;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.DOMAIN_PARTICIPANT;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.LOGPRE;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.PARTICIPANT_ATTRIBUTE_COLLABORATION_ID;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.PARTICIPANT_ATTRIBUTE_EMAIL;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.PARTICIPANT_ATTRIBUTE_IS_REG;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.PARTICIPANT_ATTRIBUTE_NAME;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.auth.PropertiesCredentials;
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

/**
 * @author dongdong
 * @version 1.0
 * @creatDate Jul 6, 2012
 * @email dong.dong@unb.ca
 * 
 */
public class PcsfSimpleDBAccessImpl implements PcsfSimpleDBAccess {
	private Logger logger = Logger.getLogger(PcsfSimpleDBAccessImpl.class.getName());

	private AmazonSimpleDB sdb;

	public PcsfSimpleDBAccessImpl() {
		initDB();
	}

	/**
	 * initialize the data base, create domains that not exist yet.
	 */
	private void initDB() {
		logger.debug(LOGPRE + "initDB() start" + LOGPRE);

		try {
			logger.debug("Initalizing database...");
			// get credentials
			sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
					PcsfSimpleDBAccessImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH)));

			createDomain(DOMAIN_CREATOR); // create domain "Creator"
			createDomain(DOMAIN_COLLABORATION);// create domain "Collaboration"
			createDomain(DOMAIN_PARTICIPANT); // create domain "Participant"

			logger.debug("Database initialized");

		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.debug(LOGPRE + "initDB() end" + LOGPRE);
	}

	/**
	 * create a domain
	 * 
	 * @param domainName
	 * @return if the domain been created successfully
	 */
	private void createDomain(String domainName) {
		logger.debug(LOGPRE + "createDomain() start" + LOGPRE);

		if (!isDomainExist(domainName)) {
			logger.info("Creating domain called <" + domainName + ">...");
			sdb.createDomain(new CreateDomainRequest(domainName));
			logger.info("Domain <" + domainName + "> has been created");
		}

		logger.debug(LOGPRE + "createDomain() end" + LOGPRE);
	}

	/**
	 * check if the domain exist or not
	 * 
	 * @param domainName
	 * @return if certain domain exist or not
	 */
	private boolean isDomainExist(String domainName) {
		logger.debug(LOGPRE + "isDomainExist() start" + LOGPRE);

		for (String dn : sdb.listDomains().getDomainNames()) {
			if (dn.equals(domainName)) {
				logger.debug("Domain <" + domainName + "> exists");
				logger.debug(LOGPRE + "isDomainExist() end" + LOGPRE);
				return true;
			}
		}

		logger.debug("Domain <" + domainName + "> doesn't exist");
		logger.debug(LOGPRE + "isDomainExist() end" + LOGPRE);
		return false;
	}

	/**
	 * select data from a domain
	 * 
	 * @param selectExpression
	 * @return the data selected from the domain
	 */
	private List<Item> getDataFromDomain(String selectExpression) {
		logger.debug(LOGPRE + "getDataFromDomain() start" + LOGPRE);
		logger.debug("Selecting: " + selectExpression);

		logger.debug("Getting data...");
		SelectRequest selectRequest = new SelectRequest(selectExpression);

		logger.debug(LOGPRE + "getDataFromDomain() end" + LOGPRE);
		return sdb.select(selectRequest).getItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#isCreatorExist(java.lang.String)
	 */
	public boolean isCreatorExist(String name) {
		logger.debug(LOGPRE + "isCreatorExist() start" + LOGPRE);

		String selectRequest = "select * from `" + DOMAIN_CREATOR + "` where " + CREATOR_ATTRIBUTE_NAME + " = '" + name
				+ "'";
		List<Item> items = this.getDataFromDomain(selectRequest);
		if (items.isEmpty()) {
			logger.info("Creator <" + name + "> doesn't exist");
			logger.debug(LOGPRE + "isCreatorExist() end" + LOGPRE);
			return false;
		} else {
			logger.info("Creator <" + name + "> exists");
			logger.debug(LOGPRE + "isCreatorExist() end" + LOGPRE);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#isCollaborationExist(java.lang.String)
	 */
	public boolean isCollaborationExist(String collaborationName) {
		logger.debug(LOGPRE + "isCollaborationExist() start" + LOGPRE);

		String selectRequest = "select * from `" + DOMAIN_COLLABORATION + "` where " + COLLABORATION_ATTRIBUTE_NAME
				+ " = '" + collaborationName + "'";
		List<Item> items = this.getDataFromDomain(selectRequest);
		if (items.isEmpty()) {
			logger.info("Collaboration <" + collaborationName + "> doesn't exist");
			logger.debug(LOGPRE + "isCollaborationExist() end" + LOGPRE);
			return false;
		} else {
			logger.info("Collaboration <" + collaborationName + "> exists");
			logger.debug(LOGPRE + "isCollaborationExist() end" + LOGPRE);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.PcsfSimpleDBAccess#isParticipantExist(java.lang.String)
	 */
	public boolean isParticipantExist(String participantId) {
		logger.debug(LOGPRE + "isParticipantExist() start" + LOGPRE);
		String selectRequest = "select * from `" + DOMAIN_PARTICIPANT + "`";
		List<Item> items = this.getDataFromDomain(selectRequest);
		Item findItem = null;

		if (!items.isEmpty()) {
			for (Item item : items) {
				if (item.getName().equals(participantId)) {
					findItem = item;
					break;
				}
			}
		}

		if (findItem == null) {
			logger.info("Participant <" + participantId + "> doesn't exist");
			logger.debug(LOGPRE + "isParticipantExist() end" + LOGPRE);
			return false;
		} else {
			logger.info("Participant <" + participantId + "> exists");
			logger.debug(LOGPRE + "isParticipantExist() end" + LOGPRE);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#putDataIntoDomain(java.lang.Object)
	 */
	public void putDataIntoDomain(Object object) {
		logger.debug(LOGPRE + "putDataIntoDomain() start" + LOGPRE);

		if (object instanceof Participant) {
			Participant participant = (Participant) object;
			List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
			items.add(new ReplaceableItem(participant.getId()).withAttributes(new ReplaceableAttribute(
					PARTICIPANT_ATTRIBUTE_NAME, participant.getName(), true), new ReplaceableAttribute(
					PARTICIPANT_ATTRIBUTE_EMAIL, participant.getEmail(), true), new ReplaceableAttribute(
					PARTICIPANT_ATTRIBUTE_COLLABORATION_ID, participant.getCollaborationId(), false),
					new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_IS_REG, participant.getIsReg(), true),
					new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_ROLE, participant.getRole(), true),
					new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_GROUP, participant.getGroup(), true)));

			logger.info("Putting participant <" + participant.getName() + "> into domain...");
			sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_PARTICIPANT, items));
		}

		if (object instanceof Creator) {
			Creator creator = (Creator) object;
			if (!isCreatorExist(creator.getName())) {
				List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
				items.add(new ReplaceableItem(creator.getId()).withAttributes(new ReplaceableAttribute(
						CREATOR_ATTRIBUTE_NAME, creator.getName(), true), new ReplaceableAttribute(
						CREATOR_ATTRIBUTE_PASSWORD, creator.getPassword(), true), new ReplaceableAttribute(
						CREATOR_ATTRIBUTE_EMAIL, creator.getEmail(), true)));

				logger.info("Putting creator <" + creator.getName() + "> into domain...");
				sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_CREATOR, items));
			}
		}

		if (object instanceof Collaboration) {
			Collaboration collaboration = (Collaboration) object;

			if (!isCollaborationExist(collaboration.getName())) {
				List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();
				ReplaceableItem item = new ReplaceableItem(collaboration.getId());
				item.withAttributes(
						new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_NAME, collaboration.getName(), true),
						new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CREATOR_ID, collaboration.getCreatorId(), true),
						new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE,
								collaboration.getCurrentState(), true), new ReplaceableAttribute(
								COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL, collaboration.getWorkflowModel(), true));

				List<Participant> participants = collaboration.getParticipants();
				for (Participant participant : participants)
					item.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, participant
							.getName(), true));

				items.add(item);

				logger.info("Putting collaboration <" + collaboration.getName() + "> into domain...");
				sdb.batchPutAttributes(new BatchPutAttributesRequest(DOMAIN_COLLABORATION, items));
			}
		}

		logger.debug(LOGPRE + "putDataIntoDomain() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#deleteCollaboration(ca.unb.cs.pcsf.db.Collaboration)
	 */
	public void deleteCollaboration(Collaboration collaboration) {
		logger.debug(LOGPRE + "deleteCollaboration() start" + LOGPRE);

		if (isCollaborationExist(collaboration.getName())) {
			logger.info("Deleting participants...");
			List<Participant> participants = collaboration.getParticipants();
			for (Participant p : participants) {
				sdb.deleteAttributes(new DeleteAttributesRequest(DOMAIN_PARTICIPANT, p.getId()));
			}
			sdb.deleteAttributes(new DeleteAttributesRequest(DOMAIN_COLLABORATION, collaboration.getId()));

			logger.info("Delete Done!");
			logger.debug(LOGPRE + "deleteCollaboration() end" + LOGPRE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#update(java.lang.Object)
	 */
	public void update(Object o) {
		logger.debug(LOGPRE + "update() start" + LOGPRE);

		if (o instanceof Collaboration) {
			Collaboration collaboration = (Collaboration) o;
			logger.info("Updating Collaboration <" + collaboration.getName() + ">...");

			String domainName = DOMAIN_COLLABORATION;
			String itemName = collaboration.getId();
			List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();

			replaceableAttributes.add(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_NAME, collaboration.getName(),
					true));
			for (Participant s : collaboration.getParticipants())
				replaceableAttributes.add(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, s.getName(),
						true));

			sdb.putAttributes(new PutAttributesRequest(domainName, itemName, replaceableAttributes));
		}

		if (o instanceof Participant) {
			Participant participant = (Participant) o;
			logger.info("Updating participant <" + participant.getName() + ">...");

			String domainName = DOMAIN_PARTICIPANT;
			String itemName = participant.getId();
			List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();

			replaceableAttributes.add(new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_IS_REG, participant.getIsReg(),
					true));

			sdb.putAttributes(new PutAttributesRequest(domainName, itemName, replaceableAttributes));
		}

		logger.debug(LOGPRE + "update() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#updateCollaborationState(ca.unb.cs.pcsf.db.Collaboration,
	 * java.lang.String)
	 */
	public void updateCollaborationState(Collaboration collaboration, String state) {
		logger.debug(LOGPRE + "updateCollaborationState() start" + LOGPRE);

		logger.info("Updating collaboration <" + collaboration.getName() + "> state to be <" + state + ">");
		String domainName = DOMAIN_COLLABORATION;
		String itemName = collaboration.getId();
		List<ReplaceableAttribute> replaceableAttributes = new ArrayList<ReplaceableAttribute>();

		replaceableAttributes.add(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CURRENT_STATE, state, true));

		sdb.putAttributes(new PutAttributesRequest(domainName, itemName, replaceableAttributes));

		logger.debug(LOGPRE + "updateCollaborationState() end" + LOGPRE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getAllCreatorIds()
	 */
	public List<String> getAllCreatorIds() {
		logger.debug(LOGPRE + "getAllCreatorIds() start" + LOGPRE);
		logger.debug("Getting all creator ids");

		List<String> participantIds = new ArrayList<String>();

		String selectExpression = "select * from `" + DOMAIN_CREATOR + "`";
		List<Item> items = this.getDataFromDomain(selectExpression);
		if (!items.isEmpty()) {
			for (Item item : items) {
				participantIds.add(item.getName());
			}
		}

		logger.debug(LOGPRE + "getAllCreatorIds() end" + LOGPRE);
		return participantIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getAllCollaborationIds()
	 */
	public List<String> getAllCollaborationIds() {
		logger.debug(LOGPRE + "getAllCollaborationIds() start" + LOGPRE);
		logger.debug("Getting all collaboration ids");

		List<String> collaborationIds = new ArrayList<String>();

		String selectExpression = "select * from `" + DOMAIN_COLLABORATION + "`";
		List<Item> items = this.getDataFromDomain(selectExpression);
		if (!items.isEmpty()) {
			for (Item item : items) {
				collaborationIds.add(item.getName());
			}
		}

		logger.debug(LOGPRE + "getAllCollaborationIds() end" + LOGPRE);
		return collaborationIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getAllParticipantIds()
	 */
	public List<String> getAllParticipantIds() {
		logger.debug(LOGPRE + "getAllParticipantIds() start" + LOGPRE);
		logger.debug("Getting all participant ids");

		List<String> participantIds = new ArrayList<String>();

		String selectExpression = "select * from `" + DOMAIN_PARTICIPANT + "`";
		List<Item> items = this.getDataFromDomain(selectExpression);
		if (!items.isEmpty()) {
			for (Item item : items) {
				participantIds.add(item.getName());
			}
		}

		logger.debug(LOGPRE + "getAllParticipantIds() end" + LOGPRE);
		return participantIds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getCreatorByName(java.lang.String)
	 */
	public Creator getCreatorByName(String userName) {
		logger.debug(LOGPRE + "getCreatorByName() start" + LOGPRE);

		Creator creator = new Creator();
		String selectRequest = "select * from `" + DOMAIN_CREATOR + "` where " + CREATOR_ATTRIBUTE_NAME + " = '"
				+ userName + "'";
		Item item = this.getDataFromDomain(selectRequest).get(0);

		creator.setId(item.getName());
		for (Attribute attribute : item.getAttributes()) {
			if (attribute.getName().equals(CREATOR_ATTRIBUTE_NAME))
				creator.setName(attribute.getValue());
			if (attribute.getName().equals(CREATOR_ATTRIBUTE_PASSWORD))
				creator.setPassword(attribute.getValue());
			if (attribute.getName().equals(CREATOR_ATTRIBUTE_EMAIL))
				creator.setEmail(attribute.getValue());
		}

		logger.debug(LOGPRE + "getCreatorByName() end" + LOGPRE);
		return creator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getCreatorById(java.lang.String)
	 */
	public Creator getCreatorById(String id) {
		logger.debug(LOGPRE + "getCreatorById() start" + LOGPRE);

		Creator creator = new Creator();
		String selectRequest = "select * from `" + DOMAIN_CREATOR + "`";
		List<Item> items = this.getDataFromDomain(selectRequest);
		Item findItem = new Item();

		if (!items.isEmpty()) {
			for (Item item : items) {
				if (item.getName().equals(id)) {
					findItem = item;
					break;
				}
			}
		}

		if (findItem != null) {
			creator.setId(findItem.getName());
			for (Attribute attribute : findItem.getAttributes()) {
				if (attribute.getName().equals(CREATOR_ATTRIBUTE_NAME))
					creator.setName(attribute.getValue());
				if (attribute.getName().equals(CREATOR_ATTRIBUTE_PASSWORD))
					creator.setPassword(attribute.getValue());
				if (attribute.getName().equals(CREATOR_ATTRIBUTE_EMAIL))
					creator.setEmail(attribute.getValue());
			}
		}

		logger.debug(LOGPRE + "getCreatorById() end" + LOGPRE);
		return creator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getCollaborationById(java.lang.String)
	 */
	public Collaboration getCollaborationById(String collaborationId) {
		logger.debug(LOGPRE + "getCollaborationById() start" + LOGPRE);

		Collaboration collaboration = new Collaboration();
		String selectRequest = "select * from `" + DOMAIN_COLLABORATION + "`";
		List<Item> items = this.getDataFromDomain(selectRequest);
		Item findItem = new Item();

		if (!items.isEmpty()) {
			for (Item item : items) {
				if (item.getName().equals(collaborationId)) {
					findItem = item;
					break;
				}
			}
		}

		if (findItem != null) {
			List<String> participantNames = new ArrayList<String>();
			List<Participant> participants = new ArrayList<Participant>();

			collaboration.setId(findItem.getName());
			for (Attribute attribute : findItem.getAttributes()) {
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME))
					collaboration.setName(attribute.getValue());
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CREATOR_ID))
					collaboration.setCreatorId(attribute.getValue());
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CURRENT_STATE))
					collaboration.setCurrentState(attribute.getValue());
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
					participantNames.add(attribute.getValue());
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL))
					collaboration.setWorkflowModel(attribute.getValue());
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PROCESS_DEFINITION_ID))
					collaboration.setProcessDefId(attribute.getValue());
				if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PACKAGE))
					collaboration.setPackageLocation(attribute.getValue());
			}

			for (String s : participantNames) {
				Participant p = this.getParticipantByNameAndCollaborationId(s, findItem.getName());
				participants.add(p);
			}

			collaboration.setParticipants(participants);
		}

		logger.debug(LOGPRE + "getCollaborationById() end" + LOGPRE);
		return collaboration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.PcsfSimpleDBAccess#getCollaborationByName(java.lang.String)
	 */
	@Override
	public Collaboration getCollaborationByName(String collaborationName) {
		logger.debug(LOGPRE + "getCollaborationByName() start" + LOGPRE);

		Collaboration collaboration = new Collaboration();
		String selectExpression = "select * from `" + DOMAIN_COLLABORATION + "` where " + COLLABORATION_ATTRIBUTE_NAME
				+ "='" + collaborationName + "'";
		Item findItem = this.getDataFromDomain(selectExpression).get(0);

		List<String> participantNames = new ArrayList<String>();
		List<Participant> participants = new ArrayList<Participant>();

		collaboration.setId(findItem.getName());
		for (Attribute attribute : findItem.getAttributes()) {
			if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME))
				collaboration.setName(attribute.getValue());
			if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CREATOR_ID))
				collaboration.setCreatorId(attribute.getValue());
			if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CURRENT_STATE))
				collaboration.setCurrentState(attribute.getValue());
			if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
				participantNames.add(attribute.getValue());
			if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL))
				collaboration.setWorkflowModel(attribute.getValue());
			if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PACKAGE))
				collaboration.setPackageLocation(attribute.getValue());
		}

		for (String s : participantNames) {
			Participant p = this.getParticipantByNameAndCollaborationId(s, findItem.getName());
			participants.add(p);
		}

		collaboration.setParticipants(participants);

		logger.debug(LOGPRE + "getCollaborationByName() end" + LOGPRE);
		return collaboration;
	}

	/**
	 * Get a participant record from the data base by name and collaboration id.
	 * 
	 * @param participantName
	 * @param collaborationId
	 * @return a participant
	 */
	private Participant getParticipantByNameAndCollaborationId(String participantName, String collaborationId) {
		logger.debug(LOGPRE + "getParticipantByNameAndCollaborationId() start" + LOGPRE);

		Participant participant = new Participant();
		String selectRequest = "select * from `" + DOMAIN_PARTICIPANT + "` where " + PARTICIPANT_ATTRIBUTE_NAME
				+ " = '" + participantName + "' AND " + PARTICIPANT_ATTRIBUTE_COLLABORATION_ID + "='" + collaborationId
				+ "'";
		List<Item> items = this.getDataFromDomain(selectRequest);

		if (items != null) {
			Item item = items.get(0);
			participant.setId(item.getName());
			for (Attribute attribute : item.getAttributes()) {
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_NAME))
					participant.setName(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_EMAIL))
					participant.setEmail(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_COLLABORATION_ID))
					participant.setCollaborationId(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_IS_REG))
					participant.setIsReg(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_ROLE))
					participant.setRole(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_GROUP))
					participant.setGroup(attribute.getValue());
			}
		}

		logger.debug(LOGPRE + "getParticipantByNameAndCollaborationId() end" + LOGPRE);
		return participant;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getCollaborationsByCreatorId(java.lang.String)
	 */
	public List<Collaboration> getCollaborationsByCreatorId(String creatorId) {
		logger.debug(LOGPRE + "getCollaborationsByCreatorId() start" + LOGPRE);

		List<Collaboration> collaborations = new ArrayList<Collaboration>();
		String selectExpression = "select * from `" + DOMAIN_COLLABORATION + "` where "
				+ COLLABORATION_ATTRIBUTE_CREATOR_ID + "='" + creatorId + "'";
		List<Item> items = this.getDataFromDomain(selectExpression);

		if (!items.isEmpty()) {
			for (Item item : items) {
				Collaboration collaboration = new Collaboration();
				List<String> users = new ArrayList<String>();
				List<Participant> participants = new ArrayList<Participant>();
				collaboration.setId(item.getName());
				for (Attribute attribute : item.getAttributes()) {
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_NAME))
						collaboration.setName(attribute.getValue());
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CREATOR_ID))
						collaboration.setCreatorId(attribute.getValue());
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_CURRENT_STATE))
						collaboration.setCurrentState(attribute.getValue());
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PARTICIPANT))
						users.add(attribute.getValue());
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL))
						collaboration.setWorkflowModel(attribute.getValue());
					if (attribute.getName().equals(COLLABORATION_ATTRIBUTE_PACKAGE))
						collaboration.setPackageLocation(attribute.getValue());
				}

				for (String user : users) {
					Participant p = this.getParticipantByNameAndCollaborationId(user, collaboration.getId());
					participants.add(p);
				}

				collaboration.setParticipants(participants);
				collaborations.add(collaboration);
			}
		}

		logger.debug(LOGPRE + "getCollaborationsByCreatorId() end" + LOGPRE);
		return collaborations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.db.DBAccessService#getParticipantById(java.lang.String)
	 */
	public Participant getParticipantById(String participantId) {
		logger.debug(LOGPRE + "getParticipantById() start" + LOGPRE);

		Participant participant = new Participant();
		String selectRequest = "select * from `" + DOMAIN_PARTICIPANT + "`";
		List<Item> items = this.getDataFromDomain(selectRequest);
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
			participant.setId(findItem.getName());
			for (Attribute attribute : findItem.getAttributes()) {
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_NAME))
					participant.setName(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_EMAIL))
					participant.setEmail(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_COLLABORATION_ID))
					participant.setCollaborationId(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_IS_REG))
					participant.setIsReg(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_ROLE))
					participant.setRole(attribute.getValue());
				if (attribute.getName().equals(PARTICIPANT_ATTRIBUTE_GROUP))
					participant.setGroup(attribute.getValue());
			}
		}

		logger.debug(LOGPRE + "getParticipantById() end" + LOGPRE);
		return participant;
	}
}
