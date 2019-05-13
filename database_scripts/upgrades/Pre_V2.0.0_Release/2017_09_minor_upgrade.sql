
--  change psm.charge to NOT NULL

ALTER TABLE psm
CHANGE COLUMN charge charge SMALLINT(6) NOT NULL ;