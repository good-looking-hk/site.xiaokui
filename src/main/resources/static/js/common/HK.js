/**
 * 使用本js，请确保页面已经包含了jquery，layui(layer)等模块
 * 在layer表情符号中，1是钩钩，2是叉叉，3是问号，4是灰色的锁，5是苦脸，6是笑脸，7是感叹号,16是转圈圈等待
 * 在layer使用open方法时，0（信息框，默认），1（页面层），2（iframe层），3（加载层），4（tips层）
 * javascript中以下值会被转换为false:false、undefined、null、0、-0、NaN、空字符串
 */
$.ajaxSettings.async = false;
var HK = {
    ctxPath: "",
    data: {},
    /**
     * 内置封装的treeTable实例
     */
    tableInstance: null,
    isEmpty: function (o) {
        return o == null || o === undefined || o === '';
    },
    reLocate: function(url, times) {
        url = this.ctxPath + url;
        if (typeof times === "number") {
            setTimeout("location.href = '" + url + "'", times);
        } else {
            location.href = url;
        }
    },
    newPage: function(url) {
        var el = document.createElement("a");
        document.body.appendChild(el);
        el.href = this.ctxPath + url;
        el.target = "_blank";
        el.click();
        document.body.removeChild(el);
    },
    /**
     * data为json对象
     */
    toString: function (data) {
        layer.msg(JSON.stringify(data), {time: 5000});
        //
        // var str = "";
        // if (typeof data === "object" && data.length === undefined) {
        //     for (var i in data) {
        //         str += i + ":" + data[i] + "\n";
        //     }
        //     layer.msg(str);
        // } else {
        //     layer.msg(JSON.stringify(data));
        // }
    },
    addCtx: function (ctx) {
        if (this.ctxPath === "" && !this.isEmpty(ctx)) {
            this.ctxPath = ctx;
        }
    },
    setData: function (key, value) {
        if (typeof key === "object") {
            for (var i in key) {
                if (typeof i === "function")
                    continue;
                this.data[i] = key[i];
            }
        } else {
            this.data[key] = (typeof value === "undefined") ? $("#" + key).val() : value;
        }
        return this;
    },
    getData: function () {
        return this.data;
    },
    clearData: function () {
        this.data = {};
    },
    ok: function (msg) {
        this.msg(msg, 6, 2000);
    },
    error: function (msg) {
        this.msg(msg, 5, 3000);
    },
    info: function (msg) {
        this.msg(msg, 7, 2000);
    },
    /**
     * end为关闭后是事件
     */
    msg: function (msg, icon, time, end) {
        layer.msg(msg, {icon: icon, time: time}, end);
    },
    /**
     * yes为点击确定后的事件
     * cancel为点击关闭后的事件
     */
    confirm: function (msg, yes) {
        layer.confirm(msg, {icon: 3, title: '提示', time: 4000}, function (index) {
            yes();
            layer.close(index);
        });
    },
    open: function (title, url) {
        var index = layer.open({
            type: 2,
            title: title,
            area: ['1000px', '600px'], //设置宽高，默认自适应，系统默认的自适应完全行不通
            fix: false, //不固定
            maxmin: true,//最大最小化
            content: HK.ctxPath + url
        });
        return index;
    },
    /**
     * 如果只是单纯接受数据，没有状态码之类的额外信息，那么直接返回
     */
    post: function (url, data, msg, success) {
        var resData = null;
        if (data == null) {
            data = {};
        }
        if (msg == null) {
            msg = "操作成功";
        }
        $.post(HK.ctxPath + url, data, function (res) {
            if (typeof res.code !== "undefined" && res.code !== 200) {
                return HK.error(res.msg);
            } else if (res.code === 200) {
                if (typeof  success === "function") {
                    return HK.ok(msg) & success(msg);
                }
                return HK.ok(msg) ;
            } else {
                resData = res;
            }
        }, 'json');
        return resData;
    },
    initZtree: function (treeId, url, selectMany) {
        var zNodes = HK.post(HK.ctxPath + url, null, null);
        var settings = {
            view: {
                dblClickExpand: true,
                selectedMulti: false
            },
            data: {
                simpleData: {
                    enable: true,
                    idKey: 'id',
                    pIdKey: 'parentId'
                },
            },
            callback: {
                onClick: this.onClick,
                onDblClick: this.ondblclick
            }
        };
        if (typeof selectMany === "boolean" && selectMany) {
            settings.check = {
                enable: true,
                chkboxType: { "Y": "p", "N": "s" }
            }
        }
        var ztree = $.fn.zTree.init($("#" + treeId), settings, zNodes);
        return ztree;
    },
    initSelectZtree: function(url, yes, selectMany) {
        var zTreeObj = null;
        var selectNode = null;
        var id = url.length + "_id";
        var index = layer.open({
            type: 0,
            area: ['500px', '400px'],
            offset: 't',
            anim: 5,
            title: '选择',
            content: "<ul id='" + id + "' class='ztree'></ul>",
            btn: ['确定', '关闭'],
            success: function (layero, index) {
                zTreeObj = HK.initZtree(id, HK.ctxPath + url, selectMany);
            },
            yes: function (index, layero) {
                var nodes = zTreeObj.getSelectedNodes();
                if (!selectMany) {
                    if (nodes.length == 1) {
                        var nods = nodes[0];
                        if (typeof yes === "function") {
                            yes(nods);
                        }
                        selectNode = nods;
                    }
                } else {
                    nodes = zTreeObj.getCheckedNodes();
                    var ids = "";
                    for (var i = 0, l = nodes.length; i < l; i++) {
                        ids += "," + nodes[i].id;
                    }
                    selectNode = ids.substring(1);
                    if (typeof yes === "function") {
                        yes(selectNode);
                    }
                }

            },
            btn2: function (index, layero) {
                layer.close(index);
            }
        });
        return selectNode;
    },
    /**
     * 为输入框选择id和value值
     */
    initInputSelectZtree: function(id, name, url,  yes) {
        var nameInput = $("#" + name);
        var idInput = $("#" + id);
        var zTreeObj = null;
        var selectNode = null;
        var index = layer.open({
            type: 0,
            area: ['500px', '400px'],
            offset: 't',
            anim: 5,
            title: '选择',
            content: "<ul id='" + id + name + "' class='ztree'></ul>",
            btn: ['确定', '重置'],
            success: function (layero, index) {
                zTreeObj = HK.initZtree(id + name, HK.ctxPath + url);
            },
            yes: function (index, layero) {
                var nodes = zTreeObj.getSelectedNodes();
                if (nodes.length == 1) {
                    var nods = nodes[0];
                    idInput.val(nods.id);
                    nameInput.val(nods.name);
                    if (typeof yes === "function") {
                        yes(nods);
                    }
                    selectNode = nods;
                }
                layer.close(index);
            },
            btn2: function (index, layero) {
                idInput.val('');
                nameInput.val('');
                zTreeObj.cancelSelectedNode();
            }
        });
        return selectNode;
    },
    initTreeTable: function (tableId, url, columns, params) {
        if (this.tableInstance != null && typeof params === "undefined") {
            this.error("表实例已存在，不能再次实例化");
            return;
        }
        this.tableInstance =  $('#' + tableId).bootstrapTreeTable({
            id: 'id',// 选取记录返回的值
            code: 'id',// 用于设置父子关系
            parentCode: 'parentId',// 用于设置父子关系
            type: 'post', //请求数据的ajax类型
            url: HK.ctxPath + url,   //请求数据的ajax的url
            ajaxParams: params ? params : {}, //请求数据的ajax的data属性
            expandColumn: 2,//在哪一列上面显示展开按钮,从0开始
            striped: true,   //是否各行渐变色
            expandAll: true,  //是否全部展开
            columns: columns,		//列数组
            toolbar: "#" + tableId + "Toolbar"//顶部工具条
        });
        return this.tableInstance;
    },
    getSelectedItemId: function () {
        if (this.tableInstance != null) {
            var ids = this.tableInstance.bootstrapTreeTable('getSelections');
            if (ids.length === 0) {
                HK.info("请先选中表格中的某一记录！");
                return null;
            }
            return ids[0].id;
        }
    },
    refreshTable: function (params) {
        if (this.tableInstance != null) {
            this.tableInstance.bootstrapTreeTable('refresh', params);
        }
    },
    exec: function (method, baseUrl, baseName, deleteId) {
        var id = "id";
        if (method === "add") {
            HK.open("添加" + baseName, baseUrl + "add");
        } else if (method === "edit") {
            var id = HK.getSelectedItemId();
            if (id != null) {
                HK.open("修改" + baseName, baseUrl + "edit/" + id);
                // HK.refreshTable();
            }
        } else if (method === "remove") {
            var id = HK.getSelectedItemId();
            if (id != null) {
                if (typeof deleteId === "string") {
                    id = deleteId;
                }
                HK.confirm("确定删除该" + baseName + "？", function () {
                    HK.post(baseUrl + "remove", {id: id}, "删除" + baseName + "成功");
                    // HK.refreshTable();
                });
            }
        } else if(method === "refresh") {
            HK.refreshTable();
        } else {
            if (typeof method === 'function') {
                method();
                // HK.refreshTable();
            }
        }
    },
};
