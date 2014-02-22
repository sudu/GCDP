(function(){
	
CMDS={
	doubleToSingle:function(editor){
		;
	}
};	
	
CKEDITOR.plugins.add('ifeng',
{
    init: function(editor)
    {
        var pluginName = 'doubleToSingle';
        editor.addCommand(pluginName, new CKEDITOR.command( editor, { exec : CMDS.doubleToSingle }));
        editor.ui.addButton('doubleToSingle',
            {
                label: '双引号转为单引号',
                command: pluginName
            });
    }
});

})();