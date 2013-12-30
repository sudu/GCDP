Ext.ux.ListItemEditorField = function (editor) {
	this.editor = editor;
	this.constructor.superclass.constructor.call(this);
}
Ext.extend(Ext.ux.ListItemEditorField, Ext.form.Field, {
	editor: null
	,fieldEditor:null
	,fieldData:null
	 , "defaultTitle": ""
	, canceled: false
	, win: null
	, propTable: null
	, itemTable: null
	, leftPanel: null
	, rightCtnr: null
	, selItem:null
	, dataModel:null
	, propertyData:[]
	, initComponent: function () {
		var prot = this.constructor.prototype;
		prot.win = new Ext.Window({
			"width": 400
			, "height": 400
			, "closable": false
			, "modal": true
			, "layout":'column'
			, "buttonAlign": "center"
		})

		var leftPanel = new Ext.Panel({ 
			"columnWidth":.35,
			"buttonAlign": "center",
			"title": "列表项",
			bbar:[{ 
					text : "增加", 
					iconCls : 'add', 
					scope:this,
					handler : this.addItem 

				},{ 
					text : "删除", 
					iconCls : 'delete', 
					scope:this,
					handler : this.removeItem 
				}]
		});
		prot.itemTable = new Ext.Container({ "autoEl": "ul" });
		leftPanel.add(prot.itemTable);
		prot.leftPanel = leftPanel;
		/*
		var rightCtnr = new Ext.Container({ 
			"autoEl": "div" 
		});
		*/
		var _this = this;
		prot.propTable = new Ext.ux.grid.GroupPropertyGrid({
			"columnWidth":.64,
			listeners: {
				propertychange: function (source, recordId, value, oldValue) {
					if (recordId == "通用&&f_name"){
						var colName = _this.fieldData[value];
						this.item.el.update(colName);

						var key = '通用&&l_tpl';
						var index = this.store.data.indexOfKey(key);
						var newValue = '{'+value+'}';
						/*
						this.store.data.map[key].data.value = newValue;
						this.store.data.items[index].data.value = newValue;
						*/
						this.getView().getCell(index,1).firstChild.innerHTML = newValue;
						//var record = this.store.getById(key);
						source.通用.value.l_tpl.value = newValue;
						//record.commit(); 
						
						key = '通用&&l_columnName';
						index = this.store.data.indexOfKey(key);
						newValue = colName;
						this.getView().getCell(index,1).firstChild.innerHTML = newValue;
						source.通用.value.l_columnName.value = newValue;
						
						this.store.commitChanges();
					}else if (recordId == "通用&&l_columnName"){
						this.item.el.update(value);
					}
				}
			}
		});
		
		//prot.rightCtnr = rightCtnr;
		prot.win.addButton("确定", this.done, this);
		prot.win.addButton("取消", this.cancel, this);
		Ext.ux.ListItemEditorField.prototype.initComponent = function () {
			Ext.ux.ListItemEditorField.superclass.initComponent.call(this);
		};
		
		var editor =this.fieldEditor= new Ext.grid.GridEditor(new Ext.form.ComboBox({
			allowBlank:false,
			mode: 'local',
			store:new Ext.data.SimpleStore({　
			　　fields:['value','text'],　
			}),
			valueField : 'value',//值
			displayField : 'text',//显示文本
			editable: false,//是否允许输入
			forceSelection: true,//必须选择一个选项
			triggerAction:'all',
			selectOnFocus:false
		}));
		this.propTable.customEditors[Static.generalDataModel.通用.value.f_name.lan] = editor;		
		
		
		this.initComponent();
	}
	,loadModel:function(model){
		if(this.dataModel!=model){
			this.dataModel=model;
		}
		this.loadFieldnameData();
	}

	, loadFieldnameData:function(){
		//初始化字段combobox列表
		var flds =this.fieldData= Designer.getAllFields();
		var data=[];
		for(var i in flds){
			data.push([i,i]);
		}
		this.fieldEditor.field.store.loadData(data);
	}	
	, addItem: function () {
		var index = this.itemTable.items?this.itemTable.items.length:0;
		var	propertyData = Ext.applyDeep({},this.dataModel);
		propertyData.通用.value.l_columnName.value += index;
		this.propertyData[index] = propertyData;
		
		this.fillItem(propertyData);
		this.itemTable.doLayout();
		this.itemTable.items.get(this.itemTable.items.length - 1).fireEvent("click");
	}
	, removeItem: function () {
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
	}
	, openWindow: function (title) {
		var prot = this.constructor.prototype;
		var dt = this.editor.defaultTitle;
		dt += title;
		this.win.setTitle(dt);
		this.win.show();
		var target = this.win.getLayoutTarget();
		var height = target.getStyleSize().height;
		this.leftPanel.height = height;
		this.propTable.height = height;
		prot.win.add(this.leftPanel);
		prot.win.add(this.propTable);
		this.win.doLayout();
		(this.originalValue.length > 0 && this.itemTable.items.get(0).fireEvent("click"));
		prot.openWindow = function () {
			var dt = this.editor.defaultTitle;
			dt += title;
			this.win.setTitle(dt);
			this.win.show();
			if(this.originalValue.length > 0)
				this.itemTable.items.get(0).fireEvent("click");
			else
				this.propTable.store.removeAll();
		};
	}
	, getValue: function () {
		var v = null;
		if (!this.canceled) {
			var items = this.itemTable.items;
			if (items) {
				var index = items.length;
				v = new Array(index);
				while (--index > -1) {
					v[index] = items.get(index).data;
				}
			}
		}else{
			return this.orignalValue;
		}
		if(v==null) return '';
		return Ext.util.JSON.encode(v);
	}
	, setValue: function (val) {//pass json;
		 this.orignalValue = val;
		try{
			val = Ext.util.JSON.decode(val);
		}catch(ex){
			;
		}
		if(typeof(val) =='string')
			val=[];
		this.originalValue = val;
		if (this.itemTable.items && this.itemTable.items.length > 0) {
			this.itemTable.items.clear();
			this.itemTable.el.dom.innerHTML = "";
		}

		var len = val.length;
		for (var i = 0; i < len; i++) {
			this.fillItem(val[i]);
		}
	}
	, fillItem: function (propertyData) {

		var item = Ext.DomHelper.append(document.body, { tag: "li", cls:'listItem',html: propertyData['通用'].value.l_columnName.value}, true);
		var ths = this;
		var itemCmp = new Ext.Component({ el: item });
		itemCmp.data = propertyData;
		this.itemTable.add(itemCmp);
		itemCmp.index = this.itemTable.items.length - 1;
		item.on("click", function () {
			ths.showProp(propertyData, itemCmp);
		});
		ths.showProp(propertyData, itemCmp);
				
		this.loadFieldnameData();
	}
	, showProp: function (propData, item) {	
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
		
	}

	, done: function () {
		this.win.hide();
		this.canceled = false;
		this.complete();
	}
	, cancel: function () {
		this.win.hide();
		this.canceled = true;
		this.complete();
	}
	, complete: function () {
		this.editor.allowBlur = false;
		this.editor.onBlur();
	}
	, isEditableValue: function (val) {
		if (Ext.isDate(val)) {
			return true;
		} else if (typeof val == 'object' || typeof val == 'function') {
			return false;
		}
		return true;
	}


});




