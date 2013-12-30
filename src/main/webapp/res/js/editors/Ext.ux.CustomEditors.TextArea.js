
//(Ext.ux.CustomEditors || (Ext.ux.CustomEditors = {}));
Ext.namespace('Ext.ux.CustomEditors');	

Ext.ux.CustomEditors.TextAreaField = function (editor) {
	this.editor = editor;
	this.constructor.superclass.constructor.call(this);
}
Ext.extend(Ext.ux.CustomEditors.TextAreaField,Ext.form.Field, {
	win:null,
	canceled: false,
	clickOK:false,
	orignalValue:'',
	initComponent: function () {
		Ext.ux.CustomEditors.TextAreaField.superclass.initComponent.call(this);
		var _this = this;
		var win = new Ext.Window({
			title:'弹出窗口',
			height:500,
			width:600, 
			modal: true,
			buttonAlign: "center",
			closable:true ,
			closeAction:'hide',
			maximizable:true,
			layout:'fit',
			items:[{
				xtype:'textarea',
				anchor:'100%',
				id:'ce_txtScript'
			}],
			listeners:{
				'hide':function(){
					var _t = _this;
					return function(){
						_t.complete();
					}
				}()
			}
				
		});
		win.addButton("确定", this.ok, this);
		win.addButton("取消", this.cancel, this);
		this.win = win;
		
	},	
	openWin: function (cfg) {
		this.win.setAnimateTarget(this.editor.animateTarget);
		this.win.setTitle(cfg.title);
		this.win.show();
	},
	setValue: function (val) {
		 this.orignalValue = val;
		this.win.items.items[0].setValue(val);
	},
	getValue: function () {
		if(this.canceled){
			return this.orignalValue;
		}else{
			return this.win.items.items[0].getValue();
		}
	},
	ok: function () {
		this.canceled = false;
		this.win.hide();
		//this.canceled = false;
		//this.complete();
	},
	cancel: function () {
		this.canceled = true;
		this.win.hide();
		//this.canceled = true;
		//this.complete();
	},
	complete: function () {
		this.editor.allowBlur = false;
		this.editor.onBlur();
	}
});
Ext.ux.CustomEditors.TextArea = function(config){
	var field = new Ext.ux.CustomEditors.TextAreaField(this);
	var callback = config.callback;
	Ext.ux.CustomEditors.TextArea.superclass.constructor.call(this, field, config);

}
Ext.extend(Ext.ux.CustomEditors.TextArea, Ext.grid.GridEditor, {	
	animateTarget:null,
	startEdit: function (el, value) {
		if (this.editing) {
			this.completeEdit();
		}
		this.boundEl = Ext.get(el);
		this.animateTarget = el;
		var v = value !== undefined ? value : this.boundEl.dom.innerHTML;
		if (this.fireEvent("beforestartedit", this, this.boundEl, v) === false) {
			return;
		}
		this.startValue = v;
		this.setValue(v);
		this.editing = true;
		this.allowBlur = true;
		this.field.openWin({title:this.record.id});
            
	}, 
	setValue: function (val) {
		this.field.setValue(val);
	},
	getValue: function () {
		var val = this.field.getValue();
		if (val == null)
			val = this.startValue;
		if(typeof(this.callback)=='string'){//执行回调函数
			eval(this.callback+'(val)');
		}		
		return val;	
	}
	

});
