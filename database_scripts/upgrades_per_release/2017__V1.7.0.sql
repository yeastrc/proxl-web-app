

-- V1.7.0 Release DB changes


--  2017_07__add_new_tables_scan_file_statistics.sql


CREATE TABLE IF NOT EXISTS scan_file_statistics (
  scan_file_id INT UNSIGNED NOT NULL,
  ms1_scan_count BIGINT UNSIGNED NOT NULL,
  ms1_scan_intensities_summed DOUBLE NOT NULL,
  ms2_scan_count BIGINT UNSIGNED NOT NULL,
  ms2_scan_intensities_summed DOUBLE NOT NULL,
  only_for_on_duplicate_update TINYINT(3) UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_statistics_scan_file_id
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


CREATE TABLE IF NOT EXISTS scan_file_ms1_intensity_binned_summed_summary (
  scan_file_id INT UNSIGNED NOT NULL,
  retention_time_max_bin_minus_min_bin_plus_1 BIGINT NOT NULL,
  mz_max_bin_minus_min_bin_plus_1 BIGINT NOT NULL,
  intensity_max_minus_min DOUBLE NOT NULL,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_ms1_intenbnsmsm_scan_file_id
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE TABLE IF NOT EXISTS scan_file_ms1_intensity_binned_summed_summary_data (
  scan_file_id INT UNSIGNED NOT NULL,
  binned_summed_summary_data_json MEDIUMBLOB NOT NULL,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_ms1_intenbnsmsmdt_scan_file_id
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE TABLE IF NOT EXISTS scan_file_ms1_intensity_binned_summed_data (
  scan_file_id INT UNSIGNED NOT NULL,
  binned_summed_data_json_gzipped LONGBLOB NOT NULL,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_ms1_intenbnsmdata_scan_file_id
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;

CREATE TABLE IF NOT EXISTS scan_file_ms_1_per_scan_data_num_tic_rt (
  scan_file_id INT UNSIGNED NOT NULL,
  data_json_gzipped MEDIUMBLOB NOT NULL,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_ms_1_per_scan_data_num_tic_rt
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin
COMMENT = 'For MS1 scans, store the scan number (sn), total ion current' /* comment truncated */ /* (tic), and retention time (rt) 
as JSON blob compressed with gzip*/;

CREATE TABLE IF NOT EXISTS scan_file_ms_2_per_scan_data_num_tic_rt (
  scan_file_id INT UNSIGNED NOT NULL,
  data_json_gzipped MEDIUMBLOB NOT NULL,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_ms_2_per_scan_data_num_tic_rt
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin
COMMENT = 'For MS2 scans, store the scan number (sn), total ion current' /* comment truncated */ /* (tic), and retention time (rt), assoc ms1 scan number (sn1)
as JSON blob compressed with gzip*/


--  2017_07_b_upgrade.sql

ALTER TABLE proxl_xml_file_import_tracking_single_file 
ADD COLUMN canonical_filename_w_path_on_submit_machine VARCHAR(4000) NULL AFTER filename_on_disk_with_path_sub_same_machine,
ADD COLUMN absolute_filename_w_path_on_submit_machine VARCHAR(4000) NULL AFTER canonical_filename_w_path_on_submit_machine;

ALTER TABLE scan_file 
ADD COLUMN file_size BIGINT UNSIGNED NULL AFTER sha1sum;

CREATE TABLE  scan_file_source (
  scan_file_id INT UNSIGNED NOT NULL,
  path VARCHAR(4000) NULL,
  canonical_filename_w_path_on_submit_machine VARCHAR(4000) NULL,
  absolute_filename_w_path_on_submit_machine VARCHAR(4000) NULL,
  PRIMARY KEY (scan_file_id),
  CONSTRAINT fk_scan_file_source
    FOREIGN KEY (scan_file_id)
    REFERENCES scan_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
ENGINE = InnoDB;

--  Initial population of scan_file_source table from scan_file table
INSERT INTO scan_file_source (scan_file_id, path)
SELECT id, path FROM scan_file;


--  Update scan_file.file_size where have data

UPDATE scan_file INNER JOIN proxl_xml_file_import_tracking_single_file AS itsf
	ON scan_file.filename = itsf.filename_in_upload AND scan_file.sha1sum = itsf.sha1_sum
    SET scan_file.file_size = itsf.file_size;
    

--  2017_08_upgrade.sql

ALTER TABLE project_search 
DROP COLUMN active_project_id_search_id_unique_record,
DROP INDEX project_id_search_id_active_p_id_s_id_u_r_unique_idx ,
ADD UNIQUE INDEX project_id_search_id_unique_idx (project_id ASC, search_id ASC);



--  2017_09_minor_upgrade.sql

--  change psm.charge to NOT NULL

ALTER TABLE psm
CHANGE COLUMN charge charge SMALLINT(6) NOT NULL ;


