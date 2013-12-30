(function(){
	
CMDS={
	doubleToSingle:function(editor){
		
	}
};	
	
CKEDITOR.plugins.add('doubleToSingle',
{
    init: function(editor)
    {
        var pluginName = 'doubleToSingle';
        editor.addCommand(pluginName, new CKEDITOR.command( editor, { exec : CMDS.doubleToSingle }));
        editor.ui.addButton('doubleToSingle',
            {
                label: editor.lang.doubleToSingle,
                command: pluginName
            });
    }
});
})();