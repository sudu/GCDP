Ext.ns("ifeng");
var nodeId__=Ext.parseQuery().nodeId;
var nowDate = new Date();
function doCheck(id){
    openTab('task/taskMgr.html?nodeId='+nodeId__+'&taskId=' + id,id+'任务状态');
};

Ext.onReady(function(){
	
    Ext.QuickTips.init();
    ifeng.running_task_grid = Ext.extend(Ext.lingo.JsonGrid, {
	    id: 'running_task_grid',
	    urlPagedQuery: "task!runningList.jhtml?nodeId="+nodeId__,
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	   
	    formConfig: [
	        {fieldLabel: 'id', name: 'id', readOnly: true,width:50},
	        {fieldLabel: '任务名', name: 'taskName',width:150,
		        renderer:function(v,p,record){
					return '<a href="javascript:doCheck('+record.data["id"]+');">'+v+'</a>';
				}
	        },
	        {fieldLabel: '开始时间', name: 'startTime',xtype:'datefield',format:'Y-m-d',width:125},
	        {fieldLabel: '结束时间', name: 'endTime',xtype:'datefield',format:'Y-m-d',width:125},
	        {fieldLabel: '下次执行时间', name: 'nextFireTime',xtype:'datefield',format:'Y-m-d',width:125},
	        {fieldLabel: '重复次数', name: 'repeatCount',width:50},
	        {fieldLabel: '已执行次数', name: 'runCount',width:60},
	        {fieldLabel: '执行间隔(分钟)', name: 'repeatInterval',width:60},
	        {fieldLabel: '创建者', name: 'creator',width:70},
	        {fieldLabel: '最近修改时间', name: 'lastModifyTime',width:125},
	        {fieldLabel: '状态', name: 'status',
	         renderer:function(v,p,record){
	         	var s=record.data["endTime"];
		        var endTime= new Date(Date.parse(s.replace(/-/g,"/"))).getTime();
		        if(record.data["repeatCount"]<=record.data["runCount"] && record.data["repeatCount"]>=0){
		        	return "<div title='停止,到达重复次数'><font color='red'>停止,到达重复次数</font></div>";
		        }else if(endTime<nowDate.getTime()){
		        	return "<div title='停止,到达结束时间'><font color='red'>停止,到达结束时间</font></div>";
		        }else if(v==0){
					return "<div title='停止,未启动'><font color='red'>停止,未启动</font></div>";
				}else if(v==1){
					return "<div title='等待执行'><font color='#A6A600'>等待执行</font></div>";
				}else if(v==2){
					return "<div title='正在执行'><font color='#28FF28'>正在执行</font></div>";
				}
			}
		   }
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

        this.tbar = new Ext.Toolbar([
   /**     	{
            id      : 'del',
            text    : '停止执行',
            iconCls : 'delete',
            tooltip : '停止执行',
            handler : this.stopTask.createDelegate(this)
        },**/
        {
            id      : 'checkTask',
            text    : '查看',
            iconCls : 'check',
            tooltip : '查看',
            handler : this.checkTask.createDelegate(this)
        },{
            id      : 'refreshTask',
            text    : '刷新',
            iconCls : 'check',
            tooltip : '刷新',
            handler : this.refreshTask.createDelegate(this)
        },{
            id      : 'refreshTask',
            text    : '删除',
            iconCls : 'delete',
            tooltip : '删除',
            handler : this.delTask.createDelegate(this)
        }, '->', this.filterButton, this.filter]);

        this.filter.on('specialkey', this.onFilterKey.createDelegate(this));
        
        


        this.store.load();
    },
   delTask:function(){
	    var selections = this.getSelections();
	    if(selections.length!=1){
	    	Ext.MessageBox.alert('错误','选择一个任务删除');
	    }else{
	    	Ext.Msg.confirm("提示", "是否确定?", function(btn, text) {
	    		if (btn == 'yes') {
	    			var id=selections[0].get("id");
	    			this.body.mask('提交数据，请稍候...', 'x-mask-loading');
	    			Ext.Ajax.request({
                            url     : 'task!deleteTask.jhtml?id='+id,
                            success : function(response,action) { 
                            var responseArray = Ext.util.JSON.decode(response.responseText); 
                           	if(responseArray.success==true)
    						{
    						  	Ext.MessageBox.alert('成功',responseArray.message);
    							this.body.unmask();
    	                        this.refresh();
     						}else
    						{
    							Ext.MessageBox.alert('错误', responseArray.message);
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
    
    checkTask:function(){
    	var selections = this.getSelections();
	    if(selections.length>1){
	    	Ext.MessageBox.alert('错误','只能选择一个任务查看');
	    }else{
	    	var taskId=selections[0].get("id");
	    	openTab('task/taskMgr.html?nodeId='+nodeId__+'&taskId=' + taskId,taskId+'任务状态');
	    }
    },
    
    refreshTask:function(){
	    var store=Ext.getCmp('running_task_grid').store;
    	store.reload();
    },
    
    stopTask:function(){
    	if (!this.dialog) {
            this.createDialog();
        }
        if (this.checkMany()) {
                Ext.Msg.confirm("提示", "是否确定?", function(btn, text) {
                    if (btn == 'yes') {
                        var selections = this.getSelections();
                        var taskName = new Array();
                        var taskGroup = new Array();
                        var time=new Array();
                        for(var i = 0, len = selections.length; i < len; i++){
                            try {
                                // 如果选中的record没有在这一页显示，remove就会出问题
                                taskName[i] = selections[i].get("taskName");
                                taskGroup[i] = selections[i].get("taskGroup");
                                //this.store.remove(selections[i]);//从表格中删除
                            } catch (e) {
                            }
                            //if (this.useHistory) {
                            //    this.grid.selModel.Set.clear();
                            //}
                        }
                        this.body.mask('提交数据，请稍候...', 'x-mask-loading');
                        Ext.Ajax.request({
                            url     : 'task!stopTask.jhtml?taskName='+taskName+'&taskGroup='+taskGroup,
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
    Ext.reg('running_task_grid', ifeng.running_task_grid);
     
     
    var running_task_grid=new ifeng.running_task_grid({applyTo:'running_task_div',loadMask: false});
    running_task_grid.render();  
})
	 