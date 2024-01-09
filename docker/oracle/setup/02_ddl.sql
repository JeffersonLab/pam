alter session set container = XEPDB1;

-- Sequences
CREATE SEQUENCE PAM_OWNER.STAFF_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

CREATE SEQUENCE PAM_OWNER.WORKGROUP_ID
    INCREMENT BY 1
    START WITH 1
    NOCYCLE
    NOCACHE
    ORDER;

-- Tables
CREATE TABLE PAM_OWNER.STAFF
(
    STAFF_ID         NUMBER       NOT NULL,
    USERNAME         VARCHAR2(32),
    LASTNAME         VARCHAR2(60),
    FIRSTNAME        VARCHAR2(30),
    EMAIL            VARCHAR2(30),
    WORK_PHONE       VARCHAR2(7),
    DIVISION         VARCHAR2(80),
    UNIX_UID         NUMBER,
    HOME_DIR         VARCHAR2(40),
    STATUS           VARCHAR2(8),
    ORGANIZATION     VARCHAR2(80),
    BUILDING_NUM     VARCHAR2(10),
    ROOM_NUM         VARCHAR2(10),
    CONSTRAINT STAFF_PK PRIMARY KEY (STAFF_ID),
    CONSTRAINT STAFF_AK1 UNIQUE (USERNAME),
    CONSTRAINT STAFF_CK1 CHECK (STATUS in ('ACTIVE', 'INACTIVE'))
);

CREATE TABLE PAM_OWNER.WORKGROUP
(
    WORKGROUP_ID         INTEGER NOT NULL ,
    NAME                 VARCHAR2(256 CHAR) NOT NULL ,
    DESCRIPTION          VARCHAR2(512 CHAR) NOT NULL ,
    CONSTRAINT WORKGROUP_PK PRIMARY KEY (WORKGROUP_ID),
    CONSTRAINT WORKGROUP_AK1 UNIQUE (NAME)
);

CREATE TABLE PAM_OWNER.WORKGROUP_MEMBERSHIP
(
    STAFF_ID             INTEGER NOT NULL ,
    WORKGROUP_ID         INTEGER NOT NULL ,
    CONSTRAINT WORKGROUP_MEMBERSHIP_PK PRIMARY KEY (STAFF_ID,WORKGROUP_ID),
    CONSTRAINT WORKGROUP_MEMBERSHIP_FK1 FOREIGN KEY (STAFF_ID) REFERENCES PAM_OWNER.STAFF (STAFF_ID) ON DELETE CASCADE,
    CONSTRAINT WORKGROUP_MEMBERSHIP_FK2 FOREIGN KEY (WORKGROUP_ID) REFERENCES PAM_OWNER.WORKGROUP (WORKGROUP_ID) ON DELETE CASCADE
);

-- Functions
create or replace FUNCTION PAM_OWNER.WORKGROUP_ID_FROM_NAME ( p_name varchar2 ) return number
    is
begin
    declare
        cursor c_workgroup is
            select workgroup_id from pam_owner.workgroup where name = p_name;
        c_val 		c_workgroup%ROWTYPE;

    begin
        open c_workgroup;
        fetch c_workgroup into c_val;
        close c_workgroup;
        return c_val.workgroup_id;
    end;
end;

-- Make schema objects available to other schemas implicitly
--create public synonym workgroup for workgroup;
--create public synonym workgroup_membership for workgroup_membership;
--grant select on workgroup_membership to public;
--grant select on workgroup to public;
--create public synonym workgroup_id_from_name for workgroup_id_from_name;
--grant execute on workgroup_id_from_name to public;
--grant references on workgroup to public;
--grant references on workgroup_membership to public;

