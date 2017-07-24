

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

