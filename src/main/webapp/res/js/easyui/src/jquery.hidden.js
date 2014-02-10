 /**
 * hidden - jQuery EasyUI Extend
 * 隐藏域
 * @class hidden
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.hidden.js
 */
(function($){
	function init(target){
		return $(target);
	}
	function initValue(target){
		var state = $.data(target, 'hidden');
		var opts = state.options;
		var input = $(target);
		input.val(opts.value);
		
	}

	$.fn.hidden = function(options, param){
		if (typeof options == 'string'){
			return $.fn.hidden.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'hidden');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'hidden', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.hidden.defaults, $.fn.hidden.parseOptions(this), options),
					hidden: init(this)
				});
			}
			initValue(this);
		});
	}
	
	$.fn.hidden.methods = {
		options: function(jq){
			return $.data(jq[0], 'hidden').options;
		},
		input: function(jq){
			return $.data(jq[0], 'hidden').hidden;
		},
		getValue: function(jq){
			return this.input(jq).val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).hidden('options').value = value;
				$(this).hidden('input').val(value);
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'hidden').hidden.attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'hidden').hidden.remove();
				$(this).remove();
			});
		}
	};
	
	$.fn.hidden.parseOptions = function(target){
		var t = $(target);
		return {
			value: t.val()
		};
	};
	/*默认配置*/
	$.fn.hidden.defaults = {
		
	};
})(jQuery);
	
	