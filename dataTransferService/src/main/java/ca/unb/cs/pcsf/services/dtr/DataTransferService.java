/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.services.dtr;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author ddong
 * @version 0.1
 * @createDate Sep 21, 2012
 * @email dong.dong@unb.ca
 */
@WebService
public interface DataTransferService {

	/**
	 * upload a file to Amazon S3
	 * 
	 * @param fileName
	 * @param collaborationName
	 * @param bytes
	 * @return the key of the upload file and the bucket name
	 */
	@WebMethod
	public String[] uploadFile(String fileName, String collaborationName, byte[] bytes);

	/**
	 * download a file from Amazon S3
	 * 
	 * @param bucketName
	 * @param key
	 * @return a file
	 */
	@WebMethod
	public byte[] downloadFile(String bucketName, String key);
}
