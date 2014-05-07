#!/bin/bash
toolname=irontemplate

echo "Toolname =" $toolname

cn="$toolname.trust.f4.hs-hannover.de"
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

