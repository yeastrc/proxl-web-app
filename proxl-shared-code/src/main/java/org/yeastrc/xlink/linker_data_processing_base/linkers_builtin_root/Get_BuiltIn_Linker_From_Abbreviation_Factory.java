package org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.Get_BuiltIn_Linker_From_Abbreviation_Factory;
import org.yeastrc.xlink.linker_data_processing_base.linkers_builtin_root.linkers_builtin.*;

public class Get_BuiltIn_Linker_From_Abbreviation_Factory {
	
	private static final Logger log = LoggerFactory.getLogger( Get_BuiltIn_Linker_From_Abbreviation_Factory.class);
	
	//   WARNING:  ALL linker abbreviations MUST be Lower Case.  This is because in the importer the incoming linker is converted to lower case
	
	public static final String BUILT_IN_LINKER_BMOE = "bmoe";
	public static final String BUILT_IN_LINKER_BS2 = "bs2";
	public static final String BUILT_IN_LINKER_BS3 = "bs3";
	public static final String BUILT_IN_LINKER_DSG = "dsg";
	public static final String BUILT_IN_LINKER_DSS = "dss";
	public static final String BUILT_IN_LINKER_EDC = "edc";
	public static final String BUILT_IN_LINKER_DFDNB = "dfdnb";
	public static final String BUILT_IN_LINKER_SULFO_SMCC = "sulfo-smcc";
	public static final String BUILD_IN_LINKER_DSSO = "dsso";
	public static final String BUILD_IN_LINKER_DSBU = "dsbu";
	public static final String BUILD_IN_LINKER_PHOX = "phox";

	public static final String BUILD_IN_LINKER_TG = "tg";
	public static final String BUILD_IN_LINKER_transglutaminase = "transglutaminase";
	
	public static final String BUILT_IN_LINKER_BS3_STY = "bs3.sty";
	public static final String BUILT_IN_LINKER_DSS_STY = "dss.sty";

	//   WARNING:  ALL linker abbreviations MUST be Lower Case.  This is because in the importer the incoming linker is converted to lower case
	
	

	private static ILinker_Builtin_Linker LINKER_BMOE = new BMOE();
	private static ILinker_Builtin_Linker LINKER_BS2 = new BS2();
	private static ILinker_Builtin_Linker LINKER_BS3 = new BS3();
	private static ILinker_Builtin_Linker LINKER_DSG = new DSG();
	private static ILinker_Builtin_Linker LINKER_DSS = new DSS();
	private static ILinker_Builtin_Linker LINKER_EDC = new EDC();
	private static ILinker_Builtin_Linker LINKER_DFDNB = new DFDNB();
	private static ILinker_Builtin_Linker LINKER_SULFO_SMCC = new SulfoSMCC();
	private static ILinker_Builtin_Linker LINKER_DSSO = new DSSO();
	private static ILinker_Builtin_Linker LINKER_DSBU = new DSBU();
	private static ILinker_Builtin_Linker LINKER_TG = new Transglutaminase();
	private static ILinker_Builtin_Linker LINKER_PHOX = new PhoX();

	private static ILinker_Builtin_Linker LINKER_DSS_STY = new DSS_STY();
	private static ILinker_Builtin_Linker LINKER_BS3_STY = new BS3_STY();

	
	private static Map<String,ILinker_Builtin_Linker> linkers = new HashMap<String, ILinker_Builtin_Linker>();
	
	static {
		
		linkers.put( BUILT_IN_LINKER_BMOE, LINKER_BMOE );
		linkers.put( BUILT_IN_LINKER_BS2, LINKER_BS2 );
		linkers.put( BUILT_IN_LINKER_BS3, LINKER_BS3 );
		linkers.put( BUILT_IN_LINKER_DSG, LINKER_DSG );
		linkers.put( BUILT_IN_LINKER_DSS, LINKER_DSS );
		linkers.put( BUILT_IN_LINKER_EDC, LINKER_EDC );
		linkers.put( BUILT_IN_LINKER_DFDNB, LINKER_DFDNB );
		linkers.put( BUILT_IN_LINKER_SULFO_SMCC, LINKER_SULFO_SMCC);
		linkers.put( BUILD_IN_LINKER_DSSO, LINKER_DSSO);
		linkers.put( BUILD_IN_LINKER_DSBU, LINKER_DSBU);
		linkers.put( BUILD_IN_LINKER_PHOX, LINKER_PHOX);

		linkers.put( BUILD_IN_LINKER_TG, LINKER_TG );
		linkers.put( BUILD_IN_LINKER_transglutaminase, LINKER_TG );
		
		linkers.put( BUILT_IN_LINKER_BS3_STY, LINKER_BS3_STY );
		linkers.put( BUILT_IN_LINKER_DSS_STY, LINKER_DSS_STY );
	}
	
	/**
	 * @param linkerAbbr - Converted to lower case before looking up linker object
	 * @return - null if not found.  Not Found is valid for provided abbreviations not yet supported
	 * @throws ProxlBaseDataException 
	 */
	public static ILinker_Builtin_Linker getLinkerForAbbr( String linkerAbbr ) throws ProxlBaseDataException {
		if ( linkerAbbr == null ) {
			throw new IllegalArgumentException( "linkerAbbr is null" );
		}
		String linkerAbbrLowerCase = linkerAbbr.toLowerCase();
		ILinker_Builtin_Linker linker = linkers.get( linkerAbbrLowerCase );
		if ( linker == null ) {
			if ( log.isInfoEnabled() ) {
				String msg = "linker abbreviation '" + linkerAbbrLowerCase + "' does not match to any supported linker.  Some processing will not be provided.";
				log.info( msg );
			}
			// throw new ProxlBaseDataException( msg ); // Change to return null
		}
		return linker;
	}
	
	/**
	 * @param linkerAbbr
	 * @param linker
	 * @throws Exception
	 */
	public static void registerLinker( String linkerAbbr, ILinker_Builtin_Linker linker ) throws Exception {
		if ( linkers.containsKey(linkerAbbr)) {
			String msg = "registerLinker: linker abbreviation '" + linkerAbbr + "' already registered.";
			log.error( msg );
			throw new Exception( msg );
		}
		linkers.put( linkerAbbr, linker );
	}
	
	/**
	 * @return
	 */
	public static List<String> getLinkerAbbrList() {
		List<String> linkerAbbrList = new ArrayList<>();
		for ( Map.Entry<String,ILinker_Builtin_Linker> entry : linkers.entrySet() ) {
			linkerAbbrList.add( entry.getKey() );
		}
		return linkerAbbrList;
	}
	
}
