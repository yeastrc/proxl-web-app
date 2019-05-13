
--  Updates to Proxl DB for isotope labeling

CREATE TABLE  isotope_label (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  INDEX name (name ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

--  Populate isotope_label


--  Insert entries into isotope_label

--  id for 'none' is hard coded in Java in class IsotopeLabelsConstants
INSERT INTO isotope_label (id, name) VALUES ( 1, "none" );

--  The rest of records use auto increment for id
INSERT INTO isotope_label (name) VALUES ( "13C" );
INSERT INTO isotope_label (name) VALUES ( "15N" );
INSERT INTO isotope_label (name) VALUES ( "18O" ); -- (this is the letter O not zero)
INSERT INTO isotope_label (name) VALUES ( "2H" );



-- --------------------

-- Rename table protein_sequence

ALTER TABLE protein_sequence 
RENAME TO  protein_sequence_v2 ;

--  

--  Create table protein_sequence_version

CREATE TABLE  protein_sequence_version (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  protein_sequence_id INT(10) UNSIGNED NOT NULL,
  isotope_label_id INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX search_id_prot_seq_id_ann_id (protein_sequence_id ASC, isotope_label_id ASC),
  INDEX srch_prt_sqnc_annttn_prot_seq_id_idx (protein_sequence_id ASC),
  INDEX fk_protein__isotope_label_id_idx (isotope_label_id ASC),
  CONSTRAINT fk_protein_sequence_version__prot_seq_id
    FOREIGN KEY (protein_sequence_id)
    REFERENCES protein_sequence_v2 (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT fk_protein_sequence_version__isotope_label_id
    FOREIGN KEY (isotope_label_id)
    REFERENCES isotope_label (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin
COMMENT = 'This table is the FK on many other tables';

--  Populate table protein_sequence_version


INSERT INTO protein_sequence_version
SELECT 
	id, -- id
	id, -- protein_sequence_id
	1  -- isotope_label_id for NONE
  FROM protein_sequence_v2;


ALTER TABLE pdb_alignment 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL ,
DROP INDEX unique_prot_seq_id_chain_id_pdb_file_id ,
ADD UNIQUE INDEX unique_prot_seq_v_id_chain_id_pdb_file_id (protein_sequence_version_id ASC, chain_id ASC, pdb_file_id ASC);


ALTER TABLE search 
ADD COLUMN has_isotope_label TINYINT(1) NOT NULL DEFAULT 0 AFTER has_scan_data;

ALTER TABLE srch_rep_pept__prot_seq_id_pos_monolink 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL;

ALTER TABLE srch_rep_pept__prot_seq_id_pos_crosslink 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL;

ALTER TABLE srch_rep_pept__prot_seq_id_pos_looplink 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL;

ALTER TABLE srch_rep_pept__prot_seq_id_unlinked 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL;

ALTER TABLE srch_rep_pept__prot_seq_id_dimer 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL;


--  table search_protein_sequence_annotation

ALTER TABLE search_protein_sequence_annotation 
DROP FOREIGN KEY srch_prt_sqnc_annttn_prot_seq_id;

ALTER TABLE search_protein_sequence_annotation 
DROP INDEX srch_prt_sqnc_annttn_prot_seq_id_idx ;

ALTER TABLE search_protein_sequence_annotation 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL ;

ALTER TABLE search_protein_sequence_annotation 
ADD INDEX srch_prt_sqnc_annttn_prot_seq_v_id_idx (protein_sequence_version_id ASC);
ALTER TABLE search_protein_sequence_annotation 
ADD CONSTRAINT srch_prt_sqnc_annttn_prot_seq_v_id
  FOREIGN KEY (protein_sequence_version_id)
  REFERENCES protein_sequence_version (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;

ALTER TABLE search_protein_sequence_annotation 
RENAME TO  search__protein_sequence_version__annotation ;


ALTER TABLE proxl_xml_file_import_tracking_status_history 
CHANGE COLUMN status_timestamp status_timestamp TIMESTAMP NOT NULL ;

ALTER TABLE peptide_protein_position 
DROP FOREIGN KEY peptide_protein_position_search_id;

ALTER TABLE peptide_protein_position 
CHANGE COLUMN peptide_id peptide_id_info_only INT(10) UNSIGNED NOT NULL ,
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL ,
DROP INDEX search_id_protein_seq_id ,
ADD INDEX search_id_protein_seq_version_id (search_id ASC, protein_sequence_version_id ASC),
DROP INDEX peptide_protein_position_search_id_idx , 
RENAME TO  protein_coverage ;

ALTER TABLE protein_coverage 
ADD CONSTRAINT protein_coverage_search_id
  FOREIGN KEY (search_id)
  REFERENCES search (id)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;


CREATE TABLE  srch_rep_pept__peptide_isotope_label (
  srch_rep_pept__peptide_id INT(10) UNSIGNED NOT NULL,
  isotope_label_id INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (srch_rep_pept__peptide_id, isotope_label_id),
  INDEX fk_srch_rpppt_pptd_istplbl_istplbl_id_idx (isotope_label_id ASC),
  CONSTRAINT fk_srch_rpppt_pptd_istplbl_srch_rpppt_pptd_id
    FOREIGN KEY (srch_rep_pept__peptide_id)
    REFERENCES srch_rep_pept__peptide (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT fk_srch_rpppt_pptd_istplbl_istplbl_id
    FOREIGN KEY (isotope_label_id)
    REFERENCES isotope_label (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


CREATE TABLE  search__isotope_label_lookup (
  search_id INT(10) UNSIGNED NOT NULL,
  isotope_label_id INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (search_id, isotope_label_id),
  INDEX search__isotope_label_lookup__isotope_label_id_fk_idx (isotope_label_id ASC),
  CONSTRAINT search__isotope_label_lookup__search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT search__isotope_label_lookup__isotope_label_id_fk
    FOREIGN KEY (isotope_label_id)
    REFERENCES isotope_label (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

ALTER TABLE z_mapping__nrseq_prot_id__prot_seq_id 
CHANGE COLUMN protein_sequence_id protein_sequence_version_id INT(10) UNSIGNED NOT NULL , 
RENAME TO  z_mapping__nrseq_prot_id__prot_seq_version_id ;


-- ---------------------

--  Second additional part for unified reported peptide


ALTER TABLE unified_reported_peptide_lookup 
ADD COLUMN has_isotope_labels TINYINT(4) NOT NULL DEFAULT 0 AFTER has_dynamic_modifictions;

ALTER TABLE unified_rp__search__rep_pept__generic_lookup 
ADD COLUMN has_isotope_labels TINYINT(3) NOT NULL DEFAULT 0 AFTER has_monolinks;

ALTER TABLE unified_rp__search_reported_peptide_fltbl_value_generic_lookup 
ADD COLUMN has_isotope_labels TINYINT(3) NOT NULL DEFAULT 0 AFTER has_monolinks;

ALTER TABLE unified_rp__search__rep_pept__best_psm_value_generic_lookup 
ADD COLUMN has_isotope_labels TINYINT(3) NOT NULL DEFAULT 0 AFTER has_monolinks;


CREATE TABLE IF NOT EXISTS unified_rep_pep_isotope_label_lookup (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  rp_matched_peptide_id INT(10) UNSIGNED NOT NULL,
  isotope_label_id INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX unified_rp_dynamic_mod__rp_matched_peptide_id_fk_idx (rp_matched_peptide_id ASC),
  INDEX unified_rep_pep_isotope_label_lookup_isotope_label_id_fk_idx (isotope_label_id ASC),
  CONSTRAINT unified_rep_pep_isotope_label_lookup_matched_peptide_id_fk
    FOREIGN KEY (rp_matched_peptide_id)
    REFERENCES unified_rep_pep_matched_peptide_lookup (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rep_pep_isotope_label_lookup_isotope_label_id_fk
    FOREIGN KEY (isotope_label_id)
    REFERENCES isotope_label (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;


