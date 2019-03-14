
---  This is NOT an add on to 2019_03_add_linker_per_search_cleaved_crosslink_mass_psm_per_peptide.sql

--  This is just the next group of DB changes




CREATE TABLE IF NOT EXISTS search_linker_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  linker_abbr VARCHAR(255) NOT NULL,
  linker_name VARCHAR(500) NULL,
  PRIMARY KEY (id),
  INDEX search_linker_tbl_search_id_fk_idx (search_id ASC),
  UNIQUE INDEX search_id_abbr_unique (search_id ASC, linker_abbr ASC),
  CONSTRAINT search_linker_tbl_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB

CREATE TABLE IF NOT EXISTS linker_per_search_crosslink_mass_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_linker_id INT UNSIGNED NOT NULL,
  search_id INT UNSIGNED NOT NULL,
  crosslink_mass_double DOUBLE NOT NULL,
  crosslink_mass_string VARCHAR(200) NOT NULL,
  PRIMARY KEY (id),
  INDEX linkr_pr_srch_monolnk_mss_search_fk_idx (search_id ASC),
  INDEX linkr_pr_srch_crosslnk_mss_tbl_srch_linker_fk_idx (search_linker_id ASC),
  CONSTRAINT linkr_pr_srch_crosslnk_mss_tbl_search_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT linkr_pr_srch_crosslnk_mss_tbl_srch_linker_fk
    FOREIGN KEY (search_linker_id)
    REFERENCES search_linker_tbl (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE TABLE IF NOT EXISTS linker_per_search_cleaved_crosslink_mass_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_linker_id INT UNSIGNED NOT NULL,
  search_id INT UNSIGNED NOT NULL,
  cleaved_crosslink_mass_double DOUBLE NOT NULL,
  cleaved_crosslink_mass_string VARCHAR(200) NOT NULL,
  PRIMARY KEY (id),
  INDEX linkr_pr_srch_clvd_crosslnk_mss_search_fk_idx (search_id ASC),
  INDEX lnkr_pr_sch_clvd_crslnk_mss_tbl_srch_linker_fk_idx (search_linker_id ASC),
  CONSTRAINT lnkr_pr_sch_clvd_crslnk_mss_tbl_srch_linker_fk
    FOREIGN KEY (search_linker_id)
    REFERENCES search_linker_tbl (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT lnkr_pr_sch_clvd_crslnk_mss_tbl_search_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE TABLE IF NOT EXISTS linker_per_search_monolink_mass_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_linker_id INT UNSIGNED NOT NULL,
  search_id INT UNSIGNED NOT NULL,
  monolink_mass_double DOUBLE NOT NULL,
  monolink_mass_string VARCHAR(200) NOT NULL,
  PRIMARY KEY (id),
  INDEX linkr_pr_srch_monolnk_mss_search_fk_idx (search_id ASC),
  INDEX lnkr_pr_sch_monlnk_mss_tbl_srch_lnkr_fk_idx (search_linker_id ASC),
  CONSTRAINT lnkr_pr_sch_monlnk_mss_tbl_srch_lnkr_fk
    FOREIGN KEY (search_linker_id)
    REFERENCES search_linker_tbl (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT lnkr_pr_sch_monlnk_mss_tbl_search_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;



