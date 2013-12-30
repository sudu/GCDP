/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

/**
 * @file Horizontal Page Break
 */

// Register a plugin named "pagebreak".
CKEDITOR.plugins.add( 'pagebreak_withTitle',
{
	init : function( editor )
	{
		// Register the command.
		editor.addCommand( 'pagebreak_withTitle_add', new CKEDITOR.dialogCommand( 'pagebreak_withTitle_add' ) );
		editor.addCommand( 'pagebreak_withTitle_edit', new CKEDITOR.dialogCommand( 'pagebreak_withTitle_edit' ) );
		/*
		editor.addCommand( 'pagebreak_withTitle_remove',{
				exec : function( editor )
				{
					;//implement later;
				}
			}
		);
		*/
var lang=editor.lang.pagebreak_t;
		// Register the toolbar button.
		editor.ui.addButton( 'pagebreak_withTitle',
			{
				label : lang.label,
				command : 'pagebreak_withTitle_add'
			});
		var cssStyles = [
		     			'{' ,
		     				'background: url(' + CKEDITOR.getUrl( this.path + 'images/pagebreak_withTitle.gif' ) + ') no-repeat center 0;' ,
		     				'clear: both;' ,
		     				'width:100%; _width:99.9%;' ,
		     				'border-top: #999999 1px dotted;' ,
		     				'border-bottom: #999999 1px dotted;' ,
		     				'padding:0;' ,
		     				'height: 5px;' ,
		     				'cursor: default;' ,
		     			'}'
		     			].join( '' ).replace(/;/g, ' !important;' );	// Increase specificity to override other styles, e.g. block outline.

 		// Add the style that renders our placeholder.
 		editor.addCss( 'div.cke_pagebreak_withTitle' + cssStyles ); 		
 		
 		// Opera needs help to select the page-break.
 		CKEDITOR.env.opera && editor.on( 'contentDom', function()
 		{
 			editor.document.on( 'click', function( evt )
 			{
 				var target = evt.data.getTarget();
 				if ( target.is( 'div' ) && target.hasClass( 'cke_pagebreak_withTitle')  )
 					editor.getSelection().selectElement( target );
 			});
 		});

		if ( editor.addMenuItems )
		{
			editor.addMenuGroup("pagebreak_withTitle",10);
			editor.addMenuItems(
				{
					pagebreak_withTitle_edit :
					{
						label : lang.edit,
						command : 'pagebreak_withTitle_edit',
						group : 'pagebreak_withTitle',
						order : 1
					}
/*
					,pagebreak_withTitle_remove:
					{
						label : lang.remove,
						command : 'pagebreak_withTitle_remove',
						group : 'pagebreak_withTitle',
						order : 5
					}
*/
				} );

			if ( editor.contextMenu )
			{
				editor.contextMenu.addListener( function( element, selection )
					{
						if ( !element || element.isReadOnly() )
							return null;

						if ( element.hasClass("cke_pagebreak_withTitle") )
						{
							return {
								pagebreak_withTitle_edit : CKEDITOR.TRISTATE_OFF
								//,pagebreak_withTitle_remove : CKEDITOR.TRISTATE_OFF
							};
						}

						return null;
					} );
			}
		}

		CKEDITOR.dialog.add( 'pagebreak_withTitle_add', this.path + 'dialogs/pagebreak_withTitle.js' );
		CKEDITOR.dialog.add( 'pagebreak_withTitle_edit', this.path + 'dialogs/pagebreak_withTitle.js' );
	},
	afterInit : function( editor )
	{
		var label = editor.lang.pagebreak_t.hint;

		// Register a filter to displaying placeholders after mode change.
		var dataProcessor = editor.dataProcessor,
			dataFilter = dataProcessor && dataProcessor.dataFilter,
			htmlFilter = dataProcessor && dataProcessor.htmlFilter;

		if ( htmlFilter )
		{
			htmlFilter.addRules(
			{
				attributes : {
					'class' : function( value, element )
					{
						var className =  value.replace( 'pagebreak_withTitle', '' );
						if ( className != value )
						{
							element.children.length = 0;
							var span = CKEDITOR.htmlParser.fragment.fromHtml( '<span style="display: none;">&nbsp;</span>' );
							element.add( span );
							var attrs = element.attributes;
							delete attrs[ 'aria-label' ];
							delete attrs.contenteditable;
							attrs["title"]=encodeURIComponent(attrs.title.replace(label,""));
						}
						return className;
					}
				}
			}, 5 );
		}

		if ( dataFilter )
		{
			dataFilter.addRules(
				{
					elements :
					{
						div : function( element )
						{
							var attributes = element.attributes,
								style = attributes && attributes.style,
								title = attributes && attributes.title,
								child = style && element.children.length == 1 && element.children[ 0 ],
								childStyle = child && ( child.name == 'span' ) && child.attributes.style;
							if ( typeof title!= "undefined"&&childStyle && ( /page-break-after\s*:\s*always/i ).test( style ) && ( /display\s*:\s*none/i ).test( childStyle ) )
							{
								attributes.contenteditable = "true";
								attributes[ 'class' ] = "cke_pagebreak_withTitle";
								attributes[ 'data-cke-display-name' ] = "pagebreak_withTitle";
								attributes[ 'aria-label' ] = label;
								attributes[ 'title' ] = label+decodeURIComponent(title);

								element.children.length = 0;
							}
						}
					}
				});
		}
	},

	requires : [ 'fakeobjects' ]
});

