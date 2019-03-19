package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.Collection;
import java.util.HashSet;

import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.AmineLinker;

public class BS2 extends AmineLinker {

	@Override
	public String toString() {
		return "BS2";
	}
	
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

	@Override
	public boolean isCleavable() {
		return false;
	}
	
}
