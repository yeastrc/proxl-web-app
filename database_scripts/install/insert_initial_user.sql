

USE proxl ;

--  This will create an initial user in the "proxl" database
--  The initial user will have user id "initial_proxl_user"
--  The initial user will have password "FJS483792nzmv,xc4#&@(!VMKSDL"

--  This is required to have an 'id' of 1.  There is hard coding in the app for this id of 1.

INSERT INTO user_mgmt_user (id, username, email, first_name, last_name, organization, enabled, created_date, global_admin_user) 
VALUES (1, 'initial_proxl_user', 'NONE', 'INITIAL USER', '', '', 1, NOW(), 1 );

INSERT INTO password_mgmt_user_password (user_id, password_hashed)
 VALUES (1, 'CBE805BE949A46C0E906266DD23899733A8766A059256B2A7C1174FBE29D0BBD');
 
