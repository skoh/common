@REM usage: dklogs oh-common

call dksetenv %1

docker logs -f --tail 100 $NAME
