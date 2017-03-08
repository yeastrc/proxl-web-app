package org.yeastrc.auth.dto;

import java.util.Date;



/**
 * auth_forgot_password_tracking table
 *
 */
public class AuthForgotPasswordTrackingDTO {

	private int id;
	private int userId;
	private Date createDate;
	private Date usedDate;
	private String forgotPasswordTrackingCode;
	private String submitIP;
	private String useIP;
	private boolean codeReplacedByNewer;
	
	
	public boolean isCodeReplacedByNewer() {
		return codeReplacedByNewer;
	}
	public void setCodeReplacedByNewer(boolean codeReplacedByNewer) {
		this.codeReplacedByNewer = codeReplacedByNewer;
	}
	public String getSubmitIP() {
		return submitIP;
	}
	public void setSubmitIP(String submitIP) {
		this.submitIP = submitIP;
	}
	public String getUseIP() {
		return useIP;
	}
	public void setUseIP(String useIP) {
		this.useIP = useIP;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUsedDate() {
		return usedDate;
	}
	public void setUsedDate(Date usedDate) {
		this.usedDate = usedDate;
	}
	public String getForgotPasswordTrackingCode() {
		return forgotPasswordTrackingCode;
	}
	public void setForgotPasswordTrackingCode(String forgotPasswordTrackingCode) {
		this.forgotPasswordTrackingCode = forgotPasswordTrackingCode;
	}
	
	
}


//CREATE TABLE IF NOT EXISTS auth_forgot_password_tracking (
//		  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
//		  auth_user_id INT UNSIGNED NOT NULL,
//		  create_date DATETIME NOT NULL,
//		  used_date DATETIME NULL,
//		  forgot_password_tracking_code VARCHAR(255) NOT NULL,
//		  submit_ip VARCHAR(255) NOT NULL,
//		  use_ip VARCHAR(255) NULL,
//		  code_replaced_by_newer TINYINT(1) NULL,
