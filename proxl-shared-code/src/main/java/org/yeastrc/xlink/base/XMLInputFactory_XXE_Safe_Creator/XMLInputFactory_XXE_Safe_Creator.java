package org.yeastrc.xlink.base.XMLInputFactory_XXE_Safe_Creator;

import javax.xml.stream.XMLInputFactory;

/**
 * Create XMLInputFactory object that is XXE safe
 *
 */
public class XMLInputFactory_XXE_Safe_Creator {

	/**
	 * @return Create XMLInputFactory object that is XXE safe
	 */
	public static XMLInputFactory xmlInputFactory_XXE_Safe_Creator() {
		
		XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		
		return xmlInputFactory;
	}
}
