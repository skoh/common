@REM usage: dkrun oh-common 8010

call setenv
call dksetenv %1 %2

docker stop %NAME%
docker rm %NAME%
docker run -p 1%PORT%:%PORT% --network did --network-alias %NAME% --name %NAME% -e NAME="%NAME%" -e JAVA_OPTS="%JAVA_OPTS%" ^
-v C:/Users/%USERNAME%/did/files:/files ^
-v C:/Users/%USERNAME%/did/config:/config ^
-v C:/Users/%USERNAME%/did/logs:/logs ^
--restart always -d %FULL_NAME%
@REM docker ps [-a]
