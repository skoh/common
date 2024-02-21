@REM usage: dkstop oh-common

call dksetenv %1

docker stop %NAME%
