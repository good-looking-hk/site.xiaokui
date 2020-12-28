#!/usr/bin/bash
# @author HK
# @date 2020-03-21
# @version 1.1
source /etc/profile
rootJarPath=/xiaokui/product/eladmin-system-v2.0.jar
nohup=nohup

# 如果字符串长度为0
if test -z "$rootJarPath"
then
  echo 'jar包或war包路径为空' && exit 1;
fi

pid=$(ps -aux | grep -E "eladmin-system-v2.0.war|eladmin-system-v2.0.jar" | grep -v grep | grep -v 'bash -s' | awk '{print $2}')
echo "准备部署应用，找到目标PID:${pid}"

function runApp() {
    echo "开始部署应用，路径为${rootJarPath}，运行目录为${rootJarPath%/*},nohup=${nohup}"
    cd "${rootJarPath%/*}" || { echo "进入目录${rootJarPath%/}失败"; exit 1; }
    # 如果不为空
    if test -n "$nohup"
    then
        nohup java -jar $rootJarPath --server.port=9090 --spring.profiles.active=prod --kpwd=Z1w@9?1997&
    else
        java -jar $rootJarPath --server.port=9090 --spring.profiles.active=prod --kpwd=Z1w@9?1997&
    fi
}

# 如果找到pid数大于1
if test ${#pid[*]} -gt 1
then
    echo "自动部署应用失败，请手动部署，找到多个pid:" "${pid[@]}"
    exit 1
fi

# 如果找到pid为空
if test -z "$pid"
then
    runApp
    exit 0
fi

if test ${#pid[*]} -eq 1
then
    echo "杀掉原有PID：" "${pid[@]}" "，重新开始部署"
    kill -9 "${pid}"
    runApp
    exit 0
fi