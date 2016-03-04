

use proxl;


SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';



ALTER SCHEMA `proxl`  DEFAULT CHARACTER SET utf8  DEFAULT COLLATE utf8_general_ci ;

ALTER TABLE `proxl`.`search` 
CHANGE COLUMN `path` `path` VARCHAR(2000) NULL DEFAULT NULL ,
CHANGE COLUMN `directory_name` `directory_name` VARCHAR(255) NULL DEFAULT NULL ;


CREATE TABLE IF NOT EXISTS `search_programs_per_search` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `name` VARCHAR(200) NOT NULL,
  `display_name` VARCHAR(255) NOT NULL,
  `version` VARCHAR(200) NOT NULL,
  `description` VARCHAR(4000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `search_program__search_id__name__unique_idx` (`search_id` ASC, `name` ASC),
  INDEX `search_program__search_id_fk_idx` (`search_id` ASC),
  CONSTRAINT `srch_prgrms_per_srch_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `annotation_type` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `search_programs_per_search_id` INT(10) UNSIGNED NOT NULL,
  `psm_peptide_type` ENUM('psm','peptide') NOT NULL,
  `filterable_descriptive_type` ENUM('filterable','descriptive') NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `default_visible` INT(1) NOT NULL,
  `display_order` INT(11) NULL DEFAULT NULL,
  `description` VARCHAR(4000) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `ann_tp_srch_pgm_id_fk_idx` (`search_programs_per_search_id` ASC),
  INDEX `ann_tp_srch_id_fk_idx` (`search_id` ASC),
  UNIQUE INDEX `annotation_type_Unique_idx` (`search_id` ASC, `search_programs_per_search_id` ASC, `psm_peptide_type` ASC, `filterable_descriptive_type` ASC, `name` ASC),
  CONSTRAINT `ann_tp_srch_pgm_id_fk`
    FOREIGN KEY (`search_programs_per_search_id`)
    REFERENCES `search_programs_per_search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `ann_tp_srch_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE IF NOT EXISTS `annotation_type_filterable` (
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `filter_direction` ENUM('above','below') NOT NULL,
  `default_filter` INT(1) NOT NULL,
  `default_filter_value` DOUBLE NULL DEFAULT NULL,
  `default_filter_value_string` VARCHAR(45) NULL DEFAULT NULL,
  `sort_order` INT(10) UNSIGNED NULL DEFAULT NULL,
  PRIMARY KEY (`annotation_type_id`),
  CONSTRAINT `ann_type_fltrbl__annotation_type_id_fk`
    FOREIGN KEY (`annotation_type_id`)
    REFERENCES `annotation_type` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `psm_annotation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `psm_annotation` ;

CREATE TABLE IF NOT EXISTS `psm_annotation` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `psm_id` INT UNSIGNED NOT NULL,
  `filterable_descriptive_type` ENUM('filterable','descriptive') NOT NULL,
  `annotation_type_id` INT UNSIGNED NOT NULL,
  `value_location` ENUM('local','large_value_table') NOT NULL,
  `value_double` DOUBLE NOT NULL,
  `value_string` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `psm_filterable_annotation__psm_id_fk`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `psm_filterable_ann__ann_type_id_fk`
    FOREIGN KEY (`annotation_type_id`)
    REFERENCES `annotation_type` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `psm_annotation__psm_id_fk_idx` ON `psm_annotation` (`psm_id` ASC);

CREATE INDEX `psm_ann__ann_type_id_fk_idx` ON `psm_annotation` (`annotation_type_id` ASC);

CREATE UNIQUE INDEX `psm_annotation_psm_id_ann_typ_id_idx` ON `psm_annotation` (`psm_id` ASC, `annotation_type_id` ASC);

CREATE INDEX `psm_annotation_psm_id_ann_typ_f_d_idx` ON `psm_annotation` (`psm_id` ASC, `annotation_type_id` ASC);


-- -----------------------------------------------------
-- Table `srch__rep_pept__annotation`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `srch__rep_pept__annotation` ;

CREATE TABLE IF NOT EXISTS `srch__rep_pept__annotation` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `filterable_descriptive_type` ENUM('filterable','descriptive') NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `value_location` ENUM('local','large_value_table') NOT NULL,
  `value_double` DOUBLE NOT NULL,
  `value_string` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `srch__rep_pept__ann__rep_pept_id_fk`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `srch__rep_pept__ann__type_id_fk`
    FOREIGN KEY (`annotation_type_id`)
    REFERENCES `annotation_type` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `srch__rep_pept__search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 27
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE INDEX `srch__rep_pept__ann__rep_pept_id_fk_idx` ON `srch__rep_pept__annotation` (`reported_peptide_id` ASC);

CREATE UNIQUE INDEX `srch__rep_pept_search_id_reppeptid_ann__type_id_fk_idx` ON `srch__rep_pept__annotation` (`search_id` ASC, `reported_peptide_id` ASC, `annotation_type_id` ASC);

CREATE INDEX `srch__rep_pept__ann__type_id_fk_idx` ON `srch__rep_pept__annotation` (`annotation_type_id` ASC);

CREATE INDEX `srch__rep_pept_srch_id_reppeptid_ann_tp__idx` ON `srch__rep_pept__annotation` (`search_id` ASC, `reported_peptide_id` ASC, `filterable_descriptive_type` ASC);



CREATE TABLE IF NOT EXISTS `search_crosslink_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `protein_1_position` INT(10) UNSIGNED NOT NULL,
  `protein_2_position` INT(10) UNSIGNED NOT NULL,
  `num_psm_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `num_linked_peptides_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `num_unique_peptides_linked_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `srch_crosslnk_gen_lkp_Unique` (`search_id` ASC, `nrseq_id_1` ASC, `nrseq_id_2` ASC, `protein_1_position` ASC, `protein_2_position` ASC),
  CONSTRAINT `search_crosslink_generic_lookup_ibfk_1`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_crosslink_best_psm_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_crosslink_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `protein_1_position` INT(10) UNSIGNED NOT NULL,
  `protein_2_position` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_psm_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_psm_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `Unique_srch_nrseq1_nrseq2_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id_1` ASC, `nrseq_id_2` ASC, `protein_1_position` ASC, `protein_2_position` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_psm_value_for_ann_type_id` ASC),
  INDEX `primary_ann_tp_fk_idx` (`search_crosslink_generic_lookup_id` ASC),
  CONSTRAINT `search_crosslink_best_psm_value_generic_lookup_ibfk_10`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_crosslink_best_psm_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_crosslink_generic_lookup_id`)
    REFERENCES `search_crosslink_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_crosslink_best_peptide_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_crosslink_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `protein_1_position` INT(10) UNSIGNED NOT NULL,
  `protein_2_position` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_peptide_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_peptide_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `primary_ann_tp_fk_idx` (`search_crosslink_generic_lookup_id` ASC),
  UNIQUE INDEX `Unique_srch_nrseq1_nrseq2_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id_1` ASC, `nrseq_id_2` ASC, `protein_1_position` ASC, `protein_2_position` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_peptide_value_for_ann_type_id` ASC),
  CONSTRAINT `search_crosslink_best_peptide_value_generic_lookup_ibfk_100`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_crosslink_best_peptide_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_crosslink_generic_lookup_id`)
    REFERENCES `search_crosslink_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `unified_rp__rep_pept__search__generic_lookup` (
  `unified_reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `link_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  `has_dynamic_modifictions` TINYINT(3) UNSIGNED NOT NULL,
  `has_monolinks` TINYINT(3) UNSIGNED NOT NULL,
  `sample_psm_id` INT(10) UNSIGNED NOT NULL,
  `psm_num_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `peptide_meets_default_cutoffs` ENUM('yes','no','not_applicable') NOT NULL,
  PRIMARY KEY (`unified_reported_peptide_id`, `reported_peptide_id`, `search_id`),
  INDEX `unified_rp__reported_peptide__search__reported_peptide_id_f_idx` (`reported_peptide_id` ASC),
  INDEX `unified_rp__reported_peptide__search__search_id_fk_idx` (`search_id` ASC),
  INDEX `unified_rp__rp__search__srch_type_bpsmqval_idx` (`search_id` ASC, `link_type` ASC),
  INDEX `unified_rep_pep__reported_peptide__search_lookup__sample_ps_idx` (`sample_psm_id` ASC),
  CONSTRAINT `unified_rp__rep_pept__search__generic_lookup__sample_psm_id_fk`
    FOREIGN KEY (`sample_psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__rep_pept__search__gen_lkp__reported_peptide_id_fk`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__rep_pept__search__generic_lookup__unified_rp_id_fk`
    FOREIGN KEY (`unified_reported_peptide_id`)
    REFERENCES `unified_reported_peptide_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `unified_rp__rep_pept__search__best_psm_value_generic_lookup` (
  `unified_reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `link_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  `has_dynamic_modifictions` TINYINT(3) UNSIGNED NOT NULL,
  `has_monolinks` TINYINT(3) UNSIGNED NOT NULL,
  `psm_num_for_ann_type_id_at_default_cutoff` INT(11) UNSIGNED NULL DEFAULT NULL,
  `sample_psm_id` INT(10) UNSIGNED NOT NULL,
  `best_psm_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_psm_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`unified_reported_peptide_id`, `reported_peptide_id`, `search_id`, `annotation_type_id`),
  INDEX `unified_rp__reported_peptide__search__reported_peptide_id_f_idx` (`reported_peptide_id` ASC),
  INDEX `unified_rp__reported_peptide__search__search_id_fk_idx` (`search_id` ASC),
  INDEX `unified_rp__rp__search__srch_type_bpsm_val_idx` (`search_id` ASC, `link_type` ASC, `best_psm_value_for_ann_type_id` ASC),
  INDEX `unified_rep_pep__reported_peptide__search_lookup__sample_ps_idx` (`sample_psm_id` ASC),
  CONSTRAINT `unified_rp__rep_pep__srch__bst_psm_val_gen_lkp__smpl_psm_id_fk`
    FOREIGN KEY (`sample_psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__rep_pept_srch__bst_psm_val_gen_lkp__rept_pep_id_fk`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__rep_pept__srch__bst_psm_val_gen_lkp__unfd_rp_id_fk`
    FOREIGN KEY (`unified_reported_peptide_id`)
    REFERENCES `unified_reported_peptide_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `psm_filterable_annotation__generic_lookup` (
  `psm_annotation_id` INT UNSIGNED NOT NULL,
  `psm_id` INT UNSIGNED NOT NULL,
  `annotation_type_id` INT UNSIGNED NOT NULL,
  `value_double` DOUBLE NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `psm_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  PRIMARY KEY (`psm_annotation_id`),
  INDEX `psm_filterable_annotation__psm_id_fk_idx` (`psm_id` ASC),
  INDEX `psm_filtrble_ann__generic_lkup__srch_rep_pep_anntpid_value` (`search_id` ASC, `reported_peptide_id` ASC, `annotation_type_id` ASC, `value_double` ASC),
  CONSTRAINT `psm_filterable_annotation__generic_lookup__psm_id_fk`
    FOREIGN KEY (`psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `psm_filtrble_ann__generic_lkp_psm_ann_id_fk`
    FOREIGN KEY (`psm_annotation_id`)
    REFERENCES `psm_annotation` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE TABLE IF NOT EXISTS `unified_rp__rep_pept__search__peptide_fltbl_value_generic_lookup` (
  `unified_reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `reported_peptide_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `link_type` ENUM('looplink','crosslink','unlinked','dimer') NOT NULL,
  `has_dynamic_modifictions` TINYINT(3) UNSIGNED NOT NULL,
  `has_monolinks` TINYINT(3) UNSIGNED NOT NULL,
  `sample_psm_id` INT(10) UNSIGNED NOT NULL,
  `peptide_value_for_ann_type_id` DOUBLE NOT NULL,
  `peptide_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`unified_reported_peptide_id`, `reported_peptide_id`, `search_id`, `annotation_type_id`),
  INDEX `unified_rp__reported_peptide__search__reported_peptide_id_f_idx` (`reported_peptide_id` ASC),
  INDEX `unified_rp__reported_peptide__search__search_id_fk_idx` (`search_id` ASC),
  INDEX `unified_rp__rp__search__srch_type_pept_val_idx` (`search_id` ASC, `link_type` ASC, `peptide_value_for_ann_type_id` ASC),
  INDEX `unified_rep_pep__reported_peptide__search_lookup__sample_ps_idx` (`sample_psm_id` ASC),
  CONSTRAINT `unified_rp__rep_pep__srch__peptd_val_gen_lkp__smpl_psm_id_fk0`
    FOREIGN KEY (`sample_psm_id`)
    REFERENCES `psm` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__rep_pept_srch__peptd_val_gen_lkp__rept_pep_id_fk0`
    FOREIGN KEY (`reported_peptide_id`)
    REFERENCES `reported_peptide` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `unified_rp__rep_pept__srch__peptd_val_gen_lkp__unfd_rp_id_fk0`
    FOREIGN KEY (`unified_reported_peptide_id`)
    REFERENCES `unified_reported_peptide_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_looplink_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position_1` INT(10) UNSIGNED NOT NULL,
  `protein_position_2` INT(10) UNSIGNED NOT NULL,
  `num_psm_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `num_linked_peptides_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `num_unique_peptides_linked_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `Unique_srch_nrseq_pos1_pos2` (`search_id` ASC, `nrseq_id` ASC, `protein_position_1` ASC, `protein_position_2` ASC),
  CONSTRAINT `search_looplink_generic_lookup_ibfk_10`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_looplink_best_psm_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_looplink_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position_1` INT(10) UNSIGNED NOT NULL,
  `protein_position_2` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_psm_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_psm_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `primary_ann_tp_fk_idx` (`search_looplink_generic_lookup_id` ASC),
  UNIQUE INDEX `Unique_srch_nrseq_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id` ASC, `protein_position_1` ASC, `protein_position_2` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_psm_value_for_ann_type_id` ASC),
  CONSTRAINT `search_looplink_best_psm_value_generic_lookup_ibfk_100`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_looplink_best_psm_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_looplink_generic_lookup_id`)
    REFERENCES `search_looplink_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_looplink_best_peptide_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_looplink_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position_1` INT(10) UNSIGNED NOT NULL,
  `protein_position_2` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_peptide_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_peptide_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `search_looplink_best_peptide_value_generic_lookup_primary_f_idx` (`search_looplink_generic_lookup_id` ASC),
  UNIQUE INDEX `Unique_srch_nrseq_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id` ASC, `protein_position_1` ASC, `protein_position_2` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_peptide_value_for_ann_type_id` ASC),
  CONSTRAINT `search_looplink_best_peptide_value_generic_lookup_ibfk_1000`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_looplink_best_peptide_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_looplink_generic_lookup_id`)
    REFERENCES `search_looplink_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_monolink_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position` INT(10) UNSIGNED NOT NULL,
  `num_psm_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `num_linked_peptides_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  `num_unique_peptides_linked_at_default_cutoff` INT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `Unique_srch_nrseq_pos_fk_idx` (`search_id` ASC, `nrseq_id` ASC, `protein_position` ASC),
  CONSTRAINT `search_monolink_generic_lookup_ibfk_100`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_monolink_best_psm_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_monolink_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_psm_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_psm_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `search_monolink_best_psm_value_generic_lookup_primary_fk_idx` (`search_monolink_generic_lookup_id` ASC),
  UNIQUE INDEX `Unique_srch_nrseq_pos_ann_type` (`search_id` ASC, `nrseq_id` ASC, `protein_position` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_psm_value_for_ann_type_id` ASC),
  CONSTRAINT `search_monolink_best_psm_value_generic_lookup_ibfk_1000`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_monolink_best_psm_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_monolink_generic_lookup_id`)
    REFERENCES `search_monolink_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_monolink_best_peptide_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_monolink_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `protein_position` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_peptide_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_peptide_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `search_monolink_best_peptide_value_generic_lookup_primary_f_idx` (`search_monolink_generic_lookup_id` ASC),
  UNIQUE INDEX `Unique_srch_nrseq_pos_ann_type_fk_idx` (`search_id` ASC, `nrseq_id` ASC, `protein_position` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_peptide_value_for_ann_type_id` ASC),
  CONSTRAINT `search_looplink_best_peptide_value_generic_monoup_ibfk_10000`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_monolink_best_peptide_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_monolink_generic_lookup_id`)
    REFERENCES `search_monolink_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_dimer_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `num_psm_at_default_cutoff` INT(10) UNSIGNED NULL,
  `num_linked_peptides_at_default_cutoff` INT(10) UNSIGNED NULL,
  `num_unique_peptides_linked_at_default_cutoff` INT(10) UNSIGNED NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `srch_crosslnk_gen_lkp_Unique` (`search_id` ASC, `nrseq_id_1` ASC, `nrseq_id_2` ASC),
  CONSTRAINT `search_dimer_generic_lookup_ibfk_10`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_dimer_best_psm_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_dimer_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_psm_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_psm_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `Unique_srch_nrseq1_nrseq2_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id_1` ASC, `nrseq_id_2` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_psm_value_for_ann_type_id` ASC),
  INDEX `search_dimer_best_psm_value_generic_lookup_primary_fk0_idx` (`search_dimer_generic_lookup_id` ASC),
  CONSTRAINT `search_dimer_best_psm_value_generic_lookup_ibfk_100`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_dimer_best_psm_value_generic_lookup_primary_fk0`
    FOREIGN KEY (`search_dimer_generic_lookup_id`)
    REFERENCES `search_dimer_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_dimer_best_peptide_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_dimer_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_1` INT(10) UNSIGNED NOT NULL,
  `nrseq_id_2` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_peptide_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_peptide_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `Unique_srch_nrseq1_nrseq2_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id_1` ASC, `nrseq_id_2` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_peptide_value_for_ann_type_id` ASC),
  INDEX `search_dimer_best_peptide_value_generic_lookup_primary_fk0_idx` (`search_dimer_generic_lookup_id` ASC),
  CONSTRAINT `search_dimer_best_peptide_value_generic_lookup_ibfk_1000`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_dimer_best_peptide_value_generic_lookup_primary_fk0`
    FOREIGN KEY (`search_dimer_generic_lookup_id`)
    REFERENCES `search_dimer_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_unlinked_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `num_psm_at_default_cutoff` INT(10) UNSIGNED NULL,
  `num_linked_peptides_at_default_cutoff` INT(10) UNSIGNED NULL,
  `num_unique_peptides_linked_at_default_cutoff` INT(10) UNSIGNED NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `srch_crosslnk_gen_lkp_Unique` (`search_id` ASC, `nrseq_id` ASC),
  CONSTRAINT `search_unlinked_generic_lookup_ibfk_100`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_unlinked_best_psm_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_unlinked_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_psm_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_psm_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `Unique_srch_nrseq1_nrseq2_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_psm_value_for_ann_type_id` ASC),
  INDEX `search_unlinked_best_psm_value_generic_lookup_primary_fk_idx` (`search_unlinked_generic_lookup_id` ASC),
  CONSTRAINT `search_unlinked_best_psm_value_generic_lookup_ibfk_1000`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_unlinked_best_psm_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_unlinked_generic_lookup_id`)
    REFERENCES `search_unlinked_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `search_unlinked_best_peptide_value_generic_lookup` (
  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `search_unlinked_generic_lookup_id` INT(10) UNSIGNED NOT NULL,
  `search_id` INT(10) UNSIGNED NOT NULL,
  `nrseq_id` INT(10) UNSIGNED NOT NULL,
  `annotation_type_id` INT(10) UNSIGNED NOT NULL,
  `best_peptide_value_for_ann_type_id` DOUBLE NOT NULL,
  `best_peptide_value_string_for_ann_type_id` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `Unique_srch_nrseq1_nrseq2_pos1_pos2_ann_type` (`search_id` ASC, `nrseq_id` ASC, `annotation_type_id` ASC),
  INDEX `srch_ann_type_id_value` (`search_id` ASC, `annotation_type_id` ASC, `best_peptide_value_for_ann_type_id` ASC),
  INDEX `search_unlinked_best_peptide_value_generic_lookup_primary_f_idx` (`search_unlinked_generic_lookup_id` ASC),
  CONSTRAINT `search_unlinked_best_peptide_value_generic_lookup_ibfk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `search_unlinked_best_peptide_value_generic_lookup_primary_fk`
    FOREIGN KEY (`search_unlinked_generic_lookup_id`)
    REFERENCES `search_unlinked_generic_lookup` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1
COLLATE = latin1_swedish_ci;

CREATE TABLE IF NOT EXISTS `default_page_view_generic` (
  `search_id` INT(10) UNSIGNED NOT NULL,
  `page_name` VARCHAR(80) NOT NULL,
  `auth_user_id` INT(10) UNSIGNED NOT NULL,
  `url` VARCHAR(6000) NOT NULL,
  `query_json` VARCHAR(6000) NOT NULL,
  PRIMARY KEY (`search_id`, `page_name`),
  INDEX `default_page_view_search_id_fk_idx` (`search_id` ASC),
  INDEX `default_page_view_auth_user_id_fk_idx` (`auth_user_id` ASC),
  CONSTRAINT `default_page_view_generic_auth_user_id_fk`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `default_page_view_generic_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


-- -----------------------------------------------------
-- Table `default_page_view_generic`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `default_page_view_generic` ;

CREATE TABLE IF NOT EXISTS `default_page_view_generic` (
  `search_id` INT UNSIGNED NOT NULL,
  `page_name` VARCHAR(80) NOT NULL,
  `auth_user_id` INT UNSIGNED NOT NULL,
  `url` VARCHAR(6000) NOT NULL,
  `query_json` VARCHAR(6000) NOT NULL,
  PRIMARY KEY (`search_id`, `page_name`),
  CONSTRAINT `default_page_view_generic_auth_user_id_fk`
    FOREIGN KEY (`auth_user_id`)
    REFERENCES `auth_user` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `default_page_view_generic_search_id_fk`
    FOREIGN KEY (`search_id`)
    REFERENCES `search` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `default_page_view_search_id_fk_idx` ON `default_page_view_generic` (`search_id` ASC);

CREATE INDEX `default_page_view_auth_user_id_fk_idx` ON `default_page_view_generic` (`auth_user_id` ASC);


-- -----------------------------------------------------
-- Table `srch__rep_pept__annotation_large_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `srch__rep_pept__annotation_large_value` ;

CREATE TABLE IF NOT EXISTS `srch__rep_pept__annotation_large_value` (
  `srch__rep_pept__annotation_id` INT UNSIGNED NOT NULL,
  `value_string` LONGTEXT NULL,
  PRIMARY KEY (`srch__rep_pept__annotation_id`),
  CONSTRAINT `srch__rep_pept__annotation_large_value__primary_id_fk`
    FOREIGN KEY (`srch__rep_pept__annotation_id`)
    REFERENCES `srch__rep_pept__annotation` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `psm_annotation_large_value`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `psm_annotation_large_value` ;

CREATE TABLE IF NOT EXISTS `psm_annotation_large_value` (
  `psm_annotation_id` INT UNSIGNED NOT NULL,
  `value_string` LONGTEXT NOT NULL,
  CONSTRAINT `psm_annotation_large_value_primary_id_fk`
    FOREIGN KEY (`psm_annotation_id`)
    REFERENCES `psm_annotation` (`id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;

CREATE INDEX `psm_annotation_large_value_primary_id_fk_idx` ON `psm_annotation_large_value` (`psm_annotation_id` ASC);



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
