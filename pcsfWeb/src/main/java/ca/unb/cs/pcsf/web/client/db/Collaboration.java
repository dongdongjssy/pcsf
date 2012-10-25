/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.client.db;

import static ca.unb.cs.pcsf.web.client.PCSFWebClientConstants.PARTICIPANT_IS_REG_NO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongdong
 * @version 1.0
 * @createDate Jul 6, 2012
 * @email dong.dong@unb.ca
 * 
 */
public class Collaboration implements Serializable {

	private static final long serialVersionUID = 882563921146024635L;

	private String id;
	private String name;
	private List<Participant> participants;
	private String creatorId;
	private String currentState;
	private String workflowModel;
	private String processDefId;

	public Collaboration() {
		participants = new ArrayList<Participant>();
	}

	/**
	 * @return the workflowModel
	 */
	public String getWorkflowModel() {
		return workflowModel;
	}

	/**
	 * @param workflowModel
	 *            the workflowModel to set
	 */
	public void setWorkflowModel(String workflowModel) {
		this.workflowModel = workflowModel;
	}

	/**
	 * @return the participants
	 */
	public List<Participant> getParticipants() {
		return participants;
	}

	/**
	 * @param participants
	 *            the participants to set
	 */
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the creatorId
	 */
	public String getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creatorId
	 *            the creatorId to set
	 */
	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the currentState
	 */
	public String getCurrentState() {
		return currentState;
	}

	/**
	 * @param currentState
	 *            the currentState to set
	 */
	public void setCurrentState(String currentState) {
		this.currentState = currentState;
	}

	/**
	 * Check if a certain participant get involved in this collaboration.
	 * 
	 * @param participantName
	 * @return is involved or not
	 */
	public boolean isParticipantIn(String participantName) {
		for (Participant user : participants) {
			if (user.getName().equals(participantName))
				return true;
		}
		return false;
	}

	/**
	 * Add a new participant to the collaboration
	 * 
	 * @param participant
	 */
	public void addParticipant(Participant participant) {
		getParticipants().add(participant);
	}

	/**
	 * @return have all participants registered
	 */
	public boolean isAllReg() {
		for (Participant p : getParticipants()) {
			if (p.getIsReg().equals(PARTICIPANT_IS_REG_NO))
				return false;
		}

		return true;
	}

	/**
	 * @return the processDefId
	 */
	public String getProcessDefId() {
		return processDefId;
	}

	/**
	 * @param processDefId
	 *            the processDefId to set
	 */
	public void setProcessDefId(String processDefId) {
		this.processDefId = processDefId;
	}
}
