 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>新建/修改模板</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.RadioGroup.js"></script>
	<script type="text/javascript" src="../res/js/conflictMgr.js"></script>		<!--冲突管理 -->
	<style>
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
	</style>
	<script>	
		var params__ = {
			id:#{id!0},
			dataFormId:#{dataFormId!0},
			dataId:#{dataId!0},
			nodeId:#{nodeId!0},
			recordData:${recordData!"{}"}
		};
		if(params__.id==0){
			document.title = '新建模板';
		}else{
			document.title = '修改模板';
		}
	</script>	
	<script>		
	/*
历史记录管理。包括获取历史记录列表、回滚
*/
//根据模板渲染内容
function renderField(exttpl){
	var tplStr = exttpl;
	return function(value, p, record){
		var tpl = new Ext.XTemplate(tplStr);
		return tpl.applyTemplate(record.data);
	}
}
//历史记录列表
var hisListMgr = {
	pageSize:15,
	grid:null,
	column:null,
	versionKey:"",
	formPanel:null,
	pagerBar:null,
	formDataCache:{},
	store:null,
	init:function(){
		this.store = new Ext.data.Store({ 
			proxy : new Ext.data.HttpProxy({url : 'version!list.jhtml',method:'post'}), 
			baseParams :{
				'version.key':this.versionKey,
				limit:15
			},
			reader : new Ext.data.JsonReader({
				autoLoad:true,
				root : "result",
				totalProperty : "totalCount",
				fields: ['id','key','lastmodifyStr','username','timestamp']
			}),
			remoteSort: false
		});
		this.initPageBar();
		this.initColumn();
		this.initGrid();
		this.store.load({
			start:0,
			limit:this.pageSize
		});
		
	},
	initPageBar:function(){
		this.pagerBar = new Ext.PagingToolbar({ 
			pageSize : this.pageSize, 
			store : this.store, 
			displayMsg : '{0}-{1}/{2}',
			emptyMsg : "没有要显示的数据",
			firstText : "首页",
			prevText : "前一页",
			nextText : "下一页",
			lastText : "尾页",
			refreshText : "刷新",
			displayInfo : true 
		});
	},
	initGrid:function(){
		var grid = new Ext.grid.GridPanel({ 
			renderTo:'placeholder_his',
			stripeRows: true,　　//隔行换色
			loadMask:{ 
				msg:"数据正在加载中...." 
			}, 
			columnLines: true,　　//显示列线条
			plugins:Ext.grid.plugins.AutoResize?new Ext.grid.plugins.AutoResize():null,
			store: this.store,
			cm: this.column,
			trackMouseOver:true,
			stripeRows: true,
			sm: new Ext.grid.CheckboxSelectionModel(),
			loadMask: true,
			autoSizeColumns : true,
			autoScroll:true, 
			border:false,
			viewConfig: {
				forceFit:false,//宽度是否平均分
				enableRowBody:true
			},
			autoExpandColumn :'lastmodify',
			height:this.datagridPanel_his.body.getHeight(),

			iconCls:'icon-grid',
			frame:false,
			bbar : hisListMgr.pagerBar
		}); 
		this.grid = grid;
	},
	initColumn:function(){
		var cm = new Ext.grid.ColumnModel([{
				id:'lastmodify',
			   header: "更新时间",
			   dataIndex: 'lastmodifyStr',
			   menuDisabled: true,
			   sortable:true
			},{
			    header: "操作人",
			    dataIndex: 'username',
			    menuDisabled: true,
				sortable:true,
			    
			},{
			   header: "回滚",
			   menuDisabled: true,
			   sortable:true,
			   dataIndex: 'key',
			   width: 80,
			   align:'center',
			   renderer:renderField('<a href ="javascript:hisListMgr.rollback(\'{key}\',{timestamp})">回滚</a>')
			}
		]);
		this.column = cm;
	},
	rollback:function(key,timestamp){
		if(hisListMgr.formDataCache[key+timestamp]){
			hisListMgr.formPanel.form.setValues(hisListMgr.formDataCache[key+timestamp]);
		}else{
			Ext.getBody().mask("正在回滚......");
			Ext.Ajax.request({  
				url:'version!getFormDatabyKey.jhtml?version.key='+ key +'&timespan=' + timestamp,  
				method:'get',	
				options:{key:key+timestamp},
				success:function(response,opts){
					Ext.getBody().unmask();
					try{
						var ret = Ext.util.JSON.decode(response.responseText);
						hisListMgr.formDataCache[opts.options.key] = ret;
						hisListMgr.formPanel.form.setValues(ret);
						
					}catch(ex){
						Ext.Msg.show({
							title:'错误提示',
							msg: '回滚数据失败',
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							minWidth:420,
							icon: Ext.MessageBox.ERROR 
						});
					}
					
				},
				failure:function(ret,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
						title:'错误提示',
						msg: '获取回滚数据失败',
						buttons: Ext.Msg.OK,
						animEl: 'elId',
						minWidth:420,
						icon: Ext.MessageBox.ERROR 
					});
				}
			});
		}
		hisListMgr.historyWin.hide();
	}

}

