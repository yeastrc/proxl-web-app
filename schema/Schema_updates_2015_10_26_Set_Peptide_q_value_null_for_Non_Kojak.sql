
--  Changes to peptide q value column to NULL for non-Kojak searches

use proxl;


UPDATE search_reported_peptide SET q_value = NULL 
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 
	
	
UPDATE percolator_search_reported_peptide  SET q_value = NULL
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 
	
UPDATE search_crosslink_lookup SET bestPeptideQValue = null 
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 

UPDATE search_looplink_lookup SET bestPeptideQValue = null 
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 

UPDATE search_monolink_lookup SET bestPeptideQValue = null 
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 

UPDATE search_protein_lookup 
  SET bestCrosslinkPeptideQValue = NULL, bestLooplinkPeptideQValue = NULL, bestMonolinkPeptideQValue = NULL, 
  	bestDimerPeptideQValue = NULL, bestUnlinkedPeptideQValue = NULL 
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 

-- -------

--  For recently added tables:

UPDATE unified_rep_pep__reported_peptide__search_lookup 
  SET peptide_q_value_for_search = NULL 
WHERE search_id IN
(SELECT id FROM search WHERE search_program = 'xquest' OR search_program = 'search_for_xlinks'); 

