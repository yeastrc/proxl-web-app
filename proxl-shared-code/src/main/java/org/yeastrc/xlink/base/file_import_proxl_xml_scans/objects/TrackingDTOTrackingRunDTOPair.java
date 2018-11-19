package org.yeastrc.xlink.base.file_import_proxl_xml_scans.objects;

import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.file_import_proxl_xml_scans.dto.ProxlXMLFileImportTrackingRunDTO;

/**
 * A pair of ProxlXMLFileImportTrackingDTO and ProxlXMLFileImportTrackingRunDTO
 *
 */
public class TrackingDTOTrackingRunDTOPair {

	private ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO;
	private ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO;
	
	
	public ProxlXMLFileImportTrackingDTO getProxlXMLFileImportTrackingDTO() {
		return proxlXMLFileImportTrackingDTO;
	}
	public void setProxlXMLFileImportTrackingDTO(
			ProxlXMLFileImportTrackingDTO proxlXMLFileImportTrackingDTO) {
		this.proxlXMLFileImportTrackingDTO = proxlXMLFileImportTrackingDTO;
	}
	public ProxlXMLFileImportTrackingRunDTO getProxlXMLFileImportTrackingRunDTO() {
		return proxlXMLFileImportTrackingRunDTO;
	}
	public void setProxlXMLFileImportTrackingRunDTO(
			ProxlXMLFileImportTrackingRunDTO proxlXMLFileImportTrackingRunDTO) {
		this.proxlXMLFileImportTrackingRunDTO = proxlXMLFileImportTrackingRunDTO;
	}
	
}
