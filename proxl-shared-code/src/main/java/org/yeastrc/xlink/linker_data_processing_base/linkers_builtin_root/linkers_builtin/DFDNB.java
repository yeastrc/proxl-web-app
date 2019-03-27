package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.HashSet;
import java.util.Set;

import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.AmineLinker;

public class DFDNB extends AmineLinker {

	@Override
	public String toString() {
		return "DFDNB";
	}
	
	@Override
	public double getLinkerLength() {
		return 3;
	}
	
	@Override
	public Set<String> getCrosslinkFormulas() {
		
		Set<String> formulas = new HashSet<>();
		formulas.add( "C6N2O4" );
		
		return formulas;
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {
		return "C6N2O4";
	}

	@Override
	public boolean isCleavable() {
		return false;
	}

	@Override
	public Set<String> getCleavedCrosslinkFormulas() {
		return null;
	}

	@Override
	public String getCleavedCrosslinkFormula(double mass) throws Exception {
		return null;
	}
}
