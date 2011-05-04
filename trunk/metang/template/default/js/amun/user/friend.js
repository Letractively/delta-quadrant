/*
 *  $Id: friend.js 155 2011-01-29 23:05:13Z k42b3.x $
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

Ext.ns('amun.user.friend');

/**
 * amun.user.friend
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 155 $
 */


amun.user.friend.url = amun_url + 'api/user/friend?format=json';


amun.user.friend.store = new Ext.data.Store({

	url: get_proxy_url(amun.user.friend.url),
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
		{name: 'status', type: 'int'},
		{name: 'authorDisplayName', type: 'string'},
		{name: 'friendDisplayName', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.user.friend.store_status = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/user/friend/listStatus?format=json'),
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


amun.user.friend.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.user.friend.store,
	columns: [

		//{header: 'id', width: 75,  dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Status', width: 100, dataIndex: 'status', menuDisabled: true, sortable: true},
		{header: 'User', width: 200, dataIndex: 'authorDisplayName', menuDisabled: true, sortable: true},
		{header: 'Friend', width: 200, dataIndex: 'friendDisplayName', menuDisabled: true, sortable: true},
		{header: 'Date', width: 200, dataIndex: 'date', menuDisabled: true, sortable: true}

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

	}]

});


amun.user.friend.panel_create = Ext.extend(amun.common.form, {

	url: amun.user.friend.url,
	request_method: 'POST',
	fields: [new Ext.form.ComboBox({

		fieldLabel: 'Status',
		hiddenName: 'status',
		store: amun.user.friend.store_status,
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

		fieldLabel: 'User',
		name: 'user_id',
		allowBlank: false

	},{

		fieldLabel: 'Friend',
		name: 'friend_id',
		allowBlank: false

	}]

});


amun.user.friend.panel_update = Ext.extend(amun.common.form, {

	url: amun.user.friend.url,
	request_method: 'PUT',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Status',
		hiddenName: 'status',
		store: amun.user.friend.store_status,
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

		fieldLabel: 'User',
		name: 'user_id',
		allowBlank: false

	},{

		fieldLabel: 'Friend',
		name: 'friend_id',
		allowBlank: false

	}]

});


amun.user.friend.panel_delete = Ext.extend(amun.common.form, {

	url: amun.user.friend.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Status',
		hiddenName: 'status',
		store: amun.user.friend.store_status,
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

		fieldLabel: 'User',
		name: 'user_id',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Friend',
		name: 'friend_id',
		allowBlank: false,
		disabled: true

	}]

});


amun.user.friend.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.user.friend.panel_receive();
		this.panel_create  = new amun.user.friend.panel_create();
		this.panel_update  = new amun.user.friend.panel_update();
		this.panel_delete  = new amun.user.friend.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.user.friend.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// add
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			this.panel_update.load_data(get_proxy_url(amun.user.friend.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(2);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(2).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.user.friend.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(3);

		}, this);


		var submited = function(){

			amun.user.friend.store.load();

			this.panel_receive.reset();

			this.layout.setActiveItem(0);

		}

		var canceled = function(){

			this.panel_receive.reset();

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


