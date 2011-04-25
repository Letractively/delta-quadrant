/*
 *  $Id: api.js 170 2011-02-19 19:46:21Z k42b3.x $
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

Ext.ns('amun.system.api');

/**
 * amun.system.api
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 170 $
 */

amun.system.api.url = amun_url + '/index.php/api/system/api?format=json';


amun.system.api.store = new Ext.data.Store({

	url: get_proxy_url(amun.system.api.url),
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
		{name: 'status', type: 'string'},
		{name: 'title', type: 'string'},
		{name: 'url', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.system.api.store_request = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/api/request?format=json'),
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
		{name: 'status', type: 'string'},
		{name: 'authorDisplayName', type: 'string'},
		{name: 'callback', type: 'string'},
		{name: 'expire', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.system.api.store_status = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/system/api/listStatus?format=json'),
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

		{name: 'id',   type: 'int'},
		{name: 'name', type: 'string'}

	])

});


amun.system.api.panel_receive_center = Ext.extend(amun.common.grid, {

	store: amun.system.api.store,
	columns: [

		//{header: 'id', width: 75,  dataIndex: 'id',    menuDisabled: true, sortable: true},
		{header: 'Status', width: 100, dataIndex: 'status', menuDisabled: true, sortable: true},
		{header: 'Title', width: 200, dataIndex: 'title', menuDisabled: true, sortable: true},
		{header: 'Url', width: 200, dataIndex: 'url', menuDisabled: true, sortable: true},
		{header: 'Date', width: 200, dataIndex: 'date', menuDisabled: true, sortable: true, renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: [{

		text: 'Add',
		iconCls: 'add'

	},{

		text: 'Edit',
		iconCls: 'edit',
		disabled: true

	},{

		text: 'Delete',
		iconCls: 'delete',
		disabled: true

	},{

		text: 'Keys',
		iconCls: 'key',
		disabled: true

	}]

});


amun.system.api.panel_receive_south = Ext.extend(amun.common.grid, {

	store: amun.system.api.store_request,
	columns: [

		//{header: 'id', width: 75, dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'User', width: 75, dataIndex: 'authorDisplayName', menuDisabled: true, sortable: true},
		{header: 'Status', width: 75,  dataIndex: 'status', menuDisabled: true, sortable: true},
		{header: 'Callback', width: 300, dataIndex: 'callback', menuDisabled: true, sortable: true},
		{header: 'Expire', width: 100, dataIndex: 'expire', menuDisabled: true, sortable: true},
		{header: 'Date', width: 200, dataIndex: 'date', menuDisabled: true, sortable: true, renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: []

});


amun.system.api.panel_receive = Ext.extend(Ext.Panel, {

	center: null,
	south: null,

	initComponent: function(){

		this.center = new amun.system.api.panel_receive_center();
		this.south  = new amun.system.api.panel_receive_south();

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

				title: 'Request',
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

		amun.system.api.panel_receive.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		this.center.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			amun.system.api.store_request.load({params:{api_id: id, start:0, limit:16}});

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


amun.system.api.panel_create = Ext.extend(amun.common.form, {

	url: amun.system.api.url,
	request_method: 'POST',
	fields: [new Ext.form.ComboBox({

		fieldLabel: 'Status',
		hiddenName: 'status',
		store: amun.system.api.store_status,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a status ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'Name',
		name: 'name',
		allowBlank: false

	},{

		fieldLabel: 'Email',
		name: 'email',
		allowBlank: false

	},{

		fieldLabel: 'Url',
		name: 'url',
		allowBlank: false

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},new Ext.form.TextArea({

		fieldLabel: 'Description',
		name: 'description',
		allowBlank: false,
		height: 200

	}),{

		fieldLabel: 'Callback',
		name: 'callback',
		allowBlank: true

	}]

});


amun.system.api.panel_update = Ext.extend(amun.common.form, {

	url: amun.system.api.url,
	request_method: 'PUT',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Status',
		hiddenName: 'status',
		store: amun.system.api.store_status,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a status ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'Name',
		name: 'name',
		allowBlank: false

	},{

		fieldLabel: 'Email',
		name: 'email',
		allowBlank: false

	},{

		fieldLabel: 'Url',
		name: 'url',
		allowBlank: false

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},new Ext.form.TextArea({

		fieldLabel: 'Description',
		name: 'description',
		allowBlank: false,
		height: 200

	}),{

		fieldLabel: 'Callback',
		name: 'callback',
		allowBlank: true

	}]

});


amun.system.api.panel_delete = Ext.extend(amun.common.form, {

	url: amun.system.api.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Status',
		hiddenName: 'status',
		store: amun.system.api.store_status,
		valueField: 'id',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a status ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		disabled: true

	}),{

		fieldLabel: 'Name',
		name: 'name',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Email',
		name: 'email',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Url',
		name: 'url',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false,
		disabled: true

	},new Ext.form.TextArea({

		fieldLabel: 'Description',
		name: 'description',
		allowBlank: false,
		height: 200,
		disabled: true

	}),{

		fieldLabel: 'Callback',
		name: 'callback',
		allowBlank: true,
		disabled: true

	}]

});


amun.system.api.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.system.api.panel_receive();
		this.panel_create  = new amun.system.api.panel_create();
		this.panel_update  = new amun.system.api.panel_update();
		this.panel_delete  = new amun.system.api.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.api.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		var center = this.panel_receive.center.getTopToolbar();
		var south  = this.panel_receive.south.getTopToolbar();

		// add
		center.get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// edit
		center.get(1).on('click', function(){

			this.panel_update.load_data(get_proxy_url(amun.system.api.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.center.row_id));

			this.layout.setActiveItem(2);

		}, this);

		// del
		center.get(2).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.system.api.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.center.row_id));

			this.layout.setActiveItem(3);

		}, this);

		// keys
		center.get(3).on('click', function(){

			// request keys
			var con = new Ext.data.Connection();

			con.request({

				url: get_proxy_url(amun.system.api.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.center.row_id + '&fields=consumerKey,consumerSecret'),
				method: 'GET',
				scope: this,
				success: function(response){

					var resp  = Ext.util.JSON.decode(response.responseText);
					var entry = Ext.util.Format.defaultValue(resp.entry, []);

					Ext.Msg.alert('Information', 'Consumer key: ' + entry[0].consumerKey + '<br />Consumer secret: ' + entry[0].consumerSecret);

				}

			});

		}, this);

		var submited = function(){

			amun.system.api.store.load();

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


		// update
		this.panel_update.on('submited', submited, this);

		this.panel_update.on('canceled', canceled, this);


		// delete
		this.panel_delete.on('submited', submited, this);

		this.panel_delete.on('canceled', canceled, this);
	}

});




/*
amun.system.api.store = new Ext.data.Store({

	url: amun_url + '/index.php?x=api/system/api&format=json',
	remoteSort: true,
	reader: new Ext.data.JsonReader({

		totalProperty: 'totalResults',
		root: 'entry',
		idProperty: 'id'

	},[

		{name: 'id',           type: 'int'},
		{name: 'status',       type: 'string'},
		{name: 'title',        type: 'string'},
		{name: 'url',          type: 'string'},
		{name: 'consumer_key', type: 'string'},
		{name: 'date',         type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.system.api.store_status = new Ext.data.Store({

	url: amun_url + '/index.php?x=admin/system/api/util/status',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


amun.system.api.store_request = new Ext.data.Store({

	url: amun_url + '/index.php?x=admin/system/api/request',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		totalProperty: 'results',
		record: 'item',
		id: 'id'

	},[

		{name: 'id',       type: 'int'},
		{name: 'user_id',  type: 'int'},
		{name: 'api_id',   type: 'int'},
		{name: 'status',   type: 'string'},
		{name: 'ip',       type: 'string'},
		{name: 'callback', type: 'string'},
		{name: 'date',     type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.system.api.store_api = new Ext.data.Store({

	url: amun_url + '/index.php?x=admin/system/api/util/lists',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


amun.system.api.index_center = Ext.extend(amun.common.grid, {

	store: amun.system.api.store,
	columns: [

		{header: 'id',     width: 75,  dataIndex: 'id',     menuDisabled: true, sortable: true},
		{header: 'status', width: 75,  dataIndex: 'status', menuDisabled: true, sortable: true},
		{header: 'title',  width: 225, dataIndex: 'title',  menuDisabled: true, sortable: true},
		{header: 'url',    width: 300, dataIndex: 'url',    menuDisabled: true, sortable: true},
		{header: 'date',   width: 170, dataIndex: 'date',   menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: [{

		text: 'add',
		iconCls: 'add'

	},{

		text: 'edit',
		iconCls: 'edit',
		disabled: true

	},{

		text: 'delete',
		iconCls: 'delete',
		disabled: true

	},{

		text: 'keys',
		iconCls: 'key',
		disabled: true

	}]

});


amun.system.api.index_south = Ext.extend(amun.common.grid, {

	store: amun.system.api.store_request,
	columns: [

		{header: 'id',       width: 75,  dataIndex: 'id',       menuDisabled: true, sortable: true},
		{header: 'user_id',  width: 75,  dataIndex: 'user_id',  menuDisabled: true, sortable: true},
		{header: 'status',   width: 75,  dataIndex: 'status',   menuDisabled: true, sortable: true},
		{header: 'ip',       width: 150, dataIndex: 'ip',       menuDisabled: true, sortable: true},
		{header: 'callback', width: 300, dataIndex: 'callback', menuDisabled: true, sortable: true},
		{header: 'date',     width: 170, dataIndex: 'date',     menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: []

});


amun.system.api.index = Ext.extend(Ext.Panel, {

	center: null,
	south: null,

	initComponent: function(){

		this.center = new amun.system.api.index_center();
		this.south  = new amun.system.api.index_south();

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

				title: 'Request',
				region: 'south',
				layout: 'fit',
				height: 300,
				minSize: 150,
				maxSize: 300,
				collapsible: true,
				items: [this.south]

			}]

		};

		this.handler_index();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.api.index.superclass.initComponent.apply(this, arguments);

	},

	handler_index: function(){

		this.center.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			amun.system.api.store_request.load({params:{api_id: id, start:0, limit:16}});

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


amun.system.api.form = Ext.extend(amun.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'status',
		hiddenName: 'status',
		store: amun.system.api.store_status,
		valueField: 'value',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'select a status ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'name',
		name: 'name',
		allowBlank: false

	},{

		fieldLabel: 'email',
		name: 'email',
		allowBlank: false

	},{

		fieldLabel: 'url',
		name: 'url',
		allowBlank: false

	},{

		fieldLabel: 'title',
		name: 'title',
		allowBlank: false

	},new Ext.form.TextArea({

		fieldLabel: 'description',
		name: 'description',
		allowBlank: false,
		height: 200

	}),{

		fieldLabel: 'callback',
		name: 'callback',
		allowBlank: true

	}]

});


amun.system.api.panel = Ext.extend(Ext.Panel, {

	o_index: null,
	o_form: null,
	o_win: null,

	initComponent: function(){

		this.o_index = new amun.system.api.index();
		this.o_form  = new amun.system.api.form();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.o_index, this.o_form]

		};

		this.handler_index();
		this.handler_form();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.api.panel.superclass.initComponent.apply(this, arguments);

	},

	handler_index: function(){

		var center = this.o_index.center.getTopToolbar();
		var south  = this.o_index.south.getTopToolbar();

		// add
		center.get(0).on('click', function(){

			this.o_form.url = amun_url + '/index.php?x=admin/system/api/add';

			this.layout.setActiveItem(1);

		}, this);

		// edit
		center.get(1).on('click', function(){

			this.o_form.url = amun_url + '/index.php?x=admin/system/api/edit';

			this.o_form.load_data(amun_url + '/index.php?x=admin/system/api/util/item&id=' + this.o_index.center.row_id);

			this.layout.setActiveItem(1);

		}, this);

		// del
		center.get(2).on('click', function(){

			this.o_form.url = amun_url + '/index.php?x=admin/system/api/delete';

			this.o_form.load_data(amun_url + '/index.php?x=admin/system/api/util/item&id=' + this.o_index.center.row_id);

			this.layout.setActiveItem(1);

		}, this);

		// key
		center.get(3).on('click', function(){

			var sel    = this.o_index.center.getSelectionModel();
			var record = sel.getSelected();
			var key    = record.get('consumer_key');


			var conn = new Ext.data.Connection();

			conn.request({

				url: amun_url + '/index.php?x=admin/system/api/secret&consumer_key=' + key,
				method: 'GET',
				scope: this,
				success: function(responseObject) {

					var buttons = new Ext.Button({

						text: 'Regenerate',
						scope: this,
						handler: function(){

							var conn = new Ext.data.Connection();

							conn.request({

								url: amun_url + '/index.php?x=admin/system/api/secret/regenerate',
								method: 'POST',
								params: {id: this.o_index.center.row_id},
								scope: this,
								success: function(responseObject){

									this.o_win.body.update('<div style="padding:8px;"><pre>' + responseObject.responseText + '</pre></div>');

								},
								failure: function(responseObject){

									Ext.MessageBox.alert('failure', 'couldnt regenerate keys');

								}

							});

						}

					});

					this.o_win = new Ext.Window({

						title: 'Consumer keys',
						width: 320,
						height: 116,
						resizable: false,
						html: '<div style="padding:8px;"><pre>' + responseObject.responseText + '</pre></div>',
						buttons: [buttons]

					});

					this.o_win.show();

				},
				failure: function() {

					Ext.Msg.alert('error', 'couldnt request consumer keys');

				}
			});

		}, this);
	},

	handler_form: function(){

		this.o_form.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						amun.system.api.store.load({params:{start:0, limit:16}});

						this.o_index.center.reset();

						this.layout.setActiveItem(0);

						this.o_form.reset();

					}, this);
				}

			}

		}, this);

		this.o_form.on('actionfailed', function(form, action){

			var msg = Ext.util.Format.defaultValue(action.result.message, '');

			if(msg != '')
			{
				Ext.MessageBox.alert('failure', msg);
			}

		}, this);

		this.o_form.on('submited', function(){

			this.o_form.getForm().submit({

				url: this.o_form.url,
				waitMsg: 'sending data ...'

			});

		}, this);

		this.o_form.on('canceled', function(){

			this.o_index.center.reset();

			this.layout.setActiveItem(0);

			this.o_form.reset();

		}, this);

	}

});
*/


