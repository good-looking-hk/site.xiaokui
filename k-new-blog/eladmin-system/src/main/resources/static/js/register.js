/**
 * Created by HK on 2017/10/14 13:05.
 */
$(function () {
    if (window.top !== window.self) {
        window.top.location = window.location;
    }
});

$(function () {
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-red',
    });
    $('.checkbox').removeClass('hidden');
    $("#register-form").bootstrapValidator({
        fields: {
            username: {
                validators: {
                    notEmpty: {
                        message: '用户名不能为空'
                    }
                }
            },
            email: {
                validators: {
                    notEmpty: {
                        message: '邮箱名不能为空'
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: '密码不能为空'
                    }
                }
            },
            repeat: {
                validators: {
                    notEmpty: {
                        message: '需要确认密码'
                    },
                    identical: {//相同
                        field: 'password',
                        message: '两次密码不一致'
                    },
                }
            }
        },
        submitHandler: function (validator, form, submitButton) {
            var flag = $("input[name='yes']").is(":checked");
            if (!flag) {
                layer.msg("竟然不同意我很帅，哼");
                setTimeout("location.href = '/register'", 1500);
                return;
            }
            var username = $("input[name='username']").val();
            var email = $("input[name='email']").val();
            var password = $("input[name='password']").val();
            HK.post("/sys/register", {username: username, email: email, password: password}, "注册成功，正在跳转至登录页面", function () {
                HK.reLocate("/login", 1500);
            });
        }
    });
});
