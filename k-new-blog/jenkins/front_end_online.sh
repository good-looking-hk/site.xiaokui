# jenkins工作目录
readonly JENKINS_WORKDIR=$1
# 本地统一预发布目录
export PRE_DIR=/xiaokui/pre
# 进入代码工作目录
cd /home/hk-pc/study/eladmin-web-master/new-blog-web && echo '构建开始于:'; date
# 开始构建
npm run build:prod index

# 上一步不成功则退出
if [ $? -ne 0 ]; then
  exit 1
fi

# 压缩文件
zip -r dist.zip dist
chmod 777 dist.zip
cp dist.zip "$PRE_DIR"
cp dist.zip "$JENKINS_WORKDIR"
rm -rf dist
rm -rf dist.zip
echo '本地构建完成'

