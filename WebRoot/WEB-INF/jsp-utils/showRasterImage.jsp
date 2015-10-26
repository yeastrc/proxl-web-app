<%@ page contentType="image/png" 
    import="java.awt.*,java.awt.image.*,
      com.sun.image.codec.jpeg.*,java.util.*,java.awt.image.renderable.*,javax.media.jai.*,
      javax.media.jai.operator.*,
      org.apache.log4j.Logger"
%><%


try {

// Send back image
	ServletOutputStream sos = response.getOutputStream();
	
//JPEGImageEncoder encoder = 
  //JPEGCodec.createJPEGEncoder(sos);
//encoder.encode((Raster)(request.getAttribute("image")));


	EncodeDescriptor.create( ((RenderedImage)(request.getAttribute("image"))), sos, "png", null, null);

} catch ( Exception e ) {

	String msg = "Exception rendering image in showRasterImage.jsp, QueryString = " + request.getQueryString() + ", RequestURI = " + request.getRequestURI();
	
	Logger log = Logger.getLogger("showRasterImage.jsp");

	log.error( msg, e );

	
	System.err.println( msg );
	
	e.printStackTrace();
	
	throw e;
}

%>