/*
*JSON数组编辑器
*author:cici
*date:2013/4/25
*依赖js：controls/Ext.ux.JSONEditor.js
*依赖css：
*/

Ext.ux.JSONEditor = function (config) {	
	var fields=[];
	try{
		if(typeof config.columns =="string"){
			config.columns = Ext.decode(config.columns);
		}
	}catch(ex){
		Ext.CMPP.warn('错误','columns配置语法有误' + ex.message);
		return;
	}
	for(var i=0;i<config.columns.length;i++){
		var f = config.columns[i].field;
		f && fields.push(f);
	}
	if(config.value ){
		if(typeof config.value =="string"){
			var value = Ext.decode(config.value);
		}else{
			var value = config.value;
			config.value = Ext.encode(config.value);
		}
	}
	
	//创建column
	var defaultUI = {
		"Ext.form.ComboBox":{
			triggerAction:"all",
			editable:false
		}
	};
	var cmCfg = [new Ext.grid.CheckboxSelectionModel()];
	var createEditor=function(cfg){
		var clsName = cfg.name || "Ext.form.TextField";
		var ui = cfg.ui || {};
		Ext.apply(ui,defaultUI[clsName]);
		eval('var editor = new ' + clsName + '(ui)');
		return editor;
	}
	for(var i=0;i<config.columns.length;i++){
		var item = config.columns[i];
		cmCfg.push({
			header: item.header,
			dataIndex:item.field,
			width:item.width,
			editor:createEditor(item.editor)
		});
	}
	cmCfg.push({
		header: "删除",
		align:'center',
		width: 31,
		dataIndex: 'EMPTY', 
		cls:"jsoneditor-faultMark"
	});
	var cm = new Ext.grid.ColumnModel(cmCfg);
	
	var store = new Ext.data.Store({
		proxy: new Ext.data.MemoryProxy(value), 
		reader : new Ext.data.JsonReader({
			autoLoad:true,
			fields: fields
		})
	});
	Ext.ux.JSONEditor.superclass.constructor.call(this,Ext.apply({
		value:config.value||[],
		store:store,
		cm:cm,
		hiddenName :config.hiddenName || config.name
	},config));
}
Ext.extend(Ext.ux.JSONEditor, Ext.form.Field, {
	/****/
	resRoot:'../res/',
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		Ext.ux.JSONEditor.superclass.initComponent.call(this);

	},
	onRender:function(ct,pos){
		Ext.ux.JSONEditor.superclass.onRender.call(this,ct,pos);
		this.hiddenInput = this.el.dom;
		var grid = new Ext.grid.EditorGridPanel({
			store: this.store,
			frame:false,
			enableColumnHide:false,
			cm: this.cm,
			sm: new Ext.grid.CheckboxSelectionModel(),
			autoHeight:true,
			width:this.width||700,
			enableHdMenu: false,
			clicksToEdit:1,
			tbar: [{
				text: '添加列',
				iconCls:"addField",
				scope:this,
				handler : function(){
					var store = this.grid.store;
					store.loadData([{}],true);
					var len = store.getCount();
					this.grid.startEditing(len-1, 1);
				}
			},{
				text: '删除列',
				disable:true,
				iconCls:"delField",
				scope:this,
				handler : function(){
					this._removeSelected();
				}
			},{
				xtype:'tbfill'
			}],
			listeners: {
				scope:this,
				cellclick: function (grid, row, cell, e) {
					var store = grid.store;
					var targetEl = Ext.fly(e.target);
					if(targetEl.hasClass('jsoneditor-faultMark') || targetEl.parent().hasClass('jsoneditor-faultMark')){
						var cur = store.getAt(row);
						store.remove(cur);
						this.syncValue();
					}
				},
				afteredit :function(){
					this.syncValue();
				}
			}
		});
		this.grid = grid;
		this.grid.render(ct);
		this.grid.field = this;	
		this.el = this.grid.el;
		this.store.load();
	},
	_removeSelected:function(){
		var selItems = this.grid.getSelectionModel().selections.items;
		for(var i=selItems.length-1;i>=0;i--){
			this.grid.store.remove(selItems[i]);
		}
		this.syncValue();
	},
	syncValue:function(){
		var store = this.grid.store;
		var columns = [];
		var items = store.data.items;
		for(var i=0;i<items.length;i++){
			var item = items[i].data;
			var isEmpty=true;
			for(var key in item){
				if(item[key]!=''){
					isEmpty=false;
					break;
				}
			}
			if(!isEmpty){
				columns.push(item);					
			}
		}
		if(columns.length>0)
			this.hiddenInput.value = Ext.encode(columns);
		else
			this.hiddenInput.value = "";
	},
	getValue:function(){
		return this.hiddenInput.value ;
	}
});
Ext.reg('jsoneditor', Ext.ux.JSONEditor);
