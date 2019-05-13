

ALTER TABLE annotation_type 
CHANGE COLUMN psm_peptide_type psm_peptide_type ENUM('psm', 'peptide', 'psm_per_peptide') NOT NULL COMMENT '\'peptide\' is actually reported peptide';

CREATE TABLE psm_per_peptide_annotation (
  id int(10) unsigned NOT NULL AUTO_INCREMENT,
  psm_id int(10) unsigned NOT NULL,
  srch_rep_pept__peptide_id int(10) unsigned NOT NULL,
  filterable_descriptive_type enum('filterable','descriptive') COLLATE utf8_bin NOT NULL,
  annotation_type_id int(10) unsigned NOT NULL,
  value_location enum('local','large_value_table') COLLATE utf8_bin NOT NULL,
  value_double double NOT NULL,
  value_string varchar(50) COLLATE utf8_bin NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY psm_per_p_a_psm_id_srch_rep__ann_typ_id_idx (psm_id,srch_rep_pept__peptide_id,annotation_type_id),
  KEY psm_per_peptide_annotation__psm_id_fk_idx (psm_id),
  KEY fk_psm_peptide_annotation_1_idx (srch_rep_pept__peptide_id),
  CONSTRAINT psm_per_peptide_annotation__fk_2 FOREIGN KEY (srch_rep_pept__peptide_id) REFERENCES srch_rep_pept__peptide (id) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT psm_per_peptide_annotation__psm_id_fk FOREIGN KEY (psm_id) REFERENCES psm (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


CREATE TABLE psm_per_peptide_annotation_large_value (
  psm_per_peptide_annotation_id int(10) unsigned NOT NULL,
  value_string longtext COLLATE utf8_bin NOT NULL,
  KEY psm_per_peptide_annotation_large_value_fk1_idx (psm_per_peptide_annotation_id),
  CONSTRAINT psm_per_peptide_annotation_large_value_fk1 FOREIGN KEY (psm_per_peptide_annotation_id) REFERENCES psm_per_peptide_annotation (id) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

