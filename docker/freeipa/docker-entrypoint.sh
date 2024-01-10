#!/bin/bash

echo "-------------------------"
echo "| Step 1: Setup FreeIPA |"
echo "-------------------------"

mkdir -p /data/var/log/dirsrv
mkdir -p /data/var/lib/ipa/sysrestore
mkdir -p /data/var/lib/dirsrv
mkdir -p /data/etc/ipa
mkdir -p /data/etc/pkcs11/modules
mkdir -p /data/etc/dirsrv/schema
mkdir -p /data/etc/dirsrv/config

ipa-server-install -U -r EXAMPLE.COM -p password -a password -N

sleep infinity