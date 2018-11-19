package org.yeastrc.xlink.www.actions;

import java.io.BufferedOutputStream;
import java.net.URL;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.yeastrc.xlink.www.forms.DownloadStringAsFileForm;
/**
 * Takes in a file name, mimetype, and string to trigger a file download of a text file containing that string and with that mimetype
 * @author Michael Riffle
 *
 */
public class DownloadStringAsFileAction extends Action {
	
	private static final Logger log = Logger.getLogger(DownloadStringAsFileAction.class);
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			DownloadStringAsFileForm dsform = (DownloadStringAsFileForm)form;
			if( dsform.getContent() == null || dsform.getContent().length() < 1 ) {
				dsform.setContent( "" );
			}
			if( dsform.getFilename() == null || dsform.getFilename().length() < 1 ) {
				dsform.setFilename( String.valueOf( (new DateTime() ).getMillis() ) );
			}
			if( dsform.getMimetype() == null || dsform.getMimetype().length() < 1 ) {
				dsform.setMimetype( "text/plain" );
			}
			if( request.getHeader("referer") == null ) {
				log.error( "No referer.  Exiting and not returning anything to browser" );
				return null;
			}
			// ensure referrer was on the same server as this Action
			URL referrerURL = new URL( request.getHeader("referer") );
			URL thisURL = new URL( request.getRequestURL().toString() );
			if( !referrerURL.getHost().equals( thisURL.getHost() ) ) {
				log.error( "This host and referrer host do not match.  Exiting and not returning anything to browser" );
				return null;
			}
			byte[] fileContents = dsform.getContent().getBytes();
			// generate file name
			String filename = dsform.getFilename();
			response.setContentType( dsform.getMimetype() );
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentLength( fileContents.length );
			BufferedOutputStream bos = null;
			try {
				ServletOutputStream out = response.getOutputStream();
				bos = new BufferedOutputStream(out);
				out.write( fileContents );
			} finally {
				try {
					if ( bos != null ) {
						bos.close();
					}
				} catch ( Exception ex ) {
					log.error( "bos.close():Exception " + ex.toString(), ex );
				}
				try {
					response.flushBuffer();
				} catch ( Exception ex ) {
					log.error( "response.flushBuffer():Exception " + ex.toString(), ex );
				}
			}
			return null;
		} catch ( Exception e ) {
			String msg = "Exception:  RemoteAddr: " + request.getRemoteAddr()  
					+ ", Exception caught: " + e.toString();
			log.error( msg, e );
			throw e;
		}
	}
}