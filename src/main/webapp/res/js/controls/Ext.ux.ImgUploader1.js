Ext.ux.ImgUploader1=Ext.extend(Ext.form.Field, {
	box: null
	, major: null
	, name: null
	,urlCombo:null
	,posCombo:null
	,uploadUrl:"../upload!img.jhtml"
	, majorClass: "majorClass"
	, initComponent: function () {
	    Ext.ux.ImgUploader1.superclass.initComponent.call(this);
	    this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name||"" };
	}
	, uploadSuccess: function (file, message) {
    	this.createImg(message);
	}
	, uploadError: function (file, message) {
	    Ext.Msg.alert("上传失败", "文件：【" + file.fileName + "】未能成功上传，请重试。原因：" + message);
	}
	/*
	, uploadComplete: function (upldr, args) {
	    upldr.startUpload();
	}
	*/
	, createUploader: function (file) {
	    var uploader = function (file) {
	        this.file = file;
	        var xhr = this.xhr = new XMLHttpRequest();
	        var t = this;
	        xhr.onerror = function (evt) {
	            t.onError(evt);
	        }
	        xhr.onload = function (evt) {
	            t.onLoad(evt);
	        }
	    };
	    uploader.prototype = {
	        constructor: uploader
            , context: this
            , xhr: null
            , "file": null
            , onError: function (evt) {
                this.context.uploadError(this.file); //??????????????????????????? need test.....
            }
            , onLoad: function (evt) {
                var rs = evt.currentTarget.responseText;
                rs = Ext.decode(rs);
                if (evt.loaded == evt.total &&typeof rs=="object"&& rs.success) {
                    this.context.uploadSuccess(this.file, rs.message);
                } else
                    this.context.uploadError(this.file, rs.message);
            }
            , upload: function () {
                var xhr = this.xhr;
                var file = this.file;
                xhr.open("post", this.context.uploadUrl, true);
                xhr.setRequestHeader("X-File-Name", file.fileName);
                xhr.setRequestHeader("X-File-Size", file.fileSize);
                xhr.setRequestHeader("X-File-Type", file.type);
                var fd = new FormData();
                fd.append("filedata", file);
                
                var rtPostback=this.context.rtPostback;
                if(this.context.urlCombo){
                	rtPostback.watermarkUrl=this.context.urlCombo.getValue();
                	rtPostback.watermarkPos=this.context.posCombo.getValue();
                }
                fd.append("extradata",Ext.encode(rtPostback));
                xhr.send(fd);
            }
	    }
	    var c = this.createUploader = function (file) {
	        return new uploader(file);
	    }
	    return c(file);
	}	
	
	, createImg: function (message) {
	    new Ext.ux.ImgUploader1.ImgItem(message, this);
	    //if(this.major.getAttribute("src")=="")
	    //	this.setMajor(src);
	    this.fieldChanged();
	}

	, fieldChanged: function () {
	    var val = [];
	    var itemList = this.box.dom.childNodes;
	    var len = itemList.length;
	    var i = -1;
	    while (++i < len) {
	        val[i] = itemList[i].item.getData();
	    }
	    this.value = Ext.encode(val);
	    Ext.ux.ImgUploader1.superclass.setRawValue.call(this, this.value);
	}
	, setMajor: function (item) {
	    if (this.major)
	        this.major.container.removeClass(this.majorClass);
	    item.container.addClass(this.majorClass);
	    this.major = item;
	}
	, onRender: function (ct, position) {
	    if (!this.el) {
	        var cfg = this.getAutoCreate();

	        if (this.inputType) {
	            cfg.type = this.inputType;
	        }
	        this.el = ct.createChild(cfg, position);
	        var box=ct.createChild({ tag: "div", cls: "btnCtnr_upload"});
	        var btn=box.createChild({"tag":"input",type:"file","multiple":"true",value: "选择文件",style:"width:227px"});
	        var t=this;
	        btn.on("change",function(){
	        	var files=this.dom.files,i=files.length,len=i-1;
	        	while(i--){
	        		t.createUploader(files[len-i]).upload();
	        	}
	        });
	        var urls=this.rtPostback.watermarkUrls;
	        delete this.rtPostback.watermarkUrls;
	        if(!Ext.nore(urls)){
	        	if(typeof urls=="string")
	        		urls=Ext.decode(urls);
  				if(Object.prototype.toString.call(urls)=="[object Array]"&&urls.length>0){
  					var box2=box.createChild({style:"position:relative;margin:1em 0;"});
  					box2.createChild({"style":"position:absolute;right:0;line-height:20px;",html:"(水印路径)"});
  					var urlList=[],len=urls.length;
  					while(len--){
  						urlList.unshift([urls[len],urls[len]]);	
  					}
  					urlList.unshift(["","　"]);
  					this.urlCombo=new Ext.form.ComboBox({
  						renderTo:box2.dom,
						allowBlank:true,
						store:new Ext.data.SimpleStore({
							fields:['value','text']
							,data:urlList
						}),
						valueField : 'value',
						displayField : 'text',
						editable: true,
						forceSelection: false,
						mode:'local',
						triggerAction:'all',
						selectOnFocus:false,
						sortData:function(){}
  					});
  					var pos=this.rtPostback.watermarkPosizions,posList=[];
  					delete this.rtPostback.watermarkPosizions;
  					var fullPos=Ext.listPosition;
		        	if(typeof pos=="string")
		        		pos=Ext.decode(pos);
  					if(Object.prototype.toString.call(pos)=="[object Array]"&&pos.length>0){
  						var len=pos.length;
						var i,len2=i=fullPos.length;
 						while(len--){
  							while(i--){
  								if(pos[len]==fullPos[i][0])
  									posList.unshift(fullPos[i]);
  							}
  							i=len2;
  						}
  					}
  					if(posList.length==0)
  						posList=fullPos;
  					var box2=box.createChild({style:"position:relative;"});
   					box2.createChild({"style":"position:absolute;right:0;line-height:20px;",html:"(水印位置)"});
  					this.posCombo=new Ext.form.ComboBox({
  						renderTo:box2.dom,
						store:new Ext.data.SimpleStore({
							fields:['value','text']
							,data:posList
						}),
						valueField : 'value',
						displayField : 'text',
						editable: false,
						forceSelection: true,
						mode:'local',
						triggerAction:'all',
						selectOnFocus:false,
						sortData:function(){},
						value:pos[0]
  					}); 	
  				}
	        }
	        
	        box = ct.createChild({ cls: "imgDisplayBox" });
	        this.box = box.createChild({ tag: "ul" });
	    }
	    if (this.tabIndex !== undefined) {
	        this.el.dom.setAttribute('tabIndex', this.tabIndex);
	    }

	    Ext.ux.ImgUploader1.superclass.onRender.call(this, ct, position);
	}


})

