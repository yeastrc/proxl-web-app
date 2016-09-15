package org.yeastrc.xlink.linkable_positions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.linkable_positions.GetLinkerFactory;
import org.yeastrc.xlink.linkable_positions.linkers.BS2;
import org.yeastrc.xlink.linkable_positions.linkers.BS3;
import org.yeastrc.xlink.linkable_positions.linkers.DSG;
import org.yeastrc.xlink.linkable_positions.linkers.DSS;
import org.yeastrc.xlink.linkable_positions.linkers.EDC;
import org.yeastrc.xlink.linkable_positions.linkers.DFDNB;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;

public class GetLinkerFactory {
	
	private static final Logger log = Logger.getLogger(GetLinkerFactory.class);
	
	public static final String BUILT_IN_LINKER_BS2 = "bs2";
	public static final String BUILT_IN_LINKER_BS3 = "bs3";
	public static final String BUILT_IN_LINKER_DSG = "dsg";
	public static final String BUILT_IN_LINKER_DSS = "dss";
	public static final String BUILT_IN_LINKER_EDC = "edc";
	public static final String BUILT_IN_LINKER_DFDNB = "dfdnb";

	private static ILinker LINKER_BS2 = new BS2();
	private static ILinker LINKER_BS3 = new BS3();
	private static ILinker LINKER_DSG = new DSG();
	private static ILinker LINKER_DSS = new DSS();
	private static ILinker LINKER_EDC = new EDC();
	private static ILinker LINKER_DFDNB = new DFDNB();
	
	private static Map<String,ILinker> linkers = new HashMap<String, ILinker>();
	
	static {
		
		linkers.put( BUILT_IN_LINKER_BS2, LINKER_BS2 );
		linkers.put( BUILT_IN_LINKER_BS3, LINKER_BS3 );
		linkers.put( BUILT_IN_LINKER_DSG, LINKER_DSG );
		linkers.put( BUILT_IN_LINKER_DSS, LINKER_DSS );
		linkers.put( BUILT_IN_LINKER_EDC, LINKER_EDC );
		linkers.put( BUILT_IN_LINKER_DFDNB, LINKER_DFDNB );
	}
	
	/**
	 * @param linkerAbbr
	 * @return
	 * @throws Exception 
	 */
	public static ILinker getLinkerForAbbr( String linkerAbbr ) throws Exception {
		
		ILinker linker = linkers.get( linkerAbbr );

		if ( linker == null ) {

			String msg = "linker abbreviation '" + linkerAbbr + "' does not match to any supported linker.";
			
			log.error( msg );
			
			throw new Exception( msg );
		}
		
		return linker;
		
	}
	
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
	
//	/**
//	 * @param linkerAbbrSet
//	 * @return
//	 */
//	public static Collection<IGetLinkablePositions> getLinkablePositionsCodeObjectForLinkerAbbr( Set<String> linkerAbbrSet ) {
//		
//		Collection<IGetLinkablePositions> getLinkablePositionsCodeObjects = new ArrayList<>();
//		
//		for ( String linkerAbbr : linkerAbbrSet ) {
//
//			if ( "dss".equals( linkerAbbr ) ) {
//
//				IGetLinkablePositions getLinkablePositions = new GetLinkablePositionsLinkerDSS(); 
//
//				getLinkablePositionsCodeObjects.add( getLinkablePositions );
//			}
//		}
//		
//		return getLinkablePositionsCodeObjects;
//	}
}
