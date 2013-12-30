/*
*description:可查询的下拉框，比如支持拼音查询、全拼查询
*author:chengds@ifeng.com
*date:2013-04-10
*/
// create namespace
Ext.ns('Ext.ux');

Ext.ux.ComboBoxQuery=function(config){
	if(typeof config.fields === "string"){
		config.fields = Ext.decode(config.fields);
	}
	if(typeof config.queryFields === "string"){
		config.queryFields = Ext.decode(config.queryFields);
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
    Ext.ux.ComboBoxQuery.superclass.constructor.call(this,Ext.apply({
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
Ext.extend(Ext.ux.ComboBoxQuery ,Ext.form.ComboBox, {
	doQuery : function(q, forceAll){
        if(q === undefined || q === null){
            q = '';
        }

		if(q!=='')this.setValue(q);//确保能提交手工输入的值，哪怕在列表项里没有相关的选项。
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
	getValue : function(){
        if(this.valueField){
            return typeof this.value != 'undefined' && this.value!='' ? this.value : (this.getText()===''?'':this.getText());
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    },
	getText:function(){
		return this.el.dom.value;
	}

}); // eo extend

// register xtype
Ext.reg('qcombo', Ext.ux.ComboBoxQuery);

