package org.yeastrc.xlink.www.qc_data.a_request_json_root;

import java.util.List;

import org.yeastrc.xlink.www.form_query_json_objects.QCPageQueryJSONRoot;

/**
 * Top level root of JSON passed from Page Javascript for Webservices and Downloads
 * 
 * Added to include the project search ids
 *
 */
public class QCPageRequestJSONRoot {

	private List<Integer> projectSearchIds;
	private QCPageQueryJSONRoot qcPageQueryJSONRoot;
	
	private Integer scanFileId;  // Only used in some requests
	List<Integer> scanFileIdList;  // Only used in some requests
	private String scanFileAllString;  // Only used in some requests
	private Double retentionTimeInMinutesCutoff;  // Only used in some requests
	
	public List<Integer> getProjectSearchIds() {
		return projectSearchIds;
	}
	public void setProjectSearchIds(List<Integer> projectSearchIds) {
		this.projectSearchIds = projectSearchIds;
	}
	public QCPageQueryJSONRoot getQcPageQueryJSONRoot() {
		return qcPageQueryJSONRoot;
	}
	public void setQcPageQueryJSONRoot(QCPageQueryJSONRoot qcPageQueryJSONRoot) {
		this.qcPageQueryJSONRoot = qcPageQueryJSONRoot;
	}
	public Integer getScanFileId() {
		return scanFileId;
	}
	public void setScanFileId(Integer scanFileId) {
		this.scanFileId = scanFileId;
	}
	public List<Integer> getScanFileIdList() {
		return scanFileIdList;
	}
	public void setScanFileIdList(List<Integer> scanFileIdList) {
		this.scanFileIdList = scanFileIdList;
	}
	public String getScanFileAllString() {
		return scanFileAllString;
	}
	public void setScanFileAllString(String scanFileAllString) {
		this.scanFileAllString = scanFileAllString;
	}
	public Double getRetentionTimeInMinutesCutoff() {
		return retentionTimeInMinutesCutoff;
	}
	public void setRetentionTimeInMinutesCutoff(Double retentionTimeInMinutesCutoff) {
		this.retentionTimeInMinutesCutoff = retentionTimeInMinutesCutoff;
	}
}
