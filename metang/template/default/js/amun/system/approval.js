/*
 *  $Id: approval.js 170 2011-02-19 19:46:21Z k42b3.x $
 *
 * amun
 * A social content managment system based on the psx framework. For
 * the current version and informations visit <http://amun.phpsx.org>
 *
 * Copyright (c) 2010 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of amun. amun is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * amun is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with amun. If not, see <http://www.gnu.org/licenses/>.
 */

Ext.ns('amun.system.approval');

/**
 * amun.system.approval
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 170 $
 */

amun.system.approval.url = amun_url + 'api/system/approval?format=json';


amun.system.approval.store = new Ext.data.Store({

	url: get_proxy_url(amun.system.approval.url),
	restful: true,
	remoteSort: true,
	paramNames: {

		start: 'startIndex',
		limit: 'count',
		sort: 'sortBy',
		dir: 'sortOrder'

	},
	reader: new Ext.data.JsonReader({

		totalProperty: 'totalResults',
		successProperty: 'success',
		idProperty: 'id',
		root: 'entry',
		messageProperty: 'message'

	},[

		{name: 'id', type: 'int'},
		{name: 'table', type: 'string'},
		{name: 'field', type: 'string'},
		{name: 'value', type: 'string'}

	])

});


amun.system.approval.store_record = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/approval/record?format=json'),
	restful: true,
	remoteSort: true,
	paramNames: {

		start: 'startIndex',
		limit: 'count',
		sort: 'sortBy',
		dir: 'sortOrder'

	},
	reader: new Ext.data.JsonReader({

		totalProperty: 'totalResults',
		successProperty: 'success',
		idProperty: 'id',
		root: 'entry',
		messageProperty: 'message'

	},[

		{name: 'id', type: 'int'},
		{name: 'type', type: 'string'},
		{name: 'table', type: 'string'},
		{name: 'record'},
		{name: 'authorDisplayName', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.system.approval.panel_receive_center = Ext.extend(amun.common.grid, {

	store: amun.system.approval.store_record,
	columns: [

		//{header: 'id', width: 75, dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Type', width: 100, dataIndex: 'type', menuDisabled: true, sortable: true},
		{header: 'Table', width: 200, dataIndex: 'table', menuDisabled: true, sortable: true},
		{header: 'User', width: 150, dataIndex: 'authorDisplayName', menuDisabled: true, sortable: true},
		{header: 'Date', width: 200, dataIndex: 'date', menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: [{

		text: 'View',
		iconCls: 'install',
		disabled: true

	}]

});


amun.system.approval.panel_receive_south = Ext.extend(amun.common.grid, {

	store: amun.system.approval.store,
	columns: [

		//{header: 'id', width: 75, dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Table', width: 200, dataIndex: 'table', menuDisabled: true, sortable: true},
		{header: 'Field', width: 150, dataIndex: 'field', menuDisabled: true, sortable: true},
		{header: 'Value', width: 150, dataIndex: 'value', menuDisabled: true, sortable: true}

	],
	options: [{

		text: 'Add',
		iconCls: 'add',
		disabled: false

	},{

		text: 'Delete',
		iconCls: 'delete',
		disabled: true

	}]

});


amun.system.approval.panel_receive = Ext.extend(Ext.Panel, {

	center: null,
	south: null,

	initComponent: function(){

		this.center = new amun.system.approval.panel_receive_center();
		this.south  = new amun.system.approval.panel_receive_south();

		var config = {

			layout: 'border',
			defaults: {

				collapsible: false,
				split: true,
				border: false

			},
			border: false,
			items: [{

				region: 'center',
				layout: 'fit',
				items: [this.center]

			},{

				title: 'Rules',
				region: 'south',
				layout: 'fit',
				height: 300,
				minSize: 150,
				maxSize: 300,
				collapsible: true,
				items: [this.south]

			}]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.approval.panel_receive.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		this.center.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			grid.row_id = id;

		});

		this.south.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			grid.row_id = id;

		});

	}

});


amun.system.approval.panel_create = Ext.extend(amun.common.form, {

	url: amun.system.approval.url,
	request_method: 'POST',
	fields: [{

		fieldLabel: 'Table',
		name: 'table',
		allowBlank: false

	},{

		fieldLabel: 'Field',
		name: 'field',
		allowBlank: false

	},{

		fieldLabel: 'Value',
		name: 'value',
		allowBlank: false

	}]

});


amun.system.approval.panel_delete = Ext.extend(amun.common.form, {

	url: amun.system.approval.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'Table',
		name: 'table',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Field',
		name: 'field',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Value',
		name: 'value',
		allowBlank: false,
		disabled: true

	}]

});


