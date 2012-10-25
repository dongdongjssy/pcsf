package ca.unb.cs.pcsf.web.servlet.participant;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.istack.logging.Logger;

import ca.unb.cs.pcsf.web.PcsfUtils;

/**
 * Servlet implementation class SubmitTaskServlet
 */
@WebServlet("/SubmitTask")
public class SubmitTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(SubmitTaskServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitTaskServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String taskId = request.getParameter("taskId");
		String collaborationName = request.getParameter("collaborationName");
		String collaborationId = request.getParameter("collaborationId");

		PcsfUtils pcsfUtils = new PcsfUtils();
		String url = pcsfUtils.getSncWSUrl(collaborationName);
		String method = "submitTask";

		logger.info("submitting task <" + taskId + ">...");
		pcsfUtils.callService(url, method, taskId, collaborationId);

		response.sendRedirect(request.getContextPath() + "/ParticipantDisplay");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doGet(request, response);
	}

}
