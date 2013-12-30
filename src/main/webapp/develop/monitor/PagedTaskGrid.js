Ext.ns("monitor");
    monitor.TaskGrid = Ext.extend(Ext.lingo.JsonGrid, {
	    id: 'userGrid',
	    urlPagedQuery: "./service!pagedTask.jhtml",
	    urlSave: "./service!addTask.jhtml",
	    urlLoadData: "./service!modifyTask.jhtml",
	    urlRemove: "./service!removeTask.jhtml",
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	    trackMouseOver:true,
	    
	    formConfig: [
	        {fieldLabel: 'ID', name: 'id', width:50,readOnly: true,sortable:true},
	        {fieldLabel: 'NodeID', name: 'nodeid', width:50,sortable:true},
	        {fieldLabel: '任务名', name: 'taskName', width:100,sortable:true},
	        {fieldLabel: '报警值', name: 'warnValue', width:120},
	        {fieldLabel: '错误值', name: 'errValue', width:120},
	        {fieldLabel: '单位', name: 'measure', width:50},
	        {fieldLabel: '是否开启', name: 'isCheck', width:50},
	        {fieldLabel: 'Email', name: 'email',width:150},
	        {fieldLabel: 'Mobile', name: 'mobile',width:150},
	        {fieldLabel: '任务描述', name: 'description',width:150}
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
            iconCls : 'addField',
            tooltip : '新增',
            handler : this.add.createDelegate(this)
        }, {
            id      : 'edit',
            text    : '修改',
            iconCls : 'modifyField',
            tooltip : '修改',
            handler : this.edit.createDelegate(this)
        }, {
            id      : 'del',
            text    : '删除',
            iconCls : 'delField',
            tooltip : '删除',
            handler : this.del.createDelegate(this)
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
    
    checkgroup:function(){
    	if (!this.dialog) {
	            this.createDialog();
	        }
	     if (this.checkOne()) {
	           show_checkgroup(this.getSelections()[0].id);
	     }
    },
    
    setting:function(){
    	if (!this.dialog) {
	            this.createDialog();
	        }
	     if (this.checkOne()) {
	           user_setting(this.getSelections()[0].id);
	     }
    },
   createDialog : function() {
        var readerConfig = [
            {name: 'id'},
            {name: 'nodeid'},
            {name: 'taskName'},
            {name: 'key'},
            {name: 'warnValue'},
            {name: 'errValue'},
            {name: 'measure'},
            {name: 'isCheck'},
			{name: 'email'},
			{name: 'mobile'},
			{name: 'description'}
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
			                         name:"task.id"
						},
						{
						             fieldLabel:"NodeID",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"nodeid",
			                         name:"task.nodeid",
			                         width:120,
			                         allowBlank:false,
			                         blankText:"不能为空!",
			                         msgTarget:"side",//设置错误显示
			                         regex:new RegExp("^[0-9\_]+$")//正则表达式验证
									 
						        },
						        {
						             fieldLabel:"任务名称",
			                         xtype:"combo",
			                         id:"taskName",
			                         name:"task.taskName",
			                         width:120,
			                         allowBlank:false,
			                         dataSource:[
			                                     ['redis','redis'],
			                                     ['redis_qs','redis_qs'],
			                                     ['redis_ls.key','redis_ls'],
			                                     ['db','db'],
			                                     ['cmppdb','cmppdb'],
			                                     ['sendFile','sendFile'],
			                                     ['sync','sync'],
			                                     ['search.key','search'],
			                                     ['text','text'],
			                                     ['image','image'],
			                                     ['queue','queue'],
			                                     ['queue_ls.key','queue_ls'],
			                                     ['dyn_redis.key','dyn_redis'],
			                                     ['dyn_db.key','dyn_db']
			                         ]
						        },
						        {
						             fieldLabel:"任务参数",
			                         xtype:"textfield",
			                         validateOnBlur:false, 
			                         id:"key",
			                         name:"task.key",
			                         width:120
						        },
								{
						             fieldLabel:"警告值",
			                         xtype:"textfield",
			                         validateOnBlur:false, 
			                         id:"warnValue",
			                         name:"task.warnValue",
			                         width:120
						        },
						        {
						             fieldLabel:"报警值",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"errValue",
			                         name:"task.errValue",
			                         width:120
						        },
						        {
						             fieldLabel:"单位",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         width:120,
			                         id:"measure",
			                         name:"task.measure"
						        },
						        {
						        	
						             fieldLabel:"是否开启",
						             xtype:'radiogroup',
			                         validateOnBlur:false,
			                         width:120,
			                         id:"isCheck",
			                         name:"task.isCheck",
			                         dataSource: [[true,'开启'],[false,'关闭']]
						        },
						        {
						             fieldLabel:"email",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         width:120,
			                         id:"email",
			                         name:"task.email"
			                         
						        },
						        {
						             fieldLabel:"电话",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         width:120,
			                         id:"mobile",
			                         name:"task.mobile"
						        },
						        {
						             fieldLabel:"描述",
			                         xtype:"textfield",
			                         width:120,
			                         validateOnBlur:false,
			                         id:"description",
			                         name:"task.description"
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
            width:300,
            height:350,
            title:'任务信息',
            closeAction: 'hide',
            items: [this.formPanel]
			}); 
    	}
    });