

Ext.ux.TimeField=function(config)
{
    //be true when reset

    this.dateTime=config.dateTime;
    Ext.ux.TimeField.superclass.constructor.call(this,config);
};

Ext.extend(Ext.ux.TimeField,Ext.form.TimeField, {
     
     /**
     * Clear any invalid styles/messages for this field
     */
    clearInvalid : function(){
        if(!this.rendered || this.preventMark){ // not rendered
            return;
        }
        this.el.removeClass(this.invalidClass);
        this.dateTime.clearInvalid();
        //check the other field for datetime
        //this.dateTime.df.isValid();
        
    },
    /**
     * Mark this field as invalid
     * @param {String} msg The validation message
     */
    markInvalid : function(msg){
        if(!this.rendered || this.preventMark){ // not rendered
            return;
        }
        this.el.addClass(this.invalidClass);
        msg = msg || this.invalidText;
        this.dateTime.markInvalid(msg);
    }
   
    
});

Ext.ux.DateField=function(config)
{
    //be true when reset
    this.isReset=false;
    this.dateTime=config.dateTime;
    Ext.ux.DateField.superclass.constructor.call(this,config);
};
 Ext.extend(Ext.ux.DateField,Ext.form.DateField,  {
     
     /**
     * Clear any invalid styles/messages for this field
     */
    clearInvalid : function(){
        if(!this.rendered || this.preventMark){ // not rendered
            return;
        }
        this.el.removeClass(this.invalidClass);
         this.dateTime.clearInvalid();
         //check the other field for datetime
        
        // this.dateTime.tf.isValid();
    },
    /**
     * Mark this field as invalid
     * @param {String} msg The validation message
     */
    markInvalid : function(msg){
        if(!this.rendered || this.preventMark){ // not rendered
            return;
        }
        this.el.addClass(this.invalidClass);
        msg = msg || this.invalidText;
         this.dateTime.markInvalid(msg);
    }
   
})

Ext.ux.DateTime = function(config) {

 // create DateField
 
 //message stack
 this.messages=new Array();
 var dateTime=this;
        var dateConfig = Ext.apply({}, {
             //name:config.name + '-date',
            format:config.dateFormat || Ext.form.DateField.prototype.format
            ,width:config.dateWidth
            ,selectOnFocus:config.selectOnFocus
            ,dateTime:dateTime
			//,style:config.style
            ,listeners:{
                  blur:{scope:this, fn:this.onBlur}
                 ,focus:{scope:this, fn:this.onFocus}
            }
        //}, config.dateConfig);
        });
        this.df = new Ext.ux.DateField(dateConfig);
        config.hiddenFormat=dateConfig.format;       

        if(this.timePosition!="none"){
	        // create TimeField
	        var timeConfig = Ext.apply({}, {
	             //name:config.name + '-time',
	            format:config.timeFormat || Ext.form.TimeField.prototype.format
	            ,width:config.timeWidth
	            ,selectOnFocus:config.selectOnFocus
				,style:'margin-left:10px'
	            ,dateTime:dateTime
	            ,listeners:{
	                  blur:{scope:this, fn:this.onBlur}
	                 ,focus:{scope:this, fn:this.onFocus}
	            }
	        //}, config.dateConfig);
	        });
	        this.tf = new Ext.ux.TimeField(timeConfig);
        	config.hiddenFormat+=" "+timeConfig.format;       
        }
        
        Ext.ux.DateTime.superclass.constructor.call(this,config);
        
        
};

