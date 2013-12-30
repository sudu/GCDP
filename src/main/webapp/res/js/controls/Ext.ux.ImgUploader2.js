(function () {

    Ext.ux.ImgUploader2 = Ext.extend(Ext.form.Field, {
        //--->
        uploadUrl: "../upload.jhtml"
        , size: 2
        //<-------
	    , previewBox: null
        , btnCtnr: null
        , btn: null
        , msgBox: null
        ,pathPrefix:""
        , allFiles: []
        , defaultNote: "图注"
        , classes: {
            ctnr: "uploader2_ctnr"
            , previewBox: "uploader2_previewBox"
            , msgBox: "uploader2_msgBox"
            , itemLoaded: "uploader2_itemLoaded"
            , closer: "uploader2_closer"
            , btnFill: "uploader2_ctnr_btnFill"
        }
        , tip: "+请拖拽图片至此"
        //, btnText: "上传图片(可拖拽)"
        //, btnText2: "添加图片(可拖拽)"
        , imgItemTemplate: (function () {
            var tplStr = '<dd class="item">\
				<figure>\
					<img class="image" alt="上传中..." src="{url}" />\
					<button class="button uploader2_closer">\
					</button>\
					<figcaption>\
						<textarea>{title}</textarea>\
					</figcaption>\
				</figure>\
			</dd>'
            var tpl = new Ext.Template();
            tpl.set(tplStr, true);
            return tpl;
        })()
		, initComponent: function () {
		    Ext.ux.ImgUploader2.superclass.initComponent.call(this);
	
		    this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name };
		}
        , onRender: function (ct, position) {
	        Ext.ux.ImgUploader2.superclass.onRender.call(this, ct, position);


	        var ctnr = ct.createChild({ tag: "section", cls: this.classes.ctnr, style: this.style });
	        ctnr.setWidth(this.width);
	        ctnr.setHeight(this.height);
	        this.msgBox = ctnr.createChild({ cls: this.classes.msgBox });

	        var box = this.previewBox = ctnr.createChild({ tag: "dl", cls: this.classes.previewBox, title: this.tip });
	        var btn = this.btn = box.createChild({ tag: "span", cls: "button", html: this.tip });
	        btn.enableDisplayMode();

	        btn = this.btnFill = ctnr.createChild({ tag: "span", cls: "button " + this.classes.btnFill, html: "√使用第一张图片的图注" });
	        btn.on("click", this.fillNote, this);
	        /*
	        var input = btnCtnr.createChild({ tag: "input", type: "file", multiple: "true" });
	        input.on("change", function () {
	        var files = this.dom.files;
	        var len = files.length;
	        for (var i = 0; i < len; i++) {
	        t.upload(files[i]);
	        }
	        });
	        */
	        var t = this;
	        ctnr.dom.addEventListener("dragover", function (evt) {
	            evt.stopPropagation();
	            evt.preventDefault();
	        }, false);
	        ctnr.dom.addEventListener("drop", function (evt) {
	            evt.stopPropagation();
	            evt.preventDefault();
	            var files = evt.dataTransfer.files;
	            var len = files.length;
	            for (var i = 0; i < len; i++)
	                t.upload(files[i]);
	        }, false);
	        
	        this.initOutlook();
	    }
		, afterRender: function () {
		    Ext.ux.ImgUploader2.superclass.afterRender.call(this);

		    var t = this;
		    this.el.up("form").on("submit", function () {
		        t.storeValue();
		    });
		    
		}
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
                , loaded: false
                , onError: function (evt) {
                    this.context.uploadError(this.file); //??????????????????????????? need test.....
                }
                , onLoad: function (evt) {
                    var rs = evt.currentTarget.responseText;
                    rs = Ext.decode(rs);
                    if (evt.loaded == evt.total &&typeof rs=="object"&& rs.success) {
                        this.loaded = true;
                        this.context.uploadSuccess(this.file, rs.message);
                    } else
                        this.context.uploadError(this.file, rs.message);
                }
	            , upload: function () {
	                var xhr = this.xhr;
	                var file = this.file;
	                xhr.open("post", this.context.uploadUrl, true);
	                //xhr.setRequestHeader("Content-Type", "multipart/form-data");
	                xhr.setRequestHeader("X-File-Name", file.fileName);
	                xhr.setRequestHeader("X-File-Size", file.fileSize);
	                xhr.setRequestHeader("X-File-Type", file.type);
	                var fd = new FormData();
	                fd.append("filedata", file);
	                xhr.send(fd);
	            }
                , cancel: function () {
                    this.xhr.abort();
                }
		    }
		    var c = this.createUploader = function (file) {
		        return new uploader(file);
		    }
		    return c(file);
		}
	    , inform: function (msg) {
	        this.msgBox.update(msg);
	    }
        , fillNote: function () {
            var i, len = i = this.allFiles.length;
            if (len < 1)
                return;
            var v = this.allFiles[0].title.value;
            len--;
            while (i--) {
                var f = this.allFiles[len - i];
                f.title.value = v;
            }
        }
        , upload: function (file) {
            var size = file.size = file.size / 1024;
            if (size > this.size * 1024)
                return this.inform(file.name + ":" + "File size exceeds allowed limit.");

            if (this.btn.isVisible()) {
                this.btn.hide();
            }
            var item = this.createItem(file);
            var uploader = this.createUploader(file);
            file.uploader = uploader;
            uploader.upload();
        }
        ,createItem:function(file){
            (file.title||(file.title=this.defaultNote));
            (file.url||(file.url=""));
            
            var item = this.imgItemTemplate.append(this.previewBox, file, true);
            file.item = item;
            var t = this;
            Ext.get(item.query("." + t.classes.closer)[0]).on("click", (function (file) {
                return function () {
                    t.closeItem(file);
                }
            })(file));
            file.title = item.query("textarea")[0];
            return item;
        }
        , closeItem: function (file) {
            file.item.remove();
            if (file.uploader&&!file.uploader.loaded)
                file.uploader.cancel();
            else {
                this.allFiles.splice(file.index, 1);
            }
			this.updateOutlook();
        }
	    , uploadSuccess: function (file, url) {
	        url = this.pathPrefix + url;
	        var item = file.item;
	        item.query("img")[0].setAttribute("src", url);
	        file.url = url;
	        file.index = this.allFiles.length;
	        this.allFiles.push(file);
	        item.addClass(this.classes.itemLoaded);
			this.updateOutlook();
	        if (this.allFiles.length > 1)
	            this.btnFill.dom.style.display = "inline";
	    }
	    , uploadError: function (file, msg) {
	        this.inform("上传失败。" + msg);
	        file.item.remove();
	        if (!this.allFiles.length) {
	            this.btn.show();
	        }
	    }
	    ,setValue:function(vstr) {
        	if(vstr&&!Ext.nore(vstr))
        		this.allFiles=Ext.decode(vstr);	    	
	    }
	    ,initOutlook:function(){
	    	this.updateOutlook();
           var i, len = i = this.allFiles.length;
            len--;
            while (i--) {
            	var item=this.allFiles[len-i];
            	item.index=len-i;
            	item=this.createItem(item);
 	        	item.addClass(this.classes.itemLoaded);
           }	    	
	    }
	    ,updateOutlook:function(){
            var len = this.allFiles.length;	    	
            if (!len) {
                this.btn.show();
            }else{
                this.btn.hide();
            }
            if (len < 2) {
                this.btnFill.dom.style.display = "none";
            }else{
                this.btnFill.dom.style.display = "inline";
            }
	    }
        , storeValue: function () {
            var arr = [];
            var i, len = i = this.allFiles.length;
            len--;
            while (i--) {
                var f = this.allFiles[len - i];
                arr.push({ url: f.url, title: f.title.value == this.defaultNote ? "" : f.title.value });
            }
            var v = Ext.encode(arr);
            this.constructor.superclass.setValue.call(this, v);
        }
    })


    Ext.reg('ImgUploader2', Ext.ux.ImgUploader2);


})();