Ext.ux.ListItemEditor = function (config) {
	var field = new Ext.ux.ListItemEditorField(this);
	if(config&&config.dataModel)
		field.dataModel = config.dataModel;	
	Ext.ux.ListItemEditor.superclass.constructor.call(this, field, config);
}

Ext.extend(Ext.ux.ListItemEditor, Ext.grid.GridEditor, {
	"defaultTitle": ""
	, startEdit: function (el, v) {
		if (this.editing) {
			this.completeEdit();
		}

		if (this.fireEvent("beforestartedit", this, this.boundEl, v) === false) {
			return;
		}
		this.startValue = v;
		this.setValue(v);
		//        this.doAutoSize();
		//        this.el.alignTo(this.boundEl, this.alignment);
		this.editing = true;
		//        this.show();
		this.allowBlur = true;
		this.field.openWindow(this.record.id); //title set here;
	}
	, completeEdit: function (remainVisible) {
		if (!this.editing) {
			return;
		}
		var v = this.getValue();
		if (this.revertInvalid !== false && !this.field.isValid()) {
			v = this.startValue;
			this.cancelEdit(true);
		}
		if (String(v) === String(this.startValue) && this.ignoreNoChange) {
			this.editing = false;
			this.hide();
			return;
		}
		if (this.fireEvent("beforecomplete", this, v, this.startValue) !== false) {
			this.editing = false;
			if (this.updateEl && this.boundEl) {
				var val = v;
				if (Ext.type(v) == "array") {
					val = Ext.ux.PropertyColumnModel.prototype.renderArray(v);
				}
				this.boundEl.update(v);
			}
			if (remainVisible !== true) {
				this.hide();
			}
			this.fireEvent("complete", this, v, this.startValue);
		}
	}
	, getValue: function () {
		var val = this.field.getValue(val);
		if (val == null)
			val = this.startValue;
		return val;
	}
});