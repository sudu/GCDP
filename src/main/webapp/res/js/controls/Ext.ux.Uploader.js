(function () {
    var isHtml5 = false;

    Ext.onReady(function () {
        var file = document.createElement("input");
        file.setAttribute("type", "file");
        if (!(isHtml5 = file.files)) {
            Ext.loadScript("./../../res/js/swfupload.js");
        }
    })

    Ext.ux.Uploader = function (config) {
        Ext.ux.Uploader.superclass.constructor.call(this, config);
    }

    Ext.extend(Ext.ux.Uploader, Ext.Component, {
    	//--->
        upload_url: ""
        ,file_size_limit:2
        //<-------
	    , previewBox: null
        , msgBox: null
        , uploader: null
        , btnMask: null
        ,pathPrefix:""
        , allFiles: []
        , failedFiles: []
        , allImageItems: []
        , imgIndex: 0
        , chaining: false
        , classes: {
            ctnr: "uploader_ctnr"
            , previewBox: "uploader_previewBox"
            , left: "uploader_left"
            , btnCtnr: "uploader_btnCtnr"
            , msgBox: "uploader_msgBox"
            , altImgItem: "uploader_imgItem_alt"
            , nohtml5: "nohtml5"
            , progressBar: "uploader_bar"
            , success: "uploader_success"
            , failure: "uploader_failure"
            , mark: "uploader_mark"
            , btnMask: "uploader_btnMask"
            , btnStop: "uploader_stop"
            , selectBtnCtnr: "uploader_selectBtnCtnr"
        }
        , previewTip: "预览图片"
        , progressIdPrefix: "progress_"
        , imgItemTemplate: (function () {
            var tplStr = "\
            <div class='uploader_imgItem'>\
                <div class='uploader_img'><img src='{0}' /></div>\
                <div class='uploader_imgItem_right'>\
                    <div class='uploader_delete' title='删除'></div>\
                    <div class='uploader_upload' title='上传'></div>\
                    <div class='uploader_size'>Size:{2}</div>\
                    <div class='uploader_title'>文件名:{1}</div>\
                    <div class='uploader_progress' id='{3}'>\
                        <div class='uploader_bar'></div>\
                        <div class='uploader_mark'></div>\
                    </div>\
                </div>\
            </div>";
            var tpl = new Ext.Template();
            tpl.set(tplStr, true);
            return tpl;
        })()
	    , swfConfig: {
	        flash_url: "./../../res/js/swfupload.swf"
 		    , upload_url: ""
		    , file_size_limit: "2 MB",
	        file_types: "*.*",
	        file_types_description: "All Files",
	        file_upload_limit: 100,
	        file_queue_limit: 0,
	        debug: false,
	        requeue_on_error: true,
	        button_placeholder_id: "spanButtonPlaceHolder" + new Date().valueOf(),
	        file_post_name: "filedata",
	        button_text: '<span class="textStle">选择文件</span>',
	        button_window_mode:"transparent",
	        button_width: 68,
	        button_height: 22,
	        button_text_style: ".textStle{font-size:12;text-align:center;}",
	        button_cursor: -2
	    }
	    , initComponent: function () {
	        Ext.ux.Uploader.superclass.initComponent.call(this);
	        if (!isHtml5) {
	            this.swfConfig = Ext.applyDeep({}, this.swfConfig);
	            this.file_size_limit=this.file_size_limit+"MB";
	            Ext.applyLmt(this.swfConfig, this);
	            var inncfg = this.swfConfig;
	            var t = this;
	            
	            inncfg.file_queue_error_handler = function (file,code,msg) {
	            	t.inform(file.name+":"+msg);
	            }
	            inncfg.file_queued_handler = function (file) {
	            	Ext.trace("file",true);
	                t.preview(file);
	            }
	            inncfg.upload_start_handler = function (file) {
	                t.uploadStart(file);
	            }
	            inncfg.upload_progress_handler = function (file, current, total) {
	                t.uploadProgress(file, current, total);
	            }
	            inncfg.upload_error_handler = function (file, error, message) {
	                t.uploadError(file, message);
	            }
	            inncfg.upload_success_handler = function (file, data, response) {
	                t.uploadSuccess(file);
	            }
	            inncfg.upload_complete_handler = function () {
	                t.uploadComplete();
	            }
	        } else {
	            this.previewTip += ";你也可以直接从文件系统拖拽至此";
	        }
	    }
	    , onRender: function (ct, position) {
	    	var cls= this.classes.ctnr +(isHtml5 ? "":" "+ this.classes.nohtml5);
	        var ctnr = this.el = ct.createChild({ cls: cls });
	        var left = ctnr.createChild({ cls: this.classes.left });
	        var btnCtnr = left.createChild({ cls: this.classes.btnCtnr });
	        this.btnMask = btnCtnr.createChild({ cls: this.classes.btnMask });
	              
	        
	        var boxRgt = ctnr.createChild({ cls: this.classes.previewBox, title: this.previewTip });        
            this.previewBox = boxRgt.createChild({ tag: "ul" });
        
	        var t = this;
	        var box = btnCtnr.createChild({ tag: "span", cls: this.classes.selectBtnCtnr });
	        
	        if (!isHtml5) {
	  		      box.createChild({ tag: "input", type: "button"});
	            box.createChild({ tag: "span", id: this.swfConfig.button_placeholder_id });
	            (function () {
	                if (typeof (SWFUpload) == "undefined") {
	                    window.setTimeout(arguments.callee, 100);
	                } else {
	                    t.uploader = new SWFUpload(t.swfConfig);
	                }
	            })();
	        } else {
	            boxRgt.dom.addEventListener("dragover",function(evt){
	            	evt.stopPropagation();
					evt.preventDefault();
	            },false);
	           boxRgt.dom.addEventListener("drop",function(evt){
	            	evt.stopPropagation();
					evt.preventDefault();
	            	var files = evt.dataTransfer.files;
	                var len = files.length;
	                for (var i = 0; i < len; i++)
	                    t.preview(files[i]);
	            },false);	
           
           		var btn = box.createChild({ tag: "input", type: "file", multiple: "true" });
	            btn.on("change", function () {
	                var files = this.dom.files;
	                var len = files.length;
	                for (var i = 0; i < len; i++){
	                    t.preview(files[i]);
	                }
	            });

	            this.uploader = (function () {
	                var xhr = new XMLHttpRequest();
	                xhr.upload.addEventListener("loadstart", function (evt) {
	                    t.uploadStart(t.currentFile);
	                }, false);
	                xhr.upload.onprogress = function (evt) {
	                    if (evt.lengthComputable) {
	                        t.uploadProgress(t.currentFile, evt.loaded, evt.total);
	                    }
	                };
	                xhr.upload.onerror = function (evt) {
	                    t.uploadError(t.currentFile);//??????????????????????????? need test.....
	                };
	                xhr.upload.onload = function (evt) {
	                    this.onprogress.handleEvent(evt);
	                    if(evt.loaded==evt.total)
	                    	t.uploadSuccess(t.currentFile);
	                    t.uploadComplete();                   
	                };
	                /*
	                xhr.upload.onloadend = function (evt) {
	                    Ext.trace("end", true);
	                    t.uploadComplete();
	                };
	                */
	                return {
	                    upload: function (file) {
	                        t.currentFile = file;
	                        xhr.open("post", t.upload_url, true);
	                        xhr.setRequestHeader("Content-Type", "multipart/form-data");
	                        xhr.setRequestHeader("X-File-Name", file.fileName);
	                        xhr.setRequestHeader("X-File-Size", file.fileSize);
	                        xhr.setRequestHeader("X-File-Type", file.type);
	                        xhr.send(file);
	                    }
                        , cancel: function () {
                            xhr.abort();
                        }
	                };
	            })();
	        }


	        var btn = btnCtnr.createChild({ tag: "input", type: "button", value: "全部清空" });
	        btn.on("click", this.clearImgs, this);

	        var btn = btnCtnr.createChild({ tag: "input", type: "button", value: "停止上传", cls: this.classes.btnStop });
	        btn.on("click", this.cancelUpload, this);

	        var btn = btnCtnr.createChild({ tag: "input", type: "button", value: "全部上传" });
	        btn.on("click", this.startUpload, this);

	        this.msgBox = left.createChild({ cls: this.classes.msgBox });
	        
            
            Ext.ux.Uploader.superclass.onRender.call(this, ct, position);
	    }
	    ,inform:function(msg){
	    	this.msgBox.createChild({ tag:"p",html:msg});	
	    }
	    
        , clearImgs: function () {
            this.imgIndex = 0;
            this.allFiles.length = 0;
            this.failedFiles.length = 0;
            this.allImageItems.length = 0;
            this.previewBox.update("");
        }
        , reorderImg: function () {
            var items = this.allImageItems;
            var len = this.imgIndex = items.length;
            for (var i = 0; i < len; i++) {
                if (i % 2)
                    items[i].addClass(this.classes.altImgItem);
                else
                    items[i].removeClass(this.classes.altImgItem);
            }
        }
        , preview: function (file) {//file when html5, or id;
        	var size=file.size=file.size/1024;
        	if(isHtml5&&size>this.size*1024)
        		return this.inform(file.name+":"+"File size exceeds allowed limit.");
            var data = [null, file.name, file.size];
            if (!file.id) {//html5
                if (file.type && typeof FileReader != "undefined" && (/image/i).test(file.type)) {
                    var reader = new FileReader();
                    reader.onload = (function (d) {
                        return function (evt) {
                            d[0] = evt.target.result;
                        }
                    })(data);
                    reader.readAsDataURL(file);
                } else {
                    data[0] = " ";
                }
                var id = file.progressId = this.progressIdPrefix + (new Date()).valueOf();
                data[3] = id;
            } else
                data[3] = data[0] = this.getProgressId(file);
            var t = this;
            (function () {
                if (!data[0])
                    return window.setTimeout(arguments.callee, 100);

                var item = t.imgItemTemplate.append(t.previewBox, data, true);
                t.allImageItems.push(item);
                (t.imgIndex % 2 && item.addClass(t.classes.altImgItem));
                item.child(".uploader_delete").on("click", function () {
                    item.remove();
                    t.allFiles.remove(file);
                    t.allImageItems.remove(item);
                    t.reorderImg();
                });
                item.child(".uploader_upload").on("click", function () {
                    t.upload(file);
                });
                t.imgIndex++;
            })();
            this.allFiles.push(file);
        }
        , getProgressId: function (file) {
            return file.progressId || this.progressIdPrefix + file.id;
        }
        , removeFile: function (file) {
            var files = this.allFiles;
            var len = files.length;
            for (var i = 0; i < len; i++) {
                if (files[i].id == file.id) {
                    files.splice(i, 1);
                    break;
                }
            }
        }
	    , startUpload: function () {
	        if (this.allFiles.length) {
	            this.chaining = true;
	            this.btnMask.show();
	            this.upload();
	        } else {
	            this.inform("未选择文件");
	        }
	    }
	    , cancelUpload: function () {
	        this.btnMask.hide();
	        isHtml5 ? this.uploader.cancel():this.uploader.stopUpload();
	    }
        , upload: function (file) {
            isHtml5 ? this.uploadHtml5(file) : this.uploader.startUpload((file || this.allFiles[0]).id);
        }
        , uploadHtml5: function (file) {
            file = file || this.allFiles[0];
            this.uploader.upload(file);
        }
        , uploadProgress: function (file, current, total) {
            Ext.get(this.getProgressId(file)).child("." + this.classes.progressBar).setStyle("width", current / total * 100 + "%");
        }
	    , uploadStart: function (file) {
	        Ext.get(this.getProgressId(file)).child("." + this.classes.progressBar).setStyle("width", 0);
	    }
	    , uploadSuccess: function (file) {
	        this.removeFile(file);
	        var mark = Ext.get(this.getProgressId(file)).child("." + this.classes.mark);
	        mark.addClass(this.classes.success);
	        mark.removeClass(this.classes.failure);
	    }
	    , uploadError: function (file, msg) {
	        var mark = Ext.get(this.getProgressId(file)).child("." + this.classes.mark);
	        mark.addClass(this.classes.failure);
	        mark.removeClass(this.classes.success);
	        mark.dom.setAttribute("title", msg);
	        if (this.chaining) {
	            this.removeFile(file);
	            this.failedFiles.push(file);
	        }
	    }
	    , uploadComplete: function () {
	        if (this.chaining) {
	            if (this.allFiles.length)
	                this.upload();
	            else {
	                this.inform("上传已全部完成。");
	                this.allFiles = this.failedFiles;
	                this.failedFiles = [];
	                this.btnMask.hide();
	            }
	        } else
	            this.inform("上传已完成。");
	    }

    })


    Ext.reg('uploader', Ext.ux.Uploader);


})();