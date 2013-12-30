/*
listManage_*.ftl使用

*/

var opEnZh__=[
	['=','等于(=)'],
	['like','包含(like)'],
	['not like','不包含(not like)'],
	['>','大于(>)'],
	['>=','大于或等于(≥)'],
	['<','小于(<)'],
	['<=','小于或等于(≤)'],
	['<>','不等于(≠)']
];
var controlTypeEnZh__=[['textfield','文本框'],['combo','下拉框'],['multiselect','多选下拉框'],['treecombo','树状下拉框'],['radiogroup','单选框'],['datefieldextent','日期区间']];
var andorEnZH__ ={
	'and':'并且','or':'或者'
}
	
function myZhuanyi(str){
	var ret = str;
	//ret = ret.replace(/&/g,'&amp;');
	ret = ret.replace(/"/g,'&quot;');
	ret = ret.replace(/>/g,'&gt;');
	ret = ret.replace(/</g,'&lt;');
	return ret;
}	
function myUnZhuanyi(str){
	var ret = str;
	ret = ret.replace(/&quot;/g,'"');
	ret = ret.replace(/&gt;/g,'>');
	ret = ret.replace(/&lt;/g,'<');
	//ret = ret.replace(/&amp;/g,'&');		
	return ret;
}	

function setActiveTab(url,tabId,title){
	if(top&& top.centerTabPanel){
		var _url = url;
		top.centerTabPanel.addIframe('tab_' + tabId,title ,_url);
	}else{
		window.open(url);	
	}
}