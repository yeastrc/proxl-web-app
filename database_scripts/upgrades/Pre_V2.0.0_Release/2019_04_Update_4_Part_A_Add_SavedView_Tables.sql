
--  2019_04_Update_4_Part_A_Add_SavedView_Tables.sql

CREATE TABLE IF NOT EXISTS data_page_saved_view_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  project_id INT UNSIGNED NOT NULL,
  page_name VARCHAR(80) NOT NULL,
  label VARCHAR(500) NOT NULL,
  url_start_at_page_name VARCHAR(6000) NOT NULL,
  page_query_json_string VARCHAR(6000) NOT NULL,
  auth_user_id_created_record INT UNSIGNED NOT NULL,
  auth_user_id_last_updated_record INT UNSIGNED NOT NULL,
  date_record_created DATETIME NULL,
  date_record_last_updated DATETIME NULL,
  PRIMARY KEY (id),
  INDEX fk_data_page_saved_view_tbl_project_id_idx (project_id ASC),
  CONSTRAINT fk_data_page_saved_view_tbl_project_id
    FOREIGN KEY (project_id)
    REFERENCES project (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS data_page_saved_view_assoc_project_search_id_tbl (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  assoc_main_id INT UNSIGNED NOT NULL,
  project_search_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  INDEX fk_dtpg_svdvw_ascprjsch_tbl_mnid_idx (assoc_main_id ASC),
  CONSTRAINT fk_dtpg_svdvw_ascprjsch_tbl_mnid
    FOREIGN KEY (assoc_main_id)
    REFERENCES data_page_saved_view_tbl (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


