/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpledb.model.BatchPutAttributesRequest;
import com.amazonaws.services.simpledb.model.ReplaceableAttribute;
import com.amazonaws.services.simpledb.model.ReplaceableItem;

/**
 * @author ddong
 * @version 0.1
 * @createDate Aug 20, 2012
 * @email dong.dong@unb.ca
 */
public class ScheduleNCoordinateServiceTest {
	static AmazonSimpleDB sdb;
	static AmazonS3 s3;

	// simple db
	// domain collaboration attributes
	private static final String COLLABORATION_ATTRIBUTE_NAME = "Name";
	private static final String COLLABORATION_ATTRIBUTE_CREATOR_ID = "CreatorId";
	private static final String COLLABORATION_ATTRIBUTE_CURRENT_STATE = "CurrentState";
	private static final String COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL = "WorkflowModel";
	private static final String COLLABORATION_ATTRIBUTE_PARTICIPANT = "Participant";

	// collaboration state
	private static final String COLLABORATION_STATE_NEW_CREATED = "new created";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		sdb = new AmazonSimpleDBClient(new PropertiesCredentials(
				ScheduleNCoordinateServiceTest.class.getResourceAsStream("AwsCredentials.properties")));
		s3 = new AmazonS3Client(new PropertiesCredentials(
				ScheduleNCoordinateServiceTest.class.getResourceAsStream("AwsCredentials.properties")));
		List<ReplaceableItem> items = new ArrayList<ReplaceableItem>();

		// add file to s3
		String bucketName = "pcsf-s3-bucket-leave";
		s3.createBucket(bucketName);
		File file = new File(System.getProperty("user.home") + File.separator + "leave.jpdl.xml");
		String key = file.getName();
		s3.putObject(new PutObjectRequest(bucketName, key, file));

		// add collaboration:
		ReplaceableItem c1 = new ReplaceableItem("222222");
		c1.withAttributes(new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_NAME, "collaboration1", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_CREATOR_ID, "489823", true), new ReplaceableAttribute(
						COLLABORATION_ATTRIBUTE_CURRENT_STATE, COLLABORATION_STATE_NEW_CREATED, true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_WORKFLOW_MODEL, file.getAbsolutePath(), true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, "dongdong", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, "teamleader", true),
				new ReplaceableAttribute(COLLABORATION_ATTRIBUTE_PARTICIPANT, "manager", true));
		items.add(c1);

		sdb.batchPutAttributes(new BatchPutAttributesRequest("Collaboration", items));
		items.clear();
	}

	@Test
	@Ignore
	public void test() {
		String wsUrl = "http://localhost:8080/scheduleNCoordinateService/ScheduleNCoordinateService?wsdl";
		String method = "deployProcess";

		JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
		Client client = dcf.createClient(wsUrl);
		try {
			client.invoke(method, "222222");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
