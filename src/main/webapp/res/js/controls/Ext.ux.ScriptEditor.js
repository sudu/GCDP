/*
*脚本编辑器
*author:cici
*date:2013/4/25
*依赖js：controls/Ext.ux.ScriptEditor.js lib/editArea/edit_area_loader.js
*依赖css：
*/

Ext.ux.ScriptEditor = function (config) {	
	if(typeof editAreaLoader =="undefined"){
		editAreaLoader=new EditAreaLoader();
	}
	if(config.debuger && typeof config.debuger =="string"){
		config.debuger = eval('(' + config.debuger + ')');
	}
	Ext.ux.ScriptEditor.superclass.constructor.call(this,Ext.apply({
		hiddenName :config.hiddenName || config.name,
		editAreaLoader:editAreaLoader
	},config));
}
Ext.extend(Ext.ux.ScriptEditor, Ext.form.TextArea, {
	/****/
	debugButtonEnable:false,
	resRoot:'../res/',
	editorIframe:null,
	scriptTplContent:'',//脚本模板
	syntax:"js",
	word_wrap:true,//默认是否换行
	allow_toggle:false,//是否需要切换编辑器按钮
    toolbar: "undo, redo, |, search, go_to_line, |,select_font,|, change_smooth_selection, highlight, reset_highlight, word_wrap,format_code, |, fullscreen",
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		
		try{
			if(this.loadedCallback && typeof this.loadedCallback =="string"){
				this.loadedCallback = eval('0,'+this.loadedCallback);
			}
		}catch(ex){}
		
		//this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		this.editorId = this.id;// + "_editor";
        if(!this.save_callback)  this.save_callback="Ext.getCmp('"+ this.id +"').saveFileHandler";
        if(!this.EA_release_callback) this.EA_release_callback ="Ext.getCmp('"+ this.id +"').releaseFileHandler";
		this.editorSetting={
			id: this.editorId,
			is_editable:!(this.readOnly===true || this.disabled===true) , 
			language:"zh",
			syntax:this.syntax,	
			is_multi_files:false,
			allow_resize:true,
			allow_toggle:this.allow_toggle,
			word_wrap:this.word_wrap,
			start_highlight:true,
			ifeng_script_debug: true,
			EA_load_callback:"Ext.getCmp('"+ this.id +"').editorLoaded",
            save_callback:this.save_callback,
            EA_release_callback:this.EA_release_callback,
			toolbar: this.toolbar
	    }
		
		Ext.ux.ScriptEditor.superclass.initComponent.call(this);

	},
	onRender:function(ct,pos){
		Ext.ux.ScriptEditor.superclass.onRender.call(this,ct,pos);		
		this.hiddenInput = this.el.dom;
		this.editAreaLoader.init(this.editorSetting); 

	},
    releaseFileHandler:function(){

        alert(this.getValue());
    },
    saveFileHandler:function(){
       alert(this.getValue());
    },
	editorLoaded:function(){
		this.editorIframe=window.frames["frame_"+this.editorId];	
		var iframeEl = Ext.fly(this.editorIframe.frameElement);		
		//创建调试按钮
		if(this.debugButtonEnable){
			var debugEl = iframeEl.insertSibling({tag:'div',cls:'cmpp-scripteditor-debug',style:"width:" + iframeEl.getWidth() +"px"},"after");
			if(!this.debuger) this.debuger = Ext.jsDebugger("../develop/",0,null,null,"form",false);
			Ext.ComponentMgr.create({
				xtype:"button",
				renderTo:debugEl,	
				text:"调试脚本",
				style:"margin-left:1em;display:inline-block;",
				listeners:{
					scope:this,
					'click':function(){
						this.debuger(this);
					}
				}
			},'panel');
		}
	
		this.setValue(this.value || this.scriptTplContent);

		this.editAreaLoader.add_event(this.editorIframe.editArea.textarea,'change',function(t){
			var _this = t;
			return function(){
				_this.syncValue();
			}
		}(this));
		window.scrollTo(0,0);//解决该控件默认获得焦点导致页面滚动条不在顶部的Bug
		if(typeof this.loadedCallback ==="function"){
			this.loadedCallback();
		}
	},
	syncValue:function(){
		var val=this.editAreaLoader.getValue(this.editorId);
		this.hiddenInput.value = val;
	},
	getValue:function(){
		this.syncValue();
		return this.hiddenInput.value ;
	},
	setValue:function(v){
		if(this.editAreaLoader) this.editAreaLoader.setValue(this.editorId,v);
		if(this.hiddenInput) this.hiddenInput.value = v;	
		Ext.ux.ScriptEditor.superclass.setValue.call(this,v);
	},
	disable:function(){
		editAreaLoader.execCommand(this.editorId, 'set_editable',false);
		Ext.ux.ScriptEditor.superclass.disable.call(this);
	},
	enable:function(){
		editAreaLoader.execCommand(this.editorId, 'set_editable',true);
		Ext.ux.ScriptEditor.superclass.enable.call(this);
	}
	
});
Ext.reg('scripteditor', Ext.ux.ScriptEditor);
