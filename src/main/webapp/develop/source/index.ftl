 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>数据源订阅</title>

	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="../../res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="../../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="../../res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="../../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="../../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="../../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript" src="../../res/js/controls/Ext.ux.MultiSelect.js?20120516"></script>	
	<style>
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.itemStyle5 {
		    padding:5px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		/**checkboxGroup radiogroup**/
		.inputUnit {
			display: inline-block;
			margin-right: 5px;
		}
		/***for Ext.ux.MultiSelect****/
		checked {
			background-image: url("../../res/js/ext2/resources/images/default/menu/checked.gif");
		}
		.unchecked {
			background-image: url("../../res/js/ext2/resources/images/default/menu/unchecked.gif");
		}
		.ux-MultiSelect-icon {
			background-position: -1px -1px !important;
			background-repeat: no-repeat !important;
			float: left;
			height: 16px;
			width: 16px;
		}
		.ux-MultiSelect-icon-checked {
			background: url("../../res/js/ext2/resources/images/default/menu/checked.gif") repeat scroll 0 0 transparent;
		}
		.ux-MultiSelect-icon-unchecked {
			background: url("../../res/js/ext2/resources/images/default/menu/unchecked.gif") repeat scroll 0 0 transparent;
		}
	</style>
<script type="text/javascript">
Cookies={};
/*
 * 读取Cookies
 */
Cookies.get = function(name){
	var arg = name + "=";
	var alen = arg.length;
	var clen = document.cookie.length;
	var i = 0;
	var j = 0;
	while(i < clen){
		j = i + alen;
		if (document.cookie.substring(i, j) == arg)
			return Cookies.getCookieVal(j);
		i = document.cookie.indexOf(" ", i) + 1;
		if(i == 0)
			break;
	}
	return null;
};

Cookies.getCookieVal = function(offset){
   var endstr = document.cookie.indexOf(";", offset);
   if(endstr == -1){
	   endstr = document.cookie.length;
   }
   return decodeURIComponent(document.cookie.substring(offset, endstr));
};
	
openTab = function(url,title){
	if(!title) title='';
	if(top&& top.centerTabPanel){
		var _url = url;
		top.centerTabPanel.addIframe('tab_' + escape(_url),title ,_url);
	}else{
		window.open(url);	
	}
}
</script>

</head>
<body>
<textarea id="txtScriptTemplate" style="display:none;">
//订阅过滤器*****
function filter(){
	var flag = false;
	var str = dataPool.get("data").get("field_1")+"";
	if(str.indexOf("test")>=0){
		flag = true;
	}else{
		flag = false;
	}
	return flag;
}

//获取订阅参数列表
function getParamsList(){
	return ["field_1","field_2","field_3"];
}

var ret = filter();
var plist = getParamsList();
dataPool.put("subscribe_ret",ret);
dataPool.put("subscribe_plist",plist);
</textarea>

<script type="text/javascript">
var params = Ext.parseQuery();
var _sourceId = params['id'];
var _nodeId = params['nodeId'];

sourceMgr = {
	isEdit:_sourceId && _sourceId>0,
	centerPanel:null,
	init:function(){
		var viewport = new Ext.Viewport({
			layout: 'border',
			items:[{
				xtype:"panel",
				id:"centerPanel",
				region:'center',
				//style:'padding:5px',
				layout:'fit'
			}]
		});
		var centerPanel = viewport.findById('centerPanel');
		this.centerPanel = centerPanel;
		
		if(this.isEdit){//编辑状态
			document.title = '数据源';
			var panel = new Ext.Panel({
				border:false,
				frame:false,
				layout:'fit',
				style:'padding:5px',
				id:'newSourcePanel',
				autoScroll:true
			});	
			centerPanel.add(panel);
			centerPanel.doLayout();

			this.getSource();
			
		}else{//新建状态
			document.title = '发布数据源';
			var panel = new Ext.Panel({
				border:false,
				layout:'fit',
				id:'newSourcePanel',
				style:'padding:5px',
				autoScroll:true,
				//autoHeight:true,
				tbar:[{
					xtype:'label',
					style:'padding-left:10px;',
					text:'请选择要发布的表单：'
				},{
					xtype:'combo',
					id:'cbForm',
					autoWidth:true,	
					allowBlank : false, 
					readOnly : false,  
					editable:false,		
					mode : "remote",      
					triggerAction : 'all',
					store : new Ext.data.JsonStore({ //填充的数据
					   url : "../source!add.jhtml?nodeId=" + _nodeId,
					   fields : ['title', 'id'],
					   root : 'formlist'
					}),
					valueField : 'id',
					displayField : 'title',
					listeners:{
						scope:this,
						'select':function(obj,record,index){
							var  formId = record.data.id;
							if(formId=='')return;
							this.ajaxGetFields(formId,this.newSource);
						}
					
					}					
				}]
			});
			centerPanel.add(panel);
			centerPanel.doLayout();
		}
	},
	ajaxGetFields:function(formId,callback,options){
	
		Ext.getBody().mask("正在获取数据......");
		Ext.Ajax.request({  
			url:'../source!fieldList.jhtml?nodeId=' + _nodeId + '&form.id=' + formId,  
			method:'get',	
			options:options,
			success:function(response,opts){
				Ext.getBody().unmask();
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
					var isError =false;
					if(ret.success==false){
						isError = true;
					}else{
						if(typeof(callback)=='function')callback(ret,opts.options);
					}
				}catch(ex){
					isError=true;
				}
				if(isError){
					Ext.Msg.show({
					   title:'错误提示',
					   msg: ret&&ret.message?decodeURIComponent(ret.message):'出现异常',
					   buttons: Ext.Msg.OK,
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});
				}
			},
			failure:function(ret,opts){
				Ext.getBody().unmask();
				Ext.Msg.show({
					title:'错误提示',
					msg: ret.message?decodeURIComponent(ret.message):'出现异常',
					buttons: Ext.Msg.OK,
					animEl: 'elId',
					minWidth:420,
					icon: Ext.MessageBox.ERROR 
				});
			}	
		});
						
	},
	getSource:function(){
		Ext.getBody().mask("正在获取数据......");
		Ext.Ajax.request({  
			url:'../source!viewSource.jhtml?nodeId=' + _nodeId + '&source.id=' + _sourceId,  
			method:'get',	
			success:function(response,opts){
				Ext.getBody().unmask();
				try{
					var ret = Ext.util.JSON.decode(response.responseText);
					var isError =false;
					if(ret.success==false){
						isError = true;
					}else{
						sourceMgr.ajaxGetFields(ret.formId,sourceMgr.initSourcePanel,{data:ret});	
					}
				}catch(ex){
					isError=true;
				}
				if(isError){
					Ext.Msg.show({
					   title:'错误提示',
					   msg: ret.message?decodeURIComponent(ret.message):'出现异常',
					   buttons: Ext.Msg.OK,
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});
				}
			},
			failure:function(ret,opts){
				Ext.getBody().unmask();
				Ext.Msg.show({
					title:'错误提示',
					msg: ret.message?decodeURIComponent(ret.message):'出现异常',
					buttons: Ext.Msg.OK,
					animEl: 'elId',
					minWidth:420,
					icon: Ext.MessageBox.ERROR 
				});
			}
		});
	},
	newSource:function(data){
		var newSourcePanel = Ext.getCmp('newSourcePanel');
		if(sourceMgr.newSourcePanel) newSourcePanel.remove(sourceMgr.newSourcePanel);
		sourceMgr.newSourcePanel = sourceMgr.createSourcePanel(data);
		newSourcePanel.add(sourceMgr.newSourcePanel);
		newSourcePanel.doLayout();
	},
	createSourcePanel:function(data){
		var cbForm = Ext.getCmp('cbForm');
		var sourceName = data.name;
		
		if(cbForm){
			var store = cbForm.store;
			sourceName = store.getAt(store.find('id',data.formId)).data.title;
		}
		var panel= new Ext.form.FormPanel({
			buttonAlign:'center',
			layout:'form',
			frame:false,
			labelAlign :'left',
			itemCls:'itemStyle',
			style:'padding:10px',
			bodyStyle:'padding:0 10px 10px 10px',
			autoScroll:true,
			items:[{
				xtype:'hidden',
				name:'source.formId',
				value:data.formId
			},
			{
				border:false,
				layout:'column',
				items:[{
					layout: 'xform',
					border:false,
					width:570,
					items:[{
						xtype:'textfield',
						fieldLabel:'数据源名称',
						name:'source.name',
						allowBlank :false,
						blankText :'不能为空',
						value:sourceName,
						width:450
					}]
				},{
					border:false,
					hideLabels:true,
					//width:290,
					anchor:'99%',
					style:'padding-top:8px;',
					layout:'table',
					hidden:!(_sourceId>0),
					items:[{
						xtype:'button',
						text:'我要订阅',
						style:'margin-right:5px;',
						handler:function(obj){
							subMgr.showSubWin(this.el);
						}
					},{
						xtype:'button',
						text:'动态前端订阅',
						style:'margin-right:5px;',
						handler:function(obj){
							subMgr.showSubWin(this.el,1);
						}
					},{
						xtype:'button',
						text:'查看所有订阅者',
						handler:function(obj){
							if(top&& top.centerTabPanel){
								var url = 'source/subscriberMgr.html?sourceId='+_sourceId+'&nodeId=' + _nodeId;
							}else{
								var url = 'subscriberMgr.html?sourceId='+_sourceId+'&nodeId=' + _nodeId;
							}
							openTab(url,'所有订阅者');
							//todo Ext.Windows({
						}
					}]
				}]
			},
			{
				xtype:'textarea',
				fieldLabel:'说明',
				name:'source.des',
				width:450,
				value:data.desc,
				height:50		
			},{
				xtype:'fieldset',
				autoHeight :true,
				title:'选择要发布的字段',
				layout:'fit',
				items:[sourceMgr.initSourceGrid(data)]
			}],
			buttons:[{
				text:"提交",
				handler:function(obj,e){
					if(!obj.ownerCt.form.isValid()){
						Ext.Toast.show('输入未完成或不合法',{
						   title:'提示',
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.WARNING
						});
						return;
					}
					
					var store = sourceMgr.sourceGrid.store;
					var fieldList = store.query('checked','true');
					var fieldListArr=[];
					fieldList.each(function(item){
						fieldListArr.push(item.data);
					});
					var params = obj.ownerCt.form.getValues();
					params.nodeId = _nodeId	;
					params['source.id'] = _sourceId;
					params['source.fieldList'] = Ext.util.JSON.encode(fieldListArr);
					
					Ext.getBody().mask("正在提交数据......");
					Ext.Ajax.request({  
						url:_sourceId>0?'../source!update.jhtml':'../source!insert.jhtml',  
						params:params,
						success:function(response,opts){
							Ext.getBody().unmask();
							var ret = Ext.util.JSON.decode(response.responseText);
							if(ret.success == false){
								Ext.Msg.show({
									title:'错误提示',
									msg: ret.message?decodeURIComponent(ret.message):'出现异常',
									buttons: Ext.Msg.OK,
									minWidth:420,
									icon: Ext.MessageBox.ERROR 
								});
							}else{
								if(top.initSourceTree)top.initSourceTree();//更新主页面上的数据源树
								var msg = '提交成功';
								if(parseInt(ret.message)>0)_sourceId = parseInt(ret.message);
								Ext.Toast.show(msg,{
								   title:'提示',
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.INFO,
								   callback:sourceMgr.redirect
								});
							}
						},
						failure:function(ret,opts){
							Ext.getBody().unmask();
							Ext.Msg.show({
								title:'错误提示',
								msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
								buttons: Ext.Msg.OK,
								animEl: 'elId',
								minWidth:420,
								icon: Ext.MessageBox.ERROR 
							});
						}	
					});
				}
			}]
				
		});
		sourceMgr.sourcePanel = panel;
		return panel;
	},
	initSourceGrid:function(data){
		var checkColumn=new Ext.grid.CheckColumn({
			dataIndex: 'checked',
			width: 30,
			sortable :true
		});
		var cm = new Ext.grid.ColumnModel([
			new Ext.grid.RowNumberer(),
			checkColumn,
			{
				header: "字段名",
				dataIndex: 'name',
				width: 200
			},{
				header: "类型(长度)",
				dataIndex: 'type',
				width:120
			},{
				id:'title',
				header: "字段说明",
				dataIndex: 'title',
				editor: new Ext.form.TextField({
					allowBlank:true
				})
			}
			
		]);
		cm.allowSort = true;

		var store = new Ext.data.Store({
			proxy: new Ext.data.MemoryProxy(data), 
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				root:'fields',
				fields: ["name","type","title","checked"]
			}),
			sortInfo  : {field: "checked", direction: "DESC"} 
		});

		var grid = new Ext.grid.EditorGridPanel({
			store: store,
			frame:false,
			hideBorders:true,
			enableColumnHide:false,
			stripeRows :true,//表格行颜色间隔显示
			cm: cm,
			autoHeight:true,
			width:700,
			autoExpandColumn:'title',
			enableHdMenu: false,
			trackMouseOver : true,
			clicksToEdit:1,
			plugins:[checkColumn],
			tbar:[{
				text:'全选',
				handler:function(){
					sourceMgr.selectHelper('all');
				}
			},{
				text:'全不选',
				handler:function(){
					sourceMgr.selectHelper('none');
				}
			},{
				text:'反选',
				handler:function(){
					sourceMgr.selectHelper('not');
				}
			}]
		});
		
		sourceMgr.sourceGrid = grid;
		store.load();
		return grid;
	},
	initSourcePanel:function(formData,opts){
	
		var sourceInfo = opts.data;
		/**拷贝字段信息**/
		var sourceFlds = {};
		var formFlds = {};
		var sourceFldsArr=[];
		for(var i=0;i<sourceInfo.fields.length;i++){
			sourceFlds[sourceInfo.fields[i].name] = sourceInfo.fields[i];
		}
		for(var i=0;i<formData.fields.length;i++){
			var name = formData.fields[i].name;
			if(!sourceFlds[name]){
				sourceInfo.fields.push(formData.fields[i]);
			}
		}
		sourceMgr.newSource(sourceInfo);
	},
	
	selectHelper:function(flag){
		var store = sourceMgr.sourceGrid.store;
		switch(flag){
			case 'all':
				store.each(function(record){
					record.set('checked',true);
				});
				break;
			case 'none':
				store.each(function(record){
					record.set('checked',false);
				});
				break;
			case 'not'://反选
				store.each(function(record){
					record.set('checked',!record.data.checked);
				});
				break;	
		}
		sourceMgr.sourceGrid.getView().refresh();
	},
	redirect:function(){
		location.href = 'index.jhtml?id=' + _sourceId + '&nodeId=' + _nodeId
	}	
};

