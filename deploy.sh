#!/bin/bash
# usage: ./deploy.sh 10.0.3.2 opc oh-common

./setenv.sh

HOST=$1
USER=$2
MODULE_NAME=$3

sftp $USER@$HOST << SFTP_EOF
cd edublock/$MODULE_NAME
put $MODULE_NAME-$VERSION.war $MODULE_NAME-$VERSION.war_
SFTP_EOF

ssh $USER@$HOST << SSH_EOF
cd edublock/$MODULE_NAME
./shutdown.sh
sleep 1
mv $MODULE_NAME-$VERSION.war war/$MODULE_NAME-${VERSION}_${TODAY}_$TOTIME.war
mv $MODULE_NAME-$VERSION.war_ $MODULE_NAME-$VERSION.war
./startup.sh
SSH_EOF
