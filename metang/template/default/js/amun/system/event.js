/*
 *  $Id: event.js 171 2011-02-19 20:06:40Z k42b3.x $
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

Ext.ns('amun.system.event');

/**
 * amun.system.event
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 171 $
 */

amun.system.event.url = amun_url + 'api/system/event?format=json';
amun.system.event.action_url = amun_url + 'api/system/event/action?format=json';


amun.system.event.store = new Ext.data.Store({

	url: get_proxy_url(amun.system.event.url),
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
		{name: 'actionName', type: 'string'},
		{name: 'priority', type: 'string'},
		{name: 'type', type: 'string'},
		{name: 'table', type: 'string'},

	])

});


amun.system.event.store_action = new Ext.data.Store({

	url: get_proxy_url(amun.system.event.action_url),
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
		{name: 'class', type: 'string'},
		{name: 'name', type: 'string'}

	])

});


amun.system.event.store_type = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/event/listType?format=json'),
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

		{name: 'id',   type: 'string'},
		{name: 'name', type: 'string'}

	])

});


amun.system.event.store_table = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/event/listTable?format=json'),
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

		{name: 'id',   type: 'string'},
		{name: 'name', type: 'string'}

	])

});


amun.system.event.store_class = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/event/action/listClass?format=json'),
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

		{name: 'id',   type: 'string'},
		{name: 'name', type: 'string'}

	])

});


amun.system.event.store_config = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/event/action/getConfig?format=json'),
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
		idProperty: 'name',
		root: 'entry',
		messageProperty: 'message'

	},[

		{name: 'name', type: 'string'},
		{name: 'value', type: 'string'}

	])

});


