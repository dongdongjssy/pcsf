/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.mnc;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.jws.WebService;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.simpledb.AmazonSimpleDB;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;

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

	private Timer timer = new Timer();
	private AmazonSimpleDB sdb;
	private AmazonSimpleEmailService ses;
	private PropertiesCredentials credentials;

	/**
	 * Constructor
	 */
	public MonitorNControlServiceImpl() {
		// get credentials
		try {
			credentials = new PropertiesCredentials(MonitorNControlServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH));
			sdb = new AmazonSimpleDBClient(credentials);
			ses = new AmazonSimpleEmailServiceClient(credentials);
		} catch (IOException e) {
			e.printStackTrace();
		}

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
		TimerTask timeoutTask = new TimeOutHandler();

		timer.schedule(timeoutTask, new Date(), 100000000);
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
	 * Inner class TimeOutControl used to handle time out exceptions
	 */
	class TimeOutHandler extends TimerTask {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {

		}

	}
}
