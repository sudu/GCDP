Static = {
	saveType:[[1,'nosave'],[2,'db'],[3,'nosql']],
	indexType:[[0,'不检索'],[1,'索引'],[2,'索引+排序'],[3,'索引+分词'],[4,'全文索引'],[5,'特殊符号分词'],[6,'仅存储']],
	fieldType:[['INT','INT'],['FLOAT','FLOAT'],['DOUBLE','DOUBLE'],['CHAR','CHAR'],['VARCHAR','VARCHAR'],['TEXT','TEXT'],['mediumtext','mediumtext'],['DATETIME','DATETIME']],
	inputType:[[1,'可编辑'],[2,'隐藏'],[3,'只读'],[4,'不出现']],
	generalDataModel:{
		"通用":{value:{
			f_name:{value:'',lan:'字段'},
			l_columnName:{value:'列名',lan:'列名'},
			l_tpl:{value:'',lan:'列模板'},
			l_isView:{value:true,lan:'是否显示'}
		}}
	}
}

Designer = {
    saveWindow: null,
    openWindow: null,
    mainUIPanel: null,
    mainPanel: null,
    controlsBox: null, //控件库
    propGrid: null,
    centerPanel: null,
    maxUIId: 0,
	relyJSJSON:{},
	relyCSSJSON:{},
    getId: function (uitype) {
        return uitype + "_" + (++this.maxUIId);
    },
	upperFirstLetter:function(str)  
	{  
		return str.replace(/\b\w+\b/g, function(word) {  
							   return word.substring(0,1).toUpperCase( ) +  
									  word.substring(1);  
							 });  
	  
	}, 
    createdControls: {}, //创建好的控件及配置信息
    selectedObj: null,
    formId: null,
    funNames: [], //函数名数组
    events: ['onBlur', 'onChange', 'onFocus', 'onKeyDown', 'onKeyPress', 'onKeyUp', 'onClick', 'onMouseMove', 'onMouseOver', 'onMouseOut', 'onDblClick'],
    customEditors: {//可共用的属性编辑器
        TextArea: new Ext.grid.GridEditor(new Ext.form.TextArea({})),
		NumberField: new Ext.grid.GridEditor(new Ext.form.NumberField({})),
		CustomEditors_TextField_alphanum: new Ext.grid.GridEditor(new Ext.form.TextField({vtype:'alphanum'})),
        CustomEditors_TextArea: new Ext.ux.CustomEditors.TextArea({}),
		"function":new Ext.ux.CustomEditors.TextArea({}),
        CustomEditors_TextArea_cb: new Ext.ux.CustomEditors.TextArea({ callback: "Designer.setJsFunction" }),
        JsonEditor:new Ext.ux.JsonEditor({}),
        //TextFieldReadonly: new Ext.grid.GridEditor(new Ext.form.TextField({
        //    disabled: true,
        //    disabledClass: ""
        //})),
        DbType: new Ext.grid.GridEditor(new Ext.form.ComboBox({
            allowBlank: false,
            mode: 'local',
            store: new Ext.data.SimpleStore({ fields: ['value', 'text'], data: Static.fieldType }),
            valueField: 'value', //值
            displayField: 'text', //显示文本
            editable: false, //是否允许输入
            forceSelection: false, //必须选择一个选项
            triggerAction: 'all',
            selectOnFocus: false
        })),
        ComboBox_1:new Ext.grid.GridEditor(new Ext.form.ComboBox({
				allowBlank:true,
				store:new Ext.data.SimpleStore({
					fields:['value','text']
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				editable: true,//是否允许输入
				forceSelection: false,//必须选择一个选项
				mode:'local',
				triggerAction:'all',
				selectOnFocus:false,
				sortData:function(){},
				loadStore:function(d){
					this.store.loadData(d);	
				}
		})),
		Event:new Ext.grid.GridEditor(new Ext.form.ComboBox({
				allowBlank:true,
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text']
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				editable: true,//是否允许输入
				forceSelection: false,//必须选择一个选项
				mode:'local',
				triggerAction:'all',
				selectOnFocus:false,
				sortData:function(){},
				loadStore:function(d){
					this.store.loadData(d);	
				}		
		})),
		ListColumn_1:new Ext.ux.ListItemEditor(),
		ArrayEditorField:new Ext.ux.CustomEditors.ArrayEditor(),
		DimenSingle:new Ext.ux.DimenSingleEditor(),
		DimenList:new Ext.ux.DimenListEditor(),
		Array:new Ext.ux.ArrayEditor(),		
		KeyValueEditor:new Ext.ux.CustomEditors.KeyValueEditor(),	
		Position:new Ext.ux.CheckGroupEditor({dataSource:Ext.listPosition})
    },
    //提取js函数名,初始JS脚本属性改变时回调执行
    setJsFunction: function (value) {
        try {
            eval('var onloadJsFunction={' + value + '}');
            var funNames = [[' '], [' ']];
            for (var fun in onloadJsFunction) {
                if (typeof (onloadJsFunction[fun]) == 'function') {
                    funNames.push([fun, fun]);
                }
            }
            this.customEditors["Event"].field.store.loadData(funNames);
            return true;
        } catch (ex) {
            Ext.Msg.show({
                title: '错误提示',
                msg: '脚本有语法错误!<br />' + ex.toString(),
                buttons: Ext.Msg.OK,
                animEl: 'elId',
                minWidth: 420,
                icon: Ext.MessageBox.ERROR
            });
            return false;
        }
    },
    Init: function () {
        var de = Designer;
        //初始化主界面
        de.mainUIPanel = new de.mainUI();
        de.controlsBox = Ext.getCmp('controlsBox');
        de.centerPanel = Ext.getCmp('centerPanel');

        de.propGrid = Designer.CreatePropsGrid();
        /*
        for(var k in Static.zh_en){
        var value = Static.zh_en[k];
        Static.zh_en[value] = k;
        }
        //初始化属性名字典
        for(var ctrl in de.controls.ui){
        var props = de.controls.ui[ctrl].property;
        de.initEn_Zh(props);
        }
        de.initEn_Zh(de.controls.commonProperty);
        Static.zh_en.id='(id)';
        Static.zh_en['(id)']='id';
        */
        //初始化工具箱
        Designer.controls.InitControlsUI();
        Designer.initForm();
		//快捷键保存
		var docMap = new Ext.KeyMap(Ext.getDoc(), {
			key: 's',
			shift:false,
			ctrl:true,
			handler: function(key,e){
				e.preventDefault();
				Designer.btnFormSave2Click();
			}
		});			
    },
	preProcCfgForEdit:function(config){
		delete config.name;
		
		var f=config["f_name"];
		if(f!=undefined){
			var c=config["db"]={};
			c["f_name"]=f;
			delete config["f_name"];
		}
    },
    preProcCfgForOpen:function(config){
    	if(!config.listPage)
    		return;
		var lc=config.listPage.l_listCfg;
		lc=Ext.decode(lc);
    	if(Object.prototype.toString.call(lc)=="[object Array]"){
    		var len=lc.length;
    		var nlc=[];
    		while(len--){
     			var itm=lc[len];
   				var newItm=Ext.applyDeep({}, Static.generalDataModel);
    			nlc.push(newItm);
    			for(var i in newItm){
    				if(typeof newItm[i].value !="object"){
    					newItm[i].value=itm[i];	
    				}else{
    					var v=newItm[i].value
		     			for(var j in v){
							v[j].value=itm[i][j];	
		     			}
    				}
    			}
    		}
    		config.listPage.l_listCfg=Ext.encode(nlc);
    	}
    },
    initForm: function (fConfig,isEdit) {
		if(fConfig){
			var ctrls=fConfig.controls;
			delete fConfig.controls;
			(isEdit?this.preProcCfgForEdit(fConfig):this.preProcCfgForOpen(fConfig));
			if(fConfig.script)
				Designer.setJsFunction(fConfig.script.s_onloadJs);
		}
		
		var f=Designer.newForm(fConfig);
		
		(ctrls&&this.createCmp(ctrls,f,isEdit));
		Designer.mainPanel.doLayout();
		f.footer.mask();
    },
    newForm: function (formCfg) {
        var centerPanel = Designer.centerPanel;

        if (centerPanel.items) {
            for (var i in centerPanel.items.items) {
                centerPanel.remove(centerPanel.items.items[i]);
            }
        }
        //Designer.controls.ui.FormPanel.design.ui.height = centerPanel.getSize().height;
        var newForm = Designer.objFunction.createUI('FormPanel', formCfg);

        var btnCfg = formCfg ? formCfg['buttons']: 0;
        newForm.addButton({
            text: '保存',
            id: 'btn_b_save',
            type: 'button',
            cls: btnCfg && !btnCfg.b_save ? "disable-button" : ""
        });
        newForm.addButton({
            text: '保存并继续添加',
            id: 'btn_b_saveAndAdd',
            type: 'button',
            cls: btnCfg && !btnCfg.b_saveAndAdd ? "disable-button" : ""
        });		
        newForm.addButton({
            text: '保存并关闭',
            id: 'btn_b_saveAndClose',
            type: 'button',
            cls: btnCfg && !btnCfg.b_saveAndClose ? "disable-button" : ""
        });
        newForm.addButton({
            text: '预览',
            id: 'btn_b_preview',
            type: 'button',
            cls: btnCfg && !btnCfg.b_preview ? "disable-button" : ""
        });
        newForm.addButton({
            text: '关闭',
            id: 'btn_b_close',
            type: 'button',
            cls: btnCfg && !btnCfg.b_close ? "disable-button" : ""
        });
        //centerPanel.getEl().dom.innerHTML = '';

        //newForm.render('centerPanel');	    
        centerPanel.add(newForm);
        centerPanel.doLayout();
        Designer.mainPanel = newForm;
        new Ext.dd.DDTarget(newForm, 'dd'); //建立拖动目标区(这里的dd与上面拖动源的group:dd相同	
        var formEl = newForm.getEl();
        formEl.uitype = 'FormPanel';
        formEl.dom.ctrl = newForm;
        Designer.formId = newForm.id;
        Designer.objFunction.createProps(newForm.id, formCfg);
        formEl.on("click", Designer.objFunction.objClick);
        formEl.fireEvent("click", formEl);
        return newForm;
    },

	createCmp:function(ctrlList,ctnr,isEdit) {
		if(!ctrlList||ctrlList.length==0||!ctnr)
			return;
		var index=-1,len=ctrlList.length;
		while(++index<len){
			var config=ctrlList[index];
			var controls=config.controls;
			delete config.controls;//preProcCfg shouldn't run repeatedly.
			(isEdit&&this.preProcCfgForEdit(config));
			
			var ui=config.ui;
			var id=ui["id"];
			var uitype = id.split('_')[0];
			var uiobj = Designer.objFunction.createUI(uitype,config);//创建UI元素	
			ctnr.add(uiobj);
			ctnr.doLayout();
			Designer.objFunction.createSort(uiobj);		
			var isPanel = uiobj.isContainer;
			if (isPanel){
				uiobj.el.getDDs().dropDDForNew = new Ext.dd.DDTarget(uiobj, 'dd');
				uiobj.formField.dropDiv.getDDs().dropDD.disable();
			}
			uiobj.formField.uitype = uitype;
			Designer.objFunction.createProps.call(uiobj.formField,uiobj.id,config);
			uiobj.formField.on("click", Designer.objFunction.objClick);		
			
			if(controls)
				this.createCmp(controls,uiobj,isEdit);
		}
	},
    /*属性表格的定义*/
    CreatePropsGrid: function () {
        Designer.propGrid = new Ext.ux.grid.GroupPropertyGrid({
            id: "prop_grid_right",
            //hidden:true,//去掉滚动条
            title: '属性配置',
            nameWidth: 100,
            source: {},
            viewConfig: {
                forceFit: true,
                scrollOffset: 19 //scrollbars
            },
            listeners: {
                propertychange: function (source, recordId, value, oldValue) {
                    //var group = recordId.split('&&')[0];
                    var propName = recordId.split('&&')[1];
                    propName=Designer.upperFirstLetter(propName);
                    var id = source.ui.value.id.value;
                    var obj = Ext.getCmp(id);
                    if (typeof (obj['set' + propName]) == 'function') {
                        obj['set' + propName](value);
                    }

                },
                render: function (grid) {
                    grid.getColumnModel().setColumnWidth(0, 100);
                    grid.getColumnModel().setColumnWidth(1, 200);
                },
                afteredit: function (e) {
					
                    //标记跟原始属性不一致的表格
                    var cell = e.grid.getView().getCell(e.row, e.column);
                    var element = Ext.get(cell);
                    var key = e.record.id.split('&&');
                    var id = e.grid.store.data.map["ui&&id"].data.value; //获得控件id
                    var uitype = id.split('_')[0]; //获得
                    var cfg = Designer.controls.ui[uitype];
                    var originalValue = cfg.property[key[0]].value[key[1]].value; //获取默认原始值
					
					//去除字段名的所有空格
					if(key[1]==='f_name'){
						if(e.value.toLowerCase()==="id"){
							Ext.Msg.show({
							   title:'错误提示',
							   msg: '您不需要创建id字段，系统会自动为你创建的。',
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.INFO 
							});
							e.value=e.originalValue;
							return;
						}
						e.value = e.value.replace(/\s/g,"");
					}
                    if (e.value === originalValue) {
                        element.replaceClass('x-grid3-dirty-cell', '');
                    } else {
                        element.addClass('x-grid3-dirty-cell');
                    }

                    //处理form的按钮状态
                    if (key[0] == 'buttons') {
						var enName = key[1];
                        var btn = Ext.getCmp('btn_' + enName);
                        if (btn) {
                            if (e.value == true)
                                btn.getEl().replaceClass('disable-button', '');
                            else
                                btn.getEl().addClass('disable-button');
                        }
                    }
                }
            }

        });
        this.CreatePropsGrid = function () {
            return Designer.propGrid;
        };
        return Designer.propGrid;
    },
    /*
    preview:function(){
    var formConfig = Designer.formatFormCfg(Designer.getAllControls2());
    Designer.render(formConfig);
    },
    render:function(formConfig){
    var frm = formConfig.form;
    var form_win = new Ext.Window({
    title:'表单预览',
    layout:"absolute",
    modal:true,
    id:"saveFormWin",
    closeAction:'close',
    width:685,
    height:658,
    autoScroll:true,
    padding:"10px"
    });
    frm.ui.id='';
    var formPanel = new Ext.form.FormPanel(frm.ui);

    for(var i=0;i<frm.controls.length;i++){
    var item = frm.controls[i];
    item.ui.id='';
    if(item.name.indexOf('Button')==-1){
    eval('var obj = new ' + item.name + '(item.ui)');
    formPanel.add(obj);
    }else{ //按钮
    formPanel.addButton(item.ui);
    }
    }
						
    form_win.add(formPanel);		
    form_win.setPagePosition(350,150);
    form_win.setAnimateTarget('btnFormPreview');//动画效果
    form_win.show();
    //form_win.doLayout();
    },
    */
    getUitype: function (id) {
        return id.split('_')[0];
    },

    searchCtrl: function (id, ctnr) {
        (ctnr || (ctnr = this.createdControls));
        var result = { ctrl: null, parent: null };
        if (!ctnr.id)
            return result;
        if (ctnr.id == id) {
            result.ctrl = ctnr;
            return result;
        }
        var ctrls = ctnr.controls;
        var i, len = i = ctrls.length;
        while (i--) {
            if (ctrls[i].id == id) {
                result.ctrl = ctrls[i];
                result.parent = ctnr;
                break;
            }
        }
        if (!result.ctrl) {
            i = len;
            while (i--) {
                if (ctrls[i].controls) {
                    result = this.searchCtrl(id, ctrls[i]);
                    if (result.ctrl) break;
                }
            }
        }
        return result;
    },
    getCtrlIndex: function (id, ctrls) {
        var len = ctrls.length;
        while (len--) {
            if (ctrls[len].id == id)
                break;
        }
        return len;
    },
    insertCtrl: function (propsObj, ctnrId) {
        if (!ctnrId) {//must be form;
            propsObj.controls = [];
            this.createdControls = propsObj;
        } else {
            var ctnr = this.searchCtrl(ctnrId).ctrl;
            if (this.getUitype(propsObj.id) == "Panel")
                propsObj.controls = [];
            ctnr.controls.push(propsObj);
        }
    },
    removeCtrl: function (id) {
        var o = this.searchCtrl(id);
        if (o.ctrl) {
            var ctrls = o.parent.controls;
            var index = this.getCtrlIndex(id, ctrls);
            ctrls.splice(index, 1);
        }
    },
    reorderCtrl: function (tarId, prevId, ctnrId) {
        var tarObj = this.searchCtrl(tarId);
        var parent = tarObj.parent;
        if (!parent)
            Ext.Msg.alert("Error", "target can't be found:reorderCtrl()");
        var ctrls = tarCtrls = parent.controls;
        if (parent.id != ctnrId) {
            tarCtrls = this.searchCtrl(ctnrId).ctrl.controls;
        }
        var tarIndex = this.getCtrlIndex(tarId, ctrls);

        var tar = ctrls.splice(tarIndex, 1)[0];
        if (!prevId) {
            tarCtrls.unshift(tar);
        } else {
            var prevIndex = this.getCtrlIndex(prevId, tarCtrls);
            tarCtrls.splice(prevIndex + 1, 0, tar);
        }
    },

    //获取所有字段和标签
    _getField:function(ctrl,fields){
        uitype = ctrl.id.split('_')[0];
        var ui = Designer.controls.ui[uitype];
        if (ui&&ctrl.ui && ctrl.ui.db) {
	        var fc = ctrl.ui;
	        var fname = fc.db.value.f_name.value;
	        var f_saveType = fc.db.value.f_saveType.value;
	        if (f_saveType != 'nosave') {
	            var label = fc.ui.value.fieldLabel.value;
	            fields[fname]=label;
	        }   	    	
        }
    },
    getAllFields: function () {
        var fields = {};
        var de = Designer;
        for (var k = 0; k < de.createdControls.controls.length; k++) {
            var ctrl = de.createdControls.controls[k];
            var ctrls=ctrl.controls;
            if (ctrls) {
            	var len=ctrls.length;
                for (var i = 0; i < len; i++) {
                	this._getField(ctrls[i],fields);
                }
            } else {
            	this._getField(ctrl,fields);
            }
        }
        return fields;
    },
	_retrieveGroupValue:function(cfg,target){
    	for(var g in cfg){
    		var gv=cfg[g].value;
    		var tg=target[g]={};
    		for(var p in gv){
    			tg[p]=gv[p].value;
    		}
    	}		
	},
    formatFormCfg: function () {
    	var oriCfg=this.getAllControls();
    	
        var de = Designer;
        var ctrls = [];
        var formCtrl = {};
        Ext.applyDeep(formCtrl, oriCfg);
        var formConfig = {form:{}};

        var fmUitype = de.formId.split('_')[0];
        formConfig.form.name = de.controls.ui[fmUitype].design.rtName;
      
		var cfg=formCtrl.form;
		var target=formConfig.form;
		this._retrieveGroupValue(cfg,target);
    	
		if(formCtrl.form.listPage){
	        var listCfg = [];
			var listCfgStr=formCtrl.form.listPage.value.l_listCfg.value;
			if(listCfgStr){
	            //格式化listCfgStr
	            var listCfgTmp = null;
	            try {
	                listCfgTmp = Ext.util.JSON.decode(listCfgStr);
	            } catch (ex) {
	                ;
	            }
	            if (listCfgTmp != null) {
	                for (var i = 0; i < listCfgTmp.length; i++) {
	                    var lc = listCfgTmp[i];
	                    var o = {};
						this._retrieveGroupValue(lc,o);
	                    listCfg[listCfg.length] = o;
	                }
	
	            }
	       }
	     	target.listPage.l_listCfg=Ext.util.JSON.encode(listCfg);
		}
        var propsObjList = formCtrl.controls;
        var index = propsObjList.length;
        while (index--) {
            var itm = propsObjList[index];
            var ctrl = this.getControl(itm);
            ctrls.unshift(ctrl);
        }
        target.controls = ctrls;
		//处理依赖脚本配置
		var relyJSArr=[];
		var relyCSSArr=[];
		for(var js in this.relyJSJSON){
			relyJSArr.push(js);
		}
		formConfig.relyJS = relyJSArr;
		for(var css in this.relyCSSJSON){
			relyCSSArr.push(css);
		}
		formConfig.relyJS = relyJSArr;
		formConfig.relyCSS = relyCSSArr;
        return formConfig;
    },
	saveFormCfg:function(configStr){
		Ext.getBody().mask("正在提交,请稍候......");
		var params=Ext.parseQuery();
		var url = "xform!saveConfig.jhtml";
		if(typeof(configStr)=='undefined'){
			params.config = Ext.getCmp('formConfigJson').getValue();
		}else{
			params.config = configStr
		}
		Ext.Ajax.request({  
			url:url,  
			params:params,  
			method:"POST",  
			options:{},
			success:function(response,opts){
				Ext.getBody().unmask();
				var ret = Ext.util.JSON.decode(response.responseText);
				if(!ret.success){
					Ext.Msg.show({
					   title:'错误提示',
					   msg: decodeURIComponent(ret.message),
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});
				}else{
					Ext.Toast.show('提交成功',{
					   title:'提示',
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.INFO  
					});
					var previewFormWin = Ext.getCmp('previewFormWin');
					if(previewFormWin) previewFormWin.hide();
					if(top.devMgr)
						top.devMgr.initXFormPanel();
					else if(opener && opener.devMgr)
						opener.devMgr.initXFormPanel();
						
					closePage();
				}
				
			},
			failure:function(ret,opts){
				Ext.getBody().unmask();
				Ext.Msg.show({
					   title:'错误提示',
					   msg: decodeURIComponent(ret.message?ret.message:ret.statusText),
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
				});
			}		
		});	
	},
    getAllControls: function () {
        var formConfig = {
            form: {},
            controls: []
        };
        var de = Designer;
        var formCtrl = this.createdControls;
        Ext.applyDeep(formConfig.form, formCtrl.ui);
        Ext.applyDeep(formConfig.controls, formCtrl.controls);
        return formConfig;
    },

    getControl: function (propsObj) {
        var prop = this.getProperty(propsObj.ui);
        var ctrls = propsObj.controls;
        if (ctrls) {
            prop.controls = [];
            var index = ctrls.length;
            while (index--) {
                var itm = ctrls[index];
                var p = this.getControl(itm);
                prop.controls.unshift(p);
            }
        }
        return prop;
    },
    getProperty: function (props) {
        var de = Designer;
        var id = props.ui.value["id"].value;
        var uitype = this.getUitype(id);
        var ui = de.controls.ui[uitype];
		var relyJS =ui.relyJS ;
		var relyCSS =ui.relyCSS ;
        var rtName = ui.design.rtName;
        var ct = { name: rtName,ctrlName:uitype};
		if(relyJS) {
			for(var i=0;i<relyJS.length;i++){
				this.relyJSJSON[relyJS[i]] = 1;
			}	
			delete ui.relyJS;
		}
		if(relyCSS) {
			for(var i=0;i<relyCSS.length;i++){
				this.relyCSSJSON[relyCSS[i]] = 1;
			}	
			delete ui.relyCSS;
		}

        var fc = props;
		delete fc.ui.value.helpHtml;
		
        for (var g in fc) {
            ct[g] = {"function":{}};
            var val=fc[g].value;
            for (var k in val) {
				var item = fc[g].value[k];
                var value = item.value;
                if (item&&item.type == 'ComboBox') {
                    var arr = item.store.join(',').split(',');
                    var pos = arr.indexOf(value);
                    if (pos % 2 == 1) {
                        value = arr[pos - 1];
                    }
                }
				if(item&&item.type == 'function') {//如果配置为function，就需要单独提取到上一级function里，在表单渲染时再eval
					ct[g]["function"][k] = value;
				}else{
					ct[g][k] = value;
				}
            }
        }
        return ct;
    }
}

//控件库
Designer.controls = {
	cls:'controlItem',
	curCls:'controlItem current',
	baseIcon:"../res/js/ext2/resources/images/default/grid/group-by.gif",
	selObj : null,
	tools:[{
        id:'close',
        handler: function(e, target, panel){
            panel.ownerCt.remove(panel, true);
        }
    }],
	renderHtml:function(){
		var html='';
		var ui = Designer.controls.ui;
		var arr=[];
		for(var item in ui){
			var cfg=ui[item];
			cfg.property.ui.value.id = { value: "", lan: '(id)', readOnly:true };
			cfg.id = item;
			if(cfg.isTool!=false){
				var o={};			
				o.cfg=cfg;		
				arr.push(o);
			}
		}
		arr.sort(function(a,b){
			//return a.index.charCodeAt(0)-b.index.charCodeAt(0);
			return a.cfg.tip.charCodeAt(0)-b.cfg.tip.charCodeAt(0);
		});
		var len=arr.length;
		for(var i=0;i<len;i++){
			var cfg=arr[i].cfg;
			var id= cfg.id;
			html+='<div class="controlItem" title="'+cfg.text+'" id="ctrl_'+ id +'"><img title="关于" id="about_'+ id + '" src="'+(cfg.icon||this.baseIcon)+'" /><span id="proxy_'+ id +'">'+ cfg.tip +'</span></div>';
			if(!cfg.design.ui.isContainer){
				var tmp={};
				Ext.applyDeep(tmp,Designer.controls.commonProperty);
				cfg.property=Ext.applyDeep(tmp,cfg.property);
				var designUi=cfg.design.ui;
				designUi.readOnly=true;
				if(designUi.hideLabel){
					delete 	cfg.property.fieldLabel;
					delete 	cfg.property.labelStyle;
					delete 	cfg.property.hideLabel;
				}else{
					designUi.fieldLabel=designUi.fieldLabel||cfg.property.ui.value.fieldLabel.value;
				}
				if(designUi.hideNote){
					delete 	cfg.property.fieldNote;
					delete 	cfg.property.noteStyle;
					delete 	cfg.property.hideNote;
				}else{
					designUi.fieldNote=designUi.fieldNote||cfg.property.ui.value.fieldNote.value;
				}					
			}
		}
		return html;
	},
	InitControlsUI:function(){
		var p = new Ext.Panel(
			{
				html: this.renderHtml(),
				title:'常用控件',
				autoScroll:true,
				//bodyStyle:'overflow-x:hidden;overflow-y:auto;',
				border:false
			}
		);
		Designer.controlsBox.add(p);
		Designer.controlsBox.doLayout();
		this.setDD();
	},
	setDD:function(){
		for(var item in Designer.controls.ui){
			if(Designer.controls.ui[item].isTool!=false){
				var prox = new Ext.dd.DragSource('ctrl_' + item, {group:'dd'});
				prox.afterDragDrop = Designer.objFunction.afterDragDrop;
				this.bindEvent('proxy_' + item);
				this.bindEvent('ctrl_' + item);
				this.bindAboutEvent(item);
			}
		}
	},
	bindEvent:function(id){
		
		Ext.get(id).on('mousedown',function(){
			var dc = Designer.controls;
			if(dc.selObj)dc.selObj.replaceClass(dc.curCls,dc.cls);
			if(id.indexOf('proxy_')!=-1){
				this.parent().replaceClass(dc.cls,dc.curCls);
				dc.selObj = this.parent();
			}else{
				this.replaceClass(dc.cls,dc.curCls);
				dc.selObj = this;
			}	
		});		
	},
	bindAboutEvent:function(name){
		Ext.get('about_' + name).on('click',function(){
			var name = this;
			var ctrl = Designer.controls.ui[name];
			var win = new Ext.Window({
				title:'关于',
				modal:false,
				closeAction:'close',
				maximizable:true,
				width:640,
				height:500,
				buttonAlign: "center",
				layout:'fit',
				autoScroll:true,
				items:[{
					xtype:'panel',
					layout:"xform",
					bodyStyle :'padding:5px',
					border:false,
					items:[{
						xtype:'textfield',
						anchor:"98%",
						cls:'readonlyText',
						readOnly:true,
						fieldLabel :"控件名称",
						value:ctrl.tip + "(" + name +") V" + (ctrl.about?ctrl.about.version:'1.0')
					},{
						xtype:'textfield',
						anchor:"98%",
						cls:'readonlyText',
						readOnly:true,
						fieldLabel:"作者",
						value:ctrl.about?ctrl.about.author:'匿名'
					},{
						fieldLabel:"版本历史",
						xtype:'html',							
						autoHeight:true,
						html:ctrl.about?ctrl.about.update:''
					},{
						xtype:'html',
						fieldLabel:"帮助",
						html:ctrl.about?ctrl.about.help:''
					}]
				}],
				buttons:[{
					text:'关闭',
					width:100,
					handler:function(){
						this.ownerCt.close();
					}
				}]
			});
			win.show();
		},name);
	}
}


Designer.objFunction={
	afterDragDrop:function(target, e, id) {
		if (target.id != id) return;
		var targetCmp = Ext.getCmp(id) || Ext.get(id).dom.ctrl;
		var srcEl = Ext.get(this.getEl());
		var uitype = srcEl.dom.id.split("_")[1];
		var uiobj = Designer.objFunction.createUI(uitype); //创建UI元素

		if (targetCmp.isContainer && targetCmp.items.length == 0)
			targetCmp.formField.dropDiv.getDDs().dropDD.disable();
		targetCmp.add(uiobj);
		targetCmp.doLayout();
		Designer.objFunction.createSort(uiobj);
		var isPanel = uiobj.isContainer;
		if (isPanel)
			uiobj.el.getDDs().dropDDForNew = new Ext.dd.DDTarget(uiobj, 'dd');
		uiobj.formField.uitype = uitype;
		uiobj.formField.on("click", Designer.objFunction.objClick);
		uiobj.formField.fireEvent("click");
	},
	createUI: function (uitype,config) {

		var cfg=Designer.controls.ui[uitype].design;
		var oriCfg={};
		Ext.applyDeep(oriCfg,cfg);
		var ui=oriCfg.ui;
		if(config){
			var propUi = config.ui;
			for(var k in ui){
				if(propUi[k]) ui[k] = propUi[k];
			}
			ui.id=propUi.id;
			var num=ui.id.split("_")[1];
			if(!Ext.nore(num)){
				num=Number(num);
				(num>Designer.maxUIId&&(Designer.maxUIId=num));
			}
		}else{
			ui.id = Designer.getId(uitype);
		}
		var newObj;
		eval('newObj = new '+ oriCfg.name +'(ui)');	

		//newObj.on("click",Designer.objFunction.objClick);
		return newObj;	
	},	
	createProps: function (ctrlId,formCfg) {
		var uitype = Designer.getUitype(ctrlId);
		var cfg = Designer.controls.ui[uitype];
		var propSource =Ext.applyDeep({}, cfg.property);
		
		for(var i in propSource){
			var g = propSource[i].value;
			for(var j in g){
				var item = g[j];
				(formCfg&&formCfg[i]&&(formCfg[i][j]!=undefined)&&(formCfg[i][j]!=null)&&(item.value=formCfg[i][j]));
			}
		}
		propSource.ui.value.id.value=cfg.property.ui.value.id.value=ctrlId;//adjust same to avoid the red mark;
		if(!formCfg&&propSource.db && propSource.db.value.f_name)
			propSource.db.value.f_name.value=ctrlId.replace(uitype,'field');
		
		propObj = { "id": ctrlId, "ui": propSource };
		if (cfg.design.ui.isContainer)
            propObj.controls = [];
		Designer.insertCtrl(propObj, this.ctnrId);
		if (uitype != "FormPanel") {
			var closer = Ext.DomHelper.append(this, { cls: "closer" }, true);
			this.closer = closer;
			var ths = this;
			closer.on("click", function () {
				var ctnr = Ext.getCmp(ths.ctnrId);
				ctnr.remove(ths.dom.ctrl);
				Designer.removeCtrl(ths.dom.ctrl.id);
				if (ctnr.isContainer && ctnr.items.length == 0) {
					ctnr.formField.dropDiv.getDDs()["dropDD"].enable();
				}
				ctnr.fireEvent("click");
			});
		}
		return propObj;
	},	
	/*
	convertCfg2PropSource:function(formCfg){
		var propSource={};
		var ctrlType = formCfg.ui.id.split('_')[0];
		if(formCfg){
			for(var g in formCfg){
				var g_zh = Static.zh_en[g];
				if(g_zh){
					propSource[g_zh]={};
					for(var k in formCfg[g]){
						var value = formCfg[g][k];
						var k_zh = Designer.controls.ui[ctrlType].property[g_zh][k].en_ch;
						propSource[g_zh][k_zh] = value;
					}
				}
			}
		}
		return propSource;
	},
	*/
	objClick: function (ev, obj) {
		// 阻止事件的传播
		if (ev) {
			if (ev.preventDefault) ev.preventDefault();
			if (ev.stopPropagation) ev.stopPropagation();
			if (ev.stopEvent) ev.stopEvent() // preventDefault + stopPropagation
		}

		var el = this;
		var id = this.dom.ctrl.id;
		var uitype = id.split('_')[0];

		if (Designer.selectedObj) {
			if (Designer.selectedObj.dom == el.dom)
				return;
			Designer.selectedObj.removeClass('selecteditem');
			//(Designer.selectedObj.closer && Designer.selectedObj.closer.hide());
		}
		el.addClass('selecteditem');
		//(el.closer && el.closer.show());
		Designer.selectedObj = el;

		//配置属性
		var parentProps = Designer.searchCtrl(this.ctnrId).ctrl;
		var propObj = Designer.searchCtrl(id, parentProps).ctrl;
		if (!propObj){
			propObj = Designer.objFunction.createProps.call(this, id);
		}
		var propSource;

		propSource = propObj.ui;
		if(Designer.propGrid.activeEditor)
			Designer.propGrid.stopEditing();
		Designer.propGrid.setSource(propSource);
		Designer.objFunction.setCustomEditors(uitype);


		//标记属性值修改过的单元格
		var items = Designer.propGrid.store.data.items;
		var cc = propSource;
		var gridView = Designer.propGrid.getView();
		for(var p=0;p<items.length;p++){
			var cfg = Designer.controls.ui[uitype];
			var arr = items[p].id.split("&&");
			var g=arr[0],i=arr[1];
			if(cfg.property[g].value[i]){
				var originalValue = cfg.property[g].value[i].value;//获取默认原始值
				if(cc[g].value[i].value!=originalValue){
					Ext.get(gridView.getCell(p,1)).addClass('x-grid3-dirty-cell');
				}
			}
		}

		// Designer.selectedObj.clicked = true;
	},
	onSortEnd: function (drg) {
		var prev = drg.dom.previousSibling;
		Designer.reorderCtrl(drg.dom.ctrl.id, (prev && prev.ctrl && prev.ctrl.id), drg.ctnrId);
	},
	createSort: function (ctrlInst) {
		var uitype = ctrlInst.id.split('_')[0];
		var ui = Designer.controls.ui[uitype]
		if (ui.isTool == false)
			return;
		var dd = Ext.sortControl(ctrlInst.formField, "sortGroup_controls", this.onSortEnd, ctrlInst.topTrigger); //if container,drag it's header.

		if (ctrlInst.isContainer) {
			Ext.sortControl(ctrlInst.formField, "sortGroup_controls", this.onSortEnd, ctrlInst.btmTrigger); //if container,drag it's footer.
		}
		return dd;
	},
	//为各种属性类型定制值编辑
	setCustomEditors:function(uitype){	
		var editors=Designer.controls.ui[uitype].editors;
		Designer.propGrid.customEditors=editors||{};
		if(editors)
			return;
		else
			editors=Designer.controls.ui[uitype].editors=Designer.propGrid.customEditors;
		var property=Designer.controls.ui[uitype].property;
		for(var i in property){
			var g = property[i].value;
			for(var j in g){
				var p=g[j];
				var type=p.type;
				if(type){
					editors[p.lan]=Designer.customEditors[type];
				}
			}
		}
	}
		/*		
				if(Designer.propGrid.customEditors[g[j].en_ch]) break;
				//有些编辑器可以共用
				var customEd = ;
				if(customEd!=undefined){
					Designer.propGrid.customEditors[g[j].en_ch] = customEd;
				}else{
					switch(type){ //todo other case				
						case 'ComboBox':
							g[j].store.push([[' '],[' ']]);
							customEd = ;
							break;	
						case 'ListColumn'://列表项配置
							//给Designer.controls.ui.dataModel设定 ch_en的键值对
							var ch_en = {};
							var dm = Static.listItemEditor.dataModel;
							for(var d in dm){
								ch_en[d]={};
								for(var i in dm[d]){
									ch_en[d][dm[d][i].en_ch] = i;	
								}
							}
							Static.listItemEditor.ch_en = ch_en;
							customEd = 					
										
							break;
						case 'Event':
						
							customEd = 
							break;		
						default:
							customEd = null;
							break;			
					}
					if(customEd)	[g[j].en_ch] = customEd;
				}
			}
			
		}
	}
	*/

};
