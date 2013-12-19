#!/bin/bash
toolname=irontemplate

echo "Toolname =" $toolname

cn="irontemplate.trust.f4.hs-hannover.de"
ou="Trust@HsH"
o="Hochschule Hannover"
l="Hannover"
st="Niedersachsen"
c="DE"
dname="cn=$cn, ou=$ou, o=$o, l=$l, st=$st, c=$c"

echo "CN =" $cn
echo "OU =" $ou
echo "O =" $o
echo "L =" $l
echo "ST =" $st
echo "C =" $c

echo "Generating keystore: keytool -genkey -dname \"$dname\" -keyalg RSA -alias $toolname -keystore $toolname.jks -storepass $toolname -validity 3650 -keysize 2048"
keytool -genkey -dname "$dname" -keyalg RSA -alias $toolname -keystore $toolname.jks -storepass $toolname -validity 3650 -keysize 2048

echo "Exporting certificate: keytool -keystore $toolname.jks -storepass $toolname -exportcert -alias $toolname -file $toolname.pem -rfc"
keytool -keystore $toolname.jks -storepass $toolname -exportcert -alias $toolname -file $toolname.pem -rfc

echo "Importing irond certificate into $toolname keystore: keytool -importcert -file irond.pem -keystore $toolname.jks -alias irond"
# keytool -importcert -file irond.pem -keystore $toolname.jks -alias irond

echo "Importing $toolname certificate into irond-keystore: keytool -importcert -file $toolname.pem -keystore irond.jks -alias $toolname"
# keytool -importcert -file $toolname.pem -keystore irond.jks -alias $toolname