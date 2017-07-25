

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
    