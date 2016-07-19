package org.yeastrc.proxl.import_xml_to_db.utils;

import org.yeastrc.proxl.import_xml_to_db.constants.FASTA_DataTruncationConstants;

/**
 * Truncate the name property in protein annotation to value in FASTA_DataTruncationConstants
 *
 */
public class ProteinAnnotationNameTruncationUtil {

	/**
	 * Truncate header name, if needed
	 * 
	 * @param headerName
	 * @return
	 */
	public static String truncateProteinAnnotationName( String headerName ) {
		
		
		//  Truncate header name, if needed
		
		if ( headerName != null ) {
			
			if ( headerName.length() > FASTA_DataTruncationConstants.HEADER_NAME_MAX_LENGTH ) {
				
				headerName = headerName.substring( 0, FASTA_DataTruncationConstants.HEADER_NAME_MAX_LENGTH );
			}

		}
		
		return headerName;
	}

}
