 <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>表单配置修改</title>
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="./../res/js/ext2/resources/css/patch.css" />
	<style>
		.addField{background:url("./../res/js/ext2/resources/images/default/dd/drop-add.gif") left  no-repeat !important;}
		.delField{background:url("./../res/js/ext2/resources/images/default/my/del-form.gif") left  no-repeat !important;}
		.saveField{background:url("./../res/js/ext2/resources/images/default/my/save.gif") left  no-repeat !important;}
		.deleteButton{background:url("../res/img/runTime/delete1.gif") left  no-repeat !important;width:16px;height:16px;cursor:pointer;}
	</style>
 	<script type="text/javascript" src="./../res/js/ext2/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="./../res/js/ext2/ext-all-debug.js"></script>
	<script type="text/javascript" src="./../res/js/ext2/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="./../res/js/ext_base_extension.js"></script> 
	<script>
	var formId__ = #{formId!0};
	var nodeId__ = #{nodeId!0};
	var formConfig__= ${formConfig};//格式如下
	var formName__ = "${formName}";
	var powerPath__= '${(powerPath?js_string)!""}';
	
	if(!formConfig__.tableBak)formConfig__.tableBak={enableBak:false,recordCount:2000000,dateFieldName:''};
	function parseConfig(){
		var dataArr=formConfig__.fieldsConfig.fieldsConfig;
		var newDataArr=[];
		var fieldsArr = formConfig__.fieldsConfig.fields;
		var newFieldsArr = fieldsArr.join(',').split(',');
		for(var i=0;i<dataArr.length;i++){
			var saveType = parseInt(dataArr[i].f_saveType );
			if(saveType>1){
				dataArr[i].f_saveType = saveType;
				dataArr[i].indexType = parseInt(dataArr[i].indexType );
				var f_name = dataArr[i].f_name;
				var pos = newFieldsArr.indexOf(f_name);
				if(pos!=-1 && pos%2==0){
					dataArr[i].f_title = newFieldsArr[pos+1];
				}
				newDataArr.push(dataArr[i]);
			}
		}
		return newDataArr;
	}
	var data__ = {data:parseConfig()};
	
	var COMMONDATA = {
		saveType:[[1,'nosave'],[2,'db'],[3,'nosql']],
		indexType:[[0,'不检索'],[1,'索引'],[2,'索引+排序'],[3,'索引+分词'],[4,'全文索引'],[5,'特殊符号分词'],[6,'仅存储'],[7,'精细分词'],[8,'地理编码(GEO)']],
		fieldType:[['INT','INT'],['FLOAT','FLOAT'],['DOUBLE','DOUBLE'],['CHAR','CHAR'],['VARCHAR','VARCHAR'],['TEXT','TEXT'],['mediumtext','mediumtext'],['DATETIME','DATETIME']],
		postUrl:"xform!saveConfig.jhtml"//保存接口地址
	};
	function getSaveTypeText(value){
		for(var i=0;i<COMMONDATA.saveType.length;i++){
			if(value==COMMONDATA.saveType[i][0]){
				return COMMONDATA.saveType[i][1];
			}
		}
		return "db";
	}
	function getSaveTypeValue(text){
		for(var i=0;i<COMMONDATA.saveType.length;i++){
			if(text==COMMONDATA.saveType[i][1]){
				return COMMONDATA.saveType[i][0];
			}
		}
		return 2;
	}	
	</script>
</head>
<body>	

<div id="placeholder"></div>

