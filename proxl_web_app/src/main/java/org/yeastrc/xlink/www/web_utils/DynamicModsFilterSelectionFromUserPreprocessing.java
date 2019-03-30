package org.yeastrc.xlink.www.web_utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.xlink.www.constants.DynamicModificationsSelectionConstants;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.searcher.SearchModMassDistinctSearcher;

/**
 * For Selection of Dynamic Mods Filter, extract info
 *
 */
public class DynamicModsFilterSelectionFromUserPreprocessing {

	private static final Logger log = LoggerFactory.getLogger( DynamicModsFilterSelectionFromUserPreprocessing.class);
	private DynamicModsFilterSelectionFromUserPreprocessing() { }
	public static DynamicModsFilterSelectionFromUserPreprocessing getInstance() { return new DynamicModsFilterSelectionFromUserPreprocessing(); }
	

	/**
	 * If @param modMassSelections is null, fills in all possible values for search ids
	 * 
	 * @param modMassSelections
	 * @param searchIdsDeduppedSorted
	 * @return
	 * @throws Exception 
	 */
	public DynamicModsFilterSelectionFromUserPreprocessingResult getDynamicModsFilterHandleAllSelected( 
			String[] modMassSelections,
			Collection<Integer> searchIdsDeduppedSorted ) throws Exception {
		
		DynamicModsFilterSelectionFromUserPreprocessingResult result =
				getDynamicModsFilterAsDoubleResult( modMassSelections );
		
		if ( modMassSelections == null ) {
			//  All options are selected so get all possible mod masses and put in output lists
			int[] searchIds = new int[ searchIdsDeduppedSorted.size() ];
			int index = 0;
			for ( int searchId : searchIdsDeduppedSorted ) {
				searchIds[ index ] = searchId;
				index++;
			}
			List<Double> modMassDistinctForSearchesList = SearchModMassDistinctSearcher.getInstance().getDistinctDynamicModMassesForSearchId( searchIds );
			List<String> modMassStringsList = new ArrayList<>( modMassDistinctForSearchesList.size() );
			for ( Double modMass : modMassDistinctForSearchesList ) {
				String modMassAsString = modMass.toString();
				modMassStringsList.add( modMassAsString );
			}
			result.setModMassSelectionsIncludesNoModifications(true);
			result.modMassSelectionsWithoutNoModsDouble = modMassDistinctForSearchesList;
			result.modMassSelectionsWithoutNoMods = modMassStringsList;
		}
		return result;
	}
	
	/**
	 * @param modMassSelections
	 * @return
	 * @throws ProxlWebappDataException 
	 */
	public DynamicModsFilterSelectionFromUserPreprocessingResult getDynamicModsFilterAsDoubleResult( String[] modMassSelections ) throws ProxlWebappDataException {
		DynamicModsFilterSelectionFromUserPreprocessingResult result =
				getDynamicModsFilterResult( modMassSelections );
		
		if ( result.getModMassSelectionsWithoutNoMods() != null ) {
			List<Double> modMassSelectionsWithoutNoModsDouble = new ArrayList<>(  result.getModMassSelectionsWithoutNoMods().size() );
			result.setModMassSelectionsWithoutNoModsDouble( modMassSelectionsWithoutNoModsDouble );
			for ( String modMass : result.getModMassSelectionsWithoutNoMods() ) {
				try {
					Double modMassDouble = new Double( modMass );
					modMassSelectionsWithoutNoModsDouble.add( modMassDouble );
				} catch ( Exception e ) {
					String msg = "Mod Mass in User Selection does not parse to Double: '" + modMass + "'";
					log.error( msg, e );
					throw new ProxlWebappDataException(msg);
				}
			}
			Collections.sort( modMassSelectionsWithoutNoModsDouble );
		}
		return result;
	}
	
	/**
	 * @param modMassSelections
	 * @return
	 */
	public DynamicModsFilterSelectionFromUserPreprocessingResult getDynamicModsFilterResult( String[] modMassSelections ) {
		
		DynamicModsFilterSelectionFromUserPreprocessingResult result = new DynamicModsFilterSelectionFromUserPreprocessingResult();
		if ( modMassSelections == null ) {
			return result; // EARLY EXIT
		}
		if ( modMassSelections != null ) {
			for ( String modMassSelection : modMassSelections ) {
				if ( DynamicModificationsSelectionConstants.NO_DYNAMIC_MODIFICATIONS_SELECTION_ITEM.equals( modMassSelection ) ) {
					result.modMassSelectionsIncludesNoModifications = true;
				} else {
					result.modMassSelectionsIncludesYesModifications = true;
					if ( result.modMassSelectionsWithoutNoMods == null ) {
						result.modMassSelectionsWithoutNoMods = new ArrayList<>( modMassSelections.length );
					}
					result.modMassSelectionsWithoutNoMods.add( modMassSelection );
				}
			}
		}
		return result;
	}
	

	/**
	 * Result
	 *
	 */
	public static class DynamicModsFilterSelectionFromUserPreprocessingResult {
		boolean modMassSelectionsIncludesNoModifications = false;
		boolean modMassSelectionsIncludesYesModifications = false;
		List<String> modMassSelectionsWithoutNoMods = null;
		List<Double> modMassSelectionsWithoutNoModsDouble = null;
		
		public boolean isModMassSelectionsIncludesNoModifications() {
			return modMassSelectionsIncludesNoModifications;
		}
		public void setModMassSelectionsIncludesNoModifications(boolean modMassSelectionsIncludesNoModifications) {
			this.modMassSelectionsIncludesNoModifications = modMassSelectionsIncludesNoModifications;
		}
		public boolean isModMassSelectionsIncludesYesModifications() {
			return modMassSelectionsIncludesYesModifications;
		}
		public void setModMassSelectionsIncludesYesModifications(boolean modMassSelectionsIncludesYesModifications) {
			this.modMassSelectionsIncludesYesModifications = modMassSelectionsIncludesYesModifications;
		}
		public List<String> getModMassSelectionsWithoutNoMods() {
			return modMassSelectionsWithoutNoMods;
		}
		public void setModMassSelectionsWithoutNoMods(List<String> modMassSelectionsWithoutNoMods) {
			this.modMassSelectionsWithoutNoMods = modMassSelectionsWithoutNoMods;
		}
		public List<Double> getModMassSelectionsWithoutNoModsDouble() {
			return modMassSelectionsWithoutNoModsDouble;
		}
		public void setModMassSelectionsWithoutNoModsDouble(List<Double> modMassSelectionsWithoutNoModsDouble) {
			this.modMassSelectionsWithoutNoModsDouble = modMassSelectionsWithoutNoModsDouble;
		}
	}
	
}
