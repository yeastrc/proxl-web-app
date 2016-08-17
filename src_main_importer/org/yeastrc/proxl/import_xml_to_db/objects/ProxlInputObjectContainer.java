package org.yeastrc.proxl.import_xml_to_db.objects;

import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;

/**
 * Holds a reference to a ProxlImport object so it can be set to null 
 * as soon as it is no longer needed to be cleared for garbage collection
 *
 */
public class ProxlInputObjectContainer {

	private ProxlInput proxlInput;

	public ProxlInput getProxlInput() {
		return proxlInput;
	}

	public void setProxlInput(ProxlInput proxlInput) {
		this.proxlInput = proxlInput;
	}

}
