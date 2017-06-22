package org.yeastrc.xlink.utils;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.yeastrc.proteomics.mass.MassUtils;
import org.yeastrc.proteomics.peptide.atom.AtomUtils;
import org.yeastrc.proteomics.peptide.peptide.Peptide;
import org.yeastrc.xlink.dao.StaticModDAO;
import org.yeastrc.xlink.dto.StaticModDTO;
import org.yeastrc.xlink.www.dao.PeptideDAO;
import org.yeastrc.xlink.www.dto.PeptideDTO;
import org.yeastrc.xlink.www.dto.PsmDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.xlink.www.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.xlink.www.searcher.SearchReportedPeptideLinkTypeSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptDynamicModSearcher;
import org.yeastrc.xlink.www.searcher.SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher;

public class PSMMassCalculator {

	public static double calculateMZForPSM( PeptideDTO peptide1,
			PeptideDTO peptide2,
			List<StaticModDTO> staticMods,
			List<SrchRepPeptPeptDynamicModDTO> dynamicMods1,
			List<SrchRepPeptPeptDynamicModDTO> dynamicMods2,
			Integer charge,
			Double linkerMass ) throws Exception {
		

		if( charge == null )
			throw new Exception( "charge cannot be null." );
		
		double mass = calculateNeutralMassForPSM( peptide1, peptide2, staticMods, dynamicMods1, dynamicMods2, linkerMass );
		
		// add in the mass of the protons (charge == number of proteins)
		mass += charge * AtomUtils.getAtom( 'p' ).getMass( MassUtils.MASS_TYPE_MONOISOTOPIC );

		// divide by the charge
		mass /= charge;

		return mass;	
	}
	
	
	public static double calculateNeutralMassForPSM( PeptideDTO peptide1,
											PeptideDTO peptide2,
											List<StaticModDTO> staticMods,
											List<SrchRepPeptPeptDynamicModDTO> dynamicMods1,
											List<SrchRepPeptPeptDynamicModDTO> dynamicMods2,
											Double linkerMass ) throws Exception {
		
		double mass = 0.0;
		
		if( peptide1 == null )
			throw new Exception( "peptide1 cannot be null" );
		
		Peptide peptide = new Peptide( peptide1.getSequence() );
		mass += getTotalMass( peptide, staticMods, dynamicMods1 );
		
		if( peptide2 != null ) {
			
			peptide = new Peptide( peptide2.getSequence() );
			mass += getTotalMass( peptide, staticMods, dynamicMods2 );
		}
		
		if( linkerMass != null ) {
			mass += linkerMass;
		}
		
		return mass;
	}
	
	public static double calculateMZForPSM( PsmDTO psm ) throws Exception {
		
		double mass = calculateNeutralMassForPSM( psm );
		
		// add in the mass of the protons (charge == number of proteins)
		mass += (double)psm.getCharge() * AtomUtils.getAtom( 'p' ).getMass( MassUtils.MASS_TYPE_MONOISOTOPIC );

		// divide by the charge
		mass /= (double)psm.getCharge();

		return mass;
	}
	
	
	public static double calculateNeutralMassForPSM( PsmDTO psm ) throws Exception {
		
		// our static mods
		List<StaticModDTO> staticMods = StaticModDAO.getInstance().getStaticModDTOForSearchId( psm.getSearchId() );

		// our search reported peptide peptide(s)
		List<SrchRepPeptPeptideDTO> searchReportedPeptidePeptides = SrchRepPeptPeptideOnSearchIdRepPeptIdSearcher.getInstance().getForSearchIdReportedPeptideId( psm.getSearchId(), psm.getReportedPeptideId() );
		
		int linkType = SearchReportedPeptideLinkTypeSearcher.getInstance().getSearchReportedPeptideLinkTypeNumber( psm.getSearchId(), psm.getReportedPeptideId() );
		
		if( linkType == XLinkUtils.TYPE_UNLINKED ) {
			
			SrchRepPeptPeptideDTO searchReportedPeptidePeptide = searchReportedPeptidePeptides.get( 0 );
			
			// our dynamic mods for this peptide
			List<SrchRepPeptPeptDynamicModDTO> dynamicMods = SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( searchReportedPeptidePeptide.getId() );			
			
			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide.getPeptideId() );
			
			// get the mass of the base peptide
			Peptide peptide = new Peptide( peptideDTO.getSequence() );
			double mass = getTotalMass( peptide, staticMods, dynamicMods );
			
			return mass;	
		}
		
		
		if( linkType == XLinkUtils.TYPE_DIMER ) {
			
			double mass = 0.0;
			
			for( SrchRepPeptPeptideDTO searchReportedPeptidePeptide : searchReportedPeptidePeptides ) {
				
				// our dynamic mods for this peptide
				List<SrchRepPeptPeptDynamicModDTO> dynamicMods = SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( searchReportedPeptidePeptide.getId() );			
				
				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide.getPeptideId() );
				
				// get the mass of the base peptide
				Peptide peptide = new Peptide( peptideDTO.getSequence() );
				mass += getTotalMass( peptide, staticMods, dynamicMods );				
			}
			
			return mass;
		}
		
		
		if( linkType == XLinkUtils.TYPE_LOOPLINK ) {
			
			SrchRepPeptPeptideDTO searchReportedPeptidePeptide = searchReportedPeptidePeptides.get( 0 );
			
			// our dynamic mods for this peptide
			List<SrchRepPeptPeptDynamicModDTO> dynamicMods = SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( searchReportedPeptidePeptide.getId() );			
			
			PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide.getPeptideId() );
			
			// get the mass of the base peptide
			Peptide peptide = new Peptide( peptideDTO.getSequence() );
			double mass = getTotalMass( peptide, staticMods, dynamicMods );
			
			// add in mass of cross-linker
			mass += psm.getLinkerMass().doubleValue();			
			
			return mass;
		}
		
		
		if( linkType == XLinkUtils.TYPE_CROSSLINK ) {
			
			double mass = 0.0;
			
			for( SrchRepPeptPeptideDTO searchReportedPeptidePeptide : searchReportedPeptidePeptides ) {
				
				// our dynamic mods for this peptide
				List<SrchRepPeptPeptDynamicModDTO> dynamicMods = SrchRepPeptPeptDynamicModSearcher.getInstance().getSrchRepPeptPeptDynamicModForSrchRepPeptPeptideId( searchReportedPeptidePeptide.getId() );			
				
				PeptideDTO peptideDTO = PeptideDAO.getInstance().getPeptideDTOFromDatabase( searchReportedPeptidePeptide.getPeptideId() );
				
				// get the mass of the base peptide
				Peptide peptide = new Peptide( peptideDTO.getSequence() );
				mass += getTotalMass( peptide, staticMods, dynamicMods );				
			}
			
			// add in mass of cross-linker
			mass += psm.getLinkerMass().doubleValue();
			
			return mass;
			
		}
		
		
		// should not get here.
		throw new Exception( "Unknown PSM type." );
		
		
	}
	
	
	/**
	 * Get the mass of the peptide, given the static mods and dynamic mods.
	 * 
	 * @param peptide
	 * @param staticMods
	 * @param dynamicMods
	 * @return
	 * @throws Exception
	 */
	private static double getTotalMass( Peptide peptide, List<StaticModDTO> staticMods, List<SrchRepPeptPeptDynamicModDTO> dynamicMods ) throws Exception {
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
