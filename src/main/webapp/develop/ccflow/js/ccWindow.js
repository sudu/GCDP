/**
* 属性配置编辑器,依赖
*/
function ccWindow(){

}
ccWindow.prototype.constructor = ccWindow;
ccWindow.prototype.form = null;
ccWindow.prototype.editor = null;
ccWindow.prototype.ownerCt = null;
ccWindow.prototype.isEditState = !0;
ccWindow.prototype.show = function(){
	if(this.ownerCt){
		this.hide();
		this.form.show();
		//隐藏不需要的子项
		//this.hideSomething();
	}
};
ccWindow.prototype.hide = function(){
	if(this.ownerCt){
		var items = this.ownerCt.items;
		for(var i=items.length-1;i>=0;i--){
			items.get(i).hide();
		}
	}
};
//接口
ccWindow.prototype.hideSomething = function(){

};

/**
* 元素属性配置编辑器
*/
function ccCellWindow(container,editor){
	this.ownerCt = container;
	this.editor = editor;
	this.init();	
	if(typeof this.plugin ==="function"){
		this.plugin();	
	}
}
ccCellWindow.prototype = new ccWindow;
ccCellWindow.prototype.constructor = ccCellWindow;
ccCellWindow.prototype.cell = null;
ccCellWindow.prototype.init = function(){
	this.isEditState = !this.editor.locked;
	if(this.form) return;
	var form  = new Ext.form.FormPanel({
		labelWidth:80,
		labelAlign:'left',
		layout:'xform2',
		autoScroll:true,
		frame:true,
		itemCls:"itemStyle5",
		enableKeyEvents:true,
		items:[{
			fieldLabel :'标签',
			xtype:'textarea',
			name:'label',
			readOnly:true,
			anchor:'95%',
			height:40
		},{
			fieldLabel :'警报邮箱',
			xtype:'textfield',
			name:'mailTo',
			anchor:'95%',
			readOnly:!this.isEditState,
			extra:{
				xtype:"checkbox",
				boxLabel :"启用邮箱报警",
				checked : true,
				value:true,
				name:"mailToEnable"			
			}
		},{
			fieldLabel :'警报手机号',
			xtype:'textfield',
			name:'smsTo',
			anchor:'95%',
			readOnly:!this.isEditState,
			extra:{
				xtype:"checkbox",
				boxLabel :"启用短信报警",
				checked : true,
				inputValue:1,
				name:"smsToEnable"
			}
		},{
			fieldLabel :'备注',
			xtype:'textarea',
			name:'remark',
			anchor:'95%',
			readOnly:!this.isEditState,
			height:100,
			grow:true,//大小是否可变
            growMin:50,//在大小可变的情况下设置最小宽度
            growMax:200//在大小可变的情况下设置最大宽度
		},{
			fieldLabel :'脚本',
			xtype:'textarea',
			anchor:'95%',
			height:150,
			extra:{
				xtype:"button",
				text:this.isEditState?"录入":"查看",
				style:"display:inline-block;",
				listeners:{
					scope:this,
					'click':function(){
						var cellType = this.editor.graph.validator.getCellType(this.cell);
						if("switch"===cellType){
							this.inputScript("process","process_condition");
						}else{
							this.inputScript("process","process");
						}
					},
					render:function(obj){
						obj.container.child("textarea").setDisplayed(false);
					}
				}						
			}
		},{
			fieldLabel :'异常处理脚本',
			xtype:'textarea',
			anchor:'95%',
			height:150,
			extra:{
				xtype:"button",
				text:this.isEditState?"录入":"查看",
				style:"display:inline-block;",
				listeners:{
					scope:this,
					'click':function(){
						this.inputScript("process_exception","process_exception");
					},
					render:function(obj){
						obj.container.child("textarea").setDisplayed(false);
					}
				}						
			}
		},{
			fieldLabel :'执行条件值',
			xtype:'textfield',
			name:'case',
			readOnly:!this.isEditState,
			anchor:'95%'
		}],
		listeners: {
			scope:this,
            beforehide : function(p){
                this.save();
            }
        }
	});
	this.form = form;
	this.ownerCt.add(form);
	this.ownerCt.doLayout();
	this.form.hide();
};
ccCellWindow.prototype.inputScript = function(type,tpl){
	if(globalvars.id==0){
		alert("保存后才能录入脚本");	
		return;
	}
	var url = "scriptdebug.jhtml?nodeId=" + globalvars.nodeId + "&id1="+ globalvars.id +"&id2=" + this.cell.objectIdentity + "&stype="+ type +"&tpl="+tpl;
	if(top && top.openTab){
		top.openTab(url,"脚本窗口");
	}else{
		window.open(url);
	}
};
ccCellWindow.prototype.setCell = function(v){
	this.show();
	this.cell = v;
	this.form.getForm().setValues(v.property);

	var chkMail = this.form.find("name","mailTo")[0].extra;
	chkMail.setValue(v.property.mailToEnable!==0?true:false);
	var chkSms = this.form.find("name","smsTo")[0].extra;
	chkSms.setValue(v.property.smsToEnable!==0?true:false);
	
	this.hideSomething();
};
ccCellWindow.prototype.save = function(){
	if(this.cell){
		var v = this.form.getForm().getValues();
		delete v.label;
		v.mailToEnable = v.mailToEnable?1:0
		v.smsToEnable = v.smsToEnable?1:0;
		
		this.cell.setProperty(v);
	}
};
ccCellWindow.prototype.hideSomething = function(){
	var caseFld = this.form.find("name","case");
	if(caseFld.length==0) return;
	//如果不需要case配置则隐藏
	var shapeName = this.cell.parent.length>0?ccUtils.getFunctionName(this.cell.parent[0].shape.constructor):"";
	if(shapeName==="ccRhombusShape"){
		caseFld[0].container.parent().show();
	}else{
		this.form.getForm().setValues({"case":null});
		caseFld[0].container.parent().hide();
	}
};

