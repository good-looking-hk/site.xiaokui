/**
 * Created by HK on 2017/07/20.
 */
$(function () {
    var url = window.location.href;
    if (url.indexOf("about") != -1) {
        $("#about").addClass("active");
    } else if (url.indexOf("catalogue") != -1) {
        $("#catalogue").addClass("active");
    } else if (url.indexOf("blog") != -1) {
        $("#blog").addClass("active");
    }


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

    var needLocate = $(window).height() / 4 * 3 < $('#right-nav').height();
    if (needLocate) {
        $('#right-nav').css({top: top - 20});
        $('#right-nav').css({"padding-top": 0});
        $('#right-nav').css({"padding-bottom": 0});
    }
    // $(window).scroll(function () {
    //     if (!needLocate) {
    //         return;
    //     }
    //     var currentScroll = $(window).scrollTop();
    //     var top = ($(window).height() - $('#right-nav').height())/2;
    //     if(currentScroll > 54 + 48) {
    //         $('#right-nav').css({top:top - 20});
    //         $('#right-nav').css({"padding-top": 0});
    //     } else if (currentScroll <= 54 + 48){
    //         $('#right-nav').css({top:'16%'});
    //     }
    // });

});

