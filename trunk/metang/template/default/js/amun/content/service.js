/*
 *  $Id: service.js 120 2010-11-25 14:51:53Z k42b3.x $
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

Ext.ns('amun.content.service');

/**
 * amun.content.service
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 120 $
 */


amun.content.service.url = amun_url + '/index.php/api/content/service?format=json';


amun.content.service.store = new Ext.data.Store({

	url: get_proxy_url(amun.content.service.url),
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

		{name: 'id',      type: 'int'},
		{name: 'status',  type: 'string'},
		{name: 'name',    type: 'string'},
		{name: 'type',    type: 'string'},
		{name: 'link',    type: 'string'},
		{name: 'author',  type: 'string'},
		{name: 'license', type: 'string'},
		{name: 'version', type: 'string'},
		{name: 'date',    type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.content.service.store_service = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/service/listInstallableService?format=json'),
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
		idProperty: 'value',
		root: 'entry',
		messageProperty: 'message'

	},[

		{name: 'name',  type: 'string'},
		{name: 'value', type: 'string'}

	])

});


amun.content.service.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.content.service.store,
	columns: [

		//{header: 'id',      width: 75,  dataIndex: 'id',      menuDisabled: true, sortable: true},
		//{header: 'Status',  width: 100, dataIndex: 'status',  menuDisabled: true, sortable: true},
		{header: 'Name',    width: 100, dataIndex: 'name',    menuDisabled: true, sortable: true},
		{header: 'Link',    width: 150, dataIndex: 'link',    menuDisabled: true, sortable: true},
		{header: 'Author',  width: 150, dataIndex: 'author',  menuDisabled: true, sortable: true},
		{header: 'License', width: 100, dataIndex: 'license', menuDisabled: true, sortable: true},
		{header: 'Version', width: 100, dataIndex: 'version', menuDisabled: true, sortable: true},
		{header: 'Date',    width: 170, dataIndex: 'date',    menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: [{

		text: 'Install',
		iconCls: 'install'

	},{

		text: 'Uninstall',
		iconCls: 'uninstall',
		disabled: true

	}]

});


amun.content.service.panel_create = Ext.extend(amun.common.form, {

	url: amun.content.service.url,
	request_method: 'POST',
	fields: [new Ext.form.ComboBox({

		fieldLabel: 'Service',
		hiddenName: 'name',
		store: amun.content.service.store_service,
		valueField: 'value',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a service ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16

	})]

});


amun.content.service.panel_delete = Ext.extend(amun.common.form, {

	url: amun.content.service.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'Status',
		name: 'status',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Name',
		name: 'name',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Link',
		name: 'link',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Author',
		name: 'author',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'License',
		name: 'license',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Version',
		name: 'version',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Date',
		name: 'date',
		allowBlank: false,
		disabled: true

	}]

});


amun.content.service.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.content.service.panel_receive();
		this.panel_create  = new amun.content.service.panel_create();
		this.panel_delete  = new amun.content.service.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.content.service.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// add
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.content.service.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(2);

		}, this);


		var submited = function(){

			amun.content.service.store.load();

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


		// delete
		this.panel_delete.on('submited', submited, this);

		this.panel_delete.on('canceled', canceled, this);
	}

});



