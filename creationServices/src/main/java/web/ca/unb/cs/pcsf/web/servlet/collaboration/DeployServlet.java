/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.servlet.collaboration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.unb.cs.pcsf.services.crt.CreationServiceImpl;

/**
 * Servlet implementation class DeployServlet
 * 
 * @author ddong
 * @version 0.1
 * @createDate Jul 20, 2012
 * @email dong.dong@unb.ca
 */
@WebServlet(description = "Deploy Collaboration Servlet", urlPatterns = { "/Deploy" })
public class DeployServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeployServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CreationServiceImpl creationService = new CreationServiceImpl();

		String collaborationId = request.getParameter("collaborationId");

		// deploy collaboration services.
		creationService.deployCollaboration(collaborationId);

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
