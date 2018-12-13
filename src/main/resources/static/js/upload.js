/**
 * Created by HK on 2017/10/22 19:02.
 */
//博客空间名
var blogSpace;
//预览文件索引
var previewIndex;
//上传文件列表
var saveFlag;

function open_link(url) {
    var el = document.createElement("a");
    document.body.appendChild(el);
    el.href = url;
    el.target = "_blank";
    el.click();
    document.body.removeChild(el);
}

function isEmpty(obj) {
    if (typeof obj === "undefined" || obj == null || obj === "") {
        return true;
    }
    return false;
}

function green(str) {
    if (typeof str === "string" || typeof str === "number") {
        return "<font color='#5FB878'>" + str + "</font>";
    }
}

layui.use(['form', 'layer', 'upload'], function () {
    var layer = layui.layer, upload = layui.upload, form = layui.form;
    //多文件列表示例
    var demoListView = $('#demoList'), uploadListIns = upload.render({
        elem: '#testList',
        url: HK.ctxPath + '/sys/blog/temp',
        accept: 'file',
        exts: 'html',
        size: 8 * 1024,
        multiple: true,
        auto: false,
        bindAction: '#testListAction',
        choose: function (obj) {
            var files = this.files = obj.pushFile(); //将每次选择的文件追加到文件队列
            //读取本地文件
            obj.preview(function (index, file, result) {
                var tr = $(['<tr id="upload-' + index + '">'
                    , '<td>' + file.name + '</td>'
                    , '<td>' + (file.size / 1014).toFixed(1) + 'kb</td>'
                    , '<td></td>'
                    , '<td>等待上传</td>'
                    , '<td>'
                    , '<button class="layui-btn layui-btn-mini blog-preview">上传</button>'
                    , '<button class="layui-btn layui-btn-mini layui-btn-danger blog-delete">删除</button>'
                    , '</td>'
                    , '</tr>'].join(''));

                //上传预览
                tr.find('.blog-preview').on('click', function () {
                    previewIndex = index;
                    var tds = tr.children();
                    if (isEmpty(tds.eq(2).text())) {
                        obj.upload(index, file);
                    } else {
                        var blogName = tds.eq(2).text().split(' : ')[2];
                        if (!isEmpty(blogName)) {
                            var url = "/blog/" + blogSpace + "/preview?blogName=" + blogName;
                            open_link(url);
                            // window.open(url);
                        }
                    }
                });
                //删除
                tr.find('.blog-delete').on('click', function () {
                    delete files[index]; //删除对应的文件
                    tr.remove();
                    uploadListIns.config.elem.next()[0].value = ''; //清空 input file 值，以免删除后出现同名文件不可选
                });
                demoListView.append(tr);
            });
        }
        , done: function (res, index, upload) {
            if (res.code === 200) { //上传成功
                var blog = res.upload;
                blogSpace = blog.blogSpace;
                saveFlag = true;
                var tr = demoListView.find('tr#upload-' + index)
                    , tds = tr.children();
                //如果没有已上传的记录
                if (isEmpty(tds.eq(2).text())) {
                    tds.eq(2).html('<span>' + green(blog.dir) + ' : ' + (isEmpty(blog.orderNum) ? '未指定' : green(blog.orderNum)) + ' : ' + green(blog.name) + ' : ' + green(blog.createTime) +'</span>');
                    tds.eq(3).html('<span style="color: #5FB878;">上传成功</span>');
                    tr.find('.blog-preview').text('预览');
                }
                // tds.eq(4).find('.blog-delete').remove(); //清空操作
                return delete this.files[index]; //删除文件队列已经上传成功的文件
            }
            this.error(index, upload, res.msg);
        }
        , error: function (index, upload, msg) {
            var tr = demoListView.find('tr#upload-' + index)
                , tds = tr.children();
            var info = $(['<span style="color: #FF5722;">', msg, '</span>'].join(''));
            tds.eq(3).html(info);
            // tds.eq(4).find('.blog-preview').remove();
            tds.eq(4).find('.blog-preview').addClass('layui-btn layui-btn-disabled'); //禁用预览
            return delete this.files[index];
        }
    });
});

var vm = new Vue({
    el: "#app",
    data: {
        menuList: {},
    },
    methods: {
        save: function () {
            if (isEmpty(blogSpace)) {
                layer.msg("请先上传文件！");
                return;
            }
            if (!saveFlag) {
                return;
            }
            saveFlag = false;
            $('#demoList tr').each(function () {
                var blogInfo = $(this).find('td').eq(2).text().split(' : ');
                if (blogInfo.length === 0 || blogInfo.length === 1) {
                    return;
                } else if(!isEmpty($(this).find('td').eq(4).find('.blog-show').text())) {
                    return;
                } else if (blogInfo.length !== 4) {
                    layer.msg('不合法数据');
                    return;
                }
                var data = {
                    dir: blogInfo[0],
                    orderNum: parseInt(blogInfo[1]),
                    name: blogInfo[2],
                    createTime: blogInfo[3]
                };
                HK.toString(data);
                // return;
                var temp = $(this);
                $.post(HK.ctxPath + '/sys/blog/add', data, function (response) {
                    if (response.code == 200) {
                        var info = $(['<span style="color: #5FB878;">', response.msg, '</span>'].join(''));
                        temp.find('td').eq(3).html(info);
                        temp.find('td').eq(4).find('.blog-preview').addClass('blog-show').removeClass('blog-preview'); //清空操作
                        temp.find('td').eq(4).find('.blog-show').text("查看");
                        temp.find('.blog-delete').addClass('layui-btn layui-btn-disabled'); //禁用预览
                        temp.find('.blog-show').off('click').on('click', function () {
                            var info = temp.find('td').eq(2).text().split(' : ');
                            var dir = info[0];
                            var blogName = info[2];
                            if (!isEmpty(blogName)) {
                                var url = HK.ctxPath + "/blog/" + blogSpace + '/' + dir + '/' + blogName;
                                open_link(url);
                                // window.open(url);
                            }
                        });
                    } else {
                        temp.find('td').eq(3).html('<span style="color: #FFB800;">' + response.msg + '</span>');
                    }
                });
            });
        }
    },
    created: function () {
        // this.menu();
    }
});