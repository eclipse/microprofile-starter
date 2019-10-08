#!/bin/sh

# Generates certificates for JWT_AUTH demo examples.

pushd src/main/resources/

openssl genrsa -out private.key 2048
cat << EOF | openssl req -new -key private.key \
         -x509 -days 0 \
         -out publickey.cer
--
JWT
JWT
JWT
JWT
JWT
JWT
EOF

keytool -import -noprompt -alias theKeyId -keystore public.jks -file publickey.cer -storepass atbash

cat private.key | openssl pkcs8 -topk8 -nocrypt -out private.pem
openssl rsa -in private.pem -outform PEM -pubout -out public.pem
rm -rf private.key
rm -rf publickey.cer
mv public.jks ./files/liberty/public.jks.tpl
mv private.pem ./files/privateKey.pem.tpl
mv public.pem  ./files/publicKey.pem.tpl

# As soon as we switch to VertX JWT https://github.com/eclipse/microprofile-starter/issues/206
# This conversion won't be needed:
# org.bouncycastle.asn1.pkcs.PrivateKeyInfo cannot be cast to org.bouncycastle.openssl.PEMKeyPair
#openssl rsa -in ./files/privateKey.pem.tpl -text -out ./files/privateKey.pem.tpl.long
#mv ./files/privateKey.pem.tpl.long ./files/privateKey.pem.tpl

# KumuluzEE needs pub key in config
KEY=`cat ./files/publicKey.pem.tpl | grep -ve '^-' | tr -d '\n'`
sed -i "s~[ ]*public-key:.*~    public-key: $KEY~g" ./files/kumuluzEE/service-b/config.yaml.tpl

popd
