package org.yeastrc.xlink.www.web_utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Marshal object to JSON
 *
 */
public class MarshalObjectToJSON {

	private static final Logger log = LoggerFactory.getLogger( MarshalObjectToJSON.class );

	private MarshalObjectToJSON() { }
	private static final MarshalObjectToJSON _INSTANCE = new MarshalObjectToJSON();
	public static MarshalObjectToJSON getInstance() { return _INSTANCE; }
	
	/**
	 * @param object
	 * @param searchId
	 * @return
	 * @throws IOException
	 */
	public byte[] getJSONByteArray( Object object ) throws IOException {
		
		if ( object == null ) {
			throw new IllegalArgumentException( "param cannot be null" );
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			jacksonJSON_Mapper.writeValue( baos, object );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonParseException. class of param: " + object.getClass() ;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'resultsObject', JsonMappingException. class of param: " + object.getClass() ;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'resultsObject', IOException. class of param: " + object.getClass() ;
			log.error( msg, e );
			throw e;
		}
		
		return baos.toByteArray();
	}
	
	/**
	 * @param searchDataLookupParamsRoot
	 * @return
	 * @throws Exception
	 */
	public String getJSONString( Object object ) throws Exception {

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
		//   serialize 
		try {
			return jacksonJSON_Mapper.writeValueAsString( object );
		} catch ( JsonParseException e ) {
			String msg = "Failed to serialize 'object', JsonParseException. class of param: " + object.getClass() ;
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to serialize 'object', JsonMappingException. class of param: " + object.getClass() ;
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to serialize 'object', IOException. class of param: " + object.getClass() ;
			log.error( msg, e );
			throw e;
		}
	}
	
}
