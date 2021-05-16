### mysql创建目录
mysqld --basedir=/home/hk-pc/DEV/mysql/3307 --datadir=/home/hk-pc/DEV/mysql/3307/data --user=mysql
mysqld --basedir=/home/hk-pc/DEV/mysql/3308 --datadir=/home/hk-pc/DEV/mysql/3308/data --user=mysql
mysqld --basedir=/home/hk-pc/DEV/mysql/3309 --datadir=/home/hk-pc/DEV/mysql/3309/data --user=mysql

### hk-pc@hk-pc:~/JavaSpace/newxiaokui$ mysql_install_db --defaults-file=/home/hk-pc/DEV/mysql/3307/my.conf
#Installing MariaDB/MySQL system tables in '/home/hk-pc/DEV/mysql/3307/data' ...
#OK
#
#To start mysqld at boot time you have to copy
#support-files/mysql.server to the right place for your system
#
#PLEASE REMEMBER TO SET A PASSWORD FOR THE MariaDB root USER !
#To do so, start the server, then issue the following commands:
#
#'/usr/bin/mysqladmin' -u root password 'new-password'
#'/usr/bin/mysqladmin' -u root -h hk-pc password 'new-password'
#
#Alternatively you can run:
#'/usr/bin/mysql_secure_installation'
#
#which will also give you the option of removing the test
#databases and anonymous user created by default.  This is
#strongly recommended for production servers.
#
#See the MariaDB Knowledgebase at http://mariadb.com/kb or the
#MySQL manual for more instructions.
#
#You can start the MariaDB daemon with:
#cd '/usr' ; /usr/bin/mysqld_safe --datadir='/home/hk-pc/DEV/mysql/3307/data'
#
#You can test the MariaDB daemon with mysql-test-run.pl
#cd '/usr/mysql-test' ; perl mysql-test-run.pl
#
#Please report any problems at http://mariadb.org/jira
#
#The latest information about MariaDB is available at http://mariadb.org/.
#You can find additional information about the MySQL part at:
#http://dev.mysql.com
#Consider joining MariaDB's strong and vibrant community:
#https://mariadb.org/get-involved/
### 初始化数据库
mysql_install_db --defaults-file=/home/hk-pc/DEV/mysql/3307/my.conf
mysql_install_db --defaults-file=/home/hk-pc/DEV/mysql/3308/my.conf
mysql_install_db --defaults-file=/home/hk-pc/DEV/mysql/3309/my.conf


### 启动数据库
mysqld_safe --defaults-file=/home/hk-pc/DEV/mysql/3307/my.conf &
mysqld_safe --defaults-file=/home/hk-pc/DEV/mysql/3308/my.conf &
mysqld_safe --defaults-file=/home/hk-pc/DEV/mysql/3309/my.conf &

###登录从数据库，//mysql-bin.000001，show master status 为之前master查到的状态值。
change master to master_host='127.0.0.1',master_user='root',master_password='199710',master_log_file='mysql-bin.000001',master_log_pos=1493;
change master to master_host='127.0.0.1',master_user='root',master_password='199710',master_log_file='mysql-bin.000003',master_log_pos=3260;
show slave status;

### 登录从数据库
mysql -h 127.0.0.1 -u root -P 3307 -p
mysql -h 127.0.0.1 -u root -P 3308 -p
mysql -h 127.0.0.1 -u root -P 3309 -p

### 找出3307
netstat -nptl|grep 3307
netstat -nptl|grep 3308
netstat -nptl|grep 3309