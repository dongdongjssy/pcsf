/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.mnc;

import javax.jws.WebService;

import org.apache.log4j.Logger;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.mnc.MonitorNControlService")
public class MonitorNControlServiceImpl implements MonitorNControlService {
	private static final String LOGPRE = ">>>>";
	private static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";

	Logger logger = Logger.getLogger(MonitorNControlServiceImpl.class.getName());

	/**
	 * Constructor
	 */
	public MonitorNControlServiceImpl() {
		logger.info("===========================================");
		logger.info("Monitor and Control Service has started!");
		logger.info("===========================================");
	}

}
