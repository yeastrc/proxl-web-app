

ALTER TABLE search_scan_filename
ADD COLUMN scan_file_id INT UNSIGNED NULL AFTER filename,
ADD INDEX search_scan_filename_scan_file_fk_idx (scan_file_id ASC);

ALTER TABLE search_scan_filename 
ADD CONSTRAINT search_scan_filename_scan_file_fk
  FOREIGN KEY (scan_file_id)
  REFERENCES scan_file (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;



UPDATE search_scan_filename
INNER JOIN psm ON search_scan_filename.id = psm.search_scan_filename_id
INNER JOIN scan ON psm.scan_id = scan.id
INNER JOIN scan_file ON scan.scan_file_id = scan_file.id
SET search_scan_filename.scan_file_id = scan_file.id;

-- Add to do in batches :  WHERE psm.search_id > X AND psm.search_id <= Y;

