# usage: ./dkbuild.sh oh-common

./dksetenv.sh $1

#./build.sh $1
docker image prune -f
docker rmi -f $FULL_NAME
docker build -t $FULL_NAME --build-arg NAME=$NAME .
#docker network create did
#docker images [-a]
