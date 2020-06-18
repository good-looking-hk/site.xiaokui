$(function () {
    if (window.top !== window.self) {
        window.top.location = window.location;
    }
    var str = window.location.search;
    if(str.endsWith('forceOut=true')) {
        layer.msg("您的账号在另一个地方登录，您已下线", {
            icon: 7,
            time: 3000
        });
    } else if (str.endsWith('kickout=true')) {
        layer.msg("您已被管理员下线", {
            icon: 7,
            time: 3000
        });
    }
});

//判断当前字符串是否以str结束
if (typeof String.prototype.endsWith != 'function') {
    String.prototype.endsWith = function (str){
        return this.slice(-str.length).toLowerCase() == str.toLowerCase();
    };
}

$(function () {
    $('#login-form input').iCheck({
        checkboxClass: 'icheckbox_square-blue'
    });
    $('#register-form input').iCheck({
        checkboxClass: 'icheckbox_square-red'
    });
    $('.checkbox').removeClass('hidden');
});

$(document).on('keydown', function (event) {
    if (event.keyCode === 13) {
        $("#submitBtn").click();
    }
});

$("#submitBtn").click(function () {
    var username = $(".username1").val();
    var password = $(".password1").val();
    if (username === "" || password === "") {
        return false;
    }
    var flag = $("input[name='remember']").is(":checked") === true ? 'on' : 'off';
    HK.post("/sys/login", {loginName: username, password: password, remember: flag}, "登录成功", function () {
        HK.reLocate("/manage", 1500);
    });
});