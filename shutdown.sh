echo usage: ./shutdown.sh oh-common

MODULE_NAME=$1

pkill -F $MODULE_NAME.pid
#kill `cat $MODULE_NAME.pid`
