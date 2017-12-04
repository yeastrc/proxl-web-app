

--  Add fields for Spectral Storage Service

ALTER TABLE scan_file 
ADD COLUMN spectral_storage_api_key VARCHAR(300) NULL DEFAULT NULL AFTER file_size,
ADD COLUMN spectral_storage_process_key_temp VARCHAR(300) NULL DEFAULT NULL AFTER spectral_storage_api_key,
ADD COLUMN create_date DATETIME NULL DEFAULT CURRENT_TIMESTAMP AFTER spectral_storage_process_key_temp;

UPDATE scan_file SET create_date = NULL ; -- Clear field for existing records since not valid

--  Drop table scan_spectrum_data since no longer used
DROP TABLE scan_spectrum_data;

--  remove all records in scan table with level 1
DELETE FROM scan WHERE level = 1

DROP TABLE scan_file_ms1_intensity_binned_summed_data;

DROP TABLE scan_retention_time;

