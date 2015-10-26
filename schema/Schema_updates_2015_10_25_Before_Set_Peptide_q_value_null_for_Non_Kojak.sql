
--  Changes to peptide q value column to allow null values

use proxl;


ALTER TABLE search_crosslink_lookup 
CHANGE COLUMN bestPeptideQValue bestPeptideQValue DOUBLE NULL DEFAULT NULL ;

ALTER TABLE search_looplink_lookup 
CHANGE COLUMN bestPeptideQValue bestPeptideQValue DOUBLE NULL DEFAULT NULL ;

ALTER TABLE search_monolink_lookup 
CHANGE COLUMN bestPeptideQValue bestPeptideQValue DOUBLE NULL DEFAULT NULL ;

ALTER TABLE search_reported_peptide 
CHANGE COLUMN q_value q_value DOUBLE NULL DEFAULT NULL ;


ALTER TABLE search_protein_lookup 
	CHANGE COLUMN bestCrosslinkPeptideQValue bestCrosslinkPeptideQValue DOUBLE NULL DEFAULT NULL,
	CHANGE COLUMN bestLooplinkPeptideQValue bestLooplinkPeptideQValue DOUBLE NULL DEFAULT NULL,
	CHANGE COLUMN bestMonolinkPeptideQValue bestMonolinkPeptideQValue DOUBLE NULL DEFAULT NULL,
	CHANGE COLUMN bestDimerPeptideQValue bestDimerPeptideQValue DOUBLE NULL DEFAULT NULL,
	CHANGE COLUMN bestUnlinkedPeptideQValue bestUnlinkedPeptideQValue DOUBLE NULL DEFAULT NULL;
