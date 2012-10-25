/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.reg.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.Attribute;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.Item;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;
import com.amazonaws.services.simpledb.model.SelectRequest;

/**
 * @author dongdong
 * @version 1.0
 * @createDate 2012-10-01
 * @email dong.dong@unb.ca
 * 
 */
public class RegistrationServiceTest {
	static AmazonSimpleDB sdb;

	// simple db
	// domain collaboration attributes
	private static final String COLLABORATION_ATTRIBUTE_NAME = "Name";
	private static final String COLLABORATION_ATTRIBUTE_CREATOR_ID = "CreatorId";
	private static final String COLLABORATION_ATTRIBUTE_CURRENT_STATE = "CurrentState";
	private static final String COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL = "WorkflowModel";
	private static final String COLLABORATION_ATTRIBUTE_PARTICIPANT = "Participant";

	// domain participant attributes
	private static final String PARTICIPANT_ATTRIBUTE_NAME = "Name";
	private static final String PARTICIPANT_ATTRIBUTE_EMAIL = "Email";
	private static final String PARTICIPANT_ATTRIBUTE_COLLABORATION_ID = "CollaborationId";
	private static final String PARTICIPANT_ATTRIBUTE_IS_REG = "IsReg";

	// collaboration state
	private static final String COLLABORATION_STATE_NEW_CREATED = "new created";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
				RegistrationServiceTest.class.getResourceAsStream("AwsCredentials.properties")));
		List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();

		// add participants:
		ReplaceableItem p1 = new ReplaceableItem("12345");
		p1.withAttributes(new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_NAME, "dongdong", true),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_EMAIL, "dong.dong@me.com", true),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_COLLABORATION_ID, "111111", false),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_IS_REG, "no", true));
		items.add(p1);

		ReplaceableItem p2 = new ReplaceableItem("23456");
		p2.withAttributes(new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_NAME, "teamleader", true),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_EMAIL, "dong.dong@me.com", true),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_COLLABORATION_ID, "111111", false),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_IS_REG, "no", true));
		items.add(p2);

		ReplaceableItem p3 = new ReplaceableItem("34567");
		p3.withAttributes(new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_NAME, "manager", true),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_EMAIL, "dong.dong@me.com", true),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_COLLABORATION_ID, "111111", false),
				new ReplaceableAttribute(PARTICIPANT_ATTRIBUTE_IS_REG, "no", true));
		items.add(p3);

		sdb.batchPutAttributes(new BatchPutAttributesRequest("Participant", items));
		items.clear();

		// add collaboration:
		ReplaceableItem c1 = new ReplaceableItem("111111");
		c1.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_NAME, "collaboration1", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CREATOR_ID, "489823", true), new ReplaceableAttribute(
						COLLABORATION_ATTRIBUTE_CURRENT_STATE, COLLABORATION_STATE_NEW_CREATED, true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL, "test", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, "dongdong", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, "teamleader", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, "manager", true));
		items.add(c1);

		sdb.batchPutAttributes(new BatchPutAttributesRequest("Collaboration", items));
		items.clear();
	}

	@Test
	public void testSetAsReg() {
		String wsUrl = "http://localhost:8080/registrationService/RegistrationService?wsdl";
		String method = "setAsReg";
		String arg = "12345";

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsUrl);
		try {
			client.invoke(method, arg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String isReg = "";
		String getParticipantRequest = "select * from `Participant`";
		List<Item> items = sdb.select(new SelectRequest(getParticipantRequest)).getItems();
		Item findItem = new Item();

		if (!items.isEmpty()) {
			for (Item item : items) {
				if (item.getName().equals("12345")) {
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
			}
		}

		Assert.assertEquals("yes", isReg);
	}

}
