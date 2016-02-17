package org.yeastrc.xlink.www.searcher;

import java.util.Collection;

import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.searcher_psm_peptide_cutoff_objects.SearcherCutoffValuesRootLevel;
import org.yeastrc.xlink.www.objects.MergedSearchProtein;
import org.yeastrc.xlink.www.objects.ProteinSequenceCoverage;

public class MergedSearchProteinSequenceCoverageSearcher {

	private MergedSearchProteinSequenceCoverageSearcher() { }
	private static final MergedSearchProteinSequenceCoverageSearcher _INSTANCE = new MergedSearchProteinSequenceCoverageSearcher();
	public static MergedSearchProteinSequenceCoverageSearcher getInstance() { return _INSTANCE; }
	
	public ProteinSequenceCoverage getProteinSequenceCoverage( MergedSearchProtein protein, SearcherCutoffValuesRootLevel searcherCutoffValuesRootLevel ) throws Exception {
		
		ProteinSequenceCoverage psc = new ProteinSequenceCoverage( protein.getNrProtein() );
		
		Collection<PeptideDTO> peptides = MergedSearchPeptideSearcher.getInstance().getPeptides(protein.getNrProtein(), protein.getSearchs(), searcherCutoffValuesRootLevel);
		for( PeptideDTO peptide : peptides ) {
			psc.addPeptide( peptide.getSequence() );
		}
		
		return psc;
	}
}
