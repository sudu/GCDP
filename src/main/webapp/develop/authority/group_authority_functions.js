function addGroupUser(id){
	var add_group_user=new Ext.lingo.JsonGrid({
		id: 'add_group_user',
	    urlPagedQuery: "authority!userNotJoinByGroupId.jhtml?id="+id,
		autoScroll: true,
	    autoHeight:true,
	    autoWidth:true,
	    formConfig: [
	        {fieldLabel: 'ID', name: 'id', readOnly: true},
	        {fieldLabel: '中文名', name: 'cnname'},
	        {fieldLabel: '用户名', name: 'username'},
	        {fieldLabel: '部门', name: 'dept'},
	        {fieldLabel: '电话', name: 'telphone'},
	        {fieldLabel: 'email', name: 'email'}
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
	            id      : 'join',
	            text    : '加入',
	            iconCls : 'addField',
	            tooltip : '加入',
	            handler : this.join.createDelegate(this)
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
	            params:{start:0, limit:20}
	        });
	        this.bbar = paging;

	    },
	    
	    join:function(){
	    	if (!this.dialog) {
	            this.createDialog();
	        }
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
	                            url     : 'authority!doUserJoin.jhtml?ids='+ids+'&id='+id,
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
	    }
	});
	var setting= new Ext.Window({
        layout: 'fit',
        title:'查看用户',
        width:700,
        height:800,
        
        closeAction: 'hide',
        items:[add_group_user]
    });
    setting.show();
}

//----------------------------------------------------------
//----------------------------------------------------------
//----------------------------------------------------------


function checkGroupUser(id){
	var check_group_user=new Ext.lingo.JsonGrid({
		id: 'check_group_user',
	    urlPagedQuery: "authority!userPagedQueryByGroupId.jhtml?id="+id,
	    urlRemove: "authority!removeUser.jhtml",
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	   
	    formConfig: [
	        {fieldLabel: 'ID', name: 'id', readOnly: true},
	        {fieldLabel: '中文名', name: 'cnname'},
	        {fieldLabel: '用户名', name: 'username'},
	        {fieldLabel: '部门', name: 'dept'},
	        {fieldLabel: '电话', name: 'telphone'},
	        {fieldLabel: 'email', name: 'email'}
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
	            id      : 'delete',
	            text    : '删除',
	            iconCls : 'delete',
	            tooltip : '删除',
	            handler : this.deleteGroupUser.createDelegate(this)
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
	    deleteGroupUser : function(){
	    	if (!this.dialog) {
	            this.createDialog();
	        }
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
	                            url     : 'authority!deleteGroupUser.jhtml?ids='+ids+'&id='+id,
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
	    }
	});
	var setting= new Ext.Window({
        layout: 'fit',
        title:'查看用户',
        width:700,
        height:800,
        closeAction: 'hide',
        items:[check_group_user]
    });
    setting.show();
}

//----------------------------------------------------------
//----------------------------------------------------------
//----------------------------------------------------------
function show_authority(id){

    var checkBoxValue=0;
	var authority_grid=new Ext.lingo.JsonGrid({
		id: 'authorityGrid',
		urlPagedQuery: "authority!queryAuthorityByGroupId.jhtml?id="+id,
		urlLoadData: "authority!modifyAuthority.jhtml",
	    urlSave: "authority!addAuthority.jhtml?id="+id,
	    urlRemove: "authority!removeAuthority.jhtml",
	    formConfig: [
	                 {fieldLabel: 'ID', name: 'id', readOnly: true},
	                 {fieldLabel: '类型', name: 'type',renderer:
	                  function(value){
	                  	if(value==0)
	                  	{
	                         return "排除";
	                     }else{
	                    	 return "授权";
	                     }
	                   }
	                  },
	                 {fieldLabel: '权限项', name: 'permissionString'},
	                 {fieldLabel: '权限校验正则', name: 'powerpath'},
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

	        this.tbar = new Ext.Toolbar([{
	            id      : 'add_authority',
	            text    : '新增权限',
	            iconCls : 'addField',
	            tooltip : '新增权限',
	            handler : this.add.createDelegate(this)
	        },{
	            id      : 'edit_authority',
	            text    : '修改权限',
	            iconCls : 'edit',
	            tooltip : '修改权限',
	            handler : this.edit.createDelegate(this)
	        },{
	            id      : 'del_authority',
	            text    : '删除权限',
	            iconCls : 'delete',
	            tooltip : '删除权限',
	            handler : this.del_authority.createDelegate(this)
	        }]);
	        
	        this.store.load();
	    },
	    createDialog : function() {
	        var readerConfig = [
	            {name: 'id'},
	            {name: 'type'},
	            {name: 'permission'},
	            {name: 'powerpath'}
	        ];
	        var reader = new Ext.data.JsonReader({}, readerConfig);    
	        this.formPanel= new Ext.form.FormPanel({
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
						                         name:"authority.id"
									},
									{	
										columnWidth:1,     
						                layout: 'form',
						                id:'type',
									      items : [{
												xtype: 'panel',     
												layout: 'table',     
												fieldLabel: '类型',     
												defaultType: 'radio',     
												isFormField: true, 
												items : [
													new Ext.form.Radio({  
										                name:'authority.type',
										                boxLabel:'授权',
										                inputValue:1,
										                checked:true
										            }), 
										            
										            new Ext.form.Radio({  
										                name:'authority.type',
										                boxLabel:'排除',
										                inputValue:0
										            })
												]
									      }]
									},
									{	
										columnWidth:1,     
						                layout: 'form',
						                id:'permission',
						                
									      items : [{
												xtype: 'panel',     
												layout: 'table',     
												fieldLabel: '权限',     
												defaultType: 'checkbox',     
												isFormField: true, 
												items : [
													new Ext.form.Checkbox({  
										                name:'permission',
										                boxLabel:'查看',
										                inputValue:1,
										                checked:true
										            }), 
										            
										            new Ext.form.Checkbox({  
										                name:'permission',
										                boxLabel:'添加',
										                inputValue:2
										            }),
										            new Ext.form.Checkbox({  
										                name:'permission',
										                boxLabel:'修改',
										                inputValue:4
										            }),
										            new Ext.form.Checkbox({  
										                name:'permission',
										                boxLabel:'删除',
										                inputValue:8
										            }),
										            new Ext.form.Checkbox({  
										                name:'permission',
										                boxLabel:'发布',
										                inputValue:16
										            })
												]
									      }]
									},
									
							        {
							             fieldLabel:"权限表达式",
							             id:'powerpath',
				                         xtype:'textfield',
				                         name:"authority.powerpath"
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
	            title:'权限信息',
	            width: this.dlgWidth ? this.dlgWidth : 400,
	            height: this.dlgHeight ? this.dlgHeight : 300,
	            closeAction: 'close',
	            items: [this.formPanel]
	        });
	    }, 
	    del_authority : function() {
	    	if (!this.dialog) {
	            this.createDialog();
	        }
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
	                            url     : this.urlRemove+'?ids='+ids+'&id='+id,
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
	    }
	});
	
	var authority_win= new Ext.Window({
        layout: 'fit',
        title:'查看组权限',
        width:600,
        height:250,
        closeAction: 'close',
        items:[authority_grid]
    });
	authority_win.show();
}