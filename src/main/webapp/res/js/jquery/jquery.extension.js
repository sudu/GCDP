
(function($){
	/*****解析url带参数*******/
	$.parseQuery = function(qs){// optionally pass a querystring to parse
		var params = {};

		if (qs == null) {
			qs = location.search.substring(1, location.search.length);
		}else if (qs.length > 0) {
			qs = qs.split('?');
			if(qs.length>1){
				qs = qs[1];
			}else{
				return params;
			}
		}
		// See: http://www.w3.org/TR/REC-html40/interact/forms.html#h-17.13.4.1
		qs = qs.replace(/\+/g, ' ');
		var args = qs.split('&'); // parse out name/value pairs separated via &

		// split out each name=value pair
		for (var i = 0; i < args.length; i++) {
			var pair = args[i].split('=');
			var name = decodeURIComponent(pair[0]);

			var value = (pair.length == 2)
				? decodeURIComponent(pair[1])
				: name;
			params[name] = value;
		}
			
		return params;
	}
	/*****将参数JSON转成参数字符串*******/
	$.getQueryString = function(params){
		var paramsString=[];
		for(var key in params){
			var value = encodeURIComponent(params[key]);
			paramsString.push(key + "=" + value);
		}
		return paramsString.join("&");
	}
})(jQuery);


