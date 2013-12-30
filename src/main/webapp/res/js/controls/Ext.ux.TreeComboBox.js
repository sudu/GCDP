/*
*树状下拉框控件 Ext.ux.TreeComboBox
*author:cici
*date:2012/11/8
*依赖：
*接口返回的数据格式：[{"text":"财经","value":"frm2018-3-","leaf":true},{"text":"娱乐","value":"frm2018-4-","leaf":false,"children":[{....}]}]
*调用实例:var treecombo = new Ext.ux.TreeComboBox({
				name:"searchPath",
				fieldLabel:'树状选择框',
				maxHeight:350,
				width:300,
				valueField:"value",
				displayField:"text",
				dataUrl:"http://m.cmpp.ifeng.com/Cmpp/runtime/interface_36.jhtml?for=combo"
		});
*/

Ext.ux.OptionTreeNode=function(attributes)
{
    Ext.ux.OptionTreeNode.superclass.constructor.call(this,attributes);
    this.value=attributes.value||'';
    this.proirity=attributes.proirity||'';
};
Ext.extend(Ext.ux.OptionTreeNode ,Ext.tree.TreeNode, {});

Ext.ux.AsyncOptionTreeNode=function(attributes)
{
    if(attributes.dataUrl){
		var loadCallback=attributes.callback||function(){};
		attributes.loader = new Ext.ux.OptionTreeLoader({dataUrl:attributes.dataUrl,listeners:{
			load:loadCallback
		}});
		attributes.listeners = {
			"beforeappend":function(tree,thisNode,node){
				if(node.attributes.dataUrl){
					node.attributes.loader.dataUrl = node.attributes.dataUrl;
				}
				node.on("beforeappend",arguments.callee);
			}
		}
	}else{
		//支持children
		attributes.loader = new Ext.tree.TreeLoader();
	}
	
    Ext.ux.AsyncOptionTreeNode.superclass.constructor.call(this,attributes);
    this.value=attributes.value||'';
    this.proirity=attributes.proirity||'';

};
Ext.extend(Ext.ux.AsyncOptionTreeNode ,Ext.tree.AsyncTreeNode, {
	
});

Ext.ux.OptionTreeLoader = function(config) {
    Ext.ux.OptionTreeLoader.superclass.constructor.call(this, config);
};

Ext.extend(Ext.ux.OptionTreeLoader, Ext.tree.TreeLoader, {
    createNode : function(attr){
        Ext.apply(attr, this.baseAttr || {});
        if(typeof attr.uiProvider == 'string'){
            attr.uiProvider = this.uiProviders[attr.uiProvider] || eval(attr.uiProvider);
        }

        return(attr.leaf ?
            new Ext.ux.OptionTreeNode(attr) :
                new Ext.ux.AsyncOptionTreeNode(attr));
    }
});


