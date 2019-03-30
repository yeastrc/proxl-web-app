package org.yeastrc.xlink.www.annotation_display;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
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
public class DeserializeAnnTypeIdDisplayIncludeExclude {
	
	private static final Logger log = LoggerFactory.getLogger( DeserializeAnnTypeIdDisplayIncludeExclude.class);
	private DeserializeAnnTypeIdDisplayIncludeExclude() { }
	public static DeserializeAnnTypeIdDisplayIncludeExclude getInstance() { return new DeserializeAnnTypeIdDisplayIncludeExclude(); }
	
	/**
	 * Take the AnnTypeIdDisplayJSON_PsmPeptide as serialized JSON and deserialize into the object
	 * 
	 * @param psmAnnTypeDisplayIncludeExclude_JSONString
	 * @return 
	 * @throws Exception 
	 */
	public AnnTypeIdDisplayJSON_PsmPeptide deserialize_JSON_ToAnnTypeIdDisplayJSON_PsmPeptide ( String psmAnnTypeDisplayIncludeExclude_JSONString ) throws Exception {
		//   Get PSM or Peptide display data from JSON
		if ( StringUtils.isEmpty( psmAnnTypeDisplayIncludeExclude_JSONString ) ) {
			return null;
		}
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON Mapper object for JSON deserialization
		AnnTypeIdDisplayJSON_PsmPeptide annTypeIdDisplayJSON_PsmPeptide = null;
		try {
			annTypeIdDisplayJSON_PsmPeptide = jacksonJSON_Mapper.readValue( psmAnnTypeDisplayIncludeExclude_JSONString, AnnTypeIdDisplayJSON_PsmPeptide.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'psmAnnTypeDisplayIncludeExclude_JSONString', JsonParseException.  psmAnnTypeDisplayIncludeExclude_JSONString: " + psmAnnTypeDisplayIncludeExclude_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'psmAnnTypeDisplayIncludeExclude_JSONString', JsonMappingException.  psmAnnTypeDisplayIncludeExclude_JSONString: " + psmAnnTypeDisplayIncludeExclude_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		} catch ( IOException e ) {
			String msg = "Failed to parse 'psmAnnTypeDisplayIncludeExclude_JSONString', IOException.  psmAnnTypeDisplayIncludeExclude_JSONString: " + psmAnnTypeDisplayIncludeExclude_JSONString;
			log.error( msg, e );
			throw new ProxlWebappDataException( msg, e );
		}
		return annTypeIdDisplayJSON_PsmPeptide;
	}
}
