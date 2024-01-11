#!/bin/bash

## Note: we rely on "standard" LDAP schemas as defined in /usr/share/dirsrv/schema

dsconf localhost backend create --suffix="dc=example,dc=com" --be-name="example"

# We must wait for dsconf to finish.  It apparently returns before server is actually ready for following ldapadds
# TODO: maybe use: dsctl --json slapd-localhost healthcheck
sleep 5

SUFFIX="dc=example,dc=com"

# Add structure
echo "Adding example dcObject"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: dc=example,dc=com
dc: example
objectClass: dcObject
EOF

# We are simulating freeipa LDAP so need freeipa flat schema
# See freeipa/install/share/bootstrap-template.ldif
echo "Adding accounts nsContainer"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: cn=accounts,$SUFFIX
changetype: add
objectClass: top
objectClass: nsContainer
cn: accounts
EOF

echo "Adding users nsContainer"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: cn=users,cn=accounts,$SUFFIX
changetype: add
objectClass: top
objectClass: nsContainer
cn: users
EOF

echo "Adding groups nsContainer"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: cn=groups,cn=accounts,$SUFFIX
changetype: add
objectClass: top
objectClass: nsContainer
cn: groups
EOF

## Now add some entries
echo "Adding User jdoe"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: uid=jdoe,cn=users,cn=accounts,dc=example,dc=com
uid: jdoe
givenName: John
objectClass: inetorgperson
sn: Doe
cn: John Doe
EOF

echo "Adding ops management group"
ldapadd -D "cn=Directory Manager" -w password -H ldap://ldap:3389 -x <<EOF
dn: cn=opsmngd,cn=groups,cn=accounts,dc=example,dc=com
cn: opsmngd
objectclass: groupOfNames
member: uid=jdoe,ou=People,dc=example,dc=com
EOF
