alter session set container = XEPDB1;

ALTER SYSTEM SET db_create_file_dest = '/opt/oracle/oradata';

create tablespace PAM;

create user "PAM_OWNER" profile "DEFAULT" identified by "password" default tablespace "PAM" account unlock;

grant connect to PAM_OWNER;
grant unlimited tablespace to PAM_OWNER;

grant create view to PAM_OWNER;
grant create sequence to PAM_OWNER;
grant create table to PAM_OWNER;
grant create procedure to PAM_OWNER;
grant create type to PAM_OWNER;
grant create trigger to PAM_OWNER;
