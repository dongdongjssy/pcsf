/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.dtr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService(endpointInterface = "ca.unb.cs.pcsf.services.dtr.DataTransferService")
public class DataTransferServiceImpl implements DataTransferService {
	private static final String LOGPRE = ">>>>";
	private static final String CREDENTIAL_FILE_PATH = "AwsCredentials.properties";

	private Logger logger = Logger.getLogger(DataTransferService.class.getName());

	private AmazonS3 s3;

	/**
	 * Constructor
	 */
	public DataTransferServiceImpl() {
		try {
			s3 = new AmazonS3Client(new PropertiesCredentials(
					DataTransferServiceImpl.class.getResourceAsStream(CREDENTIAL_FILE_PATH)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("===========================================");
		logger.info("Data Transfer Service has started!");
		logger.info("===========================================");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.dtr.DataTransferService#uploadFile(java.lang.String, int, byte[])
	 */
	@Override
	public String[] uploadFile(String fileName, String collaborationName, byte[] bytes) {
		logger.debug(LOGPRE + "uploadFile() start" + LOGPRE);

		String[] result = new String[2];

		// create a temporary file with text data
		logger.info("loading the file...");
		FileOutputStream outputStream = null;
		File newFile = null;

		try {
			newFile = File.createTempFile(fileName, null);
			newFile.deleteOnExit();

			outputStream = new FileOutputStream(newFile);
			outputStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

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
		String key = fileName;
		s3.putObject(new PutObjectRequest(bucketName, key, newFile));

		logger.info("file <" + fileName + "> has been uploaded to bucket <" + bucketName + "> with key <" + key + ">");

		// return the key and bucket name back to user
		result[0] = key;
		result[1] = bucketName;
		logger.debug(LOGPRE + "uploadFile() end" + LOGPRE);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.unb.cs.pcsf.services.dtr.DataTransferService#downloadFile(java.lang.String)
	 */
	@Override
	public byte[] downloadFile(String bucketName, String key) {
		logger.debug(LOGPRE + "downloadFile() start" + LOGPRE);

		// create input stream from the object.
		S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
		InputStream inputStream = object.getObjectContent();

		// convert input stream into byte array.
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] data = new byte[100];
		int count = -1;

		try {
			while ((count = inputStream.read(data, 0, 100)) != -1)
				outputStream.write(data, 0, count);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			data = null;
		}

		byte[] result = outputStream.toByteArray();
		try {
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("file <" + key + "> has been downloaded!");
		logger.debug(LOGPRE + "downloadFile() end" + LOGPRE);
		return result;
	}
}
