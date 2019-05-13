
-- -----------------------------------------------------
-- Table psm_per_peptide
-- -----------------------------------------------------

CREATE TABLE  psm_per_peptide (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  psm_id INT(10) UNSIGNED NOT NULL,
  srch_rep_pept__peptide_id INT(10) UNSIGNED NOT NULL COMMENT 'srch_rep_pept__peptide.id',
  scan_id INT UNSIGNED NULL,
  charge SMALLINT NULL,
  linker_mass DECIMAL(18,9) NULL,
  scan_number INT UNSIGNED NULL,
  search_scan_filename_id INT UNSIGNED NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_psm_per_peptide_psm_id
    FOREIGN KEY (psm_id)
    REFERENCES psm (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX fk_psm_per_peptide_psm_id_idx ON psm_per_peptide (psm_id ASC);


-- -----------------------------------------------------
-- Table linker_per_search_cleaved_crosslink_mass
-- -----------------------------------------------------

CREATE TABLE  linker_per_search_cleaved_crosslink_mass (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  linker_id INT UNSIGNED NOT NULL,
  search_id INT UNSIGNED NOT NULL,
  cleaved_crosslink_mass_double DOUBLE NOT NULL,
  cleaved_crosslink_mass_string VARCHAR(200) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT linkr_pr_srch_clvd_crosslnk_mss_linker_fk
    FOREIGN KEY (linker_id)
    REFERENCES linker (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT linkr_pr_srch_clvd_crosslnk_mss_search_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX linkr_pr_srch_clvd_crosslnk_mss_linker_fk_idx ON linker_per_search_cleaved_crosslink_mass (linker_id ASC);

CREATE INDEX linkr_pr_srch_clvd_crosslnk_mss_search_fk_idx ON linker_per_search_cleaved_crosslink_mass (search_id ASC);


