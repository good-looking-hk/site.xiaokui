/**
 * Created by HK on 2017/10/8 22:35.
 */
function fn(event) {
    if (event.keyCode === 13) {
        $('.layui-layer-btn0').click();
    } else if (event.keyCode === 27) {
        $(document).off('keydown');
        $('.layui-layer-btn1').click();
    }
}

var vm = new Vue({
    el: '#manager',
    data: {
    },
    methods: {
        adjustIframeSize: function () {
            $(window).resize(function () {
                var $content = $('.content');
                $content.height($(this).height());
                $content.find('iframe').first().height($content.height() - 52);
            });
            $(window).resize();
        },
        showWelcome: function () {
            var now = new Date(), hour = now.getHours();
            var $showWelcome = $('#showWelcome');
            if (hour < 6) {
                $showWelcome.text("夜深了，早点休息吧~" );
            } else if (hour < 9) {
                $showWelcome.text("早上好~" );
            } else if (hour < 12) {
                $showWelcome.text("上午好~");
            } else if (hour < 14) {
                $showWelcome.text("中午好~");
            } else if (hour < 17) {
                $showWelcome.text("下午好~");
            } else if (hour < 19) {
                $showWelcome.text("傍晚好~");
            } else if (hour < 23) {
                $showWelcome.text("晚上好~");
            } else {
                $showWelcome.text("熬夜对身体不好哟~")
            }
        },
        addMenuListener: function () {
            $(".sidebar-menu li").not(".treeview").find("a").click(function (evt) {
                var url = $(this).attr("href").substring(1);
                $(".sidebar-menu li").removeClass("active");
                $(this).parent().addClass("active");
                loadPage(url);
                return false;
                // return true;
            });
        },
        updateUsername: function () {
            layer.prompt({
                title: '输入新昵称：',
                success: function (layero, index) {
                    $(document).on('keydown', fn);
                }
            }, function (value, index, elem) {
                if (value === '') {
                    return false;
                }
                if (value.length > 10) {
                    layer.msg('不能超过10个字符');
                    return false;
                }
                $(document).off('keydown');
                $.post('/sys/user/update', {username: value}, function (response) {
                    if (response.code === 200) {
                        layer.msg('修改成功', {icon: 1, time: 2000});//默认3000
                        $('.username').text(value);
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 2000});
                    }
                });
                layer.close(index);
            });
        },
        updateBlogSpace: function () {
            layer.prompt({
                title: '输入新博客空间名称：',
                success: function (layero, index) {
                    $(document).on('keydown', fn);
                }
            }, function (value, index, elem) {
                if (value === '') {
                    return false;
                }
                if (value.length > 16) {
                    layer.msg('不能超过16个字符');
                    return false;
                }
                $(document).off('keydown');
                $.post('/sys/user/update', {blogSpace: value}, function (response) {
                    if (response.code === 200) {
                        $('.blogSpace').attr('href', '/blog/' + value);
                        layer.msg('修改成功', {icon: 1, time: 2000});//默认3000
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 2000});
                    }
                });
                layer.close(index);
            });
        },
        updateDescription: function () {
            layer.prompt({
                title: '输入新签名：',
                success: function (layero, index) {
                    $(document).on('keydown', fn);
                }
            }, function (value, index, elem) {
                if (value === '') {
                    return false;
                }
                if (value.length > 10) {
                    layer.msg("不能超过10个字符")
                }
                $(document).off('keydown');
                $.post('/sys/user/update', {selfDescription: value}, function (response) {
                    if (response.code === 200) {
                        layer.msg('修改成功', {icon: 1, time: 2000});
                        $('.selfDescription').text(value);
                    } else {
                        layer.msg(response.msg, {icon: 2, time: 2000});
                    }
                });
                layer.close(index);
            });
        },
        updatePassword: function () {
            layer.prompt({
                formType: 1,
                title: '输入原密码：',
                success: function (layero, index) {
                    $(document).on('keydown', fn);
                }
            }, function (oldValue, index, elem) {
                layer.close(index);
                layer.prompt({
                    formType: 1,
                    title: '输入新密码：'
                }, function (newValue, index, elem) {
                    layer.close(index);
                    layer.prompt({
                        formType: 1,
                        title: '确认密码：'
                    }, function (sureValue, index, elem) {
                        $(document).off('keydown');
                        layer.close(index);
                        if (newValue !== sureValue) {
                            layer.msg("两次密码不一致", {icon: 2, time: 2000});
                            return;
                        }
                        $.post('/sys/user/update', {password: oldValue, newPassword: newValue}, function (response) {
                            if (response.code === 200) {
                                layer.msg('修改成功', {icon: 2, time: 3000});
                                window.location.href = '/login';
                            } else {
                                layer.msg(response.msg, {icon: 2, time: 2000});
                            }
                        });
                    });
                });
            });
        },
        logout: function () {
            $.post("/sys/logout", function (response) {
                location.href = '/login.html';
            })
        }
    },
    created: function () {
        this.adjustIframeSize();
        this.showWelcome();
    },
    mounted: function () {
        this.addMenuListener();
        var url = window.location.hash.substring(1);
        loadPage(url);
    }
});

function loadPage(url) {
    $("iframe").attr("src", url);
}

layui.use(['form', 'layer', 'upload'], function () {
    var form = layui.form, layer = layui.layer, upload = layui.upload;
    var fileName;
    var uploadImg = upload.render({
        elem: '#uploadImg', //绑定元素
        url: '/sys/user/img', //上传接口
        accept: 'images',
        auto: true,
        multipart: false,
        size: 512,
        before: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function (index, file, result) {
                fileName = file.name;
            });
        },
        done: function (res) {
            if (res.code === 200) {
                layer.msg("上传成功", {icon: 1});
                vm.headImg = '/image/headImg/' + fileName;
                $('.headImg').attr('src', vm.headImg);
            } else {
                layer.msg(res.msg, {icon: 2});
            }
        },
        error: function () {
            layer.msg("本地上传失败", {icon: 2});
        }
    });
    var uploadResume = upload.render({
        elem: '#uploadResume', //绑定元素
        url: '/sys/blog/user', //上传接口
        accept: 'file',
        exts: 'html',
        auto: true,
        multipart: false,
        size: 512,
        before: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function (index, file, result) {
                fileName = file.name;
                if (!fileName.startsWith("简历")) {
                    this.error()
                }
            });
        },
        done: function (res) {
            if (res.code === 200) {
                layer.msg("上传成功", {icon: 1});
            } else {
                layer.msg(res.msg, {icon: 2});
            }
        },
        error: function () {
            layer.msg("本地上传失败", {icon: 2});
        }
    });
    var uploadAbout = upload.render({
        elem: '#uploadAbout', //绑定元素
        url: '/sys/blog/user', //上传接口
        accept: 'file',
        exts: 'html',
        auto: true,
        multipart: false,
        size: 512,
        before: function (obj) {
            //预读本地文件示例，不支持ie8
            obj.preview(function (index, file, result) {
                fileName = file.name;
                if (!fileName.startsWith("关于")) {
                    this.error()
                }
            });
        },
        done: function (res) {
            if (res.code === 200) {
                layer.msg("上传成功", {icon: 1});
            } else {
                layer.msg(res.msg, {icon: 2});
            }
        },
        error: function () {
            layer.msg("本地上传失败", {icon: 2});
        }
    });
});