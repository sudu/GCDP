<<<<<<< HEAD
Designer = typeof Designer == "object" ?Designer: {};
Designer.controls = Designer.controls || {};
Designer.controls.ui={
	FormPanel:{
		runtime:{
			ui:{
				title:'我的表单',
				buttonAlign:'center',
				xtype:'form',
				layout : 'xform',
				anchor:'100%',
				labelWidth:100,
				labelAlign:"right",
				items:{},
				padding:"10px",
				margin:"20px",
				autoScroll :true,
				frame:true,
				autoScroll:true
			}
		}
	},
	TextField:{
		runtime:{
			//在设计器中创建控件时需要的配置
			ui:{
				validateOnBlur:true
			}
		}
	},
	FieldSet: {
		runtime:{
			//在设计器中创建控件时需要的配置
			ui:{
				labelAlign: "right",
				xtype:"xfieldset",
				isContainer:true,
				layout: 'xform',
				cls:'fieldsetStyle',
				collapsible:true,
				checkboxToggle:false,
				titleCollapse:true,
				autoHeight:true
			}
		}
    },
	ComboBox:{
		runtime:{
			ui:{
				triggerAction:"all"
			}
		}
	},
	MultiSelect:{
		runtime:{
			ui:{
				triggerAction:"all"
			}
		}
	},
	HtmlEditor2:{
		runtime:{
			ui:{
				//fontFamilies : ["宋体","黑体","楷体"]
			}
		}
	},
	TextArea2:{
		runtime:{
			ui:{
			
			},
			inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}"/>'
		}
	},
	NumberSpinner:{
		runtime:{
			ui:{},
			inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}" class="easyui-numberspinner" style="width:80px;"></input>'
		}
	},
	NumberBox:{
		runtime:{
			ui:{},
			inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}" class="easyui-numberbox" style="width:80px;"></input>'
		}
	},
	Hidden:{
		runtime:{
			ui:{},
			inputTemplate:'<input id="{{= id}}" type="hidden" name="{{= name}}"></input>',
			formItemTemplate4HideLabel:'',//控件容器模板
			formItemTemplate:''
		}
	},
	DataBox:{
		runtime:{
			ui:{
				currentText:"今天",
				closeText:"关闭",
				okText:"OK",
			}
		}
	}
	 
}
=======
Designer = typeof Designer == "object" ?Designer: {};
Designer.controls = Designer.controls || {};
Designer.controls.ui={
	FormPanel:{
		runtime:{
			ui:{
				title:'我的表单',
				buttonAlign:'center',
				xtype:'form',
				layout : 'xform',
				anchor:'100%',
				labelWidth:100,
				labelAlign:"right",
				items:{},
				padding:"10px",
				margin:"20px",
				autoScroll :true,
				frame:true,
				autoScroll:true
			}
		}
	},
	TextField:{
		runtime:{
			//在设计器中创建控件时需要的配置
			ui:{
				validateOnBlur:true
			}
		}
	},
	FieldSet: {
		runtime:{
			//在设计器中创建控件时需要的配置
			ui:{
				labelAlign: "right",
				xtype:"xfieldset",
				isContainer:true,
				layout: 'xform',
				cls:'fieldsetStyle',
				collapsible:true,
				checkboxToggle:false,
				titleCollapse:true,
				autoHeight:true
			}
		}
    },
	ComboBox:{
		runtime:{
			ui:{
				triggerAction:"all"
			}
		}
	},
	MultiSelect:{
		runtime:{
			ui:{
				triggerAction:"all"
			}
		}
	},
	HtmlEditor2:{
		runtime:{
			ui:{
				//fontFamilies : ["宋体","黑体","楷体"]
			}
		}
	},
	TextArea2:{
		runtime:{
			ui:{
			
			},
			inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}"/>'
		}
	},
	NumberSpinner:{
		runtime:{
			ui:{},
			inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}" class="easyui-numberspinner" style="width:80px;"></input>'
		}
	},
	NumberBox:{
		runtime:{
			ui:{},
			inputTemplate:'<input id="{{= id}}" type="text" name="{{= name}}" class="easyui-numberbox" style="width:80px;"></input>'
		}
	},
	Hidden:{
		runtime:{
			ui:{},
			inputTemplate:'<input id="{{= id}}" type="hidden" name="{{= name}}"></input>',
			formItemTemplate4HideLabel:'',//控件容器模板
			formItemTemplate:''
		}
	},
	DataBox:{
		runtime:{
			ui:{
				currentText:"今天",
				closeText:"关闭",
				okText:"OK",
			}
		}
	}
	 
}
>>>>>>> 2bc3b6021afb4e6e01b52e3e266049ff763910c3
	