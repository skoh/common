# usage: ./dkrun.sh oh-common 8010

./setenv.sh
./dksetenv.sh $1 $2

docker stop $NAME
docker rm $NAME
docker run -p 1$PORT:PORT --network did --network-alias $NAME --name $NAME -e NAME="$NAME" -e JAVA_OPTS="$JAVA_OPTS" \
-v /home/$USER/did/files:/files \
-v /home/$USER/did/config:/config \
-v /home/$USER/did/logs:/logs \
--restart always -d $FULL_NAME
#docker ps [-a]