<script>
Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL = "./../res/js/ext2/resources/images/default/s.gif";
    Ext.QuickTips.init();

    var fm = Ext.form;
	var chkAllowNull = new Ext.grid.CheckColumn({
	   header: "允许为空?",
	   dataIndex: 'f_allowNull',
	   width: 75
	});
	var chkl_allowSearch = new Ext.grid.CheckColumn({
	   header: "允许搜索?",
	   dataIndex: 'l_allowSearch',
	   width: 75
	});
	var chkl_allowSort = new Ext.grid.CheckColumn({
	   header: "允许排序?",
	   dataIndex: 'l_allowSort',
	   width: 75
	});	

	
    var cm = new Ext.grid.ColumnModel([
	    new Ext.grid.CheckboxSelectionModel(),{
		   id:'f_name',
           header: "字段名",
           dataIndex: 'f_name',
           width: 150,
		   sortable: true
        },{
		   id:'f_title',
           header: "字段描述",
           dataIndex: 'f_title',
           width: 150,
           editor: new fm.TextField({
               allowBlank: true
           })
        },{
           header: "字段类型",
           dataIndex: 'f_type',
           width: 100,
		   sortable: true
        },{
           header: "字段长度",
           dataIndex: 'f_length',
           width: 75,
           align: 'right',
           editor: new fm.NumberField({
               allowBlank: false,
               allowNegative: false,
               maxValue: 100000
           })
        },{
			header: "存储类型",
			dataIndex: 'f_saveType',
			width: 100,
			sortable: true,
			renderer:function(v,p,record){
				return getSaveTypeText(v);
			}
        },
		chkAllowNull,
		chkl_allowSearch,
		chkl_allowSort,
		{
			header: "索引类型",
			dataIndex: 'indexType',
			width: 100,
			sortable: true,
		    renderer:function(v,p,record){
				var text = v;
				var combo = Ext.getCmp('comboIndexType');
				if(combo.valueField){
					var r = combo.findRecord(combo.valueField, v);
					if(r){
						text = r.data[combo.displayField];
					}else if(combo.valueNotFoundText !== undefined){
						text = combo.valueNotFoundText;
					}
				}
				return text;  //显示显示值
			},
			editor: new fm.ComboBox({
				id:"comboIndexType",
				triggerAction: 'all',
				editable:false,
				mode: 'local',
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text'],　
				　　data:COMMONDATA.indexType
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				listClass: 'x-combo-list-small'
			})
        },
		
    ]);
    cm.allowSort = true;

	
	deleteNewColumn = function (){
		var selItems = grid_new.getSelectionModel().selections.items;
		if(selItems.length>0){
			var ids=[];
			for(var i=selItems.length-1;i>=0;i--){
				ids.push(selItems[i].data.f_name);
				store_new.remove(selItems[i]);
			}			
		}
	}
	
    var cm_new = new Ext.grid.ColumnModel([
		   new Ext.grid.CheckboxSelectionModel(),{
		   id:'f_name',
           header: "字段名",
           dataIndex: 'f_name',
           width: 150,
           editor: new fm.TextField({
               allowBlank: true,
			   validateOnBlur:false,
			   vtype:'alphanum',//只能输入字母和数字
			   validator:function(value){
					var value = value.toLowerCase();
					if(value===this.startValue) return true;
					var isValid=true;
					this.invalidText = '只能输入字母和数字';
					if(value=='id'){
						this.invalidText ='id是系统默认字段,不需要创建';
						isValid = false;
					}else if((',' + formConfig__.fieldsConfig.fields.join(',').toLowerCase()).indexOf(',' + value + ',')!=-1){
						this.invalidText = '字段' + value + '已存在';
						isValid = false;
					}
					if(isValid){
						Ext.getCmp('lblStatus').setValue("");	
					}else{
						Ext.getCmp('lblStatus').setValue(this.invalidText);
						this.value="";
					}
					return isValid;
				}
           })
        },{
		   id:'f_title',
           header: "字段描述",
           dataIndex: 'f_title',
           width: 150,
           editor: new fm.TextField({
               allowBlank: true
           })
        },{
           header: "字段类型",
           dataIndex: 'f_type',
           width: 100,
		    renderer:function(v,p,record){
				var text = v;
				var combo = Ext.getCmp('comboFieldType');
				if(combo.valueField){
					var r = combo.findRecord(combo.valueField, v);
					if(r){
						text = r.data[combo.displayField];
					}else if(combo.valueNotFoundText !== undefined){
						text = combo.valueNotFoundText;
					}
				}
				return text;  //显示显示值
			},			
           editor: new fm.ComboBox({
			   id:"comboFieldType",
               triggerAction: 'all',
			   editable:false,
				mode: 'local',
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text'],　
				　　data:COMMONDATA.fieldType
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
               listClass: 'x-combo-list-small'
            })
        },{
           header: "字段长度",
           dataIndex: 'f_length',
           width: 75,
           align: 'right',
           editor: new fm.NumberField({
               allowBlank: false,
               allowNegative: false,
               maxValue: 100000
           })
        },{
			header: "存储类型",
			dataIndex: 'f_saveType',
			width: 100,
		    renderer:function(v,p,record){
				var text = v;
				var combo = Ext.getCmp('comboSaveType');
				if(combo.valueField){
					var r = combo.findRecord(combo.valueField, v);
					if(r){
						text = r.data[combo.displayField];
					}else if(combo.valueNotFoundText !== undefined){
						text = combo.valueNotFoundText;
					}
				}
				return text;  //显示显示值
			},
			editor: new fm.ComboBox({
				id:"comboSaveType",
				editable:false,
				triggerAction: 'all',
				mode: 'local',
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text'],　
				　　data:COMMONDATA.saveType
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				listClass: 'x-combo-list-small'
			})
        },
		chkAllowNull,
		chkl_allowSearch,
		chkl_allowSort,
		{
			header: "索引类型",
			dataIndex: 'indexType',
			width: 100,
		    renderer:function(v,p,record){
				var text = v;
				var combo = Ext.getCmp('comboIndexType_new');
				if(combo.valueField){
					var r = combo.findRecord(combo.valueField, v);
					if(r){
						text = r.data[combo.displayField];
					}else if(combo.valueNotFoundText !== undefined){
						text = combo.valueNotFoundText;
					}
				}
				return text;  //显示显示值
			},
			editor: new fm.ComboBox({
				id:"comboIndexType_new",
				triggerAction: 'all',
				editable:false,
				mode: 'local',
				store:new Ext.data.SimpleStore({　
				　　fields:['value','text'],　
				　　data:COMMONDATA.indexType
				}),
				valueField : 'value',//值
				displayField : 'text',//显示文本
				listClass: 'x-combo-list-small'
			})
        },{
			header: "删除",
			align:'center',
			width: 30,
			dataIndex:'id',
			renderer:function(v,p,record){
				return '<div class="deleteButton" onclick="deleteNewColumn()"></div>'; 
			}
		}
    ]);
    cm_new.allowSort = true;


    var fieldColumn = Ext.data.Record.create([
           {name: 'f_name', type: 'string'},
           {name: 'f_type', type: 'string'},
           {name: 'f_length', type: 'int'},  
           {name: 'f_saveType', mapping: 'f_saveType', type: 'int' },
           {name: 'f_allowNull', type: 'bool'}
      ]);

    // create the Data Store
    var store_old = new Ext.data.Store({
        proxy: new Ext.data.MemoryProxy(data__), 
        reader : new Ext.data.JsonReader({
			autoLoad:true,
			root : "data",
			fields: ["f_name","f_title","f_type","f_length","f_saveType","f_allowNull","l_allowSearch","l_allowSort","indexType"]
		}),

        //sortInfo:{field:'f_name', direction:'ASC'}
    });
    var store_new = new Ext.data.Store({
        proxy: new Ext.data.MemoryProxy({data:[]}), 
        reader : new Ext.data.JsonReader({
			autoLoad:true,
			root : "data",
			fields: ["f_name","f_title","f_type","f_length","f_saveType","f_allowNull","l_allowSearch","l_allowSort","indexType"]
		}),

        //sortInfo:{field:'f_name', direction:'ASC'}
    });

    // create the editor grid_old
    var grid_old = new Ext.grid.EditorGridPanel({
        store: store_old,
		frame:false,
		hideBorders:true,
		enableColumnHide:true,
		stripeRows :true,//表格行颜色间隔显示
        cm: cm,
		sm: new Ext.grid.CheckboxSelectionModel(),
        renderTo: 'placeholder',
        //width:940,
        //height:500,
		autoHeight:true,
        autoExpandColumn:'f_title',
        title:'',
        frame:true,
        plugins:[chkAllowNull,chkl_allowSearch,chkl_allowSort],
        clicksToEdit:1,

        tbar: [new Ext.form.Label({
        	text:'表单名称:',
			style:'margin-left:5px'				
		}),{
			xtype:'textfield',
			id:'txtFormName',
			width:150,
			value:formName__
		
		},new Ext.form.Label({
        	text:'权限路径:',
			style:'margin-left:5px'	
		}),{
			xtype:'textfield',
			id:'txtPowerPath',
			width:150,
			value:powerPath__
		
		},new Ext.form.Label({
        	text:'启用分表:',
			style:'margin-left:5px'	
		}),{
			xtype:'checkbox',
			id:'enableBak',
			checked:formConfig__.tableBak.enableBak,
			value:formConfig__.tableBak.enableBak
		
		},new Ext.form.Label({
        	text:'分表条件(记录数):',
			style:'margin-left:5px'	
		}),{
			xtype:'numberfield',
			id:'recordCount',
			width:60,
			emptyText :'最大记录数',
			value:formConfig__.tableBak.recordCount
		
		},new Ext.form.Label({
        	text:'分表条件(天数):',
			style:'margin-left:5px'	
		}),{
			xtype:'numberfield',
			id:'dateFieldName',
			width:80,
			emptyText:'N天以前的数据将转移到附属表中',
			value:formConfig__.tableBak.dateFieldName
		
		},{
			xtype:'tbfill'
		},{
            text: '新建字段',
			iconCls:"addField",
            handler : function(){
				if(grid_new==null){
					createNewGrid();
				}
                var p = new fieldColumn({
                    f_name: '',
					f_title:'',
                    f_type: 'VARCHAR',
                    f_length: 255,
					f_saveType:2,
                    f_allowNull: true,
					l_allowSearch:false,
					l_allowSort:false,
					indexType:1
					
                });
                grid_new.stopEditing();
				var len = store_new.getCount();
                store_new.insert(len, p);
                grid_new.startEditing(len, 1);
            }
        },{
            text: '删除字段',
			disabled :true,
			iconCls:"delField",
            handler : function(){
                var selItems = grid_old.getSelectionModel().selections.items;
				if(selItems.length>0){
					Ext.MessageBox.confirm("提示","删除字段可能造成系统故障,确定删除吗？", 
						function(button,text){  
							if(button=='yes'){
								var ids=[];
								for(var i=selItems.length-1;i>=0;i--){
									ids.push(selItems[i].data.f_name);
									//store_old.remove(selItems[i]);
								}							
								alert(ids.join(',') + ' delete todo...');
							}
						}
					)
				}
				if(grid_new!=null){
					var selItems_new = grid_new.getSelectionModel().selections.items;
					for(var i=selItems_new.length-1;i>=0;i--){
						store_new.remove(selItems_new[i]);
					}	
				}
				
            }
        },{
            text: '保存',
			iconCls:"saveField",
            handler : function(){
				var fieldArr=[];
				var len = store_old.getCount();
				var isDirty=false;
				for(var i=0;i<len;i++){
					var data = store_old.getAt(i).data;
					fieldArr[fieldArr.length]=data;
				}
				len = store_new.getCount();
				for(var i=0;i<len;i++){
					var data = store_new.getAt(i).data;
					if(!data.f_name){
						isDirty = true;	
						break;
					}	
					fieldArr[fieldArr.length]=data;
				}
				if(isDirty){
					Ext.Msg.show({
					   title:'错误提示',
					   msg: '字段名不能为空',
					   buttons: Ext.Msg.OK,
					   animEl: 'elId',
					   minWidth:420,
					   icon: Ext.MessageBox.ERROR 
					});
					return;
				}
				
				var l_allowSortArr = [];
				var l_allowSearchArr = [];
				var fieldsArr =[];
				var s_l_allowSortArr=[];
				var s_l_allowSearchArr = []; //;[[0,'不检索'],[1,'索引'],[2,'索引+排序'],[3,'索引+分词'],[4,'全文索引'],[5,'特殊符号分词'],[6,'仅存储']],
				for(var i=0;i<fieldArr.length;i++){
					var f = fieldArr[i];
					fieldsArr.push([f.f_name,f.f_title,f.f_type]);
					if(f.l_allowSort) l_allowSortArr.push(f.f_name);
					if(f.l_allowSearch) l_allowSearchArr.push(f.f_name);
					if(f.indexType==2) s_l_allowSortArr.push(f.f_name);
					if(f.indexType>0 && f.indexType<6) s_l_allowSearchArr.push(f.f_name);
					
				}
				var allConfig = {
					fieldsConfig:{
						fieldsConfig:fieldArr,
						sortable:l_allowSortArr,
						searchable:l_allowSearchArr,
						fields:fieldsArr
					},
					searchConfig:{
						sortable:s_l_allowSortArr,
						searchable:s_l_allowSearchArr
					},
					tableBak:{
						enableBak:Ext.getCmp('enableBak').checked,
						recordCount:Ext.getCmp('recordCount').getValue(),
						dateFieldName:Ext.getCmp('dateFieldName').getValue(),
					}
				}
				
				var allConfigStr = Ext.util.JSON.encode(allConfig);
				var formName = Ext.getCmp('txtFormName').getValue();
				var powerPath = Ext.getCmp('txtPowerPath').getValue();
				
				Ext.getBody().mask("正在提交,请稍候......");
				Ext.Ajax.request({  
					url:COMMONDATA.postUrl,  
					params:{formId:formId__,formName:formName,powerPath:powerPath,config:allConfigStr,nodeId:nodeId__},  
					method:"POST",  
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
							Ext.Toast.show('保存成功',{
							   title:'提示',
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.INFO,
								callback:function(){
									window.location.reload();
								}								
							});
							
						}
						//location.reload();
					},
					failure:function(response,opts){
						//Ext.Msg.alert('错误提示',response.responseText)	;
						Ext.getBody().unmask();
						Ext.Msg.show({
							   title:'错误提示',
							   msg: decodeURIComponent(ret.message),
							   buttons: Ext.Msg.OK,
							   animEl: 'elId',
							   minWidth:420,
							   icon: Ext.MessageBox.ERROR 
						});

					}		
				});	
            }
        }]
    });

    // trigger the data store load
    store_old.load();
	
	var grid_new= null;
	function createNewGrid(){
		grid_new = 	new Ext.grid.EditorGridPanel({
			store: store_new,
			frame:false,
			hideBorders:true,
			enableColumnHide:true,
			stripeRows :true,//表格行颜色间隔显示
			cm: cm_new,
			sm: new Ext.grid.CheckboxSelectionModel(),
			renderTo: 'placeholder',
			//width:940,
			//height:500,
			autoHeight:true,
			autoExpandColumn:'f_title',
			title:'',
			frame:true,
			plugins:[chkAllowNull,chkl_allowSearch,chkl_allowSort],
			clicksToEdit:1,
			bbar:[{
				xtype:"textfield",
				id:'lblStatus',
				width:200,
				readOnly:true,
				style:'color:red;border: 1px inset rgb(169, 191, 211); background: none repeat scroll 0% 0% transparent;'
			}]
			
		});
		store_new.load();
	}

	 
});

Ext.grid.CheckColumn = function(config){
    Ext.apply(this, config);
    if(!this.id){
        this.id = Ext.id();
    }
    this.renderer = this.renderer.createDelegate(this);
};

Ext.grid.CheckColumn.prototype ={
    init : function(grid){
        this.grid = grid;
        this.grid.on('render', function(){
            var view = this.grid.getView();
            view.mainBody.on('mousedown', this.onMouseDown, this);
        }, this);
    },

    onMouseDown : function(e, t){
        if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
            e.stopEvent();
            var index = this.grid.getView().findRowIndex(t);
            var record = this.grid.store.getAt(index);
            record.set(this.dataIndex, !record.data[this.dataIndex]);
        }
    },

    renderer : function(v, p, record){
        p.css += ' x-grid3-check-col-td'; 
        return '<div class="x-grid3-check-col'+(v?'-on':'')+' x-grid3-cc-'+this.id+'">&#160;</div>';
    }
};
</script>
</body>	
</head>
</html>