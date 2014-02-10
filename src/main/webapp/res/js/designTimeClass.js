Ext.namespace('Ext.de');

Ext.de.DateTime=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({tag:"img",src:"../res/img/designTime/datepicker.jpg"});
    }
});
Ext.de.HtmlEditor2=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({tag:"img",src:"../res/img/designTime/Ext.form.HtmlEditor2.jpg"});
    }
});


Ext.de.SimpleUploader=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
	    if (!this.el) {
	        this.el=ctnr.createChild({html:'<span style="margin:0 1em;font-size:14px;">选择文件</span>\
	        		<input type="file" name="filedata" /><input type="button" value="上传"/>'});
	    }
	}
});
Ext.de.Uploader=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({cls:"uploader_bg",tag:"img",src:"../res/img/designTime/uploader.jpg"});
    }
});

Ext.de.FieldSet = function (config) {
    Ext.apply(config, {
        autoHeight: true
        ,checkboxToggle:true
        , cls: "collapsiblePanel"
        ,hideLabel:true
        ,hideNote:true
        , onCheckClick: function (ev) {
            if (ev.stopPropagation) ev.stopPropagation();
            this[this.checkbox.dom.checked ? 'expand' : 'collapse']();
        }
    });
    Ext.de.FieldSet.superclass.constructor.call(this, config);
}
Ext.extend(Ext.de.FieldSet, Ext.form.FieldSet, {
    onRender: function (ctnr, pos) {
        Ext.de.FieldSet.superclass.onRender.call(this, ctnr, pos);
        this.body.addClass("collapsiblePanel_body");
        this.topTrigger = Ext.DomHelper.insertFirst(this.el, { cls: "collapsiblePanel_topTrigger" }, true);
        this.btmTrigger = this.el.createChild({ cls: "collapsiblePanel_btmTrigger" });
    }
});
Ext.reg('xfieldset', Ext.de.FieldSet);


Ext.de.ImgUploader1 = Ext.extend(Ext.form.Field, {
    initComponent: function () {
        Ext.de.ImgUploader1.superclass.initComponent.call(this);
        this.autoCreate = { tag: 'input', type: 'button' };
    }
});


Ext.de.ImgUploader2=Ext.extend(Ext.form.Field,{
	onRender:function(ct,position){
        if(!this.el){
			var html="<div class='ctr'></div><div class='tp'></div><div class='btm'></div><div class='lft'></div><div class='rgt'></div>"+
				"<div class='lftTp'></div><div class='rgtTp'></div><div class='lftBtm'></div><div class='rgtBtm'></div>";
			this.el=ct.createChild({cls:"ImgUploader2",html:html});	
        }
	}
});
Ext.de.HtmlEditor=Ext.extend(Ext.form.Field,{
	onRender:function(ct,position){
        if(!this.el){
			var html="<div class='tp'></div><div class='btm'></div><div class='lft'></div><div class='rgt'></div>"+
				"<div class='lftTp'></div><div class='rgtTp'></div><div class='lftBtm'></div><div class='rgtBtm'></div>";
			this.el=ct.createChild({cls:"htmlEditorCtnr",html:html});	
        }
	}
});

Ext.reg('xhtmleditor',Ext.de.HtmlEditor);

Ext.de.SelectableText = Ext.extend(Ext.form.TextField, {});
Ext.reg('stext', Ext.de.SelectableText);


