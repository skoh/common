@REM usage: dkbuild oh-common

call dksetenv %1

@REM call build %1
docker image prune -f
docker rmi -f %FULL_NAME%
docker build -t %FULL_NAME% --build-arg NAME=%NAME% .
@REM docker network create did
@REM docker images [-a]
