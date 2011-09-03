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
 * Metang.basic.Grid
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.basic.Grid', {
	extend: 'Ext.panel.Panel',

	itemsPerPage: 32,
	supportedFields: null,
	store: null,

	initComponent: function(){

		var config = {

			layout: 'fit',
			border: false

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.basic.Grid.superclass.initComponent.apply(this, arguments);


		this.loadAvailableFields();


		this.addEvents('itemclick');

	},

	build: function(){

		// define model
		var fields = [];

		for(var i = 0; i < this.supportedFields.length; i++)
		{
			fields.push({

				name: this.supportedFields[i]

			});
		}

		Ext.define(this.uri,{

			extend: 'Ext.data.Model',
			fields: fields

		});


		// build store
		this.store = Ext.create('Ext.data.Store', {

			model: this.uri,
			autoLoad: false,
			pageSize: this.itemsPerPage,
			proxy: {

				type: 'ajax',
				url: Metang.main.Util.getProxyUrl(this.uri + '?format=json'),
				reader: {

					type: 'json',
					root: 'entry'

				}

			}

		});

		// build grid
		var columns = [];

		for(var i = 0; i < this.supportedFields.length; i++)
		{
			columns.push({

				header: this.supportedFields[i],
				dataIndex: this.supportedFields[i]

			});
		}

		var grid = Ext.create('Ext.grid.Panel', {

			store: this.store,
			columns: columns,
			border: false,
			dockedItems: [{

				xtype: 'pagingtoolbar',
				store: this.store,
				dock: 'bottom',
				displayInfo: true

			}]

		});


		// row select
		grid.on('itemclick', function(el, rec){

			var id = rec.get(this.supportedFields[0]);

			this.fireEvent('itemclick', id);

		}, this);


		this.add(grid);

		this.doLayout();

		this.load();

	},

	load: function(){

		this.store.load({

			params:{

				start: 0,
				limit: this.itemsPerPage

			}

		});

	},

	loadAvailableFields: function(){

		Ext.Ajax.request({

			url: Metang.main.Util.getProxyUrl(this.uri + '/@supportedFields?format=json'),
			method: 'GET',
			scope: this,
			disableCaching: true,
			success: function(response){

				var fields = Ext.JSON.decode(response.responseText);

				this.supportedFields = fields.item;

				this.build();

			},
			failure: function(response){

				Ext.Msg.alert('Error', 'Could not get supported fields.');

			}

		});

	}

});
