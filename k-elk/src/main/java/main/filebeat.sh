# ln -s /usr/elk/filebeat-7.8.0-linux-x86_64/myfilebeat.yml ./myfilebeat.yml
# ln -s /usr/elk/filebeat-7.8.0-linux-x86_64 filebeat
cd /usr/elk/filebeat-7.8.0-linux-x86_64 && ./filebeat -e -c myfilebeat.yml
