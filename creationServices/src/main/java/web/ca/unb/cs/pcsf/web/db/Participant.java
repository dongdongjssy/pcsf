/**
 * Faculty of Computer Science
 * University of New Brunswick
 */
package ca.unb.cs.pcsf.web.db;

import java.io.Serializable;

/**
 * @author dongdong
 * @version 1.0
 * @createDate Jul 6, 2012
 * @email dong.dong@unb.ca
 * 
 */
public class Participant implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private String name;
	private String email;
	private String collaborationId;
	private String role;
	private String group;
	private String isReg;

	/**
	 * @return the collaborationId
	 */
	public String getCollaborationId() {
		return collaborationId;
	}

	/**
	 * @param collaborationId
	 *            the collaborationId to set
	 */
	public void setCollaborationId(String collaborationId) {
		this.collaborationId = collaborationId;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the isReg
	 */
	public String getIsReg() {
		return isReg;
	}

	/**
	 * @param isReg
	 *            the isReg to set
	 */
	public void setIsReg(String isReg) {
		this.isReg = isReg;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}
}
