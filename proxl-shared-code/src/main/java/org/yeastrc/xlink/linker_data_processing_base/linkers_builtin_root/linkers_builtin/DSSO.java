package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DSSO extends AmineLinker {

	private static final Map<String, BigDecimal> FORMULA_MASSES;

	static {
		FORMULA_MASSES = new HashMap<>();

		FORMULA_MASSES.put( "C6H6O3S", BigDecimal.valueOf( 158.0038 ) );
		FORMULA_MASSES.put( "C3H2O", BigDecimal.valueOf( 54.01056 ) );
		FORMULA_MASSES.put( "C3H4O2S", BigDecimal.valueOf( 103.9932 ) );
		FORMULA_MASSES.put( "C3H2OS", BigDecimal.valueOf( 85.982635 ) );
	}

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
		return FORMULA_MASSES.keySet();
	}
	
	@Override
	public String getCrosslinkFormula(double mass) throws Exception {

		BigDecimal testMass = BigDecimal.valueOf( mass ).setScale( 2, RoundingMode.HALF_UP );

		for( String formula : FORMULA_MASSES.keySet() ) {

			BigDecimal formulaMass = FORMULA_MASSES.get( formula ).setScale( 2, RoundingMode.HALF_UP );

			if( formulaMass.equals( testMass ) ) {
				return formula;
			}
		}

		throw new Exception( "Did not get a valid mass for a DSSO cross-linker. Was given " + mass );
	}

	@Override
	public boolean isCleavable() {
		return true;
	}
}
