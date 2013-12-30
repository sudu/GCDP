Ext.ns("ifeng");
Ext.onReady(function(){
    Ext.QuickTips.init();
    ifeng.GroupGrid = Ext.extend(Ext.lingo.JsonGrid, {
	    id: 'groupGrid',
	    urlPagedQuery: "authority!groupPagedQuery.jhtml",
	    urlLoadData: "authority!modifyGroup.jhtml",
	    urlSave: "authority!addGroup.jhtml",
	    urlRemove: "authority!removeGroup.jhtml",
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	    formConfig: [
	        {fieldLabel: 'ID', name: 'id', readOnly: true},
	        {fieldLabel: '组名称', name: 'groupname'},
	        {fieldLabel: '备注', name: 'remark'}
	     ],
	     
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

        this.tbar = new Ext.Toolbar([{
            id      : 'add',
            text    : '新增',
            iconCls : 'add',
            tooltip : '新增',
            handler : this.add.createDelegate(this)
        }, {
            id      : 'edit',
            text    : '修改',
            iconCls : 'edit',
            tooltip : '修改',
            handler : this.edit.createDelegate(this)
        }, {
            id      : 'del',
            text    : '删除',
            iconCls : 'delete',
            tooltip : '删除',
            handler : this.del.createDelegate(this)
        },{
            id      : 'settingAnthrity',
            text    : '查看组权限',
            iconCls : 'settingAnthrity',
            tooltip : '查看组权限',
            handler : this.settingAnthrity.createDelegate(this)
        }, '->', this.filterButton, this.filter]);

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
    
    
    settingAnthrity:function(){
    	if (!this.dialog) {
	            this.createDialog();
	        }
	     if (this.checkOne()) {
	           show_authority(this.getSelections()[0].id);
	     }
    },
   createDialog : function() {
        var readerConfig = [
            {name: 'id'},
            {name: 'groupname'},
            {name: 'remark'}
        ];
        var reader = new Ext.data.JsonReader({}, readerConfig);     
        this.formPanel = new Ext.form.FormPanel({
        	method:'POST',
 			fileUpload: true,
            labelAlign: 'center',
            frame: true,
            reader: reader,
            url: this.urlSave,
            items:[
            			{
						            xtype:"hidden",
			                         id:"id",
			                         name:"group.id"
						},
						{
						             fieldLabel:"组名称",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"groupname",
			                         name:"group.groupname",
			                         allowBlank:false,
			                         blankText:"不能为空!",
			                         msgTarget:"side"//设置错误显示
						        },
						        {
						             fieldLabel:"备注",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"remark",
			                         name:"group.remark"
						        }

      					],
            buttons: [{
                text: '确定',
                handler: function() { 
                	          		
                    if (this.formPanel.getForm().isValid()) {
                        this.formPanel.getForm().submit({
                            waitTitle: "请稍候",
                            waitMsg : '提交数据，请稍候...',
                            success: function(response,action) { 
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
            width:400,
            height:250,
            title:'用户信息',
            closeAction: 'hide',
            items: [this.formPanel]
			}); 
    	}
    });
     
     var groupGrid=new ifeng.GroupGrid({applyTo:'group_div'});
	 groupGrid.render();  
})
	 
	 