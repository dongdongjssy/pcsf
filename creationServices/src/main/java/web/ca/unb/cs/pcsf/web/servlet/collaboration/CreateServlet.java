/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.collaboration;

import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ADDED_PARTICIPANTS;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_CREATOR_BEAN;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ERR_MSG;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.COMMON_PAGE_ERROR;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ERR_MSG_COLLABORATION_ALREADY_EXIST;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.SAVE_PATH;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.services.crt.CreationServiceImpl;
import ca.unb.cs.pcsf.web.db.Creator;
import ca.unb.cs.pcsf.web.db.Participant;
import ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class CreateServlet
 * 
 * @author ddong
 * @version 0.1
 * @createDate Jul 20, 2012
 * @email dong.dong@unb.ca
 */
@WebServlet(description = "Create Collaboration Servlet", urlPatterns = { "/Create" })
public class CreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(CreateServlet.class.getName());
	private PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
	private CreationServiceImpl creationService = new CreationServiceImpl();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);

		String collaborationName = "";
		FileItem uploadFileItem = null;
		File uploadFile = null;
		String uploadFileName = "";
		String filePath = "";

		// get participants
		Creator creator = (Creator) session.getAttribute(ATTRIBUTE_CREATOR_BEAN);

		// get collaboration name and upload file from the form
		logger.info("creating collaboration...");
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
					if (fileItem.isFormField()) {
						String field = fileItem.getFieldName();
						if (field.equals("collaborationName")) {
							collaborationName = fileItem.getString();
							logger.info("collaboration name: " + collaborationName);
						}
					}
					if (!fileItem.isFormField()) {
						uploadFileItem = fileItem;
						uploadFileName = uploadFileItem.getName();
						logger.info("upload file name: " + uploadFileName);
					}
				}
				if (dbAccess.isCollaborationExist(collaborationName)) {
					session.setAttribute(ATTRIBUTE_ERR_MSG, ERR_MSG_COLLABORATION_ALREADY_EXIST);
					logger.info(ERR_MSG_COLLABORATION_ALREADY_EXIST);
					logger.info("redirect to error page");
					response.sendRedirect(COMMON_PAGE_ERROR);
				}

				File root = new File(SAVE_PATH);
				if (!root.exists() || !root.isDirectory())
					root.mkdir();

				String folderName = SAVE_PATH + File.separator + collaborationName;
				File folder = new File(folderName);
				if (!folder.exists() && !folder.isDirectory()) {
					folder.mkdir();
				}

				if (!uploadFileName.endsWith(".xml")) {
					uploadFileName += ".xml";
				}
				filePath = folderName + File.separator + uploadFileName;
				uploadFile = new File(filePath);

				// check work flow file
				logger.info("start uploading...");
				logger.info("upload workflow file <" + uploadFileName + "> to: " + folderName);
				try {
					uploadFileItem.write(uploadFile);
					uploadFileItem.delete();
					logger.info("finish uploading!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (FileUploadException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		creationService.createCollaboration(collaborationName, creator.getId(), uploadFile);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		response.sendRedirect(request.getContextPath() + "/CreatorDisplay");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
