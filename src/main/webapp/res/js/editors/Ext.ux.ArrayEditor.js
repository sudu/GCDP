(function () {
    (Ext.ux || (Ext.ux = {}));
    var field = Ext.createRecordsField("RecordsField_Array",[
	   {
	       header: "数组项",
	       dataIndex: 'item',
	       width: 350,
	       allowBlank:false
	    }
	]);

    Ext.createWinEditor({name:'Array',baseField:field,override:{
   		setValue:function(v){
  			if(typeof v ==="string"&&!Ext.nore(v))
  				v=Ext.decode(v);
  			if(Object.prototype.toString.call(v)=="[object Array]"){
  				var o=[],i=v.length;
  				while(i--){
  					o.unshift({item:v[i]});	
  				};
  				v=Ext.encode(o);
  				Ext.ux.ArrayEditor.superclass.setValue.call(this,v);
  			}
  		}
  		,getValue:function(){
			var v=Ext.ux.ArrayEditor.superclass.getValue.call(this);
			v=Ext.decode(v);
  			if(Object.prototype.toString.call(v)=="[object Array]"){
  				var o=[],i=v.length;
  				while(i--){
  					o.unshift(v[i]["item"]);	
  				};
  				v=Ext.encode(o);
  			}
  			return v;
  		}  	
    }});  	
})();