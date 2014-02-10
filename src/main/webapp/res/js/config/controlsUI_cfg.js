
/**
 *控件公共属性配置
*/
Designer.controls.commonProperty = {
    ui: { 
		lan: "UI",
		value: {
			fieldLabel: { value: '标签', lan: '标签' },
			hideLabel: { value: false, lan: '隐藏标签' },
			labelStyle: { value: '', lan: '标签样式', type: 'CustomEditors_TextArea' },
			fieldNote: { value: '', lan: '说明' },
			hideNote: { value: true, lan: '隐藏说明' },
			noteStyle: { value: '', lan: '说明样式', type: 'CustomEditors_TextArea' },
			style: { value: '', lan: '样式', type: 'CustomEditors_TextArea' }
		}
    },
	mod: { 
		lan: "模式", 
		value: {
			inputType_new: { value: 1, lan: '录入时模式', type: 'ComboBox_1', extra:{loadStore: Static.inputType }},
			inputType_edit: { value: 1, lan: '编辑时模式', type: 'ComboBox_1', extra:{loadStore: Static.inputType }}
		}
    },
	db: { 
		lan: "字段配置", 
		value: {
			f_saveType: { value: '2', lan: 'db_字段存储类型', type: 'ComboBox_1',extra:{loadStore: Static.saveType }},
			f_name: { value: "", lan: 'db_字段名',type: 'CustomEditors_TextField_alphanum' },
			f_type: { value: "VARCHAR", lan: 'db_类型', type: 'DbType' },
			f_length: { value: 255, lan: 'db_长度' },
			f_allowNull: { value: true, lan: 'db_允许为空' },
			l_allowSearch: { value: true, lan: '可搜索' },
			l_allowSort: { value: true, lan: '可排序' },
			indexType: { value: 1, lan: '索引类型', type: 'ComboBox_1', extra:{loadStore: Static.indexType} }
		}
    },
	mem: { 
		lan: "记忆功能", 
		value: {
			memory_enable: { value: false, lan: '是否启用'},
			memory_max_count: { value: 3, lan: '记忆数量'},
			memory_must_fill:{value:false,lan:'是否自动填值'}
		}
    }
};

Designer.controls.dataSource={
	lan: "数据源配置",
	value: {
		ext_dataSource_type: {
			value: "json",
			lan: '数据源类型',
			type: 'ComboBox_1',
			extra: {
				loadStore: [['json','json'],['sql','sql'],['url','url']]
			}
		},
		ext_dataSource_value: {
			value: JSON.stringify([{  
				"value":1,  
				"text":"text1"  
			},{   
				"value":2,   
				"text":"text2"  
			}]),
			lan: '数据源内容',
			type: "CustomEditors_TextArea"
		},
		ext_dataSource_asyc: {
			value: true,
			lan: '是否同步获取数据'
		}
	}
};