Ext.de.CheckboxGroup=Ext.extend(Ext.form.Field, {
	dataSource:''
	,inputList:null
	,name:""
	,value:""
	,labelFirst:false
	,initComponent:function(){
		this.constructor.superclass.initComponent.call(this);
		this.setExt_dataSource_value(this.dataSource||this.data||this.source,true);
	}
	,setValue:function(val){
		if(!val)
			val="";
		else{
			try{
		    	val=eval("("+val+")");//[];	
			}catch(e){}//string
		}
		if(Object.prototype.toString.call(val)!="[object Array]")
			val=[val];
		var i=val.length;
		while(i--){
			val[i]=val[i].toString();	
		}
		var i=this.dataSource.length;
		var l=this.inputList;
		while(i--){
			var input=l[i];
			if(val.indexOf(input.value)>-1)
				input.checked=true;
			else
				input.checked=false;		
		}		
		this.value=val;
	}
	,setExt_dataSource_value:function(s,notRender){
		try{
	    	var s=eval("("+s+")");//[];	
		}catch(e){
			var s=[this.dataSource];	//string
		}

		this.dataSource=s;
		if(!notRender){
			this.container.update("");
			this.onRender(this.container);
		}
	}	
	,onRender:function(ct,pos){
		var ctnr=this.el=ct.createChild({cls:'group'});
		var s=this.dataSource,v=this.value,n=this.name;
		var len,i=len=s.length;
		this.inputList=[];
		while(i--){
			var vs=s[len-i-1];
			var cfg={tag:"input",type:"checkbox",name:n,value:vs[0]};
			(v.indexOf(vs[0])>-1&&(cfg.checked="checked"));
			var unit=ctnr.createChild({"tag":"span",cls:"inputUnit"});
			if(this.labelFirst){
				var id="input"+(++Ext.Component.AUTO_ID);
				unit.createChild({tag:"label",for:id,html:vs[1]});
				this.inputList.push(unit.createChild(cfg,null,true));
			}else{
				var input=unit.createChild(cfg);
				this.inputList.push(input.dom);				
				unit.createChild({tag:"label",for:input.id,html:vs[1]});
			}
		}
	}
});

Ext.reg("checkboxgroup",Ext.de.CheckboxGroup);



Ext.de.Hidden=Ext.extend(Ext.form.Field, {
	onRender:function(ctnr,pos){
		this.el=ctnr.createChild({tag:"div",cls:"hiddenBox",html:"Hidden(隐藏域)"});	
	}
});
Ext.reg("xhidden",Ext.de.Hidden);

Ext.de.RadioGroup=Ext.extend(Ext.form.Field, {
	dataSource:''
	,inputList:null
	,name:""
	,value:""
	,labelFirst:false
	,initComponent:function(){
		this.constructor.superclass.initComponent.call(this);
		this.setExt_dataSource_value(this.dataSource||this.data||this.source,true);
	}
	,setValue:function(val){
		var l=this.inputList;
		if(l){
			var i=l.length;
			while(i--){
				var input=l[i];
				if(val==input.value)
					return input.checked=true;		
			}		
		}
	}
	,setExt_dataSource_value:function(s,notRender){
		try{
	    	var s=eval("("+s+")");//[];	
		}catch(e){
			var s=[this.dataSource];	//string
		}

		this.dataSource=s;
		if(!notRender){
			this.container.update("");
			this.onRender(this.container);
		}
	}	
	,onRender:function(ct,pos){
		var ctnr=this.el=ct.createChild({cls:'group'});
		var s=this.dataSource,v=this.value,n=this.name;
		var len,i=len=s.length;
		this.inputList=[];
		while(i--){
			var vs=s[len-i-1];
			var cfg={tag:"input",type:"radio",name:n,value:vs[0]};
			(v==vs[0]&&(cfg.checked="checked"));
			var unit=ctnr.createChild({"tag":"span",cls:"inputUnit"});
			if(this.labelFirst){
				var id="input"+(++Ext.Component.AUTO_ID);
				unit.createChild({tag:"label",for:id,html:vs[1]});
				this.inputList.push(unit.createChild(cfg,null,true));
			}else{
				var input=unit.createChild(cfg);
				this.inputList.push(input.dom);				
				unit.createChild({tag:"label",for:input.id,html:vs[1]});
			}
		}
	}
});

Ext.reg("radiogroup",Ext.de.RadioGroup);

Ext.de.UploadField = Ext.extend(Ext.form.Field, {
    initComponent: function () {
        Ext.de.UploadField.superclass.initComponent.call(this);
        this.autoCreate = {tag:"div",style:"",html:""};
    },
	onRender:function(ctnr,pos){
		if(!this.el){
			this.el = ctnr.createChild({tag: 'input', type: 'text',cls:'x-form-text x-form-field',value:'' });	
			this.button = ctnr.createChild({tag: 'input', style:'margin-left:10px', type: 'button',value:'上传' });	
        }
		
	},
	setButton_text:function(text){
		this.button.dom.value = text;
	}
});

