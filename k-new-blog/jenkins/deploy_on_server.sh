#!/bin/bash
# 该脚本主要用于自动部署jar到服务器，仅用于交流学习
# @author HK
# @date 2020-03-21
# @version 1.1
# 1.推荐给所有变量加上括号，便于阅读，例如${USER}，好于$USER，注意这里是大括号，后期还可以对大括号中的值进行修改
# 2.$(xxx)大致等同于`xxx`，新开子shell执行命令并返回
# 3.判断上一条命令是否执行完毕，可以使用 if [ $? eq 0 ]; then exit 1 fi，只要非0，就表示返回异常

readonly root_jar_path=$2
readonly remote_ip_list=("120.79.20.49")
readonly remote_user=("root")
readonly remote_passwd=$REMOTE_PASSWORD
readonly remote_target_dir="/xiaokui"
readonly base_path=$(cd .; pwd)

function startScp() {
    # 如果文件存在 -e exist
    if test -e "$root_jar_path"
    then
        echo "${root_jar_path}文件存在，开始复制到远程服务器"
    else
        echo "${root_jar_path}文件不存在，请确认存在后再执行本脚本"
        exit 1
    fi
    # for 语法是有两个小括号
    # ${#xx[@} 获取xx数组的长度
    for((i=0;i<${#remote_ip_list[@]};i++))
    do
        # 定义临时变量
        ip=${remote_ip_list[$i]}
        # 字符串长度不为0
        if test -n "$ip"
        then
          # 使用预期的命令交付
    	    expect -c "
            set timeout -1;
            spawn scp ${root_jar_path} ${remote_user[$i]}@${ip}:${remote_target_dir}
    	      expect {
                "*yes*" {send yes\r;exp_continue}
                "*pass*" {send ${remote_passwd[$i]}\r}
            }
            interact;"
          echo "复制到远程服务器$ip:${remote_target_dir[$i]}成功"
        fi
    done
    return 0
}

function login() {
    # 判断数组长度是否为1，这里的*可换位#
    # -eq / ne比较两数是否相等，= / != 判断两字符串是否相等
    if test ${#remote_ip_list[*]} -eq 1
    then
      echo "尝试登录远程服务器${remote_user[0]}"
	    expect -c "
        set timeout -1;
        spawn ssh ${remote_user[0]}@${remote_ip_list[0]}
	      expect {
            "*yes*" {send yes\r;exp_continue}
            "*pass*" {send ${remote_passwd[0]}\r}
        }
        interact;"
    fi
}

# 貌似这种方法并不适用，弃用之
function runJar() {
    if test ${#remote_ip_list[*]} -eq 1
    then
        echo "尝试登录远程服务器${remote_user[0]}，并自动执行部署脚本"
        filename=${root_jar_path##*/}
        ssh "${remote_user[0]}@${remote_ip_list[0]}" 'bash -s' < "${base_path}"/_stop_and_run.sh ${remote_target_dir[0]}/$filename $1
    fi
}

case $1 in
    go)
        startScp
        runJar nohup
        ;;
    scp)
        startScp
        ;;
    login)
        login
        ;;
    scp-login)
        startScp
        login
        ;;
    scp-run)
        startScp
        runJar
        ;;
    nohup-run)
        startScp
        runJar nohup
        ;;
    *)
        echo "后面参数为:{scp|login|scp-login)|scp-run|nohup-run}"
esac
