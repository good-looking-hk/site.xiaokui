# 本地统一预发布目录
export PRE_DIR=/xiaokui/pre
# 进入代码工作目录
cd /home/hk-pc/JavaSpace/newxiaokui
# 清除缓存 -p指定目录
gralde -p k-new-blog/eladmin-system clean
# 开始构建
gralde -p k-new-blog/eladmin-system bootJar
# 上一步不成功则退出
if [ $? -ne 0 ]; then
  exit 1
fi

# 进入构建后的生成jar包目录
cd k-new-blog/eladmin-system/build/libs
# 获取文件，由于之前已经执行了clean，一般来说只有一个文件，这里兼容jar和war包
file_name=$(ls)
echo '找到文件' "$file_name"
jar_suffix='.jar'
war_suffix='.war'
if [[ $file_name == *$jar_suffix ]]; then
  cp "$(pwd)"'/'"$file_name" "$PRE_DIR"
elif [[ $file_name == *$war_suffix ]]; then
  echo $war_suffix
else
  echo $war_suffix
fi
gralde -p k-new-blog clean
echo '构建完成'
