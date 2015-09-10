package org.yeastrc.xlink.utils;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.xlink.dao.LinkerDAO;
import org.yeastrc.xlink.dto.LinkerDTO;


/**
 * Is the Dynamic Mod a Mono-link
 *
 */
public class IsDynamicModMassAMonolink {

	
	/**
	 * Multiply mass by 10 to power of LinkerMassComparsonConstants.NUMBER_OF_DECIMAL_POSITIONS_TO_ROUND_TO
	 */
//	private static final double scaleMultiplier =  Math.pow( 10, LinkerMassComparsonConstants.NUMBER_OF_DECIMAL_POSITIONS_TO_ROUND_TO );
	
	private static IsDynamicModMassAMonolink _instance;
	
	/**
	 * linker table entry for the linkerAbbreviation ('abbr') passed in  
	 */
	private LinkerDTO linkerDTO;
	
	public LinkerDTO getLinkerDTO() {
		return linkerDTO;
	}


	/**
	 * list of masses in linker_monolink_mass table for the linkerAbbreviation ('abbr') passed in  
	 */
	private List<Double> monoLinkMasses = new ArrayList<Double>();
	
	
	// private constructor
	private IsDynamicModMassAMonolink(){}
	

	
	/**
	 * Initialize the values
	 * @throws Exception 
	 */
	public static void init( String linkerAbbreviation ) throws Exception {

		_instance = new IsDynamicModMassAMonolink();
		
		LinkerDTO linkerDTOlocal = LinkerDAO.getInstance().getLinkerDTOForAbbr( linkerAbbreviation );
		
		if ( linkerDTOlocal == null ) {
			
			String msg = "No 'linker' record found for 'abbr': " + linkerAbbreviation;
			throw new Exception( msg );
		}
		

		_instance.linkerDTO = linkerDTOlocal;
		
		List<Double> monoLinkMassesTemp = LoadMonoLinkMasses.loadMonoLinkMassesForlinkerId( linkerDTOlocal.getId() );
		
		//  Now scale and round the masses so they are ready for comparison 
		
		List<Double> monoLinkMasses = new ArrayList<Double>( monoLinkMassesTemp.size() );
		
		for ( double mass : monoLinkMassesTemp ) {
		
//			double scaledRoundedMass = scaleUpMassAndRoundForComparison( mass );
//					
//			monoLinkMasses.add( scaledRoundedMass );
			
			monoLinkMasses.add( mass );
		}
		
		// store the scaled and rounded masses
		
		_instance.monoLinkMasses = monoLinkMasses;
	}
	
	
	public static IsDynamicModMassAMonolink getInstance() {

		if ( _instance == null ) {
			
			throw new IllegalStateException( "Not initialized, call static init(...) first" );
		}
		
		return _instance;
	}
	
	
	/**
	 * @param pseq
	 * @return
	 * @throws Exception
	 */
	public boolean isDynamicModMassAMonolink( double dynamicModMass ) throws Exception  {
		
//		double scaledRoundedMass = scaleUpMassAndRoundForComparison( dynamicModMass );
//
//		for ( double monoLinkMass : monoLinkMasses ) {
//
//			if ( scaledRoundedMass == monoLinkMass ) {
//
//				return true;  //  EARLY EXIT
//			}
//		}
		
		for ( double monoLinkMass : monoLinkMasses ) {

			if ( dynamicModMass == monoLinkMass ) {

				return true;  //  EARLY EXIT
			}
		}
		
		return false;
	}
	
	
	/**
	 * Multiply mass by 10 to power of LinkerMassComparsonConstants.NUMBER_OF_DECIMAL_POSITIONS_TO_ROUND_TO
	 * then round for comparisons
	 * 
	 * @param mass
	 * @return
	 */
//	private static double scaleUpMassAndRoundForComparison( double mass ) {
//		
//		double tmpMass = mass * scaleMultiplier;
//		
//		double outMass = Math.round( tmpMass );
//		
//		return outMass;
//	}
	
}
