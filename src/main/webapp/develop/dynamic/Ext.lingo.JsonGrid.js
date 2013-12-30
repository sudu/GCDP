/*
 * Ext JS Library 2.0.2
 * Copyright(c) 2006-2008, Ext JS, LLC.
 * licensing@extjs.com
 *
 * http://www.extjs.com/license
 *
 * @author Lingo
 * @since 2008-03-23
 * http://code.google.com/p/anewssystem/
 */
Ext.namespace("Ext.lingo");
/**
 * 拥有CRUD功能的表格.
 *
 * @param config    需要的配置{}
 */
Ext.lingo.JsonGrid = Ext.extend(Ext.grid.GridPanel, { 
    loadMask: true,
    stripeRows: true,

    // 初始化
    initComponent: function() {
        //this.useHistory = this.useHistory !== false;
        Ext.applyIf(this, {
            urlPagedQuery: "pagedQuery",
            urlLoadData: "loadData",
            urlSave: "save",
            urlRemove: "remove",
            pageSize: 10
        });

        this.buildColumnModel();
        this.buildRecord();
        this.buildDataStore();
        if (this.createHeader !== false) {
            this.buildToolbar();
        }
        // 设置baseParams
        this.setBaseParams();

        Ext.lingo.JsonGrid.superclass.initComponent.call(this);

        //this.on('rowdblclick', this.edit, this);

        //右键菜单
        //this.on('rowcontextmenu', this.contextmenu, this);
    },

    // 初始化ColumnModel
    buildColumnModel: function() {
        this.sm = new Ext.grid.CheckboxSelectionModel();
        var columnHeaders = new Array();
        columnHeaders[0] = new Ext.grid.RowNumberer();
        columnHeaders[1] = this.sm;

        for (var i = 0; i < this.formConfig.length; i++) {
            var col = this.formConfig[i];
            if (col.hideGrid === true) {
                continue;
            }
            columnHeaders.push({
            	id:col.id,
                header: col.fieldLabel,
                sortable: col.sortable,
                dataIndex: col.name,
                renderer: col.renderer,
                hidden:col.hidden,
                width:col.width,
                listeners:col.listeners
            });
        }
        this.cm = new Ext.grid.ColumnModel(columnHeaders);
        this.cm.defaultSortable = true;
        this.columnModel = this.cm;
    },
	 
      
    buildRecord: function() {
        this.dataRecord = Ext.data.Record.create(this.formConfig);
    },

    buildDataStore: function() {
        this.store = new Ext.data.Store({
            proxy  : new Ext.data.HttpProxy({url:this.urlPagedQuery}),
            reader : new Ext.data.JsonReader({
                root          : "result",
                totalProperty : "totalCount",
                id            : "id"
            }, this.dataRecord),
            remoteSort : false
        });
        // this.store.setDefaultSort("id", "DESC");
    },

    buildToolbar: function() {
        //
     
        var checkItems = new Array();
        for (var i = 0; i < this.formConfig.length; i++) {
           if(this.formConfig[i].name!='id')
        	 {
        	 	var meta = this.formConfig[i];
        	 	if(i==1)
        	 	{
        	 		 this.store.baseParams.filterTxt = meta.name;
        	 	}
        	  	 
           	 	if (meta.showInGrid === false) {
              		continue;
            	}
            	 var item = new Ext.menu.CheckItem({
                 text         : meta.fieldLabel,
                 value        : meta.name,
                 checked      : true,
                 group        : "filter",
                 checkHandler : this.onItemCheck.createDelegate(this)
            });
            checkItems[checkItems.length] = item;
          }
         }

        this.filterButton = new Ext.Toolbar.MenuButton({
            iconCls  : "refresh",
            text     : this.formConfig[1].fieldLabel,
            tooltip  : "选择搜索的字段",
            menu     : checkItems,
            minWidth : 105
        });
        // 输入框
        this.filter = new Ext.form.TextField({
            'name': 'filter'
        });

//        this.tbar = new Ext.Toolbar([{
//            id      : 'add',
//            text    : '新增',
//            iconCls : 'add',
//            tooltip : '新增',
//            handler : this.add.createDelegate(this)
//        }, {
//            id      : 'edit',
//            text    : '修改',
//            iconCls : 'edit',
//            tooltip : '修改',
//            handler : this.edit.createDelegate(this)
//        }, {
//            id      : 'del',
//            text    : '删除',
//            iconCls : 'delete',
//            tooltip : '删除',
//            handler : this.del.createDelegate(this)
//        }, '->', this.filterButton, this.filter]);

        this.filter.on('specialkey', this.onFilterKey.createDelegate(this));
        
        
        // 把分页工具条，放在页脚
        var paging = new Ext.PagingToolbar({
            pageSize: this.pageSize,
            store: this.store,
            displayInfo: true,
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            plugins: [new Ext.ux.PageSizePlugin()]
        });

        this.store.load({
            params:{start:0, limit:paging.pageSize}
        });
        this.bbar = paging;
    },

    // 设置baseParams
    setBaseParams : function() {
        // 读取数据
/*
        this.store.on('beforeload', function() {
            this.store.baseParams = {
                filterValue : this.filter.getValue(),
                filterTxt   : this.filterTxt
            };
        }.createDelegate(this));
*/
    },

    // 选中搜索属性选项时
    onItemCheck: function(item, checked) {
    	 
        if(checked) {
            this.filterButton.setText(item.text + ':');
            this.filter.setValue('');
            this.store.baseParams.filterTxt = item.value;
            this.store.baseParams.filterValue = '';
        }
    },

    // 监听模糊搜索框里的按键
    onFilterKey: function(field, e) {  
    	var filterTxt = this.store.baseParams.filterTxt;  
        if (typeof filterTxt == 'undefined' || filterTxt == '') {
        	 
            Ext.Msg.alert('提示', '请先选择搜索的字段');
            return;
        } 
        if(e.getKey() == e.ENTER && field.getValue().length > 0) { 
            this.store.baseParams.filterValue = field.getValue(); 
        }else if(e.getKey() == e.ENTER && field.getValue()=="") {
            this.store.baseParams.filterValue = '';
        } else {
           return;
        } 
        this.store.load({
            params:{start:0, limit:20}
        });
    },

    // 检测至少选择一个
    checkOne: function() {
        var selections = this.getSelections();
        if (selections.length == 0) {
            Ext.MessageBox.alert("提示", "请选择一条的记录！");
            return false;
        } else if (selections.length != 1) {
            Ext.MessageBox.alert("提示", "不能选择多行！");
            return false;
        }
        return true;
    },

    // 检测必须选择一个
    checkMany: function() {
        var selections = this.getSelections();
        if (selections.length == 0) {
            Ext.MessageBox.alert("提示", "请至少选择一条的记录！");
            return false;
        }
        return true;
    },

    batchSubmit: function(url) {
        if (this.checkMany()) {
            Ext.Msg.confirm("提示", "是否确定?", function(btn, text) {
                if (btn == 'yes') {
                    var selections = this.getSelections();
                    var ids = new Array();
                    for(var i = 0, len = selections.length; i < len; i++){
                        try {
                            // 如果选中的record没有在这一页显示，remove就会出问题
                            selections[i].get("id");
                            ids[i] = selections[i].get("id");
                            //this.store.remove(selections[i]);//从表格中删除
                        } catch (e) {
                        }
                        //if (this.useHistory) {
                        //    this.grid.selModel.Set.clear();
                        //}
                    }

                    this.body.mask('提交数据，请稍候...', 'x-mask-loading');
                    Ext.Ajax.request({
                        url     : url + '?ids=' + ids,
                        success : function(response,action) { 
                        var responseArray = Ext.util.JSON.decode(response.responseText); 
                       	if(responseArray.success==true)
						{
						  	Ext.MessageBox.alert('成功',responseArray.msg);
							this.body.unmask();
	                        this.refresh();
 						}else
						{
							Ext.MessageBox.alert('错误', responseArray.msg);
							this.body.unmask();
                            this.refresh();
						}
                        }.createDelegate(this),
						failure : function(){
						 this.el.unmask();
						 Ext.MessageBox.alert('错误', '与服务器连接发生中断,请联系管理员!');
						}}); 
                }
            }.createDelegate(this));
        }
    },

    // 弹出添加对话框，添加一条新记录
    add : function() {
        if (!this.dialog) {
            this.createDialog();
        }
        this.formPanel.getForm().reset();
        this.dialog.show(Ext.get("add"));
    },

    // 弹出修改对话框
    edit : function() {
        if (!this.dialog) {
            this.createDialog();
        }

        if (this.checkOne()) {
            this.dialog.show(Ext.get('edit'));
            this.formPanel.load({
                url: this.urlLoadData + "?id=" + this.getSelections()[0].id
            });
        }
    },

    // 删除记录
    del: function() {
        this.batchSubmit(this.urlRemove);
    },

    // 创建弹出式对话框
    createDialog : function() {
    	 
        //var items = this.formConfig;
    	var items=new Array();
    	
    	//去掉主键（编码）的输入框
        for (var i = 0; i < this.formConfig.length; i++) {
         		if(this.formConfig[i].name=='id'){
         			this.formConfig[i].hidden=true;
         			this.formConfig[i].hideLabel=true;
         			items.push(this.formConfig[i]);
         		}else{
         			items.push(this.formConfig[i]);
         		}
         }
         
         
        Ext.each(items, function(item) {
            Ext.applyIf(item, {
                anchor: '90%'
            });
        });

        var reader = new Ext.data.JsonReader({}, this.formConfig);
        this.formPanel = new Ext.form.FormPanel({
            defaultType: 'textfield',
            labelAlign: 'right',
            labelWidth: 100,
            frame: true,
            autoScroll: true,
            title: '详细信息',
            reader: reader,
            url: this.urlSave,
            items: items,
            buttons: [{
                text: '确定',
                handler: function() {
                    if (this.formPanel.getForm().isValid()) {
                        this.formPanel.getForm().submit({
                            waitTitle: "请稍候",
                            waitMsg : '提交数据，请稍候...',
                            success: function(_form,_action) { 
                               var responseArray = Ext.util.JSON.decode(_action.response.responseText); 
                               Ext.MessageBox.alert('成功', responseArray.msg);
                               this.dialog.hide();
                               this.refresh();
                            },
                            failure: function(_form,_action) {
                            	 	var responseArray = Ext.util.JSON.decode(_action.response.responseText); 
                            	 	Ext.MessageBox.alert('错误', responseArray.msg);
                            	 	  this.dialog.hide();
                                	  this.refresh();
                            },
                            scope: this
                        });
                    }
                }.createDelegate(this)
            },{
                text: '取消',
                handler: function() {
                    this.dialog.hide();
                }.createDelegate(this)
            }]
        });
        this.dialog = new Ext.Window({
            layout: 'fit',
            width: this.dlgWidth ? this.dlgWidth : 400,
            height: this.dlgHeight ? this.dlgHeight : 300,
            closeAction: 'hide',
            items: [this.formPanel]
        });
    },

    // 弹出右键菜单
    // 修改，和批量删除的功能
    // 多选的时候，不允许修改就好了
    contextmenu : function(grid, rowIndex, e) {
        e.preventDefault();
        e.stopEvent();

        var updateMenu = new Ext.menu.Item({
            iconCls : 'edit',
            id      : 'updateMenu',
            text    : '修改',
            handler : this.edit.createDelegate(this)
        });
        var removeMenu = new Ext.menu.Item({
            iconCls : 'delete',
            id      : 'removeMenu',
            text    : '删除',
            handler :  this.del.createDelegate(this)
        });

        var selections = this.getSelections();

        if (selections.length > 1) {
            updateMenu.disable();
        }
        
        var menuList = [updateMenu, removeMenu];

        this.grid_menu = new Ext.menu.Menu({
            id    : 'mainMenu',
            items : menuList
        });

        var coords = e.getXY();
        grid.getSelectionModel().selectRow(rowIndex);
        this.grid_menu.showAt([coords[0], coords[1]]);
    },

    // 刷新表格数据
    refresh : function() {
        this.store.reload();
    },

    onDestroy : function(){
        if(this.rendered){
            if (this.grid_menu) {
                this.grid_menu.destory();
            }
            if (this.dialog) {
                this.dialog.destroy();
            }
        }
        Ext.lingo.JsonGrid.superclass.onDestroy.call(this);
    }
});

