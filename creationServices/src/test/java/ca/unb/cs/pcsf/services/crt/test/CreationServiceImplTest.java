/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.crt.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ca.unb.cs.pcsf.services.crt.CreationServiceImpl;

/**
 * @author dongdong
 * @version 1.0
 * @createDate 2012-10-02
 * @email dong.dong@unb.ca
 * 
 */
public class CreationServiceImplTest {

	@Test
	public void testCreateCollaboration() {
		CreationServiceImpl creationService = new CreationServiceImpl();
		String collaborationName = "collaboration1";
		List<String> participantNames = new ArrayList<String>();
		participantNames.add("p1,dong.dong@me.com");
		participantNames.add("p2,dong.dong@me.com");
		String creatorId = "123456789";
		File workflowFile = new File(System.getProperty("user.home") + File.separator + "leave.jpdl.xml");
		creationService.createCollaboration(collaborationName, participantNames, creatorId, workflowFile);
	}

}
