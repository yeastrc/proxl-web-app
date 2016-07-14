package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.www.objects.ProteinSequenceObject;

public interface IProtein {

	public ProteinSequenceObject getProteinSequenceObject();
	public String getName() throws Exception;
	public String getDescription() throws Exception;
	
}
