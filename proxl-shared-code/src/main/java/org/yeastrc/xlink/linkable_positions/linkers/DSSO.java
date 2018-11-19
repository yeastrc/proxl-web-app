package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

public class DSSO extends AmineLinker {

	@Override
	public String toString() {
		return "DSSO";
	}
	
	@Override
	public double getLinkerLength() {
		return 10.3;
	}

	@Override
	public Collection<String> getCrosslinkFormulas() {
		
		Collection<String> formulas = new HashSet<>();
		formulas.add( "C6H6O3S" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C6H6O3S";
	}
}
