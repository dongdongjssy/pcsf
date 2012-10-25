/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.client.servlet.creator;

import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_CREATOR_BEAN;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.ATTRIBUTE_ERR_MSG;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.COMMON_PAGE_ERROR;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.LOGPRE;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.MESSAGE_RESULT_FAIL;
import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.MESSAGE_RESULT_OK;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import ca.unb.cs.pcsf.web.client.PCSFMessage;
import ca.unb.cs.pcsf.web.client.db.Creator;
import ca.unb.cs.pcsf.web.client.db.PcsfSimpleDBAccessImpl;

/**
 * Servlet implementation class CreatorLoginServlet
 * 
 * @author dongdong
 * @version 1.0
 * @createDate Jul 16, 2012
 * @email dong.dong@unb.ca
 * 
 */
@WebServlet(description = "Creator Login Servlet", urlPatterns = { "/CreatorLogin" })
public class CreatorLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(CreatorLoginServlet.class.getName());

	PcsfSimpleDBAccessImpl dbAccess = new PcsfSimpleDBAccessImpl();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreatorLoginServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		// get user name and password from the user input
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		PCSFMessage message = loginAsCreator(username, password);

		if (message.getResult().equals(MESSAGE_RESULT_OK)) {
			logger.info("Redirect to success page");
			session.setAttribute(ATTRIBUTE_CREATOR_BEAN, message.getObject());
			response.sendRedirect(request.getContextPath() + "/CreatorDisplay");
		} else {
			logger.info("Redirect to error page");
			session.setAttribute(ATTRIBUTE_ERR_MSG, message.getMsg());
			response.sendRedirect(COMMON_PAGE_ERROR);
		}
	}

	private PCSFMessage loginAsCreator(String name, String pwd) {
		logger.debug(LOGPRE + "loginAsCreator() start" + LOGPRE);

		if (dbAccess.isCreatorExist(name)) {
			Creator creator = dbAccess.getCreatorByName(name);
			if (creator.getName().equals(name) && creator.getPassword().equals(pwd)) {
				logger.info("Creator " + name + " login successfully!");
				logger.debug(LOGPRE + "loginAsCreator() end" + LOGPRE);
				return new PCSFMessage(MESSAGE_RESULT_OK, "Creator " + name + " login successfully!", creator);
			} else {
				logger.info("Creator " + name + " login failed, check your password!");
				logger.debug(LOGPRE + "loginAsCreator() end" + LOGPRE);
				return new PCSFMessage(MESSAGE_RESULT_FAIL, "Creator " + name + " login failed, check your password!",
						null);
			}
		} else {
			logger.info("Creator " + name + " doesn't exist, register first!");
			logger.debug(LOGPRE + "loginAsCreator() end" + LOGPRE);
			return new PCSFMessage(MESSAGE_RESULT_FAIL, "Creator " + name + " doesn't exist, register first!", null);
		}
	}

}
