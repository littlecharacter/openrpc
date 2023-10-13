#!/bin/bash

PROG_NAME=$0
APP_NAME=$1
ACTION=$2
# ENV=$3
echo "APP_NAME=${APP_NAME}"
echo "ACTION=${ACTION}"
APP_START_TIMEOUT=20    # 等待应用启动的时间
APP_PORT=10926          # 应用端口
APP_HOME=/opt/app/${APP_NAME} # 从package.tgz中解压出来的jar包放到这个目录下
JAR_NAME=${APP_HOME}/${APP_NAME}.jar # jar包的名字
LOG_HOME=${APP_HOME}/logs
JAVA_OUT=${LOG_HOME}/start.log  #应用的启动日志

# TODO:健康检查
HEALTH_CHECK_URL=http://127.0.0.1:${APP_PORT}/${APP_NAME}  # 应用健康检查URL（curl调用一个健康检测接口看看是否正常）
HEALTH_CHECK_FILE_DIR=/opt/app/${APP_NAME}/status   # 脚本会在这个目录下生成nginx-status文件

# 可以使用配置文件配置
JAVA_OPTS="-Xmx128M -Xms128M -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:$LOG_HOME/gc`date +%Y%m%d`.log"


# 创建出相关目录
mkdir -p ${HEALTH_CHECK_FILE_DIR}
mkdir -p ${APP_HOME}
mkdir -p ${LOG_HOME}

usage() {
    # echo "Usage: ${PROG_NAME} {APP_NAME} {start|stop|restart} env"
    echo "Usage: ${PROG_NAME} {APP_NAME} {start|stop|restart}"
    exit 2
}

start_application() {
    echo "starting java process"
    # nohup java ${JAVA_OPTS} -jar ${JAR_NAME} --spring.profiles.active=${ENV} > ${JAVA_OUT} 2>&1 &
    nohup java ${JAVA_OPTS} -jar ${JAR_NAME} > ${JAVA_OUT} 2>&1 &
    echo "started java process"
}

stop_application() {
   checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`

   if [[ ! $checkjavapid ]];then
      echo -e "\rno java process"
      return
   fi

   echo "stop java process"
   times=60
   for e in $(seq 60)
   do
        sleep 1
        COSTTIME=$(($times - $e ))
        checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`
        if [[ $checkjavapid ]];then
            kill -9 $checkjavapid
            echo -e  "\r        -- stopping java lasts `expr $COSTTIME` seconds."
        else
            echo -e "\rjava process has exited"
            break;
        fi
   done
   echo ""
}

health_check() {
    exptime=0
    echo "checking ${HEALTH_CHECK_URL}"
    while true
        do
            status_code=`/usr/bin/curl -L -o /dev/null --connect-timeout 5 -s -w %{http_code}  ${HEALTH_CHECK_URL}`
            if [ "$?" != "0" ]; then
               echo -n -e "\rapplication not started"
            else
                echo "code is $status_code"
                if [ "$status_code" == "200" ];then
                    break
                fi
            fi
            sleep 1
            ((exptime++))

            echo -e "\rWait app to pass health check: $exptime..."

            if [ $exptime -gt ${APP_START_TIMEOUT} ]; then
                echo 'app start failed'
               exit 1
            fi
        done
    echo "check ${HEALTH_CHECK_URL} success"
}

start() {
    start_application
    # health_check
}
stop() {
    stop_application
}
case "$ACTION" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        stop
        start
    ;;
    *)
        usage
    ;;
esac