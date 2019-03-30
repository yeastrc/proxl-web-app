package org.yeastrc.xlink.www.form_query_json_objects;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Take the  as serialized JSON and deserialize into the object
 *
 */
public class Z_Deserialize_ExcludeLinksWith_JSONRoot {

	private static final Logger log = LoggerFactory.getLogger( Z_Deserialize_ExcludeLinksWith_JSONRoot.class);
	private Z_Deserialize_ExcludeLinksWith_JSONRoot() { }
	public static Z_Deserialize_ExcludeLinksWith_JSONRoot getInstance() { return new Z_Deserialize_ExcludeLinksWith_JSONRoot(); }
	
	/**
	 * Take the ExcludeLinksWith_JSONRoot as serialized JSON and deserialize into the object
	 * 
	 * @param excludeLinksWith_JSONRoot_JSONString
	 * @return 
	 * @throws Exception 
	 */
	public ExcludeLinksWith_JSONRoot deserialize_JSON_ToExcludeLinksWith_JSONRoot ( String excludeLinksWith_JSONRoot_JSONString ) throws Exception {
		//   Get PSM or Peptide display data from JSON
		if ( StringUtils.isEmpty( excludeLinksWith_JSONRoot_JSONString ) ) {
			return null;
		}
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization
		ExcludeLinksWith_JSONRoot excludeLinksWith_JSONRoot = null;
		try {
			excludeLinksWith_JSONRoot = jacksonJSON_Mapper.readValue( excludeLinksWith_JSONRoot_JSONString, ExcludeLinksWith_JSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'excludeLinksWith_JSONRoot_JSONString', JsonParseException.  excludeLinksWith_JSONRoot_JSONString: " + excludeLinksWith_JSONRoot_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'excludeLinksWith_JSONRoot_JSONString', JsonMappingException.  excludeLinksWith_JSONRoot_JSONString: " + excludeLinksWith_JSONRoot_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( IOException e ) {
			String msg = "Failed to parse 'excludeLinksWith_JSONRoot_JSONString', IOException.  excludeLinksWith_JSONRoot_JSONString: " + excludeLinksWith_JSONRoot_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
		return excludeLinksWith_JSONRoot;
	}

}
