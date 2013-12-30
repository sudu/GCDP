 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>列表页配置</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<style>
		/*列表页属性编辑器样式*/
	.listItem{background:silver;padding:2px 0 2px 0;text-align:center;cursor:pointer;height:15px;
		border-top: 1px solid #FFFFFF;
		border-left: 1px solid #FFFFFF;
		border-bottom: 1px solid #717171;
		border-right: 1px solid #717171;
	}
	.selectlistItem{
		border-top: 1px solid #717171;
		border-left: 1px solid #717171;
		border-bottom: 1px solid #FFFFFF;
		border-right: 1px solid #FFFFFF;
	}
	</style>
	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script> 
	<script type="text/javascript" src="./../res/js/editors/Ext.ux.CustomEditors.TextArea.js"></script>
	<script type="text/javascript" src="./../res/js/editors/Ext.ux.listItemEditor.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript">
		Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
		var Designer = {
			customEditors:{
				TextFieldReadonly:new Ext.grid.GridEditor(new Ext.form.TextField({
						disabled:true,      
						disabledClass:"" 
					})),
				CustomEditors_TextArea:new Ext.ux.CustomEditors.TextArea({})
			}
		}
		var listPage__=${listPageJson};
		listPage__.config = Ext.util.JSON.decode(listPage__.config);
	</script>
</head>
<body>

