/*
*空白控件
*说明：该控件只是一个容器，根据配置生成界面
*author:cici
*date:2012/11/8
*依赖：
*/

Ext.ux.BlankField = function (config) {	
	var controlsCfg = config.items;
	if(typeof controlsCfg==="string"){
		try{
			config.items = Ext.decode(controlsCfg);
		}catch(ex){
			Ext.msg.alert("控件的配置出错");
		}
	}
	Ext.ux.BlankField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.BlankField, Ext.form.Field, {
	/****/
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		Ext.ux.BlankField.superclass.initComponent.call(this);
	},
	onRender:function(ct,pos){
		Ext.ux.BlankField.superclass.onRender.call(this,ct,pos);	
		//var childConfig = Ext.isArray(this.items)?this.items:[this.items];	
		
		var itemCfg = Ext.applyDeep({},this.initialConfig);
		delete itemCfg.id;delete itemCfg.name;delete itemCfg.fieldLabel;
		
		this.itemsPanel = new Ext.Panel(itemCfg);
		this.itemsPanel.render(ct);
		this.itemsPanel.field = this;
	},	
	setValue:function(v){
		Ext.ux.BlankField.superclass.setValue.call(this,v);
	}
});
Ext.reg('blankfield', Ext.ux.BlankField);
