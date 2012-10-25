/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web;

import static ca.unb.cs.pcsf.web.PCSFWebConstants.WSURL_PRE;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.DOMAIN_CREATOR;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.LOGPRE;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.MAX;

import java.util.List;
import java.util.Random;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 4, 2012
 * @email dong.dong@unb.ca
 */
public class PcsfUtils {
	private static Logger logger = Logger.getLogger(PcsfUtils.class.getName());

	/**
	 * A general method used to call web service.
	 * 
	 * @param wsUrl
	 * @param method
	 * @param arg
	 */
	public Object[] callService(String wsUrl, String method, Object... arg) {
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
	 * @param collaborationName
	 * @return registration service url
	 */
	public String getRegWSUrl(String collaborationName) {
		return WSURL_PRE + collaborationName + "-registrationService/RegistrationService?wsdl";
	}

	/**
	 * @param collaborationName
	 * @return schedule and coordinate service url
	 */
	public String getSncWSUrl(String collaborationName) {
		return WSURL_PRE + collaborationName + "-scheduleNCoordinateService/ScheduleNCoordinateService?wsdl";
	}

	/**
	 * @param collaborationName
	 * @return data transfer service url
	 */
	public String getDtrWSUrl(String collaborationName) {
		return WSURL_PRE + collaborationName + "-dataTransferService/DataTransferService?wsdl";
	}

	/**
	 * @param collaborationName
	 * @return monitor and control service url
	 */
	public String getMncWSUrl(String collaborationName) {
		return WSURL_PRE + collaborationName + "-monitorNControlService/MonitorNControlService?wsdl";
	}

	/**
	 * wait for a certain time
	 */
	public void waitAndGo() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate a random id for new user or collaboration.
	 * 
	 * @param domainName
	 * @return random id
	 */
	public String idGenerator(String domainName) {
		logger.debug(LOGPRE + "idGenerator() start" + LOGPRE);

		PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
		Random random = new Random(System.currentTimeMillis());
		String id = String.valueOf(((int) (random.nextDouble() * MAX)) + 1);
		boolean isDuplicated = true;

		if (domainName.equals(DOMAIN_CREATOR)) {
			logger.info("generating id for creator ...");

			List<String> creatorIds = dbAccess.getAllCreatorIds();
			while (isDuplicated) {
				isDuplicated = false;
				for (String s : creatorIds) {
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

		logger.debug(LOGPRE + "idGenerator() end" + LOGPRE);
		return id;
	}
}
