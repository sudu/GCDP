
var formListStore,//表单列表store
docStore,//文章系统表单store
columnStore,//栏目系统表单store
fieldColumn = Ext.data.Record.create([
      {name: 'name', type: 'string'},
      {name: 'description', type: 'string'},
      {name: 'type', type: 'string'},
      {name: 'length', type: 'int'},
      {name: 'local_name', type: 'string'},
 ]);

//初始化文章系统表单store
function initDocStore(){
	var fieldsConfig = docFormConfig__ ? docFormConfig__.fieldsConfig.fieldsConfig: [];
	//console.log(fieldsConfig);
	var data = {data:[]};
	for(var i=0; i<fieldsConfig.length;i++){
		
		var local_name = sysFormFieldAdapterCfg__.docFieldAdapterCfg!='' && sysFormFieldAdapterCfg__.docFieldAdapterCfg.map[fieldsConfig[i].f_name];
		local_name = local_name || "-------请选择-------";
		
		data.data.push({"name":fieldsConfig[i].f_name,
			"description":fieldsConfig[i].f_title,
			"type":fieldsConfig[i].f_type,
			"length":fieldsConfig[i].f_length,
			"local_name":local_name});
	}
	return new Ext.data.Store({
	    proxy: new Ext.data.MemoryProxy(data), 
	    reader : new Ext.data.JsonReader({
			//autoLoad:true,
			root : "data",
			fields: ["name","description","type","length","local_name"]
		})
	});
}
//初始化栏目系统表单store
function initColumnStore(){
	fieldsConfig = columnFormConfig__ ? columnFormConfig__.fieldsConfig.fieldsConfig : [];
	//console.log(fieldsConfig);
	var data = {data:[]};
	for(var i=0; i<fieldsConfig.length;i++){
		var local_name = sysFormFieldAdapterCfg__.columnFieldAdapterCfg!='' && sysFormFieldAdapterCfg__.columnFieldAdapterCfg.map[fieldsConfig[i].f_name];
		local_name = local_name || "-------请选择-------";
		data.data.push({"name":fieldsConfig[i].f_name,
			"description":fieldsConfig[i].f_title,
			"type":fieldsConfig[i].f_type,
			"length":fieldsConfig[i].f_length,
			"local_name":local_name});
	}
	return new Ext.data.Store({
	    proxy: new Ext.data.MemoryProxy(data), 
	    reader : new Ext.data.JsonReader({
			//autoLoad:true,
			root : "data",
			fields: ["name","description","type","length","local_name"]
		})
	});
}
(function(){
	//初始化表单列表store
	var data = [];
	for(var i=0; i<formListConfig__.length;i++){
		//过滤掉系统表单
		if(formListConfig__[i].isSysForm != 0){
			continue;
		}
		data.push([
			formListConfig__[i].id,
			formListConfig__[i].title+'('+formListConfig__[i].tableName+')'
		]);
	}
	//console.log(data);
	formListStore = new Ext.data.SimpleStore({
		data: data,
		fields: ["value","text"]
	});
	
	//初始化文章系统表单store
	docStore = initDocStore();
	//初始化栏目系统表单store
	columnStore = initColumnStore();

})();