/**
 *控件UI配置、属性配置
*/
Designer.controls.ui = {
    FormPanel: {
        isTool: false, //是否出现在工具箱里
        design: {//设计时
            name: 'Ext.form.FormPanel',
            rtName: 'Ext.form.FormPanel',
            ui: {
                //title: '我的表单',
                buttonAlign: 'center',
                xtype: 'form',
                layout: 'xform',
                anchor: '100%',
                labelWidth: 100,
                noteWidth: 200, //extended for the right tip column;
                items: {},
                padding: "10px",
                margin: "20px",
				height:2000,
                frame: true,
				autoScroll:true,
				//bodyStyle:"min-height:1500px;"
            }
        },
        property: {
            ui: { lan: "UI", value: {
                title: { value: '我的表单', lan: '名称',required:true},
                labelWidth: { value: 100, lan: '标签宽度' },
                hideLabels: { value: false, lan: '隐藏标签' },
                noteWidth: { value: 0, lan: '说明宽度' },
                hideNotes: { value: false, lan: '隐藏说明' }
                }
            },
            script: { lan: "脚本注入", value: {
                s_beforeJs: { value: '', lan: 's_保存前JS脚本', type: 'CustomEditors_TextArea' },
                s_savedJs: { value: '', lan: 's_保存后JS脚本', type: 'CustomEditors_TextArea' },
                s_afterJs: { value: '', lan: 's_加载后JS脚本', type: 'CustomEditors_TextArea' },
                s_onloadJs: { value: '', lan: 's_事件JS脚本', type: 'CustomEditors_TextArea_cb' },
                s_powerRegex: { value: '', lan: 's_权限表达式' },
                s_powerScript: { value: '', lan: 's_权限脚本', type: 'CustomEditors_TextArea' }
            }
            },
            listPage: { lan: "列表页配置", value: {
            	l_listCfg: { value: '', lan: 'l_列表项', type: 'ListColumn_1',extra:{loadModel:Static.generalDataModel }},
				l_template: { value: 'listTemplate_default_1', lan: 'l_列表模板',type: 'ComboBox_1', extra:{loadStore: [['listTemplate_default_1', '默认列表页模板1']] }},
				l_customedTpl: { value: '', lan: '自定义列表模板', type: 'CustomEditors_TextArea' },
                l_pagesize: { value: 20, lan: 'l_每页显示记录数' }
            }
            },
            buttons: { lan: "按钮配置", value: {
                b_save: { value: true, lan: '保存' },
                b_saveAndAdd: { value: true, lan: '保存并继续添加' },
                b_saveAndClose: { value: true, lan: '保存并关闭' },
                b_preview: { value: true, lan: '预览' },
                b_close: { value: true, lan: '关闭' }
            }
            },
            template: { lan: "模板配置", value: {
                defaultTpl: { value: 'template_1', lan: '默认模板', type: 'ComboBox_1', extra:{loadStore: ( typeof Designer_controls_formTemplate !== 'undefined' ? Designer_controls_formTemplate : [['template_1', '标准视图'], ['template_for_idxEditor', '碎片编辑视图'],['template_for_templateDesign','碎片设计视图'],['template_for_tuijianwei','推荐位数据项编辑视图']] )}},
                customedTpl: { value: '', lan: '自定义模板', type: 'CustomEditors_TextArea' },
				headInject: { value: '', lan: '&lt;/head&gt;之前注入', type: 'CustomEditors_TextArea' },
				bodyInject: { value: '', lan: '&lt;/body&gt;之前注入', type: 'CustomEditors_TextArea' }
            }
            }/*,
			tableBak:{lan: "分表配置",value: {
				enableBak: { value: true, lan: '是否分表' },
				recordCount: { value: 2000000, lan: '按数据量' },
				dateFieldName: { value: "", lan: '按日期字段' }
			}
			}*/
        }
    },
	TextField: {
        text: 'TextField',
        tip: "单行文本域",
        icon: null,
		relyJS:['easyui/src/jquery.textfield.js'],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.TextField',
            rtName: 'textfield',
            ui: {
                width: 300,
                style: 'cursor:default',
                emptyText: ""
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 300,
						lan: '宽度'
					},
					anchor: {
						value: "",
						lan: 'anchor(%)'
					},
					readOnly: {
						value: false,
						lan: '只读'
					},
					disabled: {
						value: false,
						lan: '不启用'
					},
					value: {
						value: "",
						lan: '默认值'
					},
					placeholder: {
						value: "",
						lan: '输入提示',
						help:'当文本框为空时提示的文本信息'
					},
					type:{
						value:"text",
						lan: 'type',
						type: 'ComboBox_1', 
						extra:{
							loadStore: [['text', 'text'],['password', 'password'], ['email', 'email'], ['url', 'url'], ['telephone', 'telephone'], ['search', 'search']]
						}
					},
					required: {
						value: false,
						lan: '是否必填',
						help:"定义文本域是否为必填项"
					},
					validType:{
						value:"",
						lan: 'validType',
						type: 'ComboBox_1', 
						extra:{
							loadStore: [['', '不限'],['url', 'url'], ['email', 'email']]
						}
					},
					invalidMessage:{
						value: "",
						lan: '验证提示信息',
						help:"当文本框内容不合法时提示的文本信息"
					}
					
				}
			},
			events: {
				lan: "事件",
				value: {
					onChange: {
						value: "",
						lan: 'onChange',
						type: 'Event'
					},
					onBlur: {
						value: "",
						lan: 'onBlur',
						type: 'Event'
					},
					onFocus: {
						value: "",
						lan: 'onFocus',
						type: 'Event'
					}
				}
			}
		}
    },
	
	TextArea: {
        text: 'TextArea',
        tip: "多行文本域",
        icon: null,
		relyJS:['easyui/src/jquery.textarea.js'],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.TextArea',
            rtName: 'textarea',
            ui: {
                width: 300,
                style: 'cursor:default',
                emptyText: ""
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 300,
						lan: '宽度'
					},
					height: {
						value: 120,
						lan: '高度'
					},
					readOnly: {
						value: false,
						lan: '只读'
					},
					disabled: {
						value: false,
						lan: '不启用'
					},
					value: {
						value: "",
						lan: '默认值'
					},
					placeholder: {
						value: "",
						lan: '输入提示'
					},
					required: {
						value: false,
						lan: '必填'
					}
					
				}
			},
			events: {
				lan: "事件",
				value: {
					onChange: {
						value: "",
						lan: 'onChange',
						type: 'Event'
					},
					onBlur: {
						value: "",
						lan: 'onBlur',
						type: 'Event'
					},
					onFocus: {
						value: "",
						lan: 'onFocus',
						type: 'Event'
					}
				}
			}
		}
    },
	
	TextArea2: {
        text: 'TextArea2',
        tip: "多行文本域(带字数提示)",
        icon: null,
		relyJS:['easyui/src/jquery.textarea2.js'],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.TextArea',
            rtName: 'textarea2',
            ui: {
                width: 300,
                style: 'cursor:default',
                emptyText: "",
				fieldNote:"限制字数:N字，还可以输入N2字"
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 300,
						lan: '宽度'
					},
					height: {
						value: 120,
						lan: '高度'
					},
					readOnly: {
						value: false,
						lan: '只读'
					},
					disabled: {
						value: false,
						lan: '不启用'
					},
					value: {
						value: "",
						lan: '默认值'
					},
					placeholder: {
						value: "",
						lan: '输入提示'
					},
					required: {
						value: false,
						lan: '必填'
					},
					warnCharCount: {
						value: 50,
						lan: '限制字数'
					}
					
				}
			},
			events: {
				lan: "事件",
				value: {
					onChange: {
						value: "",
						lan: 'onChange',
						type: 'Event'
					},
					onBlur: {
						value: "",
						lan: 'onBlur',
						type: 'Event'
					},
					onFocus: {
						value: "",
						lan: 'onFocus',
						type: 'Event'
					}
				}
			}
		}
    },
	
	NumberSpinner: {
        text: 'NumberSpinner',
        tip: "数值微调器",
        icon: null,
		relyJS:[],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.TextField',
            rtName: 'numberspinner',
            ui: {
                width: 80,
                style: 'cursor:default',
                emptyText: ""
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 80,
						lan: '宽度'
					},
					min: {
						value: "",
						lan: '允许的最小值',
						type:"NumberField"
					},
					max: {
						value: "",
						lan: '允许的最大值',
						type:"NumberField"
					},
					increment: {
						value: 1,
						lan: '增量值',
						help:"点击微调器按钮时的增量值"
					},
					editable: {
						value: true,
						lan: '用户可输入',
						help:"定义用户是否可以往文本域中直接输入值"
					},
					readOnly: {
						value: false,
						lan: '只读'
					},
					disabled: {
						value: false,
						lan: '是否禁用'
					},
					value: {
						value: "",
						lan: '默认值'
					},
					placeholder: {
						value: "",
						lan: '输入提示',
						help:'当文本框为空时提示的文本信息'
					},
					required: {
						value: false,
						lan: '是否必填',
						help:"定义文本域是否为必填项"
					}
					
				}
			},
			events: {
				lan: "事件",
				value: {
					onSpinUp: {
						value: "",
						lan: '上调按钮时触发',
						type: 'Event'
					},
					onSpinDown: {
						value: "",
						lan: '下调按钮时触发',
						type: 'Event'
					}
				}
			}
		}
    },
	
	NumberBox: {
        text: 'NumberBox',
        tip: "数字框",
        icon: null,
		relyJS:[],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.NumberField',
            rtName: 'numberbox',
            ui: {
                width: 80,
                style: 'cursor:default',
                emptyText: "数字框"
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 80,
						lan: '宽度'
					},
					min: {
						value: "",
						lan: '允许的最小值',
						type:"NumberField"
					},
					max: {
						value: "",
						lan: '允许的最大值',
						type:"NumberField"
					},
					disabled: {
						value: false,
						lan: '是否禁用'
					},
					value: {
						value: "",
						lan: '默认值'
					}
					
				}
			}
		}
    },
	
	ComboBox: { //Bug:onChange事件
        text: 'ComboBox',
        tip: "下拉框",
        icon: null,
		relyJS:[],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.ComboBox',
            rtName: 'combobox',
            ui: {
                width: 120,
                style: 'cursor:default'
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				lan:"ui",
				value: {
					width: {
						value: 120,
						lan: '宽度'
					},
					panelWidth: {
						value: "",
						lan: '下拉面板的宽度',
						type:"NumberField"
					},
					panelHeight: {
						value: "auto",
						lan: '下拉面板的高度'
					},
					multiple: {
						value: false,
						lan: '是否可多选'
					},
					editable: {
						value: false,
						lan: '是否可直接输入'
					},
					readonly: {
						value: false,
						lan: '只读'
					},
					disabled: {
						value: false,
						lan: '是否禁用'
					},
					delay: {
						value: "200",
						lan: '延迟搜索',
						type:"NumberField"
					},
					keyHandler: {
						value: "",
						lan: '用户按键函数',
						type:"CustomEditors_TextArea"
					},
					value: {
						value: "",
						lan: '默认值'
					},
					textField: {
						value: "text",
						lan: '文本字段'
					},
					valueField: {
						value: "value",
						lan: '值字段'
					},
					mode:{
						value:"local",
						lan: '数据加载模式',
						type: 'ComboBox_1', 
						extra:{
							loadStore: [['local', 'local'],['remote', 'remote']]
						}
					},
					filter: {
						value: null,
						lan: '过滤数据函数',
						type:"CustomEditors_TextArea",
						html:"定义当 'mode' 设为 'local' 时如何过滤数据。这个函数有两个参数：\
							q：用户输入的文字\
							row：列表中的行数据。\
							返回 true 就允许显示该行。"
					},
					formatter: {
						value: null,
						lan: '行呈现格式函数',
						type:"CustomEditors_TextArea",
						html:"定义如何呈现行。这个函数有一个参数：row。"
					}
					
				}
			},
			data: Designer.controls.dataSource,
			events: {
				lan: "事件",
				value: {
					onSelect: {
						value: "",
						lan: 'onSelect',
						type: 'Event'
					},
					onUnselect: {
						value: "",
						lan: 'onUnselect',
						type: 'Event'
					},
					onBeforeLoad: {
						value: "",
						lan: 'onUnselect',
						type: 'Event'
					},
					onLoadSuccess: {
						value: "",
						lan: 'onUnselect',
						type: 'Event'
					},
					onLoadError: {
						value: "",
						lan: 'onUnselect',
						type: 'Event'
					},
					onShowPanel: {
						value: "",
						lan: '下拉面板显示的时候触发',
						type: 'Event'
					},
					onHidePanel: {
						value: "",
						lan: '下拉面板隐藏的时候触发',
						type: 'Event'
					}
				}
			}
		
		}
    },

	DateBox: {
        text: 'DateBox',
        tip: "日期框",
        icon: null,
		relyJS:[],//依赖脚本文件
        design: {//设计时
            name: 'Ext.form.DateField',
            rtName: 'datebox',
            ui: {
                fieldLabel: "选择日期",
				format:'Y-m-d'
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					panelWidth: {
						value: 180,
						lan: '面板宽度'
					},
					panelHeight: {
						value: "auto",
						lan: '面板高度'
					},
					disabled: {
						value: false,
						lan: '不启用'
					},
					value: {
						value: "",
						lan: '默认值'
					},
					required: {
						value: false,
						lan: '必填'
					},
					formatter: {
						value:"function(date){ var y = date.getFullYear(); var m = date.getMonth()+1; var d = date.getDate(); return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d); }",
						lan: '格式化函数',
						type: "function",
						help:"格式化日期的函数，此函数有一个 'date' 参数，并返回一个字符串值。"
					},
					parser: {
						value: "function(s){ if (!s) return new Date(); var ss = (s.split('-')); var y = parseInt(ss[0],10); var m = parseInt(ss[1],10); var d = parseInt(ss[2],10); if (!isNaN(y) && !isNaN(m) && !isNaN(d)){ return new Date(y,m-1,d); } else { return new Date(); } }",
						lan: '解析函数',
						type: "function",
						help:"解析日期字符串的函数，此函数有一个 'date' 字符串参数，并返回一个日期值。"
					}
					
					
				}
			},
			events: {
				lan: "事件",
				value: {
					onSelect: {
						value: "",
						lan: 'onSelect',
						type: 'Event'
					}
					
				}
			}
		}
    },
	
	CheckboxGroup: {
        text: 'CheckboxGroup',
        tip: "复选框",
        icon: null,
		relyJS:["easyui/src/jquery.checkboxgroup.js"],//依赖脚本文件
        design: {//设计时
            name: 'Ext.ux.CheckboxGroup',
            rtName: 'checkboxgroup',
            ui: {
                width: 300,
                data: Ext.encode([['lanqiu','篮球'],['zuqiu','足球']]),
                value: "['lanqiu']"
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 300,
						lan: '宽度'
					},
					displayField: {
						value: "text",
						lan: '文本字段'
					},
					valueField: {
						value: "value",
						lan: '值字段'
					},
					disabled: {
						value: false,
						lan: '不启用'
					},
					value: {
						value: "",
						lan: '默认值',
						type:"CustomEditors_TextArea",
						help:"格式要求：JSON数组字符串，[1,2]"
					},
					required: {
						value: false,
						lan: '必填'
					}
					
				}
			},
			data: Designer.controls.dataSource,
			events: {
				lan: "事件",
				value: {
					onChange: {
						value: "",
						lan: 'onChange',
						type: 'Event'
					}
					
				}
			}
		}
    },
	
	RadioGroup: {
        text: 'RadioGroup',
        tip: "单选框",
        icon: null,
		relyJS:["easyui/src/jquery.radiogroup.js"],//依赖脚本文件
        design: {//设计时
            name: 'Ext.de.RadioGroup',
            rtName: 'radiogroup',
            ui: {
                width: 300,
                data: Ext.encode([['lanqiu','篮球'],['zuqiu','足球']]),
                value: "lanqiu"
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					width: {
						value: 300,
						lan: '宽度'
					},
					displayField: {
						value: "text",
						lan: '文本字段'
					},
					valueField: {
						value: "value",
						lan: '值字段'
					},
					disabled: {
						value: false,
						lan: '不启用'
					},
					value: {
						value: "",
						lan: '默认值',
						type:"CustomEditors_TextArea"
					},
					required: {
						value: false,
						lan: '必填'
					}
					
				}
			},
			data: Designer.controls.dataSource,
			events: {
				lan: "事件",
				value: {
					onChange: {
						value: "",
						lan: 'onChange',
						type: 'Event'
					}
					
				}
			}
		}
    },
	
	Hidden: {
        text: 'Hidden',
        tip: "隐藏控件",
        icon: null,
		relyJS:["easyui/src/jquery.hidden.js"],//依赖脚本文件
        design: {//设计时
            name: 'Ext.de.Hidden',
            rtName: 'hidden',
            ui: {
                hideLabel: true,
				hideNote: true
            }
        },
        //在属性表显示的属性
        property: {
			ui: {
				value: {
					value: {
						value: "",
						lan: '默认值',
						type:"CustomEditors_TextArea"
					}
				}
			}
		}
    }
	
	
};
