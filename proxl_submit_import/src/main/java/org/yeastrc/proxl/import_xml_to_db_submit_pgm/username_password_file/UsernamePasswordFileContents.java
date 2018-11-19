package org.yeastrc.proxl.import_xml_to_db_submit_pgm.username_password_file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db_submit_pgm.exceptions.ProxlSubImportUsernamePasswordFileException;

/**
 * Read Username Password File Contents
 *
 */
public class UsernamePasswordFileContents {

	private static final Logger log = Logger.getLogger( UsernamePasswordFileContents.class );

	private static final UsernamePasswordFileContents instance = new UsernamePasswordFileContents();

	private UsernamePasswordFileContents() { }
	public static UsernamePasswordFileContents getInstance() { return instance; }


	private static final String PROPERTY_USERNAME = "username";
	private static final String PROPERTY_PASSWORD = "password";


	private File usernamePasswordFileCommandLine;

	private String username;
	
	private String password;

	private boolean configured;
	
	public String getUsername() {
		if ( ! configured ) {
			
			throw new IllegalStateException("readUsernamePasswordFileContents() not called or failed");
		}
		return username;
	}
	public String getPassword() {
		if ( ! configured ) {
			
			throw new IllegalStateException("readUsernamePasswordFileContents() not called or failed");
		}
		return password;
	}



	public void readUsernamePasswordFileContents() throws Exception {

		if ( configured ) {

			return;
		}

		Properties configProps = null;

		InputStream propertiesFileAsStream = null;

		try {

			if ( usernamePasswordFileCommandLine == null ) {

				throw new IllegalStateException("usernamePasswordFileCommandLine not set");
			}
			
			String fileNotFoundMsg = "Username Password File not found: " + usernamePasswordFileCommandLine.getAbsolutePath();


			if ( ! usernamePasswordFileCommandLine.exists() ) {

				System.err.println( fileNotFoundMsg );

				throw new ProxlSubImportUsernamePasswordFileException( fileNotFoundMsg );
			}

			try {

				propertiesFileAsStream = new FileInputStream( usernamePasswordFileCommandLine );

			} catch ( FileNotFoundException e ) {

				System.err.println( fileNotFoundMsg );

				throw new ProxlSubImportUsernamePasswordFileException( fileNotFoundMsg );
			}



			configProps = new Properties();

			configProps.load( propertiesFileAsStream );

		} catch (IOException e) {

			log.error( "In init(),   Username Password file '" 
					+ usernamePasswordFileCommandLine.getAbsolutePath() + "', IOException: " + e.toString(), e );

			throw new ProxlSubImportUsernamePasswordFileException( e );

		} finally {

			if ( propertiesFileAsStream != null ) {

				try {
					propertiesFileAsStream.close();
				} catch (IOException e) {

					log.error( "In init(), Username Password .close():   Username Password file '" 
							+ usernamePasswordFileCommandLine.getAbsolutePath() + "', IOException: " + e.toString(), e );

					throw new ProxlSubImportUsernamePasswordFileException( e );
				}
			}
		}

		username = configProps.getProperty( PROPERTY_USERNAME );
		
		if ( username != null ) {
			
			username = username.trim();
		}

		password = configProps.getProperty( PROPERTY_PASSWORD );
		
		if ( password != null ) {
			
			password = password.trim();
		}

		if ( StringUtils.isEmpty( username ) ) {

			String msg = "parameter '" + PROPERTY_USERNAME + "' is not provided or is empty string.";
			System.err.println( msg );
			throw new ProxlSubImportUsernamePasswordFileException(msg);
		}

		if ( StringUtils.isEmpty( password ) ) {

			String msg = "parameter '" + PROPERTY_PASSWORD + "' is not provided or is empty string.";
			System.err.println( msg );
			throw new ProxlSubImportUsernamePasswordFileException(msg);
		}
		
		
		configured = true;
	}
	

	public File getUsernamePasswordFileCommandLine() {
		return usernamePasswordFileCommandLine;
	}
	public void setUsernamePasswordFileCommandLine(
			File usernamePasswordFileCommandLine) {
		this.usernamePasswordFileCommandLine = usernamePasswordFileCommandLine;
	}
	
	
	
}
