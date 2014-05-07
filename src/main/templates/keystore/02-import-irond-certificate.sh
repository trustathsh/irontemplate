#!/bin/bash
toolname=irontemplate

echo "Toolname =" $toolname

echo "Importing irond certificate into $toolname keystore: keytool -importcert -file irond.pem -keystore $toolname.jks -alias irond"
keytool -importcert -file irond.pem -keystore $toolname.jks -alias irond

