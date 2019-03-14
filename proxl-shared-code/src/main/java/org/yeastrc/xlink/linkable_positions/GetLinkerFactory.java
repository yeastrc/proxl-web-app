package org.yeastrc.xlink.linkable_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.exceptions.ProxlBaseDataException;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.*;

public class GetLinkerFactory {
	
	private static final Logger log = Logger.getLogger(GetLinkerFactory.class);
	
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

	public static final String BUILD_IN_LINKER_TG = "tg";
	public static final String BUILD_IN_LINKER_transglutaminase = "transglutaminase";
	
	public static final String BUILT_IN_LINKER_BS3_STY = "bs3.sty";
	public static final String BUILT_IN_LINKER_DSS_STY = "dss.sty";

	//   WARNING:  ALL linker abbreviations MUST be Lower Case.  This is because in the importer the incoming linker is converted to lower case
	
	

	private static ILinker LINKER_BMOE = new BMOE();
	private static ILinker LINKER_BS2 = new BS2();
	private static ILinker LINKER_BS3 = new BS3();
	private static ILinker LINKER_DSG = new DSG();
	private static ILinker LINKER_DSS = new DSS();
	private static ILinker LINKER_EDC = new EDC();
	private static ILinker LINKER_DFDNB = new DFDNB();
	private static ILinker LINKER_SULFO_SMCC = new SulfoSMCC();
	private static ILinker LINKER_DSSO = new DSSO();
	private static ILinker LINKER_DSBU = new DSBU();
	private static ILinker LINKER_TG = new Transglutaminase();

	private static ILinker LINKER_DSS_STY = new DSS_STY();
	private static ILinker LINKER_BS3_STY = new BS3_STY();

	
	private static Map<String,ILinker> linkers = new HashMap<String, ILinker>();
	
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
	public static ILinker getLinkerForAbbr( String linkerAbbr ) throws ProxlBaseDataException {
		if ( linkerAbbr == null ) {
			throw new IllegalArgumentException( "linkerAbbr is null" );
		}
		String linkerAbbrLowerCase = linkerAbbr.toLowerCase();
		ILinker linker = linkers.get( linkerAbbrLowerCase );
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
	public static void registerLinker( String linkerAbbr, ILinker linker ) throws Exception {
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
		for ( Map.Entry<String,ILinker> entry : linkers.entrySet() ) {
			linkerAbbrList.add( entry.getKey() );
		}
		return linkerAbbrList;
	}
	
}
