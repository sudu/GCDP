(function(){
	Ext.createWinEditor=function(cfg){
		var name=cfg.name
		var baseField=cfg.baseField||Ext.form.Field;
		var width=cfg.width||400;
		var height=cfg.height||300;
		
		var fieldCstr=Ext.ux[name+"Field"]= Ext.extend(baseField,{//Ext.ux.nameField;
		   win:{
	            "width": width
	            , "height": height
	            , "closable": false
	            , "modal": true
	            , "buttonAlign": "center"
	            ,layout:"fit"
	        }
			,onRender:function(ctnr,pos){
				var win=this.win=new Ext.Window(this.win);
	            win.addButton("确定", this.done, this);
	            win.addButton("取消", this.cancel, this);			
	            win.show();
				fieldCstr.superclass.onRender.call(this,win.body);
			}
	        , openWindow: function (title) {
	            this.win.setTitle(title);
	            this.win.show();
	        }
	        , done: function () {
	            this.win.hide();
	            this.complete();
	        }
	        , cancel: function () {
	            this.win.hide();
	            this.editor.restoreValue();
	            this.complete();
	        }
	        , complete: function () {
	            this.editor.allowBlur = false;
	            this.editor.onBlur();
	        }
	    });
		
	    var override={
	    	isCanceled:false
	        ,startEdit: function (el, value,title) {
	            if (this.editing) {
	                this.completeEdit();
	            }
	            this.isCanceled=false;
	            this.boundEl = Ext.get(el);
	            var v = value !== undefined ? value : "";
	            if (this.fireEvent("beforestartedit", this, this.boundEl, v) === false) {
	                return;
	            }
	            this.startValue = v;
	            this.setValue(v);
	            this.editing = true;
	            this.allowBlur = true;
	            this.field.openWindow(title||(this.record&&this.record.id)); //title set here;
	        }
	  		,restoreValue:function(){
	            this.isCanceled=true;
	            this.setValue(this.startValue);
	  		}	
	    }
	    if(cfg.override){
	    	Ext.apply(override,cfg.override);	
	    }
	    
	    var editorCstr=Ext.ux[name+"Editor"] = function (config) {
	        var field = new fieldCstr({editor:this});
	        editorCstr.superclass.constructor.call(this, field, config);
	    }
	  	Ext.extend(editorCstr, Ext.grid.GridEditor,override);	
	  	
	  	var cstr=cfg.cstr;
	  	if(cstr){
	  		Ext.ux[name+"Editor"] =cstr;
	  		Ext.extend(cstr,editorCstr);	
	  		editorCstr=cstr;
	  	}
	  	
	  	return editorCstr;
	}
	
	

	
})();