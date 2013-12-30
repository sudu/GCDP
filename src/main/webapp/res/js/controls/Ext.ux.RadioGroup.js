Ext.ux.RadioGroup=function(cfg){
 	cfg.dataSource=cfg.dataSource||cfg.data||cfg.source;
    this.addEvents("change");
    Ext.ux.RadioGroup.superclass.constructor.call(this,cfg);
}

Ext.extend(Ext.ux.RadioGroup,Ext.form.Field, {
	dataSource:''
	,inputList:null
	,name:""
	,value:"3"
	,labelFirst:false
	,onchange:function(e,t){
		this.storeValue(t.value);
		this.fireEvent("change");		
	}
	,initComponent:function(){
		this.constructor.superclass.initComponent.call(this);
	    this.autoCreate = { tag: 'input', type: 'hidden', name: this.initialConfig.name||"" };
	    
		var s=this.dataSource||this.data||this.source;
		if(Object.prototype.toString.call(s)!="[object Array]"){
			try{
		    	s=eval("("+this.dataSource+")");//[];	
			}catch(e){
				s=[this.dataSource];	//string
			}
		}
		this.dataSource=s;
	}
	,storeValue:function(val){
		this.constructor.superclass.setValue.call(this,val);
	}
	,setValue:function(val){
		var l=this.inputList;
		if(l){
			var i=l.length;
			while(i--){
				var input=l[i];
				if(val==input.value){
					input.checked=true;		
					break;
				}
			}		
		}
		this.constructor.superclass.setValue.call(this,val);
	}
	,onRender:function(ct,pos){
		this.constructor.superclass.onRender.call(this,ct,pos);		
		var cls="group";
		(this.initialConfig.readOnly&&(cls+=" readOnly"));
		var ctnr=ct.createChild({cls:cls});
		var s=this.dataSource,v=this.value;
		var len,i=len=s.length,n="radio"+new Date().valueOf();
		this.inputList=[];
		while(i--){
			var vs=s[len-i-1];
			var cfg={tag:"input",type:"radio",value:vs[0],name:n};
			(v==vs[0]&&(cfg.checked="checked"));
			(this.initialConfig.readOnly&&(cfg.disabled="disabled"));
			var unit=ctnr.createChild({"tag":"span",cls:"inputUnit"});
			if(this.labelFirst){
				var id="input"+(++Ext.Component.AUTO_ID);
				var input=unit.createChild(cfg);
				this.inputList.push(input.dom);
			}else{
				var input=unit.createChild(cfg);
				this.inputList.push(input.dom);				
				unit.createChild({tag:"label",for:input.id,html:vs[1]});
			}
			input.on("change",this.onchange,this);			
		}
	}
});

Ext.reg("radiogroup",Ext.ux.RadioGroup);