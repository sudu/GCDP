(function(){
Ext.ux.SimpleUploader = Ext.extend(Ext.form.Field, {
    //uploadTarget: "uploadTarget"
    isMultiple:false
    ,bsurl:""
    ,isSwfPathSet:false
    , uploadUrl: "../upload.jhtml"
    ,data:[]
	, initComponent: function () {
	    Ext.ux.SimpleUploader.superclass.initComponent.call(this);
	    this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name };
	}
	,setSwfPath:function(){
		if(ZeroClipboard){
	    	ZeroClipboard.setMoviePath(this.bsurl+"ZeroClipboard.swf");
	    	this.isSwfPathSet=true;
		}
	}
, afterRender: function () {
    Ext.ux.SimpleUploader.superclass.afterRender.call(this);

    var t = this;
    this.el.up("form").on("submit", function () {
        t.storeValue();
    });
    if(typeof(ZeroClipboard)=="undefined"){
    	var bsurl=this.bsurl=Ext.getDirectory("Ext.ux.SimpleUploader.js",2);
    	Ext.loadScript(bsurl+"ZeroClipboard.js");
    }
}
,addData:function(fileName,path){
	this.data.push({url:path,name:fileName});
}
,storeValue:function(){
	this.setValue(Ext.encode(this.data));
}
, onRender: function (ct, position) {
	    if (!this.el) {
	        var t = this;
	        var cfg = this.getAutoCreate();

	        if (this.inputType) {
	            cfg.type = this.inputType;
	        }
	        this.el = ct.createChild(cfg, position);
	        var form = ct.createChild();
	        cfg={tag:"input",type:"file",value:"选择文件"};
	        (this.isMultiple&&(cfg.multiple="multiple"));
	        var fileCtrl = form.createChild(cfg);
	        fileCtrl.on("change",function(){
	    	        	if ( this.dom.files.length> 0) {
	    	        		btn.show();
	    	        	}else{
	    	        		btn.hide();
	    	        	}
    		})
	        var btn = form.createChild({ tag: "input", type: "button",style:"display:none", value: "上传" });
	        btn.on("click", function () {
	            var len=fileCtrl.dom.files.length;
	        	if ( len> 0) {
	        		btn.hide();
	        		t.data=[];
	                t.console.update("");
	            	for(var i=0;i<len;i++)
	            		t.upload(fileCtrl.dom.files[i]);
	            }
	        });
	        this.console = ct.createChild({tag:"ul"});
	    }
	    Ext.ux.SimpleUploader.superclass.onRender.call(this, ct, position);
	}
	,upload:function(file){
		new LI(file,this).start();

	}

});
Ext.reg('simpleuploader', Ext.ux.SimpleUploader);

var LI=function(file,context){
	var li=context.console.createChild({tag:"li"});
	this.context=context;
	this.file=file;
	this.input=li.createChild({ tag: "input", type: "text", value: file.name });
	this.msgBox=li.createChild({style:"display:inline-block"});
};
LI.prototype={
	file:null
	,input:null
	,msgBox:null
	,start:function(){
		this.msgBox.update("上传中...");
        var xhr = new XMLHttpRequest();
        var file=this.file;
        var t=this;
        xhr.onerror = function (evt) {
            t.onError()
        }
        xhr.onload = function (evt) {
            t.loaded(evt);
        }
        xhr.open("post", this.context.uploadUrl, true);
        xhr.setRequestHeader("X-File-Name", file.fileName);
        xhr.setRequestHeader("X-File-Size", file.fileSize);
        xhr.setRequestHeader("X-File-Type", file.type);
        var fd = new FormData();
        fd.append("filedata", file);
        xhr.send(fd);
    }
    ,onError:function(){
		this.msgBox.update("上传失败。");
		this.createResend();
    }
	, loaded: function (evt) {
	    var rs = evt.currentTarget.responseText;
	    rs = Ext.decode(rs);
	    if (evt.loaded == evt.total && typeof rs == "object" && rs.success) {
	    	var path=rs.message;
	    	this.context.addData(this.file.name,path);
	    	this.input.dom.value=path;
	        this.msgBox.update("上传成功。");
			this.createCopy();
	    } else{
	        this.msgBox.update("上传失败。原因为：" + rs.message);
			this.createResend();
	    }
	}
	,createResend:function(){
		var btn=this.msgBox.createChild({ tag: "input", type: "button", value: "重传" });
		var t=this;
		btn.on("click", function () {
			t.start();
		});
	}
	,createCopy:function(){
		var btn=this.msgBox.createChild({ tag: "input", type: "button", value: "复制" });
		var t=this;
		if(!this.context.isSwfPathSet)this.context.setSwfPath();
        var clip = new ZeroClipboard.Client();
        clip.glue(btn.dom);
        clip.addEventListener("onMouseDown",function(client){        
        	clip.setText(t.input.dom.value);
        	t.input.dom.style.borderColor="#6666ff";
    	})
        clip.addEventListener("onMouseUp",function(client){        
        	t.input.dom.style.borderColor="#888888";
    	})
    	}
};
	
})();
