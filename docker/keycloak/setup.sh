#!/bin/bash

KEYCLOAK_REALM_DISPLAY_NAME="TEST REALM"
KEYCLOAK_DEBUG='["true"]'
KEYCLOAK_LDAP_CONNECTION_URL='["ldap://dirsrv:3389"]'
KEYCLOAK_USERS_DN='["cn=users,cn=accounts,dc=example,dc=com"]'
KEYCLOAK_BIND_DN='["cn=Directory Manager"]'
KEYCLOAK_BIND_CREDENTIAL='["password"]'
KEYCLOAK_USER_OBJECT_CLASSES='["person","organizationalPerson","inetorgperson"]'
KEYCLOAK_KERBEROS_AUTHN='["false"]'
KEYCLOAK_KERBEROS_FOR_PASS='["false"]'

if [[ -z "${KEYCLOAK_HOME}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_HOME in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_SERVER_URL}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_SERVER_URL in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_ADMIN}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_ADMIN in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_ADMIN_PASSWORD}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_ADMIN_PASSWORD in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_REALM}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_REALM in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_RESOURCE}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_RESOURCE in environment"
    return 0
fi

if [[ -z "${KEYCLOAK_SECRET}" ]]; then
    echo "Skipping Keycloak Setup: Must provide KEYCLOAK_SECRET in environment"
    return 0
fi

echo "-----------------"
echo "| Step A: Login |"
echo "-----------------"
${KEYCLOAK_HOME}/bin/kcadm.sh config credentials --server "${KEYCLOAK_SERVER_URL}" --realm master --user "${KEYCLOAK_ADMIN}" --password "${KEYCLOAK_ADMIN_PASSWORD}"

echo "------------------------"
echo "| Step B: Create Realm |"
echo "------------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create realms -s id=${KEYCLOAK_REALM} -s realm="${KEYCLOAK_REALM}" -s enabled=true -s displayName="${KEYCLOAK_REALM_DISPLAY_NAME}" -s loginWithEmailAllowed=false

echo "-------------------------"
echo "| Step C: Create Client |"
echo "-------------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create clients -r "${KEYCLOAK_REALM}" -s clientId=${KEYCLOAK_RESOURCE} -s 'redirectUris=["https://localhost:8443/'${KEYCLOAK_RESOURCE}'/*"]' -s secret=${KEYCLOAK_SECRET} -s 'serviceAccountsEnabled=true'
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r "${KEYCLOAK_REALM}" --uusername service-account-${KEYCLOAK_RESOURCE} --cclientid realm-management --rolename view-users
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r "${KEYCLOAK_REALM}" --uusername service-account-${KEYCLOAK_RESOURCE} --cclientid realm-management --rolename view-authorization
${KEYCLOAK_HOME}/bin/kcadm.sh add-roles -r "${KEYCLOAK_REALM}" --uusername service-account-${KEYCLOAK_RESOURCE} --cclientid realm-management --rolename view-realm

echo "----------------------------------------"
echo "| Step D: Create LDAP Storage Provider |"
echo "----------------------------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create components -r ${KEYCLOAK_REALM} -s parentId=${KEYCLOAK_REALM} \
-s id=${KEYCLOAK_REALM}-ldap-provider -s name=${KEYCLOAK_REALM}-ldap-provider \
-s providerId=ldap -s providerType=org.keycloak.storage.UserStorageProvider \
-s config.debug=${KEYCLOAK_DEBUG} \
-s config.authType='["simple"]' \
-s config.vendor='["rhds"]' \
-s config.priority='["0"]' \
-s config.connectionUrl=${KEYCLOAK_LDAP_CONNECTION_URL} \
-s config.editMode='["READ_ONLY"]' \
-s config.usersDn=${KEYCLOAK_USERS_DN} \
-s config.serverPrincipal='[""]' \
-s config.bindDn="${KEYCLOAK_BIND_DN}" \
-s config.bindCredential=${KEYCLOAK_BIND_CREDENTIAL} \
-s 'config.fullSyncPeriod=["86400"]' \
-s 'config.changedSyncPeriod=["-1"]' \
-s 'config.cachePolicy=["NO_CACHE"]' \
-s config.evictionDay=[] \
-s config.evictionHour=[] \
-s config.evictionMinute=[] \
-s config.maxLifespan=[] \
-s config.importEnabled='["true"]' \
-s 'config.batchSizeForSync=["1000"]' \
-s 'config.syncRegistrations=["false"]' \
-s 'config.usernameLDAPAttribute=["uid"]' \
-s 'config.rdnLDAPAttribute=["uid"]' \
-s 'config.uuidLDAPAttribute=["uid"]' \
-s config.userObjectClasses="${KEYCLOAK_USER_OBJECT_CLASSES}" \
-s 'config.searchScope=["1"]' \
-s 'config.useTruststoreSpi=["ldapsOnly"]' \
-s 'config.connectionPooling=["true"]' \
-s 'config.pagination=["true"]' \
-s config.allowKerberosAuthentication=${KEYCLOAK_KERBEROS_AUTHN} \
-s config.keyTab='[""]' \
-s config.kerberosRealm='[""]' \
-s config.useKerberosForPasswordAuthentication=${KEYCLOAK_KERBEROS_FOR_PASS}

echo "----------------------"
echo "| Step E: Sync Users |"
echo "----------------------"
${KEYCLOAK_HOME}/bin/kcadm.sh create -r ${KEYCLOAK_REALM} user-storage/${KEYCLOAK_REALM}-ldap-provider/sync?action=triggerFullSync