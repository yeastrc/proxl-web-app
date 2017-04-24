package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.linkable_positions.linkers.AmineLinker;

public class DFDNB extends AmineLinker {

	@Override
	public double getLinkerLength() {
		return 3;
	}
	
	@Override
	public Collection<String> getCrosslinkFormulas() {
		
		Collection<String> formulas = new HashSet<>();
		formulas.add( "C6N2O4" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C6N2O4";
	}
}