Ext.de.ImageCutter = Ext.extend(Ext.form.Field, {
    initComponent: function () {
        Ext.de.ImageCutter.superclass.initComponent.call(this);
        this.autoCreate = {tag:"div",style:"",html:""};
    },
	onRender:function(ctnr,pos){
		if(!this.el){
			this.el = ctnr.createChild({tag: 'input', type: 'text',cls:'x-form-text x-form-field',value:'' });	
			this.button = ctnr.createChild({tag: 'input', style:'margin-left:10px', type: 'button',value:'裁图&上传' });	
        }
	},
	setButton_text:function(text){
		this.button.dom.value = text;
	}
});

/**
 * 对于级联选择组合组件的开发形式
 * @class Ext.de.LinkageTextField
 * @extends Ext.form.FieldSet
 */
Ext.de.LinkageTextField = Ext.extend(Ext.form.FieldSet, {
	autoHeight: true,
    initComponent: function(){
    	this.init_option();
        Ext.de.LinkageTextField.superclass.initComponent.call(this);
    },
    
    //初始化组件配置
    init_option: function(){
    	this.title = this.x_fieldset_title || "";
    	
		if(this.x_direction == "Horizontal")
		{
			this.layout = "column";
		}
		
		if(this.x_container_type == "panel")
		{
			this.baseCls = "x-fieldset-noborder";
			this.title = "";
		}
		
		this.items = this.creatItems();
		
    },
    
    creatItems : function(){
		var sComboBoxData = this.select_comboBox_config(this.x_data_type);
		var aComboBox = this.trans_to_array(sComboBoxData);
		aComboBox = this.set_comboBox_column(aComboBox);
		return aComboBox;
		
    },
    
	select_comboBox_config: function(x_data_type){

		return this["x_comboBox_" + x_data_type + "_config"];
		
	},
	
	trans_to_array: function(sString){
		var sString = sString;
		
		if(typeof sString =="undefined" || sString =="")
		{
			sString = [{"fieldLabel":"选项一"},{"fieldLabel":"选项二"},{"fieldLabel":"选项三"}];
		}
		
		try{
			return Ext.isArray(sString) ? sString : Ext.decode(sString);
		}
		catch(e){
			return [{"fieldLabel":"选项一"},{"fieldLabel":"选项二"},{"fieldLabel":"选项三"}];
		}
		
		
	},    
	set_comboBox_column: function(aComboBox){
		var _this  = this; 
		Ext.each(aComboBox,function(v,i,all){
			
			if(_this.x_direction == "Horizontal")
			{
				v.xtype = "combo";
				v.width = 120;
				aComboBox[i] = {columnWidth:v.columnWidth/1,border: false,layout: 'form',labelWidth : 50,items: [v]};
			}
			else
			{
				v.xtype = "combo";
				v.width = 120;
			}
			
		});
		
		return aComboBox;
		
	}	
});

Ext.reg('dLinkageTextField', Ext.de.LinkageTextField);

/**
 * 对于上传组件（开发者模式）的开发形式
 */
