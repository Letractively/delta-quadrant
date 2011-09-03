/*
 *  $Id$
 *
 * metang
 * An web application to access the API of amun.
 *
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of metang. metang is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * metang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with metang. If not, see <http://www.gnu.org/licenses/>.
 */

Ext.require('Metang.main.Header');
Ext.require('Metang.main.Nav');
Ext.require('Metang.main.Content');

/**
 * Metang.main.Layout
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.main.Layout', {
	extend: 'Ext.container.Viewport',

	objHeader: null,
	objNav: null,
	objContent: null,

	initComponent: function() {

		this.objHeader  = Ext.create('Metang.main.Header');
		this.objNav     = Ext.create('Metang.main.Nav');
		this.objContent = Ext.create('Metang.main.Content');

		var config = {

			layout: 'border',
			id: 'viewport',
			items: [this.objHeader, this.objNav, this.objContent]

		};


		/*
		this.handlerWebsite();

		this.handlerService();
		*/

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Layout.superclass.initComponent.apply(this, arguments);


		// header select handler
		this.objHeader.on('item_selected', function(url){

			this.handlerContentLoader(url);

		}, this);

	},

	handlerWebsite: function(){

		this.objNav.get(0).on('click', function(n){

				/*
			// load page panel
			this.handlerContentLoader('content_page');

			// load data
			Metang.content.Page.storeGadget.load({

				params: {

					filterBy: 'id',
					filterOp: 'equals',
					filterValue: n.id

				}

			});
			Metang.content.Page.storeRight.load({

				params: {

					filterBy: 'id',
					filterOp: 'equals',
					filterValue: n.id

				}

			});

			// show edit page
			var obj = this.objContent.layout.activeItem;

			obj.panel_update.load_data(get_proxy_url(metang.content.page.url + '&filterBy=id&filterOp=equals&filterValue=' + n.id));

			obj.panel_update.set_active_tab(0);

			obj.layout.setActiveItem(2);
			*/

		}, this);

		/*
		this.o_nav.get(0).on('startdrag', function(tree, node, event){

			this.o_nav.get(0).oldPosition    = node.parentNode.indexOf(node);
			this.o_nav.get(0).oldNextSibling = node.nextSibling;

		}, this);

		this.o_nav.get(0).on('movenode', function(tree, node, oldParent, newParent, position){

			if(oldParent == newParent){

				var url    = amun_url + 'api/content/page/reorderPage?format=json';
				var params = {'pageId': node.id, 'delta': (position - this.o_nav.get(0).oldPosition)};

			} else {

				Ext.MessageBox.alert('Information', 'You can only change the parent of a node by editing the node');

				return false;

				var url    = amun_url + 'api/content/page/reparentPage?format=json';
				var params = {'pageId': node.id, 'parentId': newParent.id, 'position': position};

			}

			this.o_nav.get(0).disable();

			var con = new Ext.data.Connection();

			con.request({

				url: get_proxy_url(url),
				method: 'POST',
				jsonData: params,
				scope: this,
				headers: {

					'Content-type': 'application/json',
					'X-Http-Method-Override': 'POST'

				},
				success: function(response){

					var response = Ext.JSON.decode(response.responseText);

					if(response.success)
					{
						this.o_nav.get(0).enable();
					}
					else
					{
						this.o_nav.get(0).enable();

						Ext.MessageBox.alert('Error', response.message);
					}

				},
				failure: function(){

					this.o_nav.get(0).enable();

					Ext.MessageBox.alert('Error', 'Error while saving your changes');

				}

			});

		}, this);
		*/

	},

	handlerContentLoader: function(ns){

		var panel = this.objContent.getContainer(ns);

		if(panel === false)
		{
			try
			{
				var uri = Metang.main.Services.find(ns);

				if(uri !== false)
				{
					var className = this.getClassNameFromType(ns);
					var panel;

					try
					{
						panel = Ext.create(className);
					}
					catch(e)
					{
						panel = Ext.create('Metang.basic.Container');
					}

					this.objContent.addContainer(ns, panel);
				}
				else
				{
					Ext.Msg.alert('Error', 'Could not find service: ' + ns);
				}
			}
			catch(e)
			{
				Ext.Msg.alert('Exception', e);
			}
		}

		panel.onLoad(Metang.main.Services.find(ns));

	},

	getClassNameFromType: function(type){

		baseNs = "http://ns.amun-project.org/2011/amun/";

		if(type.substring(0, baseNs.length) != baseNs)
		{
			throw new Error('Type must be in amun namespace');
		}

		type = type.substring(baseNs.length);


		parts = type.split("/");
		className = "";

		for(var i = 0; i < parts.length; i++)
		{
			className+= parts[i] + ".";
		}

		if(className == '')
		{
			throw new Error('Invalid type');
		}

		className = 'Metang.amun.' + className + 'Container';

		return className;

	}

});