var subMgr = {
	showSubWin:function(sender,type){//type==1时 为动态前端订阅
		var items = [];
		items.push({
			xtype:'hidden',
			name:'nodeId',
			value:_nodeId
		},{
			xtype:'hidden',
			name:'subscribe.sourceId',
			value:_sourceId
		});
		
		if(type==1){
			var dstore = new Ext.data.JsonStore({
				url: "../data!list.jhtml?listId=2&nodeId=" + _nodeId,
				fields: ["id", "name"],
				root: 'data'
			});
			dstore.load();
			items.push({
				xtype:'hidden',
				name:'subscribe.name'
			},{
				xtype:'multiselect',
				fieldLabel:'选择动态前端',
				name:type==1?"subscribe.dynIds":'',
				width:250,
				editable: false,
				separator:',',
				valueField :"id",
				displayField: "name",
				mode: 'local',
				triggerAction: 'all',
				emptyText:'请选择(可多选)',
				store:dstore,
				listeners:{
					change:function(obj){
						var frm = Ext.getCmp('frmSubscriber');
						frm.find('name','subscribe.name')[0].setValue(this.el.dom.value);
					}
				}
			});
		}else{
			items.push({
				xtype:'textfield',
				fieldLabel:'订阅者名称',
				width:363,
				allowBlank :true,
				blankText :'不能为空',
				name:"subscribe.name"
			},{
				xtype:'textfield',
				fieldLabel:'回调接口URL',
				width:363,
				allowBlank :false,
				blankText :'不能为空',
				name:"subscribe.callBackUrl"
			},{
				xtype:'radiogroup',
				fieldLabel:'回调方法',
				name:'subscribe.method',
				data:"[['get','GET'],['post','POST']]",
				value:'get'//data.method
			});
		}
		items.push({
			xtype:'textfield',
			fieldLabel:'操作人',
			width:168,
			name:'subscribe.creator',
			value:Cookies.get('cmpp_cn')
		},{
			xtype:'textarea',
			fieldLabel:'脚本',
			name:'subscribe.script',
			value:txtScriptTemplateStr,
			width:363,
			height:282,
			extra:{
				xtype:"button",
				text:"调试脚本",
				style:"margin-left:1em;display:inline-block;",
				listeners:{
					'click':function(){
						subMgr.debug(this.field);
					}
				}						
			}
		});
		
		var win = new Ext.Window({
			title:'请填写订阅信息',
			height:500,
			width:600, 
			buttonAlign: "center",
			closable:true ,
			closeAction:'close',
			modal:true,
			maximizable:false,
			layout:'fit',
			items:[{
				xtype:'form',
				buttonAlign:'center',
				id:'frmSubscriber',
				labelAlign :'left',
				itemCls:'itemStyle5',
				style:'padding:5px',
				bodyStyle:'padding:0 5px 5px 5px',
				url:'../source!subscribe.jhtml',
				layout:'xform2',
				items:items
			}],
			buttons:[{
				text:'确定',
				handler:function(){
					var frm = Ext.getCmp('frmSubscriber').getForm();
					if(!frm.isValid()){
						Ext.Toast.show('输入不合法！',{
							title:'提示',
							time:1000,
							minWidth:420
						});
						return;
					}
					frm.submit({
						waitMsg : "正在提交数据，请稍候......",  
						success:function(form,xhr){
							var ret = Ext.util.JSON.decode(xhr.response.responseText);
							if(!ret.success){
								Ext.Msg.show({
								   title:'错误提示',
								   msg: decodeURIComponent(ret.message),
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR 
								});
							}else{
								Ext.Toast.show('订阅成功',{
								   title:'提示',
								   buttons: Ext.Msg.OK,
								   animEl: 'elId',
								   minWidth:420,
								   icon: Ext.MessageBox.INFO,
								   callback:function(){
										subMgr.subWin.close(); 
								   }
								});
							}
						},
						failure:function(form,xhr){
							Ext.Msg.show({
								   title:'错误提示',
								   msg: decodeURIComponent((xhr.result&&xhr.result.message)||xhr.response.statusText),
								   buttons: Ext.Msg.OK,
								   minWidth:420,
								   icon: Ext.MessageBox.ERROR 
							});				
						}	
					});
				}
			},{
				text:'关闭',
				handler:function(obj,e){
					subMgr.subWin.close();
				}
			}]
		});
		subMgr.subWin = win;
		win.show(sender);
	},
	debug:Ext.jsDebugger("../",_nodeId,_sourceId,null,"subscribe")
}

var txtScriptTemplateStr = document.getElementById('txtScriptTemplate').value;
Ext.onReady(function(){
Ext.BLANK_IMAGE_URL = "./../../res/js/ext2/resources/images/default/s.gif";
Ext.QuickTips.init();
Ext.form.Field.prototype.msgTarget = 'qtip';


sourceMgr.init();

});

</script>
</body>	
</html>