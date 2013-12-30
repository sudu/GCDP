Ext.ux.HTMLField = function (config) {	
	Ext.ux.HTMLField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.HTMLField, Ext.form.Field, {
	/****/
	html:'',
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		Ext.ux.HTMLField.superclass.initComponent.call(this);
	},
	onRender:function(ct,pos){
		//Ext.ux.HTMLField.superclass.onRender.call(this,ct,pos);		
		
		this.el = ct.createChild({
			tag:'div',
			cls:this.cls?this.cls:null,
			style:'padding-left: 4px;',
			html:this.html
		});	

	},	
	setValue:function(v){
		if(this.el){
			this.html = v;
			this.el.update(v);
		}
	}
});
Ext.reg('html', Ext.ux.HTMLField);