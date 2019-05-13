

ALTER TABLE project_search 
DROP COLUMN active_project_id_search_id_unique_record,
DROP INDEX project_id_search_id_active_p_id_s_id_u_r_unique_idx ,
ADD UNIQUE INDEX project_id_search_id_unique_idx (project_id ASC, search_id ASC);
