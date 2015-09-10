


USE proxl;

INSERT INTO linker(abbr,name)VALUES( 'dss','disuccinimidyl suberate' );
INSERT INTO linker(abbr,name)VALUES( 'bs3','bis[sulfosuccinimidyl] suberate' );

INSERT INTO linker(abbr,name)VALUES( 'edc','1-ethyl-3-(3-dimethylaminopropyl)carbodiimide hydrochloride' );

INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'dss'),155.0946 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'dss'),156.0786 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'bs3'),155.0946 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'bs3'),156.0786 );

INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'edc'), -0.9837153 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'edc'), -0.9837 );


--  Mass Rounded to 2 decimal places to match current output from Kojak

INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'dss'),155.09 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'dss'),156.08 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'bs3'),155.09 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'bs3'),156.08 );
INSERT INTO linker_monolink_mass(linker_id,mass)VALUES( (SELECT id FROM linker WHERE abbr = 'edc'), -0.98 );