amun.system.approval.detail = {

	win: null,
	panel: null,

	show: function(data, panel){

		this.panel = panel;

		if(!this.win)
		{
			this.win = new Ext.Window({

				layout: 'fit',
				width: 500,
				height: 300,
				closeAction: 'hide',
				items: new Ext.grid.PropertyGrid({

					border: false

				}),
				buttons: [{

					text: 'Approve',
					scope: this,
					handler: function(){

						var con = new Ext.data.Connection();
						var id  = this.panel.panel_receive.center.row_id;

						if(id > 0)
						{
							con.request({

								url: get_proxy_url(amun_url + '/index.php/api/system/approval/approveRecord?id=' + id),
								method: 'GET',
								scope: this,
								success: function(response){

									response = Ext.util.JSON.decode(response.responseText);

									if(response.success)
									{
										amun.system.approval.store_record.load();

										this.panel.panel_receive.center.reset();

										this.win.hide();
									}
									else
									{
										Ext.Msg.alert('Error', response.message);
									}

								}

							});
						}

					}

				},{

					text: 'Delete',
					scope: this,
					handler: function(){

						var con = new Ext.data.Connection();
						var id  = this.panel.panel_receive.center.row_id;

						if(id > 0)
						{
							con.request({

								url: get_proxy_url(amun_url + '/index.php/api/system/approval/record?format=json'),
								method: 'POST',
								scope: this,
								jsonData: {id: id},
								scope: this,
								headers: {

									'Content-type': 'application/json',
									'X-Http-Method-Override': 'DELETE'

								},
								success: function(response){

									response = Ext.util.JSON.decode(response.responseText);

									if(response.success)
									{
										amun.system.approval.store_record.load();

										this.panel.panel_receive.center.reset();

										this.win.hide();
									}
									else
									{
										Ext.Msg.alert('Error', response.message);
									}

								}

							});
						}

					}

				}]

			});
		}

		this.win.get(0).setSource(data);

		this.win.show();

	}

};


amun.system.approval.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.system.approval.panel_receive();
		this.panel_create  = new amun.system.approval.panel_create();
		this.panel_delete  = new amun.system.approval.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.approval.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		var center = this.panel_receive.center.getTopToolbar();
		var south  = this.panel_receive.south.getTopToolbar();

		// view
		this.panel_receive.center.on('rowdblclick', function(grid, row, e){

			var store  = grid.getStore();
			var record = store.getAt(row);
			var data   = record.get('record');

			amun.system.approval.detail.show(data, this);

		}, this);

		// view
		center.get(0).on('click', function(){

			var store  = this.panel_receive.center.getStore();
			var record = store.getById(this.panel_receive.center.row_id);
			var data   = record.get('record');

			amun.system.approval.detail.show(data, this);

		}, this);

		// add
		south.get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// del
		south.get(1).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.system.approval.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.south.row_id));

			this.layout.setActiveItem(2);

		}, this);


		var submited = function(){

			amun.system.approval.store.load();

			this.panel_receive.center.reset();

			this.layout.setActiveItem(0);

		}

		var canceled = function(){

			this.panel_receive.center.reset();

			this.layout.setActiveItem(0);

		}


		// create
		this.panel_create.on('submited', submited, this);

		this.panel_create.on('canceled', canceled, this);


		// delete
		this.panel_delete.on('submited', submited, this);

		this.panel_delete.on('canceled', canceled, this);
	}

});



