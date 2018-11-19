package org.yeastrc.xlink.www.annotation_display;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Take the  as serialized JSON and deserialize into the object
 */
public class DeserializeAnnTypeIdDisplayJSONRoot {
	
	private static final Logger log = Logger.getLogger(DeserializeAnnTypeIdDisplayJSONRoot.class);
	private DeserializeAnnTypeIdDisplayJSONRoot() { }
	public static DeserializeAnnTypeIdDisplayJSONRoot getInstance() { return new DeserializeAnnTypeIdDisplayJSONRoot(); }
	/**
	 * Take the AnnTypeIdDisplayJSONRoot as serialized JSON and deserialize into the object
	 * 
	 * @param annTypeIdDisplayJSONRoot_JSONString
	 * @return 
	 * @throws Exception 
	 */
	public AnnTypeIdDisplayJSONRoot deserializeAnnTypeIdDisplayJSONRoot ( String annTypeIdDisplayJSONRoot_JSONString ) throws Exception {
		//   Get PSM or Peptide display data from JSON
		if ( StringUtils.isEmpty( annTypeIdDisplayJSONRoot_JSONString ) ) {
			return null;
		}
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization
		AnnTypeIdDisplayJSONRoot annTypeIdDisplayJSONRoot = null;
		try {
			annTypeIdDisplayJSONRoot = jacksonJSON_Mapper.readValue( annTypeIdDisplayJSONRoot_JSONString, AnnTypeIdDisplayJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'annTypeIdDisplayJSONRoot_JSONString', JsonParseException.  annTypeIdDisplayJSONRoot_JSONString: " + annTypeIdDisplayJSONRoot_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'annTypeIdDisplayJSONRoot_JSONString', JsonMappingException.  annTypeIdDisplayJSONRoot_JSONString: " + annTypeIdDisplayJSONRoot_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( IOException e ) {
			String msg = "Failed to parse 'annTypeIdDisplayJSONRoot_JSONString', IOException.  annTypeIdDisplayJSONRoot_JSONString: " + annTypeIdDisplayJSONRoot_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
		return annTypeIdDisplayJSONRoot;
	}
}
