# ln -s /usr/elk/logstash-7.8.0 logstash
cd /usr/elk/logstash-7.8.0/bin && ./logstash -f ../filebeat-to-es.conf