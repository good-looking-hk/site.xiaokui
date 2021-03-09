echo 'JAVA_HOME='"$JAVA_HOME"
cd /usr/app/rocketmq-all-4.7.1-bin-release
/bin/bash bin/mqnamesrv & /bin/bash bin/mqbroker -n localhost:9876 -c conf/broker.conf

