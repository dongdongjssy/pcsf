/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.db;

import java.util.List;

/**
 * @author ddong
 * @version 0.1
 * @createDate Jul 13, 2012
 * @email dong.dong@unb.ca
 */
public interface PcsfSimpleDBAccess {
	/**
	 * Check if given creator exist or not
	 * 
	 * @param name
	 * @return is exist or not
	 */
	public boolean isCreatorExist(String name);

	/**
	 * Check if given collaboration exist or not
	 * 
	 * @param collaborationName
	 * @return is exist or not
	 */
	public boolean isCollaborationExist(String collaborationName);

	/**
	 * Check if given participant exist or not
	 * 
	 * @param participantId
	 * @return is exist or not
	 */
	public boolean isParticipantExist(String participantId);

	/**
	 * Put data into domain
	 * 
	 * @param object
	 */
	public void putDataIntoDomain(Object object);

	/**
	 * delete an item and all its attributes
	 * 
	 * @param domainName
	 * @param collaborationId
	 */
	public void deleteCollaboration(Collaboration collaboration);

	/**
	 * update database
	 * 
	 * @param o
	 */
	public void update(Object o);

	/**
	 * Update the state of a collaboration.
	 * 
	 * @param collaboration
	 * @param state
	 */
	public void updateCollaborationState(Collaboration collaboration, String state);

	/**
	 * Get all creators' IDs.
	 * 
	 * @return ID list
	 */
	public List<String> getAllCreatorIds();

	/**
	 * Get all collaboration IDs.
	 * 
	 * @return ID list
	 */
	public List<String> getAllCollaborationIds();

	/**
	 * Get all participants IDs.
	 * 
	 * @return ID list
	 */
	public List<String> getAllParticipantIds();

	/**
	 * Get a creator record from the data base by name.
	 * 
	 * @param userName
	 * @return the creator
	 */
	public Creator getCreatorByName(String userName);

	/**
	 * Get a creator record from the data base by id.
	 * 
	 * @param creatorId
	 * @return the creator
	 */
	public Creator getCreatorById(String id);

	/**
	 * Get a collaboration from the data base by id.
	 * 
	 * @param collaborationId
	 * @return the find collaboration
	 */
	public Collaboration getCollaborationById(String collaborationId);

	/**
	 * Get a collaboration from the data base by name.
	 * 
	 * @param collaborationName
	 * @return the find collaboration
	 */
	public Collaboration getCollaborationByName(String collaborationName);

	/**
	 * Get all the collaborations created by a certain creator.
	 * 
	 * @param creatorId
	 * @return a set of collaborations
	 */
	public List<Collaboration> getCollaborationsByCreatorId(String creatorId);

	/**
	 * Get a participant record from the data base by id.
	 * 
	 * @param participantId
	 * @return a participant
	 */
	public Participant getParticipantById(String participantId);
}
