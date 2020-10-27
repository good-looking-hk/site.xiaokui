# ln -s /usr/elk/logstash-7.8.0 logstash
cd /usr/elk/logstash/bin && ./logstash -f ../config/filebeat-to-es.yml