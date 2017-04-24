package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;

import org.yeastrc.xlink.linkable_positions.linkers.AmineLinker;

public class DFDNB extends AmineLinker {

	@Override
	public double getLinkerLength() {
		return 3;
	}
	
	@Override
	public Collection<String> getCrosslinkFormula() throws Exception {
		throw new Exception( "Undefined for this cross-linker." );
	}
}
