SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS proxl ;
CREATE SCHEMA  proxl DEFAULT CHARACTER SET utf8 COLLATE utf8_bin ;
USE proxl ;

-- -----------------------------------------------------
-- Table auth_user
-- -----------------------------------------------------
DROP TABLE IF EXISTS auth_user ;

CREATE TABLE  auth_user (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password_hashed VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  user_access_level SMALLINT NULL,
  last_login DATETIME NULL,
  last_login_ip VARCHAR(255) NULL,
  last_password_change DATETIME NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX auth_username_UNIQUE ON auth_user (username ASC);

CREATE UNIQUE INDEX email_UNIQUE ON auth_user (email ASC);


-- -----------------------------------------------------
-- Table auth_shared_object
-- -----------------------------------------------------
DROP TABLE IF EXISTS auth_shared_object ;

CREATE TABLE  auth_shared_object (
  shared_object_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  public_access_code_enabled TINYINT NOT NULL DEFAULT 0,
  public_access_code VARCHAR(255) NULL,
  PRIMARY KEY (shared_object_id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE UNIQUE INDEX public_access_code_UNIQUE ON auth_shared_object (public_access_code ASC);


-- -----------------------------------------------------
-- Table xl_user
-- -----------------------------------------------------
DROP TABLE IF EXISTS xl_user ;

CREATE TABLE  xl_user (
  auth_user_id INT UNSIGNED NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  organization VARCHAR(2000) NULL,
  PRIMARY KEY (auth_user_id),
  CONSTRAINT fk_auth_user_id
    FOREIGN KEY (auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table project
-- -----------------------------------------------------
DROP TABLE IF EXISTS project ;

CREATE TABLE  project (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  auth_shareable_object_id INT UNSIGNED NOT NULL,
  title VARCHAR(255) NULL,
  abstract TEXT NULL,
  enabled TINYINT UNSIGNED NOT NULL DEFAULT 1,
  marked_for_deletion TINYINT UNSIGNED NOT NULL DEFAULT 0,
  project_locked TINYINT NOT NULL DEFAULT 0,
  public_access_level SMALLINT NULL,
  public_access_locked TINYINT NULL DEFAULT 0,
  marked_for_deletion_timestamp TIMESTAMP NULL,
  marked_for_deletion_auth_user_id INT UNSIGNED NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_auth_shareable_object_id
    FOREIGN KEY (auth_shareable_object_id)
    REFERENCES auth_shared_object (shared_object_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_auth_shareable_object_id_idx ON project (auth_shareable_object_id ASC);


-- -----------------------------------------------------
-- Table linker
-- -----------------------------------------------------
DROP TABLE IF EXISTS linker ;

CREATE TABLE  linker (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  abbr VARCHAR(255) NOT NULL,
  name VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE UNIQUE INDEX abbr ON linker (abbr ASC);


-- -----------------------------------------------------
-- Table reported_peptide
-- -----------------------------------------------------
DROP TABLE IF EXISTS reported_peptide ;

CREATE TABLE  reported_peptide (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  sequence VARCHAR(2000) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX sequence ON reported_peptide (sequence(20) ASC);


-- -----------------------------------------------------
-- Table pdb_file
-- -----------------------------------------------------
DROP TABLE IF EXISTS pdb_file ;

CREATE TABLE  pdb_file (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(2000) NULL DEFAULT NULL,
  content LONGTEXT NOT NULL,
  upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  uploaded_by INT(10) UNSIGNED NOT NULL,
  project_id INT(10) UNSIGNED NOT NULL,
  visibility VARCHAR(255) NOT NULL DEFAULT 'project',
  PRIMARY KEY (id),
  CONSTRAINT pdb_file_ibfk_1
    FOREIGN KEY (uploaded_by)
    REFERENCES auth_user (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT pdb_file_ibfk_2
    FOREIGN KEY (project_id)
    REFERENCES project (id)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE INDEX uploaded_by ON pdb_file (uploaded_by ASC);

CREATE INDEX project_id ON pdb_file (project_id ASC);


-- -----------------------------------------------------
-- Table pdb_alignment
-- -----------------------------------------------------
DROP TABLE IF EXISTS pdb_alignment ;

CREATE TABLE  pdb_alignment (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  pdb_file_id INT(10) UNSIGNED NOT NULL,
  chain_id CHAR(1) NULL DEFAULT NULL,
  protein_sequence_id INT(10) UNSIGNED NOT NULL,
  aligned_pdb_sequence VARCHAR(10000) NULL,
  aligned_experimental_sequence VARCHAR(10000) NULL,
  PRIMARY KEY (id),
  CONSTRAINT pdb_alignment_ibfk_1
    FOREIGN KEY (pdb_file_id)
    REFERENCES pdb_file (id)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE UNIQUE INDEX nrseq_id ON pdb_alignment (pdb_file_id ASC, chain_id ASC);

CREATE INDEX pdb_file_id ON pdb_alignment (pdb_file_id ASC, chain_id ASC);

CREATE UNIQUE INDEX unique_prot_seq_id_chain_id_pdb_file_id ON pdb_alignment (protein_sequence_id ASC, chain_id ASC, pdb_file_id ASC);


-- -----------------------------------------------------
-- Table peptide
-- -----------------------------------------------------
DROP TABLE IF EXISTS peptide ;

CREATE TABLE  peptide (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  sequence VARCHAR(2000) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX sequence ON peptide (sequence(20) ASC);


-- -----------------------------------------------------
-- Table search
-- -----------------------------------------------------
DROP TABLE IF EXISTS search ;

CREATE TABLE  search (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  path VARCHAR(2000) NULL,
  fasta_filename VARCHAR(2000) NOT NULL,
  load_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name VARCHAR(2000) NULL,
  project_id INT UNSIGNED NOT NULL,
  insert_complete TINYINT UNSIGNED NOT NULL DEFAULT 0,
  directory_name VARCHAR(255) NULL,
  display_order INT NOT NULL DEFAULT 0,
  no_scan_data TINYINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT fk_project_id
    FOREIGN KEY (project_id)
    REFERENCES project (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_project_id_idx ON search (project_id ASC);


-- -----------------------------------------------------
-- Table search_comment
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_comment ;

CREATE TABLE  search_comment (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT(10) UNSIGNED NOT NULL,
  comment VARCHAR(2000) NOT NULL,
  commentTimestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  auth_user_id INT UNSIGNED NULL,
  commentCreatedTimestamp TIMESTAMP NULL,
  created_auth_user_id INT NULL,
  PRIMARY KEY (id),
  CONSTRAINT perc_run_comment_ibfk_1
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE,
  CONSTRAINT search_comment_user_fk
    FOREIGN KEY (auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX search_id ON search_comment (search_id ASC, commentTimestamp ASC);

CREATE INDEX search_comment_user_fk_idx ON search_comment (auth_user_id ASC);


-- -----------------------------------------------------
-- Table search_reported_peptide
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_reported_peptide ;

CREATE TABLE  search_reported_peptide (
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  PRIMARY KEY (search_id, reported_peptide_id),
  CONSTRAINT search_reported_peptide_ibfk_1
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX reported_peptide_id ON search_reported_peptide (reported_peptide_id ASC);


-- -----------------------------------------------------
-- Table psm
-- -----------------------------------------------------
DROP TABLE IF EXISTS psm ;

CREATE TABLE  psm (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT(10) UNSIGNED NOT NULL,
  scan_id INT UNSIGNED NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  charge SMALLINT NULL,
  linker_mass DECIMAL(18,9) NULL,
  PRIMARY KEY (id),
  CONSTRAINT psm_ibfk_1
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE,
  CONSTRAINT psm_ibfk_2
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX perc_run_id ON psm (search_id ASC);

CREATE INDEX peplynx_peptide_id ON psm (reported_peptide_id ASC);

CREATE INDEX psm_scan_id_fk_idx ON psm (scan_id ASC);

CREATE INDEX psm__search_id_rep_pep_id ON psm (search_id ASC, reported_peptide_id ASC);

CREATE INDEX psm__search_id_type_idx ON psm (search_id ASC);


-- -----------------------------------------------------
-- Table taxonomy
-- -----------------------------------------------------
DROP TABLE IF EXISTS taxonomy ;

CREATE TABLE  taxonomy (
  id INT(10) UNSIGNED NOT NULL,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;

CREATE INDEX name ON taxonomy (name ASC);


-- -----------------------------------------------------
-- Table note
-- -----------------------------------------------------
DROP TABLE IF EXISTS note ;

CREATE TABLE  note (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  project_id INT UNSIGNED NOT NULL,
  auth_user_id_created INT UNSIGNED NOT NULL,
  created_date_time DATETIME NOT NULL,
  auth_user_id_last_updated INT UNSIGNED NOT NULL,
  last_updated_date_time DATETIME NOT NULL,
  note_text TEXT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_note_auth_user_id
    FOREIGN KEY (auth_user_id_created)
    REFERENCES auth_user (id)
    ON UPDATE NO ACTION,
  CONSTRAINT fk_note_project_id
    FOREIGN KEY (project_id)
    REFERENCES project (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_project_id_idx ON note (project_id ASC);

CREATE INDEX fk_auth_user_id_idx ON note (auth_user_id_created ASC);


-- -----------------------------------------------------
-- Table auth_forgot_password_tracking
-- -----------------------------------------------------
DROP TABLE IF EXISTS auth_forgot_password_tracking ;

CREATE TABLE  auth_forgot_password_tracking (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  auth_user_id INT UNSIGNED NOT NULL,
  create_date DATETIME NOT NULL,
  used_date DATETIME NULL,
  forgot_password_tracking_code VARCHAR(255) NOT NULL,
  submit_ip VARCHAR(255) NOT NULL,
  use_ip VARCHAR(255) NULL,
  code_replaced_by_newer TINYINT(1) NULL,
  PRIMARY KEY (id),
  CONSTRAINT forgot_pwd_trk_auth_user_id_fk
    FOREIGN KEY (auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE UNIQUE INDEX forgot_password_tracking_code_UNIQUE ON auth_forgot_password_tracking (forgot_password_tracking_code ASC);

CREATE INDEX forgot_pwd_trk_auth_user_id_fk_idx ON auth_forgot_password_tracking (auth_user_id ASC);


-- -----------------------------------------------------
-- Table auth_shared_object_users
-- -----------------------------------------------------
DROP TABLE IF EXISTS auth_shared_object_users ;

CREATE TABLE  auth_shared_object_users (
  shared_object_id INT UNSIGNED NOT NULL,
  user_id INT UNSIGNED NOT NULL,
  access_level SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (shared_object_id, user_id),
  CONSTRAINT fk_shared_objects_object_id
    FOREIGN KEY (shared_object_id)
    REFERENCES auth_shared_object (shared_object_id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT fk_shared_objects_user_id
    FOREIGN KEY (user_id)
    REFERENCES auth_user (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX idx_shared_objects_user_id ON auth_shared_object_users (user_id ASC);


-- -----------------------------------------------------
-- Table auth_user_invite_tracking
-- -----------------------------------------------------
DROP TABLE IF EXISTS auth_user_invite_tracking ;

CREATE TABLE  auth_user_invite_tracking (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  submitting_auth_user_id INT UNSIGNED NOT NULL,
  submit_ip VARCHAR(255) NOT NULL,
  invite_tracking_code VARCHAR(255) NOT NULL,
  invited_user_email VARCHAR(255) NOT NULL,
  invited_user_access_level SMALLINT NOT NULL,
  invited_shared_object_id INT UNSIGNED NULL,
  invite_create_date DATETIME NOT NULL,
  invite_used TINYINT NULL,
  invite_used_date DATETIME NULL,
  invite_used_auth_user_id INT UNSIGNED NULL,
  use_ip VARCHAR(255) NULL,
  code_replaced_by_newer TINYINT NULL,
  invite_revoked TINYINT NULL,
  revoking_auth_user_id INT UNSIGNED NULL,
  revoked_date DATETIME NULL,
  PRIMARY KEY (id),
  CONSTRAINT user_invite_trk_auth_user_id_fk
    FOREIGN KEY (submitting_auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE CASCADE,
  CONSTRAINT user_invite_trk_revoking_auth_user_id
    FOREIGN KEY (revoking_auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT user_invite_trk_shared_object_id_fk
    FOREIGN KEY (invited_shared_object_id)
    REFERENCES auth_shared_object (shared_object_id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT user_invite_trk_submitting_auth_user_id_fk
    FOREIGN KEY (submitting_auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT user_invite_trk_used_auth_user_id_fk
    FOREIGN KEY (invite_used_auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE UNIQUE INDEX invite_tracking_code_UNIQUE ON auth_user_invite_tracking (invite_tracking_code ASC);

CREATE INDEX forgot_pwd_trk_auth_user_id_fk_idx ON auth_user_invite_tracking (submitting_auth_user_id ASC);

CREATE INDEX user_invite_trk_revoking_auth_user_id_idx ON auth_user_invite_tracking (revoking_auth_user_id ASC);

CREATE INDEX user_invite_trk_shared_object_id_fk_idx ON auth_user_invite_tracking (invited_shared_object_id ASC);

CREATE INDEX user_invite_trk_used_auth_user_id_fk_idx ON auth_user_invite_tracking (invite_used_auth_user_id ASC);


-- -----------------------------------------------------
-- Table xl_user_access_level_label_description
-- -----------------------------------------------------
DROP TABLE IF EXISTS xl_user_access_level_label_description ;

CREATE TABLE  xl_user_access_level_label_description (
  xl_user_access_level_numeric_value INT UNSIGNED NOT NULL,
  label VARCHAR(255) NOT NULL,
  description VARCHAR(255) NULL,
  PRIMARY KEY (xl_user_access_level_numeric_value))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table scan_file
-- -----------------------------------------------------
DROP TABLE IF EXISTS scan_file ;

CREATE TABLE  scan_file (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  filename VARCHAR(255) NOT NULL,
  path VARCHAR(2000) NULL,
  sha1sum VARCHAR(255) NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;

CREATE UNIQUE INDEX filename ON scan_file (filename ASC, sha1sum ASC);


-- -----------------------------------------------------
-- Table scan_file_header
-- -----------------------------------------------------
DROP TABLE IF EXISTS scan_file_header ;

CREATE TABLE  scan_file_header (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  scan_file_id INT UNSIGNED NOT NULL,
  header VARCHAR(255) NOT NULL,
  value TEXT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_scan_file_header_scan_file_id
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX fk_scan_file_header_scan_file_id_idx ON scan_file_header (scan_file_id ASC);


-- -----------------------------------------------------
-- Table scan
-- -----------------------------------------------------
DROP TABLE IF EXISTS scan ;

CREATE TABLE  scan (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  scan_file_id INT UNSIGNED NOT NULL,
  start_scan_number INT UNSIGNED NOT NULL,
  end_scan_number INT UNSIGNED NULL,
  level SMALLINT UNSIGNED NOT NULL,
  preMZ DECIMAL(18,9) NULL,
  precursor_scan_number INT NOT NULL,
  precursor_scan_id INT UNSIGNED NULL,
  retention_time DECIMAL(18,9) NULL,
  peak_count INT NOT NULL,
  fragmentation_type VARCHAR(45) NULL,
  is_centroid CHAR(1) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_scan_scan_file_id
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE UNIQUE INDEX start_scan_number ON scan (start_scan_number ASC, scan_file_id ASC);

CREATE INDEX fk_scan_scan_file_id_idx ON scan (scan_file_id ASC);


-- -----------------------------------------------------
-- Table scan_spectrum_data
-- -----------------------------------------------------
DROP TABLE IF EXISTS scan_spectrum_data ;

CREATE TABLE  scan_spectrum_data (
  scan_id INT UNSIGNED NOT NULL,
  spectrum_data LONGBLOB NULL,
  PRIMARY KEY (scan_id),
  CONSTRAINT fk_scan_spectrum_data_scan_id
    FOREIGN KEY (scan_id)
    REFERENCES scan (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table search_linker
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_linker ;

CREATE TABLE  search_linker (
  search_id INT UNSIGNED NOT NULL,
  linker_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (search_id, linker_id),
  CONSTRAINT search_linker_linker_id_fk
    FOREIGN KEY (linker_id)
    REFERENCES linker (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT search_linker_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX search_linker_linker_id_fk_idx ON search_linker (linker_id ASC);


-- -----------------------------------------------------
-- Table search_file
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_file ;

CREATE TABLE  search_file (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  filename VARCHAR(255) NOT NULL,
  display_filename VARCHAR(255) NULL,
  path VARCHAR(2000) NULL,
  filesize INT NOT NULL,
  mime_type VARCHAR(500) NULL,
  description VARCHAR(2500) NULL,
  upload_date DATETIME NOT NULL,
  file_contents LONGBLOB NULL,
  PRIMARY KEY (id),
  CONSTRAINT search_file_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX search_file_search_id_fk_idx ON search_file (search_id ASC);


-- -----------------------------------------------------
-- Table scan_retention_time
-- -----------------------------------------------------
DROP TABLE IF EXISTS scan_retention_time ;

CREATE TABLE  scan_retention_time (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  scan_file_id INT UNSIGNED NOT NULL,
  scan_number INT NOT NULL,
  precursor_scan_number INT NULL,
  scan_level INT NOT NULL,
  retention_time DECIMAL(18,9) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT scan_retention_time_scan_file_id_fk
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX scan_retention_time_scan_file_id_fk_idx ON scan_retention_time (scan_file_id ASC);

CREATE UNIQUE INDEX scan_retention_time_unique ON scan_retention_time (scan_file_id ASC, scan_number ASC);


-- -----------------------------------------------------
-- Table static_mod
-- -----------------------------------------------------
DROP TABLE IF EXISTS static_mod ;

CREATE TABLE  static_mod (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  residue VARCHAR(45) NOT NULL,
  mass DECIMAL(18,9) NULL,
  mass_string VARCHAR(45) NULL,
  PRIMARY KEY (id),
  CONSTRAINT static_mod_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX static_mod_search_id_fk_idx ON static_mod (search_id ASC);


-- -----------------------------------------------------
-- Table config_system
-- -----------------------------------------------------
DROP TABLE IF EXISTS config_system ;

CREATE TABLE  config_system (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  config_key VARCHAR(255) NOT NULL,
  config_value VARCHAR(4000) NULL,
  comment VARCHAR(4000) NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;

CREATE UNIQUE INDEX config_system_config_key_idx ON config_system (config_key ASC);


-- -----------------------------------------------------
-- Table search_web_links
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_web_links ;

CREATE TABLE  search_web_links (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  auth_user_id INT UNSIGNED NULL,
  link_url VARCHAR(600) NOT NULL,
  link_label VARCHAR(400) NOT NULL,
  link_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT search_links_auth_user_id_fk
    FOREIGN KEY (auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE SET NULL
    ON UPDATE RESTRICT,
  CONSTRAINT search_links_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX search_links_search_id_fk_idx ON search_web_links (search_id ASC);

CREATE INDEX search_links_auth_user_id_fk_idx ON search_web_links (auth_user_id ASC);


-- -----------------------------------------------------
-- Table unified_reported_peptide_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS unified_reported_peptide_lookup ;

CREATE TABLE  unified_reported_peptide_lookup (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  unified_sequence VARCHAR(2000) NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  has_dynamic_modifictions TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

CREATE INDEX unified_reported_peptide__unified_sequence_idx ON unified_reported_peptide_lookup (unified_sequence(20) ASC);


-- -----------------------------------------------------
-- Table unified_rep_pep_matched_peptide_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS unified_rep_pep_matched_peptide_lookup ;

CREATE TABLE  unified_rep_pep_matched_peptide_lookup (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  unified_reported_peptide_id INT UNSIGNED NOT NULL,
  peptide_id INT UNSIGNED NOT NULL,
  peptide_order INT UNSIGNED NOT NULL,
  link_position_1 INT UNSIGNED NULL,
  link_position_2 INT UNSIGNED NULL,
  PRIMARY KEY (id),
  CONSTRAINT unified_matched_peptide__unified_reported_peptide_id_fk
    FOREIGN KEY (unified_reported_peptide_id)
    REFERENCES unified_reported_peptide_lookup (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_matched_peptide__peptide_id_fk
    FOREIGN KEY (peptide_id)
    REFERENCES peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX unified_matched_peptide__unified_reported_peptide_id_fk_idx ON unified_rep_pep_matched_peptide_lookup (unified_reported_peptide_id ASC);

CREATE INDEX unified_matched_peptide__peptide_id_fk_idx ON unified_rep_pep_matched_peptide_lookup (peptide_id ASC);


-- -----------------------------------------------------
-- Table unified_rep_pep_dynamic_mod_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS unified_rep_pep_dynamic_mod_lookup ;

CREATE TABLE  unified_rep_pep_dynamic_mod_lookup (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  rp_matched_peptide_id INT(10) UNSIGNED NOT NULL,
  position INT(10) UNSIGNED NOT NULL,
  mass DOUBLE NOT NULL,
  mass_rounded DOUBLE NOT NULL,
  mass_rounded_string VARCHAR(200) NOT NULL,
  mass_rounding_places SMALLINT NOT NULL,
  mod_order SMALLINT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT unified_rp_dynamic_mod__rp_matched_peptide_id_fk
    FOREIGN KEY (rp_matched_peptide_id)
    REFERENCES unified_rep_pep_matched_peptide_lookup (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

CREATE INDEX unified_rp_dynamic_mod__rp_matched_peptide_id_fk_idx ON unified_rep_pep_dynamic_mod_lookup (rp_matched_peptide_id ASC);


-- -----------------------------------------------------
-- Table search__dynamic_mod_mass_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS search__dynamic_mod_mass_lookup ;

CREATE TABLE  search__dynamic_mod_mass_lookup (
  search_id INT UNSIGNED NOT NULL,
  dynamic_mod_mass DOUBLE NOT NULL,
  PRIMARY KEY (search_id, dynamic_mod_mass),
  CONSTRAINT search__dynamic_mod_mass__search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table search__reported_peptide__dynamic_mod_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS search__reported_peptide__dynamic_mod_lookup ;

CREATE TABLE  search__reported_peptide__dynamic_mod_lookup (
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  dynamic_mod_mass DOUBLE NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  PRIMARY KEY (search_id, reported_peptide_id, dynamic_mod_mass),
  CONSTRAINT search__rep_pep__dyn_mods_rep_pep_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT search__rep_pep__dyn_mods_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX search__rep_pep__dyn_mods_rep_pep_id_fk_idx ON search__reported_peptide__dynamic_mod_lookup (reported_peptide_id ASC);

CREATE INDEX search__rep_pep__dyn_mods_search_id_lnk_tp_idx ON search__reported_peptide__dynamic_mod_lookup (search_id ASC, link_type ASC);


-- -----------------------------------------------------
-- Table search_programs_per_search
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_programs_per_search ;

CREATE TABLE  search_programs_per_search (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  name VARCHAR(200) NOT NULL,
  display_name VARCHAR(255) NOT NULL,
  version VARCHAR(200) NOT NULL,
  description VARCHAR(4000) NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch_prgrms_per_srch_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE UNIQUE INDEX search_program__search_id__name__unique_idx ON search_programs_per_search (search_id ASC, name ASC);

CREATE INDEX search_program__search_id_fk_idx ON search_programs_per_search (search_id ASC);


-- -----------------------------------------------------
-- Table annotation_type
-- -----------------------------------------------------
DROP TABLE IF EXISTS annotation_type ;

CREATE TABLE  annotation_type (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  search_programs_per_search_id INT(10) UNSIGNED NOT NULL,
  psm_peptide_type ENUM('psm','peptide') NOT NULL,
  filterable_descriptive_type ENUM('filterable','descriptive') NOT NULL,
  name VARCHAR(255) NOT NULL,
  default_visible INT(1) NOT NULL,
  display_order INT NULL,
  description VARCHAR(4000) NULL,
  PRIMARY KEY (id),
  CONSTRAINT ann_tp_srch_pgm_id_fk
    FOREIGN KEY (search_programs_per_search_id)
    REFERENCES search_programs_per_search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX ann_tp_srch_pgm_id_fk_idx ON annotation_type (search_programs_per_search_id ASC);

CREATE UNIQUE INDEX annotation_type_Unique_idx ON annotation_type (search_id ASC, search_programs_per_search_id ASC, psm_peptide_type ASC, filterable_descriptive_type ASC, name ASC);


-- -----------------------------------------------------
-- Table annotation_type_filterable
-- -----------------------------------------------------
DROP TABLE IF EXISTS annotation_type_filterable ;

CREATE TABLE  annotation_type_filterable (
  annotation_type_id INT UNSIGNED NOT NULL,
  filter_direction ENUM('above','below') NOT NULL,
  default_filter INT(1) NOT NULL,
  default_filter_value DOUBLE NULL,
  default_filter_value_string VARCHAR(45) NULL,
  sort_order INT UNSIGNED NULL,
  default_filter_at_database_load INT(1) NOT NULL DEFAULT 0,
  default_filter_value_at_database_load DOUBLE NULL,
  default_filter_value_string_at_database_load VARCHAR(45) NULL,
  PRIMARY KEY (annotation_type_id),
  CONSTRAINT ann_type_fltrbl__annotation_type_id_fk
    FOREIGN KEY (annotation_type_id)
    REFERENCES annotation_type (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table psm_annotation
-- -----------------------------------------------------
DROP TABLE IF EXISTS psm_annotation ;

CREATE TABLE  psm_annotation (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  psm_id INT UNSIGNED NOT NULL,
  filterable_descriptive_type ENUM('filterable','descriptive') NOT NULL,
  annotation_type_id INT UNSIGNED NOT NULL,
  value_location ENUM('local','large_value_table') NOT NULL,
  value_double DOUBLE NOT NULL,
  value_string VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT psm_filterable_annotation__psm_id_fk
    FOREIGN KEY (psm_id)
    REFERENCES psm (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX psm_annotation__psm_id_fk_idx ON psm_annotation (psm_id ASC);

CREATE UNIQUE INDEX psm_annotation_psm_id_ann_typ_id_idx ON psm_annotation (psm_id ASC, annotation_type_id ASC);

CREATE INDEX psm_annotation_psm_id_ann_typ_f_d_idx ON psm_annotation (psm_id ASC, annotation_type_id ASC);


-- -----------------------------------------------------
-- Table srch__rep_pept__annotation
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch__rep_pept__annotation ;

CREATE TABLE  srch__rep_pept__annotation (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  filterable_descriptive_type ENUM('filterable','descriptive') NOT NULL,
  annotation_type_id INT(10) UNSIGNED NOT NULL,
  value_location ENUM('local','large_value_table') NOT NULL,
  value_double DOUBLE NOT NULL,
  value_string VARCHAR(50) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch__rep_pept__ann__rep_pept_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT srch__rep_pept__ann__type_id_fk
    FOREIGN KEY (annotation_type_id)
    REFERENCES annotation_type (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 27
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE INDEX srch__rep_pept__ann__rep_pept_id_fk_idx ON srch__rep_pept__annotation (reported_peptide_id ASC);

CREATE UNIQUE INDEX srch__rep_pept_search_id_reppeptid_ann__type_id_fk_idx ON srch__rep_pept__annotation (search_id ASC, reported_peptide_id ASC, annotation_type_id ASC);

CREATE INDEX srch__rep_pept__ann__type_id_fk_idx ON srch__rep_pept__annotation (annotation_type_id ASC);

CREATE INDEX srch__rep_pept_srch_id_reppeptid_ann_tp__idx ON srch__rep_pept__annotation (search_id ASC, reported_peptide_id ASC, filterable_descriptive_type ASC);


-- -----------------------------------------------------
-- Table psm_filterable_annotation__generic_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS psm_filterable_annotation__generic_lookup ;

CREATE TABLE  psm_filterable_annotation__generic_lookup (
  psm_annotation_id INT UNSIGNED NOT NULL,
  psm_id INT UNSIGNED NOT NULL,
  annotation_type_id INT UNSIGNED NOT NULL,
  value_double DOUBLE NOT NULL,
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  psm_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  PRIMARY KEY (psm_annotation_id),
  CONSTRAINT psm_filtrble_ann__generic_lkp_psm_ann_id_fk
    FOREIGN KEY (psm_annotation_id)
    REFERENCES psm_annotation (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX psm_filtrble_ann__generic_lkup__srch_rep_pep_anntpid_value ON psm_filterable_annotation__generic_lookup (search_id ASC, reported_peptide_id ASC, annotation_type_id ASC, value_double ASC);

CREATE INDEX psm_filtrble_ann__generic_lkup__psm_id_idx ON psm_filterable_annotation__generic_lookup (psm_id ASC);


-- -----------------------------------------------------
-- Table default_page_view_generic
-- -----------------------------------------------------
DROP TABLE IF EXISTS default_page_view_generic ;

CREATE TABLE  default_page_view_generic (
  search_id INT UNSIGNED NOT NULL,
  page_name VARCHAR(80) NOT NULL,
  auth_user_id_created_record INT UNSIGNED NOT NULL,
  auth_user_id_last_updated_record INT UNSIGNED NOT NULL,
  date_record_created DATETIME NULL,
  date_record_last_updated DATETIME NULL,
  url VARCHAR(6000) NOT NULL,
  query_json VARCHAR(6000) NOT NULL,
  PRIMARY KEY (search_id, page_name),
  CONSTRAINT default_page_view_generic_auth_user_id_fk
    FOREIGN KEY (auth_user_id_created_record)
    REFERENCES auth_user (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT default_page_view_generic_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT default_page_view_generic_auth_lst_upd
    FOREIGN KEY (auth_user_id_last_updated_record)
    REFERENCES auth_user (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX default_page_view_search_id_fk_idx ON default_page_view_generic (search_id ASC);

CREATE INDEX default_page_view_auth_user_id_fk_idx ON default_page_view_generic (auth_user_id_created_record ASC);

CREATE INDEX default_page_view_generic_auth_lst_upd_idx ON default_page_view_generic (auth_user_id_last_updated_record ASC);


-- -----------------------------------------------------
-- Table srch__rep_pept__annotation_large_value
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch__rep_pept__annotation_large_value ;

CREATE TABLE  srch__rep_pept__annotation_large_value (
  srch__rep_pept__annotation_id INT UNSIGNED NOT NULL,
  value_string LONGTEXT NULL,
  PRIMARY KEY (srch__rep_pept__annotation_id),
  CONSTRAINT srch__rep_pept__annotation_large_value__primary_id_fk
    FOREIGN KEY (srch__rep_pept__annotation_id)
    REFERENCES srch__rep_pept__annotation (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table psm_annotation_large_value
-- -----------------------------------------------------
DROP TABLE IF EXISTS psm_annotation_large_value ;

CREATE TABLE  psm_annotation_large_value (
  psm_annotation_id INT UNSIGNED NOT NULL,
  value_string LONGTEXT NOT NULL,
  CONSTRAINT psm_annotation_large_value_primary_id_fk
    FOREIGN KEY (psm_annotation_id)
    REFERENCES psm_annotation (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX psm_annotation_large_value_primary_id_fk_idx ON psm_annotation_large_value (psm_annotation_id ASC);


-- -----------------------------------------------------
-- Table linker_per_search_monolink_mass
-- -----------------------------------------------------
DROP TABLE IF EXISTS linker_per_search_monolink_mass ;

CREATE TABLE  linker_per_search_monolink_mass (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  linker_id INT UNSIGNED NOT NULL,
  search_id INT UNSIGNED NOT NULL,
  monolink_mass_double DOUBLE NOT NULL,
  monolink_mass_string VARCHAR(200) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT linkr_pr_srch_monolnk_mss_linker_fk
    FOREIGN KEY (linker_id)
    REFERENCES linker (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT linkr_pr_srch_monolnk_mss_search_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX linkr_pr_srch_monolnk_mss_linker_fk_idx ON linker_per_search_monolink_mass (linker_id ASC);

CREATE INDEX linkr_pr_srch_monolnk_mss_search_fk_idx ON linker_per_search_monolink_mass (search_id ASC);


-- -----------------------------------------------------
-- Table linker_per_search_crosslink_mass
-- -----------------------------------------------------
DROP TABLE IF EXISTS linker_per_search_crosslink_mass ;

CREATE TABLE  linker_per_search_crosslink_mass (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  linker_id INT UNSIGNED NOT NULL,
  search_id INT UNSIGNED NOT NULL,
  crosslink_mass_double DOUBLE NOT NULL,
  crosslink_mass_string VARCHAR(200) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT linkr_pr_srch_crosslnk_mss_linker_fk
    FOREIGN KEY (linker_id)
    REFERENCES linker (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT linkr_pr_srch_crosslnk_mss_search_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX linkr_pr_srch_monolnk_mss_linker_fk_idx ON linker_per_search_crosslink_mass (linker_id ASC);

CREATE INDEX linkr_pr_srch_monolnk_mss_search_fk_idx ON linker_per_search_crosslink_mass (search_id ASC);


-- -----------------------------------------------------
-- Table cutoffs_applied_on_import
-- -----------------------------------------------------
DROP TABLE IF EXISTS cutoffs_applied_on_import ;

CREATE TABLE  cutoffs_applied_on_import (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  annotation_type_id INT UNSIGNED NOT NULL,
  cutoff_value_string VARCHAR(255) NOT NULL,
  cutoff_value_double DOUBLE NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT cutoffs_applied_on_import_ann_type_id_fk
    FOREIGN KEY (annotation_type_id)
    REFERENCES annotation_type (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX cutoffs_at_import_ann_type_id_fk_idx ON cutoffs_applied_on_import (annotation_type_id ASC);

CREATE INDEX cutoffs_applied_on_import_search_id_idx ON cutoffs_applied_on_import (search_id ASC);

CREATE UNIQUE INDEX cutoffs_applied_on_import_search_ann_type_unique ON cutoffs_applied_on_import (search_id ASC, annotation_type_id ASC);


-- -----------------------------------------------------
-- Table srch_rep_pept__peptide
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch_rep_pept__peptide ;

CREATE TABLE  srch_rep_pept__peptide (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  peptide_id INT UNSIGNED NOT NULL,
  peptide_position_1 INT NOT NULL DEFAULT -1,
  peptide_position_2 INT NOT NULL DEFAULT -1,
  PRIMARY KEY (id),
  CONSTRAINT srch_rep_pept_pept_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT srch_rep_pept_pept_rep_pept_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT srch_rep_pept_pept_peptide_id_fk
    FOREIGN KEY (peptide_id)
    REFERENCES peptide (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX srch_rep_pept_pept_search_id_fk_idx ON srch_rep_pept__peptide (search_id ASC);

CREATE INDEX srch_rep_pept_pept_rep_pept_id_fk_idx ON srch_rep_pept__peptide (reported_peptide_id ASC);

CREATE INDEX srch_rep_pept_pept_peptide_id_fk_idx ON srch_rep_pept__peptide (peptide_id ASC);


-- -----------------------------------------------------
-- Table srch_rep_pept__pept__dynamic_mod
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch_rep_pept__pept__dynamic_mod ;

CREATE TABLE  srch_rep_pept__pept__dynamic_mod (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  search_reported_peptide_peptide_id INT(10) UNSIGNED NOT NULL,
  position INT(10) UNSIGNED NOT NULL,
  mass DOUBLE NOT NULL,
  is_monolink INT(1) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  CONSTRAINT srch_rp_ppt_ppt_dn_md_schrptpeppep_fk
    FOREIGN KEY (search_reported_peptide_peptide_id)
    REFERENCES srch_rep_pept__peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX srch_rp_ppt_ppt_dn_md_schrptpeppep_fk_idx ON srch_rep_pept__pept__dynamic_mod (search_reported_peptide_peptide_id ASC);


-- -----------------------------------------------------
-- Table unified_rp__search__rep_pept__generic_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS unified_rp__search__rep_pept__generic_lookup ;

CREATE TABLE  unified_rp__search__rep_pept__generic_lookup (
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
  has_monolinks TINYINT(3) UNSIGNED NOT NULL,
  psm_num_at_default_cutoff INT(10) UNSIGNED NOT NULL,
  peptide_meets_default_cutoffs ENUM('yes','no','not_applicable') NOT NULL,
  related_peptides_unique_for_search TINYINT(1) NOT NULL DEFAULT 0,
  num_unique_psm_at_default_cutoff INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (search_id, reported_peptide_id),
  CONSTRAINT unified_rp__search__rep_pept__gnrc_lkp_reported_peptide_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp__search__rep_pept__gnrc_lkp_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp__search__rep_pept__gnrc_lkp_unified_rp_id_fk
    FOREIGN KEY (unified_reported_peptide_id)
    REFERENCES unified_reported_peptide_lookup (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX search__rep_pept__generic_lookup__reported_peptide_id_f_idx ON unified_rp__search__rep_pept__generic_lookup (reported_peptide_id ASC);

CREATE INDEX search__rep_pept__generic_lookup__search_id_fk_idx ON unified_rp__search__rep_pept__generic_lookup (search_id ASC);

CREATE INDEX search__rep_pept__generic_lookup_search__srch_type_mts_dflt_idx ON unified_rp__search__rep_pept__generic_lookup (search_id ASC, link_type ASC, peptide_meets_default_cutoffs ASC);

CREATE INDEX unified_rp__search__rep_pept__generic_lookup_unified_rp_id__idx ON unified_rp__search__rep_pept__generic_lookup (unified_reported_peptide_id ASC);


-- -----------------------------------------------------
-- Table unified_rp__search_reported_peptide_fltbl_value_generic_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS unified_rp__search_reported_peptide_fltbl_value_generic_lookup ;

CREATE TABLE  unified_rp__search_reported_peptide_fltbl_value_generic_lookup (
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  annotation_type_id INT(10) UNSIGNED NOT NULL,
  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
  has_monolinks TINYINT(3) UNSIGNED NOT NULL,
  peptide_value_for_ann_type_id DOUBLE NOT NULL,
  PRIMARY KEY (search_id, reported_peptide_id, annotation_type_id),
  CONSTRAINT unified_rp_srch_rep_pept_fltbl_val_gnrc_lkp_srch_id_rep_pept_id
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp_srch_rep_pept_fltbl_value_generic_lookup_search_id
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp_srch_rep_pept_fltbl_vl_gnrc_lkp_unified_rp_id_fk
    FOREIGN KEY (unified_reported_peptide_id)
    REFERENCES unified_reported_peptide_lookup (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX reported_peptide_id_f_idx ON unified_rp__search_reported_peptide_fltbl_value_generic_lookup (reported_peptide_id ASC);

CREATE INDEX search__srch_type_pept_val_idx ON unified_rp__search_reported_peptide_fltbl_value_generic_lookup (search_id ASC, link_type ASC, peptide_value_for_ann_type_id ASC);

CREATE INDEX unified_rp_srch_rep_pept_fltbl_vl_gnrc_lkp_unified_rp_id_fk_idx ON unified_rp__search_reported_peptide_fltbl_value_generic_lookup (unified_reported_peptide_id ASC);


-- -----------------------------------------------------
-- Table unified_rp__search__rep_pept__best_psm_value_generic_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS unified_rp__search__rep_pept__best_psm_value_generic_lookup ;

CREATE TABLE  unified_rp__search__rep_pept__best_psm_value_generic_lookup (
  search_id INT(10) UNSIGNED NOT NULL,
  reported_peptide_id INT(10) UNSIGNED NOT NULL,
  annotation_type_id INT(10) UNSIGNED NOT NULL,
  unified_reported_peptide_id INT(10) UNSIGNED NOT NULL,
  link_type ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  has_dynamic_modifictions TINYINT(3) UNSIGNED NOT NULL,
  has_monolinks TINYINT(3) UNSIGNED NOT NULL,
  best_psm_value_for_ann_type_id DOUBLE NOT NULL,
  psm_id_for_best_value__non_fk INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (search_id, reported_peptide_id, annotation_type_id),
  CONSTRAINT unified_rp_srch_rp_ppt_bst_psm_vl_gnrc_lkp_rep_pept_id_fk
    FOREIGN KEY (reported_peptide_id)
    REFERENCES reported_peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp_srch_rp_ppt_bst_psm_vl_gnrc_lkp_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT unified_rp_srch_rp_ppt_bst_psm_vl_gnrc_lkp_unified_rp_pept_id_fk
    FOREIGN KEY (unified_reported_peptide_id)
    REFERENCES unified_reported_peptide_lookup (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX reported_peptide_id_f_idx ON unified_rp__search__rep_pept__best_psm_value_generic_lookup (reported_peptide_id ASC);

CREATE INDEX search_id_for_fk___type_best_psm_val_idx ON unified_rp__search__rep_pept__best_psm_value_generic_lookup (search_id ASC, link_type ASC, best_psm_value_for_ann_type_id ASC);

CREATE INDEX unified_rp_srch_rp_ppt_bst_psm_vl_gnrc_lkp_unified_rp_pept__idx ON unified_rp__search__rep_pept__best_psm_value_generic_lookup (unified_reported_peptide_id ASC);


-- -----------------------------------------------------
-- Table srch_rep_pept__prot_seq_id_pos_monolink
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch_rep_pept__prot_seq_id_pos_monolink ;

CREATE TABLE  srch_rep_pept__prot_seq_id_pos_monolink (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  search_reported_peptide_peptide_id INT UNSIGNED NOT NULL,
  peptide_position INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  protein_sequence_position INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch_rppp_prt_sq_d_ps_mnlnk_srch_rppptpptd
    FOREIGN KEY (search_reported_peptide_peptide_id)
    REFERENCES srch_rep_pept__peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX search_rep_pept_idx ON srch_rep_pept__prot_seq_id_pos_monolink (search_id ASC, reported_peptide_id ASC);

CREATE INDEX srch_rppp_prt_sq_d_ps_mnlnk_srch_rppptpptd ON srch_rep_pept__prot_seq_id_pos_monolink (search_reported_peptide_peptide_id ASC);


-- -----------------------------------------------------
-- Table srch_rep_pept__prot_seq_id_pos_crosslink
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch_rep_pept__prot_seq_id_pos_crosslink ;

CREATE TABLE  srch_rep_pept__prot_seq_id_pos_crosslink (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  search_reported_peptide_peptide_id INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  protein_sequence_position INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch_rppp_prt_sq_d_ps_crslnk_srch_rppptpptd
    FOREIGN KEY (search_reported_peptide_peptide_id)
    REFERENCES srch_rep_pept__peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX search_rep_pept_idx ON srch_rep_pept__prot_seq_id_pos_crosslink (search_id ASC, reported_peptide_id ASC);

CREATE INDEX srch_rppp_prt_sq_d_ps_crslnk_srch_rppptpptd_idx ON srch_rep_pept__prot_seq_id_pos_crosslink (search_reported_peptide_peptide_id ASC);


-- -----------------------------------------------------
-- Table srch_rep_pept__prot_seq_id_pos_looplink
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch_rep_pept__prot_seq_id_pos_looplink ;

CREATE TABLE  srch_rep_pept__prot_seq_id_pos_looplink (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  search_reported_peptide_peptide_id INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  protein_sequence_position_1 INT UNSIGNED NOT NULL,
  protein_sequence_position_2 INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch_rppp_prt_sq_d_ps_lplnk_srch_rppptpptd
    FOREIGN KEY (search_reported_peptide_peptide_id)
    REFERENCES srch_rep_pept__peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX search_rep_pept_idx ON srch_rep_pept__prot_seq_id_pos_looplink (search_id ASC, reported_peptide_id ASC);

CREATE INDEX srch_rppp_prt_sq_d_ps_lplnk_srch_rppptpptd ON srch_rep_pept__prot_seq_id_pos_looplink (search_reported_peptide_peptide_id ASC);


-- -----------------------------------------------------
-- Table srch_rep_pept__prot_seq_id_unlinked_dimer
-- -----------------------------------------------------
DROP TABLE IF EXISTS srch_rep_pept__prot_seq_id_unlinked_dimer ;

CREATE TABLE  srch_rep_pept__prot_seq_id_unlinked_dimer (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  search_reported_peptide_peptide_id INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch_rppp_prt_sq_d_ps_unlnkd_srch_rppptpptd
    FOREIGN KEY (search_reported_peptide_peptide_id)
    REFERENCES srch_rep_pept__peptide (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX search_rep_pept_idx ON srch_rep_pept__prot_seq_id_unlinked_dimer (search_id ASC, reported_peptide_id ASC);

CREATE INDEX srch_rppp_prt_sq_d_ps_unlnkd_srch_rppptpptd ON srch_rep_pept__prot_seq_id_unlinked_dimer (search_reported_peptide_peptide_id ASC);


-- -----------------------------------------------------
-- Table protein_sequence
-- -----------------------------------------------------
DROP TABLE IF EXISTS protein_sequence ;

CREATE TABLE  protein_sequence (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  sequence MEDIUMTEXT NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX sequence ON protein_sequence (sequence(500) ASC);


-- -----------------------------------------------------
-- Table annotation
-- -----------------------------------------------------
DROP TABLE IF EXISTS annotation ;

CREATE TABLE  annotation (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  taxonomy INT(10) UNSIGNED NULL,
  name VARCHAR(2000) NOT NULL,
  description VARCHAR(2500) NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE INDEX name ON annotation (name(100) ASC);

CREATE INDEX tax_name_desc ON annotation (taxonomy ASC, name(100) ASC, description(100) ASC);


-- -----------------------------------------------------
-- Table search_protein_sequence_annotation
-- -----------------------------------------------------
DROP TABLE IF EXISTS search_protein_sequence_annotation ;

CREATE TABLE  search_protein_sequence_annotation (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  annotation_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT srch_prt_sqnc_annttn__search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT srch_prt_sqnc_annttn_prot_seq_id
    FOREIGN KEY (protein_sequence_id)
    REFERENCES protein_sequence (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT srch_prt_sqnc_annttn_annotation_id
    FOREIGN KEY (annotation_id)
    REFERENCES annotation (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_bin;

CREATE UNIQUE INDEX search_id_prot_seq_id_ann_id ON search_protein_sequence_annotation (search_id ASC, protein_sequence_id ASC, annotation_id ASC);

CREATE INDEX srch_prt_sqnc_annttn_prot_seq_id_idx ON search_protein_sequence_annotation (protein_sequence_id ASC);

CREATE INDEX srch_prt_sqnc_annttn_annotation_id_idx ON search_protein_sequence_annotation (annotation_id ASC);


-- -----------------------------------------------------
-- Table z_mapping__nrseq_prot_id__prot_seq_id
-- -----------------------------------------------------
DROP TABLE IF EXISTS z_mapping__nrseq_prot_id__prot_seq_id ;

CREATE TABLE  z_mapping__nrseq_prot_id__prot_seq_id (
  nrseq_protein_id INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (nrseq_protein_id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_id_creator
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_id_creator ;

CREATE TABLE  proxl_xml_file_import_tracking_id_creator (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_status_values_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_status_values_lookup ;

CREATE TABLE  proxl_xml_file_import_tracking_status_values_lookup (
  id TINYINT UNSIGNED NOT NULL,
  display_text VARCHAR(100) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking ;

CREATE TABLE  proxl_xml_file_import_tracking (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  project_id INT UNSIGNED NOT NULL,
  priority TINYINT NOT NULL,
  auth_user_id INT UNSIGNED NOT NULL,
  status_id TINYINT UNSIGNED NOT NULL,
  remote_user_ip_address VARCHAR(45) NOT NULL,
  marked_for_deletion TINYINT UNSIGNED NOT NULL DEFAULT 0,
  search_name VARCHAR(2000) NULL,
  insert_request_url VARCHAR(255) NOT NULL,
  record_insert_date_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_date_time TIMESTAMP NULL,
  import_start_date_time DATETIME NULL,
  import_end_date_time DATETIME NULL,
  deleted_by_auth_user_id INT NULL,
  deleted_date_time DATETIME NULL,
  PRIMARY KEY (id),
  CONSTRAINT proxl_xml_file_import_tracking_auth_user_id
    FOREIGN KEY (auth_user_id)
    REFERENCES auth_user (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT proxl_xml_file_import_tracking_status_id
    FOREIGN KEY (status_id)
    REFERENCES proxl_xml_file_import_tracking_status_values_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX proxl_xml_file_import_tracking_auth_user_id_idx ON proxl_xml_file_import_tracking (auth_user_id ASC);

CREATE INDEX project_id_status_id ON proxl_xml_file_import_tracking (project_id ASC, status_id ASC);

CREATE INDEX proxl_xml_file_import_tracking_status_id_idx ON proxl_xml_file_import_tracking (status_id ASC);


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_run_sub_status_values_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_run_sub_status_values_lookup ;

CREATE TABLE  proxl_xml_file_import_tracking_run_sub_status_values_lookup (
  id TINYINT UNSIGNED NOT NULL,
  display_text VARCHAR(100) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_run
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_run ;

CREATE TABLE  proxl_xml_file_import_tracking_run (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  proxl_xml_file_import_tracking_id INT UNSIGNED NOT NULL,
  current_run TINYINT UNSIGNED NULL,
  status_id TINYINT UNSIGNED NOT NULL,
  importer_sub_status_id TINYINT UNSIGNED NULL,
  importer_percent_psms_processed TINYINT NULL,
  inserted_search_id INT UNSIGNED NULL,
  import_result_text MEDIUMTEXT NULL,
  data_error_text MEDIUMTEXT NULL,
  start_date_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_date_time TIMESTAMP NULL,
  PRIMARY KEY (id),
  CONSTRAINT prxl_xml_fl_imprt_trkng_run_id
    FOREIGN KEY (proxl_xml_file_import_tracking_id)
    REFERENCES proxl_xml_file_import_tracking (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT prxl_xml_fl_imprt_trkng_run_status_id
    FOREIGN KEY (status_id)
    REFERENCES proxl_xml_file_import_tracking_status_values_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT prxl_xml_fl_imprt_trkng_run_sub_status
    FOREIGN KEY (importer_sub_status_id)
    REFERENCES proxl_xml_file_import_tracking_run_sub_status_values_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX prxl_xml_fl_imprt_trkng_stats_hist_id_idx ON proxl_xml_file_import_tracking_run (proxl_xml_file_import_tracking_id ASC);

CREATE INDEX prxl_xml_fl_imprt_trkng_run_status_id_idx ON proxl_xml_file_import_tracking_run (status_id ASC);

CREATE INDEX prxl_xml_fl_imprt_trkng_run_sub_status_idx ON proxl_xml_file_import_tracking_run (importer_sub_status_id ASC);


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_status_history
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_status_history ;

CREATE TABLE  proxl_xml_file_import_tracking_status_history (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  proxl_xml_file_import_tracking_id INT UNSIGNED NOT NULL,
  status_id TINYINT UNSIGNED NOT NULL,
  status_timestamp TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT prxl_xml_fl_imprt_trkng_stats_hist_id
    FOREIGN KEY (proxl_xml_file_import_tracking_id)
    REFERENCES proxl_xml_file_import_tracking (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX prxl_xml_fl_imprt_trkng_stats_hist_id_idx ON proxl_xml_file_import_tracking_status_history (proxl_xml_file_import_tracking_id ASC);


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_single_file_type_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_single_file_type_lookup ;

CREATE TABLE  proxl_xml_file_import_tracking_single_file_type_lookup (
  id TINYINT UNSIGNED NOT NULL,
  display_text VARCHAR(100) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_single_file_upload_status_lookup
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_single_file_upload_status_lookup ;

CREATE TABLE  proxl_xml_file_import_tracking_single_file_upload_status_lookup (
  id TINYINT UNSIGNED NOT NULL,
  display_text VARCHAR(100) NOT NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_single_file
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_single_file ;

CREATE TABLE  proxl_xml_file_import_tracking_single_file (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  proxl_xml_file_import_tracking_id INT UNSIGNED NOT NULL,
  file_type_id TINYINT UNSIGNED NOT NULL,
  file_upload_status_id TINYINT UNSIGNED NOT NULL,
  filename_in_upload VARCHAR(500) NOT NULL,
  filename_on_disk VARCHAR(500) NOT NULL,
  file_size BIGINT(20) UNSIGNED NULL,
  sha1_sum VARCHAR(255) NULL,
  PRIMARY KEY (id),
  CONSTRAINT prxl_xml_fl_imprt_trkng_sngl_fl_id
    FOREIGN KEY (proxl_xml_file_import_tracking_id)
    REFERENCES proxl_xml_file_import_tracking (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT prxl_xml_fl_imprt_trkng_sngl_fl_type_id
    FOREIGN KEY (file_type_id)
    REFERENCES proxl_xml_file_import_tracking_single_file_type_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT prxl_xml_fl_imprt_trkng_sngl_fl_up_st_id
    FOREIGN KEY (file_upload_status_id)
    REFERENCES proxl_xml_file_import_tracking_single_file_upload_status_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX prxl_xml_fl_imprt_trkng_stats_hist_id_idx ON proxl_xml_file_import_tracking_single_file (proxl_xml_file_import_tracking_id ASC);

CREATE INDEX prxl_xml_fl_imprt_trkng_sngl_fl_type_id_idx ON proxl_xml_file_import_tracking_single_file (file_type_id ASC);

CREATE INDEX prxl_xml_fl_imprt_trkng_sngl_fl_up_st_id_idx ON proxl_xml_file_import_tracking_single_file (file_upload_status_id ASC);


-- -----------------------------------------------------
-- Table proxl_xml_file_import_tracking_sngl_fl_del_web_user
-- -----------------------------------------------------
DROP TABLE IF EXISTS proxl_xml_file_import_tracking_sngl_fl_del_web_user ;

CREATE TABLE  proxl_xml_file_import_tracking_sngl_fl_del_web_user (
  id INT UNSIGNED NOT NULL,
  proxl_xml_file_import_tracking_id INT UNSIGNED NOT NULL,
  file_type_id TINYINT UNSIGNED NOT NULL,
  file_upload_status_id TINYINT UNSIGNED NOT NULL,
  filename_in_upload VARCHAR(500) NOT NULL,
  filename_on_disk VARCHAR(500) NOT NULL,
  sha1_sum VARCHAR(255) NULL,
  file_size INT(20) NULL,
  PRIMARY KEY (id),
  CONSTRAINT prxl_xml_fl_imprt_trkng_sngl_fl_id0
    FOREIGN KEY (proxl_xml_file_import_tracking_id)
    REFERENCES proxl_xml_file_import_tracking (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT prxl_xml_fl_imprt_trkng_sngl_fl_type_id0
    FOREIGN KEY (file_type_id)
    REFERENCES proxl_xml_file_import_tracking_single_file_type_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT prxl_xml_fl_imprt_trkng_sngl_fl_up_st_id0
    FOREIGN KEY (file_upload_status_id)
    REFERENCES proxl_xml_file_import_tracking_single_file_upload_status_lookup (id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX prxl_xml_fl_imprt_trkng_stats_hist_id_idx ON proxl_xml_file_import_tracking_sngl_fl_del_web_user (proxl_xml_file_import_tracking_id ASC);

CREATE INDEX prxl_xml_fl_imprt_trkng_sngl_fl_type_id_idx ON proxl_xml_file_import_tracking_sngl_fl_del_web_user (file_type_id ASC);

CREATE INDEX prxl_xml_fl_imprt_trkng_sngl_fl_up_st_id_idx ON proxl_xml_file_import_tracking_sngl_fl_del_web_user (file_upload_status_id ASC);


-- -----------------------------------------------------
-- Table sequence_coverage_cache
-- -----------------------------------------------------
DROP TABLE IF EXISTS sequence_coverage_cache ;

CREATE TABLE  sequence_coverage_cache (
  search_param_hash_code INT(11) NOT NULL,
  search_param_hash_idx INT(11) NOT NULL,
  search_id INT(11) UNSIGNED NOT NULL,
  search_param_json TEXT NOT NULL,
  sequence_coverage_json TEXT NOT NULL,
  PRIMARY KEY (search_param_hash_code, search_param_hash_idx),
  CONSTRAINT fk_sequence_coverage_srch_id
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE INDEX fk_sequence_coverage_srch_id_idx ON sequence_coverage_cache (search_id ASC);


-- -----------------------------------------------------
-- Table peptide_protein_position
-- -----------------------------------------------------
DROP TABLE IF EXISTS peptide_protein_position ;

CREATE TABLE  peptide_protein_position (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  reported_peptide_id INT UNSIGNED NOT NULL,
  peptide_id INT UNSIGNED NOT NULL,
  protein_sequence_id INT UNSIGNED NOT NULL,
  protein_start_position INT UNSIGNED NOT NULL,
  protein_end_position INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT peptide_protein_position_search_id
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX peptide_protein_position_search_id_idx ON peptide_protein_position (search_id ASC);

CREATE INDEX search_id_protein_seq_id ON peptide_protein_position (search_id ASC, protein_sequence_id ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
