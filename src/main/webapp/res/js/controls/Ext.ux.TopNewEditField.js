Ext.namespace("Ext.ux");
/**
 * 头条新闻模块编辑器
 * options:
 * @param value: 为空或者字符串化的二维数组，如'[[{},{}],[{},{}]]'
 */
Ext.ux.TopNewEditField = Ext.extend(Ext.form.TextField, {
    
    initComponent: function(){

        this.x_init_param();
        Ext.ux.TopNewEditField.superclass.initComponent.call(this);

    },
    
    onRender: function(ct, position){
        
        Ext.ux.TopNewEditField.superclass.onRender.call(this, ct, position);
        this.x_init_els(ct, position);
        this.x_init_event();

    },

    /**
     * 初始化参数
     */
    x_init_param: function(){

      this.inputType = "hidden";
      //如果value值为空或者没有设置，则默认为一个空数组
      this.value = this.value || '[]';
    },

    /**
     * 初始化附加的ext.el对象
     * 在内部插入一个容器，在这个容器上插入TopNewEdit用来显示和维护value的内容。
     * @param {} ct
     * @param {} position
     */
    x_init_els: function(ct, position){

        this.editWrap = ct.createChild({tag:'div'}, position);
        this.editer = new Ext.ux.TopNewEdit({
            //如果value为字符串，则将字符串decode。
            data : this.tryInitData(),
            renderTo: this.editWrap
        });

    },

    tryInitData: function(){
        
        var data = [];

        try{

            data = typeof this.value == "string" ? Ext.decode(this.value) : this.value;

        }
        catch(e){
            // alert("输入数据格式有问题" + this.value);
        }

        return data;
    },
    
    /**
     * 当给field设置值的时候，同时尝试更新grid的数据。
     */
    setValue: function(v){
        
        var isEditerHasRender = this.editer;

        if(isEditerHasRender)
        {
            this.tryToUpdateEditerView(v);
        }

        Ext.ux.TopNewEditField.superclass.setValue.apply(this, arguments);
 
    },

    /**
     * 尝试更新grid的数据
     */
    tryToUpdateEditerView: function(v){

        try{
            var e_editer = this.editer;
            var a_orgData = typeof v == "string" ? Ext.decode(v) : v;
            var a_data = e_editer.x_initData(a_orgData);
            e_editer.store.loadData(a_data);
        }
        catch(e){

        }

    },

    
    /**
     * 初始化事件，在grid store发生update,remove,add操作的时候，对field进行setValue操作，更新value值。
     */
    x_init_event: function(){

        this.editer.store.on("update", function(){
            this.x_setValue(this.editer.x_getData());
        },this);

        this.editer.store.on("remove", function(){
            this.x_setValue(this.editer.x_getData());
        },this);

        this.editer.store.on("add", function(){
            this.x_setValue(this.editer.x_getData());
        },this);
    },
    
    //设置textField的值
    x_setValue: function(v){
        
        Ext.ux.TopNewEditField.superclass.setValue.apply(this, arguments);

    }
    
    
});

Ext.reg('xtopneweditfield', Ext.ux.TopNewEditField);



Ext.namespace("Ext.ux");
/**
 * 数据输入输出实例：
 * 输入：为一个二维数组
 [
    [
        {"title":"富士康高管：iPhone5于10月推出 已下订单","url":"http://tech.ifeng.com/apple/news/detail_2012_04/09/13754975_0.shtml","isBold":true,"isBreak":true,"className":"","pre":"视频"},
        {"title":"下一代iPhone或配更大屏幕","url":"http://tech.ifeng.com/digi/mobile/news/detail_2012_04/10/13765066_0.shtml","isBold":false,"isBreak":false,"className":"","pre":"视频"},
        {"title":"将进一步完善Siri功能","url":"http://tech.ifeng.com/apple/news/detail_2012_04/10/13766232_0.shtml","isBold":false,"isBreak":true,"className":"","pre":"热点"},
        {"title":"单次充电仅需30秒","url":"http://tech.ifeng.com/digi/mobile/new/detail_2012_04/06/13686984_0.shtml","isBold":false,"isBreak":false,"className":"","pre":""},
        {"title":"德仪为下一代iPhone芯片提供商","url":"http://tech.ifeng.com/apple/news/detail_2012_03/30/13556007_0.shtml","isBold":false,"isBreak":true,"className":"","pre":""}
    ],
    [
        {"title":"Facebook斥资$10亿收购图片商Instagram","url":"http://tech.ifeng.com/internet/detail_2012_04/10/13765094_0.shtml","isBold":true,"isBreak":true,"className":"","pre":""},
        {"title":"扎克伯格公开信：为里程碑事件","url":"http://tech.ifeng.com/internet/detail_2012_04/10/13766374_0.shtml","isBold":false,"isBreak":false,"className":"","pre":""},
        {"title":"Instagram CEO获$4亿","url":"http://tech.ifeng.com/internet/detail_2012_04/10/13769826_0.shtml","isBold":false,"isBreak":true,"className":"","pre":"音乐"}
    ]
]
 * 输出：为此数组的字符串形式：
 * 属性说明：
 * [pre]        新闻标题的前缀，一般是俩个汉字或者空，如：视频，要闻
 * [title]      新闻标题
 * [url]        新闻链接地址
 * [isBold]     新闻标题是否加粗
 * [isBreak]    此条新闻后是否要断行
 * [className]      新闻标题的附加样式名称
 */

