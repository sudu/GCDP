Ext.ns("monitor");
    monitor.LogGrid = Ext.extend(Ext.lingo.JsonGrid, {
	    id: 'logGrid',
		urlPagedQuery: "./service!pagedLog.jhtml",
	    dlgWidth: 300,
	    dlgHeight: 400,
	    autoHeight:true,
	    autoWidth:true,
	    trackMouseOver:true,

		formConfig: [
	        {fieldLabel: 'ID', width:50,name: 'id'},
	        {fieldLabel: '任务名称', width:100,name: 'taskName'},
			{fieldLabel: '结果数值', width:100,name: 'result'},
			{fieldLabel: '检测时间', width:200,name: 'issueDate'},
			{
				fieldLabel: '备注',
				width:100,
				name: 'details',
				renderer:function(v,metadata,record){
					metadata.attr = 'ext:qtitle="报告内容"'+ 'ext:qtip="'+v+'"'; 
					return v;	
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
    },
   });
	 
	 