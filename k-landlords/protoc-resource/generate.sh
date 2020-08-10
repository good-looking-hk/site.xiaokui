# 安装好protoc命令后直接运行，即可生成对应的转换类
protoc -I=. --java_out=../k-landlords-common/src/main/java/ ./*.proto
echo '生成protoc相关的.java文件成功'