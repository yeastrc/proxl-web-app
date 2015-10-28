

CREATE TABLE unified_reported_peptide_lookup (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  unified_sequence VARCHAR(2000) NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX unified_reported_peptide__unified_sequence_idx (unified_sequence(20) ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

CREATE TABLE unified_rep_pep_matched_peptide_lookup (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
  peptide_id INT(10) UNSIGNED NOT NULL,
  peptide_order INT(10) UNSIGNED NOT NULL,
  link_position_1 INT(10) UNSIGNED NULL DEFAULT NULL,
  link_position_2 INT(10) UNSIGNED NULL DEFAULT NULL,
  INDEX unified_matched_peptide__unified_reported_peptide_id_fk_idx (unified_reported_peptide_id ASC),
  INDEX unified_matched_peptide__peptide_id_fk_idx (peptide_id ASC),
  PRIMARY KEY (id),
  CONSTRAINT unified_matched_peptide__unified_reported_peptide_id_fk
    FOREIGN KEY (unified_reported_peptide_id)
    REFERENCES proxl.unified_reported_peptide_lookup (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_matched_peptide__peptide_id_fk
    FOREIGN KEY (peptide_id)
    REFERENCES proxl.peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE unified_rep_pep_dynamic_mod_lookup (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  rp_matched_peptide_id INT(10) UNSIGNED NOT NULL,
  position INT(10) UNSIGNED NOT NULL,
  mass DOUBLE NOT NULL,
  mass_rounded DOUBLE NOT NULL,
  mass_rounded_string VARCHAR(200) NOT NULL,
  mass_rounding_places SMALLINT(6) NOT NULL,
  mod_order SMALLINT(6) NOT NULL,
  PRIMARY KEY (id),
  INDEX unified_rp_dynamic_mod__rp_matched_peptide_id_fk_idx (rp_matched_peptide_id ASC),
  CONSTRAINT unified_rp_dynamic_mod__rp_matched_peptide_id_fk
    FOREIGN KEY (rp_matched_peptide_id)
    REFERENCES proxl.unified_rep_pep_matched_peptide_lookup (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

CREATE TABLE search__dynamic_mod_mass_lookup (
  search_id INT(10) UNSIGNED NOT NULL,
  dynamic_mod_mass DOUBLE UNSIGNED NOT NULL,
  search_id_dynamic_mod_mass_count INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (search_id, dynamic_mod_mass),
  CONSTRAINT search__dynamic_mod_mass__search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES proxl.search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE query_criteria_value_counts (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  field VARCHAR(45) NOT NULL,
  value VARCHAR(45) NOT NULL,
  count INT(10) UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE INDEX query_criteria_value_counts__field_value_unique_idx (field ASC, value ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE unified_rep_pep__reported_peptide__search_lookup (
  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  search_id INT(10) UNSIGNED NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  peptide_q_value_for_search DOUBLE NULL DEFAULT NULL,
  best_psm_q_value DOUBLE NULL DEFAULT NULL,
  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
  has_monolinks TINYINT(3) UNSIGNED NOT NULL,
  psm_num_at_pt_01_q_cutoff INT(11) NOT NULL,
  PRIMARY KEY (unified_reported_peptide_id, reported_peptide_id, search_id),
  INDEX unified_rp__reported_peptide__search__reported_peptide_id_f_idx (reported_peptide_id ASC),
  INDEX unified_rp__reported_peptide__search__search_id_fk_idx (search_id ASC),
  INDEX unified_rp__rp__search__srch_type_bpsmqval_idx (search_id ASC, link_type ASC, best_psm_q_value ASC),
  CONSTRAINT unified_rp__reported_peptide__search__unified_rp_id_fk
    FOREIGN KEY (unified_reported_peptide_id)
    REFERENCES proxl.unified_reported_peptide_lookup (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp__reported_peptide__search__reported_peptide_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES proxl.reported_peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp__reported_peptide__search__search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES proxl.search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE search__reported_peptide__dynamic_mod_lookup (
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  dynamic_mod_mass DOUBLE UNSIGNED NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  best_psm_q_value DOUBLE NOT NULL,
  PRIMARY KEY (search_id, reported_peptide_id, dynamic_mod_mass),
  INDEX search__rep_pep__dyn_mods_rep_pep_id_fk_idx (reported_peptide_id ASC),
  INDEX search__rep_pep__dyn_mods_search_id_lnk_tp_bpqv_idx (search_id ASC, link_type ASC, best_psm_q_value ASC),
  INDEX search__rep_pep__dyn_mods_search_id_bpqv_idx (search_id ASC, best_psm_q_value ASC),
  CONSTRAINT search__rep_pep__dyn_mods_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES proxl.search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT search__rep_pep__dyn_mods_rep_pep_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES proxl.reported_peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

