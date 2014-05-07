#!/bin/bash
toolname=irontemplate

echo "Toolname =" $toolname

echo "Importing $toolname certificate into irond-keystore: keytool -importcert -file $toolname.pem -keystore irond.jks -alias $toolname"
keytool -importcert -file $toolname.pem -keystore irond.jks -alias $toolname

