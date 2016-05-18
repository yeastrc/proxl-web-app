


use proxl;


CREATE TABLE IF NOT EXISTS cutoffs_applied_on_import (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  annotation_type_id INT UNSIGNED NOT NULL,
  cutoff_value_string VARCHAR(255) NOT NULL,
  cutoff_value_double DOUBLE NOT NULL,
  PRIMARY KEY (id),
  INDEX cutoffs_at_import_ann_type_id_fk_idx (annotation_type_id ASC),
  INDEX cutoffs_applied_on_import_search_id_idx (search_id ASC),
  CONSTRAINT cutoffs_applied_on_import_ann_type_id_fk
    FOREIGN KEY (annotation_type_id)
    REFERENCES annotation_type (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB



--  drop foreign key where ther is an alt path to same foreign key





--  drop search id foreign key where ther is an alt path to search id




ALTER TABLE annotation_type 
DROP FOREIGN KEY ann_tp_srch_id_fk;

ALTER TABLE srch__rep_pept__annotation 
DROP FOREIGN KEY srch__rep_pept__search_id_fk;


ALTER TABLE search_crosslink_best_peptide_value_generic_lookup 
DROP FOREIGN KEY search_crosslink_best_peptide_value_generic_lookup_ibfk_100;

ALTER TABLE search_crosslink_best_psm_value_generic_lookup 
DROP FOREIGN KEY search_crosslink_best_psm_value_generic_lookup_ibfk_10;

ALTER TABLE search_dimer_best_peptide_value_generic_lookup 
DROP FOREIGN KEY search_dimer_best_peptide_value_generic_lookup_ibfk_1000;

ALTER TABLE search_dimer_best_psm_value_generic_lookup 
DROP FOREIGN KEY search_dimer_best_psm_value_generic_lookup_ibfk_100;

ALTER TABLE search_dimer_best_psm_value_generic_lookup 
DROP FOREIGN KEY search_dimer_best_psm_value_generic_lookup_ibfk_100;

ALTER TABLE search_looplink_best_peptide_value_generic_lookup 
DROP FOREIGN KEY search_looplink_best_peptide_value_generic_lookup_ibfk_1000;

ALTER TABLE search_looplink_best_psm_value_generic_lookup 
DROP FOREIGN KEY search_looplink_best_psm_value_generic_lookup_ibfk_100;

ALTER TABLE search_monolink_best_peptide_value_generic_lookup 
DROP FOREIGN KEY search_looplink_best_peptide_value_generic_monoup_ibfk_10000;

ALTER TABLE search_monolink_best_psm_value_generic_lookup 
DROP FOREIGN KEY search_monolink_best_psm_value_generic_lookup_ibfk_1000;

ALTER TABLE search_unlinked_best_peptide_value_generic_lookup 
DROP FOREIGN KEY search_unlinked_best_peptide_value_generic_lookup_ibfk;

ALTER TABLE search_unlinked_best_psm_value_generic_lookup 
DROP FOREIGN KEY search_unlinked_best_psm_value_generic_lookup_ibfk_1000;





--  drop other than  psm id foreign key where ther is an alt path to psm id

ALTER TABLE psm_annotation 
DROP FOREIGN KEY psm_filterable_ann__ann_type_id_fk;

ALTER TABLE crosslink   -- drop matched peptide id
DROP FOREIGN KEY crosslink_ibfk_5,
DROP FOREIGN KEY crosslink_ibfk_4;


--  drop  psm id foreign key where ther is an alt path to psm id


ALTER TABLE psm_filterable_annotation__generic_lookup 
DROP FOREIGN KEY psm_filterable_annotation__generic_lookup__psm_id_fk;


-- Remove  Other Unneeded indexes


ALTER TABLE psm_annotation
DROP INDEX psm_annotation_psm_id_ann_typ_f_d_idx ,
DROP INDEX psm_ann__ann_type_id_fk_idx ,
DROP INDEX psm_annotation__psm_id_fk_idx ;


