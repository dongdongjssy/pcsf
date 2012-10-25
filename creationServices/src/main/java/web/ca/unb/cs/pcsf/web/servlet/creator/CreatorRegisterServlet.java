/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.creator;

import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_ERR_MSG;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.ATTRIBUTE_INFO_MSG;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.COMMON_PAGE_ERROR;
import static ca.unb.cs.pcsf.web.PCSFWebConstants.COMMON_PAGE_SUCCESS;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.DOMAIN_CREATOR;
import static ca.unb.cs.pcsf.web.PcsfSimpleDBAccessConstants.LOGPRE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.PcsfUtils;
import ca.unb.cs.pcsf.web.db.Creator;
import ca.unb.cs.pcsf.web.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class CreatorRegisterServlet
 * 
 * @author dongdong
 * @version 1.0
 * @createDate Jul 16, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebServlet(description = "Creator Register Servlet", urlPatterns = { "/CreatorRegister" })
public class CreatorRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(CreatorRegisterServlet.class.getName());

	private PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();
	private PcsfUtils pcsfUtils = new PcsfUtils();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreatorRegisterServlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		Logger logger = Logger.getLogger(CreatorRegisterServlet.class.getName());

		// create a new creator
		String name = request.getParameter("username");
		String pwd = request.getParameter("password");
		String email = request.getParameter("email");
		Creator creator = regNewCreator(name, pwd, email);

		if (creator != null) {
			logger.info("Redirect to success page");
			session.setAttribute(ATTRIBUTE_INFO_MSG, "Creator " + name + " has been registered successfully!");
			response.sendRedirect(COMMON_PAGE_SUCCESS);
		} else {
			logger.info("Redirect to error page");
			session.setAttribute(ATTRIBUTE_ERR_MSG, "Creator " + name + " already exist, use another name please!");
			response.sendRedirect(COMMON_PAGE_ERROR);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	private Creator regNewCreator(String name, String pwd, String email) {
		logger.debug(LOGPRE + "regNewCreator() start" + LOGPRE);

		if (dbAccess.isCreatorExist(name)) {
			logger.info("Creator " + name + " already exist, use another name please!");
			logger.debug(LOGPRE + "regNewCreator() end" + LOGPRE);
			return null;
		}

		String id = pcsfUtils.idGenerator(DOMAIN_CREATOR);

		Creator newCreator = new Creator();
		newCreator.setName(name);
		newCreator.setPassword(pwd);
		newCreator.setEmail(email);
		newCreator.setId(id);

		dbAccess.putDataIntoDomain(newCreator);

		logger.info("Creator " + name + " been registered successfully!");
		logger.debug(LOGPRE + "regNewCreator() end" + LOGPRE);
		return newCreator;
	}
}
