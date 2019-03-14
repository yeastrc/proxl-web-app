
--  Populate new tables with data from existing tables

INSERT INTO search_linker_tbl (search_id, linker_abbr, linker_name) 
SELECT search_linker.search_id, linker.abbr AS linker_abbr, linker.name AS linker_name 
FROM 
search_linker
INNER JOIN linker on search_linker.linker_id = linker.id;

INSERT INTO linker_per_search_crosslink_mass_tbl (search_linker_id, search_id, crosslink_mass_double, crosslink_mass_string)
SELECT search_linker_tbl.id AS search_linker_id, 
lpscm.search_id, lpscm.crosslink_mass_double, lpscm.crosslink_mass_string
FROM linker_per_search_crosslink_mass AS lpscm
INNER JOIN linker ON lpscm.linker_id = linker.id
INNER JOIN search_linker_tbl ON linker.abbr = search_linker_tbl.linker_abbr AND lpscm.search_id = search_linker_tbl.search_id;

INSERT INTO linker_per_search_cleaved_crosslink_mass_tbl 
(search_linker_id, search_id, cleaved_crosslink_mass_double, cleaved_crosslink_mass_string) 
SELECT search_linker_tbl.id AS search_linker_id, 
lpscm.search_id, lpscm.cleaved_crosslink_mass_double, lpscm.cleaved_crosslink_mass_string
FROM linker_per_search_cleaved_crosslink_mass AS lpscm
INNER JOIN linker ON lpscm.linker_id = linker.id
INNER JOIN search_linker_tbl ON linker.abbr = search_linker_tbl.linker_abbr AND lpscm.search_id = search_linker_tbl.search_id;

INSERT INTO linker_per_search_monolink_mass_tbl (search_linker_id, search_id, monolink_mass_double, monolink_mass_string) 
SELECT search_linker_tbl.id AS search_linker_id, 
lpsmm.search_id, lpsmm.monolink_mass_double, lpsmm.monolink_mass_string
FROM linker_per_search_monolink_mass AS lpsmm
INNER JOIN linker ON lpsmm.linker_id = linker.id
INNER JOIN search_linker_tbl ON linker.abbr = search_linker_tbl.linker_abbr AND lpsmm.search_id = search_linker_tbl.search_id;


