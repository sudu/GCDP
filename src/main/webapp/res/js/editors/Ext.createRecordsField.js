(function(){
	var fm=Ext.form;	
   
    var GridView = Ext.extend(Ext.grid.GridView, {
        columns:null
    	,appendNewRow: function () {
            var cs = this.getColumnData();
            //delete cs[2].style;
      
            var rsJson={},columns=this.columns,i=columns.length,len=i-1,clm,str;
            var num=(new Date()).valueOf();
            while(i--){
            	clm=columns[len-i];
             	var id=clm.cellid="id"+(++num);
	           	str="<div id='" + id + "'></div>";
            	rsJson[clm.dataIndex]=str;
            }
            str = "<div class='rightMark' id='" + id  + "'></div>";
        	rsJson["EMPTY"]=str;
            var rs = [new Ext.data.Record(rsJson, rsJson[columns[0].dataIndex])];
            	
            var ds = this.grid.store;
            var rowHTML = this.doRender(cs, rs, ds, 0, len+1);
            rowHTML = rowHTML.replace("x-grid3-row", "");
            Ext.DomHelper.append(this.mainBody, rowHTML);

            len=columns.length-1;//omit ["EMPTY"]
            while(len--){
            	clm=columns[len];
            	var fld=clm.newField?(clm.newField["renderTo"]=clm.cellid)&&Ext.ComponentMgr.create(clm.newField):new Ext.form.TextField({ "selectOnFocus": true, "emptyText": clm.header, "renderTo": clm.cellid ,"allowBlank": !clm.required,style:"width:95%",vtype:clm.vtype });
            	clm.field=fld;
            }

            var g = this.grid;
            Ext.get(columns[columns.length-1].cellid).on("click", function () { g.addItem(); });
			this.on("deactivate",function(p){
				alert(p);
			},this);
            
            var div=this.mainBody.dom.lastChild;
			this.appendNewRow=function(){
            	this.mainBody.dom.appendChild(div);
			}
        }
        , layout: function () {
            this.appendNewRow();
            GridView.superclass.layout.call(this);
        }
        , init: function (grid) {
            GridView.superclass.init.call(this, grid);
            var html = this.templates.row.html;
            this.templates.row.html = html.replace('cellpadding="0" style="{tstyle}', 'cellpadding="0" style="table-layout:fixed;{tstyle}');
            this.templates.row.compile();
        }

    });

	
var createRecordsField=function(name,clms){
	
	var RecordsField=Ext.extend(Ext.form.Field,{
		autoCreate:{ tag: 'input', type: 'hidden'}	
		,list:null
		,setValue:function(v){
			if(typeof v =="object")
				v=Ext.encode(v);
			RecordsField.superclass.setValue.call(this,v);
			if(this.list){
	            this.list.store.loadData(this.getData());
				this.list.refreshView();
			}
		}

		,getData:function(){
			var val=this.getValue();
			var data=[];
			if(!Ext.nore(val)){
				data=Ext.decode(val);
				if(Object.prototype.toString.call(data)!="[object Array]")
					data=[data];
			}
			return {data:data};
		}
		,onRender:function(ctnr,pos){
			columns=Ext.applyDeep([],clms);
			RecordsField.superclass.onRender.call(this,ctnr,pos);
			
			columns.push({ dataIndex: 'EMPTY', width: 31, menuDisabled: true,cls:"faultMark"});
			
			var fields=[],i=columns.length,len=i-1,fld;
			while(i--){
				fld=columns[len-i];
				fields.push(fld.dataIndex);
				if(!fld.editor){
					fld.editor=new Ext.form.TextField({allowBlank:fld.allowBlank,vtype:fld.vtype});
				}
			}
			
			var t=this;
			var store=new Ext.data.JsonStore({
		        data:this.getData(), 
				autoLoad:true,
				root : "data"
				,fields: fields
			});
			var cm= new Ext.grid.ColumnModel(columns);
			this.list=new Ext.grid.EditorGridPanel({
				store:store
				,cm:cm
				,view:new GridView({columns:columns})
				,renderTo:ctnr
				,clicksToEdit:1
				,enableHdMenu:false
				,listeners:{
					afteredit:function(){
						this.afterEdit();
					}
				}
				,afterEdit:function(){
				       var datar = new Array();     
				       var jsonDataEncode = "";  
				       var records = store.getRange(); 
				       var len=records.length;
				       for (var i = 0; i < len; i++) { 
				    	   var di=records[i].data;
				    	   delete di["EMPTY"];
				            datar.push(di);      
				       } 
						t.setValue(datar);
				}
	            , addItem: function () {
	            	if(t.max&&this.store.getCount()>=t.max){
	            		return Ext.Msg.alert("提示","已达到允许的最大数："+t.max+"条。");	
	            	}
	            	var len=columns.length-1;//omit the last one;
	            	var d={};
	            	while(len--){
	            		var clm=columns[len];
		                if (!clm.field.validate())
		                    return;
		                else{
		                	d[clm.dataIndex]=clm.field.getValue();
							clm.field.setValue('');
						}
	            	}
	                var ds = this.store;
	                ds.add(new Ext.grid.PropertyRecord(d));
	                this.refreshView();
					this.afterEdit();
	            }
	            , deleteItem: function (rowIndex) {	            	
	                var ds = this.store;
	                if (t.min&&ds.getCount() <= t.min) {
	            		return Ext.Msg.alert("提示","已达到允许的最小数："+t.min+"条。");	
	                }
	                var v = this.view;
	                ds.data.removeAt(rowIndex);
	                v.removeRows(rowIndex, rowIndex);
	                this.refreshView();
					this.afterEdit();
	            }
	            , refreshView: function () {
	                var v = this.view;
	                v.scroller.dom.style.height = "auto";
	                v.scroller.dom.style.width = "auto";
	                v.el.dom.style.height = "auto";
	                v.refresh();
	            }
	            , afterRender: function () {
	                this.selModel.on('beforecellselect', function (sm, rowIndex, colIndex) {
	                    if (colIndex === columns.length-1) {
	                        this.deleteItem(rowIndex);
	                        return false;
	                    }
	                }, this);
	                Ext.grid.EditorGridPanel.superclass.afterRender.apply(this, arguments);
	                if (this.source) {
	                    this.setSource(this.source);
	                }
	            }
	            , processEvent: function (name, e) {
	                this.fireEvent(name, e);
	                var t = e.getTarget();
	                var v = this.view;
	                var header = v.findHeaderIndex(t);
	                if (header !== false) {
	                    this.fireEvent("header" + name, this, header, e);
	                } else {
	                    var row = v.findRowIndex(t);
	                    if (row == undefined)//for row to add item;
	                        return;
	                    var cell = v.findCellIndex(t);
	                    if (row !== false) {
	                        this.fireEvent("row" + name, this, row, e);
	                        if (cell !== false) {
	                            this.fireEvent("cell" + name, this, row, cell, e);
	                        }
	                    }
	                }
	            }			
			});
		}
	});	
	if(name)
		Ext.reg(name,RecordsField); 
	return RecordsField;
}

Ext.createRecordsField=createRecordsField;
})();