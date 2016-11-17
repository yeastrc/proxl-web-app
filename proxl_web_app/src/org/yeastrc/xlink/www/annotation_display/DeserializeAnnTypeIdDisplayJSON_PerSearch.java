package org.yeastrc.xlink.www.annotation_display;


import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Take the  as serialized JSON and deserialize into the object
 * 
 * 
 */
public class DeserializeAnnTypeIdDisplayJSON_PerSearch {

	private static final Logger log = Logger.getLogger(DeserializeAnnTypeIdDisplayJSON_PerSearch.class);

	private DeserializeAnnTypeIdDisplayJSON_PerSearch() { }
	public static DeserializeAnnTypeIdDisplayJSON_PerSearch getInstance() { return new DeserializeAnnTypeIdDisplayJSON_PerSearch(); }
	
	
	

	/**
	 * Take the AnnTypeIdDisplayJSON_PerSearch as serialized JSON and deserialize into the object
	 * 
	 * @param annTypeIdDisplayJSON_PerSearch_JSONString
	 * @return 
	 * @throws Exception 
	 */
	public AnnTypeIdDisplayJSON_PerSearch deserializeAnnTypeIdDisplayJSON_PerSearch ( String annTypeIdDisplayJSON_PerSearch_JSONString ) throws Exception {

		//   Get PSM or Peptide display data from JSON

		if ( StringUtils.isEmpty( annTypeIdDisplayJSON_PerSearch_JSONString ) ) {
			
			return null;
		}

		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization


		AnnTypeIdDisplayJSON_PerSearch annTypeIdDisplayJSONRoot = null;

		try {
			annTypeIdDisplayJSONRoot = jacksonJSON_Mapper.readValue( annTypeIdDisplayJSON_PerSearch_JSONString, AnnTypeIdDisplayJSON_PerSearch.class );

		} catch ( JsonParseException e ) {

			String msg = "Failed to parse 'annTypeIdDisplayJSON_PerSearch_JSONString', JsonParseException.  annTypeIdDisplayJSON_PerSearch_JSONString: " + annTypeIdDisplayJSON_PerSearch_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );

		} catch ( JsonMappingException e ) {

			String msg = "Failed to parse 'annTypeIdDisplayJSON_PerSearch_JSONString', JsonMappingException.  annTypeIdDisplayJSON_PerSearch_JSONString: " + annTypeIdDisplayJSON_PerSearch_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );

		} catch ( IOException e ) {

			String msg = "Failed to parse 'annTypeIdDisplayJSON_PerSearch_JSONString', IOException.  annTypeIdDisplayJSON_PerSearch_JSONString: " + annTypeIdDisplayJSON_PerSearch_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
		
		return annTypeIdDisplayJSONRoot;
	}
}
