package org.yeastrc.xlink.www.webservices_utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.yeastrc.xlink.www.web_utils.UnmarshalJSON_ToObject;

/**
 * Unmarshal Rest Request JSON ToObject
 *
 */
public class Unmarshal_RestRequest_JSON_ToObject {

	private Unmarshal_RestRequest_JSON_ToObject() { }
	private static final Unmarshal_RestRequest_JSON_ToObject _INSTANCE = new Unmarshal_RestRequest_JSON_ToObject();
	public static Unmarshal_RestRequest_JSON_ToObject getInstance() { return _INSTANCE; }
	
	/**
	 * @param bytesJSON
	 * @param valueType
	 * @return
	 * @throws WebApplicationException(BAD_REQUEST) - when unmarshal fails
	 */
	public <T> T getObjectFromJSONByteArray( byte[] bytesJSON, Class<T> valueType ) {
		
		try {
			return UnmarshalJSON_ToObject.getInstance().getObjectFromJSONByteArray( bytesJSON, valueType );
		} catch ( Exception e ) {
		    throw new WebApplicationException(
		    	      Response.status(javax.ws.rs.core.Response.Status.BAD_REQUEST)  //  return 400 error
//		    	        .entity(  )
		    	        .build()
		    	        );
		}
	}
}
