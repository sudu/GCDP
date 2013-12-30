/*
*树状下拉框控件
*author:cici
*date:2013/04/18
*依赖：Ext.ux.TreeComboBox.js
*接口返回的数据格式：[{"text":"财经","value":"frm2018-3-","leaf":true},{"text":"娱乐","value":"frm2018-4-","leaf":false,"children":[{....}]}]
*/
Ext.ux.MultiSelectTree=function(config){
	if(config.value && typeof config.value === "string"){
		config.value = Ext.decode(config.value);
	}
	if(typeof config.hiddenName == "undefined") config.hiddenName = config.name || Ext.id()

    Ext.ux.MultiSelectTree.superclass.constructor.call(this,Ext.apply({
		value:config.value,
		rootRes:'../res/'
	},config));
}
	
Ext.extend(Ext.ux.MultiSelectTree, Ext.ux.TreeComboBox ,{
	hasRequestData:false,
	initComponent : function() {

		Ext.ux.MultiSelectTree.superclass.initComponent.apply(this, arguments);
		// remove selection from input field
		this.afterRender = this.afterRender.createSequence(function() {
			var selectedBoxEl = this.el.insertSibling({
				tag:'div',
				cls:'x-form-text x-form-field cmpp-MultiSelectQuery-display',
				style:'width: '+ parseInt(this.el.getStyle("width")) +'px; float: left;cursor:'+ (this.searchable?'text':'pointer') +';'
			},'after');
			selectedBoxEl.insertFirst(this.el);
			this.selectedBoxEl = selectedBoxEl;
			this.el.setWidth(60);
			this.el.setStyle({"border":"none"});
			this.el.dom.style.cssFloat = "left";
			this.el.parent().setStyle({"height":"auto"});
			this.setValue(this.value);
			
			//绑定事件	
			if(this.searchable){			
				selectedBoxEl.on('click',function(){
					this.el.dom.focus();
				},this);
			}else{
				selectedBoxEl.on('click',function(e,target){
					if(target.id !== this.el.id){
						this.onTriggerClick();
					}
				},this);
			}
			this.tree.on('click',function(node){
				var text = this._getTextByValue(node.attributes[this.valueField]);
				var record=this.setValueAndText(node.attributes[this.valueField],text);
				this.collapse();
				this.select(record, 0);
			},this);
		});
	},
	select : function(record, index){
		if(this.fireEvent('beforeselect', this, record, index) !== false){
			this._appendSelectedItem(record.data[this.valueField || this.displayField],record.data[this.displayField]);
			this.collapse();
			this.fireEvent('select', this, record, index);
		}
		this.el.dom.focus();
	},
		
	//追加选中的项
	_appendSelectedItem:function(v,text){
		this.el.dom.value = "";//清空
		var existId = this.value.indexOf(v);
		if(existId==-1){
			var selItemEl = this.el.insertSibling({
				tag:'div',
				cls:'cmmp-mqselect-selectedItem',
				style:'',
				html:'<span class="text">' + text + '</span>  '
			},'before');
			var delEl = selItemEl.createChild({
				tag:'span',
				cls:'close',
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
			this.syncValue();
		}
		
	},
	setValue: function(v){
		//v为JSON格式数组
		try{
			if(typeof v==="string") v = Ext.decode(v);
		}catch(ex){
			v='';
		}
		this.value = v||[];
		if(v){
			if(!this.dataSource && !this.hasRequestData){
				this.hasRequestData = true;
				this.requestDataUrl(function(t){
					var _this =t;
					return function(){
						_this.setValue(_this.getValue());
					}
				}(this));
			}else{
				if(this.hiddenField) this.hiddenField.value = Ext.encode(v);
				if(this.selectedBoxEl){
					this._clearSelectedItem();
					//初始化显示选中项的文本
					for(var i = 0;i <v.length;i++){
						var text = this._getTextByValue(v[i]);
						this._appendSelectedItem(v[i],text);
					}
				}
			}
        }else
			if(this.hiddenField) this.hiddenField.value="";
	},
	syncValue:function(){
		try{
			this.hiddenField.value = Ext.encode(this.value);
		}catch(e){
			
		}
	},
	setValueAndText : function(v,t){
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
	getValue : function(){
		if(!this.value || this.value.length==0) 
			return "";
		else
			return Ext.encode(this.value);
    },	
	getText:function(){
		var t = [];
		/*
		if(this.value){
			for(var i=0;i<this.value.length;i++){
				t.push(this._getTextByValue(this.value[i]));
			}
		}
		*/
		var pre = this.el.prev();
		while(pre){
			var texts = pre.dom.textContent.split('>');
			var text = texts[texts.length-1].trimRight();
			t.push(text);
			pre = pre.prev();
		}
		return t.join(',');
	},
	_removeSelectedItem:function(obj,v){
		var pos = this.value.indexOf(v);
		if(pos!==-1){
			this.value.splice(pos,1);
		}
		Ext.fly(obj).parent().remove();
		this.syncValue();
	},
	_clearSelectedItem:function(){
		var pre = this.el.prev();
		while(pre){
			pre.remove();
			pre = this.el.prev();
		}
		this.value=[];
		this.syncValue();
	}
});
Ext.reg("mstree",Ext.ux.MultiSelectTree);	
