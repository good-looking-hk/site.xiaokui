/**
 * ztree插件的封装
 */
(function () {
    var $ZTree = function (id, url) {
        this.id = id;
        this.url = url;
        this.onClick = null;
        this.settings = null;
        this.ondblclick = null;
    };
    $ZTree.prototype = {
        /**
         * 初始化ztree的设置
         */
        initSetting: function () {
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
            return settings;
        },

        /**
         * 手动设置ztree的设置
         */
        setSettings: function (val) {
            this.settings = val;
        },

        /**
         * 初始化ztree
         */
        init: function () {
            var zNodeSeting = null;
            if (this.settings != null) {
                zNodeSeting = this.settings;
            } else {
                zNodeSeting = this.initSetting();
            }
            var zNodes = this.loadNodes();
            // $.fn.zTree.init($("#deptTree"), zNodeSeting, zNodes);
            $.fn.zTree.init($("#" + this.id), zNodeSeting, zNodes);
        },

        /**
         * 绑定onclick事件
         */
        bindOnClick: function (func) {
            this.onClick = func;
        },
        /**
         * 绑定双击事件
         */
        bindOnDblClick: function (func) {
            this.ondblclick = func;
        },
        /**
         * 加载节点
         */
        loadNodes: function () {
            var zNodes = null;
            HK.post(HK.ctxPath + this.url, null, null, function (data) {
                zNodes = data;
            });
            return zNodes;
        },

        /**
         * 获取选中的值
         */
        getSelectedVal: function () {
            var zTree = $.fn.zTree.getZTreeObj(this.id);
            var nodes = zTree.getSelectedNodes();
            return nodes[0].name;
        }
    };
    window.$ZTree = $ZTree;
}());