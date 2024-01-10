#!/bin/bash

## Note: we rely on "standard" LDAP schemas as defined in /usr/share/dirsrv/schema

dsconf localhost backend create --suffix="dc=example,dc=com" --be-name="example"

# We must wait for dsconf to finish.  It apparently returns before server is actually ready for following ldapadds
# TODO: maybe use: dsctl --json slapd-localhost healthcheck
sleep 5

echo "Adding OU example"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: dc=example,dc=com
dc: example
objectClass: dcObject
objectClass: organizationalUnit
ou: example
EOF

echo "Adding OU People"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: ou=People,dc=example,dc=com
objectClass: top
objectClass: organizationalUnit
ou: People
EOF

echo "Adding User jdoe"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: uid=jdoe,ou=People,dc=example,dc=com
uid: jdoe
givenName: John
objectClass: inetorgperson
sn: Doe
cn: John Doe
EOF

echo "Adding ops management group"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: cn=opsmngd,dc=example,dc=com
cn: opsmngd
objectclass: groupOfNames
member: uid=jdoe,ou=People,dc=example,dc=com
EOF
