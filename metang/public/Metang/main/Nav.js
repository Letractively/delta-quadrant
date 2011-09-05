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

/**
 * Metang.main.Nav
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.main.Nav', {
	extend: 'Ext.tree.TreePanel',

	objWebsite: null,
	store: null,

	initComponent: function() {

		// tree panel
		this.store = Ext.create('Ext.data.TreeStore', {

			root: {
				expanded: true,
				children: {}
			}

		});


		// load tree
		this.loadTree();


		var config = {

			title: 'Navigation',
			id: 'nav',
			region: 'west',
			margins: '0 5 0 0',
			width: 200,
			border: true,
			store: this.store,
			collapsible: false,
			rootVisible: false,
			singleExpand: false,
			autoScroll: true

		};


		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.main.Nav.superclass.initComponent.apply(this, arguments);

	},

	buildTree: function(node, entries){

		for(var i = 0; i < entries.length; i++)
		{
			var child = node.appendChild({

				id: entries[i].id,
				text: entries[i].text,
				leaf: entries[i].children.length == 0
				//iconCls: 'page'

			});

			if(typeof(entries[i].children) != 'undefined')
			{
				this.buildTree(child, entries[i].children);
			}
		}

	},

	loadTree: function(){

		var uri = Metang.main.Services.find('http://ns.amun-project.org/2011/amun/content/page');

		if(uri !== false)
		{
			this.getRootNode().removeAll();

			Ext.Ajax.request({

				url: Metang.main.Util.getProxyUrl(uri + '/buildTree?format=json'),
				method: 'GET',
				scope: this,
				disableCaching: true,
				success: function(response){

					var resp = Ext.JSON.decode(response.responseText);

					this.buildTree(this.store.getRootNode(), resp.entry);

					this.getRootNode().firstChild.expand();

				},
				failure: function(response){

					Ext.Msg.alert('Error', 'Couldnt load tree.');

				}

			});
		}

	}

});

