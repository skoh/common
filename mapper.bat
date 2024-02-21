@REM usage: mapper oh-common org.oh.sample.model.Sample org.oh.sample.mapper.SampleMapper src/main/resources/mapper/mysql/SampleMapper.xml

call setenv

set MODULE_NAME=%1
set MODEL_FULL_NAME=%2
set MAPPER_FULL_NAME=%3
set MAPPER_FILE_PATH=%4

gradlew -S %MODULE_NAME%:mapper --args="%MODEL_FULL_NAME% %MAPPER_FULL_NAME% %MAPPER_FILE_PATH%"