var viewMgr = {
	reinit:function(){
		typeof this.mainViewport != 'undefined' && this.mainViewport.destroy();
		this.init();
	},
	init:function(){
		this.mainViewport = new Ext.Viewport({
			frame:true,
			layout:"border",
			//margins:'5',
			items:[{
				xtype:'vrtabpanel',
				//id:"centerTabPanel",
				region:"center",
				//enableTabScroll :true,
				autoScroll:true, 
				layoutOnTabChange:true,
				resizeTabs:true,
				activeTab:0,
				frame:true,
				//border:false,
				bodyStyle:'overflow:hidden;',
				tabPosition: 'left',
				verticalText: true,//竖直文本标志
				items:[{
						xtype:"panel",
						closable:false,
						autoScroll:true,	
						title:"文章字段映射",
						items:{
							xtype:'form',
							id:'docCfgForm',
							style:'padding:10px;overflow-x:hidden;',
							//bodyStyle:'padding:0 5px 10px 10px',
							itemCls:"itemStyle",
							layout:'xform',
							labelAlign :'right',
							labelWidth :110,
							//autoScroll:true,
							border:false,
							items:[{
								xtype:'combo',
								fieldLabel:'请选择文章表单',
								name:'defaultSort',
								id:"docListCombo",
								hiddenName:'defaultSort',
								triggerAction:"all",
								valueField : 'value',
								displayField : 'text',
								typeAhead: false,
								editable: false,
								forceSelection:true,
								mode:'local',
								width:200,
								emptyText:'-------请选择-------',
								store:formListStore
							},
							
							{
								border:false,
								ctCls :"itemStyle2",
								layout:'xform',
								items:[{
									xtype:'editorgrid',
									id:'docGrid',
									store: docStore,
									sm: new Ext.grid.CheckboxSelectionModel(),
									clicksToEdit:1,
									stripeRows :true,//表格行颜色间隔显示
							        cm: new Ext.grid.ColumnModel([{
		        								header: "系统字段描述",
		        								dataIndex: 'description',
		                                        width: 150
		                                     },{
		                                    	 header: "系统字段名",
			                                     dataIndex: 'name',
		                                        width: 150
		                                     },{
		                                    	 header: "系统字段类型",
			                                     dataIndex: 'type',
		                                        width: 150
		                                     },{
		                                    	 header: "系统字段长度",
			                                     dataIndex: 'length',
		                                        width: 150
		                                     },{
		                             			header: "映射字段名",
		                            			dataIndex: 'local_name',
		                            			width: 300,
		                            			editor: new Ext.form.ComboBox({
		                            				id:"docComboName",
		                            				editable:false,
		                            				triggerAction: 'all',
		                            				mode: 'local',
		                            				store:new Ext.data.SimpleStore({
		                            					fields:['value','text'],
		                            					data:[]
		                            				}),
		                            				valueField : 'value',//值
		                            				displayField : 'text',//显示文本
		                            				listClass: 'x-combo-list-small'
		                            			})
		                                    }]),
									autoHeight:true,
							        title:'系统与本地表单字段映射',
								},
								]
							},
							{
								xtype:'button',
								text:'文章系统表单映射脚本',
						        style:"margin-left:1em;display:inline-block;",
								listeners:{
									'click':function(){
										//console.log(this)
										if(!this.debuger) this.debuger = Ext.jsDebugger('../develop/', nodeId__, 'common.docsysform',null,'common',true,'adapter_docsysform')
										this.debuger(this.formField);
									}
								}
							},
							]
						 },
						 buttonAlign:'center',
						 buttons:[{
								text:'保存',
								id:'btn_b_save',
								type:'button',
								handler:function(){

									var localFormId = Ext.getCmp('docListCombo').getValue();
									if(localFormId == ''){
										//console.log(localFormId);
										return;
									}

									var fieldAdapterCfg = {
											sysFormId:sysFormFieldAdapterCfg__.docFieldAdapterCfg.sysFormId,
											viewId:sysFormFieldAdapterCfg__.docFieldAdapterCfg.viewId,
											listId:sysFormFieldAdapterCfg__.docFieldAdapterCfg.listId,
											localFormId:localFormId,
											map:{}
									};
									for(var i=0;i<docStore.getCount();i++){
										var item = docStore.getAt(i);
										var local_name = '';
										/-/.test(item.data.local_name) || ( local_name = item.data.local_name);
										eval('( fieldAdapterCfg.map.'+item.data.name+' = local_name )');
									}
									//console.log(fieldAdapterCfg);
									
									Ext.getBody().mask('正在请求数据');
									Ext.Ajax.request({  
										url:'xformAdapter!saveFieldAdapterCfg.jhtml?nodeId='+nodeId__+'&type=doc',
										method:"post",
										params:{fieldAdapterCfg:Ext.util.JSON.encode(fieldAdapterCfg)},
										success:function(response,opts){
											Ext.getBody().unmask();
											var ret = Ext.util.JSON.decode(response.responseText);
											//console.log(ret);
											if(ret.success){
												//reset
												sysFormFieldAdapterCfg__.docFieldAdapterCfg = fieldAdapterCfg;
												//文章系统表单docStore
												docStore = initDocStore();
												viewMgr.reinit();
											}else{
												Ext.Msg.show({
													title:'保存文章映射配置失败',
													msg: ret.msg,
													buttons: Ext.Msg.OK,
													animEl: 'elId',
													minWidth:420,
													icon: Ext.MessageBox.ERROR 
												});
											}
										}
									});
									
									
								}
						 }]
				       },//end 文章表单字段配置 panel
				       {
							xtype:"panel",
							closable:false,
							autoScroll:true,
							title:"栏目字段映射",
							items:{
								xtype:'form',
								id:'columnCfgForm',
								style:'padding:10px;overflow-x:hidden;',
								//bodyStyle:'padding:0 5px 10px 10px',
								itemCls:"itemStyle",
								layout:'xform',
								labelAlign :'right',
								labelWidth :110,
								//autoScroll:true,
								border:false,
								items:[{
									xtype:'combo',
									fieldLabel:'请选择栏目表单',
									name:'defaultSort',
									id:"columnListCombo",
									hiddenName:'defaultSort',
									triggerAction:"all",
									valueField : 'value',
									displayField : 'text',
									typeAhead: false,
									editable: false,
									forceSelection:true,
									mode:'local',
									width:200,
									emptyText:'-------请选择-------',
									store:formListStore
								},
								
								{
									border:false,
									ctCls :"itemStyle2",
									layout:'xform',
									items:[{
										xtype:'editorgrid',
										id:'docGrid',
										store: columnStore,
										sm: new Ext.grid.CheckboxSelectionModel(),
										clicksToEdit:1,
										stripeRows :true,//表格行颜色间隔显示
								        cm: new Ext.grid.ColumnModel([{
			        								header: "系统字段描述",
			        								dataIndex: 'description',
			                                        width: 150
			                                     },{
			                                    	 header: "系统字段名",
				                                     dataIndex: 'name',
			                                        width: 150
			                                     },{
			                                    	 header: "系统字段类型",
				                                     dataIndex: 'type',
			                                        width: 150
			                                     },{
			                                    	 header: "系统字段长度",
				                                     dataIndex: 'length',
			                                        width: 150
			                                     },{
			                             			header: "映射字段名",
			                            			dataIndex: 'local_name',
			                            			width: 300,
			                            			editor: new Ext.form.ComboBox({
			                            				id:"columnComboName",
			                            				editable:false,
			                            				triggerAction: 'all',
			                            				mode: 'local',
			                            				store:new Ext.data.SimpleStore({
			                            					fields:['value','text'],
			                            					data:[]//[['id','id']]//COMMONDATA.saveType
			                            				}),
			                            				valueField : 'value',//值
			                            				displayField : 'text',//显示文本
			                            				listClass: 'x-combo-list-small'
			                            			})
			                                    }]),
										autoHeight:true,
								        //autoExpandColumn:'f_title',
								        title:'系统与本地表单字段映射',
									},
									]
								},
								{
									xtype:'button',
									text:'栏目系统表单映射脚本',
							        style:"margin-left:1em;display:inline-block;",
									listeners:{
										'click':function(){
											//console.log(this)
											if(!this.debuger) this.debuger = Ext.jsDebugger('../develop/', nodeId__, 'common.columnsysform',null,'common',true,'adapter_columnsysform')
											this.debuger(this.formField);
										}
									}
								},
								]
							 },
							 buttonAlign:'center',
							 buttons:[{
									text:'保存',
									id:'btn_b_save',
									type:'button',
									handler:function(){
										var localFormId = Ext.getCmp('columnListCombo').getValue();
										if(localFormId == ''){
											//console.log(localFormId);
											return;
										}
	
										var fieldAdapterCfg = {
												sysFormId:sysFormFieldAdapterCfg__.columnFieldAdapterCfg.sysFormId,
												viewId:sysFormFieldAdapterCfg__.columnFieldAdapterCfg.viewId,
												listId:sysFormFieldAdapterCfg__.columnFieldAdapterCfg.listId,
												localFormId:localFormId,
												map:{}
										};
										for(var i=0;i<columnStore.getCount();i++){
											var item = columnStore.getAt(i);
											var local_name = '';
											/-/.test(item.data.local_name) || ( local_name = item.data.local_name);
											eval('( fieldAdapterCfg.map.'+item.data.name+' = local_name )');
										}
										//console.log(fieldAdapterCfg);

										Ext.getBody().mask('正在请求数据');
										Ext.Ajax.request({  
											url:'xformAdapter!saveFieldAdapterCfg.jhtml?nodeId='+nodeId__+'&type=column',
											method:"post",
											params:{fieldAdapterCfg:Ext.util.JSON.encode(fieldAdapterCfg)},
											success:function(response,opts){
												Ext.getBody().unmask();

												var ret = Ext.util.JSON.decode(response.responseText);
												//console.log(ret);
												if(ret.success){
													//reset
													sysFormFieldAdapterCfg__.columnFieldAdapterCfg = fieldAdapterCfg;
													//栏目系统表单columnStore
													columnStore = initColumnStore();
													viewMgr.reinit();
												}else{
													Ext.Msg.show({
														title:'保存栏目映射配置失败',
														msg: ret.msg,
														buttons: Ext.Msg.OK,
														animEl: 'elId',
														minWidth:420,
														icon: Ext.MessageBox.ERROR 
													});
												}
											}
										});
									}
							 }]
							
				       },//end 栏目表单字段配置 panel
				]
			}]//end tabpanel
		});//end Ext.Viewport()
		

		docStore.load();
		columnStore.load();
		
		this.initFormList();
	},//end init()
	initFormList:function(){
	
		var combo = Ext.getCmp('docListCombo');
		combo.on('select',function(combo, value, index){
			var formid=value.data.value;
			Ext.Ajax.request({
				url:'xform!FormConfig.jhtml?formId='+formid+'&nodeId='+nodeId__,
				method:"get",
				success:function(response,opts){

					docStore.load();
					var ret = Ext.util.JSON.decode(response.responseText)
					var fieldsConfig = ret.fieldsConfig.fieldsConfig;
					//console.log(fieldsConfig);
					var combo = Ext.getCmp('docComboName');
					combo.store.removeAll();
					combo.store.insert(0,new fieldColumn({value: "-------请选择-------", text:"-------请选择-------"}));
					for(var i=0; i<fieldsConfig.length;i++){
						combo.store.insert(i+1,new fieldColumn({value: fieldsConfig[i].f_name, text:fieldsConfig[i].f_name+'('+fieldsConfig[i].f_title+'|'+fieldsConfig[i].f_type+'|'+fieldsConfig[i].f_length+')'}));
					}
					
					//若文章系统表单与该表字段适配，则进行相应值设置
					if(sysFormFieldAdapterCfg__.docFieldAdapterCfg != ''){
						var docFieldAdapterCfg = sysFormFieldAdapterCfg__.docFieldAdapterCfg;
						//console.log(docFieldAdapterCfg);
						//console.log(formid);
						if(docFieldAdapterCfg.localFormId != formid){
							for(var i=0;i<docStore.getCount();i++){
								var item = docStore.getAt(i);
								item.set('local_name', "-------请选择-------");
							}
						}
					}
					
					
				}
			})
				
			
		})
		
		var localFormId = sysFormFieldAdapterCfg__.docFieldAdapterCfg.localFormId;
		if(localFormId !=0){
			combo.setValue(localFormId);
			var record = combo.findRecord(combo.valueField || combo.displayField, localFormId);  
	    	var index = combo.store.indexOf(record); 
	    	//console.log(record)
	    	//console.log(index)
	    	combo.fireEvent('select', combo, record, index);
		}
		
		var combo2 = Ext.getCmp('columnListCombo');
		combo2.on('select',function(combo, value, index){
			var formid=value.data.value;
			Ext.Ajax.request({
				url:'xform!FormConfig.jhtml?formId='+formid+'&nodeId='+nodeId__,
				method:"get",
				success:function(response,opts){
					columnStore.load();

					var ret = Ext.util.JSON.decode(response.responseText)
					//console.log(ret);
					var fieldsConfig = ret.fieldsConfig.fieldsConfig;
					var combo = Ext.getCmp('columnComboName');
					combo.store.removeAll();
					combo.store.insert(0,new fieldColumn({value: "-------请选择-------", text:"-------请选择-------"}));
					for(var i=0; i<fieldsConfig.length;i++){
						combo.store.insert(i+1,new fieldColumn({value: fieldsConfig[i].f_name, text:fieldsConfig[i].f_name+'('+fieldsConfig[i].f_title+'|'+fieldsConfig[i].f_type+'|'+fieldsConfig[i].f_length+')'}));
					}
					
					//若栏目系统表单与该表字段适配，则进行相应值设置
					if(sysFormFieldAdapterCfg__.columnFieldAdapterCfg != ''){
						var columnFieldAdapterCfg = sysFormFieldAdapterCfg__.columnFieldAdapterCfg;
						if(columnFieldAdapterCfg.localFormId != formid){
							for(var i=0;i<columnStore.getCount();i++){
								var item = columnStore.getAt(i);
								item.set('local_name', "-------请选择-------");
							}
							
						}
					}
					
				}
			})
			
		})
		
		
		var localFormId2 = sysFormFieldAdapterCfg__.columnFieldAdapterCfg.localFormId;
		if(localFormId2 !=0){
			combo2.setValue(localFormId2);
			var record = combo2.findRecord(combo2.valueField || combo2.displayField, localFormId2);  
	    	var index = combo2.store.indexOf(record); 
	    	//console.log(record)
	    	//console.log(index)
	    	combo2.fireEvent('select', combo2, record, index);
		}

	},//end initFormList()
}// end viewMgr
