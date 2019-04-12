
-- For updated jar yrc_password_mgmt_public.jar

ALTER TABLE password_mgmt_user_password
CHANGE COLUMN password_hashed password_hashed VARCHAR(255) NULL ,
CHANGE COLUMN last_password_change last_password_change TIMESTAMP NOT NULL ,
ADD COLUMN password_hashed_new VARCHAR(255) NULL AFTER last_password_reset_user_ip,
ADD COLUMN last_record_change TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP AFTER password_hashed_new;
