package org.yeastrc.xlink.www.objects;

import org.yeastrc.xlink.dto.NRProteinDTO;

public interface IProtein {

	public NRProteinDTO getNrProtein();
	public String getName() throws Exception;
	public String getDescription() throws Exception;
	
}
