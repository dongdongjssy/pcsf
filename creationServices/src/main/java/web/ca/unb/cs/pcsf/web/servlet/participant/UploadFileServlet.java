package ca.unb.cs.pcsf.web.servlet.participant;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.sun.istack.logging.Logger;

/**
 * Servlet implementation class UploadFileServlet
 */
@WebServlet("/UploadFile")
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(UploadFileServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadFileServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("uploading document...");
		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory dff = new DiskFileItemFactory();
			dff.setSizeThreshold(1024000);

			ServletFileUpload sfu = new ServletFileUpload(dff);
			sfu.setFileSizeMax(5000000);
			sfu.setSizeMax(10000000);

			try {
				List<?> fileItems = sfu.parseRequest(request);
				for (int i = 0; i < fileItems.size(); i++) {
					FileItem fileItem = (FileItem) fileItems.get(i);
					if (!fileItem.isFormField()) {
						FileItem uploadFileItem = fileItem;
						String uploadFileName = uploadFileItem.getName();
						logger.info("upload file name: " + uploadFileName);

						File uploadFile = File.createTempFile(uploadFileName, null);
						uploadFileItem.write(uploadFile);
						uploadFileItem.delete();
						logger.info("finish uploading!");
					}
				}
			} catch (FileUploadException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