Ext.de.UploadDevPanel = Ext.extend(Ext.Panel, {
	
	//设置panel的样式名称，对内部样式进行限制，避免和其他组件样式冲突。
	cls: "x_UploadDevPanel",

	autoHeight: true,
	//是否使用文件本身的路径，默认为true
	useWebkitdirectory : true,
	repath: true,

    initComponent: function(){
		
    	//初始化参数
   		this.x_init_option();
   		//初始化dataView

        Ext.de.UploadDevPanel.superclass.initComponent.call(this);

    },

    onRender: function(ct, position){

        Ext.de.UploadDevPanel.superclass.onRender.call(this, ct, position);
        
        //为了调整input path的位置，对其增加了一个外部包含
        if(this.repath)
        {
			this.x_uploadPath_box.innerHTML = '<span class="x_upload_path_intro">填写路径：</span><span class="x_UploadDev_UploadPathWrap"></span>';
			this.x_uploadPath = new Ext.form.TextField({ width: 300 , name: "", id: "",emptyText : "请输入完整的路径，如：http://v.ifeng.com/path/"});
			this.x_uploadPath.render(Ext.query(".x_UploadDev_UploadPathWrap",this.x_uploadPath_box)[0]);

        }
    },
	
    /**
     * 初始化参数，增加一些需要用到的对象。
     */
	x_init_option: function(){
		//初始文件路径为空
		this.x_base_path = "";
		this.x_uploadPath = new Ext.form.TextField({ width: 300 , name: "", id: ""});
		this.x_uploadState = document.createElement("span");
		this.x_uploadState.setAttribute("class","xUploadDev_state");
		this.x_uploadCount = document.createElement("span");
		this.x_uploadCount.setAttribute("class","xUploadDev_count");
		this.x_uploadPath_box = document.createElement("span");
		this.x_uploadPath_box.setAttribute("class","xUploadDev_pathBox");
		this.tbar = [
			{
				text: "选择文件上传",
				handler: this.x_selectFiles,
				iconCls: "file",
				scope : this
			},
			{
				text: "选择文件夹上传",
				handler: this.x_selectCatalog,
				iconCls: "catalog",
				scope: this
			},
			'->',
			'-',
			this.x_uploadPath_box,
			{
				text: "开始上传",
				handler: this.x_beginUpload,
				iconCls: "upload",
				scope: this
			}
		];
		this.bbar = [this.x_uploadCount,'->',this.x_uploadState];
		this.html = '<div class="x_uploadEmpty_box" >还没有需要上传的文件，请点击上方按钮选择或者直接将文件拖动到此处</div>';

	}

});

Ext.reg('dUploadDevPanel', Ext.de.UploadDevPanel);

Ext.de.TuiJianWei=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({tag:"img",src:"../res/img/designTime/Ext.ux.TuiJianWei.jpg"});
    }
});
Ext.reg('dtuijianwei', Ext.de.UploadDevPanel);

Ext.de.ImageCollection=Ext.extend(Ext.form.Field, {
	/****/
	buttonText:"图集编辑",
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		Ext.de.ImageCollection.superclass.initComponent.call(this);
	},
	onRender: function (ct, pos) {
		Ext.de.ImageCollection.superclass.onRender.call(this,ct,pos);	
		this.itemsPanel = new Ext.Panel({
			border:false,
			items:[{
				xtype:'button',
				text:this.buttonText
			}]
		});
		this.itemsPanel.render(ct);
		this.itemsPanel.field = this;
	},	
	setButtonText:function(text){
		this.itemsPanel.items.items[0].setText(text);
	}
});
Ext.reg('dimagecollection', Ext.de.ImageCollection);

Ext.de.BlankField = function (config) {	
	var controlsCfg = config.items;
	if(typeof controlsCfg==="string"){
		try{
			config.items = Ext.decode(controlsCfg);
		}catch(ex){
			Ext.msg.alert("控件的配置出错");
		}
	}
	Ext.de.BlankField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.de.BlankField, Ext.form.Field, {
	/****/
	childConfig:null,
	initComponent: function () {
		Ext.applyDeep(this,this.initialConfig);
		this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name || "" };
		Ext.de.BlankField.superclass.initComponent.call(this);
	},
	onRender:function(ct,pos){
		Ext.de.BlankField.superclass.onRender.call(this,ct,pos);	
		var childConfig = Ext.isArray(this.items)?this.items:[this.items];	
		
		var itemCfg = Ext.applyDeep({},this.initialConfig);
		delete itemCfg.id;delete itemCfg.name;delete itemCfg.fieldLabel;
		
		this.itemsPanel = new Ext.Panel(itemCfg);
		this.itemsPanel.render(ct);
		this.itemsPanel.field = this;
	},	
	setValue:function(v){
		Ext.de.BlankField.superclass.setValue.call(this,v);
	}
});
Ext.reg('dblankfield', Ext.de.BlankField);

Ext.de.JSONEditor=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({tag:"img",src:"../res/img/designTime/Ext.ux.JSONEditor.jpg"});
    }
});

Ext.de.ScriptEditor=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({tag:"img",src:"../res/img/designTime/Ext.ux.ScriptEditor.jpg"});
    }
});

Ext.de.SlideEditor=Ext.extend(Ext.form.Field, {
    onRender: function (ctnr, pos) {
    	this.el=ctnr.createChild({tag:"img",src:"../res/img/designTime/Ext.ux.SlideEditor.jpg"});
    }
});