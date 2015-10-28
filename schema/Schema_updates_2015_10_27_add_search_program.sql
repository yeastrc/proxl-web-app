

USE proxl;

CREATE TABLE IF NOT EXISTS search_program (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  short_name VARCHAR(100) NOT NULL,
  display_name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NULL,
  PRIMARY KEY (id))
ENGINE = InnoDB

CREATE TABLE IF NOT EXISTS search_program_version (
  search_id INT UNSIGNED NOT NULL,
  search_program_id INT UNSIGNED NOT NULL,
  version VARCHAR(100) NOT NULL,
  PRIMARY KEY (search_id, search_program_id),
  INDEX search_program_version_search_program_id_fk_idx (search_program_id ASC),
  CONSTRAINT search_program_version_search_program_id_fk
    FOREIGN KEY (search_program_id)
    REFERENCES search_program (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT search_program_version__search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB




INSERT INTO search__search_program
SELECT DISTINCT search_id , 1, substring( kojak_program_version, 15 )
FROM 
kojak_file INNER JOIN kojak_psm ON kojak_file.id = kojak_psm.kojak_file_id
INNER JOIN kojakpsm_psm ON kojak_psm.id = kojakpsm_psm.kojakpsm_id
INNER JOIN psm ON kojakpsm_psm.psm_id = psm.id
WHERE kojak_program_version LIKE "kojak%";

