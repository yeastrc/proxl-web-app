
--  Rename OLD tables to not delete yet

ALTER TABLE  linker_per_search_crosslink_mass
RENAME TO  z_old_unused__linker_per_search_crosslink_mass ;

ALTER TABLE linker_per_search_cleaved_crosslink_mass 
RENAME TO  z_old_unused__linker_per_search_cleaved_crosslink_mass ;

ALTER TABLE  linker_per_search_monolink_mass
RENAME TO  z_old_unused__linker_per_search_monolink_mass ;

ALTER TABLE  search_linker
RENAME TO  z_old_unused__search_linker ;

ALTER TABLE  linker
RENAME TO  z_old_unused__linker ;



