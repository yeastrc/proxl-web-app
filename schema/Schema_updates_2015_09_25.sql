
use proxl;


CREATE TABLE search_for_xlinks_file (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT(10) UNSIGNED NOT NULL,
  filename VARCHAR(255) NOT NULL,
  path VARCHAR(2000) NOT NULL,
  sha1sum VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  INDEX filename (filename ASC),
  INDEX search_for_xlinks_file_search_id_idx (search_id ASC),
  CONSTRAINT search_for_xlinks_file_search_id
    FOREIGN KEY (search_id)
    REFERENCES proxl.search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


CREATE TABLE search_for_xlinks_psm (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  file_id INT(10) UNSIGNED NOT NULL,
  psm_id INT(10) UNSIGNED NOT NULL,
  data_key VARCHAR(255) NOT NULL,
  value VARCHAR(4000) NULL DEFAULT NULL,
  PRIMARY KEY (id),
  INDEX search_for_xlinks_psm_psm_id_fk_idx (psm_id ASC),
  INDEX search_for_xlinks_psm_file_id_idx (file_id ASC),
  CONSTRAINT search_for_xlinks_psm_psm_id_fk
    FOREIGN KEY (psm_id)
    REFERENCES proxl.psm (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT search_for_xlinks_psm_file_id
    FOREIGN KEY (file_id)
    REFERENCES proxl.search_for_xlinks_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;


CREATE TABLE search_for_xlinks_params_file (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  search_for_xlinks_file_id INT(10) UNSIGNED NOT NULL,
  filename VARCHAR(255) NOT NULL,
  path VARCHAR(2000) NOT NULL,
  sha1sum VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  INDEX filename (filename ASC),
  INDEX search_for_xlinks_file_search_for_xlinks_file_id_idx (search_for_xlinks_file_id ASC),
  CONSTRAINT search_for_xlinks_file_search_for_xlinks_file_id
    FOREIGN KEY (search_for_xlinks_file_id)
    REFERENCES proxl.search_for_xlinks_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE search_for_xlinks_params_key_value (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  file_id INT(10) UNSIGNED NOT NULL,
  data_key VARCHAR(255) NOT NULL,
  value VARCHAR(4000) NULL DEFAULT NULL,
  PRIMARY KEY (id),
  INDEX search_for_xlinks_params_key_value_file_id_idx (file_id ASC),
  CONSTRAINT search_for_xlinks_params_key_value_file_id
    FOREIGN KEY (file_id)
    REFERENCES proxl.search_for_xlinks_params_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;

CREATE TABLE search_for_xlinks_params_line (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  file_id INT(10) UNSIGNED NOT NULL,
  line VARCHAR(4000) NULL DEFAULT NULL,
  PRIMARY KEY (id),
  INDEX search_for_xlinks_params_key_value_file_id_idx (file_id ASC),
  CONSTRAINT search_for_xlinks_params_line_file_id
    FOREIGN KEY (file_id)
    REFERENCES proxl.search_for_xlinks_params_file (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_general_ci;
