


use proxl;

CREATE TABLE IF NOT EXISTS cutoffs_applied_on_import (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  search_id INT UNSIGNED NOT NULL,
  annotation_type_id INT UNSIGNED NOT NULL,
  cutoff_value_string VARCHAR(255) NOT NULL,
  cutoff_value_double DOUBLE NOT NULL,
  PRIMARY KEY (id),
  INDEX cutoffs_at_import_ann_type_id_fk_idx (annotation_type_id ASC),
  INDEX cutoffs_applied_on_import_search_id_fk_idx (search_id ASC),
  CONSTRAINT cutoffs_applied_on_import_ann_type_id_fk
    FOREIGN KEY (annotation_type_id)
    REFERENCES annotation_type (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT cutoffs_applied_on_import_search_id_fk
    FOREIGN KEY (search_id)
    REFERENCES search (id)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB

