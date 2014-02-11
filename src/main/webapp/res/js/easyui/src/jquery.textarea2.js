<<<<<<< HEAD
 /**
 * textarea2 - jQuery EasyUI Extend
 * 带字数提示的多行文本域控件。支持全角和半角的字数统计
 * @class textarea2
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.textarea2.js
 */
(function($){
	function init(target){
		$(target).hide();
		var span = $('<span class="textarea2"></span>').insertAfter(target);
		var textarea = $('<textarea class="textarea2-text"></textarea>').appendTo(span);
		var prompt = $('<div class="textarea2-prompt-ct">限制字数:<span class="textarea2-warnCharCount"></span>字，<span class="textarea2-prompt-prefix"></span></span><span class="textarea2-prompt prompt-info"></span><span class="textarea2-prompt-suffix">字</span></div>').appendTo(span);
		
		var name = $(target).attr('name');
		if (name){
			textarea.attr('name', name);
			$(target).removeAttr('name').attr('textarea2Name', name);
		}

		return span;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'textarea2').options;
		var sb = $.data(target, 'textarea2').textarea2;
		if (width) opts.width = width;
		if (height) opts.height = height;
		sb.appendTo('body');
		
		var textarea = sb.find('textarea.textarea2-text');
		
		if (isNaN(opts.width)){
			opts.width = textarea.outerWidth();
		}
		if (isNaN(opts.height)){
			opts.height = textarea.outerHeight();
		}
		textarea._outerWidth(opts.width);
		textarea._outerHeight(opts.height);
		sb.insertAfter(target);
	}
	
	function showContentLenth(target){
		var state = $.data(target, 'textarea2');
		var opts = state.options;
		var input = state.textarea2.find('textarea.textarea2-text');
		var prompt = state.textarea2.find('span.textarea2-prompt');
		var prompt_pre = state.textarea2.find('span.textarea2-prompt-prefix');
		if(input){
			var content = input.val();
			var count = getCharLength(content);
			var countText;
			if(count>opts.warnCharCount){
				prompt_pre.html('已经超过');
				countText =  (count - opts.warnCharCount);
				prompt.removeClass('prompt-info');
				prompt.addClass('prompt-warn');
			}else{
				prompt_pre.html('还可以输入');
				countText = (opts.warnCharCount - count); 
				prompt.removeClass('prompt-warn');
				prompt.addClass('prompt-info');
			}
			prompt.html(countText);
		}
	}
	
	function getCharLength(content){
		var pattern = /[^\x00-\x80]+/;
		var contentLength = 0;
		for (var i = 0; i < content.length; i++) {
			if (pattern.test(content.charAt(i))) {
				contentLength++;
			} else {
				contentLength += 0.5;
			}
		}
		return contentLength;
	}
	
	
	function bindEvents(target){
		var state = $.data(target, 'textarea2');
		var opts = state.options
		var input = state.textarea2.find('textarea.textarea2-text');
		
		input.bind('keyup.textarea2', function(e){
			showContentLenth(target);
		}).bind('change.textarea2', opts.onChange)
		.bind('blur.textarea2', opts.onBlur);
		
		
	}
	
	function initValue(target){
		var state = $.data(target, 'textarea2');
		var opts = state.options;
		var input = state.textarea2.find('textarea.textarea2-text');
		input.val(opts.value);
		
		var warn = state.textarea2.find('span.textarea2-warnCharCount');
		warn.html(opts.warnCharCount);
		
		showContentLenth(target);
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'textarea2');
			var opts = state.options;
			var input = state.textarea2.find('textarea.textarea2-text');
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		var state = $.data(target, 'textarea2');
		var opts = state.options;
		var input = state.textarea2.find('textarea.textarea2-text');
		if (disabled){
			opts.disabled = true;
			input.attr('disabled', true);
		} else {
			opts.disabled = false;
			input.removeAttr('disabled');
		}
	}
	
	$.fn.textarea2 = function(options, param){
		if (typeof options == 'string'){
			return $.fn.textarea2.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'textarea2');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'textarea2', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.textarea2.defaults, $.fn.textarea2.parseOptions(this), options),
					textarea2: init(this)
				});
			}
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			validate(this);
		});
	}
	
	$.fn.textarea2.methods = {
		options: function(jq){
			return $.data(jq[0], 'textarea2').options;
		},
		textbox: function(jq){
			return $.data(jq[0], 'textarea2').textarea2.find('textarea.textarea2-text');
		},
		getValue: function(jq){
			return this.textbox(jq).val();;
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).textarea2('options').value = value;
				$(this).textarea2('textbox').val(value);
				$(this).textarea2('textbox').blur();
				showContentLenth(this);
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'textarea2').textarea2.find('textarea.textarea2-text').attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'textarea2').textarea2.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.textarea2.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder'),
			warnCharCount: (t.attr('warnCharCount') || undefined)
		};
	};
	/*默认配置*/
	$.fn.textarea2.defaults = {
		width:300,
		height:120,
		disabled:false,
		warnCharCount:50,
		required:false,
		onChange: function(e){},
		onBlur:function(e){}
	};
})(jQuery);
	
=======
 /**
 * textarea2 - jQuery EasyUI Extend
 * 带字数提示的多行文本域控件。支持全角和半角的字数统计
 * @class textarea2
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.textarea2.js
 */
(function($){
	function init(target){
		$(target).hide();
		var span = $('<span class="textarea2"></span>').insertAfter(target);
		var textarea = $('<textarea class="textarea2-text"></textarea>').appendTo(span);
		var prompt = $('<div class="textarea2-prompt-ct">限制字数:<span class="textarea2-warnCharCount"></span>字，<span class="textarea2-prompt-prefix"></span></span><span class="textarea2-prompt prompt-info"></span><span class="textarea2-prompt-suffix">字</span></div>').appendTo(span);
		
		var name = $(target).attr('name');
		if (name){
			textarea.attr('name', name);
			$(target).removeAttr('name').attr('textarea2Name', name);
		}

		return span;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'textarea2').options;
		var sb = $.data(target, 'textarea2').textarea2;
		if (width) opts.width = width;
		if (height) opts.height = height;
		sb.appendTo('body');
		
		var textarea = sb.find('textarea.textarea2-text');
		
		if (isNaN(opts.width)){
			opts.width = textarea.outerWidth();
		}
		if (isNaN(opts.height)){
			opts.height = textarea.outerHeight();
		}
		textarea._outerWidth(opts.width);
		textarea._outerHeight(opts.height);
		sb.insertAfter(target);
	}
	
	function showContentLenth(target){
		var state = $.data(target, 'textarea2');
		var opts = state.options;
		var input = state.textarea2.find('textarea.textarea2-text');
		var prompt = state.textarea2.find('span.textarea2-prompt');
		var prompt_pre = state.textarea2.find('span.textarea2-prompt-prefix');
		if(input){
			var content = input.val();
			var count = getCharLength(content);
			var countText;
			if(count>opts.warnCharCount){
				prompt_pre.html('已经超过');
				countText =  (count - opts.warnCharCount);
				prompt.removeClass('prompt-info');
				prompt.addClass('prompt-warn');
			}else{
				prompt_pre.html('还可以输入');
				countText = (opts.warnCharCount - count); 
				prompt.removeClass('prompt-warn');
				prompt.addClass('prompt-info');
			}
			prompt.html(countText);
		}
	}
	
	function getCharLength(content){
		var pattern = /[^\x00-\x80]+/;
		var contentLength = 0;
		for (var i = 0; i < content.length; i++) {
			if (pattern.test(content.charAt(i))) {
				contentLength++;
			} else {
				contentLength += 0.5;
			}
		}
		return contentLength;
	}
	
	
	function bindEvents(target){
		var state = $.data(target, 'textarea2');
		var opts = state.options
		var input = state.textarea2.find('textarea.textarea2-text');
		
		input.bind('keyup.textarea2', function(e){
			showContentLenth(target);
		}).bind('change.textarea2', opts.onChange)
		.bind('blur.textarea2', opts.onBlur);
		
		
	}
	
	function initValue(target){
		var state = $.data(target, 'textarea2');
		var opts = state.options;
		var input = state.textarea2.find('textarea.textarea2-text');
		input.val(opts.value);
		
		var warn = state.textarea2.find('span.textarea2-warnCharCount');
		warn.html(opts.warnCharCount);
		
		showContentLenth(target);
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'textarea2');
			var opts = state.options;
			var input = state.textarea2.find('textarea.textarea2-text');
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		var state = $.data(target, 'textarea2');
		var opts = state.options;
		var input = state.textarea2.find('textarea.textarea2-text');
		if (disabled){
			opts.disabled = true;
			input.attr('disabled', true);
		} else {
			opts.disabled = false;
			input.removeAttr('disabled');
		}
	}
	
	$.fn.textarea2 = function(options, param){
		if (typeof options == 'string'){
			return $.fn.textarea2.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'textarea2');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'textarea2', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.textarea2.defaults, $.fn.textarea2.parseOptions(this), options),
					textarea2: init(this)
				});
			}
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			validate(this);
		});
	}
	
	$.fn.textarea2.methods = {
		options: function(jq){
			return $.data(jq[0], 'textarea2').options;
		},
		textbox: function(jq){
			return $.data(jq[0], 'textarea2').textarea2.find('textarea.textarea2-text');
		},
		getValue: function(jq){
			return this.textbox(jq).val();;
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).textarea2('options').value = value;
				$(this).textarea2('textbox').val(value);
				$(this).textarea2('textbox').blur();
				showContentLenth(this);
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'textarea2').textarea2.find('textarea.textarea2-text').attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'textarea2').textarea2.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.textarea2.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder'),
			warnCharCount: (t.attr('warnCharCount') || undefined)
		};
	};
	/*默认配置*/
	$.fn.textarea2.defaults = {
		width:300,
		height:120,
		disabled:false,
		warnCharCount:50,
		required:false,
		onChange: function(e){},
		onBlur:function(e){}
	};
})(jQuery);
	
>>>>>>> 2bc3b6021afb4e6e01b52e3e266049ff763910c3
	