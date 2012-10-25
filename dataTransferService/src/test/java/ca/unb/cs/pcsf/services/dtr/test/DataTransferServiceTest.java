/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.dtr.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dongdong
 * @version 1.0
 * @createDate 2012-10-01
 * @email dong.dong@unb.ca
 * 
 */
public class DataTransferServiceTest {
	private static final String FILE_NAME = "leave.jpdl.xml";
	private static final String COLLABORATION_NAME = "leave";
	private static final String BUCKET_NAME = "pcsf-s3-bucket-";

	@Test
	@Ignore
	public void testUpload() {
		String wsUrl = "http://localhost:8080/dataTransferService/DataTransferService?wsdl";
		String method = "uploadFile";

		File uploadFile = new File(System.getProperty("user.home") + File.separator + FILE_NAME);
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(uploadFile);
			byte[] bs = new byte[inputStream.available()];

			inputStream.read(bs);
			inputStream.close();

			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
			Client client = dcf.createClient(wsUrl);
			Object[] resultsObjects = client.invoke(method, uploadFile.getName(),
					COLLABORATION_NAME, bs);
			List<?> results = (ArrayList<?>) resultsObjects[0];

			Assert.assertEquals(uploadFile.getName(), results.get(0));
			Assert.assertEquals(BUCKET_NAME + COLLABORATION_NAME, results.get(1));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDownload() {
		String wsUrl = "http://localhost:8080/dataTransferService/DataTransferService?wsdl";
		String method = "downloadFile";

		String bucketName = BUCKET_NAME + COLLABORATION_NAME;
		String key = FILE_NAME;
		String filePath = System.getProperty("user.home") + File.separator + FILE_NAME;
		File downloadFile = new File(filePath);
		FileOutputStream outputStream = null;

		try {
			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
			Client client = dcf.createClient(wsUrl);
			Object[] resultsObjects = client.invoke(method, bucketName, key);
			byte[] bytes = (byte[]) resultsObjects[0];

			outputStream = new FileOutputStream(downloadFile);
			outputStream.write(bytes);

			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
