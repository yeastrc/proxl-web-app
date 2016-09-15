package org.yeastrc.xlink.base.proxl_xml_file_import.objects;

import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingDTO;
import org.yeastrc.xlink.base.proxl_xml_file_import.dto.ProxlXMLFileImportTrackingRunDTO;

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
