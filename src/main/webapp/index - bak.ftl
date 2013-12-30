<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="./res/img/favicon.ico" />
<title>CMPP_节点管理</title>
	<style type="text/css">
	/*清除文档类型声明中的默认属性*/
	body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,p,blockquote,th,td{margin:0;padding:0;}
	table{border-collapse:collapse;border-spacing:0;} 
	fieldset,img{border:0;}
	address,caption,cite,code,dfn,em,strong,th,var{font-style:normal;font-weight:normal;}
	ol,ul {list-style:none;}
	caption,th {text-align:left;}
	h1,h2,h3,h4,h5,h6{font-size:100%;}
	q:before,q:after{content:'';}
	/*字体定义*/
	body {font-size:12px; font-family: simsun,arial,helvetica,clean,sans-serif; background:#fff;}
	table {font-size:inherit;font:100%;} 
	html{height:100%;}
	body {font-size:12px;height:100%;}
	#placeholder{width:100%;height:100%;}
	
	.starUl{ width:100%; padding:10px 0 0; clear:both; float:left;}
	.starUl li{ width:191px; float:left; margin:0 15px 10px 17px; display:inline; padding:0 0 15px;}
	.starUl .pic{ width:160px; height:120px; border:1px solid #ccc; padding:7px 15px 8px 14px; display:block;}
	.starUl .pic img{width:160px; height:120px; overflow:hidden;}
	.starUl p{ width:190px; display:block; line-height:20px; height:20px; overflow:hidden;}
	.us_gray,.us_gray a{ color:#3A3A3A;}
	.line{ height:1px; background:#DEDBDB; width:191px; float:left;margin-top:5px}
	
	.item-tittle {
		background: none repeat scroll 0 0 #6F6F6A;
		border-bottom: 1px solid #86867F;
		color: #FFFFFF;
		font: 16px/24px "微软雅黑";
		height: 24px;
		text-indent: 16px;
		width: 191px;
	}
	
	.control-link {float:right}
	.control-link a{padding:0 5px 0 5px}
	
	.add{background:url("./res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
	</style>
	<link rel="stylesheet" type="text/css" href="./res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./res/js/ext2/resources/css/patch.css" />
 	<script type="text/javascript" src="./res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./res/js/ext2/ext-all-debug.js"></script>	
	<script type="text/javascript" src="./res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./res/js/ext_base_extension.js"></script>
	<script type="text/javascript" src="./res/js/ext_vtypes.js"></script>
	<!--
    <script type="text/javascript" src="./res/js/portal/Portal.js"></script>
    <script type="text/javascript" src="./res/js/portal/PortalColumn.js"></script>
    <script type="text/javascript" src="./res/js/portal/Portlet.js"></script>
    <link rel="stylesheet" type="text/css" href="./res/js/portal/portal.css" />
	-->
</head>
<body>

<script>
var bindFunc = function(obj,func){
	return function(){
		func.apply(obj,arguments);
	};
};
var addEventHandler=function(obj, type, func) {
		if(!obj){return;}
		var doOn=function(o){
			if(o.addEventListener){o.addEventListener(type, func, false);}
			else if(o.attachEvent){o.attachEvent("on" + type, func);}
			else{o["on" + type] = func;}
		}
		var IsArray=function(v){
			try{
				var a = v[0];
				return typeof(a) != "undefined";
			}catch(e){
				return false;
			}
		}
		if(obj.tagName!='SELECT'&&IsArray(obj)){
			for(var i=0,oLen=obj.length;i<oLen;i++){
				doOn(obj[i],type.func);
			}
		}else{
			doOn(obj);
		}
};
</script>

<!-- 列表编辑器定义 begin-->
<script type="text/javascript">
    var imgPath = "./res/js/ext2/resources"; 
	var fm=Ext.form;
    
    var gridView_1 = Ext.extend(Ext.grid.GridView, {
        appendNewRow: function () {
            var cs = this.getColumnData();
            delete cs[2].style;

            var keyId = "id" + (new Date()).valueOf();
            var k = "<div id='" + keyId + "'></div>";
            var valId = keyId + "1";
            var val = "<div id='" + valId + "'></div>";
            var desId = valId + "1";
            var des = "<div id='" + desId + "'></div>";

            var btnId = desId + "1";
            var btn = "<div style='height:20px;background:url(" + imgPath + "/images/default/tree/drop-yes.gif) no-repeat center;cursor:pointer;' id='" + btnId + "'></div>";

            var rs = [new Ext.data.Record({ key: k, value: val,desc:des, EMPTY: btn }, k)];
            var ds = this.grid.store;

            var rowHTML = this.doRender(cs, rs, ds, 0, 4);
            rowHTML = rowHTML.replace("x-grid3-row", "");
            Ext.DomHelper.append(this.mainBody, rowHTML);

            var keyField = new Ext.form.TextField({ "selectOnFocus": true, "emptyText": "变量名(仅支持英文字母)", "renderTo": keyId ,"allowBlank": false});
            var valField =new Ext.form.TextField({ "selectOnFocus": true, "renderTo": valId});
            var desField = new Ext.form.TextField({ "selectOnFocus": true, "emptyText": "说明", "renderTo": desId,style:"width:98%" });
            var btnField = Ext.get(btnId);
            var g = this.grid;
            btnField.on("click", function () { g.addItem([keyField, valField,desField],['key','value','desc']); });

        }
        , layout: function () {
            this.appendNewRow();
            gridView_1.superclass.layout.call(this);
        }
        , init: function (grid) {
            gridView_1.superclass.init.call(this, grid);
            var html = this.templates.row.html;
            this.templates.row.html = html.replace('cellpadding="0" style="{tstyle}', 'cellpadding="0" style="table-layout:fixed;{tstyle}');
            this.templates.row.compile();
        }

    });

    var gridView_2 = Ext.extend(Ext.grid.GridView, {
        appendNewRow: function () {
            var cs = this.getColumnData();
            delete cs[1].style;

            var keyId = "id" + (new Date()).valueOf();
            var k = "<div id='" + keyId + "'></div>";
            var valId = keyId + "1";
            var val = "<div id='" + valId + "'></div>";
           
            var btnId = valId + "1";
            var btn = "<div style='height:20px;background:url(" + imgPath + "/images/default/tree/drop-yes.gif) no-repeat center;cursor:pointer;' id='" + btnId + "'></div>";

            var rs = [new Ext.data.Record({ ip: k, port: val, EMPTY: btn }, k)];
            var ds = this.grid.store;

            var rowHTML = this.doRender(cs, rs, ds, 0, 3);
            rowHTML = rowHTML.replace("x-grid3-row", "");
            Ext.DomHelper.append(this.mainBody, rowHTML);

            var ipField = new Ext.form.TextField({ "selectOnFocus": true, vtype:"ip","renderTo": keyId });
            var portField =new Ext.form.TextField({ "selectOnFocus": true,vtype:"port", "renderTo": valId});
            var btnField = Ext.get(btnId);
            var g = this.grid;
            btnField.on("click", function () { g.addItem([ipField, portField],['ip','port'])});

        }
        , layout: function () {
            this.appendNewRow();
            gridView_2.superclass.layout.call(this);
        }
        , init: function (grid) {
            gridView_2.superclass.init.call(this, grid);
            var html = this.templates.row.html;
            this.templates.row.html = html.replace('cellpadding="0" style="{tstyle}', 'cellpadding="0" style="table-layout:fixed;{tstyle}');
            this.templates.row.compile();
        }

    });
	

var ListField=Ext.extend(Ext.form.Field,{
	fields:[],
	cm:null,
	gridView:null,
	onRender:function(ctnr,pos){
		ListField.superclass.onRender.call(this,ctnr,pos);
		
		var store=new Ext.data.JsonStore({
	        data:this.getData(), 
			autoLoad:true,
			root : "data"
			,fields: this.fields
		});
		var field=this;
		this.list=new Ext.grid.EditorGridPanel({
			store:store
			,cm:field.cm
			,view:field.gridView
			,renderTo:ctnr
			,clicksToEdit:1
			,width:field.width
			,listeners:{
				afteredit:function(){
					this.afterEdit();
				}
			}
			,afterEdit:function(){
			       var datar = new Array();     
			       var jsonDataEncode = "";  
			       var records = store.getRange();   
			       for (var i = 0; i < records.length; i++) {      
			            datar.push(records[i].data);      
			       } 
					field.setValue(datar);
			}
            , addItem: function (fieldCtrlArr,fields) {
				var propRecord={};
				for(var i=0;i<fieldCtrlArr.length;i++){
					var fld = fieldCtrlArr[i];
					if (!fld.validate()) return;
					propRecord[fields[i]] = fld.getValue();
				}				
                var ds = this.store;
                ds.add(new Ext.grid.PropertyRecord(propRecord));
                this.refreshView();
            }
            , deleteItem: function (rowIndex) {
                var v = this.view;
                var ds = this.store;
                if (ds.getCount() < 1) {
                    return;
                }
                ds.data.removeAt(rowIndex);
                v.removeRows(rowIndex, rowIndex);
                this.refreshView();
            }
            , refreshView: function () {
                var v = this.view;
                v.scroller.dom.style.height = "auto";
                v.scroller.dom.style.width = "auto";
                v.el.dom.style.height = "auto";
                v.refresh();
				this.afterEdit();
            }
            , afterRender: function () {
                this.selModel.on('beforecellselect', function (sm, rowIndex, colIndex) {
                    if (colIndex === this.store.fields.length-1) {
                        this.deleteItem(rowIndex);
                        return false;
                    }
                }, this);
                Ext.grid.EditorGridPanel.superclass.afterRender.apply(this, arguments);
                if (this.source) {
                    this.setSource(this.source);
                }
            }
            , processEvent: function (name, e) {
                this.fireEvent(name, e);
                var t = e.getTarget();
                var v = this.view;
                var header = v.findHeaderIndex(t);
                if (header !== false) {
                    this.fireEvent("header" + name, this, header, e);
                } else {
                    var row = v.findRowIndex(t);
                    if (row == undefined)//for row to add item;
                        return;
                    var cell = v.findCellIndex(t);
                    if (row !== false) {
                        this.fireEvent("row" + name, this, row, e);
                        if (cell !== false) {
                            this.fireEvent("cell" + name, this, row, cell, e);
                        }
                    }
                }
            }			
		});
	}
	,list:null
	,recordType:[]
	,setValue:function(v){
		v=Ext.encode(v);
		ListField.superclass.setValue.call(this,v);
	}
	,getData:function(){
		var val=this.getValue();
		try{
			var data=Ext.decode(val);
			return {data:data};
		}catch(ex){
			return {data:{}};
		}
		
	}
	
});
Ext.reg("listfield",ListField);

</script>
<!-- 列表编辑器定义 end-->

<!-- 节点列表、节点查看修改、节点查询、节点列表初始化 begin-->
<script type="text/javascript">

	var nodeMgr = {
		container:null,
		nodeInfoData:[],
		init:function(dataList){
			nodeMgr.container = nodeMgr.$('nodeListBox');
			Ext.getBody().mask("正在获取数据,请稍候......");
			Ext.Ajax.request({  
				url:'develop/node.jhtml',  //获取节点列表
				method:"GET",  
				success:function(response,opts){
					Ext.getBody().unmask();
					var res = Ext.util.JSON.decode(response.responseText);
					if(res.success == false){
						Ext.Msg.show({
							   title:'错误提示',
							   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.ERROR 
						});
					}else{
						nodeMgr.nodeInfoData = res;
						nodeMgr.setHtml(nodeMgr.nodeInfoData);
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
		},
		search:function(e){
			var target=nodeMgr.getEvent(e),searchtxt=target.value.replace(/\s/ig,'');
			var _data = nodeMgr.nodeInfoData;
			var result=[];
			for (i=0;i<_data.length;i++){
				if (nodeMgr._match(_data[i],searchtxt)){
					result.push(_data[i]);
				}
			}
			nodeMgr.setHtml(result);
		},
		_match:function(item,searchtxt){
			var len = searchtxt.length;
			var pinyins = item.en[0];
			for(var i=0;i<pinyins.length;i++){
				if(pinyins[i].substr(0,len).toLowerCase()==searchtxt){
					return true;
				}
			}
			var jianpins = item.en[1];
			for(var i=0;i<jianpins.length;i++){
				if(jianpins[i].substr(0,len).toLowerCase()==searchtxt){
					return true;
				}
			}
			if(item.title.substr(0,len).toLowerCase()==searchtxt){
				return true;
			}
			return false;
		},
		setHtml:function(dataList){
			var s='';
			for(var i=dataList.length-1;i>=0;i--){
				var item = dataList[i];
				s+='<li>';
				s+='	<div class="item-tittle">'+ item.title +'</div>';
				s+='	<div class="pic"><a href="'+ item.nodeUrl +'" target="_blank">';
				var imgHtml = '';
				for(var j=0;j<item.img.length;j++){
					if(item.img[j]!=''){
						imgHtml+='	<img src="'+ item.img[j] +'" width="130" height="36" border="0" />';
					}
				}
				if(imgHtml=='') imgHtml='	<img src="./res/img/ifeng_logo.png" width="160" height="120" border="0" />';
				s+=imgHtml;
				s+='	</a></div>';
				s+='	<p><span>'+ item.title +'</span><span class="control-link"><a href="develop/index.jhtml?nodeId='+ item.nodeId +'" target="_blank">设计时</a><a href="runtime/index.jhtml?nodeId='+ item.nodeId +'" target="_blank">运行时</a></span></p>';
				s+='	<p><span>节点配置：</span><span class="control-link"><a  href="javascript:nodeCreater.view('+ item.nodeId +')">查看</a><a  href="javascript:nodeCreater.edit('+ item.nodeId +',\'edit\')">修改</a><a href="javascript:menuConfig.init('+ item.nodeId +')">配置菜单</a></span></p>';
				s+=	'	<div class="line"></div>';
				s+='</li>';
			}
			this.container.innerHTML = s;
		},
		configMenu:function(nodeId){
			
		},
		$:function(elId){
			return document.getElementById(elId);
		},
		getEvent:function(e){
			var event=e||window.event;
			if(event){return event.srcElement||event.target;}
		}
	};
	
	
	/*
	* 节点创建、修改、查看
	*/
	var nodeCreater = {
		win:null,
		nodeId:0,
		init:function(nodeConfig,type){
			var newNodeCfg = {};
			if(nodeConfig && nodeConfig.nodeId){
				nodeCreater.nodeId = nodeConfig.nodeId;
				Ext.applyDeep(newNodeCfg,nodeConfig);

			}else{
				nodeCreater.nodeId=0;
			}
			nodeCreater.form = new Ext.form.FormPanel({
				xform:'formpanel',
				layout:"form",
				id:'frmNodeMgr',
				labelAlign:'right',
				labelWidth:120,
				border:false,
				frame:false,
				autoScroll:true,
				style:'padding:5px;',
				items:[{
					xtype:'textfield',
					name:'name',
					style:'margin:2px 10px 2px 10px',
					allowBlank:false,
					maxLength:20,
					value:newNodeCfg.name,
					fieldLabel:'节点名称'
				},{
					xtype:'fieldset',
					title:'节点服务器部署配置',
					name:'remoteHostJson',
					height:200,
					labelAlign:'right',
					labelWidth: 120,
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.port:'',
						fieldLabel:'端口'
					},{
						xtype:'textfield',
						name:'remoteDeployPath',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.remoteDeployPath:'',
						fieldLabel:'远程部署目录'
					},{
						xtype:'textfield',
						name:'remoteDataPath',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.remoteDataPath:'',
						fieldLabel:'远程存储目录'
					},{
						xtype:'textfield',
						name:'uname',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.uname:'',
						fieldLabel:'用户名'
					},{
						xtype:'textfield',
						name:'pwd',
						value:newNodeCfg.remoteHostJson?newNodeCfg.remoteHostJson.pwd:'',
						fieldLabel:'密码'
					}]
				},{
					xtype:'fieldset',
					title:'节点主数据库配置',
					name:'masterDBJson',
					height:220,
					labelAlign:'right',
					labelWidth: 120,
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.port:'',
						fieldLabel:'端口',
						value:3306
					},{
						xtype:'textfield',
						name:'dbname',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.dbname:'',
						fieldLabel:'数据库名'
					},{
						xtype:'textfield',
						name:'uname',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.uname:'',
						fieldLabel:'用户名'
					},{
						xtype:'textfield',
						name:'pwd',
						value:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.pwd:'',
						fieldLabel:'密码'
					},{
						xtype:'checkbox',
						name:'readable',
						checked:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.readable:true,
						fieldLabel:'是否可读'
					},{
						xtype:'checkbox',
						name:'writable',
						fieldLabel:'是否可写',
						checked:newNodeCfg.masterDBJson?newNodeCfg.masterDBJson.writable:true
					}]
				},{
					xtype:'fieldset',
					title:'节点从数据库配置',
					height:220,
					labelAlign:'right',
					labelWidth: 120,
					name:'slaveDBJson',
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.port:'',
						fieldLabel:'端口',
						value:3306
					},{
						xtype:'textfield',
						name:'dbname',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.dbname:'',
						fieldLabel:'数据库名'
					},{
						xtype:'textfield',
						name:'uname',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.uname:'',
						fieldLabel:'用户名'
					},{
						xtype:'textfield',
						name:'pwd',
						value:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.pwd:'',
						fieldLabel:'密码'
					},{
						xtype:'checkbox',
						name:'readable',
						checked:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.readable:true,
						fieldLabel:'是否可读'
					},{
						xtype:'checkbox',
						name:'writable',
						fieldLabel:'是否可写',
						checked:newNodeCfg.slaveDBJson?newNodeCfg.slaveDBJson.writable:false
					}]
				},{
					xtype:'listfield',
					fieldLabel:'公共检索服务器',
					name:'publicSearchHostsJson',
					width:465,
					fields:["ip","port",'EMPTY'],
					autoCreate:{ tag: 'input', type: 'hidden',name:'publicSearchHostsJson'},	
					cm:new Ext.grid.ColumnModel([{
						   header: "ip",
						   dataIndex: 'ip',
						   sortable: true,
						   width: 150,
						   editor: new Ext.form.TextField({
							   vtype:'ip'
						   })
						},{
						   header: "port",
						   dataIndex: 'port',
						   width: 150,
						   editor: new Ext.form.TextField({
							   vtype:'port'
						   })
						},{ dataIndex: 'EMPTY', width: 31, menuDisabled: true
							, css: "background:url(" + imgPath + "/images/default/layout/panel-close.gif) no-repeat center;cursor:pointer;"
						}
					]),
					gridView:new gridView_2(),
					value:newNodeCfg.publicSearchHostsJson
				},{
					xtype:'listfield',
					name:'privateSearchHostsJson',
					style:'margin:2px 10px 2px 10px',
					fieldLabel:'私有检索服务器列表',
					width:465,
					fields:["ip","port",'EMPTY'],
					autoCreate:{ tag: 'input', type: 'hidden',name:'privateSearchHostsJson'},	
					cm:new Ext.grid.ColumnModel([{
						   header: "ip",
						   dataIndex: 'ip',
						   width: 150,
						   sortable: true,
						   editor: new Ext.form.TextField({
							   vtype:'ip'
						   })
						},{
						   header: "port",
						   dataIndex: 'port',
						   width: 150,
						   editor: new Ext.form.TextField({
							   vtype:'port'
						   })
						},{ dataIndex: 'EMPTY', width: 31, menuDisabled: true
							, css: "background:url(" + imgPath + "/images/default/layout/panel-close.gif) no-repeat center;cursor:pointer;"
						}
					]),
					gridView:new gridView_2(),
					value:newNodeCfg.privateSearchHostsJson
				},{
					xtype:'fieldset',
					title:'Nosql服务器',
					height:90,
					labelAlign:'right',
					labelWidth: 120,
					name:'nosqlHostJson',
					style:'margin:2px 10px 2px 10px',
					items:[{
						xtype:'textfield',
						name:'ip',
						vtype:'ip',
						value:newNodeCfg.nosqlHostJson?newNodeCfg.nosqlHostJson.ip:'',
						fieldLabel:'IP地址'
					},{
						xtype:'numberfield',
						name:'port',
						vtype:'port',
						value:newNodeCfg.nosqlHostJson?newNodeCfg.nosqlHostJson.port:11211,
						fieldLabel:'端口'
					}]
				},{
					xtype:'listfield',
					name:'envMapJson',
					style:'margin:2px 10px 2px 10px',
					fieldLabel:'环境变量',
					width:465,
					fields:["key","value","desc",'EMPTY'],
					autoCreate:{ tag: 'input', type: 'hidden',name:'envMapJson'},	
					cm:new Ext.grid.ColumnModel([{
						   header: "key",
						   dataIndex: 'key',
						   width: 110,
						   sortable: true,
						   editor: new fm.TextField({
							   allowBlank: false
						   })
						},{
						   header: "value",
						   dataIndex: 'value',
						   width: 150,
						   editor: new fm.TextField({
							   allowBlank: false
						   })
						},{
						   header: "描述",
						   width: 150,
						   sortable: true,
						   dataIndex: 'desc',
						   editor: new fm.TextField({
							   allowBlank: true
						   })
						},{ dataIndex: 'EMPTY', width: 31, menuDisabled: true
							, css: "background:url(" + imgPath + "/images/default/layout/panel-close.gif) no-repeat center;cursor:pointer;"
						}
					]),
					gridView:new gridView_1(),
					value:newNodeCfg.envMapJson
				}],
				buttons:[]
			});
			//非查看时
			if(type!='view'){
				var btnSubmitCfg = {
					xtype:'button',
					text:'提交',
					id:'btnSubmit',
					width:100,
					handler:function(){
						//验证						
						var frm = nodeCreater.form;
						if(!frm.form.isValid()){
							Ext.Toast.show("输入不合法!",{time:1500,icon: Ext.MessageBox.ERROR });
							return;
						}
					
						Ext.getBody().mask("正在提交,请稍候......");
						
						var url = nodeCreater.nodeId>0?"develop/node!update.jhtml":"develop/node!add.jhtml";
						var params =  {"node.id":nodeCreater.nodeId};

						//搜集参数
						for(var i=0;i<frm.items.items.length;i++){
							var item = frm.items.items[i];
							var key = 'node.' + item.name;
							var value = '';
							if(item.xtype=='fieldset'){
								for(var j=0;j<item.items.items.length;j++){
									var it = item.items.items[j];
									if(value!='') {
										value+=',';
									}else{
										value='{';
									}
									 value += '"' + it.name + '":' + '"' + it.getValue() + '"'; 
								}
								value+='}';
							}else{
								value = item.getValue();
							}
							params[key] =  value ;
						}
						Ext.Ajax.request({  
							url:url,  
							method:"POST",  
							params:params,
							success:function(response,opts){
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
									Ext.Toast.show(nodeCreater.nodeId>0?ret.message:'提交成功',{
									   title:'提示',
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.INFO
								    });
									nodeCreater.win.close();
									nodeMgr.init();
								}
								Ext.getBody().unmask();
							},
							failure:function(ret,opts){
								Ext.Msg.show({
									   title:'错误提示',
									   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   minWidth:420,
									   icon: Ext.MessageBox.ERROR 
								});
								Ext.getBody().unmask();
							}		
						});	
					}
				};
				var btnCancelCfg = {
					xtype:'button',
					text:'取消',
					width:100,
					handler:function(){
						nodeCreater.win.close('btnAddNode');
					}	
				};
				nodeCreater.form.addButton(btnSubmitCfg);
				nodeCreater.form.addButton(btnCancelCfg);
			}
			//创建窗口
			nodeCreater.win= new Ext.Window({
				title:'节点配置',
				layout:"fit",
				modal:true,
				id:"openFormWin",
				closeAction:'close',
				maximizable:true,
				width:640,
				height:500,
				buttonAlign: "center",
				animateTarget:'btnAddNode',
				html:'<a id="ajaxInfo-tip">提交提示信息</a>',
				items:[nodeCreater.form]
			});

			if(type=='view'){
				nodeCreater.win.setTitle('查看节点【'+ nodeConfig.name +'】的配置信息');
			}else if(type=='edit'){
				nodeCreater.win.setTitle('修改节点【'+ nodeConfig.name +'】的配置信息');
			}else{
				nodeCreater.win.setTitle('创建新节点');
			}
			nodeCreater.win.show('btnAddNode');
		},
		edit:function(nodeId,type){
			Ext.getBody().mask("正在获取数据,请稍候......");
			Ext.Ajax.request({  
				url:'develop/node!view.jhtml',  
				method:"GET",  
				params:{"node.id":nodeId},
				success:function(response,opts){
					Ext.getBody().unmask();
					var res = Ext.util.JSON.decode(response.responseText);
					if(res.success == false){
						Ext.Msg.show({
							   title:'错误提示',
							   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.ERROR 
						});
					}else{
						nodeCreater.init(res,type);
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
		},
		view:function(nodeId){
			nodeCreater.edit(nodeId,'view');
		}
	}
	//创建节点
	function createNode(){
		//nodeCreater.win.show('btnAddNode');
		nodeCreater.init();
	}
	
	/*
	* 节点菜单配置
	*/
	var menuConfig ={
		win:{},
		setUrl:'develop/rtMgr!writeSysMenu.jhtml',
		getUrl:'develop/rtMgr!readSysMenu.jhtml',
		init:function(nodeId){
			
			var win = new Ext.Window({
				title:'菜单配置',
				height:500,
				width:600, 
				modal: true,
				buttonAlign: "center",
				closable:true ,
				closeAction:'close',
				maskDisabled:false,
				modal:false,
				maximizable:true,
				layout:'fit',
				items:[{
					xtype:'textarea',
					id:'txtMenuConfig' + nodeId
				}],
				buttons:[{
					text:'保存',
					scope:{nodeId:nodeId},
					handler:function(){
						menuConfig.win[this.nodeId].getEl().mask('正在提交...');
						var cfgContent = Ext.getCmp('txtMenuConfig' + this.nodeId).getValue();
						Ext.Ajax.request({  
							url:menuConfig.setUrl,
							method:'post',	
							params:{nodeId:this.nodeId,content:cfgContent},
							options:{nodeId:this.nodeId},
							success:function(response,action){
								menuConfig.win[action.options.nodeId].getEl().unmask();
								try{
									var ret = Ext.util.JSON.decode(response.responseText);
									if(ret.success){
										menuConfig.win[action.options.nodeId].close();
									}else{
										Ext.Toast.show('保存出错',{
										   title:'提示',
										   buttons: Ext.Msg.OK,
										   animEl: 'elId',
										   minWidth:420,
										   icon: Ext.MessageBox.ERROR
										});
									}
									
								}catch(ex){
								
								}
							},
							failure:function(ret,action){
								menuConfig.win[action.options.nodeId].getEl().unmask();
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
				},{
					text:'关闭',
					scope:{nodeId:nodeId},
					handler:function(){
						menuConfig.win[this.nodeId].close();
						delete menuConfig.win[this.nodeId];
					}
				}]
			});
			menuConfig.win[nodeId] = win;
			win.show('btnAddNode');
			
			win.getEl().mask('正在获取配置信息...');
			Ext.Ajax.request({  
				url:menuConfig.getUrl + '?nodeId=' + nodeId,
				method:'get',	
				options:{nodeId:nodeId},
				success:function(response,action){
					menuConfig.win[action.options.nodeId].getEl().unmask();
					Ext.getCmp('txtMenuConfig' + action.options.nodeId).setValue(response.responseText);
				},
				failure:function(ret,action){
					menuConfig.win[action.options.nodeId].getEl().unmask();
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
	}
</script>
<!-- 节点列表、节点查看修改、节点查询、节点列表初始化 end-->

<!-- 页面初始化 begin-->
<script type="text/javascript">
Ext.onReady(function(){
Ext.BLANK_IMAGE_URL = "./res/js/ext2/resources/images/default/s.gif";
Ext.lOADING_IMAGE_URL= "./res/js/ext2/resources/images/default/grid/wait.gif"; //resources\images\default\grid
Ext.QuickTips.init();
Ext.form.Field.prototype.msgTarget = 'qtip';

mainPanel = new Ext.Viewport({
	layout:"border",
	items:[{
		xtype:'panel',
		region:"center",
		layout:"fit",
		border:true,
		html:'<ul class="starUl" id="nodeListBox"></ul>',
		tbar:[{
			xtype:'label',
			text:'搜索节点：'
		},{
			xtype:'textfield',
			id:'txtSearch',
			width:180,
			emptyText:'输入节点名称全拼或全拼首字母',
			enableKeyEvents:true,
			style:'background:url(./res/img/search.gif) no-repeat;padding-left:18px;'
			
		},{
			xtype:'button',
			iconCls:'add',
			text:'新建节点',
			id:'btnAddNode',
			handler:function(obj,e){
				createNode();
			}
		}]
	}]
});
var txtSearch = Ext.getCmp('txtSearch');
addEventHandler(txtSearch.getEl().dom,'keyup',bindFunc(txtSearch.getEl().dom,nodeMgr.search));
nodeMgr.init();//初始化节点列表

});
</script>
<!-- 页面初始化 end-->

</body>
</html>