//初始化历史记录
function initHistory(){
	//添加回滚按钮
	if(!hisListMgr.formPanel) return;

	var bbar = hisListMgr.formPanel.getBottomToolbar();
	bbar.add({
		text:'历史记录>>',
		handler:function(obj){
			if(!hisListMgr.historyWin){
				hisListMgr.historyWin = new Ext.Window({
					title:'历史记录',
					height:465,
					width:400, 
					buttonAlign: "center",
					closable:true ,
					closeAction:'hide',
					autoScroll:true,
					modal:false,
					layout:'fit',
					resizable :false,
					items:[{
						xtype:'panel',
						layout:'anchor',
						frame:false,
						header :false,
						border:false,
						id:'datagridPanel_his',
						items:[{
							xtype:'panel',
							anchor:'100%',
							autoHeight:true,
							frame:false,
							border:false,
							header :false,		
							id:'placeholder_his'
						}]
					}],
					defaultButton:'btnHisClose',
					buttons:[{
						scope:this,
						text:'关闭',
						id:'btnHisClose',
						handler:function(){
							hisListMgr.historyWin.hide(this.el);
						}
					}]
				});
				hisListMgr.historyWin.show(this.el,function(){
					hisListMgr.datagridPanel_his = Ext.getCmp('datagridPanel_his');
					hisListMgr.init();
					hisListMgr.grid.render();	
				});
			}else{
				hisListMgr.historyWin.center();
				hisListMgr.historyWin.show(this.el);
			}
		}
	});
	hisListMgr.formPanel.ownerCt.doLayout();

};

</script>		
<head>
<body>	
<script>	
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'qtip';

	new Ext.Viewport({
		layout:'fit',
		items:[{
			xtype:'form',
			layout:'xform',
			//style:'padding:10px',
			bodyStyle:'padding:10px',
			itemCls:"itemStyle",
			buttonAlign:'center',
			id:'mainFormPanel',
			autoScroll:true,
			bbar:params__.id>0?[{
				xtype:'textfield',
				style:'border:1px inset #A9BFD3;background:transparent;cursor:default;',
				readOnly:true,
				width:250,
				id:'editorInfoBox'
			},{
				xtype:'tbfill'
			}]:null,
			items:[{
				fieldLabel:'名称',
				xtype:'textfield',
				emptyText:'不能为空',
				allowBlank:false,
				blankText:'不能为空',
				width:300,
				name:'name',
				value:params__.recordData.name
			},{
				fieldLabel:'内容',
				xtype:'textarea',
				width:600,
				height:600,
				name:'content',
				value:params__.recordData.content
			},{
				fieldLabel:'是否启用',
				xtype:'radiogroup',
				name:'enable',
				data:[[1,'启用'],[0,'停用']],
				value:params__.recordData.enable==0?0:1
			},
			{
				fieldLabel:'模板目录',
				xtype:'textfield',
				emptyText:'不能为空',
				allowBlank:false,
				blankText:'不能为空',
				width:300,
				name:'powerPath',
				value:params__.recordData.powerPath
			}],
			buttons:[{
				text:'保存',
				handler:function(e,obj){
					submitFormPanel(e);
				}
			},{
				text:'保存并继续添加',
				handler:function(e,obj){
					submitFormPanel(e,false,true);
				}
			},{
				text:'保存并关闭',
				style:top.centerTabPanel?'':'display:none',
				handler:function(e,obj){
					submitFormPanel(e,true);
				}
			},{
				text:'关闭',
				style:top.centerTabPanel?'':'display:none',
				handler:function(e,obj){
					closeMe();
				}
			}]			
		}]
		
	});
	formPanel__ = Ext.getCmp('mainFormPanel');//全局
	
	//历史记录	
	if(params__.id>0){
			//历史记录
		hisListMgr.formPanel = formPanel__;
		hisListMgr.versionKey= params__.nodeId + '_template_'+ params__.id;
		//'id='+ params__.id +'_dataFormId='+ params__.dataFormId +'_dataId='+ params__.dataId +'_nodeId='+ params__.nodeId;
		initHistory();
		
		//编辑冲突检查
		conflictMgr.init(Ext.getCmp('editorInfoBox'),'tplform_edit_' + 'id' + params__.id  + '_' + 'dataFormId' + params__.dataFormId + '_' + 'dataId' + params__.dataId);
	}
	//关闭自己所在的页签
	function closeMe(){
		if(top && top.centerTabPanel){
			top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
		}
	}
	//更改自己所在页签的标题
	function updateTabTitle(){
		if(top && top.centerTabPanel){
			top.centerTabPanel.getActiveTab().setTitle('【' + document.title+'】' + top.centerTabPanel.getActiveTab().getTitle());
		}
	};
	updateTabTitle();
	
	function submitFormPanel(e,isClose,isContinue){
		var formPanel = formPanel__;
		if(!formPanel.form.isValid()){
			Ext.Toast.show('输入未完成或验证不通过',{
			   title:'提示',
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   minWidth:420,
			   icon: Ext.MessageBox.WARNING
			});
			return;
		}

		formPanel.form.submit({  
			waitTitle : "请稍候",  
			waitMsg : "正在提交数据，请稍候......",  
			url :'tplform!save.jhtml', 
			params:{id:params__.id,dataFormId:params__.dataFormId,dataId:params__.dataId,nodeId:params__.nodeId},
			method : "POST",  
			success : function(form, action) {  
				Ext.Msg.show({
				   title:'提示',
				   msg:  '提交成功！',
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.INFO 
				});
				if(isClose){
					closeMe();
				}
				var id=0;
				if(!isContinue){
					id = typeof(action.result)=='undefined'?params__.id:action.result.Id;
				}	
				location.href = 'tplform!editor.jhtml?dataFormId='+ params__.dataFormId  +'&dataId='+params__.dataId+'&id=' + id+'&nodeId=' + params__.nodeId;
			},  
			failure : function(form, action) { 
				var msg="出现未知异常!";
				if(action.failureType){
					msg="failureType:"+action.failureType;
				}else
					msg = action.result?action.result.message:action.response.statusText;
				Ext.Msg.show({
				   title:'错误提示',
				   msg:  decodeURIComponent(msg),
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR 
				});						
			}  
		}); 
	}
});
</script>		
	
</body>
</html>	
