/*
*description:可查询可多选的下拉框，比如支持拼音查询、全拼查询
*author:chengds@ifeng.com
*date:2013-04-12
*/
// create namespace
Ext.ns('Ext.ux');

Ext.ux.MultiSelectQuery=function(config){
	if(typeof config.fields === "string"){
		config.fields = Ext.decode(config.fields);
	}
	if(typeof config.queryFields === "string"){
		config.queryFields = Ext.decode(config.queryFields);
	}
	if(config.value && typeof config.value === "string"){
		config.value = Ext.decode(config.value);
	}else{
		config.value = [];
	}
    if(config.dataSource){
		if(typeof config.dataSource === "string"){
			config.dataSource = Ext.decode(config.dataSource);
		}
		var store=new Ext.data.JsonStore({　
			fields:config.fields||['text'],
			//autoLoad:true,
			root:config.root?config.root:'',
			data:config.dataSource||config.data
		});
		delete config.dataSource;
	}else{		
		var store=new Ext.data.JsonStore({　
			fields:config.fields||['text'],
			autoLoad:true,
			root:config.root?config.root:'',
			url:config.url,
			listeners :{
				scope:this,	
				load :function(store,records,opt){
					this.setValue(this.getValue());//异步请求的数据，需要在请求之后重新赋值，以匹配displayField的显示
				}
			}
		});
	}
    Ext.ux.MultiSelectQuery.superclass.constructor.call(this,Ext.apply({
		rootRes:'../res/',
        typeAhead:true,
		triggerAction:'all',
		forceSelection:false,
		matchMode:config.matchMode||1,
		mode:'local',
		queryFields:config.queryFields||['text'],
		valueField:config.valueField||'value',
        displayField:config.displayField||'text',
		store:store
	},config));
};
Ext.extend(Ext.ux.MultiSelectQuery ,Ext.form.ComboBox, {
	initComponent : function() {

		Ext.ux.MultiSelectQuery.superclass.initComponent.apply(this, arguments);
		// remove selection from input field
		this.afterRender = this.afterRender.createSequence(function() {
			var selectedBoxEl = this.el.insertSibling({
				tag:'div',
				cls:'x-form-text x-form-field cmpp-MultiSelectQuery-display',
				style:'width: '+ parseInt(this.el.getStyle("width")) +'px; float: left;cursor:text;'
			},'after');
			selectedBoxEl.insertFirst(this.el);
			this.selectedBoxEl = selectedBoxEl;
			this.el.setWidth(60);
			this.el.setStyle({"border":"none"});
			this.el.dom.style.cssFloat = "left";
			this.el.parent().setStyle({"height":"auto"});
			this.setValue(this.value);
			
			//绑定事件			
			selectedBoxEl.on('click',function(){
				this.el.dom.focus();
			},this);
		});
	},
	doQuery : function(q, forceAll){
        if(q === undefined || q === null){
            q = '';
        }

        var qe = {
            query: q,
            forceAll: forceAll,
            combo: this,
            cancel:false
        };
        if(this.fireEvent('beforequery', qe)===false || qe.cancel){
            return false;
        }
        q = qe.query;
        forceAll = qe.forceAll;
        if(forceAll === true || (q.length >= this.minChars)){
            if(this.lastQuery !== q){
                this.lastQuery = q;
                if(this.mode == 'local'){
                    this.selectedIndex = -1;
                    if(forceAll){
                        this.store.clearFilter();
                    }else{
						this.store.filterBy(function(record,id){
							var reg = new RegExp((this.matchMode===1?'^':'') + Ext.escapeRe(this.lastQuery),'i');
							for(var i=0;i<this.queryFields.length;i++){
								var key = this.queryFields[i];
								//如果是数组 todo
								var v = record.data[key];
								if(Ext.isArray(v)){
									for(var j=0;j<v.length;j++){
										var isMatch = reg.test(v[j]);
										if(isMatch) return isMatch;
									}
								}else{
									var isMatch = reg.test(v);
									if(isMatch) return isMatch;
								}
							}
							return false;
						},this);
                    }
                    this.onLoad();
                }else{
                    this.store.baseParams[this.queryParam] = q;
                    this.store.load({
                        params: this.getParams(q)
                    });
                    this.expand();
                }
            }else{
                this.selectedIndex = -1;
                this.onLoad();
            }
        }
    },
	onLoad : function(){
        if(!this.hasFocus){
            return;
        }
        if(this.store.getCount() > 0){
            this.expand();
            this.restrictHeight();
            if(this.lastQuery == this.allQuery){
                if(this.editable){
                    this.el.dom.select();
                }
                if(!this.selectByValue(this.value, true)){
                    this.select(0, true);
                }
            }else{
                this.selectNext();
            }
        }else{
            this.onEmptyResults();
        }
	},
	onSelect : function(record, index){
		if(this.fireEvent('beforeselect', this, record, index) !== false){
			this._appendSelectedItem(record.data[this.valueField || this.displayField],record.data[this.displayField]);
			this.collapse();
			this.fireEvent('select', this, record, index);
		}
	},
	//追加选中的项
	_appendSelectedItem:function(v,text){
		this.el.dom.value = "";//清空
		var existId = this.value.indexOf(v);
		if(existId==-1){
			var selItemEl = this.el.insertSibling({
				tag:'div',
				cls:'cmmp-mqselect-selectedItem',
				style:'background:#E0E5EE;border:1px solid #CCD5E4;display: block; float: left; height: 18px; line-height: 18px; margin: 0px 5px 1px 0px; padding: 0px 5px;"',
				html:text + "  "
			},'before');
			var delEl = selItemEl.createChild({
				tag:'img',
				src:this.rootRes + 'img/runTime/del.gif',
				style:'border: 0px none; cursor: pointer;',
				title:'移除'
			});
			delEl.on('click',function(v){
				var _v = v;
				return function(e,obj){
					e.stopPropagation();
					this._removeSelectedItem(obj,_v);
				}
			}(v),this);			
			this.value.push(v);
		}
		
	},
	setValue: function(v){
		//v为JSON格式数组
		try{
			if(typeof v==="string") v = Ext.decode(v);
		}catch(ex){
			v=[];
		}		
		this.value = v||[];
		if(this.hiddenField) this.hiddenField.value = Ext.encode(v);
		if(this.selectedBoxEl){
			this._clearSelectedItem();
			//初始化显示选中项的文本
			for(var i = 0;i <v.length;i++){
				var row = this.store.find(this.valueField,v[i]);
				var text;
				if(row==-1){
					text = v[i];
				}else{
					text = this.store.getAt(row).data[this.displayField];
				}
				this._appendSelectedItem(v[i],text);
			}
		}
	},
	getValue : function(){
		if(this.value.length==0) 
			return "";
		else
			return Ext.encode(this.value);
    },	
	getText:function(){
		//todo
		return this.el.dom.value;
	},
	_removeSelectedItem:function(obj,v){
		var pos = this.value.indexOf(v);
		if(pos!==-1){
			this.value.splice(pos,1);
		}
		Ext.fly(obj).parent().remove();
		if(this.hiddenField) this.hiddenField.value = Ext.encode(v);
	},
	_clearSelectedItem:function(){
		var pre = this.el.prev();
		while(pre){
			pre.remove();
			pre = this.el.prev();
		}
		this.value=[];
		if(this.hiddenField) this.hiddenField.value = "";
	}

}); // eo extend

// register xtype
Ext.reg('qmselect', Ext.ux.MultiSelectQuery);

