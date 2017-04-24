package org.yeastrc.xlink.linkable_positions.linkers;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.linkable_positions.linkers.AmineLinker;

public class BS2 extends AmineLinker {

	@Override
	public double getLinkerLength() {
		return 7.7;
	}
	
	@Override
	public Collection<String> getCrosslinkFormulas() {
		
		Collection<String> formulas = new HashSet<>();
		formulas.add( "C5H4O2" );
		
		return formulas;
	}

	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C5H4O2";
	}
	
	
}