Ext.ux.ImgUploader1.ImgItem = function (data, uploader) {
	data=Ext.decode(data);
    this.uploader = uploader;
    this.pathData = data;
    this.init(data.bigDimen);
}
Ext.ux.ImgUploader1.ImgItem.prototype = {
    container: null
	, src: null
	, index: null
	, comment: null
	, link: null
	, isMajor: false
	,thumbs:null
	, uploader: null
	, init: function (src) {
	    var ctnr = this.container = this.uploader.box.createChild({ tag: "li" });
	    ctnr.dom.item = this;

	    var div = ctnr.createChild({ cls: "imgItem_clm" });
	    this.index = div.createChild({ tag: "input", "cls": "imgItem_index", value: this.uploader.box.dom.childNodes.length });

	    div = ctnr.createChild({ cls: "imgItem_clm" });
	    div.createChild({ tag: "img", "src": src, "cls": "imgItem_clm_Image" });
	    div = ctnr.createChild({ cls: "imgItem_clm" });
	    this.comment = div.createChild({ tag: "textarea", "cls": "imgItem_comment" });
	    div.createChild({ tag: "br" });

	    div.createChild({ tag: "span", "html": "链接  " });
	    this.link = div.createChild({ tag: "input", "type": "text", "cls": "imgItem_link" });

	    div = ctnr.createChild({ cls: "imgItem_clm" });
	    this.isMajor = div.createChild({ tag: "input", "type": "radio", "name": "majorImg" });
	    div.createChild({ tag: "span", "html": "主图" });
	    div.createChild({ tag: "br" });

	    var btnRemove = div.createChild({ tag: "input", "type": "button", "value": "移除", cls: "imgItem_remove" });

	    var id = "id" + new Date().valueOf();

	    var t = this;
	    btnRemove.on("click", function () {
	        t.container.remove();
	        t.uploader.fieldChanged();
	    });

	    var fields = [this.index, this.comment, this.link, this.isMajor];
	    var len = fields.length, i = -1;
	    while (++i < len) {
	        fields[i].on("change", function () {
	            t.uploader.fieldChanged();
	        });
	    }

	    this.isMajor.on("click", function () { t.uploader.setMajor(t); });
	    this.isMajor.dom.checked = true;
	    this.uploader.setMajor(this);
	}
	, getData: function () {
	    var d = {};
	    d.pathData = this.pathData;
	    d.index = this.index.dom.value;
	    d.comment = this.comment.dom.value;
	    d.link = this.link.dom.value;
	    d.isMajor = this.isMajor.dom.checked;
	    
	    return d;
	}
}

Ext.reg('imguploader', Ext.ux.ImgUploader1);


