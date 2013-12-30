Ext.namespace("Ext.ux");

/**
 * 对textField进行扩展，使其能接收两个日期的值。
 * @class Ext.ux.DateFieldExtent
 * @extends Ext.form.TextField
 */
Ext.ux.DateFieldExtent = Ext.extend(Ext.form.TextField, {
	
    useDefaultDate: false,
    x_minDate: "1970-01-01",
    x_maxDate: "2099-12-31",

    initComponent: function(){
    	this.x_init_param();
        Ext.ux.DateFieldExtent.superclass.initComponent.call(this);

    },
    
    onRender: function(ct, position){
        
        Ext.ux.DateFieldExtent.superclass.onRender.call(this, ct, position);
        this.x_init_els(ct, position);
        this.x_init_event();

    },
    
    setValue : function(v,isSys){
    	
    	Ext.ux.DateFieldExtent.superclass.setValue.apply(this, arguments);
    	if(!isSys)
    	{
	    	if(this.dateBegin && this.dateEnd)
	    	{
		    	if(typeof v !=="undefined" && v !=="")
		    	{
		    		var a = v.split(",");
		    		this.dateBegin.setValue(a[0]);
		    		this.dateEnd.setValue(a[1]||a[0]);
		    		
		    	}
		    	else
		    	{
		    		this.dateBegin.setValue("");
		    		this.dateEnd.setValue("");
		    	}
		    	this.dateBegin.clearInvalid();
		    	this.dateEnd.clearInvalid();
	    	}
    	}
    },

	
    /**
     * 初始化参数，将textField的值进行拆分，将拆分后的值当做内部俩个日期对象的初始值。
     */
    x_init_param: function(){

        var defaultDate = Ext.util.Format.date(new Date(),"Y-m-d");
    	if(typeof this.value !=="undefined" && this.value !== "")
    	{
    		var a = this.value.split(",");
    		this.dateBeginValue = a[0];
    		this.dateEndValue = a[1] || this.useDefaultDate ? defaultDate : this.x_maxDate;

    	}
    	else
    	{
            if(this.useDefaultDate)
            {
                this.value = defaultDate + "," + defaultDate;
                this.dateBeginValue = defaultDate ;
                this.dateEndValue = defaultDate ;
            }
            else
            {
                // this.value = this.x_minDate + "," + this.x_maxDate;
                this.value = "";
                this.dateBeginValue = "" ;
                this.dateEndValue = "" ;
            }
    	}

    },
	
    /**
     * 初始化附加的ext.el对象
     * 在内部插入一个容器，在这个容器内部放入3个子容器，分别放入开始日期对象，文字，结束日期对象。
     * 日前对象的allowBlank，readOnly属性来自与textfield的，同其保持一致
     * @param {} ct
     * @param {} position
     */
    x_init_els: function(ct, position){

        this.el.dom.style.display = "none";
        this.dateWrap = ct.createChild({tag:'div',style:'width: 220px'}, position);
        this.dateBeginWrap = this.dateWrap.createChild({tag:'div',style:"float:left;"});
        this.dateInfo = this.dateWrap.createChild({tag:'div',html:"到",style:"float:left; padding: 3px 5px 0px 5px"});
        this.dateEndWrap = this.dateWrap.createChild({tag:'div',style:"float:left;"});
       	this.dateBegin = new Ext.ux.DateFieldPower({
       		format:"Y-m-d",
       		renderTo : this.dateBeginWrap,
       		allowBlank : this.allowBlank,
       		value: this.dateBeginValue,
       		//将结束日期设为开始日期的最大值
       		// maxValue:this.dateEndValue,
       		readOnly : this.readOnly,
       		width: 90
       	});
       	this.dateEnd = new Ext.ux.DateFieldPower({
       		format:"Y-m-d",
       		renderTo : this.dateEndWrap,
       		allowBlank : this.allowBlank,
       		value: this.dateEndValue,
       		//将开始日期设为结束日期的最小值       		
       		// minValue: this.dateBeginValue,
       		readOnly : this.readOnly,
       		width: 90
       	});
       	
		//清除掉两个日期对象的name，在form提交时就不会把他们的值提交上去了。
       	this.dateBegin.el.dom.name = "";
       	this.dateEnd.el.dom.name = "";
		
       	//清除掉出错状态
       	this.dateBegin.clearInvalid();
       	this.dateEnd.clearInvalid();

    },
	
    /**
     * 初始化事件
     */
    x_init_event: function(){

    	//绑定开始时间对象的setValue事件
    	this.dateBegin.on("setValue", function(EdateFile,value){
			if(typeof value !== "object" && value !=="")
			{
   				value = new Date(value);
			}
			//由于ext2.02没有setMinValue()的方法，
    		//这里将日期对象中的menu对象强制设置为null，迫使其每次打开都要重新建立，用来重置结束日期对象的minValue
			// this.dateEnd.menu = null;
			// this.dateEnd.minValue = value;
			//设置textField的值。
    		this.x_setValue(true);
			
    	}, this);

        this.dateBegin.on("change", function(EdateFile, newValue, oldValue){
            this.x_setValue(true);
        },this);
		
    	//绑定结束对象的setValue事件
    	this.dateEnd.on("setValue", function(EdateFile,value){
    		if(typeof value !== "object" )
    		{
    			value = new Date(value);
    		}
  			//由于ext2.02没有setMaxValue()的方法，
    		//这里将日期对象中的menu对象强制设置为null，迫使其每次打开都要重新建立，用来重置结束日期对象的maxValue
	   		// this.dateBegin.menu = null;
   			// this.dateBegin.maxValue = value;
	 		//设置textField的值。			
			this.x_setValue(true);    			
    	}, this);

        this.dateEnd.on("change", function(EdateFile, newValue, oldValue){
            this.x_setValue(true);
        },this);

    },
	
    //设置textField的值
    x_setValue: function(isSys){
		
    	//如果开始时间和结束时间不为空并且都通过了校验，则对textField进行赋值
//     	if(this.dateBegin.getRawValue() !== "" && this.dateEnd.getRawValue() !=="" && this.dateBegin.validate() && this.dateEnd.validate())
//     	{
//     		this.setValue(this.dateBegin.getRawValue() + "," + this.dateEnd.getRawValue(),isSys);
//     	}
//     	else
//     	{
//     		//这里到底在失败以后要不要将其设置为""呢。
// //    		this.setValue("");
//     	}
        
        var begin, end;

        if(this.dateBegin.getRawValue() !== "" && this.dateBegin.validate())
        {
            begin = this.dateBegin.getRawValue();
        }
        else
        {
            begin = this.x_minDate;
        }

        if(this.dateEnd.getRawValue() !=="" && this.dateEnd.validate())
        {
            end = this.dateEnd.getRawValue();
        }
        else
        {
            end = this.x_maxDate;
        }
        if(begin === this.x_minDate && end === this.x_maxDate)
        {
            this.setValue("", isSys);
        }
        else
        {
            this.setValue(begin + "," + end, isSys);
        }

    },
    
    /**
     * 扩展textfield的校验
     * @param {String} value textField值
     * @return {Boolean}
     */
    validateValue: function(value){
    	
    	//加这个判断是为了在第一次render textField时候，开始日期对象还没有生成。
		if(this.dateBegin)
		{
			//这里如果开始日期没有校验通过，则这个textField校验返回错误。
			if(!this.dateBegin.validate())
			{
				return false;
			}
		}
		//加这个判断是为了在第一次render textField时候，结束日期对象还没有生成。
		if(this.dateEnd)
		{
			//这里如果开始日期没有校验通过，则这个textField校验返回错误。
			if(!this.dateEnd.validate())
			{
				return false;
			}
		}

        if(this.dateBegin && this.dateEnd && this.dateBegin.getRawValue() && this.dateEnd.getRawValue())
        {
            var dateBeginValue = this.dateBegin.getRawValue();
            var dateEndValue = this.dateEnd.getRawValue();
            if(new Date(dateBeginValue).valueOf() > new Date(dateEndValue).valueOf())
            {
                this.dateBegin.markInvalid("开始日期不能大于结束日期");
                this.dateEnd.markInvalid("结束日期不能小于开始日期");
                return false;
            }
            // console.log(dateBeginValue,dateEndValue);
        }
    		
    	return Ext.ux.DateFieldExtent.superclass.validateValue.call(this, value);
    }


    
});

Ext.reg('datefieldextent', Ext.ux.DateFieldExtent);

/**
 * 增强DateField，在setValue方法中添加了一个setValue的自定义事件
 * @class Ext.ux.DateFieldPower
 * @extends Ext.form.DateField
 */
Ext.ux.DateFieldPower = Ext.extend(Ext.form.DateField,{

    initComponent: function(){

        Ext.ux.DateFieldPower.superclass.initComponent.call(this);
        this.addEvents("setValue");

    },

    setValue : function(date){
        Ext.ux.DateFieldPower.superclass.setValue.call(this, date);
        this.fireEvent('setValue', this, date);

    }

});

Ext.reg('xDateFieldPower', Ext.ux.DateFieldPower);
