Ext.ns("monitor");
    monitor.StateGrid = Ext.extend(Ext.lingo.JsonGrid, {
	    id: 'stateGrid',
		urlPagedQuery: "",
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	    trackMouseOver:true,
	    
		formConfig: [
		    {fieldLabel: '任务ID',width:50,name: 'id'},
		    {fieldLabel: '结点ID', width:50,name: 'nodeid'},
	        {fieldLabel: '任务名称', width:100, name: 'taskName'},
			{fieldLabel: '运行时间', width:100, name: 'lastLog',
				renderer:function(v,p,record){
				return v.result;
				}
	        },
			{fieldLabel: '单位', width:100, name: 'measure'},
			{fieldLabel: '是否开启', width:100, name: 'isCheck'},
			{fieldLabel: '检测时间', width:200, name: 'lastLog',
				renderer:function(v,p,record){		
					if(v.issueDate==""){
						return "未检测";
					}else{
						return v.issueDate;
					}
				}},
				{fieldLabel: '结果类型', width:100, name: 'status',
	        		renderer:function(v,p,record){		
						if(v==""){
							return "未检测";
						}else{
							return v;
						}
					}},
				{name:'draw',
					renderer:function(v,p,record){
						var date = record.data["lastLog"].issueDate;
						if(typeof(date)=="undefined"){
							return "显示细节";
						}else{
							return "<a href=\"#\" onClick=\"javascript:showWindowStatus('./service!showDetail.jhtml?taskName="+record.data["taskName"]+"')\">显示细节</a>";
						}
					}
				}
	     ],  
	buildToolbar: function() {
		
        var checkItems = new Array();
        for (var i = 0; i < this.formConfig.length; i++) {
           if(this.formConfig[i].name!='id')
        	 {
        	 	var meta = this.formConfig[i];
        	 	if(i==1)
        	 	{
        	 		 this.store.baseParams.filterTxt = meta.name;
        	 	}
        	  	 
           	 	if (meta.showInGrid === false) {
              		continue;
            	}
            	 var item = new Ext.menu.CheckItem({
                 text         : meta.fieldLabel,
                 value        : meta.name,
                 checked      : true,
                 group        : "filter",
                 checkHandler : this.onItemCheck.createDelegate(this)
            });
            checkItems[checkItems.length] = item;
          }
         }
        
        this.filterButton = new Ext.Toolbar.MenuButton({
            iconCls  : "refresh",
            text     : this.formConfig[1].fieldLabel,
            tooltip  : "选择搜索的字段",
            menu     : checkItems,
            minWidth : 105
        });
        // 输入框
        this.filter = new Ext.form.TextField({
            'name': 'filter'
        });
        
        this.tbar = new Ext.Toolbar([this.filterButton, this.filter]);
        
        this.filter.on('specialkey', this.onFilterKey.createDelegate(this));
        
        // 把分页工具条，放在页脚
        var paging = new Ext.PagingToolbar({
            pageSize: this.pageSize,
            store: this.store,
            displayInfo: true,
            displayMsg: '显示第 {0} 条到 {1} 条记录，一共 {2} 条',
            emptyMsg: "没有记录",
            plugins: [new Ext.ux.PageSizePlugin()]
        });

        this.store.load({
            params:{start:0, limit:paging.pageSize}
        });
        this.bbar = paging;
    }
   });
	 
	 