Ext.ux.TreeComboBox=function(config){
	if(config.dataSource!="" && typeof(config.dataSource)==="string"){
		try{config.dataSource = Ext.decode(config.dataSource);}catch(ex){config.dataSource=[];Ext.CMPP.warn("错误","数据源dataSource的数据格式不正确");};
	}
	if(typeof config.queryFields === "string"){
		config.queryFields = Ext.decode(config.queryFields);
	}
	if(typeof config.hiddenName == "undefined") config.hiddenName = config.name || Ext.id()
	this.url = this.dataUrl = config.dataUrl || config.url;
	var root= new Ext.ux.AsyncOptionTreeNode({
		text: '根节点',
		id:'root',
		value:'',
		"expanded": true,
		dataUrl:config.dataSource?null:this.dataUrl,
		children:config.dataSource,
		callback:function(t){
			var _this = t;
			return function(loader,node,ret){
				_this.dataSource = Ext.decode(ret.responseText);
			}
		}(this)
	});	
    var treeId=config.hiddenName + '-tree';
    var treeConfig = Ext.apply({}, {
		border:false,
		id:treeId
	}, {
		loader:config.dataSource?new Ext.tree.TreeLoader():null,
		border:false,
		rootVisible:false,
		root:root
	});
    this.tree=new Ext.tree.TreePanel(treeConfig);
	this.tree.matchMode = config.matchMode;
	Ext.apply(this.tree,{
		filterBy: function(text, by) {
			this.clearFilter();
			var me = this,
				nodesAndParents = [];
			queryFields=by;
			var root = this.getRootNode();
			
			root.cascade(function(currNode){
				currNode.expand(true);
				var reg = new RegExp((this.matchMode===1?'^':'') + Ext.escapeRe(text),'i');
				//console.log(currNode.attributes['text']);//cds
				var matcher = function(me,currNode,reg){
					for(var i=0;i<queryFields.length;i++){
						var key = queryFields[i];
						var v = currNode.attributes[key];
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
				}
				if(matcher(this,currNode,reg)) {
					me.expandPath(currNode.getPath());
	 
					while(currNode.parentNode) {
						nodesAndParents.push(currNode.id);
						currNode = currNode.parentNode;
					}
				}
			}, this);
	 
			// Hide all of the nodes which aren't in nodesAndParents
			this.getRootNode().cascade(function(n){
				if(n && !n.isRoot && nodesAndParents.indexOf(n.id)===-1) { 
					n.ui.hide();
				}
			}, this);
		},
		clearFilter: function() {
			this.getRootNode().cascade(function(n){
				n.ui.show();
			}, this);
		}
	});
    //this.hiddenField=new Ext.form.Hidden({name:config.valueName});
    var _combobox=this;
    var _tree=this.tree;
    _tree.on('click',function(node){
		var text = _combobox._getTextByValue(node.attributes[_combobox.valueField]);
		var record=_combobox.setValueAndText(node.attributes[_combobox.valueField],text);
		_combobox.collapse();
		_combobox.fireEvent('select', _combobox, record, 0);
	});
	this.onExpand=function(){
	  _tree.render(treeId);
	}
    Ext.ux.TreeComboBox.superclass.constructor.call(this,Ext.apply({
        hiddenName:config.hiddenName,
        //name:config.name,
        fieldLabel:config.fieldLabel,
        emptyText:config.emptyText,
        valueField:config.valueField||'value',
        displayField:config.displayField||'text',
        store:config.dataSource ? null:new Ext.data.SimpleStore({fields:[],data:[[]]}),
		url:typeof config.value=="undefined"?null:config.dataUrl,//预加载，确保displayField能显示
        editable:config.searchable,
        shadow:true,
		typeAhead:config.searchable,
		queryDelay:config.queryDelay||200,
		triggerAction:'all',
        autoScroll:true,
        mode: 'local',
        triggerAction:'all',
        maxHeight: config.maxHeight||200,
        tpl: '<tpl for="."><div style="height:'+config.maxHeight+'px"><div id="'+treeId+'"></div></div></tpl>',
        selectedClass:'',
        onSelect:Ext.emptyFn,
		queryFields:config.queryFields||['text']
	},config));

};

Ext.extend(Ext.ux.TreeComboBox ,Ext.form.ComboBox, {
    onRender : function(ct, position){
        Ext.ux.TreeComboBox.superclass.onRender.apply(this, arguments);
        this.on("expand",this.onExpand,this);
        if(this.allowBlank==false)
			this.setDefault();

		if(typeof(this.value)!=="undefined" && this.value!=='' && this.value.length>0 && !this.dataSource && this.dataUrl){//当有默认值时，需要预先加载数据，以匹配displayField现实
			this.requestDataUrl();	
		}		
    },
	requestDataUrl:function(callback){
		this.hasRequestData = true;
		Ext.Ajax.request({
			url: this.dataUrl,
			scope:this,
			callback:callback,
			success:function(response,options){
				this.dataSource = Ext.decode(response.responseText);
				this.tree.getRootNode().attributes.children = this.dataSource;
				this.setValue(this.getValue());
				typeof options.callback=="function" && options.callback();
			},
			failure: function(){
				Ext.CMPP.warn("错误","获取接口数据出错" + this.dataUrl);
			}
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
        
		this.expand();
		this.restrictHeight();
		
		if(qe.forceAll || q==''){
			this.tree.clearFilter();
		}else{
			this.tree.filterBy(q,this.queryFields);//非 点击钩子触发的
		}
		this.selectedNode=null;
    },
	selectNext : function(){
		var sel = this.selectedNode;
		if(sel){
			sel.childNodes.cascade(function(n){
				if(!n.isRoot && n.ui.getEl().style.display!=="none"){
					sel = n;
					return false;
				}
			},this);
		}else{
			this.selectedNode = this.tree.getRootNode().cascade(function(n){
				if(!sel && !n.isRoot && n.ui.getEl().style.display!=="none"){
					sel = n;
					return false;
				}
			},this);
		}
		sel.select();
	},
	selectPrev : function(){
		var sel = this.selectedNode;
		if(sel){
			sel.childNodes.bubble(function(n){
				n.expand(true);
				if(!n.isRoot && n.ui.getEl().style.display!=="none"){
					sel = n;
					return false;
				}
			},this);
		}else{
			this.selectedNode = this.tree.getRootNode().cascade(function(n){
				if(!sel && !n.isRoot && n.ui.getEl().style.display!=="none"){
					sel = n;
					return false;
				}
			},this);
		}
		sel.select();
		
	}, 
    setValue : function(v){
        if(v){
			if(!this.dataSource){
				this.requestDataUrl(function(t,value){
					var _this =t,v = value;
					return function(){
						var text = _this._getTextByValue(v);
						_this.setValueAndText(v,text);
					}
				}(this,v));
			}else{
				var text = this._getTextByValue(v);
				this.setValueAndText(v,text);
			}
        }else
			Ext.form.ComboBox.superclass.setValue.call(this, v);
    },
	_getTextByValue:function(v){	
		var text=null;
		var dataSource = this.dataSource;
		if(!dataSource) return v;
		var me = this;
		var l=dataSource.length;
		for(var i = 0; i < l; i++){
			(function(){
				if(text!==null) return;
				var node =arguments[0];
				if(node[me.valueField]===v){
					var path='';
					var p=node.parent;
					while(p){
						path= p[me.displayField]+">" + path;
						p = p.parent;
					}
					text = path + node[me.displayField];
				}else if(node.children){
					for(var j=0;j<node.children.length;j++){
						node.children[j].parent = node;
						arguments.callee(node.children[j]);
					}
				}
			})(Ext.applyDeep({},dataSource[i]));
		}
		return  text || v;
	},
    setValueAndText : function(v,t){
        //Ext.log(v+t);
        //var text = v==''?'根节点':t;
		var text = t;
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = v;
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
        var RecordType = Ext.data.Record.create([
            {name: this.valueField},
            {name: this.displayField}
        ]);
        var data={};
        data[this.valueField]=v;
        data[this.displayField]=t;
        var record = new RecordType(data);
        return record;
    },
    reset:function(){
        this.tree.getRootNode().collapse();
        Ext.ux.RegionField.superclass.reset.call(this);
    }
});
Ext.reg("treecombo",Ext.ux.TreeComboBox);