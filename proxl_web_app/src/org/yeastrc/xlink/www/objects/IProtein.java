package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.objects.ProteinSequenceVersionObject;

public interface IProtein {

	public ProteinSequenceVersionObject getProteinSequenceVersionObject();
	public String getName() throws Exception;
	public String getDescription() throws Exception;
	
}
