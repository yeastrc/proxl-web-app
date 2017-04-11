
--  ONLY USE after file create_empty_database.sql

USE proxl ;

-- -----------------------------------------------------
-- Table user_mgmt_user
-- -----------------------------------------------------
DROP TABLE IF EXISTS user_mgmt_user ;

CREATE TABLE  user_mgmt_user (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  first_name VARCHAR(255) NOT NULL,
  last_name VARCHAR(255) NOT NULL,
  organization VARCHAR(2000) NULL,
  last_login DATETIME NULL DEFAULT NULL,
  last_login_ip VARCHAR(255) NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  created_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  global_admin_user TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (id))
ENGINE = InnoDB;

CREATE UNIQUE INDEX email_UNIQUE ON user_mgmt_user (email ASC);

CREATE UNIQUE INDEX username_UNIQUE ON user_mgmt_user (username ASC);


-- -----------------------------------------------------
-- Table password_mgmt_user_password
-- -----------------------------------------------------
DROP TABLE IF EXISTS password_mgmt_user_password ;

CREATE TABLE  password_mgmt_user_password (
  user_id INT UNSIGNED NOT NULL,
  password_hashed VARCHAR(255) NOT NULL,
  last_password_change TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  last_password_change_user_ip VARCHAR(45) NULL,
  last_password_reset TIMESTAMP NULL,
  last_password_reset_user_ip VARCHAR(45) NULL,
  PRIMARY KEY (user_id),
  CONSTRAINT yrc_password_mgmt_user_password_user_id
    FOREIGN KEY (user_id)
    REFERENCES user_mgmt_user (id)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Password table for code in yrc_password_mgmt_public.jar';



-- SET SQL_MODE=@OLD_SQL_MODE;
-- SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
-- SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

