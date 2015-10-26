
--  This will create an initial user in the "proxl" database

--  The initial user will have user id "initial_proxl_user"

--  The initial user will have password "FJS483792nzmv,xc4#&@(!VMKSDL"


USE proxl;


INSERT INTO auth_user (username, password_hashed, email, user_access_level, enabled) 
VALUES ('initial_proxl_user', 'CBE805BE949A46C0E906266DD23899733A8766A059256B2A7C1174FBE29D0BBD', 'NONE', '0', '1');



INSERT INTO xl_user (auth_user_id, first_name, last_name, organization) 
VALUES ((SELECT id FROM auth_user WHERE username = 'initial_proxl_user'), 'Initial', 'User', 'NONE');
