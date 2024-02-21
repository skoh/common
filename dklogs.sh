# usage: ./dklogs.sh oh-common

./dksetenv.sh $1

docker logs -f --tail 100 $NAME
