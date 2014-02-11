<<<<<<< HEAD
 /**
 * textarea - jQuery EasyUI Extend
 * 带字数提示的多行文本域控件。支持全角和半角的字数统计
 * @class textarea
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.textarea.js
 */
(function($){
	function init(target){
		$(target).hide();
		var textarea = $('<textarea class="textarea-text"></textarea>').insertAfter(target);

		var name = $(target).attr('name');
		if (name){
			textarea.attr('name', name);
			$(target).removeAttr('name').attr('textareaName', name);
		}

		return textarea;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'textarea').options;
		var textarea = $.data(target, 'textarea').textarea;
		if (width) opts.width = width;
		if (height) opts.height = height;
		textarea.appendTo('body');
		
		if (isNaN(opts.width)){
			opts.width = textarea.outerWidth();
		}
		if (isNaN(opts.height)){
			opts.height = textarea.outerHeight();
		}
		textarea._outerWidth(opts.width);
		textarea._outerHeight(opts.height);
		textarea.insertAfter(target);
	}

	function bindEvents(target){
		var state = $.data(target, 'textarea');
		var opts = state.options
		var input = state.textarea;
		
		input.bind('focus.textarea', opts.onFocus).bind('change.textarea', opts.onChange)
		.bind('blur.textarea', opts.onBlur);
		
		
	}
	
	function initValue(target){
		var state = $.data(target, 'textarea');
		var opts = state.options;
		var input = state.textarea;
		input.val(opts.value);
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'textarea');
			var opts = state.options;
			var input = state.textarea;
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		var state = $.data(target, 'textarea');
		var opts = state.options;
		var input = state.textarea;
		if (disabled){
			opts.disabled = true;
			input.attr('disabled', true);
		} else {
			opts.disabled = false;
			input.removeAttr('disabled');
		}
	}
	
	$.fn.textarea = function(options, param){
		if (typeof options == 'string'){
			return $.fn.textarea.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'textarea');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'textarea', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.textarea.defaults, $.fn.textarea.parseOptions(this), options),
					textarea: init(this)
				});
			}
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			validate(this);
		});
	}
	
	$.fn.textarea.methods = {
		options: function(jq){
			return $.data(jq[0], 'textarea').options;
		},
		textbox: function(jq){
			return $.data(jq[0], 'textarea').textarea;
		},
		getValue: function(jq){
			return this.textbox(jq).val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).textarea('options').value = value;
				$(this).textarea('textbox').val(value);
				$(this).textarea('textbox').blur();
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'textarea').textarea.attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'textarea').textarea.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.textarea.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder')
		};
	};
	/*默认配置*/
	$.fn.textarea.defaults = {
		width:300,
		height:120,
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
 * textarea - jQuery EasyUI Extend
 * 带字数提示的多行文本域控件。支持全角和半角的字数统计
 * @class textarea
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.textarea.js
 */
(function($){
	function init(target){
		$(target).hide();
		var textarea = $('<textarea class="textarea-text"></textarea>').insertAfter(target);

		var name = $(target).attr('name');
		if (name){
			textarea.attr('name', name);
			$(target).removeAttr('name').attr('textareaName', name);
		}

		return textarea;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'textarea').options;
		var textarea = $.data(target, 'textarea').textarea;
		if (width) opts.width = width;
		if (height) opts.height = height;
		textarea.appendTo('body');
		
		if (isNaN(opts.width)){
			opts.width = textarea.outerWidth();
		}
		if (isNaN(opts.height)){
			opts.height = textarea.outerHeight();
		}
		textarea._outerWidth(opts.width);
		textarea._outerHeight(opts.height);
		textarea.insertAfter(target);
	}

	function bindEvents(target){
		var state = $.data(target, 'textarea');
		var opts = state.options
		var input = state.textarea;
		
		input.bind('focus.textarea', opts.onFocus).bind('change.textarea', opts.onChange)
		.bind('blur.textarea', opts.onBlur);
		
		
	}
	
	function initValue(target){
		var state = $.data(target, 'textarea');
		var opts = state.options;
		var input = state.textarea;
		input.val(opts.value);
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'textarea');
			var opts = state.options;
			var input = state.textarea;
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		var state = $.data(target, 'textarea');
		var opts = state.options;
		var input = state.textarea;
		if (disabled){
			opts.disabled = true;
			input.attr('disabled', true);
		} else {
			opts.disabled = false;
			input.removeAttr('disabled');
		}
	}
	
	$.fn.textarea = function(options, param){
		if (typeof options == 'string'){
			return $.fn.textarea.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'textarea');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'textarea', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.textarea.defaults, $.fn.textarea.parseOptions(this), options),
					textarea: init(this)
				});
			}
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			validate(this);
		});
	}
	
	$.fn.textarea.methods = {
		options: function(jq){
			return $.data(jq[0], 'textarea').options;
		},
		textbox: function(jq){
			return $.data(jq[0], 'textarea').textarea;
		},
		getValue: function(jq){
			return this.textbox(jq).val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).textarea('options').value = value;
				$(this).textarea('textbox').val(value);
				$(this).textarea('textbox').blur();
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'textarea').textarea.attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'textarea').textarea.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.textarea.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder')
		};
	};
	/*默认配置*/
	$.fn.textarea.defaults = {
		width:300,
		height:120,
		disabled:false,
		readOnly:false,
		required:false,
		onChange: function(e){},
		onFocus: function(e){},
		onBlur:function(e){}
	};
})(jQuery);
	
>>>>>>> 2bc3b6021afb4e6e01b52e3e266049ff763910c3
	