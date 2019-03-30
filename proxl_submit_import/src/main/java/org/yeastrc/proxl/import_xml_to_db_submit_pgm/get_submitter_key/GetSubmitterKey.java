package org.yeastrc.proxl.import_xml_to_db_submit_pgm.get_submitter_key;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

//import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;

import org.yeastrc.proxl.import_xml_to_db_submit_pgm.constants.ProxlXMLFileUploadSubmitterPgmSameMachineConstants;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportReportedErrorException;



/**
 * 
 *
 */
public class GetSubmitterKey {


//	private static final Logger log = LoggerFactory.getLogger( GetSubmitterKey.class);


	private static final GetSubmitterKey instance = new GetSubmitterKey();

	private GetSubmitterKey() { }
	public static GetSubmitterKey getInstance() { return instance; }

	public String getSubmitterKey( String uploadTempSubdirString, File uploadTmpBaseDir ) throws Exception {

		String submitterKey = null;

		File uploadTempSubdir = null;

		File submitterKeyFile = null;

		try {


			uploadTempSubdir = new File( uploadTmpBaseDir, uploadTempSubdirString );

			if ( ! uploadTempSubdir.exists() ) {

				String msg = "Temp Upload Directory (from server) does not exist: " + uploadTempSubdir.getCanonicalPath();
				System.err.println( msg );
				throw new ProxlSubImportReportedErrorException( msg );
			}

			submitterKeyFile = new File( uploadTempSubdir, ProxlXMLFileUploadSubmitterPgmSameMachineConstants.SUBMITTER_KEY_FILENAME );

			if ( ! submitterKeyFile.exists() ) {

				String msg = "File in Temp Upload Directory containing Submitter Key does not exist: " + submitterKeyFile.getCanonicalPath();
				System.err.println( msg );

				throw new ProxlSubImportReportedErrorException( msg );
			}

			BufferedReader reader = null;

			try {

				reader = new BufferedReader( new FileReader(submitterKeyFile));

				submitterKey = reader.readLine();

			} catch (Exception e ) {

				String msg = "Error reading File in Temp Upload Directory containing Submitter Key: " + submitterKeyFile.getCanonicalPath();
				System.err.println( msg );
				e.printStackTrace();

				throw new ProxlSubImportReportedErrorException( msg, e );

			} finally {

				if ( reader != null ) {

					reader.close();
				}
			}


			return submitterKey;

		} catch ( ProxlSubImportReportedErrorException e ) {

			throw e;

		} catch (Exception e ) {

			String msg = "Error reading File in Temp Upload Directory containing Submitter Key: " + submitterKeyFile.getCanonicalPath();
			System.err.println( msg );
			e.printStackTrace();

			throw new ProxlSubImportReportedErrorException( msg, e );

		}
	}
}