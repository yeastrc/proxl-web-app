package org.yeastrc.xlink.www.proxl_xml_file_import.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
//import org.apache.log4j.Logger;
import org.yeastrc.proxl_import.xsd_element_attr_names_constants.XSD_ElementAttributeNamesConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;

/**
 * This does minimal validation of Proxl XML file
 * and retrieves search name if in the file
 *
 */
public class Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile {
	
//	private static final Logger log = Logger.getLogger( Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile.class );
	//  private constructor
	private Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile() { }
	/**
	 * @return newly created instance
	 */
	public static Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile getInstance() { 
		return new Minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile(); 
	}
	
	/**
	 * This does minimal validation of Proxl XML file
	 * and retrieves search name if in the file
	 * 
	 * @param proxlXMLFile
	 * @return - search name if in file
	 * @throws FileNotFoundException
	 * @throws FactoryConfigurationError
	 * @throws XMLStreamException
	 * @throws IOException
	 * @throws ProxlWebappDataException
	 */
	public String minimal_Validate_ProxlXMLFile_AndGetSearchNameIfInFile( File proxlXMLFile ) 
			throws FileNotFoundException,
			FactoryConfigurationError, XMLStreamException, IOException, ProxlWebappDataException {
		String searchName = null;
		InputStream proxlXMLFileInputStream = null;
		XMLEventReader xmlEventReader = null;
		try {
			proxlXMLFileInputStream = new FileInputStream( proxlXMLFile );
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			xmlEventReader = xmlInputFactory.createXMLEventReader( proxlXMLFileInputStream );
			while ( xmlEventReader.hasNext() ) {
				XMLEvent event = xmlEventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if ( ! startElement.getName().getLocalPart().equals( XSD_ElementAttributeNamesConstants.ROOT_ELEMENT_PROXL_INPUT__NAME ) ) {
						//  The first element found is not the root element in the XSD so this is an error
						throw new ProxlWebappDataException( "Proxl XML root node is not '" 
								+ XSD_ElementAttributeNamesConstants.ROOT_ELEMENT_PROXL_INPUT__NAME
								+ "'.");
					}
					// this is the Proxl XML Root element, process it
					// read the attributes from this tag for the 
					// attribute to our object
					@SuppressWarnings("unchecked")
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						String attrName = attribute.getName().getLocalPart();
						String attrValue = attribute.getValue();
						if ( XSD_ElementAttributeNamesConstants.ATTR__NAME__ON_ROOT_ELEMENT_PROXL_INPUT__NAME.equals(attrName) ) {
							searchName = attrValue;
						}
					}
					break;  //  Exit Loop since processed first element
				}
			}
		} finally {
			if ( xmlEventReader != null ) {
				xmlEventReader.close();
			}
			if ( proxlXMLFileInputStream != null ) {
				proxlXMLFileInputStream.close();
			}
		}
		return searchName;
	}
}
