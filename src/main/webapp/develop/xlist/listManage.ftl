<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>列表视图配置</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<link rel="stylesheet" type="text/css" href="./../res/css/runTime.css" />
	
	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script> 
	<script type="text/javascript" src="./../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="./../res/js/controls/Ext.ux.CheckboxGroup.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<style>
		body {font-size:14px;}
		input,select{font-size:12px}
		label {margin:0 2px 0 2px}
		textarea {font-size:12px;font-family:tahoma,arial,helvetica,sans-serif}
		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.saveField{background:url("./../res/js/ext2/resources/images/default/my/save.gif") left  no-repeat !important;}
				
		.itemStyle {
		    padding:8px;
		    /*border: 1px solid silver;*/
		    margin-bottom:0;
		}
		.fieldsetStyle{
			margin:4px 10px 4px 10px
		}
		/**checkboxGroup**/
		.inputUnit {
			display: inline-block;
			margin: 5px;
			width: 160px;
		}

		.content{
			BORDER-BOTTOM: #fafafa 1px solid; BORDER-LEFT: #fafafa 1px solid; PADDING-BOTTOM: 0px; BACKGROUND-COLOR: #fafafa; MARGIN: 0px auto; PADDING-LEFT: 5px; WIDTH: auto; PADDING-RIGHT: 5px; DISPLAY: block; BORDER-TOP: #fafafa 1px solid; BORDER-RIGHT: #fafafa 1px solid; PADDING-TOP: 0px;
			font-size: 12px;
		}
		.content .TABLE {
			TEXT-ALIGN: center; BACKGROUND-COLOR: #cccccc; MARGIN: 5px; WIDTH: 95%
		}
		.content .TH {
			BACKGROUND-COLOR: #efeff2; HEIGHT: 30px;width:137px;
		}
		.content .TR {
			BACKGROUND-COLOR: #ffffff
		}
		.content .TD {
			PADDING-LEFT: 1px; HEIGHT: 25px
		}
		.content .head_td {
			TEXT-ALIGN: right; BACKGROUND-COLOR: #efeff2; WIDTH: 15%; PADDING-RIGHT: 10px; HEIGHT: 25px; FONT-WEIGHT: bold;width:137px;
		}
		.content .remark_td {
			TEXT-ALIGN: left; BACKGROUND-COLOR: #ffffd9; PADDING-LEFT: 5px;  HEIGHT: 25px
		}
		.content2 {
			BORDER-BOTTOM: #fafafa 1px solid; BORDER-LEFT: #fafafa 1px solid; PADDING-BOTTOM: 0px; BACKGROUND-COLOR: #fafafa; MARGIN: 0px auto; PADDING-LEFT: 5px; WIDTH: auto; PADDING-RIGHT: 5px; DISPLAY: block; BORDER-TOP: #fafafa 1px solid; BORDER-RIGHT: #fafafa 1px solid; PADDING-TOP: 0px;
			font-size: 12px;
		}
		.content2 .remark_td {
			TEXT-ALIGN: left; BACKGROUND-COLOR: #ffffd9; PADDING-LEFT: 5px;  HEIGHT: 25px
		}
		
		.details {width:100%;margin:5px 0 5px 0;}
		summary {background-color: #99BBE8;cursor:pointer； display: inline-block; cursor: pointer; vertical-align: top; text-indent: 1em; }

		.deleteButton{background:url("./../res/img/runTime/delete1.gif") left  no-repeat !important;width:16px;height:16px;cursor:pointer;}
	</style>
		
	<script>
		var listPage__=${listPageJson};
		var formConfig__= ${formConfig};	
		var viewConfig__ =  ${viewConfig};	
		var nodeId__=#{nodeId!0};
		//数据预处理
		var fields__ = [['','不绑定字段'],['id','ID']];
		for(var i=0;i<listPage__.fields.length;i++){
			var f = listPage__.fields[i];
			if(f[1]!='')
				fields__.push([f[0],f[0] + '(' + f[1] + ')']);
			else
				fields__.push([f[0],f[0]]);	
		}
		var LPCFG = listPage__.config?listPage__.config:{};
		if(!LPCFG.pagesize) LPCFG.pagesize=20;
		if(!LPCFG.filter)LPCFG.filter ='';
		if(!LPCFG.sql)LPCFG.sql ='';
		if(!LPCFG.template)LPCFG.template ='';
		if(!LPCFG.myTemplate)LPCFG.myTemplate ='';
		if(!LPCFG.viewId)LPCFG.viewId =0;		
		if(!LPCFG.buttons)LPCFG.buttons ={
			add:true,
			modify:true,
			"delete":true,
			ext:[]
			
		};
		if(!LPCFG.menus)LPCFG.menus ={
			add:false,
			modify:true,
			"delete":true
			
		};		
		if(!LPCFG.search)LPCFG.search =[];
		if(!LPCFG.searchSvr)LPCFG.searchSvr =[];
		if(!LPCFG.columns)LPCFG.columns =[];
		//if(!LPCFG.customSearch)LPCFG.customSearch ={};
		
		
		var searchFileds__={};
		//var searchableStr = ',' + formConfig__.fieldsConfig.searchable.join(',') + ','
		for(var i=0;i<formConfig__.fieldsConfig.fieldsConfig.length;i++){
			var col = formConfig__.fieldsConfig.fieldsConfig[i];
			if(col.l_allowSearch){
				searchFileds__[col.f_name] = {
					f_title:col.f_title==''?col.f_name:col.f_title,
					f_type:col.f_type
				};
			}
		}
		var opEnZh__={
			'=':'等于(=)',
			'like':'包含(like)',
			'not like':'不包含(not like)',
			'>':'大于(>)',
			'>=':'大于或等于(≥)',
			'<':'小于(<)',
			'<=':'小于或等于(≤)',
			'<>':'不等于(≠)'
		};
		var controlTypeEnZh__={
			'combo':'下拉框', 'textfield':'文本框', 'datefield':'日期'
		};
		var andorEnZH__ ={
			'and':'并且','or':'或者'
		}
		
		
		Ext.grid.CheckColumn = function(config){
			Ext.apply(this, config);
			if(!this.id){
				this.id = Ext.id();
			}
			this.renderer = this.renderer.createDelegate(this);
		};

		Ext.grid.CheckColumn.prototype ={
			init : function(grid){
				this.grid = grid;
				this.grid.on('render', function(){
					var view = this.grid.getView();
					view.mainBody.on('mousedown', this.onMouseDown, this);
				}, this);
			},

			onMouseDown : function(e, t){
				if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
					e.stopEvent();
					var index = this.grid.getView().findRowIndex(t);
					var record = this.grid.store.getAt(index);
					record.set(this.dataIndex, !record.data[this.dataIndex]);
				}
			},

			renderer : function(v, p, record){
				p.css += ' x-grid3-check-col-td'; 
				return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
			}
		};	
	function myZhuanyi(str){
		var ret = str;
		//ret = ret.replace(/&/g,'&amp;');
		ret = ret.replace(/"/g,'&quot;');
		ret = ret.replace(/>/g,'&gt;');
		ret = ret.replace(/</g,'&lt;');
		return ret;
	}	
	function myUnZhuanyi(str){
		var ret = str;
		ret = ret.replace(/&quot;/g,'"');
		ret = ret.replace(/&gt;/g,'>');
		ret = ret.replace(/&lt;/g,'<');
		//ret = ret.replace(/&amp;/g,'&');		
		return ret;
	}	

	function setActiveTab(url,tabId,title){
		if(top&& top.centerTabPanel){
			var _url = url;
			top.centerTabPanel.addIframe('tab_' + tabId,title ,_url);
		}else{
			window.open(url);	
		}
	}	
	</script>
	<script>
	Ext.ux.FormLayout = Ext.extend(Ext.layout.FormLayout, {
    renderItem : function(c, position, target){
        if(c && !c.rendered && c.isFormField && c.inputType != 'hidden'){
            var args = [
                   c.id, c.fieldLabel,
                   c.labelStyle||this.labelStyle||'',
                   this.elementStyle||'',
                   typeof c.labelSeparator == 'undefined' ? this.labelSeparator : c.labelSeparator,
                   (c.itemCls||this.container.itemCls||'') + (c.hideLabel ? ' x-hide-label' : ''),
                   c.clearCls || 'x-form-clear-left' 
            ];
            if(typeof position == 'number'){
                position = target.dom.childNodes[position] || null;
            }
            if(position){
                this.fieldTpl.insertBefore(position, args);
            }else{
                this.fieldTpl.append(target, args);
            }
            c.render('x-form-el-'+c.id);
            if(c.extra){
            	c.extra.renderTo=c.container;
            	c.extra.field=c;
            	Ext.ComponentMgr.create(c.extra);
            }
        }else {
            Ext.layout.FormLayout.superclass.renderItem.apply(this, arguments);
        }
    }
});
Ext.Container.LAYOUTS['xform'] = Ext.ux.FormLayout;
</script>	
</head>
<body>
<!-- 基础配置 -->
<div class="content" id="commonCfgBox">
<table cellSpacing=1 cellPadding=0 class="TABLE">
	<tr class="TR">
		<td class="head_td TD">视图名称:</td>
		<td height=25 width=540 align="left" class="TD"><input style="width: 95%" id="txtTitle" value=""  /> </td>
		<td class="remark_td TD"></td>
	</tr>
	<tr class="TR">
		<td class="head_td TD">过滤条件(filter):</td>
		<td height=25 width=540 align="left" class="TD"><input style="width: 95%" id="txtFilter" value=""  /> </td>
		<td class="remark_td TD"></td>
	</tr>	
	<tr class="TR">
		<td class="head_td TD">自定义sql:</td>
		<td height=25 width=540 align="left" class="TD"><input style="width: 95%" id="txtSql" value=""  /> </td>
		<td class="remark_td TD">自定义sql不允许存在order by 和 limit</td>
	</tr>	
	<tr class="TR">
		<td class="head_td TD">每页显示条数:</td>
		<td height=25 width=540 align="left" class="TD">
			<input type="range" id="rngPageCount" name="range" min="1" max="50" step="1" value="20" onchange="document.getElementById('txtPageCount').value = this.value;" />
			<input style="width: 50px" id="txtPageCount" value="20"  /> 
			<input type="checkbox" id="chkAutoPagesize" name="chkAutoPagesize" checked="checked" value=""/><label for="chkAutoPagesize">自适应</label>
		</td>
		<td class="remark_td TD"></td>
	</tr>	
	<tr class="TR">
		<td class="head_td TD">按钮配置:</td>
		<td height=25 width=540 align="left" class="TD" id="defaultButtonsCfgBox">
			<input type="checkbox" id="chkBtnAdd" name="chkBtnAdd" checked="checked" value=""/><label for="chkBtnAdd">添加</label>
			<input type="checkbox" id="chkBtnModify" name="chkBtnModify"  checked="checked" value=""/><label for="chkBtnModify">修改</label>
			<input type="checkbox" id="chkBtnDel" name="chkBtnDel" checked="checked" value=""/><label for="chkBtnDel">删除</label>
			<select name="selViewId" style="width: 150px;" id="selViewId">	
			</select>
			<img id="selViewIdHelp" src="../res/img/help.gif" />
		</td> 
		<td class="remark_td TD"></td>
	</tr>
	<tr class="TR">
		<td class="head_td TD">右键菜单配置:</td>
		<td height=25 width=540 align="left" class="TD" id="defaultMenusCfgBox">
			<input type="checkbox" id="chkMenuAdd" name="chkMenuAdd" value=""/><label for="chkMenuAdd">添加</label>
			<input type="checkbox" id="chkMenuModify" name="chkMenuModify"  checked="checked" value=""/><label for="chkMenuModify">修改</label>
			<input type="checkbox" id="chkMenuDel" name="chkMenuDel" checked="checked" value=""/><label for="chkMenuDel">删除</label>
		</td> 
		<td class="remark_td TD"></td>
	</tr>		
	<tr class="TR">
		<td class="head_td TD">自定义按钮配置:</td>
		<td height=25 width=540 align="left">
			<span id="buttonsBox" style="float:left;"></span><span id="addBtnBox" style="float:left;"></span>
		</td>
		<td class="remark_td TD"></td>
	</tr>	
	<tr class="TR">
		<td class="head_td TD">搜索面板配置:</td>
		<td height=25 width=540 align="left" class="TD" id="defaultButtonsCfgBox">
			<input type="checkbox" id="chkSearchDb" name="chkSearchDb" checked="checked" value=""/><label for="chkSearchDb">启用数据库搜索</label>
			<input type="checkbox" id="chkSearchSvr" name="chkSearchSvr"  checked="checked" value=""/><label for="chkSearchSvr">启用搜素服务搜索</label>
		</td> 
		<td class="remark_td TD"></td>
	</tr>		
	<tr class="TR">
		<td class="head_td TD">模板:</td>
		<td height=25 width=540 align="left">
			<select name="selTpl" style="width: 200px;" id="selTpl">
				<option value="listTemplate_1">通用列表页模板</option>
				<option value="listTemplate_2">自定义搜索列表页模板</option>
			</select>
			<input type="checkbox" id="chkUserMyTpl" value=""/><label for="chkUserMyTpl">使用自定义模板</label>
			<textarea id="txtTpl" style="width:95%;height:300px;display:none;"></textarea>
		</td>
		<td class="remark_td TD"></td>
	</tr>	
	<tr class="TR">
		<td class="head_td TD">&lt;/head&gt;之前注入:</td>
		<td height=25 width=540 align="left">
			<textarea id="txtInjectHead" style="width:95%;height:200px;"></textarea>
		</td>
		<td class="remark_td TD"></td>
	</tr>	
	<tr class="TR">
		<td class="head_td TD">&lt;/body&gt;之前注入:</td>
		<td height=25 width=540 align="left">
			<textarea id="txtInjectBody" style="width:95%;height:200px;"></textarea>
		</td>
		<td class="remark_td TD">
			<p></p>
		</td>
	</tr>		
</table>
</div>

<textarea id="txtCustomSearchSample" style="display:none;">
{
	searchableFields:[{
		field:'title',
		title:'标题',
		fieldType:'varchar'//varchar\int\..可选
	},{
		field:'source',
		title:'来源',
		fieldType:'varchar'//varchar\int\..可选	
	}],
	searchItem:[{
		field:'title',//字段名
		fieldTitle:'标题',//字段说明
		op:'like',//参考: '=','like','not like','>','≥','≤','≠' 其中之一
		value:'textfield',//参考:'combo':'下拉框', 'textfield':'文本框', 'datefield':'日期'
		isHide:false,
		andor:'and'//参考:and 或者 or
	},
	{
		field:'source',//字段名
		fieldTitle:'来源',//字段说明
		op:'=',//参考: '=','like','not like','>','≥','≤','≠' 其中之一
		value:'combo',//参考:'combo':'下拉框', 'textfield':'文本框', 'datefield':'日期'
		dataSource:[['china','中国'],['usa','美国']],
		isHide:false,
		andor:'and'//参考:and 或者 or
	}]
}			
</textarea>

<textarea id="txtSearchHTMLSample" style="display:none;">
<script>
function search(){
	var titleValue = Ext.get('cs_txtTitle').dom.value ;
	var cateValue = Ext.get('cs_selCate').dom.value ;
	var where = [];
	where.push({"field":"tilte","op":"like","value":titleValue,"andor":"and"});
	where.push({"field":"cate","op":"=","value":cateValue,"andor":"and"});
	listMgr.search({
		from:'db',
		where:where,
		sort:''
	});
}

</script>

<table>
	<tr>
		<td>标题:<input type="text" id="cs_txtTitle" value=""/></td>
		<td>分类:
			<select id="cs_selCate">
				<option value="1">图片</option>
				<option value="2">文字</option>
				<option value="3">视频</option>
			</select>
		</td>
		<td><button type="button" onclick="search();">搜索</button></td>
	</tr>		
</table>
</textarea>

<script>


Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';
		
	var txtTitle = Ext.get('txtTitle');
	var txtPageCount = Ext.get('txtPageCount');
	var rngPageCount = Ext.get('rngPageCount');
	var txtFilter = Ext.get('txtFilter');
	var txtSql = Ext.get('txtSql');
	var chkBtnAdd = Ext.get('chkBtnAdd');
	var chkBtnModify = Ext.get('chkBtnModify');
	var chkBtnDel = Ext.get('chkBtnDel');
	var chkMenuAdd = Ext.get('chkMenuAdd');
	var chkMenuModify = Ext.get('chkMenuModify');
	var chkMenuDel = Ext.get('chkMenuDel');	
	var selTpl = Ext.get('selTpl'); 
	var txtTpl = Ext.get('txtTpl');
	var txtInjectHead = Ext.get('txtInjectHead');
	var txtInjectBody = Ext.get('txtInjectBody');
	var buttonsBox = Ext.get('buttonsBox');
	var selViewId = Ext.get('selViewId');
	var chkUserMyTpl = Ext.get('chkUserMyTpl');
	var chkAutoPagesize = Ext.get('chkAutoPagesize');
	var chkSearchDb = Ext.get('chkSearchDb');
	var chkSearchSvr= Ext.get('chkSearchSvr');
	
	var btnCreateMyBtn=null;
	var txtCustomSearchCfg,txtSearchHTMLCfg;
	var tabpanelSearch;
	
	
	function listTplOnCheck(){
		var selTpl = Ext.get('selTpl').dom; 
		var txtTpl = Ext.get('txtTpl').dom;
		if(txtTpl.style.display=='none'){
			txtTpl.style.display='block';
			txtTpl.focus();selTpl.disabled=true
		}else{
			txtTpl.style.display='none';
			txtTpl.focus();
			selTpl.disabled=false;
		}
	}
	function listTplOnChange(){
		if(this.dom.selectedIndex==1){//自定义搜索的列表模板
			tabpanelSearch.setActiveTab(3);
			tabpanelSearch.items.items[0].setDisabled(true);
			tabpanelSearch.items.items[1].setDisabled(true);
			tabpanelSearch.items.items[2].setDisabled(true);
			tabpanelSearch.items.items[3].setDisabled(false);
		}else{
			tabpanelSearch.setActiveTab(0);
			tabpanelSearch.items.items[0].setDisabled(false);
			tabpanelSearch.items.items[1].setDisabled(false);
			tabpanelSearch.items.items[2].setDisabled(false);
			tabpanelSearch.items.items[3].setDisabled(true);
		}
	}
	chkUserMyTpl.on('change',listTplOnCheck);
	selTpl.on('change',listTplOnChange);
	
	var mBtnField_1,mBtnOp_1,mBtnSearchValue_1,mBtnAndOr_1,
		mBtnField_2,mBtnOp_2,mBtnSearchValue_2,mBtnAndOr_2,
		mBtnField_3,mBtnOp_3,mBtnSearchValue_3,mBtnAndOr_3;
	
		
	function createMyButton(btnCfg){
		var btn = buttonsBox.createChild({
			tag:'input',
			type:'button',
			style:'width:60px',
			value:btnCfg.text,
			cfg:escape(Ext.encode(btnCfg))
		},btnCreateMyBtn.dom);
		btn.on('click',function(obj,e,opts){
			showButtonCfgWin(opts);
		},this,{sender:btn,action:'edit',cfg:btnCfg});
	}
	function showButtonCfgWin(opts){
		if(!opts){
			opts = this;
		}
		var sender = opts.sender;
		
		if(buttonCfgWin==null){
			buttonCfgWin = new Ext.Window({
				title:'自定义按钮配置',
				height:458,
				width:635, 
				buttonAlign: "center",
				closable:true ,
				closeAction:'hide',
				autoScroll:true,
				modal:false,
				layout:'fit',
				resizable :false,
				btnId:'',
				defaultButton:'btnCfgOK',
				items:[{
					xtype:'form',
					id:'frmBtnCfg',
					style:'padding:5px',
					bodyStyle:'padding:5px',
					itemCls:"itemStyle",
					labelAlign:'right',
					labelWidth:80,
					style:'padding:5px',
					layout:'xform',
					items:[{
						xtype:'textfield',
						fieldLabel:'文本',
						width:400,
						name:'text',
						value:''
					},{
						xtype:'textfield',
						fieldLabel:'图标Cls',
						name:'iconCls',
						width:400,
						value:''
					},{
						xtype:'textarea',
						fieldLabel:'客户端脚本',
						width:400,
						height:100,
						name:'js',
						value:''
						
					},{
						xtype:'textarea',
						fieldLabel:'服务端脚本',
						width:400,
						height:118,
						name:'script',
						value:'',
						extra:{
							xtype:"button",
							text:"调试脚本",
							style:"margin-left:1em;display:inline-block;",
							listeners:{
								'click':function(){
									setActiveTab('scriptdebug.jhtml?nodeId='+nodeId__ +'&id1='+ listPage__.formId+'&id2='+ buttonCfgWin.btnId +'&stype=form','formmanage_' + buttonCfgWin.btnId,'调试脚本');
								}
							}						
						}
					},{
						xtype:'checkbox',
						fieldLabel:'其他',
						//hideLabel :true,
						boxLabel: '是否在右键菜单中显示?',
						value:'on',
						name:'isMenuItem'
					}]
				}],
				buttons:[{
					text:'确定',
					id:'btnCfgOK',
					scope:opts,
					handler:function(){
						var frm = Ext.getCmp('frmBtnCfg');
						var values = frm.form.getValues();
						if(this.action =='new'){
							createMyButton(values);
						}else{
							this.sender.dom.value = values.text;
							this.sender.dom.setAttribute('cfg',escape(Ext.encode(values)));
						}
						buttonCfgWin.hide();
					}
				},{
					text:'删除',
					scope:opts,
					handler:function(){
						this.sender.remove();
						buttonCfgWin.hide();
					}
				},{
					text:'取消',
					handler:function(){
						buttonCfgWin.hide();
					}
				}]
			});
		}
		buttonCfgWin.buttons[0].scope =opts;
		buttonCfgWin.buttons[1].scope =opts
		if(sender) buttonCfgWin.setAnimateTarget(sender);
		buttonCfgWin.show();
		
		//setValues
		var cfg = unescape(sender.getAttributeNS('','cfg'));
		if(cfg!='undefined'){
			cfg = Ext.decode(cfg);
			var frm = Ext.getCmp('frmBtnCfg');
			for(var name in cfg){
				var ctrs = frm.find('name',name);
				if(ctrs.length>0){
					ctrs[0].setValue(cfg[name]);
				}
			}
			if(cfg.id){	
				buttonCfgWin.btnId = cfg.id;
				buttonCfgWin.setTitle('自定义按钮配置[<a href="javascript:" id="viewBtnLog">查看日志</a>]');
				var url = '../runtime/getScriptLog.jhtml?id1='+listPage__.formId+'&id2='+ cfg.id +'&nodeId=' + nodeId__;
				new Ext.ToolTip({
					target: 'viewBtnLog',
					autoHide: false,
					closable: true,
					draggable:true,
					trackMouse :true,
					autoHeight :false,
					width: 200,
					height:300,
					autoScroll:true,
					autoLoad: {url: url}
				});
			}
			buttonCfgWin.buttons[1].el.setDisplayed(true);
		}else{
			var frm = Ext.getCmp('frmBtnCfg');
			frm.find('name','text')[0].setValue('');
			frm.find('name','iconCls')[0].setValue('');
			frm.find('name','js')[0].setValue('');
			frm.find('name','script')[0].setValue('');
			frm.find('name','isMenuItem')[0].setValue('on');
			
			buttonCfgWin.setTitle('自定义按钮配置');
			buttonCfgWin.buttons[1].el.setDisplayed(false);
		}
	}
	//查看自定义按钮日志
	function viewCustomBtnLog(btnId){

	}	
	//初始化 自定义按钮配置
	var buttonCfgWin = null;
	var frmBtnCfg=null;
	btnCreateMyBtn = new Ext.Button({
		renderTo :'addBtnBox',
		iconCls :'addField',
		style:'margin-left:5px',
		tooltip :'点击创建自定义按钮',
		handler:function(obj,e){
			showButtonCfgWin({sender:obj.getEl(),action:'new',
			cfg:{
				text:'',
				iconCls:'',
				script:'',
				isMenuItem:'on'
			}});
		}
	});
	
	//选择试图的初始化
	for(var i=0;i<viewConfig__.length;i++){
		selViewId.dom.options.add(new Option(viewConfig__[i].name, viewConfig__[i].viewId));
	}
	selViewId.dom.value = LPCFG.viewId;
	//初始化帮助
	new Ext.ToolTip({
		target:'selViewIdHelp',
		html :'添加和修改按钮对应操作的是某个表单试图，需要在这里指定按钮绑定的表单试图。'
	});
	
	//初始化一些配置值
	(function(){
		txtTitle.dom.value = listPage__.name?listPage__.name:'';
		txtPageCount.dom.value =rngPageCount.dom.value =  LPCFG.pagesize;
		txtFilter.dom.value = LPCFG.filter;
		txtSql.dom.value = LPCFG.sql;
		chkBtnAdd.dom.checked = LPCFG.buttons.add;
		chkBtnModify.dom.checked =LPCFG.buttons.modify;
		chkBtnDel.dom.checked = LPCFG.buttons.delete;
		chkMenuAdd.dom.checked = LPCFG.menus.add;
		chkMenuModify.dom.checked =LPCFG.menus.modify;
		chkMenuDel.dom.checked = LPCFG.menus.delete;
		
		chkSearchDb.dom.checked = LPCFG.enableSearchDb==false?false:true;
		chkSearchSvr.dom.checked = LPCFG.enableSearchSvr==false?false:true;
		selTpl.dom.value = LPCFG.template;
		txtTpl.dom.value = LPCFG.myTemplate;
		txtInjectHead.dom.value = LPCFG.headInject?LPCFG.headInject:'';
		txtInjectBody.dom.value = LPCFG.bodyInject?LPCFG.bodyInject:'';
		chkAutoPagesize.dom.checked = LPCFG.autoPagesize==false?false:true;
		if(LPCFG.myTemplate!='') chkUserMyTpl.dom.checked = true;
		
		for(var i=0;i<LPCFG.buttons.ext.length;i++){
			var btnCfg = LPCFG.buttons.ext[i];
			createMyButton(btnCfg);
		}

	})();
	
	var pageWidth = Ext.getBody().getWidth();
	
	//初始化 列表项配置
	var store_old;
	
	(function(){
	btnDeleteButtonClick = function (){
		var selItems = grid_old.getSelectionModel().selections.items;
		if(selItems.length>0){
			var ids=[];
			for(var i=selItems.length-1;i>=0;i--){
				ids.push(selItems[i].data.f_name);
				store_old.remove(selItems[i]);
			}			
		}
	}
	
	var chkHideColumn = new Ext.grid.CheckColumn({
		header: "隐藏列吗?",
		dataIndex: 'isView',
		width: 75,
		renderer:function(v,p,record){
			p.css += ' x-grid3-check-col-td'; 
			return '<div class="x-grid3-check-col'+(!v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
		}
	});
	var chkShowTipColumn = new Ext.grid.CheckColumn({
		id:'isShowTip',
		header: "显示Tip吗?",
		dataIndex: 'isShowTip',
		width: 75
	});	
	var cm = new Ext.grid.ColumnModel([
		new Ext.grid.RowNumberer(),
	    new Ext.grid.CheckboxSelectionModel(),{
			header: "字段名",
			dataIndex: 'field',
			width: 200,
		    renderer:function(v,p,record,rowIndex,colIndex,data ){						
				var text = v;
				var combo = Ext.getCmp('comboField');
				if(combo.valueField){
					var r = combo.findRecord(combo.valueField, v);
					if(r){
						text = r.data[combo.displayField];
					}else if(combo.valueNotFoundText !== undefined){
						text = combo.valueNotFoundText;
					}
				}
				return text;  //显示显示值
			},			
			editor: new Ext.form.ComboBox({
				id:"comboField",
				triggerAction: 'all',
				editable:false,
				mode: 'local',
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text'],　
				　　data:fields__
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				listClass: 'x-combo-list-small',
				listeners:{
					change:function(obj,v,oldValue){
						var selItems = grid_old.getSelectionModel().selections.items;
						if(selItems.length>0){
							var selIndex = grid_old.getSelectionModel().last;
							var rec = store_old.data.items[selIndex];
							
							var text=v;
							var combo = Ext.getCmp('comboField');
							if(combo.valueField){
								var r = combo.findRecord(combo.valueField, v);
								if(r){
									text = r.data[combo.displayField];
								}else if(combo.valueNotFoundText !== undefined){
									text = combo.valueNotFoundText;
								}
							}
							if(v!=''){
								text = text.replace(v,'').replace('(','').replace(')','');
								rec.data.title = text;
								rec.data.tpl = '{'+ v +'}';
							}
						}
					}
				}
			})
        },{
			id:'title',
			header: "列名",
			dataIndex: 'title',
			width: 150,
			editor: new Ext.form.TextField({
               allowBlank: true
			})
        },{
			id:'tpl',
			header: "列模板",
			dataIndex: 'tpl',
			width: 200,
			editor: new Ext.ux.CustomEditors.TextArea({}),
			renderer:function(v,p,record){
				//return '<textarea style="height:40px;scroll:none;">'+ v +'</textarea>';
				if(v) return "点击编辑";
				else return "";
			}
        },{
			id:'width',
			header: "列宽",
			dataIndex: 'width',
			width: 60,
			editor: new Ext.form.NumberField({})
        },
		chkShowTipColumn,{
			id:'tipTpl',
			header: "Tip模板",
			dataIndex: 'tipTpl',
			width: 150,
			editor:  new Ext.ux.CustomEditors.TextArea({}),
			renderer:function(v,p,record){
				if(v) return "点击编辑";
				else return "";
			}
		},
		chkHideColumn,{
			header: "删除",
			width: 30,
			dataIndex:'id',
			renderer:function(v,p,record){
				return '<div class="deleteButton" onclick="btnDeleteButtonClick()"></div>'; 
			}
		}
		
    ]);
    cm.allowSort = true;
	
    // create the Data Store
    store_old= new Ext.data.Store({
        proxy: new Ext.data.MemoryProxy(LPCFG), 
        reader : new Ext.data.JsonReader({
			autoLoad:true,
			root : "columns",
			fields: ["title","tpl","field","isView","width","isShowTip","tipTpl"]
		}),
		listeners: { 
			load : function(t, records,options) {
				for(var i=0;i<records.length;i++){
					if(records[i].get('tipTpl'))
						records[i].set('tipTpl',myUnZhuanyi(records[i].get('tipTpl')));
				}
			}
		}
    });

    // create the editor grid_old
    var grid_old = new Ext.grid.EditorGridPanel({
        store: store_old,
		frame:false,
		hideBorders:true,
		enableColumnHide:false,
		stripeRows :true,//表格行颜色间隔显示
        cm: cm,
		sm: new Ext.grid.CheckboxSelectionModel(),
        //renderTo: 'colunmCfgContainer',
        //width:pageWidth*0.95-10,
        //height:500,
		autoHeight:true,
        autoExpandColumn:'tpl',
		enableHdMenu: false,
		dropConfig: {appendOnly:false},
		trackMouseOver : true,
		enableDragDrop: true,	
		ddGroup: "GridDD",	
        clicksToEdit:1,
		plugins:[chkShowTipColumn,chkHideColumn],
        tbar: [{
            text: '添加列',
			iconCls:"addField",
            handler : function(){
				var p = new fieldColumn({
                    title: '',
					tpl:'',
					field:'',
					tipTpl:'',
					isView:true
					
                });
                grid_old.stopEditing();
				var len = store_old.getCount();
                store_old.insert(len, p);
                grid_old.startEditing(len, 1);
            }
        },{
            text: '删除列',
			disable:true,
			iconCls:"delField",
            handler : function(){
				btnDeleteButtonClick();
			}
        },{
			xtype:'tbfill'
		},{
			xtype:'label',
			text:'小提示:拖拽行可实现顺序调换'
		}],
		listeners:{
			render:function(grid_old){
				//拖拽排序
				var ddrow = new Ext.dd.DropTarget(grid_old.container, { 
					ddGroup : 'GridDD', 
					copy : false, 
					notifyDrop : function(dd, e, data) { 
						//var rows = data.selections;
						var sm = grid_old.getSelectionModel(); 
						var rows = sm.getSelections(); 
						var store = grid_old.getStore();
						var cindex = dd.getDragData(e).rowIndex; 
						if (cindex == undefined || cindex < 0){ 
							e.cancel=true; 
							return; 
						} 
						for (i = 0; i < rows.length; i++) { 
							var rowData = rows[i]; 
							if (!this.copy) { 
								store.remove(rowData); 
								store.insert(cindex, rowData); 
								grid_old.getView().refresh();
							} 
						}
					} 
				});
			}
		}

		
    });
	
    // trigger the data store load
    store_old.load();

	var fieldColumn = Ext.data.Record.create([
	   {name: 'title', type: 'string'},
	   {name: 'tpl', type: 'string'},
	   {name: 'field', type: 'string'},  
	   {name: 'width', type: 'int'},  
	   {name: 'tipTpl', type: 'string'},  
	   {name: 'isView', type: 'bool'}
	]);	
	
	//初始化需要的字段配置
	//listPage__.fields
	var ds_fields = [['id','id']];
	for(var i=0;i<listPage__.fields.length;i++){
		ds_fields.push([listPage__.fields[i][0],listPage__.fields[i][0]]);
	}
	var panel_needFields = new Ext.Panel({
		title:'请选择上面列模板和Tip模板使用到的字段',
		autoHeight:true,
		items:[{
			xtype:'checkboxgroup',
			id:'chkgp_needFields',
			width: 300,
			dataSource: ds_fields,
			value: LPCFG.mustReturnFields?LPCFG.mustReturnFields:null
		}]
	});
	
	
	//初始化 列表项配置结束
	
	
	//初始化 搜索配置开始

		var searchFiledsCfg = [{
			text         : '未设置字段',
			value        : '',
			checked      : false
		}];	
		var menuGroupIndex=0;
		function cfgSearchFileds(){
			for(var fname in searchFileds__){		
				searchFiledsCfg.push({
					text         : searchFileds__[fname].f_title,
					value        : fname,
					checked      : false
				});
			}
		}
		cfgSearchFileds();

		function createMBtnField(cfg){
			
			group='filter' + menuGroupIndex++;
			var btn = new Ext.Toolbar.MenuButton({
				text     : '未选择字段',
				tooltip  : "未选择字段",
				menu     : [],
				style:'margin:3px 0 3px 3px',
				minWidth : 120
			});
			for(var i=0;i<searchFiledsCfg.length;i++){
				searchFiledsCfg[i].checkHandler=function(item, checked){
					if(checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				};
				if(cfg) searchFiledsCfg[i].checked=cfg.field==searchFiledsCfg[i].value;
				searchFiledsCfg[i].group=group;
				btn.menu.add(searchFiledsCfg[i]);
			}
			
			return btn;
		}
		mBtnField_1 = createMBtnField(LPCFG.search[1-1]);
		mBtnField_2 = createMBtnField(LPCFG.search[2-1]);
		mBtnField_3 = createMBtnField(LPCFG.search[3-1]);
		mBtnField_svr_1 = createMBtnField(LPCFG.searchSvr[1-1]);
		mBtnField_svr_2 = createMBtnField(LPCFG.searchSvr[2-1]);
		mBtnField_svr_3 = createMBtnField(LPCFG.searchSvr[3-1]);		
		
		function CreateMBtnAndOr(index){
			var sItem = LPCFG.search[index-1];
			if(!sItem) sItem={};
			var group ='filter' + menuGroupIndex++;
			var btn = new Ext.Toolbar.MenuButton({
				text     : '并且',
				value 	 :'and',
				menu     : [{
					text         : '并且',
					value        : 'and',
					checked      : sItem.andor=='and',
					group        : group,
					checkHandler :function(item, checked){
						if(checked){
							btn.setText(item.text);
							btn.value = item.value;
						}
					}
				},{
					text         : '或者',
					value        : 'or',
					checked      : sItem.andor=='or',
					group        : group,
					checkHandler :function(item, checked){
						if(checked){
							btn.setText(item.text);
							btn.value = item.value;
						}
					}
				}],
				minWidth : 50
			});
			return btn;
		}
		
		mBtnAndOr_1 = CreateMBtnAndOr(1);
		mBtnAndOr_2 = CreateMBtnAndOr(2);
		mBtnAndOr_3 = CreateMBtnAndOr(3);
		
		function CreatechkHideItem(){
			return new Ext.form.Checkbox({
				style:'margin:3px',
				boxLabel:'是否隐藏?'
			});
		}
		chkHideItem_1 = CreatechkHideItem();
		chkHideItem_2 = CreatechkHideItem();
		chkHideItem_3 = CreatechkHideItem();
		chkHideItem_svr_1 = CreatechkHideItem();
		chkHideItem_svr_2 = CreatechkHideItem();
		chkHideItem_svr_3 = CreatechkHideItem();	
		
		function CreateMBtnOp(index){
			var sItem = LPCFG.search[index-1];
			if(!sItem) sItem={};
			var group ='filter' + menuGroupIndex++;
			var btn = new Ext.Toolbar.MenuButton({
				text     : '包含(like)',
				value 	 :'like',
				tooltip  : "选择匹配方式",
				menu     : [{
					text         : '等于(=)',
					value        : '=',
					checked      : sItem.op=='=',
					group        : group,
					checkHandler :function(item, checked){
						if(checked){
							btn.setText(item.text);
							btn.value = item.value;
						}
					}
				},{
					text         : '包含(like)',
					value        : 'like',
					checked      : sItem.op=='like',
					group        : group,
					checkHandler :function(item, checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				},{
					text         : '大于(>)',
					value        : '>',
					checked      : sItem.op=='>',
					group        : group,
					checkHandler :function(item, checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				},{
					text         : '大于或等于(>=)',
					value        : '>=',
					checked      : sItem.op=='>=',
					group        : group,
					checkHandler :function(item, checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				},{
					text         : '小于(<)',
					value        : '<',
					checked      : false,
					group        : group,
					checkHandler :function(item, checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				},{
					text         : '小于或等于(<=)',
					value        : '<=',
					checked      : sItem.op=='<=',
					group        : group,
					checkHandler :function(item, checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				},{
					text         : '不等于(<>)',
					value        : '<>',
					checked      : sItem.op=='<>',
					group        : group,
					checkHandler :function(item, checked){
						btn.setText(item.text);
						btn.value = item.value;
					}
				}],
				minWidth : 120,
				handler:function(){
					
				}
			});
			return btn;
		}
		mBtnOp_1 = CreateMBtnOp(1);
		mBtnOp_2 = CreateMBtnOp(2);
		mBtnOp_3 = CreateMBtnOp(3);
		
		
		function CreateMBtnSearchValue(cfg){
			if(!cfg) cfg={};
			var group ='filter' + menuGroupIndex++;
			var mBtnSearchValue = new Ext.Toolbar.MenuButton({
				text     : '文本框',
				dataSource:cfg.dataSource?cfg.dataSource:"",
				value:'textfield',
				tooltip  : "选择关键字的控件类型",
				menu     : [{
					text         : '文本框',
					value        : 'textfield',
					checked      : cfg.value=='textfield',
					group        : group,
					checkHandler :function(item, checked){
						if(checked){
							mBtnSearchValue.setText(item.text);
							mBtnSearchValue.value =item.value;
						}
					}
				},{
					text         : '下拉框',
					value        : 'combo',
					checked      : cfg.value=='combo',
					group        : group,
					checkHandler :function(item, checked){
						if(checked){
							mBtnSearchValue.setText(item.text);
							mBtnSearchValue.value =item.value;
						}
					}
				},{
					text         : '日期',
					value        : 'datefield',
					checked      : cfg.value=='datefield',
					group        : group,
					checkHandler :function(item, checked){
						if(checked){
							mBtnSearchValue.setText(item.text);
							mBtnSearchValue.value =item.value;
						}
					}
				}],
				minWidth : 120,
				handler:function(){
					if(this.text=='下拉框'){
						//var dataStr = unescape(mBtnSearchValue.el.getAttributeNS('','dataSource'));
						var dataStr  = mBtnSearchValue.dataSource;
						if(dataStr=='undefined')dataStr='';
						Ext.Msg.show({
							title:'请配置数据源',
							msg:'eg.[["china","中国"],["usa","美国"]]',
							animEl:this.el,
							buttons:{
								ok:'确定',
							    cancel:'取消'
						    },
							closable:true,
							value:dataStr,
							multiline:true,
							prompt:true,
							fn:function(ret, text){
								if (ret == 'ok'){
									//mBtnSearchValue.el.dom.setAttribute('dataSource',escape(text));
									mBtnSearchValue.dataSource = text;
								}
							}
						});
						
					}
				}
			});
			return mBtnSearchValue;
		}
		mBtnSearchValue_1 = CreateMBtnSearchValue(LPCFG.search[1-1]);
		mBtnSearchValue_2 = CreateMBtnSearchValue(LPCFG.search[2-1]);
		mBtnSearchValue_3 = CreateMBtnSearchValue(LPCFG.search[3-1]);
		mBtnSearchValue_svr_1 = CreateMBtnSearchValue(LPCFG.searchSvr[1-1]);
		mBtnSearchValue_svr_2 = CreateMBtnSearchValue(LPCFG.searchSvr[2-1]);
		mBtnSearchValue_svr_3 = CreateMBtnSearchValue(LPCFG.searchSvr[3-1]);		
	////////初始化 搜索配置结束	///////////////////
		
		//保存////////////////////////////////
		//解析得到列模板{title}中的title,搜集需要返回的字段名
		function parseMustReturnField(str){
			var mz=null, res=[], regex=/\{([^}]*)\}/g;
			while (mz=regex.exec(str)) {
				if(mz[1].indexOf('query_')==-1){
					res.push(mz[1]);
				}
			}
			return res;
		}
		function btnSaveClick(){
			//输入验证
			var customSearchCfg ;
			if(txtSql.dom.value!=''){
				try{
					var scfg = txtCustomSearchCfg.getValue();
					if(scfg!="") customSearchCfg = Ext.decode(scfg);
				}catch(ex){
					Ext.Msg.show({
					   title:'错误提示',
					   msg: '自定义sql数据源的搜索配置格式有误',
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});
					return;
				}
			}
		
			var listCfg={
				viewId:selViewId.dom.value,
				filter:txtFilter.dom.value,
				sql:txtSql.dom.value,
				template:selTpl.dom.value,
				pagesize:txtPageCount.dom.value,
				autoPagesize:chkAutoPagesize.dom.checked,
				headInject:txtInjectHead.dom.value,
				bodyInject:txtInjectBody.dom.value,				
				myTemplate:chkUserMyTpl.dom.checked?txtTpl.dom.value:'',
				customSearchHTML:txtSearchHTMLCfg.getValue(),
				buttons:{
					"add":chkBtnAdd.dom.checked,
					"delete":chkBtnDel.dom.checked,
					"modify":chkBtnModify.dom.checked,
					"ext":[]
				},
				menus:{
					"add":chkMenuAdd.dom.checked,
					"delete":chkMenuDel.dom.checked,
					"modify":chkMenuModify.dom.checked
				},
				columns:[],
				enableSearchDb:chkSearchDb.dom.checked,
				enableSearchSvr:chkSearchSvr.dom.checked,
				search:[],
				customSearch:'',
				searchSvr:[]
			};
			
			listCfg.customSearch = txtCustomSearchCfg.getValue();

			var btns = buttonsBox.select('input');
		
			btns.each(function(el, obj, index){
				var btnCfg = Ext.decode(unescape(el.getAttributeNS('','cfg')));
				btnCfg.id= listPage__.formId + '_' + listPage__.listId + '_' + index;
				listCfg.buttons.ext.push(btnCfg);
			});
			//var mustReturnFieldsJson = {'id':1};//通过解析模板内容得到搜索结果需要返回的字段
			var mustReturnFieldsArr=Ext.getCmp('chkgp_needFields').getValue();
			if(!Ext.isArray(mustReturnFieldsArr)){
				mustReturnFieldsArr = mustReturnFieldsArr.split(',');
			}
			for(var i=0;i<store_old.data.items.length;i++){
				var item = store_old.data.items[i].data;
				if(item.title){
					/*
					var mustFlds = parseMustReturnField(item.tpl + item.tipTpl);
					for(var j=0;j<mustFlds.length;j++){
						mustReturnFieldsJson[mustFlds[j]] = 1;
					}
					*/
					item.tipTpl = myZhuanyi(item.tipTpl);
					listCfg.columns.push(item);					
				}
			}
			/*
			for(var fld in mustReturnFieldsJson){
				mustReturnFieldsArr.push(fld);
			}
			*/
			listCfg.mustReturnFields = mustReturnFieldsArr;

			var searchItem1 = {
				field:mBtnField_1.value?mBtnField_1.value:'',
				op:mBtnOp_1.value,
				value:mBtnSearchValue_1.value,
				andor:mBtnAndOr_1.value,
				isHide:chkHideItem_1.getValue(),
				dataSource:mBtnSearchValue_1.dataSource
			};
			listCfg.search.push(searchItem1);

			var searchItem2 = {
				field:mBtnField_2.value?mBtnField_2.value:'',
				op:mBtnOp_2.value,
				value:mBtnSearchValue_2.value,
				andor:mBtnAndOr_2.value,
				isHide:chkHideItem_2.getValue(),
				dataSource:mBtnSearchValue_2.dataSource
			};
			listCfg.search.push(searchItem2);

			var searchItem3 = {
				field:mBtnField_3.value?mBtnField_3.value:'',
				op:mBtnOp_3.value,
				value:mBtnSearchValue_3.value,
				isHide:chkHideItem_3.getValue(),
				andor:mBtnAndOr_3.value,
				dataSource:mBtnSearchValue_3.dataSource
			};
			listCfg.search.push(searchItem3);
			
			//搜集从service搜索的配置
			var searchItemSvr1 = {
				field:mBtnField_svr_1.value?mBtnField_svr_1.value:'',
				value:mBtnSearchValue_svr_1.value,
				isHide:chkHideItem_svr_1.getValue(),
				dataSource:mBtnSearchValue_svr_1.dataSource
			};
			listCfg.searchSvr.push(searchItemSvr1);
			var searchItemSvr2 = {
				field:mBtnField_svr_2.value?mBtnField_svr_2.value:'',
				value:mBtnSearchValue_svr_2.value,
				isHide:chkHideItem_svr_2.getValue(),
				dataSource:mBtnSearchValue_svr_2.dataSource
			};
			listCfg.searchSvr.push(searchItemSvr2);
			var searchItemSvr3 = {
				field:mBtnField_svr_3.value?mBtnField_svr_3.value:'',
				value:mBtnSearchValue_svr_3.value,
				isHide:chkHideItem_svr_3.getValue(),
				dataSource:mBtnSearchValue_svr_3.dataSource
			};
			listCfg.searchSvr.push(searchItemSvr3);			
			//提交
			Ext.getBody().mask("正在提交,请稍候...");
			Ext.Ajax.request({  
				url: 'listConfig!saveListConfig.jhtml',
				params:{
					formId:listPage__.formId,
					listId:listPage__.listId,
					nodeId:nodeId__,
					name:txtTitle.dom.value,
					config:Ext.encode(listCfg)
				},  
				method:"POST",  
				success:function(response,opts){
					Ext.getBody().unmask();
					var ret = Ext.util.JSON.decode(response.responseText);
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
						Ext.Toast.show('提交成功',{
							title:'提示',
							buttons: Ext.Msg.OK,
							animEl: 'elId',
							icon: Ext.MessageBox.INFO,  
							time:1000,
							minWidth:420
						});
					}
				},
				failure:function(response,opts){
					Ext.getBody().unmask();
					Ext.Msg.show({
						   title:'错误提示',
						   msg: response.statusText,
						   buttons: Ext.Msg.OK,
						   animEl: 'elId',
						   minWidth:420,
						   icon: Ext.MessageBox.ERROR 
					});

				}		
			});	
		}
		
		function btnCloseClick(){
			if(top && top.centerTabPanel){
				top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
			}else{
				window.open('','_parent','');
				window.close();
			}
		}
		//保存结束///////////////////////////
	
		//////////////初始化主界面/////////////////////
		new Ext.Viewport({
			layout:"fit",
			border:false,
			frame:true,
			items:[{
				xtype:'panel',
				//region:'center',
				frame:false,
				border:false,
				autoScroll:true,
				layout:'fit',
				buttonAlign:'center',
				items:[{
					xtype:'tabpanel',
					layoutOnTabChange:true,
					activeTab:0,
					//autoHeight:true,
					titleCollapse :true,
					autoScroll:true,
					items:[{
						title:'基础配置',
						autoScroll:true,
						contentEl:'commonCfgBox'
					},{
						title:'列表项配置',
						autoScroll:true,
						items:[grid_old,panel_needFields]
					},{
						title:'默认搜索配置',
						items:[{
							xtype:'tabpanel',
							layoutOnTabChange:true,
							activeTab:0,
							id:'tabpanelSearch',
							style:'padding:1px 5px 0px 5px;',
							//width:pageWidth*0.95,
							titleCollapse :true,
							items:[{
								xtype:"panel",
								title:'从数据库搜索',
								layout:'table',
								layoutConfig: {   
									columns: 5   
								}, 
								items:[
									mBtnField_1,mBtnOp_1,mBtnSearchValue_1,mBtnAndOr_1,chkHideItem_1,
									mBtnField_2,mBtnOp_2,mBtnSearchValue_2,mBtnAndOr_2,chkHideItem_2,
									mBtnField_3,mBtnOp_3,mBtnSearchValue_3,mBtnAndOr_3,chkHideItem_3
								]
							},{
								xtype:"panel",
								title:'从搜索服务搜索',
								layout:'table',
								layoutConfig: {   
									columns: 3   
								}, 
								items:[
									mBtnField_svr_1,mBtnSearchValue_svr_1,chkHideItem_svr_1,
									mBtnField_svr_2,mBtnSearchValue_svr_2,chkHideItem_svr_2,
									mBtnField_svr_3,mBtnSearchValue_svr_3,chkHideItem_svr_3
								]
								
							},{
								xtype:"panel",
								title:"自定义sql数据源情况下的搜索配置",
								layout:'column',
								items:[{
									xtype:'panel',
									title:'请参考右侧代码示例正确填写配置信息==>',
									columnWidth:.6,
									height:300,
									autoScroll:true,
									layout:'fit',
									items:[{
										xtype:'textarea',
										id:'txtCustomSearchCfg',
										emptyText :'请输入json格式的搜索配置',
									}]
								},{
									xtype:'panel',
									title:'示例',
									columnWidth:.4,
									height:300,
									autoScroll:true,
									layout:'fit',
									items:[{
										xtype:'textarea',
										title:'代码样例',
										id:'txtCustomSearchSample1',
										
										readOnly:true,
										value:''
									}]
								}]
							},{
								xtype:"panel",
								title:"自定义搜索HTML",
								layout:'column',
								items:[{
									xtype:'panel',
									title:'请参考右侧实例代码==>',
									columnWidth:.6,
									height:300,
									autoScroll:true,
									layout:'fit',
									items:[{
										xtype:'textarea',
										id:'txtSearchHTMLCfg'
									}]
								},{
									xtype:'panel',
									title:'示例',
									columnWidth:.4,
									height:300,
									autoScroll:true,
									layout:'fit',
									items:[{
										xtype:'textarea',
										title:'HTML样例',
										id:'txtSearchHTMLSample1',
										readOnly:true,
										value:''
									}]
								}]
							}]
							
						}]
					}]
				}],
				buttons:[{
					text:'保存(S)',
					tooltip :'快捷键(Ctrl+S)',
					handler:btnSaveClick
				},{
					text:'关闭',
					handler:btnCloseClick
				}]
			}]
		});
		tabpanelSearch = Ext.getCmp('tabpanelSearch');
		//初始化主界面结束
				
		//初始化原始值
		txtCustomSearchCfg = Ext.getCmp('txtCustomSearchCfg');
		Ext.getCmp('txtCustomSearchSample1').setValue(Ext.get('txtCustomSearchSample').dom.defaultValue);
		if(LPCFG.customSearch) txtCustomSearchCfg.setValue(LPCFG.customSearch);
		txtSearchHTMLCfg = Ext.getCmp('txtSearchHTMLCfg');
		Ext.getCmp('txtSearchHTMLSample1').setValue(Ext.get('txtSearchHTMLSample').dom.defaultValue);
		if(LPCFG.customSearchHTML)txtSearchHTMLCfg.setValue(LPCFG.customSearchHTML);
		
		for(var i=1;i<=LPCFG.search.length;i++){

			eval("var btnTemp = mBtnField_" + i);
			var sItem = LPCFG.search[i-1];
			if(sItem.field && btnTemp) {
				btnTemp.setText(searchFileds__[sItem.field].f_title);
				btnTemp.value =sItem.field;
			}
			eval("var btnTemp = mBtnOp_" + i);
			if(sItem.op && btnTemp) {
				btnTemp.setText(opEnZh__[sItem.op]);
				btnTemp.value =sItem.op;
			}
			eval("var btnTemp = mBtnSearchValue_" + i);
			if(sItem.value && btnTemp) {
				btnTemp.setText(controlTypeEnZh__[sItem.value]);
				btnTemp.value =sItem.value;
			}
			if(sItem.dataSource) btnTemp.dataSource = sItem.dataSource;	
			eval("var btnTemp = mBtnAndOr_" + i);
			if(sItem.andor && btnTemp) {
				btnTemp.setText(andorEnZH__[sItem.andor]);
				btnTemp.value =sItem.andor;		
			}
			eval("var btnTemp = chkHideItem_" + i);
			if(sItem.isHide && btnTemp) {
				btnTemp.setValue(sItem.isHide);		
			}			
		}
		for(var i=1;i<=LPCFG.searchSvr.length;i++){

			eval("var btnTemp = mBtnField_svr_" + i);
			var sItem = LPCFG.searchSvr[i-1];
			if(sItem.field && btnTemp) {
				btnTemp.setText(searchFileds__[sItem.field].f_title);
				btnTemp.value =sItem.field;
			}

			eval("var btnTemp = mBtnSearchValue_svr_" + i);
			if(sItem.value && btnTemp) {
				btnTemp.setText(controlTypeEnZh__[sItem.value]);
				btnTemp.value =sItem.value;
			}
			if(sItem.dataSource) btnTemp.dataSource = sItem.dataSource;	

			eval("var btnTemp = chkHideItem_svr_" + i);
			if(sItem.isHide && btnTemp) {
				btnTemp.setValue(sItem.isHide);		
			}			
		}
		
		tabpanelSearch.setActiveTab(selTpl.dom.selectedIndex==1?3:0);
		tabpanelSearch.items.items[0].setDisabled(selTpl.dom.selectedIndex==1);
		tabpanelSearch.items.items[1].setDisabled(selTpl.dom.selectedIndex==1);
		tabpanelSearch.items.items[2].setDisabled(selTpl.dom.selectedIndex==1);
		tabpanelSearch.items.items[3].setDisabled(!selTpl.dom.selectedIndex==1);
			
		//快捷键保存
		var docMap = new Ext.KeyMap(Ext.getDoc(), {
			key: 's',
			shift:false,
			ctrl:true,
			handler: function(key,e){
				e.preventDefault();
				btnSaveClick();
			}
		});	
		
		
	})();

	
});	
</script>


</body>
</html>