/**
 * 头条新闻编辑模块
 */
Ext.ux.TopNewEdit = Ext.extend(Ext.grid.GridPanel, {

    frame: true,
    autoHeight: true,
    // height: 300,
    enableColumnMove: false,
    enableHdMenu: false,
    animCollapse: false,
    iconCls: 'icon-grid',
    cls: "x_topnewedit",
    stripeRows: true,
    /**
     * 列一下record中各个属性的含义：
     * [block]      新闻块，用于新闻分组的标示
     * [index]      索引，只是用来初始排序，没有具体意义
     * [pre]        新闻标题的前缀，一般是俩个汉字或者空，如：视频，要闻
     * [title]      新闻标题
     * [url]        新闻链接地址
     * [isBold]     新闻标题是否加粗
     * [isBreak]    此条新闻后是否要断行
     * [className]      新闻标题的附加样式名称
     */
    record: Ext.data.Record.create([{
        name: "block"
    }, {
        name: "index"
    }, {
        name: "pre"
    }, {
        name: "title"
    }, {
        name: "url"
    }, {
        name: "isBold"
    }, {
        name: "isBreak"
    }, {
        name: "className"
    }]),

    initComponent: function() {

        this.x_initParam();
        Ext.ux.TopNewEdit.superclass.initComponent.call(this);

    },

    onRender: function(ct, position) {

        Ext.ux.TopNewEdit.superclass.onRender.call(this, ct, position);
        this.x_init_event();
        this.x_init_emptyText();

    },

    x_init_emptyText: function() {

        if (this.view.emptyText && !this.view.hasRows()) {
            this.view.applyEmptyText();
        }

    },

    /**
     * 初始化参数
     */
    x_initParam: function() {

        this.x_initStore();
        this.x_initToolbar();
        this.x_initColumns();
        this.x_initView();

    },

    //初始化store
    x_initStore: function() {

        var o_gridReader = new Ext.data.JsonReader({
            id: "id"
        }, this.record);

        //设置gird的store为groupingStore,用block进行分组
        this.store = new Ext.data.GroupingStore({
            reader: o_gridReader,
            data: this.x_initData(this.data),
            sortInfo: {
                field: 'index',
                direction: "ASC"
            },
            groupField: 'block'
        });

    },

    //初始化数据，将输入数据转换成store识别的数据结构。
    //即将[[{},{}],[{},{}]]转换为[{},{},{},{}]
    x_initData: function(a_data) {

        var a_result = [];
        var i, iLen = a_data.length;
        var j, jLen;
        for (i = 0; i < iLen; i++) {
            for (j = 0, jLen = a_data[i].length; j < jLen; j++) {
                var o_temp = a_data[i][j];
                o_temp.index = 1;
                o_temp.block = (i + 1).toString();
                a_result.push(o_temp);
            }
        }

        return a_result;
    },


    //设置头部工具条
    x_initToolbar: function() {

        this.bbar = ["->",{
            "text": "添加新闻组",
            "cls": "addBlock",
            "iconCls" : "addBlockIcon",
            listeners: {
                click: {
                    fn: this.x_addNewsGroup,
                    scope: this
                }
            }
        }];

    },

    /**
     * 建立一个新的新闻块
     */
    x_addNewsGroup: function() {

        var e_newRecord, s_block;

        s_block = this.creatNewBlockName();

        e_newRecord = new this.record({
            block: s_block,
            index: 1,
            pre: "",
            title: "",
            url: "",
            className: "",
            isBold: false,
            isBreak: false
        });

        new Ext.ux.topNewWinEdit({
            grid: this,
            record: e_newRecord,
            action: "addNewsGroup"
        }).show();

    },

    //根据已有的block值，生成一个新的block值。这里block值为字符形式的整数。
    creatNewBlockName: function() {

        //获取store中的block集合
        var a_block = this.store.collect("block");

        return a_block.length > 0 ? (Math.max.apply(Math, a_block) + 1).toString() : "1";

    },

    //列设置
    x_initColumns: function() {

        this.columns = [{
            header: "index",
            dataIndex: 'index',
            hidden: true
        }, {
            header: "新闻组",
            dataIndex: 'block',
            hidden: true
        }, {
            header: "前缀",
            dataIndex: 'pre',
            width: 40,
            resizable: false,
            align: "center",
            renderer: this.x_renderBlodStyle
        }, {
            header: "新闻标题",
            dataIndex: 'title',
            width: 200,
            resizable: false,
            renderer: this.x_renderBlodStyle
        }, {
            header: "是否加粗",
            dataIndex: 'isBold',
            width: 40,
            resizable: false,
            align: "center",
            renderer: this.x_renderBold
        }, {
            header: "是否换行",
            dataIndex: 'isBreak',
            width: 40,
            resizable: false,
            align: "center",
            renderer: this.x_renderBreak
        }, {
            header: "操作",
            dataIndex: 'index',
            width: 60,
            resizable: false,
            align: "right",
            renderer: this.x_renderAction
        }];

    },

    x_renderBlodStyle: function(value, metadata, record, rowIndex, colIndex, store) {

        return record.get("isBold") ? '<b class="blodStyle">' + value + '</b>' : value;

    },

    //渲染grid中的是否加粗单元格内的html内容
    x_renderBold: function(b_isBold) {

        var s_bold = b_isBold ? "bolded" : "";
        return '<span class="js_bold_row ' + s_bold + '" data-rowClickAction="updateBlodOfCurrentNews" ></span>';

    },

    //渲染grid中的是否换行单元格内的html内容
    x_renderBreak: function(b_isBreak) {

        var s_break = b_isBreak ? "breaked" : "";
        return '<span class="js_break_row ' + s_break + '" data-rowClickAction="updateBreakOfCurrentNews" ></span>';

    },

    //渲染gird中的操作单元格内的html内容，根据当前新闻的位置，判断上升下降箭头的生成形式。
    x_renderAction: function(value, metadata, record, rowIndex, colIndex, store) {

        var s_up = '<span class="js_up_row js_row_action" title="上升" data-rowClickAction="moveUpCurrentNews" ></span>';
        var s_down = '<span class="js_down_row js_row_action" title="下降" data-rowClickAction="moveDownCurrentNews" ></span>';

        var i_index = store.indexOf(record);
        var s_block = record.get("block");

        var e_blockBoldGroup = store.queryBy(function(record, id) {
            return record.get("block") === s_block && record.get("isBold");
        });
        var i_indexBoldBlockBegin = store.indexOf(e_blockBoldGroup.items[0]);
        var i_indexBoldBlockEnd = i_indexBoldBlockBegin + e_blockBoldGroup.items.length - 1;

        var e_blockNoBoldGroup = store.queryBy(function(record, id) {
            return record.get("block") === s_block && !record.get("isBold");
        });
        var i_indexNoBoldBlockBegin = store.indexOf(e_blockNoBoldGroup.items[0]);
        var i_indexNoBoldBlockEnd = i_indexNoBoldBlockBegin + e_blockNoBoldGroup.items.length - 1;

        //判断当前行是否是加粗部分或者非加粗部分的第一条
        if (i_index === i_indexBoldBlockBegin || i_index === i_indexNoBoldBlockBegin) {
            s_up = '<span class="js_up_row_disable"></span>';
        }

        //判断当前行是否是加粗部分或者非加粗部分的最后一条
        if (i_index === i_indexBoldBlockEnd || i_index === i_indexNoBoldBlockEnd) {
            s_down = '<span class="js_down_row_disable"></span>';
        }

        return '<span class="js_edit_row js_row_action" title="编辑该条新闻" data-rowClickAction="editCurrentNews" ></span>' + s_up + s_down + '<span class="js_del_row js_row_action" title="删除新闻" data-rowClickAction="deleteCurrentNews" ></span>';
    },

    x_initView: function() {

        //重写groupView的设置，其中模板的设置，增加应状态的css class
        this.view = new Ext.grid.GroupingView({
            forceFit: true,
            scrollOffset: 26,
            emptyText: "还没有新闻，请点击右下角的添加新闻组进行添加。",
            templates: {
                row: new Ext.XTemplate(
                    '<div class="x-grid3-row {alt} " style="{tstyle} position: relative;">',
                        '<table class="x-grid3-row-table" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">', 
                            '<tbody>',
                                '<tr>{cells}</tr>',
                                (this.enableRowBody ? '<tr class="x-grid3-row-body-tr" style="{bodyStyle}"><td colspan="{cols}" class="x-grid3-body-cell" tabIndex="0" hidefocus="on"><div class="x-grid3-row-body">{body}</div></td></tr>' : ''), 
                            '</tbody>',
                        '</table>',
                    '</div>'
                )
            },

            //gird每组头部的模板，增加了与操作相关的html代码
            startGroup: new Ext.XTemplate(
                '<div id="{groupId}" class="x-grid-group {cls}" >', 
                    '<div id="{groupId}-hd" class="x-grid-group-hd-top-new" style="{style}"><div>',
                        '{text} （共{[values.rs.length]} {[values.rs.length > 1 ? "条新闻" : "条新闻"]}）',
                        '<span class="js_group_action_box">', 
                            '<span class="js_remove_group js_group_action" data-clickAction="deleteCurrentNewsGroup" data-groupBlockValue="{[values.group]}" title="删除当前新闻块"></span>',
                            '{[this.renderMoveUp(values)]}',
                            '{[this.renderMoveDown(values)]}',
                            '<span class="js_add_new_to_group js_group_action" data-clickAction="addANewsItemToCurrentNewsGroup" data-groupBlockValue="{[values.group]}" title="添加新闻"></span>',
                        '</span>',
                '</div></div>',
                '<div id="{groupId}-bd" class="x-grid-group-body">', {
                //渲染moveup，如果该模块已经是第一个的话，则返回一个disable的moveup。
                renderMoveUp: function(values) {

                    //通过判断当前新闻块中第一个rs在gird中的位置，如果是0，则表示其为第一个模块。
                    var e_firstRecord = values.rs[0];
                    //获取包含此record的store
                    var e_store = e_firstRecord.store;
                    var i_index = e_store.indexOf(e_firstRecord);
                    if (i_index === 0) {
                        return '<span class="js_move_up_disable"></span>';
                    } else {
                        return '<span class="js_move_up js_group_action" data-groupId="' + values.groupId + '" data-clickAction="moveUpCurrentNewsGroup" data-groupBlockValue="' + values.group + '" title="上升"></span>';
                    }

                },
                //渲染movedown，如果该模块已经是最后一个的话，则返回一个disable的movedown。
                renderMoveDown: function(values) {

                    //通过判断当前新闻块中最后一个rs在gird中的位置，如果是最后一个的话，则表示其为最后一个模块。
                    var e_lastRecord = values.rs[values.rs.length - 1];
                    var e_store = e_lastRecord.store;
                    var i_index = e_store.indexOf(e_lastRecord);
                    if (i_index === e_store.getCount() - 1) {
                        return '<span class="js_move_down_disable"></span>';
                    } else {
                        return '<span class="js_move_down js_group_action" data-groupId="' + values.groupId + '" data-clickAction="moveDownCurrentNewsGroup" data-groupBlockValue="' + values.group + '" title="下降"></span>';
                    }

                }

            })
        });

    },

    x_init_event: function() {

        //代理gird的click，根据点击对象的不同，分别实现不同的操作
        this.on("click", function(e) {

            e.preventDefault();
            e.stopEvent();

            var d_target = e.target;
            //通过在操作dom上预设data-clickAction的方式来对应相应的操作。
            var s_action = d_target.getAttribute("data-clickAction");

            //如果触发的dom对象上定义了data-clickAction，则执行对应的clickAction方法。
            if (s_action) {
                this[s_action](d_target);
            }

        });
        
        this.on("rowdblclick", function(e_grid, i_index, e){
            e.preventDefault();
            e.stopEvent();

            this.editCurrentNews(i_index);
            
        });
        //代理grid中row上的click事件，根据点击对象的不同，实现对应的操作。
        this.on("rowclick", function(e_grid, i_index, e) {

            e.preventDefault();
            e.stopEvent();

            var d_target = e.target;
            //通过在操作dom上预设data-rowClickAction的方式来对应相应的操作。
            var s_action = d_target.getAttribute("data-rowClickAction");

            //如果触发的dom对象上定义了data-rowClickAction，则执行对应的rowClickAction方法。
            if (s_action) {
                this[s_action](i_index);
            }

        });

    },

    //删除当前新闻块
    deleteCurrentNewsGroup: function(d_target) {

        var s_currentGroupBlockValue = d_target.getAttribute("data-groupBlockValue");
        var e_groupRecords;
        var i, iLen;

        if (confirm('确认要删除新闻模块"' + s_currentGroupBlockValue + '"么？')) {
            e_groupRecords = this.store.query("block", s_currentGroupBlockValue);
            for (i = 0, iLen = e_groupRecords.length; i < iLen; i++) {
                this.store.remove(e_groupRecords.items[i]);
            }
        }

    },

    //上移当前新闻块
    moveUpCurrentNewsGroup: function(d_target) {

        var i, iLen;
        var s_currentGroupBlockValue = d_target.getAttribute("data-groupBlockValue");
        var e_currentGroupRecords = this.store.query("block", s_currentGroupBlockValue);
        var i_firstIndexOfGroup = this.store.indexOf(e_currentGroupRecords.items[0]);

        //查找插入位置
        var e_preRecord = this.store.getAt(i_firstIndexOfGroup - 1);
        var s_preGroupBlockValue = e_preRecord.get("block");
        var e_preGroupRecords = this.store.query("block", s_preGroupBlockValue);
        var i_insertIndex = this.store.indexOf(e_preGroupRecords.items[0]);

        for (i = 0, iLen = e_currentGroupRecords.length; i < iLen; i++) {
            this.store.remove(e_currentGroupRecords.items[i]);
        }

        for (i = 0, iLen = e_currentGroupRecords.length; i < iLen; i++) {
            this.store.insert(i_insertIndex + i, e_currentGroupRecords.items[i]);
        }

        this.setHighLigth(this.store.indexOf(e_currentGroupRecords.items[0]), this.store.indexOf(e_currentGroupRecords.items[e_currentGroupRecords.items.length - 1]));

    },

    //下移当前新闻块
    moveDownCurrentNewsGroup: function(d_target) {

        var i, iLen;
        var s_currentGroupBlockValue = d_target.getAttribute("data-groupBlockValue");
        var e_currentGroupRecords = this.store.query("block", s_currentGroupBlockValue);
        var i_lastIndexOfGroup = this.store.indexOf(e_currentGroupRecords.items[e_currentGroupRecords.length - 1]);

        //查找插入位置
        var e_nextRecord = this.store.getAt(i_lastIndexOfGroup + 1);
        var s_nextGroupValue = e_nextRecord.get("block");
        var e_nextGroupRecords = this.store.query("block", s_nextGroupValue);

        for (i = 0, iLen = e_currentGroupRecords.length; i < iLen; i++) {
            this.store.remove(e_currentGroupRecords.items[i]);
        }

        var i_insertIndex = this.store.indexOf(e_nextGroupRecords.items[e_nextGroupRecords.length - 1]);

        for (i = 0, iLen = e_currentGroupRecords.length; i < iLen; i++) {
            this.store.insert(i_insertIndex + i + 1, e_currentGroupRecords.items[i]);
        }

        this.setHighLigth(this.store.indexOf(e_currentGroupRecords.items[0]), this.store.indexOf(e_currentGroupRecords.items[e_currentGroupRecords.items.length - 1]));

    },

    //添加新闻到的当前新闻块
    addANewsItemToCurrentNewsGroup: function(d_target) {

        var s_currentGroupBlockValue = d_target.getAttribute("data-groupBlockValue");
        var e_nextRecord = new this.record({
            block: s_currentGroupBlockValue,
            index: 1,
            title: "",
            url: "",
            className: "",
            isBold: false,
            isBreak: false
        });

        new Ext.ux.topNewWinEdit({
            grid: this,
            record: e_nextRecord,
            action: "addANewsItems"
        }).show();

    },

    //向上移动当前新闻
    moveUpCurrentNews: function(i_index) {

        var e_currentRecord = this.store.getAt(i_index);
        this.store.remove(e_currentRecord);
        this.store.insert(i_index - 1, e_currentRecord);
        this.setHighLigth(i_index - 1);

    },

    setHighLigth: function(i_index,end_index) {

        this.removeHighLigth();
        if (typeof end_index == "undefined") {
            Ext.get(this.view.getRow(i_index)).addClass("highLight");    
        } else {
            for (; i_index <= end_index ; i_index++) {
                Ext.get(this.view.getRow(i_index)).addClass("highLight");
            }
        }
        

    },

    removeHighLigth: function() {

        Ext.get(this.body.query(".highLight")).removeClass("highLight");

    },

    //向下移动当前新闻
    moveDownCurrentNews: function(i_index) {

        var e_currentRecord = this.store.getAt(i_index);
        this.store.remove(e_currentRecord);
        this.store.insert(i_index + 1, e_currentRecord);
        this.setHighLigth(i_index + 1);

    },

    //编辑当前新闻
    editCurrentNews: function(i_index) {

        var e_currentRecord = this.store.getAt(i_index);

        new Ext.ux.topNewWinEdit({
            grid: this,
            record: e_currentRecord,
            action: "updateCurrentNews"
        }).show();

    },

    //删除当前新闻
    deleteCurrentNews: function(i_index) {

        var e_currentRecord = this.store.getAt(i_index);

        if (confirm('确定要删除名为"' + e_currentRecord.get("title") + '"的新闻么？')) {
            this.store.remove(e_currentRecord);
            this.removeHighLigth();
        }

    },

    //更新当前新闻的断行属性
    updateBreakOfCurrentNews: function(i_index) {

        //设置是否断行
        var e_currentRecord = this.store.getAt(i_index);
        e_currentRecord.set("isBreak", !e_currentRecord.get("isBreak"));

        this.setHighLigth(i_index);
        //改变样式

    },

    //更新当前新闻的加粗属性
    updateBlodOfCurrentNews: function(i_index) {

        // 设置是否加粗
        var e_currentRecord = this.store.getAt(i_index);

        e_currentRecord.set("isBold", !e_currentRecord.get("isBold"));

        this.reposition(e_currentRecord, e_currentRecord.get("isBold"));

        // 重新获得当前新闻的索引号。
        this.setHighLigth(this.store.indexOf(e_currentRecord));

    },

    reposition: function(record, isBold) {

        var i_firstNoBoldindex, e_groupBlodRecords;
        var s_currentGroupBlockValue = record.get("block");
        var e_currentGroupRecords = this.store.query("block", s_currentGroupBlockValue);
        var i_firstIndex = this.store.indexOf(e_currentGroupRecords.items[0]);

        //根据加粗状态，将当前新闻移动到相应位置，加粗的移动到当前新闻块顶部，取消加粗的，要移到当前新闻块所有加粗新闻的下面
        if (isBold) {
            
            this.store.remove(record);
            this.store.insert(i_firstIndex, record);

        } else {

            e_groupBlodRecords = this.store.queryBy(function(record, id) {
                return record.get("block") === s_currentGroupBlockValue && record.get("isBold");
            });

            if (e_groupBlodRecords.length > 0) {
                i_firstNoBoldindex = i_firstIndex + e_groupBlodRecords.items.length;
                this.store.remove(record);
                this.store.insert(i_firstNoBoldindex, record);
            }
        }


    },


    /**
     * 转换输出store中的数据
     */
    x_getData: function() {

        var a_result = [];
        var a_block = this.store.collect("block");
        var i, iLen = a_block.length;
        var j, jLen;
        var k, kLen;
        var o_temp;
        var s_block, e_groupRecords;

        //需要输出的数据的属性名数组
        var a_outputAttribute = ["title", "pre", "url", "isBold", "isBreak", "className"];

        //遍历所有block块
        for (i = 0; i < iLen; i++) {
            a_result[i] = [];
            s_block = a_block[i];
            e_groupRecords = this.store.query("block", s_block);

            //遍历block中的record
            for (j = 0, jLen = e_groupRecords.items.length; j < jLen; j++) {
                o_temp = {};
                //遍历属性数组，将需要的属性加入结果数组
                for (k = 0, kLen = a_outputAttribute.length; k < kLen; k++) {
                    o_temp[a_outputAttribute[k]] = e_groupRecords.items[j].data[a_outputAttribute[k]];
                }
                a_result[i].push(o_temp);
            }
        }

        return Ext.encode(a_result);
    },

    x_updateData: function(record, o_values, s_action) {

        this.updateRecord(record, o_values);
        this[s_action](record);
        this.setHighLigth(this.store.indexOf(record));

    },

    updateRecord: function(record, o_values) {

        var key;
        for (key in o_values) {
            record.set(key, o_values[key]);
        }

    },

    addNewsGroup: function(record) {

        this.store.add(record);

    },

    addANewsItems: function(record){

        var i_firstIndex, i_lastIndex;
        var e_groupRecords = this.store.query("block", record.get("block"));

        if (record.get("isBold")) {

            i_firstIndex = this.store.indexOf(e_groupRecords.items[0]);
            this.store.insert(i_firstIndex, record);

        } else {

            i_lastIndex = this.store.indexOf(e_groupRecords.items[e_groupRecords.items.length - 1]);
            this.store.insert(i_lastIndex + 1, record);

        }

    },

    updateCurrentNews: function(record) {

        var o_change = record.getChanges();

        if (typeof o_change.isBold !== "undefined") {

            this.reposition(record,o_change.isBold);

        }

    }


});

