# usage: ./mapper.sh oh-common org.oh.sample.model.Sample org.oh.sample.mapper.SampleMapper src/main/resources/mapper/mysql/SampleMapper.xml

./setenv.sh

MODULE_NAME=$1
MODEL_FULL_NAME=$2
MAPPER_FULL_NAME=$3
MAPPER_FILE_PATH=$4

./gradlew -S $MODULE_NAME:mapper --args="$MODEL_FULL_NAME $MAPPER_FULL_NAME $MAPPER_FILE_PATH"
