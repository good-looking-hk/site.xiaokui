#!/usr/bin/bash
# @author HK
# @date 2020-03-21
# @version 1.1
source /etc/profile
cd /xiaokui/product
rm -rf dist
unzip dist.zip
nginx -s reload