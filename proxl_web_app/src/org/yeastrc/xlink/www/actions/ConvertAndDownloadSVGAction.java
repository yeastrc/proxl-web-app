package org.yeastrc.xlink.www.actions;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.forms.ConvertAndDownloadSVGForm;

/**
 * An action for converting a SVG document to the requested type and initiating the
 * download of the file resulting from the conversion.
 * 
 * @author mriffle
 *
 */
public class ConvertAndDownloadSVGAction extends Action {

	private static final Logger log = Logger.getLogger(ConvertAndDownloadSVGAction.class);
	
	private static final Map<String, String> mimeTypes;
	private static final Map<String, String> extensions;
	
	static {
		
		mimeTypes = new HashMap<>();
		
		mimeTypes.put( "pdf",  "application/pdf" );
		mimeTypes.put( "svg",  "image/svg+xml" );
		mimeTypes.put( "png",  "image/png" );
		mimeTypes.put( "jpeg", "image/jpeg" );
		
		extensions = new HashMap<>();
		
		extensions.put( "pdf",  ".pdf" );
		extensions.put( "svg",  ".svg" );
		extensions.put( "png",  ".png" );
		extensions.put( "jpeg", ".jpg" );
		
	}
	
	public ActionForward execute( ActionMapping mapping,
			  ActionForm form,
			  HttpServletRequest request,
			  HttpServletResponse response ) throws Exception {
		try {
			// ensure referrer was on the same server as this Action to prevent abuse
			URL referrerURL = new URL( request.getHeader("referer") );
			URL thisURL = new URL( request.getRequestURL().toString() );
			
			if( !referrerURL.getHost().equals( thisURL.getHost() ) ) {
				log.error( "This host and referrer host do not match.  Exiting and not returning anything to browser" );
				return null;
			}
			
			ConvertAndDownloadSVGForm downloadForm = (ConvertAndDownloadSVGForm)form;
			if ( StringUtils.isEmpty( downloadForm.getSvgString() ) ) {
				String msg = "downloadForm.getSvgString() is empty.";
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			if ( StringUtils.isEmpty( downloadForm.getFileType() ) ) {
				String msg = "downloadForm.getFileType() is empty.";
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			
//			String svgString = downloadForm.getSvgString();
//			int svgStringLength = svgString.length();
//			String svgStringLast100Chars = svgString.substring( svgString.length() - 100 );

			byte[] fileBytes = this.getBytes( downloadForm.getSvgString(), downloadForm.getFileType() );
			String fileName = this.getFilename( downloadForm.getFileType() );

			String contentTypeString = mimeTypes.get( downloadForm.getFileType() ); 
			
			if ( contentTypeString == null ) {
				log.error( "downloadForm.getFileType() not == any supported mime types, is: '" + downloadForm.getFileType() 
						+ "'.  Exiting and not returning anything to browser" );
				return null;
			}
			
			response.setContentType( contentTypeString );
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			response.setContentLength( fileBytes.length );

			BufferedOutputStream bos = null;
			
			try {
				ServletOutputStream out = response.getOutputStream();
				bos = new BufferedOutputStream(out);
				out.write( fileBytes );
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

	/**
	 * Get the file name to use
	 * 
	 * @param type
	 * @return
	 */
	private String getFilename( String type ) {
		
		String base = "proxl-image";
		base += extensions.get( type );
		
		return base;
	}
	
	
	/**
	 * Get the byte array for the conversion of the SVG to the requested type
	 * 
	 * @param svgString
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private byte[] getBytes( String svgString, String type ) throws Exception {
		
		if ( StringUtils.isEmpty( svgString ) ) {
			String msg = "incoming svgString is empty.";
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		if ( StringUtils.isEmpty( type ) ) {
			String msg = "incoming type is empty.";
			log.error( msg );
			throw new ProxlWebappDataException(msg);
		}
		
		// it's already svg, don't do much
		if( type.equals( "svg" ) ) {
			return svgString.getBytes();
		}
		
        // Create the transcoder input.
        InputStream istream = new ByteArrayInputStream( svgString.getBytes() );
        TranscoderInput input = new TranscoderInput( istream );
        
        // Create the transcoder output.
        ByteArrayOutputStream ostream = new ByteArrayOutputStream( );
        TranscoderOutput output = new TranscoderOutput(ostream);
		
		if( type.equals( "pdf" ) ) {
	        // Create a PDF transcoder
    		PDFTranscoder t = new PDFTranscoder();
	        // Save the image.
	        t.transcode(input, output);			

		} else if( type.equals( "png" ) ) {
	        // Create a PNG transcoder
    		PNGTranscoder t = new PNGTranscoder();
    		t.addTranscodingHint( PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white );
	        // Save the image.
	        t.transcode(input, output);			

		} else if( type.equals( "jpeg" ) ) {
	        // Create a JPEG transcoder
	        JPEGTranscoder t = new JPEGTranscoder();
	        // Set the transcoding hints.
	        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.8));
    		t.addTranscodingHint( JPEGTranscoder.KEY_BACKGROUND_COLOR, Color.white );
	        // Save the image.
	        t.transcode(input, output);			
		} else {
			throw new ProxlWebappDataException( "Unsupported type: " + type );
		}
		
		return ostream.toByteArray();
	}
}