Ext.extend(Ext.ux.DateTime ,Ext.form.Field, {
    /**
     * @cfg {String/Object} defaultAutoCreate DomHelper element spec
     * Let superclass to create hidden field instead of textbox. Hidden will be submittend to server
     */
     defaultAutoCreate:{tag:'input', type:'hidden'}
    /**
     * @cfg {Number} timeWidth Width of time field in pixels (defaults to 100)
     */
    ,timeWidth:100
    /**
     * @cfg {String} dtSeparator Date - Time separator. Used to split date and time (defaults to ' ' (space))
     */
    ,dtSeparator:' '
    /**
     * @cfg {String} hiddenFormat Format of datetime used to store value in hidden field
     * and submitted to server (defaults to 'Y-m-d H:i:s' that is mysql format)
     */
    ,hiddenFormat:'Y-m-d H:i:s'
    /**
     * @cfg {Boolean} otherToNow Set other field to now() if not explicly filled in (defaults to true)
     */
    ,otherToNow:true
    /**
     * @cfg {Boolean} emptyToNow Set field value to now if on attempt to set empty value.
     * If it is true then setValue() sets value of field to current date and time (defaults to false)
    /**
     * @cfg {String} timePosition Where the time field should be rendered. 'right' is suitable for forms
     * and 'below' is suitable if the field is used as the grid editor (defaults to 'right')
     */
    ,timePosition:'right' // valid values:'below', 'right'
    /**
     * @cfg {String} dateFormat Format of DateField. Can be localized. (defaults to 'm/y/d')
     */
    ,dateFormat:'m/d/y'
    /**
     * @cfg {String} timeFormat Format of TimeField. Can be localized. (defaults to 'g:i A')
     */
    ,timeFormat:'g:i A'
    /**
     * @cfg {Object} dateConfig Config for DateField constructor.
     */
    /**
     * @cfg {Object} timeConfig Config for TimeField constructor.
     */

    // {{{
    /**
     * private
     * creates DateField and TimeField and installs the necessary event handlers
     */
    ,initComponent:function() {
        // call parent initComponent
        Ext.ux.DateTime.superclass.initComponent.call(this);
        // relay events
        this.relayEvents(this.df, ['focus', 'specialkey', 'invalid', 'valid']);
        if(this.timePosition!="none"){
        	this.relayEvents(this.tf, ['focus', 'specialkey', 'invalid', 'valid']);
        }

    } // eo function initComponent
    // }}}
    // {{{
    /**
     * private
     * Renders underlying DateField and TimeField and provides a workaround for side error icon bug
     */
    ,onRender:function(ct, position) {
        // don't run more than once
        if(this.isRendered) {
            return;
        }

        // render underlying hidden field
        Ext.ux.DateTime.superclass.onRender.call(this, ct, position);

        // render DateField and TimeField
        // create bounding table
        var t;
        if('below' === this.timePosition) {
            t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',children:[
                 {tag:'tr',children:[{tag:'td', style:'padding-bottom:1px', cls:'ux-datetime-date'}]}
                ,{tag:'tr',children:[{tag:'td', cls:'ux-datetime-time'}]}
            ]}, true);
        }
        else if('right' === this.timePosition){
            t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',children:[
                {tag:'tr',children:[
                    {tag:'td',style:'padding-right:4px', cls:'ux-datetime-date'},{tag:'td', cls:'ux-datetime-time'}
                ]}
            ]}, true);
        }
        else{
            t = Ext.DomHelper.append(ct, {tag:'table',style:'border-collapse:collapse',children:[
                {tag:'tr',children:[
                    {tag:'td',style:'padding-right:4px', cls:'ux-datetime-date'}
                ]}
            ]}, true);        	
        }

        this.tableEl = t;
        this.wrap = t.wrap({cls:'x-form-field-wrap'});
        this.wrap.on("mousedown", this.onMouseDown, this, {delay:10});

        // render DateField & TimeField
        this.df.render(t.child('td.ux-datetime-date'));
        if('none' !== this.timePosition)
        	this.tf.render(t.child('td.ux-datetime-time'));

        // workaround for IE trigger misalignment bug
        if(Ext.isIE && Ext.isStrict) {
            t.select('input').applyStyles({top:0});
        }

        this.on('specialkey', this.onSpecialKey, this);
        this.df.el.swallowEvent(['keydown', 'keypress']);
        if(this.timePosition!="none"){
        	this.tf.el.swallowEvent(['keydown', 'keypress']);
        }
        // create icon for side invalid errorIcon
        if('side' === this.msgTarget) {
            var elp = this.el.findParent('.x-form-element', 10, true);
            this.errorIcon = elp.createChild({cls:'x-form-invalid-icon'});

            this.df.errorIcon = this.errorIcon;
	         if(this.timePosition!="none"){           
	            this.tf.errorIcon = this.errorIcon;
	         }
        }
        
        // we're rendered flag
        this.isRendered = true;

		if(!this.value && true === this.emptyToNow) {
            this.setValue(new Date());
        }	
    }		
    , afterRender: function () {
		    Ext.ux.DateTime.superclass.afterRender.call(this);
			this.updateHidden();
		    var t = this;
		    this.el.up("form").on("submit", function () {
	            t.updateDate();
		        if(t.timePosition!="none"){
		            t.updateTime();
		        }
		        t.updateHidden();
		    });
		} // eo function onRender
    // }}}
    // {{{
    /**
     * private
     */
    ,adjustSize:Ext.BoxComponent.prototype.adjustSize
    // }}}
    // {{{
    /**
     * private
     */
    ,alignErrorIcon:function() {
        this.errorIcon.alignTo(this.tableEl, 'tl-tr', [2, 0]);
    }
    // }}}
    // {{{
    /**
     * private initializes internal dateValue
     */
    ,initDateValue:function() {
        this.dateValue = this.otherToNow ? new Date() : new Date(1970, 0, 1, 0, 0, 0);
    }
    // }}}
    // {{{
    /**
     * Disable this component.
     * @return {Ext.Component} this
     */
    ,disable:function() {
        if(this.isRendered) {
            this.df.disabled = this.disabled;
            this.df.onDisable();
	        if(this.timePosition!="none"){
	            this.tf.onDisable();
	        }
        }
        this.disabled = true;
        this.df.disabled = true;
        if(this.timePosition!="none"){
        	this.tf.disabled = true;
        }
        this.fireEvent("disable", this);
        return this;
    } // eo function disable
    // }}}
    // {{{
    /**
     * Enable this component.
     * @return {Ext.Component} this
     */
    ,enable:function() {
        if(this.rendered){
            this.df.onEnable();
	        if(this.timePosition!="none"){
	            this.tf.onEnable();
	        }
        }
        this.disabled = false;
        this.df.disabled = false;
        if(this.timePosition!="none"){
        	this.tf.disabled = false;
        }
        this.fireEvent("enable", this);
        return this;
    } // eo function enable
    // }}}
    // {{{
    /**
     * private Focus date filed
     */
    ,focus:function() {
        this.df.focus();
    } // eo function focus
    // }}}
    // {{{
    /**
     * private
     */
    ,getPositionEl:function() {
        return this.wrap;
    }
    // }}}
    // {{{
    /**
     * private
     */
    ,getResizeEl:function() {
        return this.wrap;
    }
    // }}}
    // {{{
    /**
     * @return {Date/String} Returns value of this field
     */
    ,getValue:function() {
        // create new instance of date
        //return this.dateValue ? new Date(this.dateValue) : '';
		//return this.dateValue ? new Date(this.dateValue) : '';
		return this.dateValue instanceof Date ? this.dateValue.format(this.hiddenFormat) : '';
    } // eo function getValue
    // }}}
    // {{{
    /**
     * @return {Boolean} true = valid, false = invalid
     * private Calls isValid methods of underlying DateField and TimeField and returns the result
     */
    ,isValid:function() {
        return this.df.isValid() &&(this.timePosition!="none"?this.tf.isValid():true);
    } // eo function isValid
    // }}}
    // {{{
    /**
     * Returns true if this component is visible
     * @return {boolean} 
     */
    ,isVisible : function(){
        return this.df.rendered && this.df.getActionEl().isVisible();
    } // eo function isVisible
    // }}}
    // {{{
    /** 
     * private Handles blur event
     */
    ,onBlur:function(f) {
        // called by both DateField and TimeField blur events

        // revert focus to previous field if clicked in between
        if(this.wrapClick) {
            f.focus();
            this.wrapClick = false;
        }

        // update underlying value
        if(f === this.df) {
            this.updateDate();
        }
        else if(this.timePosition!="none"){
            this.updateTime();
        }
        this.updateHidden();

        // fire events later
        (function() {
            if(!this.df.hasFocus &&(this.timePosition!="none"?!this.tf.hasFocus:true)) {
                var v = this.getValue();
                if(String(v) !== String(this.startValue)) {
                    this.fireEvent("change", this, v, this.startValue);
                }
                this.hasFocus = false;
                this.fireEvent('blur', this);
            }
        }).defer(100, this);

    } // eo function onBlur
    // }}}
    // {{{
    /**
     * private Handles focus event
     */
    ,onFocus:function() {
        if(!this.hasFocus){
            this.hasFocus = true;
            this.startValue = this.getValue();
            this.fireEvent("focus", this);
        }
    }
    // }}}
    // {{{
    /**
     * private Just to prevent blur event when clicked in the middle of fields
     */
    ,onMouseDown:function(e) {
        this.wrapClick = 'td' === e.target.nodeName.toLowerCase();
    }
    // }}}
    // {{{
    /**
     * private
     * Handles Tab and Shift-Tab events
     */
    ,onSpecialKey:function(t, e) {
        var key = e.getKey();
        if(key == e.TAB) {
            if(this.timePosition!="none"&&t === this.df && !e.shiftKey) {
                e.stopEvent();
                this.tf.focus();
            }
            if(t === this.tf && e.shiftKey) {
                e.stopEvent();
                this.df.focus();
            }
        }
        // otherwise it misbehaves in editor grid
        if(key == e.ENTER) {
            this.updateValue();
        }

    } // eo function onSpecialKey
    // }}}
    // {{{
    /**
     * private Sets the value of DateField
     */
    ,setDate:function(date) {
        this.df.setValue(date);
    } // eo function setDate
    // }}}
    // {{{
    /** 
     * private Sets the value of TimeField
     */
    ,setTime:function(date) {
        this.tf.setValue(date);
    } // eo function setTime
    // }}}
    // {{{
    /**
     * private
     * Sets correct sizes of underlying DateField and TimeField
     * With workarounds for IE bugs
     */
    ,setSize:function(w, h) {
        if(!w) {
            return;
        }
        if('below' == this.timePosition) {
            this.df.setSize(w, h);
            this.tf.setSize(w, h);
            if(Ext.isIE) {
                this.df.el.up('td').setWidth(w);
                this.tf.el.up('td').setWidth(w);
            }
        }
        else if(this.timePosition!="right"){
            this.df.setSize(w - this.timeWidth - 4, h);
            this.tf.setSize(this.timeWidth, h);

            if(Ext.isIE) {
                this.df.el.up('td').setWidth(w - this.timeWidth - 4);
                this.tf.el.up('td').setWidth(this.timeWidth);
            }
        }
    } // eo function setSize
    // }}}
    // {{{
    /**
     * @param {Mixed} val Value to set
     * Sets the value of this field
     */
    ,setValue:function(val) {
        //alert(val.time);
        if(!val && true === this.emptyToNow) {
            this.setValue(new Date());
            return;
        }
        /*
        else if(!val) {
            this.setDate('');
            if(this.timePosition!="none")
            	this.setTime('');
            this.updateValue();
            return;
        }
        */
        val = val ? val : new Date(1970, 0 ,1, 0, 0, 0);
        var da, time;
        if(val instanceof Date) {
            this.setDate(val);
            if(this.timePosition!="none")
            	this.setTime(val);
            this.dateValue = new Date(val);
        }else if(val instanceof Object&&val.time)
        {
            var dtDate = new Date(parseInt(val.time));
            this.setDate(dtDate);
            if(this.timePosition!="none")
            	this.setTime(dtDate);
            this.dateValue = new Date(dtDate);
        }
        else {
            da = val.split(this.dtSeparator);
            this.setDate(da[0]);
            if(da[1]&&this.timePosition!="none") {
                this.setTime(da[1]);
            }
        }
        this.updateValue();
    } // eo function setValue
    // }}}
    // {{{
    /**
     * Hide or show this component by boolean
     * @return {Ext.Component} this
     */
    ,setVisible: function(visible){
        if(visible) {
            
            this.df.show();
            if(this.timePosition!="none")            
            	this.tf.show();

        
        }else{
            this.df.hide();
             if(this.timePosition!="none")
           		this.tf.hide();
        }
        return this;
    } // eo function setVisible
    // }}}
    //{{{
    ,
    reset : function(){
        this.df.reset();
	     if(this.timePosition!="none")
       		this.tf.reset();
        this.clearInvalid();
    },
    show:function() {

        return this.setVisible(true);
    } // eo function show
    //}}}
    //{{{
    ,hide:function() {
        
        return this.setVisible(false);
    } // eo function hide
    //}}}
    // {{{
    /**
     * private Updates the date part
     */
    ,updateDate:function() {

        var d = this.df.getValue();
        if(d) {
            if(!(this.dateValue instanceof Date)) {
                this.initDateValue();
                if(this.timePosition!="none"&&!this.tf.getValue()) {
                    this.setTime(this.dateValue);
                }
            }
            this.dateValue.setMonth(0); // because of leap years
            this.dateValue.setFullYear(d.getFullYear());
            this.dateValue.setMonth(d.getMonth());
            this.dateValue.setDate(d.getDate());
        }
        else {
            this.dateValue = '';
	     if(this.timePosition!="none")
            this.setTime('');
        }
    } // eo function updateDate
    // }}}
    // {{{
    /**
     * private
     * Updates the time part
     */
    ,updateTime:function() {
        var t = this.tf.getValue();
        if(t && !(t instanceof Date)) {
            t = Date.parseDate(t, this.tf.format);
        }
        if(t && !this.df.getValue()) {
            this.initDateValue();
            this.setDate(this.dateValue);
        }
        if(this.dateValue instanceof Date) {
            if(t) {
                this.dateValue.setHours(t.getHours());
                this.dateValue.setMinutes(t.getMinutes());
                this.dateValue.setSeconds(t.getSeconds());
            }
            else {
                this.dateValue.setHours(0);
                this.dateValue.setMinutes(0);
                this.dateValue.setSeconds(0);
            }
        }
    } // eo function updateTime
    // }}}
    // {{{
    /**
     * private Updates the underlying hidden field value
     */
    ,updateHidden:function() {
        if(this.isRendered) {
            var value = this.dateValue instanceof Date ? this.dateValue.format(this.hiddenFormat) : '';
            this.el.dom.value = value;
        }
    }
    // }}}
    // {{{
    /**
     * private Updates all of Date, Time and Hidden
     */
    ,updateValue:function() {

        this.updateDate();
	    if(this.timePosition!="none")
        	this.updateTime();
        this.updateHidden();

        return;
    } // eo function updateValue
    // }}}
    // {{{
    /**
     * @return {Boolean} true = valid, false = invalid
     * callse validate methods of DateField and TimeField
     */
    ,validate:function() {
        return this.df.validate() && (this.timePosition!="none"?this.tf.validate():true);
    } // eo function validate
    // }}}
    // {{{
    /**
     * Returns renderer suitable to render this field
     * @param {Object} Column model config
     */
    ,renderer: function(field) {
        
        var format = field.editor.dateFormat || Ext.ux.DateTime.prototype.dateFormat;
	    if(this.timePosition!="none")
        	format += ' ' + (field.editor.timeFormat || Ext.ux.DateTime.prototype.timeFormat);
        var renderer = function(val) {
            var retval = Ext.util.Format.date(val, format);
            return retval;
        };
        return renderer;
    },
    markInvalid:function(msg)
    {
        this.messages.push(msg);
         Ext.ux.DateTime.superclass.markInvalid.call(this,msg);
    },
    clearInvalid:function()
    {
        this.messages.pop();
        if(this.messages.length==0)
        {
             Ext.ux.DateTime.superclass.clearInvalid.call(this);
        }
        else
        {
            var msg=this.messages.pop();
            this.markInvalid(msg);
        }
    }
    
    
    // }}}

}); // eo extend

// register xtype
Ext.reg('xdatetime', Ext.ux.DateTime);
