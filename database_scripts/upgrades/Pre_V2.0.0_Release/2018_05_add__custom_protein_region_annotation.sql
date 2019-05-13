

-- New table custom_protein_region_annotation


CREATE TABLE  custom_protein_region_annotation (
  protein_sequence_version_id INT(10) UNSIGNED NOT NULL,
  project_id INT(10) UNSIGNED NOT NULL,
  start_position MEDIUMINT(8) UNSIGNED NOT NULL,
  end_position MEDIUMINT(8) UNSIGNED NOT NULL,
  annotation_color VARCHAR(255) NOT NULL,
  annotation_text VARCHAR(20000) NOT NULL,
  created_by INT(10) UNSIGNED NOT NULL,
  created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (protein_sequence_version_id, project_id, start_position, end_position),
  CONSTRAINT cust_prtn_rgn_anno_auth_user_fk
    FOREIGN KEY (created_by)
    REFERENCES auth_user (id)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT cust_prtn_rgn_anno_project_fk
    FOREIGN KEY (project_id)
    REFERENCES project (id)
    ON DELETE CASCADE,
  CONSTRAINT cust_prtn_rgn_anno_protn_s_v_id_fk
    FOREIGN KEY (protein_sequence_version_id)
    REFERENCES protein_sequence_version (id)
    ON DELETE CASCADE)
ENGINE = InnoDB;

CREATE INDEX cust_prtn_rgn_anno_auth_user_fk ON custom_protein_region_annotation (created_by ASC);

CREATE INDEX cust_prtn_rgn_anno_project_fk ON custom_protein_region_annotation (project_id ASC);

