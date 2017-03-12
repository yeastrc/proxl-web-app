package org.yeastrc.xlink.www.web_utils;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesRootLevel;
import org.yeastrc.xlink.www.form_query_json_objects.CutoffValuesSearchLevel;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * 
 * Take the CutoffValuesRootLevel as serialized JSON and deserialize into the object
 * 
 * 
 */
public class DeserializeCutoffForWebservices {
	
	private static final Logger log = Logger.getLogger(DeserializeCutoffForWebservices.class);
	private DeserializeCutoffForWebservices() { }
	public static DeserializeCutoffForWebservices getInstance() { return new DeserializeCutoffForWebservices(); }
	
	/**
	 * Take the CutoffValuesRootLevel as serialized JSON and deserialize into the object
	 * 
	 * @param psmPeptideCutoffsForSearchIds_JSONString
	 * @return 
	 * @throws Exception 
	 */
	public CutoffValuesRootLevel deserialize_JSON_ToCutoffRoot ( String psmPeptideCutoffsForSearchIds_JSONString ) throws Exception {
		//   Get PSM and Peptide Cutoff data from JSON
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization
		CutoffValuesRootLevel cutoffValuesRootLevel = null;
		try {
			cutoffValuesRootLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForSearchIds_JSONString, CutoffValuesRootLevel.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonParseException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', JsonMappingException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( IOException e ) {
			String msg = "Failed to parse 'psmPeptideCutoffsForSearchIds_JSONString', IOException.  psmPeptideCutoffsForSearchIds_JSONString: " + psmPeptideCutoffsForSearchIds_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
		return cutoffValuesRootLevel;
	}
	
	/**
	 * Take the CutoffValuesSearchLevel as serialized JSON and deserialize into the object
	 * 
	 * @param psmPeptideCutoffsForSearchId_JSONString
	 * @return 
	 * @throws Exception 
	 */
	public CutoffValuesSearchLevel deserialize_JSON_ToCutoffSearchLevel ( String psmPeptideCutoffsForSearchId_JSONString ) throws Exception {
		//   Get PSM and Peptide Cutoff data from JSON
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization
		CutoffValuesSearchLevel cutoffValuesSearchLevel = null;
		try {
			cutoffValuesSearchLevel = jacksonJSON_Mapper.readValue( psmPeptideCutoffsForSearchId_JSONString, CutoffValuesSearchLevel.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'psmPeptideCutoffsForSearchId_JSONString', JsonParseException.  psmPeptideCutoffsForSearchId_JSONString: " + psmPeptideCutoffsForSearchId_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'psmPeptideCutoffsForSearchId_JSONString', JsonMappingException.  psmPeptideCutoffsForSearchId_JSONString: " + psmPeptideCutoffsForSearchId_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( IOException e ) {
			String msg = "Failed to parse 'psmPeptideCutoffsForSearchId_JSONString', IOException.  psmPeptideCutoffsForSearchId_JSONString: " + psmPeptideCutoffsForSearchId_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
		return cutoffValuesSearchLevel;
	}
}
