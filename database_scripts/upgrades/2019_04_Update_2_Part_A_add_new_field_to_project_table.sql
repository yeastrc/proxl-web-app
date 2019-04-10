
ALTER TABLE project 
ADD COLUMN short_name VARCHAR(255) NULL AFTER title,
ADD UNIQUE INDEX short_name (short_name ASC);


