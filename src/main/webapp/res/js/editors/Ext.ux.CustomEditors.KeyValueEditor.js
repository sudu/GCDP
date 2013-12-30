Ext.ux.CustomEditors.KeyValueEditorField = function (editor) {
	this.editor = editor;
	this.constructor.superclass.constructor.call(this);
}
Ext.extend(Ext.ux.CustomEditors.KeyValueEditorField, Ext.form.Field, {
	editor: null,
	defaultTitle: "",
	canceled: false,
	win: null,
	propTable: null,
	selItem:null,
	dataModel:null,
	propertyData:[], 
	initComponent: function () {
		var prot = this.constructor.prototype;
		prot.win = new Ext.Window({
			"width": 400
			, "height": 400
			, "closable": false
			, "modal": true
			, "layout":'fit'
			, "buttonAlign": "center",
			autoScroll:true
		})

		var _this = this;
		prot.propTable = new Ext.ux.grid.GroupPropertyGrid();
		prot.win.addButton("确定", this.done, this);
		prot.win.addButton("取消", this.cancel, this);
		Ext.ux.CustomEditors.KeyValueEditorField.prototype.initComponent = function () {
			Ext.ux.CustomEditors.KeyValueEditorField.superclass.initComponent.call(this);
		};
		
		this.initComponent();
	},
	loadModel:function(model){
		if(this.dataModel!=model){
			this.dataModel=model;
		}
	}, 
	openWindow: function (title) {
		var prot = this.constructor.prototype;
		var dt = this.editor.defaultTitle;
		dt += title;
		this.win.setTitle(dt);
		this.win.show();
		var target = this.win.getLayoutTarget();
		var height = target.getStyleSize().height;
		this.propTable.height = height;
		prot.win.add(this.propTable);
		this.win.doLayout();
		prot.openWindow = function () {
			var dt = this.editor.defaultTitle;
			dt += title;
			this.win.setTitle(dt);
			this.win.show();
			if(this.originalValue.length == 0)
				this.propTable.store.removeAll();
		};
	},
	getValue: function () {
		var v = null;
		if (!this.canceled) {
			v=this.propTable.getSource();
		}else{
			return this.orignalValue;
		}
		if(v==null) return '';
		return Ext.util.JSON.encode(this.encode(v));
	},
	setValue: function (val) {//pass json;
		 this.orignalValue = val;
		try{
			val = Ext.util.JSON.decode(val);
		}catch(ex){
			;
		}
		if(typeof(val) =='string')
			val={};			
		this.originalValue = val;

		//根据dataModel转换数据格式
		val = this.decode(val);
		
		this.fillItem(val);
	},
	//将原始格式的数据转换成需要的数据格式
	encode:function(v){
		var item = v;
		var newItem = {};
		delete item.groupName;
		for(var g in item){
			newItem[g]={};
			for(var k in item[g].value){
				newItem[g][k] = item[g].value[k].value;
			}
		}
		return newItem;
	},
	decode:function(v){
		var	propertyData = Ext.applyDeep({},this.dataModel);
		
		for(var g in propertyData){
			for(var k in propertyData[g].value){
				if(v[g]) propertyData[g].value[k].value = v[g][k];
			}
		}

		return propertyData;
	},
	fillItem: function (propertyData) {
		this.showProp(propertyData);
	},
	showProp: function (propData) {	
		var v = this.propTable.view;
		if (v.el) {
			v.scroller.dom.style.height = "auto";
			v.scroller.dom.style.width = "auto";
			v.el.dom.style.height = "auto";
		}
		this.propTable.setSource(propData);
	},
	done: function () {
		this.win.hide();
		this.canceled = false;
		this.complete();
	},
	cancel: function () {
		this.win.hide();
		this.canceled = true;
		this.complete();
	},
	complete: function () {
		this.editor.allowBlur = false;
		this.editor.onBlur();
	},
	isEditableValue: function (val) {
		if (Ext.isDate(val)) {
			return true;
		} else if (typeof val == 'object' || typeof val == 'function') {
			return false;
		}
		return true;
	}


});




Ext.ux.CustomEditors.KeyValueEditor = function (config) {
	var field = new Ext.ux.CustomEditors.KeyValueEditorField(this);
	if(config&&config.dataModel)
		field.dataModel = config.dataModel;	
	Ext.ux.CustomEditors.KeyValueEditor.superclass.constructor.call(this, field, config);
}

Ext.extend(Ext.ux.CustomEditors.KeyValueEditor, Ext.grid.GridEditor, {
	"defaultTitle": "",
	startEdit: function (el, v) {
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
		this.field.openWindow(this.record.data.group + "-" + this.record.data.name); //title set here;
	},
	completeEdit: function (remainVisible) {
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
	},
	getValue: function () {
		var val = this.field.getValue(val);
		if (val == null)
			val = this.startValue;
		return val;
	}
});