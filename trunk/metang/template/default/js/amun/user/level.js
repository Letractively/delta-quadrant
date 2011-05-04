/*
 *  $Id: level.js 120 2010-11-25 14:51:53Z k42b3.x $
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

Ext.ns('amun.user.level');

/**
 * amun.user.level
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 120 $
 */


amun.user.level.url = amun_url + 'api/user/level?format=json';


amun.user.level.store = new Ext.data.GroupingStore({

	url: get_proxy_url(amun.user.level.url),
	restful: true,
	remoteSort: true,
	groupField: 'ugroup',
	sortInfo: {

		field: 'min',
		direction: 'descending'

	},
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

		{name: 'ugroup', type: 'string'},
		{name: 'id',     type: 'int'},
		{name: 'title',  type: 'string'},
		{name: 'min',    type: 'int'}
	])

});


amun.user.level.store_group = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/user/group?format=json&fields=id,title'),
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

		{name: 'id',     type: 'int'},
		{name: 'title',  type: 'string'},
	])

});


amun.user.level.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.user.level.store,
	columns: [

		//{header: 'id',    width: 75,  dataIndex: 'id',     menuDisabled: true, sortable: true},
		{header: 'Title', width: 300, dataIndex: 'title',  menuDisabled: true, sortable: true},
		{header: 'Min.',  width: 100, dataIndex: 'min',    menuDisabled: true, sortable: true},
		{header: 'Group', width: 200, dataIndex: 'ugroup', menuDisabled: true, sortable: true}

	],
	view: new Ext.grid.GroupingView({

		forceFit: true,
		groupTextTpl: '{text} ({[values.rs.length]} {[values.rs.length > 1 ? "Levels" : "Level"]})',
		hideGroupedColumn: true

	}),
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


amun.user.level.panel_create = Ext.extend(amun.common.form, {

	url: amun.user.level.url,
	request_method: 'POST',
	fields: [new Ext.form.ComboBox({

		fieldLabel: 'Group',
		hiddenName: 'group_id',
		store: amun.user.level.store_group,
		valueField: 'id',
		displayField: 'title',
		editable: false,
		mode: 'remote',
		emptyText: 'select a group ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},{

		fieldLabel: 'Min',
		name: 'min',
		allowBlank: false

	},{

		fieldLabel: 'Image',
		name: 'image'

	}]

});


amun.user.level.panel_update = Ext.extend(amun.common.form, {

	url: amun.user.level.url,
	request_method: 'PUT',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Group',
		hiddenName: 'group_id',
		store: amun.user.level.store_group,
		valueField: 'id',
		displayField: 'title',
		editable: false,
		mode: 'remote',
		emptyText: 'select a group ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},{

		fieldLabel: 'Min',
		name: 'min',
		allowBlank: false

	},{

		fieldLabel: 'Image',
		name: 'image'

	}]

});


amun.user.level.panel_delete = Ext.extend(amun.common.form, {

	url: amun.user.level.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Group',
		hiddenName: 'group_id',
		store: amun.user.level.store_group,
		valueField: 'id',
		displayField: 'title',
		editable: false,
		mode: 'remote',
		emptyText: 'Select a group ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		disabled: true

	}),{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Min',
		name: 'min',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Image',
		name: 'image',
		disabled: true

	}]

});


amun.user.level.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.user.level.panel_receive();
		this.panel_create  = new amun.user.level.panel_create();
		this.panel_update  = new amun.user.level.panel_update();
		this.panel_delete  = new amun.user.level.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.user.level.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// add
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			this.panel_update.load_data(get_proxy_url(amun.user.level.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(2);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(2).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.user.level.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(3);

		}, this);


		var submited = function(){

			amun.user.level.store.load();

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


