/*
 *  $Id: activity.js 170 2011-02-19 19:46:21Z k42b3.x $
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

Ext.ns('amun.user.activity');

/**
 * amun.user.activity
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 170 $
 */


amun.user.activity.url = amun_url + 'api/user/activity?format=json';


amun.user.activity.store = new Ext.data.Store({

	url: get_proxy_url(amun.user.activity.url),
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
		{name: 'authorId', type: 'int'},
		{name: 'authorDisplayName', type: 'string'},
		{name: 'title', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.user.activity.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.user.activity.store,
	columns: [

		//{header: 'id', width: 75,  dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'User', width: 150, dataIndex: 'authorDisplayName', menuDisabled: true, sortable: true},
		{header: 'Title', width: 350, dataIndex: 'title', menuDisabled: true, sortable: true},
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


amun.user.activity.panel_create = Ext.extend(amun.common.form, {

	url: amun.user.activity.url,
	request_method: 'POST',
	fields: [{

		fieldLabel: 'User',
		name: 'user_id',
		allowBlank: false

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},new Ext.form.TextArea({

		fieldLabel: 'Text',
		name: 'body',
		height: 200

	})]

});


amun.user.activity.panel_update = Ext.extend(amun.common.form, {

	url: amun.user.activity.url,
	request_method: 'PUT',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'User',
		name: 'user_id',
		allowBlank: false

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},new Ext.form.TextArea({

		fieldLabel: 'Text',
		name: 'body',
		height: 200

	})]

});


amun.user.activity.panel_delete = Ext.extend(amun.common.form, {

	url: amun.user.activity.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'User',
		name: 'user_id',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false,
		disabled: true

	},new Ext.form.TextArea({

		fieldLabel: 'Text',
		name: 'body',
		height: 200,
		disabled: true

	})]

});


amun.user.activity.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.user.activity.panel_receive();
		this.panel_create  = new amun.user.activity.panel_create();
		this.panel_update  = new amun.user.activity.panel_update();
		this.panel_delete  = new amun.user.activity.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.user.activity.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// add
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			this.panel_update.load_data(get_proxy_url(amun.user.activity.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(2);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(2).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.user.activity.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(3);

		}, this);


		var submited = function(){

			amun.user.activity.store.load();

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


