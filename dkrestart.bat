@REM usage: dkrestart oh-common

call dksetenv %1

docker restart %NAME%
