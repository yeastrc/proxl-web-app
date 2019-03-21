package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.AmineLinker;

public class DSS extends AmineLinker {

	@Override
	public String toString() {
		return "DSS";
	}
	
	@Override
	public double getLinkerLength() {
		return 11.4;
	}
	
	@Override
	public Set<String> getCrosslinkFormulas() {
		
		Set<String> formulas = new HashSet<>();
		formulas.add( "C8H10O2" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C8H10O2";
	}

	@Override
	public boolean isCleavable() {
		return false;
	}
	
}
