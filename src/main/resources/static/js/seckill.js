var seckill = {
    //封装秒杀相关ajax的url
    URL: {
        now: function () {
            return HK.ctxPath + '/sys/seckill/time/now';
        },
        expose: function (killProductId) {
            return HK.ctxPath + '/sys/seckill/' + killProductId + '/expose';
        },
        execute: function (killProductId, md5) {
            return HK.ctxPath + '/sys/seckill/' + killProductId + '/' + md5 + "/execute";
        }
    },
    //详情页秒杀逻辑
    detail: {
        //详情页初始化
        init: function (params) {
            //用户手机验证和登录，计时
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var killProductId = params['seckillId'];
            //秒杀倒计时
            $.get(seckill.URL.now(), {}, function (nowTime) {
                seckill.countDown(killProductId, nowTime, startTime, endTime);
            });
        }
    },
    countDown: function (killProductId, nowTime, startTime, endTime) {
        var seckillBox = $("#seckill-box");
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime + 1000);
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                //时间完成后的回调
                seckill.handleSecKill(killProductId, seckillBox);
            });
        } else {
            seckill.handleSecKill(killProductId, seckillBox);
        }
    },
    handleSecKill: function (killProductId, node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.expose(killProductId), {}, function (result) {
            //回调函数中执行交互
            var exposer = result;
            if (exposer['exposed']) {
                var md5 = exposer['md5'];
                var executeUrl = seckill.URL.execute(killProductId, md5);
                console.log('executeUrl:' + executeUrl);
                //只绑定一次点击事件
                $('#killBtn').one('click', function () {
                    //执行秒杀:1.禁用按钮 2.发送请求
                    $(this).addClass('disable');
                    $.post(executeUrl, {}, function (result) {
                        node.html('<span class="label label-success">' + result.statusInfo + '</span>')
                    });
                });
                node.show();
            } else {
                //客户端计时过快，重新倒计时
                var now = exposer['now'];
                var start = exposer['start'];
                var end = exposer['end'];
                seckill.countDown(killProductId, now, start, end);
            }
        });
    }
};