amun.system.event.panel_receive_center = Ext.extend(amun.common.grid, {

	store: amun.system.event.store,
	columns: [

		//{header: 'id', width: 75, dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Action', width: 100, dataIndex: 'actionName', menuDisabled: true, sortable: true},
		{header: 'Priority', width: 100, dataIndex: 'priority', menuDisabled: true, sortable: true},
		{header: 'Type', width: 200, dataIndex: 'type', menuDisabled: true, sortable: true},
		{header: 'Table', width: 150, dataIndex: 'table', menuDisabled: true, sortable: true}

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


amun.system.event.panel_receive_south = Ext.extend(amun.common.grid, {

	store: amun.system.event.store_action,
	columns: [

		//{header: 'id', width: 75, dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Class', width: 150, dataIndex: 'class', menuDisabled: true, sortable: true},
		{header: 'Name', width: 250, dataIndex: 'name', menuDisabled: true, sortable: true}

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


amun.system.event.panel_receive = Ext.extend(Ext.Panel, {

	center: null,
	south: null,

	initComponent: function(){

		this.center = new amun.system.event.panel_receive_center();
		this.south  = new amun.system.event.panel_receive_south();

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

				title: 'Actions',
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

		amun.system.event.panel_receive.superclass.initComponent.apply(this, arguments);

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


amun.system.event.panel_create = Ext.extend(amun.common.form, {

	url: amun.system.event.url,
	request_method: 'POST',
	fields: [new Ext.form.ComboBox({

		fieldLabel: 'Action',
		hiddenName: 'action_id',
		store: amun.system.event.store_action,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a action ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'Priority',
		name: 'priority',
		allowBlank: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Type',
		hiddenName: 'type',
		store: amun.system.event.store_type,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a type ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),new Ext.form.ComboBox({

		fieldLabel: 'Table',
		hiddenName: 'table',
		store: amun.system.event.store_table,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a table ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16

	})]

});


amun.system.event.panel_delete = Ext.extend(amun.common.form, {

	url: amun.system.event.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Action',
		hiddenName: 'action_id',
		store: amun.system.event.store_action,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a action ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		disabled: true

	}),{

		fieldLabel: 'Priority',
		name: 'priority',
		allowBlank: true,
		disabled: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Type',
		hiddenName: 'type',
		store: amun.system.event.store_type,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a type ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		disabled: true

	}),new Ext.form.ComboBox({

		fieldLabel: 'Table',
		hiddenName: 'table',
		store: amun.system.event.store_table,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a table ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16,
		disabled: true

	})]

});


amun.system.event.panel_action_create = Ext.extend(amun.common.form, {

	url: amun.system.event.action_url,
	request_method: 'POST',
	fields: [new Ext.form.ComboBox({

		fieldLabel: 'Class',
		hiddenName: 'class',
		store: amun.system.event.store_class,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a class ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16

	}),{

		fieldLabel: 'Name',
		name: 'name',
		allowBlank: false

	},new Ext.grid.PropertyGrid({

		fieldLabel: 'Config',
		height: 256,
		margins: '0 0 0 0',
		source: {}

	})],
	get_fields: function(){

		var values = this.getForm().getFieldValues();


		// get config
		values.config = this.get(2).getSource();


		return values;

	}

});


amun.system.event.panel_action_delete = Ext.extend(amun.common.form, {

	url: amun.system.event.action_url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Class',
		hiddenName: 'class',
		store: amun.system.event.store_class,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a class ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16

	}),{

		fieldLabel: 'Name',
		name: 'name',
		allowBlank: false

	},new Ext.grid.PropertyGrid({

		fieldLabel: 'Config',
		height: 256,
		margins: '0 0 0 0',
		source: {}

	})],

});


amun.system.event.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_delete: null,
	panel_action_create: null,
	panel_action_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.system.event.panel_receive();
		this.panel_create = new amun.system.event.panel_create();
		this.panel_delete = new amun.system.event.panel_delete();
		this.panel_action_create = new amun.system.event.panel_action_create();
		this.panel_action_delete = new amun.system.event.panel_action_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_delete, this.panel_action_create, this.panel_action_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.event.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		var center = this.panel_receive.center.getTopToolbar();
		var south  = this.panel_receive.south.getTopToolbar();

		// add
		center.get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// del
		center.get(1).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.system.event.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.center.row_id));

			this.layout.setActiveItem(2);

		}, this);

		// add
		south.get(0).on('click', function(){

			this.layout.setActiveItem(3);

		}, this);

		// del
		south.get(1).on('click', function(){

			this.panel_action_delete.load_data(get_proxy_url(amun.system.event.action_url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.south.row_id));

			this.layout.setActiveItem(4);

		}, this);

		// action config
		this.panel_action_create.get(0).on('select', function(field, new_val, old_val){

			amun.system.event.store_config.load({

				params: {action: new_val.data.id},
				scope: this,
				callback: function(record){

					var data = {};

					for(var i = 0; i < record.length; i++)
					{
						data[record[i].data.name] = '';
					}

					this.panel_action_create.get(2).setSource(data);

				}

			});

		}, this);


		var submited = function(){

			amun.system.event.store.load();

			this.panel_receive.center.reset();

			this.layout.setActiveItem(0);

		}

		var canceled = function(){

			this.panel_receive.center.reset();

			this.layout.setActiveItem(0);

		}

		var submited_action = function(){

			amun.system.event.store_action.load();

			this.panel_receive.south.reset();

			this.layout.setActiveItem(0);

		}

		var canceled_action = function(){

			this.panel_receive.south.reset();

			this.layout.setActiveItem(0);

		}


		// create
		this.panel_create.on('submited', submited, this);

		this.panel_create.on('canceled', canceled, this);


		// delete
		this.panel_delete.on('submited', submited, this);

		this.panel_delete.on('canceled', canceled, this);


		// action create
		this.panel_action_create.on('submited', submited_action, this);

		this.panel_action_create.on('canceled', canceled_action, this);


		// action delete
		this.panel_action_delete.on('submited', submited_action, this);

		this.panel_action_delete.on('canceled', canceled_action, this);
	}

});



