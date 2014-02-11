<<<<<<< HEAD
 /**
 * textfield - jQuery EasyUI Extend
 * 单行文本域
 * @class textfield
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.textfield.js
 */
(function($){
	function init(target){
		var state = $(target);
		return state;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'textfield').options;
		var textfield = $.data(target, 'textfield').textfield;
		if (width) opts.width = width;
		if (isNaN(opts.width)){
			opts.width = textfield.outerWidth();
		}
		textfield._outerWidth(opts.width);
	}
	function setProperty(target){
		var opts = $.data(target, 'textfield').options;
		var textfield = $.data(target, 'textfield').textfield;
		opts.type = opts.type || "text";
		opts.placeholder && textfield.prop("placeholder",opts.placeholder);
		textfield.prop("type",opts.type);
	}
	function bindEvents(target){
		var state = $.data(target, 'textfield');
		var opts = state.options
		var input = state.textfield;
		
		input.bind('focus.textfield', opts.onFocus).bind('change.textfield', opts.onChange)
		.bind('blur.textfield', opts.onBlur);
		
		
	}
	
	function initValue(target){
		var state = $.data(target, 'textfield');
		var opts = state.options;
		var input = state.textfield;
		input.val(opts.value);
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'textfield');
			var opts = state.options;
			var input = state.textfield;
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		var state = $.data(target, 'textfield');
		var opts = state.options;
		var input = state.textfield;
		if (disabled){
			opts.disabled = true;
			input.attr('disabled', true);
		} else {
			opts.disabled = false;
			input.removeAttr('disabled');
		}
	}
	
	$.fn.textfield = function(options, param){
		if (typeof options == 'string'){
			return $.fn.textfield.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'textfield');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'textfield', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.textfield.defaults, $.fn.textfield.parseOptions(this), options),
					textfield: init(this)
				});
			}
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			setProperty(this);
			validate(this);
		});
	}
	
	$.fn.textfield.methods = {
		options: function(jq){
			return $.data(jq[0], 'textfield').options;
		},
		textbox: function(jq){
			return $.data(jq[0], 'textfield').textfield;
		},
		getValue: function(jq){
			return this.textbox(jq).val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).textfield('options').value = value;
				$(this).textfield('textbox').val(value);
				$(this).textfield('textbox').blur();
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'textfield').textfield.attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'textfield').textfield.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.textfield.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder')
		};
	};
	/*默认配置*/
	$.fn.textfield.defaults = {
		width:300,
		disabled:false,
		readOnly:false,
		required:false,
		onChange: function(e){},
		onFocus: function(e){},
		onBlur:function(e){}
	};
})(jQuery);
	
=======
 /**
 * textfield - jQuery EasyUI Extend
 * 单行文本域
 * @class textfield
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.textfield.js
 */
(function($){
	function init(target){
		var state = $(target);
		return state;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'textfield').options;
		var textfield = $.data(target, 'textfield').textfield;
		if (width) opts.width = width;
		if (isNaN(opts.width)){
			opts.width = textfield.outerWidth();
		}
		textfield._outerWidth(opts.width);
	}
	function setProperty(target){
		var opts = $.data(target, 'textfield').options;
		var textfield = $.data(target, 'textfield').textfield;
		opts.type = opts.type || "text";
		opts.placeholder && textfield.prop("placeholder",opts.placeholder);
		textfield.prop("type",opts.type);
	}
	function bindEvents(target){
		var state = $.data(target, 'textfield');
		var opts = state.options
		var input = state.textfield;
		
		input.bind('focus.textfield', opts.onFocus).bind('change.textfield', opts.onChange)
		.bind('blur.textfield', opts.onBlur);
		
		
	}
	
	function initValue(target){
		var state = $.data(target, 'textfield');
		var opts = state.options;
		var input = state.textfield;
		input.val(opts.value);
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'textfield');
			var opts = state.options;
			var input = state.textfield;
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		var state = $.data(target, 'textfield');
		var opts = state.options;
		var input = state.textfield;
		if (disabled){
			opts.disabled = true;
			input.attr('disabled', true);
		} else {
			opts.disabled = false;
			input.removeAttr('disabled');
		}
	}
	
	$.fn.textfield = function(options, param){
		if (typeof options == 'string'){
			return $.fn.textfield.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'textfield');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'textfield', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.textfield.defaults, $.fn.textfield.parseOptions(this), options),
					textfield: init(this)
				});
			}
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			setProperty(this);
			validate(this);
		});
	}
	
	$.fn.textfield.methods = {
		options: function(jq){
			return $.data(jq[0], 'textfield').options;
		},
		textbox: function(jq){
			return $.data(jq[0], 'textfield').textfield;
		},
		getValue: function(jq){
			return this.textbox(jq).val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).textfield('options').value = value;
				$(this).textfield('textbox').val(value);
				$(this).textfield('textbox').blur();
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'textfield').textfield.attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'textfield').textfield.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.textfield.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder')
		};
	};
	/*默认配置*/
	$.fn.textfield.defaults = {
		width:300,
		disabled:false,
		readOnly:false,
		required:false,
		onChange: function(e){},
		onFocus: function(e){},
		onBlur:function(e){}
	};
})(jQuery);
	
>>>>>>> 2bc3b6021afb4e6e01b52e3e266049ff763910c3
	