/**
* 流程图属性编辑器
*/
function ccGraphWindow(container,editor){
	this.ownerCt = container;
	this.editor = editor;

	this.init();	
}
ccGraphWindow.prototype = new ccWindow;
ccGraphWindow.prototype.constructor = ccGraphWindow;
ccGraphWindow.prototype.graph = null;
ccGraphWindow.prototype.init = function(){
	this.isEditState = !this.editor.locked;
	if(this.form) return;
	
	var formItemsArr=[{
			fieldLabel :'标题',
			xtype:'textfield',
			name:'title',
			readOnly:!this.isEditState,
			anchor:'95%'
		},{
			fieldLabel :'作者',
			xtype:'textfield',
			name:'author',
			readOnly:true,
			anchor:'95%'
		},{
			fieldLabel :'创建时间',
			xtype:'textfield',
			name:'createTime',
			readOnly:true,
			anchor:'95%'
		}/*,{
			fieldLabel :'最后修改时间',
			xtype:'textfield',
			name:'lastMofdifyTime',
			readOnly:true,
			anchor:'95%'
		}*/,{
			fieldLabel :'功能描述',
			xtype:'textarea',
			name:'description',
			readOnly:!this.isEditState,
			anchor:'95%',
			height:200
		}
	];
	
	if(this.isEditState){
		formItemsArr.push({
			xtype:'panel',
			collapsible: true,
			//layout:"accordion",
			defaults:{
				autoHeight:true,
				autoScroll:true,
				collapsible: true
			},
			items:[{
				title:"调试注入变量",
				items:{
					xtype:"listfield",
					id:"injectedVariables"
				}
			},{
				//title:'<span><a style="margin:0 5px" id="btnScriptDebug" href="javascript:void(0);">Run</a></span><span style="float:right"><a style="margin:0 5px" id="btnGetLog" href="javascript:void(0);">刷新日志</a><span>',
				xtype:'panel',
				collapsible: false,
				id:'logPanel',
				tbar:[{
					text:"运行",
					scope:this,
					handler:this.scriptDebug
					//icon:'../res/img/debug.jpg'
				},{
					xtype:'tbfill'
				},{
					text:"刷新日志",
					scope:this,
					handler:this.getLog
				}]
			}]
		});
	}
	
	var form   = new Ext.form.FormPanel({
		labelWidth:80,
		labelAlign:'left',
		layout:'xform',
		autoScroll:true,
		frame:true,
		itemCls:"itemStyle5",
		items:formItemsArr,
		listeners: {
			scope:this,
            beforehide : function(p){
                this.save();
            }
        }
	});
	this.form = form;
	this.ownerCt.add(form);
	this.ownerCt.doLayout();
	this.form.hide();
	
	/*
	Ext.get("btnScriptDebug").on("click",function(){
		this.scriptDebug();
	},this);
	Ext.get("btnGetLog").on("click",function(){
		this.getLog();
	},this);	
	*/
	this.logPanel = Ext.getCmp("logPanel");	
};
ccGraphWindow.prototype.setGraph = function(v){
	this.show();
	this.graph = v;
	this.form.getForm().setValues(v.property);
};
ccGraphWindow.prototype.save = function(){
	if(this.graph){
		var v = this.form.getForm().getValues();
		this.graph.setProperty(v);
	}
};
/*
*调试运行
*/
ccGraphWindow.prototype.scriptDebug = function(){
	if(globalvars.id==0){	
		Ext.msg.alert("请保存后再运行");
		return;
	}
	this.logPanel.body.getUpdater().update({
		url: "workflow.jhtml", 
		params: {
			nodeId: globalvars.nodeId,
			id1: globalvars.id,
			stype:"process",
			variables:Ext.getCmp("injectedVariables").getValue()
		}
	});
	
};
/*
*请求日志
*/
ccGraphWindow.prototype.getLog = function(){
	if(globalvars.id==0){	
		return;
	}
	this.logPanel.body.getUpdater().update({
		url: "../runtime/getScriptLog.jhtml", 
		params: {
			nodeId: globalvars.nodeId,
			id1: globalvars.id,
			stype:"process"
		}
	});
};


