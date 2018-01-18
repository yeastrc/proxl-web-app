package org.yeastrc.xlink.www.web_utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proteomics.mass.MassUtils;
import org.yeastrc.proteomics.peptide.atom.AtomUtils;
import org.yeastrc.proteomics.peptide.isotope_label.LabelFactory;
import org.yeastrc.proteomics.peptide.peptide.Peptide;
import org.yeastrc.xlink.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.dto.StaticModDTO;

public class PSMMassCalculator {

	/**
	 * Get what the m/z would be for applying the supplied charge to the given neutral mass
	 * @param mass
	 * @param charge
	 * @return
	 * @throws Exception
	 */
	private static double getMZ( double mass, int charge ) throws Exception {
		
		// add in the mass of the protons (charge == number of proteins)
		mass += charge * AtomUtils.getAtom( "p" ).getMass( MassUtils.MASS_TYPE_MONOISOTOPIC );

		// divide by the charge
		mass /= charge;

		return mass;	
		
	}
	
	/**
	 * Calculate the error in PPM for the supplied observed m/z given the supplied calculated m/z
	 * @param obsMZ Observed m/z
	 * @param calcMZ Calculated m/z
	 * @return
	 */
	private static double getPPMError( double obsMZ, double calcMZ ) {
		return ( obsMZ - calcMZ ) / calcMZ * 1000000;
	}
	
	
	public static double calculatePPMEstimateForPSM( PSMMassCalculatorParams params ) throws Exception {
		
		if( params.getCharge() == null )
			throw new Exception( "charge cannot be null." );
		
		if( params.getPrecursorMZ() == null )
			throw new Exception( "precursorMZ cannot be null." );
			
		if( params.getPeptide1() == null )
			throw new Exception( "peptide1 cannot be null." );
		
		
		double mass = calculateTheoreticalNeutralMassForPSM( params );

		double precusorMZ = params.getPrecursorMZ();
		double calcMZ = getMZ( mass, params.getCharge() );
		int charge = params.getCharge();
		
		double neutronMass = AtomUtils.getAtom( "n" ).getMass( MassUtils.MASS_TYPE_MONOISOTOPIC );
		
		if( calcMZ > precusorMZ ) {
			for( int i = 1; i < 21; i++ ) {
				double tmass = mass - i * neutronMass;
				double tmz = getMZ( tmass, charge );
					
				if( Math.abs( precusorMZ - tmz ) < Math.abs( precusorMZ - calcMZ ) ) {
					
					/*
					System.out.println( "Found better calcMZ:" );
					System.out.println( "\tOld: pre: " + preMZ + ", calcMZ: " + calcMZ );
					System.out.println( "\tNew: pre: " + preMZ + ", calcMZ: " + tmz );
					*/
					
					calcMZ = tmz;
				} else {					
					break;
				}
			}
		} else {
			for( int i = 1; i < 21; i++ ) {
				double tmass = mass + i * neutronMass;
				double tmz = getMZ( tmass, charge );
					
				if( Math.abs( precusorMZ - tmz ) < Math.abs( precusorMZ - calcMZ ) ) {
					
					/*
					System.out.println( "Found better calcMZ:" );
					System.out.println( "\tOld: pre: " + preMZ + ", calcMZ: " + calcMZ );
					System.out.println( "\tNew: pre: " + preMZ + ", calcMZ: " + tmz );
					*/
					
					calcMZ = tmz;
				} else {					
					break;
				}
			}
		}

		return getPPMError( precusorMZ, calcMZ );	
	}
	
	/**
	 * Calculate the theoretical neutral mass for the peptide ion associated with a particular PSM. For cross-links
	 * this will be both peptides linked together with a cross-linker.
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static double calculateTheoreticalNeutralMassForPSM( PSMMassCalculatorParams params ) throws Exception {
		
		double mass = 0.0;
		
		if( params.getPeptide1() == null )
			throw new Exception( "peptide1 cannot be null" );
		
		Peptide peptide = new Peptide( params.getPeptide1().getSequence() );
		
		if( params.getLabel1() != null ) {
		
			//System.out.println( peptide.getSequence() );
			//System.out.println( "\t" + peptide.getMass( MassUtils.MASS_TYPE_MONOISOTOPIC ) + " (pre label)");		
		
			peptide.setLabel( LabelFactory.getInstance().getLabel( params.getLabel1().getName() ) );
			//System.out.println( "\t" + peptide.getMass( MassUtils.MASS_TYPE_MONOISOTOPIC ) + " (post label)" );

		}
		
		mass += getTotalMassWithMods( peptide, params.getStaticMods(), params.getDynamicMods1() );
		
		if( params.getPeptide2() != null ) {
			
			peptide = new Peptide( params.getPeptide2().getSequence() );
			
			if( params.getLabel2() != null )
				peptide.setLabel( LabelFactory.getInstance().getLabel( params.getLabel2().getName() ) );
			
			mass += getTotalMassWithMods( peptide, params.getStaticMods(), params.getDynamicMods2() );
		}
		
		if( params.getLinkerMass() != null ) {
			mass += params.getLinkerMass();
		}
		
		return mass;
	}
	
	/**
	 * Get the mass of the peptide, including the static mods and dynamic mods.
	 * 
	 * @param peptide The YRC proteomics utils peptide, which should have the label set (if any)
	 * @param staticMods The static mods in the experiment
	 * @param dynamicMods The dynamic mods found for this peptide for this PSM
	 * @return
	 * @throws Exception
	 */
	private static double getTotalMassWithMods( Peptide peptide, List<StaticModDTO> staticMods, List<SrchRepPeptPeptDynamicModDTO> dynamicMods ) throws Exception {
		double mass = peptide.getMass( MassUtils.MASS_TYPE_MONOISOTOPIC );
		
		// add in the dynamic mods
		if( dynamicMods != null && dynamicMods.size() > 0 ) {
			for( SrchRepPeptPeptDynamicModDTO mod : dynamicMods ) {
				mass += mod.getMass();
			}
		}
		
		// add in the static mods
		if( staticMods != null && staticMods.size() > 0 ) {
			for( StaticModDTO staticMod : staticMods ) {
				double staticMass = staticMod.getMass().doubleValue();
				int count = getNumberOfTimesResidueOccurs( staticMod.getResidue(), peptide.getSequence() );
				
				mass += staticMass * (double)count;
			}
		}
		
		return mass;
		
	}
	
	private static int getNumberOfTimesResidueOccurs( String residue, String sequence ) {
		return StringUtils.countMatches( sequence,  residue );
	}
}
