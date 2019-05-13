

CREATE TABLE search_linker_per_side_definition_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_linker_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX srch_lnkr_defntn_tbl_srch_lnkr_fk_idx (search_linker_id ASC),
  CONSTRAINT srch_lnkr_defntn_tbl_srch_lnkr_fk
    FOREIGN KEY (search_linker_id)
    REFERENCES search_linker_tbl (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = '2 records per linker per search';

CREATE TABLE search_linker_per_side_linkable_residues_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_linker_per_side_definition_id INT UNSIGNED NOT NULL,
  residue VARCHAR(1) NOT NULL,
  PRIMARY KEY (id),
  INDEX srchlnkrpersd_lkbl_rsds_fkid_fk_idx (search_linker_per_side_definition_id ASC),
  CONSTRAINT srchlnkrpersd_lkbl_rsds_fkid_fk
    FOREIGN KEY (search_linker_per_side_definition_id)
    REFERENCES search_linker_per_side_definition_tbl (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE TABLE search_linker_per_side_linkable_protein_termini_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_linker_per_side_definition_id INT UNSIGNED NOT NULL,
  n_terminus_c_terminus ENUM('n', 'c') NOT NULL COMMENT '\"n\" or \"c\" terminus',
  distance_from_terminus INT UNSIGNED NOT NULL COMMENT '0 indicates the terminus itself',
  PRIMARY KEY (id),
  INDEX srchlnkrprsd_lnkbl_prtn_trmn_fkid_fk_idx (search_linker_per_side_definition_id ASC),
  CONSTRAINT srchlnkrprsd_lnkbl_prtn_trmn_fkid_fk
    FOREIGN KEY (search_linker_per_side_definition_id)
    REFERENCES search_linker_per_side_definition_tbl (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Records for \"n\" and \"c\" terminus linkable';

ALTER TABLE search_linker_tbl 
ADD COLUMN spacer_arm_length DOUBLE NULL AFTER linker_abbr,
ADD COLUMN spacer_arm_length_string VARCHAR(45) NULL AFTER spacer_arm_length;

ALTER TABLE linker_per_search_crosslink_mass_tbl 
ADD COLUMN chemical_formula VARCHAR(1000) NULL AFTER crosslink_mass_string;