/**
* 元素属性配置编辑器
*/
function ccTextWindow(editor){
	this.container = editor.config.graph.container.body.first();
	this.ownerCt = this.container.createChild({
		tag:'textarea',
		style:"position:absolute;display:none;left:0;top:0;text-align:center;z-index:1000;width:74px;height:36px;"
	});
	this.ownerCt.on("click",function(e){
		e.cancelBubble = true;
		e.stopPropagation();
	});
	this.editor = editor;
	this.init();	
	if(typeof this.plugin ==="function"){
		this.plugin();	
	}
}
ccTextWindow.prototype.constructor = ccTextWindow;
ccTextWindow.prototype.cell = null;
ccTextWindow.prototype.container = null;
ccTextWindow.prototype.init = function(){
	this.ownerCt.setDisplayed(false);
	this.ownerCt.on("blur",function(e,obj){
		this.ownerCt.setDisplayed(false);
		this.cell.setProperty({label:obj.value});
	},this);
};
ccTextWindow.prototype.show = function(){
	var cell = this.cell;
	this.ownerCt.setDisplayed(true);
	var x = cell.shape.bounds.getCenterX()-this.ownerCt.getWidth()/2 + this.container.getLeft()-this.container.dom.scrollLeft;
	var y = cell.shape.bounds.getCenterY()-this.ownerCt.getHeight()/2 + this.container.getTop()-this.container.dom.scrollTop;
	this.ownerCt.setLocation(x,y);
	this.ownerCt.dom.value = this.cell.label;
};
ccTextWindow.prototype.save = function(){
	this.ownerCt.setDisplayed(false);
	this.cell.setProperty({label:this.ownerCt.dom.value});
};
