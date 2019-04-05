package org.yeastrc.xlink.www.qc_data.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.xlink.www.qc_data.a_request_json_root.QCPageRequestJSONRoot;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Take request JSON and Deserialize to object of class QCPageRequestJSONRoot
 *
 */
public class QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot {

	private static final Logger log = LoggerFactory.getLogger( QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot.class );

	/**
	 * private constructor
	 */
	private QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot(){}
	public static QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot getInstance( ) throws Exception {
		QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot instance = new QC_DeserializeRequestJSON_To_QCPageRequestJSONRoot();
		return instance;
	}
	
	/**
	 * @param requestJSONString
	 * @return
	 * @throws IOException 
	 */
	public QCPageRequestJSONRoot deserializeRequestJSON_To_QCPageRequestJSONRoot( String requestJSONString ) throws IOException {
		
		byte[] requestJSONBytes = requestJSONString.getBytes( StandardCharsets.UTF_8 );
		
		return deserializeRequestJSON_To_QCPageRequestJSONRoot( requestJSONBytes );
	}
	
	/**
	 * @param requestJSONBytes
	 * @return
	 * @throws IOException 
	 */
	public QCPageRequestJSONRoot deserializeRequestJSON_To_QCPageRequestJSONRoot( byte[] requestJSONBytes ) throws IOException {

		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		//   deserialize 
		QCPageRequestJSONRoot qcPageRequestJSONRoot = null;
		try {
			qcPageRequestJSONRoot = jacksonJSON_Mapper.readValue( requestJSONBytes, QCPageRequestJSONRoot.class );
		} catch ( JsonParseException e ) {
			String msg = "Failed to parse 'postBody', JsonParseException. requestJSONBytes: " + getStringFromBytes( requestJSONBytes ); 
			log.error( msg, e );
			throw e;
		} catch ( JsonMappingException e ) {
			String msg = "Failed to parse 'postBody', JsonMappingException. requestJSONBytes: " + getStringFromBytes( requestJSONBytes );
			log.error( msg, e );
			throw e;
		} catch ( IOException e ) {
			String msg = "Failed to parse 'postBody', IOException. requestJSONBytes: " + getStringFromBytes( requestJSONBytes );
			log.error( msg, e );
			throw e;
		}
		
		return qcPageRequestJSONRoot;
	}
	
	private String getStringFromBytes( byte[] bytes ) {
		
		try {
			String string = new String(bytes, StandardCharsets.UTF_8 );
			return string;
		} catch (Throwable t ) {
			log.warn( "Failed to convert bytes to string for error message");
			return "Failed to convert bytes to string for error message";
		}
	}
}
