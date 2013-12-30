(function()
{
	createHtml= function( editor )
	{
		var label = editor.lang.pagebreak_t.hint;

		// Create read-only element that represents a print break.
		var pagebreak = CKEDITOR.dom.element.createFromHtml(
			'<div style="' +
			'page-break-after: always;"' +
			'contenteditable="true" ' +
			'title="'+ label + '" ' +
			'data-cke-display-name="pagebreak_withTitle" ' +
			'class="cke_pagebreak_withTitle">' +
			'</div>' );

		var ranges = editor.getSelection().getRanges( true );

		editor.fire( 'saveSnapshot' );

		for ( var range, i = ranges.length - 1 ; i >= 0; i-- )
		{
			range = ranges[ i ];

			if ( i < ranges.length -1 )
				pagebreak = pagebreak.clone( true );

			range.splitBlock( 'p' );
			range.insertNode( pagebreak );
			if ( i == ranges.length - 1 )
			{
				var next = pagebreak.getNext();
				range.moveToPosition( pagebreak, CKEDITOR.POSITION_AFTER_END );

				// If there's nothing or a non-editable block followed by, establish a new paragraph
				// to make sure cursor is not trapped.
				if ( !next || next.type == CKEDITOR.NODE_ELEMENT && !next.isEditable() )
					range.fixBlock( true, editor.config.enterMode == CKEDITOR.ENTER_DIV ? 'div' : 'p'  );

				range.select();
			}
		}

		editor.fire( 'saveSnapshot' );
		return pagebreak;
	};
	
	function pagebreakTitDialog( editor, command )
	{
		var hint=editor.lang.pagebreak_t.hint;
		return {
			title : editor.lang.pagebreak_t.label,
			minWidth : 400,
			minHeight : 100,
			contents :
			[
			{
				id :'info',
				label :editor.lang.common.generalTab,
				title :editor.lang.common.generalTab,
				elements :
				[
					{
						type :'vbox',
						children :
						[
							{
								id :'title',
								type :'text',
								label :editor.lang.common.advisoryTitle,
								'default' : '[Title]',
								setup : function( element )
								{
									var ttl=element.getAttribute( this.id );
									ttl=ttl?ttl.replace(hint,""):"";
									this.setValue(ttl);
								},
								commit : function( element )
								{
									var fieldValue = this.getValue();
									// ignore default element attribute values
									if ( 'dir' == this.id && element.getComputedStyle( 'direction' ) == fieldValue )
										return;

									if ( fieldValue ){
										fieldValue=hint+fieldValue;
										element.setAttribute( this.id, fieldValue );
									}
								}
							}
						]
					}
				]
			}
			],
			onShow : function()
			{
				// Whether always create new container regardless of existed
				// ones.
				if ( command == 'edit' )
				{
					// Try to discover the containers that already existed in
					// ranges
					var div = editor.getSelection().getStartElement();
					// update dialog field values
					div && this.setupContent( this._element = div );
				}
			},
			onOk : function()
			{
				var div;
				if ( command == 'edit' )
					div =  this._element ;
				else
					div = createHtml( editor);
				this.commitContent(div);
				
				this.hide();
			}
		};
	}

	CKEDITOR.dialog.add( 'pagebreak_withTitle_add', function( editor )
		{
			return pagebreakTitDialog( editor, 'add' );
		} );
	CKEDITOR.dialog.add( 'pagebreak_withTitle_edit', function( editor )
		{
			return pagebreakTitDialog( editor, 'edit' );
		} );
} )();

