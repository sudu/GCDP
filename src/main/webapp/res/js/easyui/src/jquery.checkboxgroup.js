 /**
 * checkboxgroup - jQuery EasyUI Extend
 * 多选框
 * @class checkboxgroup
 * @extends 
 * @author cici
 * @date:2013/12/28
 * @Dependencies:jquery.checkboxgroup.js
 */
(function($){
	function init(target){
		$(target).hide();
		var ct = $('<div class="checkboxgroup"></div>').insertAfter(target);
		var input = $('<hidden></hidden>').appendTo(ct);
		var name = $(target).attr('name');
		if (name){
			input.attr('name', name);
			$(target).removeAttr('name').attr('checkboxgroupName', name);
		}
		
		return ct;
	}
	
	function setSize(target, width,height){
		var opts = $.data(target, 'checkboxgroup').options;
		var sb = $.data(target, 'checkboxgroup').checkboxgroup;
		if (width) opts.width = width;
		sb._outerWidth(opts.width);
	}
	

	function bindEvents(target){
		var state = $.data(target, 'checkboxgroup');
		var opts = state.options;
		var input = state.checkboxgroup.find('hidden');
		var checkbox = state.checkboxgroup.find('input[type="checkbox"]');
		
		checkbox.bind('change', (function(opts){
			var _state = state;
			var _input = input;
			var _opts = opts;
			return function(e){
				var thisValue = $(this).data().value;
				var valueArr = JSON.parse(_input.val());
				var pos = valueArr.indexOf(thisValue);
				if(this.checked){
					if(pos==-1){
						valueArr.push(thisValue);
					}
				}else{
					valueArr = valueArr.slice(0,pos).concat(valueArr.slice(pos+1));
				}
				_input.val(JSON.stringify(valueArr));
				opts.onChange.call(this,e);
			}
		})(opts));

	}
	
	function initValue(target){
		var state = $.data(target, 'checkboxgroup');
		var opts = state.options;
		var input = state.checkboxgroup.find('hidden');
		input.val(opts.value);
		if(opts.value){
			try {var valueTpl = JSON.parse(opts.value);}
			catch(ex){valueTpl=[]}
			var checkboxes = state.checkboxgroup.find('input[type="checkbox"]');
			for(var i = 0;i<checkboxes.length;i++){
				var chk = $(checkboxes[i]);
				if(valueTpl.indexOf(chk.data().value)>=0){
					chk[0].checked = true;
				}else{
					chk[0].checked = false;
				}
			}
		
		}
	}
	
	function initCheckBox(target){
		var state = $.data(target, 'checkboxgroup');
		var name = state.checkboxgroup.find('hidden').attr("name");
		var opts = state.options;
		var ct = state.checkboxgroup;
		var data = opts.data;
		var valueField = opts.valueField;
		var displayField = opts.displayField;
		if(data){
			for(var i=0;i<data.length;i++){
				var item = data[i];	
				var id = (target.id||name) + "_checkbox_" + i;
				$('<span class="checkboxgroup-item"><input type="checkbox" id="'+ id +'" data-value="'+ item[valueField] +'" /><label for="'+ id +'" class="checkboxgroup-item-label">'+ item[displayField] +'</label></span>').appendTo(ct);
			}
		}
	}
	
	function synCheckbox(value){
		var target = value;
		if(typeof value !== "string"){
			value = $(target).checkboxgroup('input').val();
		}
		
	}
	
	function validate(target, doit){
		if ($.fn.validatebox){
			var state = $.data(target, 'checkboxgroup');
			var opts = state.options;
			var input = state.checkboxgroup.find('hidden');
			input.validatebox(opts);
			if (doit){
				input.validatebox('validate');
				input.trigger('mouseleave');
			}
		}
	}
	
	function setDisabled(target, disabled){
		
	}
	
	$.fn.checkboxgroup = function(options, param){
		if (typeof options == 'string'){
			return $.fn.checkboxgroup.methods[options](this, param);
		}
		
		options = options || {};
		return this.each(function(){
			var state = $.data(this, 'checkboxgroup');
			if (state){
				$.extend(state.options, options);
			} else {
				state = $.data(this, 'checkboxgroup', {
					options: $.extend({}, $.fn.validatebox.parseOptions(this), $.fn.checkboxgroup.defaults, $.fn.checkboxgroup.parseOptions(this), options),
					checkboxgroup: init(this)
				});
			}
			initCheckBox(this);
			initValue(this);
			bindEvents(this);
			setDisabled(this, state.options.disabled);
			setSize(this);
			validate(this);
		});
	}
	
	$.fn.checkboxgroup.methods = {
		options: function(jq){
			return $.data(jq[0], 'checkboxgroup').options;
		},
		input: function(jq){
			return $.data(jq[0], 'checkboxgroup').checkboxgroup.find('hidden');
		},
		getValue: function(jq){
			return this.input(jq).val();
		},
		setValue: function(jq, value){
			return jq.each(function(){
				$(this).checkboxgroup('options').value = value;
				$(this).checkboxgroup('input').val(value);
				
				synCheckbox(this);
			});
		},
		getName: function(jq){
			return $.data(jq[0], 'checkboxgroup').checkboxgroup.find('hidden').attr('name');
		},
		destroy: function(jq){
			return jq.each(function(){
				$.data(this, 'checkboxgroup').checkboxgroup.remove();
				$(this).remove();
			});
		},
		resize: function(jq, width,height){
			return jq.each(function(){
				setSize(this, width,height);
			});
		}
	};
	
	$.fn.checkboxgroup.parseOptions = function(target){
		var t = $(target);
		return {
			width: (parseInt(target.style.width) || undefined),
			height: (parseInt(target.style.height) || undefined),
			value: t.val(),
			placeholder: t.attr('placeholder')
		};
	};
	/*默认配置*/
	$.fn.checkboxgroup.defaults = {
		width:300,
		data:[],
		valueField:"value",
		displayField:"text",
		disabled:false,
		required:false,
		onChange: function(e){}
	};
})(jQuery);
	
	