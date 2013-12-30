/**
 * 经过颜色渲染美化的代码编辑器
 * @class Ext.ux.TextAreaPretty
 * @extends Ext.form.TextArea
 * @author cici
 * @date:2012-11-27
 * @rely:Ext.ux.TextAreaPretty.css|/res/lib/prettify.css|Ext.ux.TextAreaPretty.js|/res/lib/prettify.js
 */
Ext.ux.TextAreaPretty = function (config) {
	if(!Ext.nore(config.regex))
		config.regex=Ext.decode(config.regex);		
	Ext.ux.TextAreaPretty.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.TextAreaPretty, Ext.form.TextArea, {
	/****/
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		Ext.ux.TextAreaPretty.superclass.initComponent.call(this);
	},
	onRender:function(ct,pos){
		var ct = ct.createChild({
			cls:'x-form-codeeditor-controls x-form-codeeditor-editor'
		});
		this.cls = "prettytextarea";
		Ext.ux.TextAreaPretty.superclass.onRender.call(this,ct,pos);	
		this.el.removeClass(['x-form-textarea','x-form-field']);

		this.codeEl = ct.createChild({//代码容器
			tag:'pre',
			cls:'prettyprint linenums',
			id:this.id + "_code"
		});
		this.el.on('keyup',this.prettyPrint,this);
		this.el.on("scroll",function(){
			this.codeEl.dom.scrollTop = this.el.dom.scrollTop;
		},this);
		this.on("resize",function(obj,adjWidth,adjHeight){
			this.prettyPrint();
		},this);
		//this.el.on("mousemove",this.prettyPrint,this);
		//this.el.on("mouseup",this.prettyPrint,this);
	},
	prettyPrint:function(){
		this.codeEl.dom.style.width=(parseInt(this.el.dom.style.width)+40) + "px";
		this.codeEl.dom.style.height=(parseInt(this.el.dom.style.height)-0) + "px";
		
		this.codeEl.update(Ext.util.Format.htmlEncode(this.getValue()));
		prettyPrint();
	},
	setValue:function(v){
		Ext.ux.TextAreaPretty.superclass.setValue.call(this, v);
		
	}
});
Ext.reg('textarea_pretty', Ext.ux.TextAreaPretty);
