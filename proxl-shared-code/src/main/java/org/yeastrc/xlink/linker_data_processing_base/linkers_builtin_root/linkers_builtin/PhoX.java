package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin;

import java.util.HashSet;
import java.util.Set;

public class PhoX extends AmineLinker {

    @Override
    public String toString() {
        return "PhoX";
    }

    @Override
    public double getLinkerLength() {
        return 5;
    }

    @Override
    public Set<String> getCrosslinkFormulas() {

        Set<String> formulas = new HashSet<>();
        formulas.add( "C8H3O5P" );

        return formulas;
    }

    @Override
    public String getCrosslinkFormula(double mass) throws Exception {
        return "C8H3O5P";
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
