package org.yeastrc.xlink.www.proxl_xml_file_import.objects;

/**
 * Webservice response to get array of tracking id and status id
 *
 */
public class ProxlXMLFileTrackingIdStatusId {

	private int trackingId;
	private int statusId;
	
	@Override
	public String toString() {
		return "ProxlXMLFIleTrackingIdStatusId [trackingId=" + trackingId
				+ ", statusId=" + statusId + "]";
	}
	
	public int getTrackingId() {
		return trackingId;
	}
	public void setTrackingId(int trackingId) {
		this.trackingId = trackingId;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}


}
