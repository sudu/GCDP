Ext.ns("ifeng");

Ext.onReady(function(){
		
	    Ext.QuickTips.init();
	    ifeng.running_script_grid = Ext.extend(Ext.lingo.JsonGrid, {
		    id: 'running_script_grid',
		    urlPagedQuery: 'scriptThreadMgr!list.jhtml?nodeId=' + globalvars.nodeId,
		    dlgWidth: 300,
		    dlgHeight: 400,
		    autoHeight:true,
		    autoWidth:true,
		   
		    formConfig: [
		        {fieldLabel: 'ID', name: 'key', readOnly: true,width:80},
		        {fieldLabel: '开始时间', name: 'startDate',xtype:'datefield',format:'Y-m-d',width:120},
		        {fieldLabel: '节点ID', name: 'nodeId',width:120},
		        {fieldLabel: '脚本类型', name: 'stype',width:120},
		        {fieldLabel: '超时时长', name: 'timeout'},
		        {fieldLabel: '执行人', name: 'excutor'},
		        {fieldLabel: '状态', name: 'status',
		        	renderer:function(v,p,record){
		        		if(v=="Running"){
		        			return "<font color='#28FF28'>正在执行...</font>";
		        		}else if(v=="Interrupted"){
		        			return "<font color='red'>被终止...</font>";
		        		}else{
		        			return v;
		        		}
		        	}
		        },
		        {fieldLabel: 'dataPool',name:'dataPoolJSON',
		        	renderer:function(value, cellmeta, record, rowIndex, columnIndex, store){
		        		var row=record.id;
		        		return '<a href="javascript:showDataPoolWindow('+ row +');">dataPool</a>';
		        	}
		        },
		        {
		        	fieldLabel: '运行节点',
		        	name:'daemonFlag',
		        	renderer:function(v,p,record) {
		        		if(v=="0") {
		        			return "前台节点";
		        		}else if(v == "1") {
		        			return "后台节点";
		        		}else {
		        			return v;
		        		}
		        	}
		        }
		     ],
		     
		buildToolbar: function() {

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



	        this.tbar = new Ext.Toolbar([
	     	{
	            id      : 'del',
	            text    : '停止执行',
	            iconCls : 'delete',
	            tooltip : '停止执行',
	            handler : this.stopTask.createDelegate(this)
	        },
	        {
	            id      : 'refreshTask',
	            text    : '刷新',
	            iconCls : 'check',
	            tooltip : '刷新',
	            handler : this.refreshTask.createDelegate(this)
	        }
	        ]);
	        
			this.filter = new Ext.form.TextField({
	            'name': 'filter'
	        });
	        
	        this.filter.on('specialkey', this.onFilterKey.createDelegate(this));
	        
	        


	        this.store.load();
	    },
	    
	    
	    
	    refreshTask:function(){
		    var store=Ext.getCmp('running_script_grid').store;
	    	store.reload();
	    },
	    
	    stopTask:function(){
	    	if (!this.dialog) {
	            this.createDialog();
	        }
	        var selections = this.getSelections();
	    	if(selections.length>1){
	    		Ext.MessageBox.alert('错误','只能选择一个任务停止');
	    	}else if(selections.length<1){
	    		Ext.MessageBox.alert('错误','选择一个任务停止');
	    	}else{
	    		var skey=selections[0].get("key");
	    		var daemonFlag = selections[0].get("daemonFlag");  
	    		this.body.mask('提交数据，请稍候...', 'x-mask-loading');
	                        Ext.Ajax.request({
	                            url     : 'scriptThreadMgr!interrupt.jhtml?skey='+skey+'&daemonFlag='+daemonFlag,
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
	    }
	 
	});
	Ext.reg('running_script_grid', ifeng.running_script_grid);
	running_script_grid=new ifeng.running_script_grid({applyTo:'running_script_div',loadMask: false});
	running_script_grid.render();  
	
});
var running_script_grid;
function showDataPoolWindow(i){
				var record= running_script_grid.store.getById(i);
		    	var setting= new Ext.Window({
			        layout: 'fit',
			        title:'dataPool',
			        width:400,
			        height:250,
			        closeAction: 'close',
			        html:record.data["dataPoolJSON"]
			    });
			    
			    setting.show();
		}
