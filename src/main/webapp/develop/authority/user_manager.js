Ext.ns("ifeng");
Ext.onReady(function(){
    Ext.QuickTips.init();
    ifeng.UserGrid = Ext.extend(Ext.lingo.JsonGrid, {
	    id: 'userGrid',
	    urlPagedQuery: "authority!userPagedQuery.jhtml",
	    urlLoadData: "authority!modifyUser.jhtml",
	    urlSave: "authority!addUser.jhtml",
	    urlRemove: "authority!removeUser.jhtml",
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	    
	   
	    formConfig: [
	        {fieldLabel: 'ID', name: 'id', readOnly: true,sortable:true},
	        {fieldLabel: '中文名', name: 'cnname',sortable:true},
	        {fieldLabel: '用户名', name: 'username',sortable:true},
	        // {fieldLabel: '密码', name: 'password'},// added by HANXIANQI
	        {fieldLabel: '部门', name: 'dept'},
	        {fieldLabel: '电话', name: 'telphone'},
	        {fieldLabel: 'email', name: 'email',width:300}
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
            id      : 'checkgroup',
            text    : '查看权限',
            iconCls : 'check',
            tooltip : '查看权限',
            handler : this.checkgroup.createDelegate(this)
        },{
            id      : 'settinggroup',
            text    : '设置权限',
            iconCls : 'check',
            tooltip : '设置权限',
            handler : this.setting.createDelegate(this)
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
            {name: 'username'},
            {name: 'password'}, // added by HANXIANQI
            {name: 'cnname'},
            {name: 'dept'},
            {name: 'telphone'},
			{name: 'email'}
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
			                         name:"user.id"
						},
						{
						             fieldLabel:"用户名",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"username",
			                         name:"user.username",
			                         allowBlank:false,
			                         blankText:"不能为空!",
			                         msgTarget:"side"//设置错误显示
						        },
						        
						        // added by HANXIANQI
						        {
						             fieldLabel:"密码",
						             xtype:"textfield",
						             inputType:"password",
			                         validateOnBlur:false,
			                         id:"password",
			                         name:"user.password",
			                         allowBlank:false,
			                         blankText:"不能为空!",
			                         msgTarget:"side"//设置错误显示
						        },
						        
						        {
						             fieldLabel:"中文名",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"cnname",
			                         name:"user.cnname",
			                         allowBlank:false,
			                         blankText:"不能为空!",
			                         msgTarget:"side"//设置错误显示
						        },
								 {
						             fieldLabel:"部门",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"dept",
			                         name:"user.dept"
						        },
						        {
						             fieldLabel:"电话",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"telphone",
			                         name:"user.telphone",
			                         regex:new RegExp("^[0-9\_]+$"),//正则表达式验证
			                         regexText:"电话只能是数字"
						        },
						        {
						             fieldLabel:"email",
			                         xtype:"textfield",
			                         validateOnBlur:false,
			                         id:"email",
			                         name:"user.email",
			                         regex:new RegExp("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$"),//正则表达式验证
			                         regexText:"请输入正确邮箱格式"
			                         
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
    Ext.reg('userGrid', ifeng.UserGrid);
     
     
    var userGrid=new ifeng.UserGrid({applyTo:'user_div'});
	userGrid.render();  
})
	 