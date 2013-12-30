(function () {
    (Ext.ux || (Ext.ux = {}));
    var field = Ext.createRecordsField("RecordsField_Dimen",[
	   {
	       header: "宽度",
	       dataIndex: 'width',
	       width: 150,
	       allowBlank:false,
	       vtype:"num"
	    },{
	       header: "高度",
	       width: 200,
	       dataIndex: 'height',
	       allowBlank:false,
	       vtype:"num"
	    }
	]);

    Ext.ux.DimenField=Ext.extend(field,{
	   win:{
            "width": 400
            , "height": 300
            , "closable": false
            , "modal": true
            , "buttonAlign": "center"
            ,layoutOut:"fit"
        }
		,onRender:function(ctnr,pos){
			var win=this.win=new Ext.Window(this.win);
            win.addButton("确定", this.done, this);
            win.addButton("取消", this.cancel, this);			
            win.show();
			Ext.ux.DimenField.superclass.onRender.call(this,win.body);
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
            this.setValue(this.editor.startValue);
            this.complete();
        }
        , complete: function () {
            this.editor.allowBlur = false;
            this.editor.onBlur();
        }

    });


    Ext.ux.DimenListEditor = function (config) {
        var field = new Ext.ux.DimenField({editor:this,max:this.max,min:this.min});
        Ext.ux.DimenListEditor.superclass.constructor.call(this, field, config);
    }
  	Ext.extend(Ext.ux.DimenListEditor, Ext.grid.GridEditor,{
  		max:null
  		,min:null
        ,startEdit: function (el, value) {
            if (this.editing) {
                this.completeEdit();
            }
            this.boundEl = Ext.get(el);
            var v = value !== undefined ? value : "";
            if (this.fireEvent("beforestartedit", this, this.boundEl, v) === false) {
                return;
            }
            this.startValue = v;
            this.setValue(v);
            this.editing = true;
            this.allowBlur = true;
            this.field.openWindow(this.record.id); //title set here;
        }
  	});
  	
  	
  	Ext.ux.DimenSingleEditor=Ext.extend(Ext.ux.DimenListEditor,{max:1,min:1});
})();