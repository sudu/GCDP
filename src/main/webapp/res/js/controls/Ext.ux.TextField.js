Ext.ux.TextField = function (config) {
	if(!Ext.nore(config.regex))
		config.regex=Ext.decode(config.regex);
	Ext.ux.TextField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.TextField, Ext.form.TextField, {
	onRender:function(ct,pos){
		Ext.ux.TextField.superclass.onRender.call(this,ct,pos);		
		(this.initialConfig.readOnly&&(this.el.addClass(" readOnly")));
	}
});
Ext.reg('xTextField', Ext.ux.TextField);