Ext.reg('topnewedit', Ext.ux.TopNewEdit);


/**
 * 新闻编辑窗口
 * option_extend:
 * @param {gird} 编辑内容所属的grid
 * @param {record} 需要编辑的record
 */
Ext.ux.topNewWinEdit = Ext.extend(Ext.Window, {

    width: 400,
    title: "新闻条目编辑",

    initComponent: function() {

        this.x_initParam();

        Ext.ux.topNewWinEdit.superclass.initComponent.call(this);
    },

    onRender: function(ct, position) {

        Ext.ux.topNewWinEdit.superclass.onRender.call(this, ct, position);

        //初始化以后消除验证提示
        var _this = this;
        setTimeout(function() {
            _this.e_form.getForm().clearInvalid();
        }, 100);

    },

    x_initParam: function() {

        this.x_initForm();
        this.x_initButtons();

    },

    x_initForm: function() {

        this.e_form = new Ext.form.FormPanel({
            frame: true,
            labelSeparator: ":",
            labelWidth: 60,
            labelAlign: "right",
            items: [{
                fieldLabel: "前缀",
                name: "pre",
                value: this.record.get("pre"),
                width: 40,
                xtype: "textfield"
            }, {
                fieldLabel: "标题",
                name: "title",
                value: this.record.get("title"),
                width: 300,
                allowBlank: false,
                xtype: "textfield"
            }, {
                fieldLabel: "链接",
                name: "url",
                value: this.record.get("url"),
                width: 300,
                allowBlank: false,
                vtype: "url",
                xtype: "textfield"
            }, {
                fieldLabel: "样式",
                name: "className",
                value: this.record.get("className"),
                width: 100,
                xtype: "textfield"
            }, {
                fieldLabel: "是否加粗",
                name: "isBold",
                checked: this.record.get("isBold"),
                xtype: "checkbox"
            }, {
                fieldLabel: "是否换行",
                name: "isBreak",
                checked: this.record.get("isBreak"),
                xtype: "checkbox"
            }]
        });

        //设置form项
        this.items = [this.e_form];
    },

    x_initButtons: function() {

        this.buttons = [{
            text: '提交',
            listeners: {
                click: {
                    fn: this.updateRecord,
                    scope: this
                }
            }
        }, {
            text: '关闭',
            listeners: {
                click: {
                    fn: this.close,
                    scope: this
                }
            }
        }];

    },

    /**
     * 更新新闻数据
     */
    updateRecord: function() {

        //校验表单
        if (!this.e_form.getForm().isValid()) {
            alert("编辑内容有误，请修改后重新提交。");
            return;
        }

        var o_values = this.x_getFormValues();

        this.grid.x_updateData(this.record, o_values, this.action);

        this.close();
    },

    x_getFormValues: function() {

        var i, iLen;
        var a_FormItems = this.e_form.items.items;
        var o_values = {};

        for (i = 0, iLen = a_FormItems.length; i < iLen; i++) {
            var o_FormItem = a_FormItems[i];
            //用现有值设置到修改的record上。
            o_values[o_FormItem.name] = o_FormItem.getValue();
        }

        return o_values;

    }

});


