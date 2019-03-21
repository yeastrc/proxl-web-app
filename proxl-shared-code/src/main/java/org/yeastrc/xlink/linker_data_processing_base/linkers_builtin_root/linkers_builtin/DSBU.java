package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DSBU extends AmineLinker {

    private static final Map<String, BigDecimal> FORMULA_MASSES;

    static {
        FORMULA_MASSES = new HashMap<>();

        FORMULA_MASSES.put( "C9H14O3", BigDecimal.valueOf( 196.0848 ) );
        FORMULA_MASSES.put( "C4H7NO", BigDecimal.valueOf( 85.05276 ) );
        FORMULA_MASSES.put( "C5H5NO2", BigDecimal.valueOf( 111.0320 ) );
    }

    @Override
    public String toString() {
        return "DSBU";
    }

    @Override
    public double getLinkerLength() {
        return 12.5;
    }

    @Override
    public Set<String> getCrosslinkFormulas() {
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

        throw new Exception( "Did not get a valid mass for a DSBU cross-linker. Was given " + mass );
    }

    @Override
    public boolean isCleavable() {
        return true;
    }
}
