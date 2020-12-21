/**
 * Created by HK on 2017/07/20.
 */
$(function () {
    var count = 0;
    var content = "";
    $("#content h2").each(function () {
        $(this).attr("id", count++);
        var attr = $(this).attr("id");
        var li = "<li><a class='jumper size-h2' href='#" + attr + "'>" + $(this).text() + "</a>";
        // h3的集合
        var son = $(this).nextUntil("h2", "h3");
        if (son.length > 0) {
            li += "<ul>";
        }
        son.each(function () {
            $(this).attr("id", count++);
            var attr = $(this).attr("id");
            // h4的集合
            var children = $(this).nextUntil("h3", "h4");
            var li1 = "<li><a class='jumper size-h3' href='#" + attr + "'>" + $(this).text() + "</a>";
            if (children.length > 0) {
                li1 += "<ul>";
            }
            children.each(function () {
                $(this).attr("id", count++);
                var attr = $(this).attr("id");
                var li2 = "<li><a class='jumper size-h4' href='#" + attr + "'>" + $(this).text() + "</a></li>";
                li1 += li2;
            });
            if (children.length > 0) {
                li1 += "</ul>";
            }
            li1 += "</li>";
            li += li1;
        });
        if (son.length > 0) {
            li += "</ul>";
        }
        li += "</li>";
        content += li;
        // console.log(li);
    });
    $("#right-nav ul").append(content);

    // $("#content h2").each(function () {
    //     $(this).attr("id", count++);
    //     var attr = $(this).attr("id");
    //     var dt = "<dt><a class='jumper' href='#" + attr + "'>" + $(this).text() + "</a></dt>";
    //     $("#right-nav dl").append(dt);
    //     var children = $(this).nextUntil("h2", "h3");
    //     children.each(function () {
    //         $(this).attr("id", count++);
    //         var attr1 = $(this).attr("id");
    //         var dd = "<dd><a class='jumper' href='#" + attr1 + "'>-" + $(this).text() + "</a></dd>";
    //         $("#right-nav dl").append(dd);
    //     })
    // });

    $("a.jumper").on("click", function (e) {
        e.preventDefault();
        $("body, html").animate({
            scrollTop: ($($(this).attr('href')).offset().top - 110)
        }, 500);
    });

    // var needLocate = $(window).height() / 4 * 3 < $('#right-nav').height();
    // if (needLocate) {
    //     $('#right-nav').css({top: top - 20});
    //     $('#right-nav').css({"padding": '15px 10px'});
    // }

    var obj = countChars();
    console.error(obj)
    var minute = (parseInt(obj.中英文单词数) / 493).toFixed(1);
    if (minute > 25) {
        minute -= 5.5;
    } else if (minute > 20) {
        minute -= 3.5;
    } else if (minute > 15) {
        minute -= 2;
    } else if (minute > 10) {
        minute -= 1;
    } else if (minute > 5) {
        minute -= 0.5;
    } else if (minute < 2) {
        minute -= -0.3;
    }
    $('#countChars').text('字数约 ' + obj.中英文单词数 + ' 字，阅读耗时约 ' + Number(minute).toFixed(1) + ' 分钟');
});


$(function () {
    $(document).scroll(function() {
        var scroH = $(document).scrollTop();  //滚动高度
        var viewH = $(window).height();  //可见高度
        var contentH = $(document).height();  //内容高度

        if(scroH > 54){  //距离顶部大于100px时
            $('#right-nav').css({top: '70px'});
            console.error("顶部高度"+ scroH)
        } else {
            $('#right-nav').css({top: '125px'});
        }
        if (contentH - (scroH + viewH) <= 100){  //距离底部高度小于100px
            console.error("底部高度" + contentH - (scroH + viewH))
        }
        if (contentH === (scroH + viewH)){  //滚动条滑到底部啦
            console.error("底部" + contentH)
        }
    });

    setTimeout(function () {
        var url = document.location.href
        var index = url.indexOf('#')
        if (index > -1) {
            var jumpText = decodeURI(url.substring(index + 1))
            $('#right-nav ul li a').each(function () {
                var text = $(this).text()
                if (text === jumpText || text.endsWith(jumpText)) {
                    $(this).click()
                    return;
                }
            })
        }
    }, 0)
})

function countChars() {
    var c = $("#content .panel-body");
    var cvalue=c.text().replace(/\r\n/g,"\n");
    var sarr=cvalue.split("");
    var len_total=sarr.length;
    var r = {
        "wd":0,
        "nwd":0,
        "c":0,
        "cb":0,
        "r":0,
        "en":0,
        "cn":0,
        "bl":0
    };
    var words=cvalue.match(/\b\w+\b/g)||[];
    var cnwords=cvalue.match(/[\u4e00-\u9fa5]/g)||[];
    r.nwd=words.length;
    r.cn=cnwords.length;
    for(var i=0;i<len_total;i++){
        r.c++;
        switch(true){
            case /[a-zA-Z]/.test(sarr[i]):
                r.en++;
                break;
            case /\S/.test(sarr[i]):
                r.cb++;
                break;
            case /\s/.test(sarr[i]):
                if(sarr[i]=="\n"||sarr[i]=="\r"){
                    r.r++;
                }else{
                    r.bl++;
                }
        }
    }
    r.wd=r.nwd+r.cn;
    // 面向中文编程，没见过吗？
    // 哼，小菜鸟！
    // 联系邮箱 467914950@qq.com
    return {
        '总字符数目': r.c-r.r,
        '总字符(不含空白)数目': r.c-r.bl-r.r,
        '空白字符数目': r.bl,
        '英文字符数目': r.en,
        '其它字符数目': r.c-r.en-r.bl-r.cn-r.r,
        '中英文单词数': r.wd,
        '中文字数': r.cn,
        '英文单词数': r.nwd,
        '行数': r.r+1
    }
}

