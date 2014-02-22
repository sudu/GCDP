 /**
 * radiogroup - jQuery EasyUI Extend
 * 单选框
 * @class radiogroup
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.radiogroup.js
 */
(function($){
	function init(target){
		$(target).hide();
		var ct = $('<div class="radiogroup"></div>').insertAfter(target);
		var input = $('<hidden></hidden>').appendTo(ct);
		var name = $(target).attr('name');
		if (name){
			input.attr('name', name);
			$(target).removeAttr('name').attr('radiogroupName', name);
		}
		
		return ct;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'radiogroup').options;
		var sb = $.data(target, 'radiogroup').radiogroup;
		if (width) opts.width = width;
		sb._outerWidth(opts.width);
	}
	

	function bindEvents(target){
		var state = $.data(target, 'radiogroup');
		var opts = state.options;
		var input = state.radiogroup.find('hidden');
		var radio = state.radiogroup.find('input[type="radio"]');
		
		radio.bind('change', (function(opts){
			var _state = state;
			var _input = input;
			var _opts = opts;
			return function(e){
				opts.onChange.call(this,e);
			}
		})(opts));

	}
	
	function initValue(target){
		var state = $.data(target, 'radiogroup');
		var opts = state.options;
		var input = state.radiogroup.find('hidden');
		input.val(opts.value);
		if(typeof opts.value !=="undefined"){
			var valueTpl = opts.value;
			var radios = state.radiogroup.find('input[type="radio"]');
			for(var i = 0;i<radios.length;i++){
				var chk = $(radios[i]);
				if(valueTpl==chk.val()){
					chk[0].checked = true;
				}else{
					chk[0].checked = false;
				}
			}
		
		}
	}
	
	function initRadio(target){
		var state = $.data(target, 'radiogroup');
		var name = state.radiogroup.find('hidden').attr("name");
		var opts = state.options;
		var ct = state.radiogroup;
		var data = opts.data;
		var valueField = opts.valueField;
		var displayField = opts.displayField;
		if(data){
			for(var i=0;i<data.length;i++){
				var item = data[i];	
				var id = (target.id||name) + "_radio_" + i;
				$('<span class="radiogroup-item"><input type="radio" id="'+ id +'" value="'+ item[valueField] +'" name="'+ name +'" /><label for="'+ id +'" class="radiogroup-item-label">'+ item[displayField] +'</label></span>').appendTo(ct);
			}
		}
	}
	
	function synRadio(value){
		var target = value;
		if(typeof value !== "string"){
			value = $(target).radiogroup('input').val();
		}
		
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'radiogroup');
			var opts = state.options;
			var input = state.radiogroup.find('hidden');
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		
	}
	
	$.fn.radiogroup = function(options, param){
		if (typeof options == 'string'){
			return $.fn.radiogroup.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'radiogroup');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'radiogroup', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.radiogroup.defaults, $.fn.radiogroup.parseOptions(this), options),
					radiogroup: init(this)
				});
			}
			initRadio(this);
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			validate(this);
		});
	}
	
	$.fn.radiogroup.methods = {
		options: function(jq){
			return $.data(jq[0], 'radiogroup').options;
		},
		input: function(jq){
			return $.data(jq[0], 'radiogroup').radiogroup.find('hidden');
		},
		getValue: function(jq){
			var radio = $.data(jq[0], 'radiogroup').radiogroup.find('input[type="radio"]:checked');
			return radio.val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).radiogroup('options').value = value;
				$(this).radiogroup('input').val(value);
				
				synRadio(this);
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'radiogroup').radiogroup.find('input[type="radio"]').attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'radiogroup').radiogroup.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.radiogroup.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder')
		};
	};
	/*默认配置*/
	$.fn.radiogroup.defaults = {
		width:300,
		data:[],
		valueField:"value",
		displayField:"text",
		disabled:false,
		required:false,
		onChange: function(e){}
	};
})(jQuery);
	
	