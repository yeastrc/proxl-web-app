SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

DROP SCHEMA IF EXISTS `proxl` ;
CREATE SCHEMA IF NOT EXISTS `proxl` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `proxl` ;

-- -----------------------------------------------------
-- Table `auth_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `auth_user` ;

CREATE TABLE IF NOT EXISTS `auth_user` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `password_hashed` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `user_access_level` SMALLINT NULL,
  `last_login` DATETIME NULL,
  `last_login_ip` VARCHAR(255) NULL,
  `last_password_change` DATETIME NULL,
  `enabled` TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `auth_username_UNIQUE` ON `auth_user` (`username` ASC);

CREATE UNIQUE INDEX `email_UNIQUE` ON `auth_user` (`email` ASC);


-- -----------------------------------------------------
-- Table `auth_shared_object`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `auth_shared_object` ;

CREATE TABLE IF NOT EXISTS `auth_shared_object` (
  `shared_object_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `public_access_code_enabled` TINYINT NOT NULL DEFAULT 0,
  `public_access_code` VARCHAR(255) NULL,
  PRIMARY KEY (`shared_object_id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `public_access_code_UNIQUE` ON `auth_shared_object` (`public_access_code` ASC);


-- -----------------------------------------------------
-- Table `xl_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xl_user` ;

CREATE TABLE IF NOT EXISTS `xl_user` (
  `auth_user_id` INT UNSIGNED NOT NULL,
  `first_name` VARCHAR(255) NOT NULL,
  `last_name` VARCHAR(255) NOT NULL,
  `organization` VARCHAR(2000) NULL,
  PRIMARY KEY (`auth_user_id`),
  CONSTRAINT `fk_auth_user_id`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `project`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `project` ;

CREATE TABLE IF NOT EXISTS `project` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `auth_shareable_object_id` INT UNSIGNED NOT NULL,
  `title` VARCHAR(255) NULL,
  `abstract` TEXT NULL,
  `enabled` TINYINT UNSIGNED NOT NULL DEFAULT 1,
  `marked_for_deletion` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `project_locked` TINYINT NOT NULL DEFAULT 0,
  `public_access_level` SMALLINT NULL,
  `public_access_locked` TINYINT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_auth_shareable_object_id`
    FOREIGN KEY (`auth_shareable_object_id`)
    REFERENCES `auth_shared_object` (`shared_object_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_auth_shareable_object_id_idx` ON `project` (`auth_shareable_object_id` ASC);


-- -----------------------------------------------------
-- Table `search`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search` ;

CREATE TABLE IF NOT EXISTS `search` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `path` VARCHAR(2000) NOT NULL,
  `fasta_filename` VARCHAR(2000) NOT NULL,
  `load_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` VARCHAR(2000) NULL,
  `project_id` INT UNSIGNED NOT NULL,
  `insert_complete` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `directory_name` VARCHAR(255) NOT NULL,
  `search_program` VARCHAR(200) NULL,
  `display_order` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_project_id`
    FOREIGN KEY (`project_id`)
    REFERENCES `project` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_project_id_idx` ON `search` (`project_id` ASC);


-- -----------------------------------------------------
-- Table `reported_peptide`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reported_peptide` ;

CREATE TABLE IF NOT EXISTS `reported_peptide` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sequence` VARCHAR(2000) NOT NULL,
  `N` CHAR(1) NULL DEFAULT NULL,
  `C` CHAR(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `sequence` ON `reported_peptide` (`sequence`(20) ASC);

CREATE INDEX `N` ON `reported_peptide` (`N` ASC);

CREATE INDEX `C` ON `reported_peptide` (`C` ASC);


-- -----------------------------------------------------
-- Table `scan_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `scan_file` ;

CREATE TABLE IF NOT EXISTS `scan_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NULL,
  `sha1sum` VARCHAR(255) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `filename` ON `scan_file` (`filename` ASC, `sha1sum` ASC);


-- -----------------------------------------------------
-- Table `scan`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `scan` ;

CREATE TABLE IF NOT EXISTS `scan` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `scan_file_id` INT UNSIGNED NOT NULL,
  `start_scan_number` INT UNSIGNED NOT NULL,
  `end_scan_number` INT UNSIGNED NULL,
  `level` SMALLINT UNSIGNED NOT NULL,
  `preMZ` DECIMAL(18,9) NULL,
  `precursor_scan_number` INT NOT NULL,
  `precursor_scan_id` INT UNSIGNED NULL,
  `retention_time` DECIMAL(18,9) NULL,
  `peak_count` INT NOT NULL,
  `fragmentation_type` VARCHAR(45) NULL,
  `is_centroid` CHAR(1) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_scan_scan_file_id`
    FOREIGN KEY (`scan_file_id`)
    REFERENCES `scan_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `start_scan_number` ON `scan` (`start_scan_number` ASC, `scan_file_id` ASC);

CREATE INDEX `fk_scan_scan_file_id_idx` ON `scan` (`scan_file_id` ASC);


-- -----------------------------------------------------
-- Table `psm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `psm` ;

CREATE TABLE IF NOT EXISTS `psm` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `scan_id` INT UNSIGNED NOT NULL,
  `q_value` DOUBLE NULL,
  `type` ENUM('looplink','crosslink','unlinked','dimer') CHARACTER SET 'latin1' NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `charge` SMALLINT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `psm_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `psm_ibfk_2`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `psm_scan_id_fk`
    FOREIGN KEY (`scan_id`)
    REFERENCES `scan` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE INDEX `q_value` ON `psm` (`q_value` ASC);

CREATE INDEX `perc_run_id` ON `psm` (`search_id` ASC);

CREATE INDEX `peplynx_peptide_id` ON `psm` (`reported_peptide_id` ASC);

CREATE INDEX `type` ON `psm` (`type` ASC);

CREATE INDEX `psm_scan_id_fk_idx` ON `psm` (`scan_id` ASC);

CREATE INDEX `psm__search_id_type_q_value_idx` ON `psm` (`search_id` ASC, `type` ASC, `q_value` ASC);

CREATE INDEX `psm__search_id_rep_pep_id_q_value_idx` ON `psm` (`search_id` ASC, `reported_peptide_id` ASC, `q_value` ASC);


-- -----------------------------------------------------
-- Table `peptide`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `peptide` ;

CREATE TABLE IF NOT EXISTS `peptide` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `sequence` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `sequence` ON `peptide` (`sequence`(20) ASC);


-- -----------------------------------------------------
-- Table `matched_peptide`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `matched_peptide` ;

CREATE TABLE IF NOT EXISTS `matched_peptide` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `peptide_id` INT UNSIGNED NOT NULL,
  `psm_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `matched_peptide_peptide_id_fk`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `matched_peptide_psm_id_fk`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `matched_peptide_peptide_id_fk_idx` ON `matched_peptide` (`peptide_id` ASC);

CREATE INDEX `matched_peptide_psm_id_fk_idx` ON `matched_peptide` (`psm_id` ASC);


-- -----------------------------------------------------
-- Table `linker`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `linker` ;

CREATE TABLE IF NOT EXISTS `linker` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `abbr` VARCHAR(255) NOT NULL,
  `name` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE UNIQUE INDEX `abbr` ON `linker` (`abbr` ASC);


-- -----------------------------------------------------
-- Table `crosslink`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `crosslink` ;

CREATE TABLE IF NOT EXISTS `crosslink` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `protein_1_position` INT(10) UNSIGNED NOT NULL,
  `protein_2_position` INT(10) UNSIGNED NOT NULL,
  `peptide_1_id` INT(10) UNSIGNED NOT NULL,
  `peptide_2_id` INT(10) UNSIGNED NOT NULL,
  `peptide_1_position` INT(10) UNSIGNED NOT NULL,
  `peptide_2_position` INT(10) UNSIGNED NOT NULL,
  `peptide_1_matched_peptide_id` INT UNSIGNED NOT NULL,
  `peptide_2_matched_peptide_id` INT UNSIGNED NOT NULL,
  `linker_mass` DECIMAL(18,9) NOT NULL,
  `linker_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `crosslink_ibfk_1`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `crosslink_ibfk_2`
    FOREIGN KEY (`peptide_1_id`)
    REFERENCES `peptide` (`id`),
  CONSTRAINT `crosslink_ibfk_3`
    FOREIGN KEY (`peptide_2_id`)
    REFERENCES `peptide` (`id`),
  CONSTRAINT `crosslink_ibfk_4`
    FOREIGN KEY (`peptide_1_matched_peptide_id`)
    REFERENCES `matched_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `crosslink_ibfk_5`
    FOREIGN KEY (`peptide_2_matched_peptide_id`)
    REFERENCES `matched_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `crosslink_linker_id_fk`
    FOREIGN KEY (`linker_id`)
    REFERENCES `linker` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `nrseq_id_1` ON `crosslink` (`nrseq_id_1` ASC);

CREATE INDEX `nrseq_id_2` ON `crosslink` (`nrseq_id_2` ASC);

CREATE INDEX `protein_1_position` ON `crosslink` (`protein_1_position` ASC);

CREATE INDEX `protein_2_position` ON `crosslink` (`protein_2_position` ASC);

CREATE INDEX `psm_id` ON `crosslink` (`psm_id` ASC);

CREATE INDEX `peptide_1_id` ON `crosslink` (`peptide_1_id` ASC, `peptide_1_position` ASC);

CREATE INDEX `peptide_2_id` ON `crosslink` (`peptide_2_id` ASC, `peptide_2_position` ASC);

CREATE INDEX `crosslink_ibfk_4_idx` ON `crosslink` (`peptide_1_matched_peptide_id` ASC);

CREATE INDEX `crosslink_ibfk_5_idx` ON `crosslink` (`peptide_2_matched_peptide_id` ASC);

CREATE INDEX `crosslink_linker_id_fj_idx` ON `crosslink` (`linker_id` ASC);


-- -----------------------------------------------------
-- Table `dimer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dimer` ;

CREATE TABLE IF NOT EXISTS `dimer` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `peptide_1_id` INT(10) UNSIGNED NOT NULL,
  `peptide_2_id` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `dimer_ibfk_1`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `dimer_ibfk_2`
    FOREIGN KEY (`peptide_1_id`)
    REFERENCES `peptide` (`id`),
  CONSTRAINT `dimer_ibfk_3`
    FOREIGN KEY (`peptide_2_id`)
    REFERENCES `peptide` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `nrseq_id_1` ON `dimer` (`nrseq_id_1` ASC);

CREATE INDEX `nrseq_id_2` ON `dimer` (`nrseq_id_2` ASC);

CREATE INDEX `peptide_1_id` ON `dimer` (`peptide_1_id` ASC);

CREATE INDEX `peptide_2_id` ON `dimer` (`peptide_2_id` ASC);

CREATE INDEX `psm_id` ON `dimer` (`psm_id` ASC);


-- -----------------------------------------------------
-- Table `dynamic_mod`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `dynamic_mod` ;

CREATE TABLE IF NOT EXISTS `dynamic_mod` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `matched_peptide_id` INT(10) UNSIGNED NOT NULL,
  `position` INT(10) UNSIGNED NOT NULL,
  `mass` DOUBLE NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `dynamic_mod_matched_peptide_id_fk`
    FOREIGN KEY (`matched_peptide_id`)
    REFERENCES `matched_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `dynamic_mod_matched_peptide_id_fk_idx` ON `dynamic_mod` (`matched_peptide_id` ASC);


-- -----------------------------------------------------
-- Table `linker_monolink_mass`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `linker_monolink_mass` ;

CREATE TABLE IF NOT EXISTS `linker_monolink_mass` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `linker_id` INT(10) UNSIGNED NOT NULL,
  `mass` DOUBLE NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `linker_monolink_mass_ibfk_1`
    FOREIGN KEY (`linker_id`)
    REFERENCES `linker` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE UNIQUE INDEX `linker_monolink_mass__linker_id_mass_Unique_idx` ON `linker_monolink_mass` (`linker_id` ASC, `mass` ASC);

CREATE INDEX `linker_id` ON `linker_monolink_mass` (`linker_id` ASC);


-- -----------------------------------------------------
-- Table `looplink`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `looplink` ;

CREATE TABLE IF NOT EXISTS `looplink` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position_1` INT(10) UNSIGNED NOT NULL,
  `protein_position_2` INT(10) UNSIGNED NOT NULL,
  `peptide_id` INT(10) UNSIGNED NOT NULL,
  `peptide_position_1` INT(10) UNSIGNED NOT NULL,
  `peptide_position_2` INT(10) UNSIGNED NOT NULL,
  `linker_mass` DECIMAL(18,9) NOT NULL,
  `linker_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `looplink_ibfk_1`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `looplink_ibfk_2`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`),
  CONSTRAINT `looplink_linker_id_fk`
    FOREIGN KEY (`linker_id`)
    REFERENCES `linker` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `nrseq_id` ON `looplink` (`nrseq_id` ASC);

CREATE INDEX `psm_id` ON `looplink` (`psm_id` ASC);

CREATE INDEX `protein_position_1` ON `looplink` (`protein_position_1` ASC);

CREATE INDEX `protein_position_2` ON `looplink` (`protein_position_2` ASC);

CREATE INDEX `peptide_id` ON `looplink` (`peptide_id` ASC);

CREATE INDEX `looplink_linker_id_fk_idx` ON `looplink` (`linker_id` ASC);


-- -----------------------------------------------------
-- Table `monolink`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `monolink` ;

CREATE TABLE IF NOT EXISTS `monolink` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position` INT(10) UNSIGNED NOT NULL,
  `peptide_id` INT(10) UNSIGNED NOT NULL,
  `peptide_position` INT(10) UNSIGNED NOT NULL,
  `linker_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `monolink_ibfk_1`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `monolink_ibfk_2`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`),
  CONSTRAINT `monolink_linker_id_fk`
    FOREIGN KEY (`linker_id`)
    REFERENCES `linker` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `nrseq_id` ON `monolink` (`nrseq_id` ASC);

CREATE INDEX `psm_id` ON `monolink` (`psm_id` ASC);

CREATE INDEX `protein_position` ON `monolink` (`protein_position` ASC);

CREATE INDEX `peptide_id` ON `monolink` (`peptide_id` ASC);

CREATE INDEX `monolink_linker_id_fk_idx` ON `monolink` (`linker_id` ASC);


-- -----------------------------------------------------
-- Table `nrseq_database_peptide_protein`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `nrseq_database_peptide_protein` ;

CREATE TABLE IF NOT EXISTS `nrseq_database_peptide_protein` (
  `nrseq_database_id` INT(10) UNSIGNED NOT NULL,
  `peptide_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `is_unique` CHAR(1) NULL DEFAULT NULL,
  PRIMARY KEY (`nrseq_database_id`, `peptide_id`, `nrseq_id`),
  CONSTRAINT `nrseq_database_peptide_protein_ibfk_1`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `peptide_id` ON `nrseq_database_peptide_protein` (`peptide_id` ASC);

CREATE INDEX `nrseq_id` ON `nrseq_database_peptide_protein` (`nrseq_id` ASC);

CREATE INDEX `is_unique` ON `nrseq_database_peptide_protein` (`is_unique` ASC);


-- -----------------------------------------------------
-- Table `pdb_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `pdb_file` ;

CREATE TABLE IF NOT EXISTS `pdb_file` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(2000) NULL DEFAULT NULL,
  `content` LONGTEXT NOT NULL,
  `upload_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `uploaded_by` INT(10) UNSIGNED NOT NULL,
  `project_id` INT(10) UNSIGNED NOT NULL,
  `visibility` VARCHAR(255) NOT NULL DEFAULT 'project',
  PRIMARY KEY (`id`),
  CONSTRAINT `pdb_file_ibfk_1`
    FOREIGN KEY (`uploaded_by`)
    REFERENCES `auth_user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `pdb_file_ibfk_2`
    FOREIGN KEY (`project_id`)
    REFERENCES `project` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE INDEX `uploaded_by` ON `pdb_file` (`uploaded_by` ASC);

CREATE INDEX `project_id` ON `pdb_file` (`project_id` ASC);


-- -----------------------------------------------------
-- Table `pdb_alignment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `pdb_alignment` ;

CREATE TABLE IF NOT EXISTS `pdb_alignment` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `pdb_file_id` INT(10) UNSIGNED NOT NULL,
  `chain_id` CHAR(1) NULL DEFAULT NULL,
  `aligned_pdb_sequence` VARCHAR(10000) NULL DEFAULT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `aligned_nrseq_sequence` VARCHAR(10000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `pdb_alignment_ibfk_1`
    FOREIGN KEY (`pdb_file_id`)
    REFERENCES `pdb_file` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE UNIQUE INDEX `nrseq_id` ON `pdb_alignment` (`nrseq_id` ASC, `pdb_file_id` ASC, `chain_id` ASC);

CREATE INDEX `pdb_file_id` ON `pdb_alignment` (`pdb_file_id` ASC, `chain_id` ASC);


-- -----------------------------------------------------
-- Table `search_comment`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_comment` ;

CREATE TABLE IF NOT EXISTS `search_comment` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `comment` VARCHAR(2000) NOT NULL,
  `commentTimestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `auth_user_id` INT UNSIGNED NULL,
  `commentCreatedTimestamp` TIMESTAMP NULL,
  `created_auth_user_id` INT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_comment_user_fk`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`),
  CONSTRAINT `perc_run_comment_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `search_id` ON `search_comment` (`search_id` ASC, `commentTimestamp` ASC);

CREATE INDEX `search_comment_user_fk_idx` ON `search_comment` (`auth_user_id` ASC);


-- -----------------------------------------------------
-- Table `search_crosslink_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_crosslink_lookup` ;

CREATE TABLE IF NOT EXISTS `search_crosslink_lookup` (
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `protein_1_position` INT(10) UNSIGNED NOT NULL,
  `protein_2_position` INT(10) UNSIGNED NOT NULL,
  `bestPSMQValue` DOUBLE NOT NULL,
  `bestPeptideQValue` DOUBLE NULL,
  PRIMARY KEY (`search_id`, `nrseq_id_1`, `nrseq_id_2`, `protein_1_position`, `protein_2_position`),
  CONSTRAINT `search_crosslink_lookup_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `bestPSMQValue` ON `search_crosslink_lookup` (`bestPSMQValue` ASC);

CREATE INDEX `bestPeptideQValue` ON `search_crosslink_lookup` (`bestPeptideQValue` ASC);


-- -----------------------------------------------------
-- Table `search_looplink_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_looplink_lookup` ;

CREATE TABLE IF NOT EXISTS `search_looplink_lookup` (
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position_1` INT(10) UNSIGNED NOT NULL,
  `protein_position_2` INT(10) UNSIGNED NOT NULL,
  `bestPSMQValue` DOUBLE NOT NULL,
  `bestPeptideQValue` DOUBLE NULL,
  PRIMARY KEY (`search_id`, `nrseq_id`, `protein_position_1`, `protein_position_2`),
  CONSTRAINT `search_looplink_lookup_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `bestPSMQValue` ON `search_looplink_lookup` (`bestPSMQValue` ASC);

CREATE INDEX `bestPeptideQValue` ON `search_looplink_lookup` (`bestPeptideQValue` ASC);


-- -----------------------------------------------------
-- Table `search_monolink_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_monolink_lookup` ;

CREATE TABLE IF NOT EXISTS `search_monolink_lookup` (
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position` INT(10) UNSIGNED NOT NULL,
  `bestPSMQValue` DOUBLE NOT NULL,
  `bestPeptideQValue` DOUBLE NULL,
  PRIMARY KEY (`search_id`, `nrseq_id`, `protein_position`),
  CONSTRAINT `search_monolink_lookup_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `bestPSMQValue` ON `search_monolink_lookup` (`bestPSMQValue` ASC);

CREATE INDEX `bestPeptideQValue` ON `search_monolink_lookup` (`bestPeptideQValue` ASC);


-- -----------------------------------------------------
-- Table `search_reported_peptide`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_reported_peptide` ;

CREATE TABLE IF NOT EXISTS `search_reported_peptide` (
  `search_id` INT(10) UNSIGNED NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `q_value` DOUBLE NULL,
  PRIMARY KEY (`search_id`, `reported_peptide_id`),
  CONSTRAINT `search_reported_peptide_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_reported_peptide_ibfk_2`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `q_value` ON `search_reported_peptide` (`q_value` ASC);

CREATE INDEX `reported_peptide_id` ON `search_reported_peptide` (`reported_peptide_id` ASC);

CREATE INDEX `search_reported_peptide__search_id_q_value_idx` ON `search_reported_peptide` (`search_id` ASC, `q_value` ASC);


-- -----------------------------------------------------
-- Table `search_protein_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_protein_lookup` ;

CREATE TABLE IF NOT EXISTS `search_protein_lookup` (
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `bestCrosslinkPSMQValue` DOUBLE NULL DEFAULT NULL,
  `bestCrosslinkPeptideQValue` DOUBLE NULL DEFAULT NULL,
  `bestLooplinkPSMQValue` DOUBLE NULL DEFAULT NULL,
  `bestLooplinkPeptideQValue` DOUBLE NULL DEFAULT NULL,
  `bestMonolinkPSMQValue` DOUBLE NULL DEFAULT NULL,
  `bestMonolinkPeptideQValue` DOUBLE NULL DEFAULT NULL,
  `bestDimerPSMQValue` DOUBLE NULL DEFAULT NULL,
  `bestDimerPeptideQValue` DOUBLE NULL DEFAULT NULL,
  `bestUnlinkedPSMQValue` DOUBLE NULL DEFAULT NULL,
  `bestUnlinkedPeptideQValue` DOUBLE NULL DEFAULT NULL,
  PRIMARY KEY (`search_id`, `nrseq_id`),
  CONSTRAINT `search_protein_lookup_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `nrseq_id` ON `search_protein_lookup` (`nrseq_id` ASC);

CREATE INDEX `bestCrosslinkPSMQValue` ON `search_protein_lookup` (`bestCrosslinkPSMQValue` ASC);

CREATE INDEX `bestCrosslinkPeptideQValue` ON `search_protein_lookup` (`bestCrosslinkPeptideQValue` ASC);

CREATE INDEX `bestLooplinkPSMQValue` ON `search_protein_lookup` (`bestLooplinkPSMQValue` ASC);

CREATE INDEX `bestLooplinkPeptideQValue` ON `search_protein_lookup` (`bestLooplinkPeptideQValue` ASC);

CREATE INDEX `bestMonolinkPSMQValue` ON `search_protein_lookup` (`bestMonolinkPSMQValue` ASC);

CREATE INDEX `bestMonolinkPeptideQValue` ON `search_protein_lookup` (`bestMonolinkPeptideQValue` ASC);

CREATE INDEX `bestUnlinkedPSMQValue` ON `search_protein_lookup` (`bestUnlinkedPSMQValue` ASC);

CREATE INDEX `bestUnlinkedPeptideQValue` ON `search_protein_lookup` (`bestUnlinkedPeptideQValue` ASC);

CREATE INDEX `bestDimerPSMQValue` ON `search_protein_lookup` (`bestDimerPSMQValue` ASC);

CREATE INDEX `bestUnlinkedPeptideQValue_2` ON `search_protein_lookup` (`bestUnlinkedPeptideQValue` ASC);


-- -----------------------------------------------------
-- Table `psm_peptide`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `psm_peptide` ;

CREATE TABLE IF NOT EXISTS `psm_peptide` (
  `psm_id` INT(10) UNSIGNED NOT NULL,
  `peptide_id` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`psm_id`, `peptide_id`),
  CONSTRAINT `psm_peptide_ibfk_1`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `psm_peptide_ibfk_2`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE INDEX `peptide_id` ON `psm_peptide` (`peptide_id` ASC);


-- -----------------------------------------------------
-- Table `taxonomy`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `taxonomy` ;

CREATE TABLE IF NOT EXISTS `taxonomy` (
  `id` INT(10) UNSIGNED NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE INDEX `name` ON `taxonomy` (`name` ASC);


-- -----------------------------------------------------
-- Table `note`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `note` ;

CREATE TABLE IF NOT EXISTS `note` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `project_id` INT UNSIGNED NOT NULL,
  `auth_user_id_created` INT UNSIGNED NOT NULL,
  `created_date_time` DATETIME NOT NULL,
  `auth_user_id_last_updated` INT UNSIGNED NOT NULL,
  `last_updated_date_time` DATETIME NOT NULL,
  `note_text` TEXT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_note_auth_user_id`
    FOREIGN KEY (`auth_user_id_created`)
    REFERENCES `auth_user` (`id`)
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_note_project_id`
    FOREIGN KEY (`project_id`)
    REFERENCES `project` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_project_id_idx` ON `note` (`project_id` ASC);

CREATE INDEX `fk_auth_user_id_idx` ON `note` (`auth_user_id_created` ASC);


-- -----------------------------------------------------
-- Table `auth_forgot_password_tracking`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `auth_forgot_password_tracking` ;

CREATE TABLE IF NOT EXISTS `auth_forgot_password_tracking` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `auth_user_id` INT UNSIGNED NOT NULL,
  `create_date` DATETIME NOT NULL,
  `used_date` DATETIME NULL,
  `forgot_password_tracking_code` VARCHAR(255) NOT NULL,
  `submit_ip` VARCHAR(255) NOT NULL,
  `use_ip` VARCHAR(255) NULL,
  `code_replaced_by_newer` TINYINT(1) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `forgot_pwd_trk_auth_user_id_fk`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `forgot_password_tracking_code_UNIQUE` ON `auth_forgot_password_tracking` (`forgot_password_tracking_code` ASC);

CREATE INDEX `forgot_pwd_trk_auth_user_id_fk_idx` ON `auth_forgot_password_tracking` (`auth_user_id` ASC);


-- -----------------------------------------------------
-- Table `auth_shared_object_users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `auth_shared_object_users` ;

CREATE TABLE IF NOT EXISTS `auth_shared_object_users` (
  `shared_object_id` INT UNSIGNED NOT NULL,
  `user_id` INT UNSIGNED NOT NULL,
  `access_level` SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (`shared_object_id`, `user_id`),
  CONSTRAINT `fk_shared_objects_object_id`
    FOREIGN KEY (`shared_object_id`)
    REFERENCES `auth_shared_object` (`shared_object_id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_shared_objects_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `auth_user` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `idx_shared_objects_user_id` ON `auth_shared_object_users` (`user_id` ASC);


-- -----------------------------------------------------
-- Table `auth_user_invite_tracking`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `auth_user_invite_tracking` ;

CREATE TABLE IF NOT EXISTS `auth_user_invite_tracking` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `submitting_auth_user_id` INT UNSIGNED NOT NULL,
  `submit_ip` VARCHAR(255) NOT NULL,
  `invite_tracking_code` VARCHAR(255) NOT NULL,
  `invited_user_email` VARCHAR(255) NOT NULL,
  `invited_user_access_level` SMALLINT NOT NULL,
  `invited_shared_object_id` INT UNSIGNED NULL,
  `invite_create_date` DATETIME NOT NULL,
  `invite_used` TINYINT NULL,
  `invite_used_date` DATETIME NULL,
  `invite_used_auth_user_id` INT UNSIGNED NULL,
  `use_ip` VARCHAR(255) NULL,
  `code_replaced_by_newer` TINYINT NULL,
  `invite_revoked` TINYINT NULL,
  `revoking_auth_user_id` INT UNSIGNED NULL,
  `revoked_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `user_invite_trk_auth_user_id_fk`
    FOREIGN KEY (`submitting_auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `user_invite_trk_revoking_auth_user_id`
    FOREIGN KEY (`revoking_auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_invite_trk_shared_object_id_fk`
    FOREIGN KEY (`invited_shared_object_id`)
    REFERENCES `auth_shared_object` (`shared_object_id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `user_invite_trk_submitting_auth_user_id_fk`
    FOREIGN KEY (`submitting_auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_invite_trk_used_auth_user_id_fk`
    FOREIGN KEY (`invite_used_auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `invite_tracking_code_UNIQUE` ON `auth_user_invite_tracking` (`invite_tracking_code` ASC);

CREATE INDEX `forgot_pwd_trk_auth_user_id_fk_idx` ON `auth_user_invite_tracking` (`submitting_auth_user_id` ASC);

CREATE INDEX `user_invite_trk_revoking_auth_user_id_idx` ON `auth_user_invite_tracking` (`revoking_auth_user_id` ASC);

CREATE INDEX `user_invite_trk_shared_object_id_fk_idx` ON `auth_user_invite_tracking` (`invited_shared_object_id` ASC);

CREATE INDEX `user_invite_trk_used_auth_user_id_fk_idx` ON `auth_user_invite_tracking` (`invite_used_auth_user_id` ASC);


-- -----------------------------------------------------
-- Table `xl_user_access_level_label_description`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xl_user_access_level_label_description` ;

CREATE TABLE IF NOT EXISTS `xl_user_access_level_label_description` (
  `xl_user_access_level_numeric_value` INT UNSIGNED NOT NULL,
  `label` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NULL,
  PRIMARY KEY (`xl_user_access_level_numeric_value`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `scan_file_header`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `scan_file_header` ;

CREATE TABLE IF NOT EXISTS `scan_file_header` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `scan_file_id` INT UNSIGNED NOT NULL,
  `header` VARCHAR(255) NOT NULL,
  `value` TEXT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_scan_file_header_scan_file_id`
    FOREIGN KEY (`scan_file_id`)
    REFERENCES `scan_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_scan_file_header_scan_file_id_idx` ON `scan_file_header` (`scan_file_id` ASC);


-- -----------------------------------------------------
-- Table `scan_spectrum_data`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `scan_spectrum_data` ;

CREATE TABLE IF NOT EXISTS `scan_spectrum_data` (
  `scan_id` INT UNSIGNED NOT NULL,
  `spectrum_data` LONGBLOB NULL,
  PRIMARY KEY (`scan_id`),
  CONSTRAINT `fk_scan_spectrum_data_scan_id`
    FOREIGN KEY (`scan_id`)
    REFERENCES `scan` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `kojak_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojak_file` ;

CREATE TABLE IF NOT EXISTS `kojak_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  `sha1sum` VARCHAR(255) NOT NULL,
  `kojak_program_version` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE INDEX `filename` ON `kojak_file` (`filename` ASC);


-- -----------------------------------------------------
-- Table `kojak_psm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojak_psm` ;

CREATE TABLE IF NOT EXISTS `kojak_psm` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kojak_file_id` INT UNSIGNED NOT NULL,
  `scan_number` INT NOT NULL,
  `obs_mass` VARCHAR(200) NOT NULL,
  `charge` SMALLINT NOT NULL,
  `psm_mass` VARCHAR(200) NOT NULL,
  `ppm_error` VARCHAR(200) NOT NULL,
  `score` VARCHAR(200) NOT NULL,
  `dscore` VARCHAR(200) NOT NULL,
  `pep_diff` VARCHAR(200) NULL,
  `peptide_1` VARCHAR(2000) NOT NULL,
  `link_1` VARCHAR(200) NOT NULL,
  `protein_1` VARCHAR(2000) NOT NULL,
  `peptide_2` VARCHAR(2000) NOT NULL,
  `link_2` VARCHAR(200) NOT NULL,
  `protein_2` VARCHAR(2000) NOT NULL,
  `linker_mass` VARCHAR(200) NOT NULL,
  `corr` VARCHAR(200) NULL,
  `label` VARCHAR(200) NULL,
  `norm_rank` VARCHAR(200) NULL,
  `mod_mass` VARCHAR(200) NULL,
  `ret_time` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_kojak_file_id_kojak_psm`
    FOREIGN KEY (`kojak_file_id`)
    REFERENCES `kojak_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_kojak_file_id_kojak_psm_idx` ON `kojak_psm` (`kojak_file_id` ASC);

CREATE INDEX `kojak_psm_scan_number_idx` ON `kojak_psm` (`scan_number` ASC, `kojak_file_id` ASC);


-- -----------------------------------------------------
-- Table `kojakpsm_psm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojakpsm_psm` ;

CREATE TABLE IF NOT EXISTS `kojakpsm_psm` (
  `kojakpsm_id` INT UNSIGNED NOT NULL,
  `psm_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`kojakpsm_id`, `psm_id`),
  CONSTRAINT `fk_kojakpsm_psm_kojakpsm_id`
    FOREIGN KEY (`kojakpsm_id`)
    REFERENCES `kojak_psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_kojakpsm_psm_psm_id`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `fk_kojakpsm_psm_psm_id_idx` ON `kojakpsm_psm` (`psm_id` ASC);


-- -----------------------------------------------------
-- Table `kojak_file_scan_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojak_file_scan_file` ;

CREATE TABLE IF NOT EXISTS `kojak_file_scan_file` (
  `kojak_file_id` INT UNSIGNED NOT NULL,
  `scan_file_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`kojak_file_id`, `scan_file_id`),
  CONSTRAINT `fk_kojak_file_scan_file_kojak_file_id`
    FOREIGN KEY (`kojak_file_id`)
    REFERENCES `kojak_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fk_kojak_file_scan_file_scan_file_id`
    FOREIGN KEY (`scan_file_id`)
    REFERENCES `scan_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_kojak_file_scan_file_scan_file_id_idx` ON `kojak_file_scan_file` (`scan_file_id` ASC);


-- -----------------------------------------------------
-- Table `kojak_conf_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojak_conf_file` ;

CREATE TABLE IF NOT EXISTS `kojak_conf_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kojak_file_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_kojak_conf_file_kojak_file_id`
    FOREIGN KEY (`kojak_file_id`)
    REFERENCES `kojak_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `filename` ON `kojak_conf_file` (`filename` ASC);

CREATE INDEX `fk_kojak_conf_file_kojak_file_id_idx` ON `kojak_conf_file` (`kojak_file_id` ASC);


-- -----------------------------------------------------
-- Table `kojak_conf_file_key_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojak_conf_file_key_value` ;

CREATE TABLE IF NOT EXISTS `kojak_conf_file_key_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kojak_conf_file_id` INT UNSIGNED NOT NULL,
  `kojak_conf_file_key` VARCHAR(255) NOT NULL,
  `value` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_kojak_conf_file_key_value_kojak_conf_file_id`
    FOREIGN KEY (`kojak_conf_file_id`)
    REFERENCES `kojak_conf_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_kojak_conf_file_key_value_kojak_conf_file_id_idx` ON `kojak_conf_file_key_value` (`kojak_conf_file_id` ASC);


-- -----------------------------------------------------
-- Table `kojak_conf_file_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `kojak_conf_file_line` ;

CREATE TABLE IF NOT EXISTS `kojak_conf_file_line` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `kojak_conf_file_id` INT UNSIGNED NOT NULL,
  `line` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_kojak_conf_file_line_kojak_conf_file_id`
    FOREIGN KEY (`kojak_conf_file_id`)
    REFERENCES `kojak_conf_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `fk_kojak_conf_file_line_kojak_conf_file_id_idx` ON `kojak_conf_file_line` (`kojak_conf_file_id` ASC);


-- -----------------------------------------------------
-- Table `percolator_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `percolator_file` ;

CREATE TABLE IF NOT EXISTS `percolator_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  `sha1sum` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `percolator_file_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `percolator_file_perc_run_id_fk_idx` ON `percolator_file` (`search_id` ASC);


-- -----------------------------------------------------
-- Table `percolator_psm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `percolator_psm` ;

CREATE TABLE IF NOT EXISTS `percolator_psm` (
  `psm_id` INT UNSIGNED NOT NULL,
  `percolator_file_id` INT UNSIGNED NULL,
  `q_value` DOUBLE NOT NULL,
  `svm_score` DOUBLE NULL,
  `calc_mass` DOUBLE NULL,
  `pep` DOUBLE NULL,
  `perc_psm_id` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`psm_id`),
  CONSTRAINT `percolator_psm_psm_id_fk`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `percolator_search_reported_peptide`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `percolator_search_reported_peptide` ;

CREATE TABLE IF NOT EXISTS `percolator_search_reported_peptide` (
  `search_id` INT UNSIGNED NOT NULL,
  `reported_peptide_id` INT UNSIGNED NOT NULL,
  `q_value` DOUBLE NULL,
  `svm_score` DOUBLE NULL,
  `pep` DOUBLE NULL,
  `calc_mass` DOUBLE NULL,
  `p_value` DOUBLE NULL,
  PRIMARY KEY (`search_id`, `reported_peptide_id`),
  CONSTRAINT `percolator_search_reported_peptide_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `percolator_search_reported_peptide_ibfk_2`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `percolator_search_reported_peptide_ibfk_2_idx` ON `percolator_search_reported_peptide` (`reported_peptide_id` ASC);


-- -----------------------------------------------------
-- Table `search_linker`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_linker` ;

CREATE TABLE IF NOT EXISTS `search_linker` (
  `search_id` INT UNSIGNED NOT NULL,
  `linker_id` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`search_id`, `linker_id`),
  CONSTRAINT `search_linker_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_linker_linker_id_fk`
    FOREIGN KEY (`linker_id`)
    REFERENCES `linker` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_linker_linker_id_fk_idx` ON `search_linker` (`linker_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_file` ;

CREATE TABLE IF NOT EXISTS `xquest_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  `sha1sum` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_file__search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `filename` ON `xquest_file` (`filename` ASC);

CREATE INDEX `xquest_file__search_id_fk_idx` ON `xquest_file` (`search_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_psm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_psm` ;

CREATE TABLE IF NOT EXISTS `xquest_psm` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT UNSIGNED NOT NULL,
  `xquest_file_id` INT UNSIGNED NOT NULL,
  `type` VARCHAR(200) NULL,
  `scan_number` VARCHAR(45) NULL,
  `xquest_id` VARCHAR(2000) NULL,
  `fdr` VARCHAR(200) NULL,
  `charge` VARCHAR(200) NULL,
  `seq1` VARCHAR(2000) NULL,
  `seq2` VARCHAR(2000) NULL,
  `xlinkposition` VARCHAR(200) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_psm_xquest_file_id_fk`
    FOREIGN KEY (`xquest_file_id`)
    REFERENCES `xquest_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `xquest_psm_psm_id_fk`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_psm_psm_id_fk_idx` ON `xquest_psm` (`psm_id` ASC);

CREATE INDEX `xquest_psm_xquest_file_id_fk_idx` ON `xquest_psm` (`xquest_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_psm_spectrum_search_attr_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_psm_spectrum_search_attr_value` ;

CREATE TABLE IF NOT EXISTS `xquest_psm_spectrum_search_attr_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_psm_id` INT UNSIGNED NULL,
  `attr` VARCHAR(200) NULL,
  `value` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_psm_attr_value_xquest_psm_id_fk`
    FOREIGN KEY (`xquest_psm_id`)
    REFERENCES `xquest_psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_psm_attr_value_xquest_psm_id_fk_idx` ON `xquest_psm_spectrum_search_attr_value` (`xquest_psm_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_psm_search_hit_attr_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_psm_search_hit_attr_value` ;

CREATE TABLE IF NOT EXISTS `xquest_psm_search_hit_attr_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_psm_id` INT UNSIGNED NULL,
  `attr` VARCHAR(200) NULL,
  `value` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_psm_attr_value_xquest_psm_id_fk0`
    FOREIGN KEY (`xquest_psm_id`)
    REFERENCES `xquest_psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_psm_attr_value_xquest_psm_id_fk_idx` ON `xquest_psm_search_hit_attr_value` (`xquest_psm_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_defs_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_defs_file` ;

CREATE TABLE IF NOT EXISTS `xquest_defs_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_file_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_defs_file_xquest_file_id_fk`
    FOREIGN KEY (`xquest_file_id`)
    REFERENCES `xquest_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_defs_file_xquest_file_id_fk_idx` ON `xquest_defs_file` (`xquest_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_defs_file_key_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_defs_file_key_value` ;

CREATE TABLE IF NOT EXISTS `xquest_defs_file_key_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_defs_file_id` INT UNSIGNED NOT NULL,
  `xquest_defs_file_key` VARCHAR(255) NOT NULL,
  `value` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_defs_file_key_value_xquest_defs_file_id_fk`
    FOREIGN KEY (`xquest_defs_file_id`)
    REFERENCES `xquest_defs_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_defs_file_key_value_xquest_defs_file_id_fk_idx` ON `xquest_defs_file_key_value` (`xquest_defs_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_defs_file_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_defs_file_line` ;

CREATE TABLE IF NOT EXISTS `xquest_defs_file_line` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_defs_file_id` INT UNSIGNED NOT NULL,
  `line` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_defs_file_line_xquest_defs_file_id_fk`
    FOREIGN KEY (`xquest_defs_file_id`)
    REFERENCES `xquest_defs_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_defs_file_line_xquest_defs_file_id_fk_idx` ON `xquest_defs_file_line` (`xquest_defs_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_xproph_defs_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_xproph_defs_file` ;

CREATE TABLE IF NOT EXISTS `xquest_xproph_defs_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_file_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_xproph_defs_file_xquest_file_id_fk`
    FOREIGN KEY (`xquest_file_id`)
    REFERENCES `xquest_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_xproph_defs_file_xquest_file_id_fk_idx` ON `xquest_xproph_defs_file` (`xquest_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_xproph_defs_file_key_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_xproph_defs_file_key_value` ;

CREATE TABLE IF NOT EXISTS `xquest_xproph_defs_file_key_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_xproph_defs_file_id` INT UNSIGNED NOT NULL,
  `xquest_xproph_defs_file_key` VARCHAR(255) NOT NULL,
  `value` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_xproph_defs_file_key_value_xquest_xproph_defs_file_key_fk`
    FOREIGN KEY (`xquest_xproph_defs_file_id`)
    REFERENCES `xquest_xproph_defs_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_xproph_defs_file_key_value_xquest_xproph_defs_file_k_idx` ON `xquest_xproph_defs_file_key_value` (`xquest_xproph_defs_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_xproph_defs_file_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_xproph_defs_file_line` ;

CREATE TABLE IF NOT EXISTS `xquest_xproph_defs_file_line` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_xproph_defs_file_id` INT UNSIGNED NOT NULL,
  `line` VARCHAR(2000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_xproph_defs_file_line_xquest_xproph_defs_file_id_fk`
    FOREIGN KEY (`xquest_xproph_defs_file_id`)
    REFERENCES `xquest_xproph_defs_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_xproph_defs_file_line_xquest_xproph_defs_file_id_fk_idx` ON `xquest_xproph_defs_file_line` (`xquest_xproph_defs_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_file_xquest_merger_attr_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_file_xquest_merger_attr_value` ;

CREATE TABLE IF NOT EXISTS `xquest_file_xquest_merger_attr_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_file_id` INT UNSIGNED NOT NULL,
  `attr` VARCHAR(200) NULL,
  `value` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_file_xquest_merger_attr_value_xquest_file_id_fk`
    FOREIGN KEY (`xquest_file_id`)
    REFERENCES `xquest_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_file_xquest_merger_attr_value_xquest_file_id_fk_idx` ON `xquest_file_xquest_merger_attr_value` (`xquest_file_id` ASC);


-- -----------------------------------------------------
-- Table `xquest_file_xquest_results_attr_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `xquest_file_xquest_results_attr_value` ;

CREATE TABLE IF NOT EXISTS `xquest_file_xquest_results_attr_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `xquest_file_id` INT UNSIGNED NOT NULL,
  `attr` VARCHAR(200) NULL,
  `value` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `xquest_file_xquest_results_attr_value_xquest_file_id_fk`
    FOREIGN KEY (`xquest_file_id`)
    REFERENCES `xquest_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `xquest_file_xquest_merger_attr_value_xquest_file_id_fk_idx` ON `xquest_file_xquest_results_attr_value` (`xquest_file_id` ASC);


-- -----------------------------------------------------
-- Table `search_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_file` ;

CREATE TABLE IF NOT EXISTS `search_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `display_filename` VARCHAR(255) NULL,
  `path` VARCHAR(2000) NULL,
  `filesize` INT NOT NULL,
  `mime_type` VARCHAR(500) NULL,
  `description` VARCHAR(2500) NULL,
  `upload_date` DATETIME NOT NULL,
  `file_contents` LONGBLOB NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_file_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_file_search_id_fk_idx` ON `search_file` (`search_id` ASC);


-- -----------------------------------------------------
-- Table `scan_retention_time`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `scan_retention_time` ;

CREATE TABLE IF NOT EXISTS `scan_retention_time` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `scan_file_id` INT UNSIGNED NOT NULL,
  `scan_number` INT NOT NULL,
  `precursor_scan_number` INT NULL,
  `scan_level` INT NOT NULL,
  `retention_time` DECIMAL(18,9) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `scan_retention_time_scan_file_id_fk`
    FOREIGN KEY (`scan_file_id`)
    REFERENCES `scan_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `scan_retention_time_scan_file_id_fk_idx` ON `scan_retention_time` (`scan_file_id` ASC);


-- -----------------------------------------------------
-- Table `static_mod`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `static_mod` ;

CREATE TABLE IF NOT EXISTS `static_mod` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT UNSIGNED NOT NULL,
  `residue` VARCHAR(45) NOT NULL,
  `mass` DECIMAL(18,9) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `static_mod_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `static_mod_search_id_fk_idx` ON `static_mod` (`search_id` ASC);


-- -----------------------------------------------------
-- Table `unlinked`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unlinked` ;

CREATE TABLE IF NOT EXISTS `unlinked` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `peptide_id` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `unlinked_ibfk_1`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `unlinked_ibfk_2`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE INDEX `nrseq_id` ON `unlinked` (`nrseq_id` ASC);

CREATE INDEX `psm_id` ON `unlinked` (`psm_id` ASC);

CREATE INDEX `peptide_id` ON `unlinked` (`peptide_id` ASC);


-- -----------------------------------------------------
-- Table `config_system`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `config_system` ;

CREATE TABLE IF NOT EXISTS `config_system` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(255) NOT NULL,
  `config_value` VARCHAR(4000) NULL,
  `comment` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `config_system_config_key_idx` ON `config_system` (`config_key` ASC);


-- -----------------------------------------------------
-- Table `search_web_links`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_web_links` ;

CREATE TABLE IF NOT EXISTS `search_web_links` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT UNSIGNED NOT NULL,
  `auth_user_id` INT UNSIGNED NULL,
  `link_url` VARCHAR(600) NOT NULL,
  `link_label` VARCHAR(400) NOT NULL,
  `link_timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_links_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_links_auth_user_id_fk`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE SET NULL
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_links_search_id_fk_idx` ON `search_web_links` (`search_id` ASC);

CREATE INDEX `search_links_auth_user_id_fk_idx` ON `search_web_links` (`auth_user_id` ASC);


-- -----------------------------------------------------
-- Table `default_page_view`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `default_page_view` ;

CREATE TABLE IF NOT EXISTS `default_page_view` (
  `search_id` INT UNSIGNED NOT NULL,
  `page_name` VARCHAR(80) NOT NULL,
  `auth_user_id` INT UNSIGNED NOT NULL,
  `url` VARCHAR(6000) NOT NULL,
  PRIMARY KEY (`search_id`, `page_name`),
  CONSTRAINT `default_page_view_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `default_page_view_auth_user_id_fk`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `default_page_view_search_id_fk_idx` ON `default_page_view` (`search_id` ASC);

CREATE INDEX `default_page_view_auth_user_id_fk_idx` ON `default_page_view` (`auth_user_id` ASC);


-- -----------------------------------------------------
-- Table `search_for_xlinks_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_for_xlinks_file` ;

CREATE TABLE IF NOT EXISTS `search_for_xlinks_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  `sha1sum` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_for_xlinks_file_search_id`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `filename` ON `search_for_xlinks_file` (`filename` ASC);

CREATE INDEX `search_for_xlinks_file_search_id_idx` ON `search_for_xlinks_file` (`search_id` ASC);


-- -----------------------------------------------------
-- Table `search_for_xlinks_params_file`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_for_xlinks_params_file` ;

CREATE TABLE IF NOT EXISTS `search_for_xlinks_params_file` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_for_xlinks_file_id` INT UNSIGNED NOT NULL,
  `filename` VARCHAR(255) NOT NULL,
  `path` VARCHAR(2000) NOT NULL,
  `sha1sum` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_for_xlinks_file_search_for_xlinks_file_id`
    FOREIGN KEY (`search_for_xlinks_file_id`)
    REFERENCES `search_for_xlinks_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `filename` ON `search_for_xlinks_params_file` (`filename` ASC);

CREATE INDEX `search_for_xlinks_file_search_for_xlinks_file_id_idx` ON `search_for_xlinks_params_file` (`search_for_xlinks_file_id` ASC);


-- -----------------------------------------------------
-- Table `search_for_xlinks_params_key_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_for_xlinks_params_key_value` ;

CREATE TABLE IF NOT EXISTS `search_for_xlinks_params_key_value` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `file_id` INT UNSIGNED NOT NULL,
  `data_key` VARCHAR(255) NOT NULL,
  `value` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_for_xlinks_params_key_value_file_id`
    FOREIGN KEY (`file_id`)
    REFERENCES `search_for_xlinks_params_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_for_xlinks_params_key_value_file_id_idx` ON `search_for_xlinks_params_key_value` (`file_id` ASC);


-- -----------------------------------------------------
-- Table `search_for_xlinks_params_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_for_xlinks_params_line` ;

CREATE TABLE IF NOT EXISTS `search_for_xlinks_params_line` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `file_id` INT UNSIGNED NOT NULL,
  `line` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_for_xlinks_params_line_file_id`
    FOREIGN KEY (`file_id`)
    REFERENCES `search_for_xlinks_params_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_for_xlinks_params_key_value_file_id_idx` ON `search_for_xlinks_params_line` (`file_id` ASC);


-- -----------------------------------------------------
-- Table `search_for_xlinks_psm`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_for_xlinks_psm` ;

CREATE TABLE IF NOT EXISTS `search_for_xlinks_psm` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `file_id` INT UNSIGNED NOT NULL,
  `psm_id` INT UNSIGNED NOT NULL,
  `data_key` VARCHAR(255) NOT NULL,
  `value` VARCHAR(4000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `search_for_xlinks_psm_psm_id_fk`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_for_xlinks_psm_file_id`
    FOREIGN KEY (`file_id`)
    REFERENCES `search_for_xlinks_file` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_for_xlinks_psm_psm_id_fk_idx` ON `search_for_xlinks_psm` (`psm_id` ASC);

CREATE INDEX `search_for_xlinks_psm_file_id_idx` ON `search_for_xlinks_psm` (`file_id` ASC);


-- -----------------------------------------------------
-- Table `unified_reported_peptide_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unified_reported_peptide_lookup` ;

CREATE TABLE IF NOT EXISTS `unified_reported_peptide_lookup` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `unified_sequence` VARCHAR(2000) NOT NULL,
  `link_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  `has_dynamic_modifictions` TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

CREATE INDEX `unified_reported_peptide__unified_sequence_idx` ON `unified_reported_peptide_lookup` (`unified_sequence`(20) ASC);


-- -----------------------------------------------------
-- Table `unified_rep_pep_matched_peptide_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unified_rep_pep_matched_peptide_lookup` ;

CREATE TABLE IF NOT EXISTS `unified_rep_pep_matched_peptide_lookup` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `unified_reported_peptide_id` INT UNSIGNED NOT NULL,
  `peptide_id` INT UNSIGNED NOT NULL,
  `peptide_order` INT UNSIGNED NOT NULL,
  `link_position_1` INT UNSIGNED NULL,
  `link_position_2` INT UNSIGNED NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `unified_matched_peptide__unified_reported_peptide_id_fk`
    FOREIGN KEY (`unified_reported_peptide_id`)
    REFERENCES `unified_reported_peptide_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_matched_peptide__peptide_id_fk`
    FOREIGN KEY (`peptide_id`)
    REFERENCES `peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `unified_matched_peptide__unified_reported_peptide_id_fk_idx` ON `unified_rep_pep_matched_peptide_lookup` (`unified_reported_peptide_id` ASC);

CREATE INDEX `unified_matched_peptide__peptide_id_fk_idx` ON `unified_rep_pep_matched_peptide_lookup` (`peptide_id` ASC);


-- -----------------------------------------------------
-- Table `unified_rep_pep_dynamic_mod_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unified_rep_pep_dynamic_mod_lookup` ;

CREATE TABLE IF NOT EXISTS `unified_rep_pep_dynamic_mod_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `rp_matched_peptide_id` INT(10) UNSIGNED NOT NULL,
  `position` INT(10) UNSIGNED NOT NULL,
  `mass` DOUBLE NOT NULL,
  `mass_rounded` DOUBLE NOT NULL,
  `mass_rounded_string` VARCHAR(200) NOT NULL,
  `mass_rounding_places` SMALLINT NOT NULL,
  `mod_order` SMALLINT NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `unified_rp_dynamic_mod__rp_matched_peptide_id_fk`
    FOREIGN KEY (`rp_matched_peptide_id`)
    REFERENCES `unified_rep_pep_matched_peptide_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_general_ci;

CREATE INDEX `unified_rp_dynamic_mod__rp_matched_peptide_id_fk_idx` ON `unified_rep_pep_dynamic_mod_lookup` (`rp_matched_peptide_id` ASC);


-- -----------------------------------------------------
-- Table `search__dynamic_mod_mass_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search__dynamic_mod_mass_lookup` ;

CREATE TABLE IF NOT EXISTS `search__dynamic_mod_mass_lookup` (
  `search_id` INT UNSIGNED NOT NULL,
  `dynamic_mod_mass` DOUBLE UNSIGNED NOT NULL,
  `search_id_dynamic_mod_mass_count` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`search_id`, `dynamic_mod_mass`),
  CONSTRAINT `search__dynamic_mod_mass__search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `query_criteria_value_counts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `query_criteria_value_counts` ;

CREATE TABLE IF NOT EXISTS `query_criteria_value_counts` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `field` VARCHAR(45) NOT NULL,
  `value` VARCHAR(45) NOT NULL,
  `count` INT UNSIGNED NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE UNIQUE INDEX `query_criteria_value_counts__field_value_unique_idx` ON `query_criteria_value_counts` (`field` ASC, `value` ASC);


-- -----------------------------------------------------
-- Table `unified_rep_pep__reported_peptide__search_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unified_rep_pep__reported_peptide__search_lookup` ;

CREATE TABLE IF NOT EXISTS `unified_rep_pep__reported_peptide__search_lookup` (
  `unified_reported_peptide_id` INT UNSIGNED NOT NULL,
  `reported_peptide_id` INT UNSIGNED NOT NULL,
  `search_id` INT UNSIGNED NOT NULL,
  `link_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  `peptide_q_value_for_search` DOUBLE NULL,
  `best_psm_q_value` DOUBLE NULL,
  `has_dynamic_modifictions` TINYINT UNSIGNED NOT NULL,
  `has_monolinks` TINYINT UNSIGNED NOT NULL,
  `psm_num_at_pt_01_q_cutoff` INT NOT NULL,
  PRIMARY KEY (`unified_reported_peptide_id`, `reported_peptide_id`, `search_id`),
  CONSTRAINT `unified_rp__reported_peptide__search__unified_rp_id_fk`
    FOREIGN KEY (`unified_reported_peptide_id`)
    REFERENCES `unified_reported_peptide_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__reported_peptide__search__reported_peptide_id_fk`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__reported_peptide__search__search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `unified_rp__reported_peptide__search__reported_peptide_id_f_idx` ON `unified_rep_pep__reported_peptide__search_lookup` (`reported_peptide_id` ASC);

CREATE INDEX `unified_rp__reported_peptide__search__search_id_fk_idx` ON `unified_rep_pep__reported_peptide__search_lookup` (`search_id` ASC);

CREATE INDEX `unified_rp__rp__search__srch_type_bpsmqval_idx` ON `unified_rep_pep__reported_peptide__search_lookup` (`search_id` ASC, `link_type` ASC, `best_psm_q_value` ASC);


-- -----------------------------------------------------
-- Table `search__reported_peptide__dynamic_mod_lookup`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search__reported_peptide__dynamic_mod_lookup` ;

CREATE TABLE IF NOT EXISTS `search__reported_peptide__dynamic_mod_lookup` (
  `search_id` INT UNSIGNED NOT NULL,
  `reported_peptide_id` INT UNSIGNED NOT NULL,
  `dynamic_mod_mass` DOUBLE UNSIGNED NOT NULL,
  `link_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  `best_psm_q_value` DOUBLE NOT NULL,
  PRIMARY KEY (`search_id`, `reported_peptide_id`, `dynamic_mod_mass`),
  CONSTRAINT `search__rep_pep__dyn_mods_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search__rep_pep__dyn_mods_rep_pep_id_fk`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search__rep_pep__dyn_mods_rep_pep_id_fk_idx` ON `search__reported_peptide__dynamic_mod_lookup` (`reported_peptide_id` ASC);

CREATE INDEX `search__rep_pep__dyn_mods_search_id_lnk_tp_bpqv_idx` ON `search__reported_peptide__dynamic_mod_lookup` (`search_id` ASC, `link_type` ASC, `best_psm_q_value` ASC);

CREATE INDEX `search__rep_pep__dyn_mods_search_id_bpqv_idx` ON `search__reported_peptide__dynamic_mod_lookup` (`search_id` ASC, `best_psm_q_value` ASC);


-- -----------------------------------------------------
-- Table `search_program`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search_program` ;

CREATE TABLE IF NOT EXISTS `search_program` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `short_name` VARCHAR(100) NOT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(255) NULL,
  `display_order` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `search_program__name_unique_idx` ON `search_program` (`name` ASC);

CREATE UNIQUE INDEX `search_program__short_name_unique_idx` ON `search_program` (`short_name` ASC);


-- -----------------------------------------------------
-- Table `search__search_program`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `search__search_program` ;

CREATE TABLE IF NOT EXISTS `search__search_program` (
  `search_id` INT UNSIGNED NOT NULL,
  `search_program_id` INT UNSIGNED NOT NULL,
  `version` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`search_id`, `search_program_id`),
  CONSTRAINT `search_program_version_search_program_id_fk`
    FOREIGN KEY (`search_program_id`)
    REFERENCES `search_program` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `search_program_version__search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `search_program_version_search_program_id_fk_idx` ON `search__search_program` (`search_program_id` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
