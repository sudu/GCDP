
function show_checkgroup(id){
	var check_user_group=new ifeng.GroupGrid({
		urlPagedQuery: "authority!groupPagedQueryByUserId.jhtml?id="+id,
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
	            id      : 'quit_group_button',
	            text    : '退出组',
	            iconCls : 'delete',
	            tooltip : '退出组',
	            handler : this.quit_group.createDelegate(this)
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
	    quit_group : function() {
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
	                            url     : 'authority!quitGroup.jhtml?ids='+ids+'&id='+id,
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
        title:'查看组',
        width:600,
        height:250,
        closeAction: 'hide',
        items:[check_user_group]
    });
    setting.show();
}

//=======================================================================
//***********************************************************************
//=======================================================================


function user_setting(id){
	
	var user_setting_group=new ifeng.GroupGrid({
		urlPagedQuery: "authority!notJoinGroupByUserId.jhtml?id="+id,
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
	            id      : 'join_group_button',
	            text    : '加入组',
	            iconCls : 'addField',
	            tooltip : '加入组',
	            handler : this.join_group.createDelegate(this)
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
	    join_group : function() {
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
	                            url     : 'authority!joinGroup.jhtml?ids='+ids+'&id='+id,
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
        title:'权限设置',
        width:600,
        height:250,
        closeAction: 'hide',
        items:[user_setting_group]
    });
    setting.show();
}