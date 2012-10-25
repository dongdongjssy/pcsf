/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.snc.test;

import ca.unb.cs.pcsf.services.snc.ScheduleNCoordinateServiceImpl;

/**
 * @author ddong
 * @version 0.1
 * @createDate Aug 20, 2012
 * @email dong.dong@unb.ca
 */
public class MncTest {
	public static void main(String[] args) {
		test2();
	}

	private static void test2() {
		// File file = new File(System.getProperty("user.home") + File.separator + "leave.jpdl.xml");
		ScheduleNCoordinateServiceImpl scheduleNCoordinateServiceImpl = new ScheduleNCoordinateServiceImpl();
		scheduleNCoordinateServiceImpl.deployProcess("222222");

		scheduleNCoordinateServiceImpl.runCollaboration("222222", "1");
	}

}