<script type="text/javascript">
var listEditor={
	fields:[],
	editor:{
		dataModel:{
				width:{value:'',en_ch:'宽度',type:'NumberField'},
				field:{value:'',en_ch:'字段',type:'TextField'},
				title:{value:'列名',en_ch:'列名',type:'TextField'},
				tpl:{value:'',en_ch:'列模板',type:'CustomEditors_TextArea'},
				isView:{value:true,en_ch:'是否显示'}
		}
	},
	ch_en:{
		"列名":"title",	
		"列模板":"tpl",	
		"字段":"field",
		"宽度":"width",
		"是否显示":"isView"
	},
	canceled: false, 
	win: null,
	propTable: null, 
	itemTable: null, 
	leftPanel: null, 
	rightCtnr: null, 
	selItem:null, 
	dataModel:{data:{},ch_en:{}}, 
	propertyData:[],
	listTitle:'',
	pagesize:20,
	viewId:'',
	addItem: function () {
		var index = this.itemTable.items?this.itemTable.items.length:0;
		var	propertyData = Ext.applyDeep({},this.dataModel.data);
		propertyData.列名 += index;
		this.propertyData[index] = propertyData;
		
		this.fillItem(propertyData);

	},
	removeItem: function () {
		var cur = this.propTable.item;
		if (!cur)
			return;
		this.propTable.item = null;
		var curIndex = cur.index;
		this.itemTable.remove(cur);
		var next = this.itemTable.items.get(curIndex);
		if (next) {
			next.fireEvent("click");
		} else {
			this.propTable.store.removeAll();
			this.propTable.doLayout();
		}
	},
	fillItem: function (propertyData) {
		var item = Ext.DomHelper.append(document.body, { tag: "li", cls:'listItem',html: propertyData[this.editor.dataModel.title.en_ch]}, true);
		var ths = this;
		var itemCmp = new Ext.Component({ el: item });
		itemCmp.data = propertyData;
		this.itemTable.add(itemCmp);
		itemCmp.index = this.itemTable.items.length - 1;
		item.on("click", function () {
			ths.showProp(propertyData, itemCmp);
		});
		ths.showProp(propertyData, itemCmp);
		this.itemTable.doLayout();
		this.itemTable.items.get(this.itemTable.items.length - 1).fireEvent("click");		
		this.setFieldCustomEditor();
	},
	showProp: function (propData, item) {	
		//设置样式
		if(this.selItem)this.selItem.removeClass('selectlistItem');
		item.addClass('selectlistItem');
		this.selItem = item;
	
		var v = this.propTable.view;
		if (v.el) {
			v.scroller.dom.style.height = "auto";
			v.scroller.dom.style.width = "auto";
			v.el.dom.style.height = "auto";
		}
		this.propTable.setSource(propData);
		this.propTable.item = item;
		
	},
	setFieldCustomEditor:function(){
		//初始化字段combobox列表
		var flds = listPage__.fields;
		this.fields = {store:[],data:{}};
		for(var i=0;i<flds.length;i++){
			this.fields.store[i] = [flds[i][0],flds[i][0] + '(' + flds[i][1] + ')'];
			this.fields.data[flds[i][0]] = flds[i][1];
		}
		
		if(this.fldCustomEditor){
			this.fldCustomEditor.field.store.loadData(this.fields.store);	
		}else{
			this.fldCustomEditor = new Ext.grid.GridEditor(new Ext.form.ComboBox({
				allowBlank:false,
				mode: 'local',
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text'],　
				　　data:this.fields.store
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				editable: false,//是否允许输入
				forceSelection: true,//必须选择一个选项
				triggerAction:'all',
				selectOnFocus:false
			}));
			this.propTable.customEditors[this.editor.dataModel.field.en_ch] = this.fldCustomEditor;
		}

	},	
	setCustomEditors:function(gdata,j){
				var type = gdata[j].type;
				//有些编辑器可以共用
				var customEd = Designer.customEditors[type];
				if(customEd!=undefined){
					this.propTable.customEditors[gdata[j].en_ch] = customEd;
				}else{
					switch(type){ //todo other case				
						case 'ComboBox':
							customEd = new Ext.grid.GridEditor(new Ext.form.ComboBox({
											allowBlank:false,
											store:new Ext.data.SimpleStore({　
											　　fields:['value','text'],　
											　　data:gdata[j].store
											}),
											valueField : 'value',//值
											displayField : 'text',//显示文本
											editable: false,//是否允许输入
											forceSelection: true,//必须选择一个选项
											mode:'local',
											triggerAction:'all',
											selectOnFocus:true
									}));
							break;	
						default:
							customEd = null;
							break;			
					}
					if(customEd)	this.propTable.customEditors[gdata[j].en_ch] = customEd;
				}
	},	
	done:function(){
		var v = this.getValue();
		//保存
		this.win.getEl().mask('正在提交,请稍候...');
		var win = this.win;
		Ext.Ajax.request({
			url: 'listConfig!saveListConfig.jhtml',
			params: { formId: listPage__.formId,listId:listPage__.listId,name:this.listTitle,config:v },
			options:{win:win},
			success: function(response, options) {   
				options.options.win.getEl().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				if(ret.success)
					Ext.Msg.show({
					   title:'提示',
					   msg:  decodeURIComponent('保存成功'),
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.INFO
					});	
				else
					Ext.Msg.show({
					   title:'错误提示',
					   msg:  decodeURIComponent('保存失败'),
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});	
			},
			failure:function(response,options){
				options.options.win.getEl().unmask();
				Ext.Msg.show({
				   title:'错误提示',
				   msg:  decodeURIComponent(response.responseText),
				   buttons: Ext.Msg.OK,
				   animEl: 'elId',
				   minWidth:420,
				   icon: Ext.MessageBox.ERROR 
				});					
			}	
			
		});
	},
	close:function(){
		if(top && top.centerTabPanel){
			top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
		}
	},
	openWindow: function (title) {
		var _this = listEditor;
		_this.win.setTitle(title);
		_this.win.show();
		var target = _this.win.getLayoutTarget();
		var height = target.getStyleSize().height;
		_this.leftPanel.height = height;
		_this.propTable.height = height;
		_this.win.add(_this.leftPanel);
		_this.win.add(_this.rightCtnr);
		_this.win.doLayout();
		(_this.originalValue.length > 0 && _this.itemTable.items.get(0).fireEvent("click"));
		_this.openWindow = function () {
			_this.win.show();
			(_this.originalValue.length > 0 && _this.itemTable.items.get(0).fireEvent("click"));
		};
	},
	setValue: function (val) {//pass json;
		if(typeof(val) !='object' || !val.config){
			val={config:{pagesize:20,columns:[]},listTitle:''};
		}
		this.listTitle = val.name;
		this.viewId = val.config.viewId;
		this.pagesize = val.config.pagesize;	
		//转换数据
		var originalValue = [];
		for(var i=0;i<val.config.columns.length;i++){
			var col = {};
			col={};
			for(var key in val.config.columns[i]){
				var ch = this.editor.dataModel[key].en_ch;
				col[ch]=val.config.columns[i][key];
			}
			originalValue.push(col);
		}		
		this.originalValue = originalValue;

	},
	getValue:function(){
		var retJson = {};
		this.listTitle = Ext.getCmp('txtListTitle').getValue();
		retJson.viewId = Ext.getCmp('txtViewId').getValue();
		retJson.pagesize =  Ext.getCmp('txtPagesize').getValue();
		
		var v=null;
		var items = this.itemTable.items;
		if (items) {
			var index = items.length;
			v = new Array(index);
			while (--index > -1) {
				var newData = {};
				var data = items.get(index).data;
					newData={};
					for(var key in data){
						newData[this.ch_en[key]] = data[key];
					}
				v[index] = newData;	
			}
		}
		retJson.columns = v;
		return Ext.util.JSON.encode(retJson);
	},
	init:function(){
		var _this = listEditor;
		_this.win = new Ext.Window({
			"width": 550
			,"title":'列表试图配置'
			,"height": 550
			, "closable": true
			, "modal": true
			, "layout":'column'
			, "buttonAlign": "center",
			tbar:[{
				xtype:'label',
				text:'列表标题:'		
			},{
				xtype:'textfield',
				id:"txtListTitle",
				value:this.listTitle,
				width:200
			},{
				xtype:'label',
				text:'每页显示:'		
			},{
				xtype:'numberfield',
				id:"txtPagesize",
				value:this.pagesize,
				width:50
			},{
				xtype:'label',
				text:'条, '		
			},{
				xtype:'label',
				text:'表单视图id(viewId):'		
			},{
				xtype:'numberfield',
				id:"txtViewId",
				value:this.viewId?this.viewId:'',
				width:50
			}],
			listeners:{
				close:function(){
					if(top && top.centerTabPanel){
						top.centerTabPanel.remove(top.centerTabPanel.getActiveTab());
					}
				}
			}
		});

		var leftPanel = new Ext.Panel({ 
			"columnWidth":.35,
			"style": "cssFloat:left;",
			"buttonAlign": "center",
			"title": "列表项",
			layout: 'border',
			bbar:[{ 
					text : "增加", 
					iconCls : 'add', 
					scope:this,
					handler : _this.addItem 

				},{ 
					text : "删除", 
					iconCls : 'delete', 
					scope:this,
					handler : _this.removeItem 
				}]
		});
		_this.itemTable = new Ext.Container({ "autoEl": "ul",region:"center" });
		leftPanel.add(_this.itemTable);
		_this.leftPanel = leftPanel;
		var rightCtnr = new Ext.Container({ 
			"columnWidth":.64,
			"style": "cssFloat:right;",
			"autoEl": "div" 
		});
		_this.propTable = new Ext.grid.PropertyGrid({
			listeners: {
				propertychange: function (source, recordId, value, oldValue) {
					if (recordId == "字段"){
						var colName = _this.fields.data[value];
						this.item.el.update(colName);
						//var source = this.getSource();
						source["列名"] = colName;
						source["列模板"] = "{"+value+"}";
						_this.propTable.setSource(source);
					}else if (recordId == "列名"){
						this.item.el.update(value);
					}
				}
			}
		}); //propertygrid could only be added after document.body is available.#32935;
		rightCtnr.add(_this.propTable);
		
		//重组组建属性源
		var dm = _this.editor.dataModel;
		var gdata = {};
		_this.dataModel.data = gdata;
		_this.dataModel.ch_en= {};
		for(var i in dm){
			gdata[dm[i].en_ch] = dm[i].value;
			_this.dataModel.ch_en[dm[i].en_ch] = i;
			_this.setCustomEditors(dm,i);
		}
		
		_this.rightCtnr = rightCtnr;
		_this.win.addButton("保存", _this.done, this);
		if(top && top.centerTabPanel) _this.win.addButton("关闭", _this.close, this);
		_this.win.show();
		_this.win.doLayout();
		var target = _this.win.getLayoutTarget();
		var height = target.getStyleSize().height;
		_this.leftPanel.height = height;
		_this.propTable.height = height;
		_this.win.add(_this.leftPanel);
		_this.win.add(_this.rightCtnr);
		_this.win.doLayout();
		
		if (_this.itemTable.items && _this.itemTable.items.length > 0) {
			_this.itemTable.items.clear();
			_this.itemTable.el.dom.innerHTML = "";
		}
		var len = _this.originalValue.length;
		for (var i = 0; i < len; i++) {
			this.fillItem(_this.originalValue[i]);
		}
	}
};

Ext.onReady(function(){
Ext.BLANK_IMAGE_URL = "../res/js/ext2/resources/images/default/s.gif";
listEditor.setValue(listPage__);
listEditor.init();	

});
</script>

</body>
</head>
</html>	
	
	
	