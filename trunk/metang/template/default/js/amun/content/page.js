/*
 *  $Id: page.js 193 2011-03-06 02:14:57Z k42b3.x $
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

Ext.ns('amun.content.page');

/**
 * amun.content.page
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 193 $
 */


amun.content.page.url = amun_url + 'api/content/page?format=json';


amun.content.page.store = new Ext.data.Store({

	url: get_proxy_url(amun.content.page.url),
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

		{name: 'id',          type: 'int'},
		{name: 'parent_id',   type: 'int'},
		{name: 'application', type: 'string'},
		{name: 'status',      type: 'int'},
		{name: 'url_title',   type: 'string'},
		{name: 'title',       type: 'string'},
		{name: 'template',    type: 'string'},
		{name: 'cache',       type: 'bool'},
		{name: 'expire',      type: 'int'},
		{name: 'date',        type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.content.page.store_page = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/page?format=json&fields=id,title'),
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

		{name: 'id',    type: 'int'},
		{name: 'title', type: 'string'}

	])

});


amun.content.page.store_application = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/page/listApplication?format=json'),
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

		{name: 'value', type: 'string'},
		{name: 'name',  type: 'string'}

	])

});


amun.content.page.store_status = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/page/listStatus?format=json'),
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


amun.content.page.store_template = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/page/listTemplate?format=json'),
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

		{name: 'value', type: 'string'},
		{name: 'name',  type: 'string'}

	])

});


/*
amun.content.page.store_service = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/service?format=json&fields=id,name'),
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
*/


amun.content.page.store_gadget = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/page/listGadget?format=json'),
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
		{name: 'title',   type: 'string'},
		{name: 'checked', type: 'bool'}

	])

});


amun.content.page.store_group = new Ext.data.Store({

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

		{name: 'id',    type: 'int'},
		{name: 'title', type: 'string'}

	])

});


amun.content.page.store_right = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/page/listRight?format=json'),
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

		{name: 'id',    type: 'int'},
		{name: 'title', type: 'string'},
		{name: 'group', type: 'string'}

	])

});


amun.content.page.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.content.page.store,
	columns: [

		//{header: 'id',         width: 75,  dataIndex: 'id',         menuDisabled: true, sortable: true},
		//{header: 'parent_id',  width: 75,  dataIndex: 'parent_id',  menuDisabled: true, sortable: true},
		//{header: 'service_id', width: 75,  dataIndex: 'service_id', menuDisabled: true, sortable: true},
		{header: 'Status',      width: 50,  dataIndex: 'status',      menuDisabled: true, sortable: true},
		{header: 'Title',       width: 200, dataIndex: 'title',       menuDisabled: true, sortable: true},
		{header: 'Template',    width: 125, dataIndex: 'template',    menuDisabled: true, sortable: true},
		{header: 'Application', width: 125, dataIndex: 'application', menuDisabled: true, sortable: true},
		{header: 'Date',        width: 170, dataIndex: 'date',        menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

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


amun.content.page.panel_create = Ext.extend(amun.common.form, {

	url: amun.content.page.url,
	request_method: 'POST',

	layout: 'fit',
	labelWidth: 125,
	frame: false,
	border: false,
	bodyStyle: null,
	trackResetOnLoad: false,
	defaults: {

		width: 250

	},

	fields: [{

		xtype: 'tabpanel',
		autoWidth: true,
		activeTab: 0,
		border: false,
		deferredRender: false,
		defaults: {

			layout: 'form',
			labelWidth: 125,
			frame: false,
			border: false,
			bodyStyle: 'padding:5px 5px 0',
			autoScroll: true,
			trackResetOnLoad: true,
			defaultType: 'textfield',
			hideMode: 'offsets',
			defaults: {

				width: 250

			}

		},
		items: [{

			title: 'Settings',
			items: [new Ext.form.ComboBox({

				fieldLabel: 'Parent',
				name: 'parent_id',
				hiddenName: 'parent_id',
				store: amun.content.page.store_page,
				valueField: 'id',
				displayField: 'title',
				editable: true,
				mode: 'remote',
				emptyText: 'Select a parent ...',
				selectOnFocus: true,
				triggerAction: 'all',
				allowBlank: false,
				listWidth: 250,
				pageSize: 16

			}),new Ext.form.ComboBox({

				fieldLabel: 'Application',
				name: 'application',
				hiddenName: 'application',
				store: amun.content.page.store_application,
				valueField: 'value',
				displayField: 'name',
				editable: false,
				mode: 'remote',
				emptyText: 'Select a application ...',
				selectOnFocus: true,
				triggerAction: 'all',
				allowBlank: false,
				listWidth: 250,
				pageSize: 16

			}),new Ext.form.ComboBox({

				fieldLabel: 'Status',
				name: 'status',
				hiddenName: 'status',
				store: amun.content.page.store_status,
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

				fieldLabel: 'Title',
				name: 'title',
				allowBlank: false

			}]

		},{

			title: 'Extras',
			items: [new Ext.form.ComboBox({

				fieldLabel: 'Template',
				name: 'template',
				hiddenName: 'template',
				store: amun.content.page.store_template,
				valueField: 'value',
				displayField: 'name',
				editable: false,
				mode: 'remote',
				emptyText: 'Select a template ...',
				selectOnFocus: true,
				triggerAction: 'all',
				allowBlank: true,
				listWidth: 250

			}),{

				fieldLabel: 'Cache',
				xtype: 'checkbox',
				name: 'cache'

			},{

				fieldLabel: 'Expire',
				name: 'expire',
				allowBlank: true,
				value: ''

			}]

		},{

			title: 'Gadgets',
			items: [new Ext.grid.GridPanel({

				fieldLabel: 'Gadgets',
				height: 256,
				margins: '0 0 0 0',
				trackMouseOver: false,
				store: amun.content.page.store_gadget,
				hideHeaders: true,
				border: true,
				stripeRows: true,
				columns: [

					{width: 250, dataIndex: 'title'},
					{xtype: 'checkcolumn', width: 55, dataIndex: 'checked'}

				],
				viewConfig: {

					forceFit: true

				}

			})]

		},{

			title: 'Rights',
			items: [new Ext.grid.EditorGridPanel({

				fieldLabel: 'Rights',
				height: 256,
				margins: '0 0 0 0',
				trackMouseOver: false,
				store: amun.content.page.store_right,
				hideHeaders: true,
				border: true,
				stripeRows: true,
				clicksToEdit: 1,
				columns: [

					{width: 125, dataIndex: 'title'},
					{width: 125, dataIndex: 'group', editor: new Ext.form.ComboBox({

						store: amun.content.page.store_group,
						valueField: 'id',
						displayField: 'title',
						editable: false,
						mode: 'remote',
						emptyText: 'Select a group ...',
						selectOnFocus: true,
						triggerAction: 'all',
						allowBlank: true,
						listWidth: 125,
						typeAhead: true

					})}

				],
				viewConfig: {

					forceFit: true

				}

			})]

		}]

	}],

	get_fields: function(){

		var values = this.getForm().getFieldValues();


		// get gadgets
		var gadgets = '';

		amun.content.page.store_gadget.each(function(r){

			if(r.get('checked'))
			{
				gadgets+= parseInt(r.get('id')) + ',';
			}

		});

		if(gadgets.length > 1)
		{
			gadgets = gadgets.substring(0, gadgets.length - 1);
		}
		else
		{
			gadgets = '';
		}

		values.gadgets = gadgets;


		// get rights
		var rights = {};

		amun.content.page.store_right.each(function(r){

			var group_id     = parseInt(r.get('id'));
			var new_group_id = parseInt(r.get('group'));

			if(group_id != new_group_id)
			{
				rights[group_id] = new_group_id;
			}

		});

		values.rights = rights;


		return values;

	},

	set_active_tab: function(index){

		this.get(0).setActiveTab(index);

	},

	reset: function(){

		this.get(0).items.each(function(item, i){

			item.items.each(function(item, i){

				if(typeof item.setValue == 'function')
				{
					item.setValue(null);
				}

			});

		});

		this.getForm().clearInvalid();

	}

});


amun.content.page.panel_update = Ext.extend(amun.common.form, {

	url: amun.content.page.url,
	request_method: 'PUT',

	layout: 'fit',
	labelWidth: 125,
	frame: false,
	border: false,
	bodyStyle: null,
	trackResetOnLoad: false,
	defaults: {

		width: 250

	},

	fields: [{

		xtype: 'tabpanel',
		autoWidth: true,
		activeTab: 0,
		border: false,
		deferredRender: false,
		defaults: {

			layout: 'form',
			labelWidth: 125,
			frame: false,
			border: false,
			bodyStyle: 'padding:5px 5px 0',
			autoScroll: true,
			trackResetOnLoad: true,
			defaultType: 'textfield',
			hideMode: 'offsets',
			defaults: {

				width: 250

			}

		},
		items: [{

			title: 'Settings',
			items: [{

				xtype: 'hidden',
				fieldLabel: 'id',
				name: 'id',
				readOnly: true

			},new Ext.form.ComboBox({

				fieldLabel: 'Parent',
				name: 'parent_id',
				hiddenName: 'parent_id',
				store: amun.content.page.store_page,
				valueField: 'id',
				displayField: 'title',
				editable: true,
				mode: 'remote',
				emptyText: 'Select a parent ...',
				selectOnFocus: true,
				triggerAction: 'all',
				allowBlank: true,
				listWidth: 250,
				pageSize: 16

			}),new Ext.form.ComboBox({

				fieldLabel: 'Application',
				name: 'application',
				hiddenName: 'application',
				store: amun.content.page.store_application,
				valueField: 'value',
				displayField: 'name',
				editable: false,
				mode: 'remote',
				emptyText: 'Select a application ...',
				selectOnFocus: true,
				triggerAction: 'all',
				allowBlank: false,
				listWidth: 250,
				pageSize: 16

			}),new Ext.form.ComboBox({

				fieldLabel: 'Status',
				name: 'status',
				hiddenName: 'status',
				store: amun.content.page.store_status,
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

				fieldLabel: 'Title',
				name: 'title',
				allowBlank: false

			}]

		},{

			title: 'Extras',
			items: [{

				fieldLabel: 'Load',
				name: 'load',
				allowBlank: true

			},new Ext.form.ComboBox({

				fieldLabel: 'Template',
				name: 'template',
				hiddenName: 'template',
				store: amun.content.page.store_template,
				valueField: 'value',
				displayField: 'name',
				editable: false,
				mode: 'remote',
				emptyText: 'Select a template ...',
				selectOnFocus: true,
				triggerAction: 'all',
				allowBlank: true,
				listWidth: 250

			}),{

				fieldLabel: 'Cache',
				xtype: 'checkbox',
				name: 'cache'

			},{

				fieldLabel: 'Expire',
				name: 'expire',
				allowBlank: true,
				value: '0'

			}]

		},{

			title: 'Gadgets',
			items: [new Ext.grid.GridPanel({

				fieldLabel: 'Gadgets',
				height: 256,
				margins: '0 0 0 0',
				trackMouseOver: false,
				store: amun.content.page.store_gadget,
				hideHeaders: true,
				border: true,
				stripeRows: true,
				columns: [

					{width: 250, dataIndex: 'title'},
					{xtype: 'checkcolumn', width: 55, dataIndex: 'checked'}

				],
				viewConfig: {

					forceFit: true

				}

			})]

		},{

			title: 'Rights',
			items: [new Ext.grid.EditorGridPanel({

				fieldLabel: 'Rights',
				height: 256,
				margins: '0 0 0 0',
				trackMouseOver: false,
				store: amun.content.page.store_right,
				hideHeaders: true,
				border: true,
				stripeRows: true,
				clicksToEdit: 1,
				columns: [

					{width: 125, dataIndex: 'title'},
					{width: 125, dataIndex: 'group', editor: new Ext.form.ComboBox({

						store: amun.content.page.store_group,
						valueField: 'id',
						displayField: 'title',
						editable: false,
						mode: 'remote',
						emptyText: 'Select a group ...',
						selectOnFocus: true,
						triggerAction: 'all',
						allowBlank: true,
						listWidth: 125,
						typeAhead: true

					})}

				],
				viewConfig: {

					forceFit: true

				}

			})]

		}]

	}],

	get_fields: function(){

		var values = this.getForm().getFieldValues();


		// get gadgets
		var gadgets = '';

		amun.content.page.store_gadget.each(function(r){

			if(r.get('checked'))
			{
				gadgets+= parseInt(r.get('id')) + ',';
			}

		});

		if(gadgets.length > 1)
		{
			gadgets = gadgets.substring(0, gadgets.length - 1);
		}
		else
		{
			gadgets = '';
		}

		values.gadgets = gadgets;


		// get rights
		var rights = {};

		amun.content.page.store_right.each(function(r){

			var group_id     = parseInt(r.get('id'));
			var new_group_id = parseInt(r.get('group'));

			if(group_id != new_group_id)
			{
				rights[group_id] = new_group_id;
			}

		});

		values.rights = rights;


		return values;

	},

	set_active_tab: function(index){

		this.get(0).setActiveTab(index);

	},

	reset: function(){

		this.get(0).items.each(function(item, i){

			item.items.each(function(item, i){

				if(typeof item.setValue == 'function')
				{
					item.setValue(null);
				}

			});

		});

		this.getForm().clearInvalid();

	}

});


amun.content.page.panel_delete = Ext.extend(amun.common.form, {

	url: amun.content.page.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	}]

});


amun.content.page.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.content.page.panel_receive();
		this.panel_create  = new amun.content.page.panel_create();
		this.panel_update  = new amun.content.page.panel_update();
		this.panel_delete  = new amun.content.page.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.content.page.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// add
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			amun.content.page.store_gadget.load();

			amun.content.page.store_right.load();

			this.panel_create.set_active_tab(0);

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			amun.content.page.store_gadget.proxy.setUrl(get_proxy_url(amun_url + '/index.php/api/content/page/listGadget?format=json&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			amun.content.page.store_gadget.load();

			amun.content.page.store_right.proxy.setUrl(get_proxy_url(amun_url + '/index.php/api/content/page/listRight?format=json&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			amun.content.page.store_right.load();

			this.panel_update.load_data(get_proxy_url(amun.content.page.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.panel_update.set_active_tab(0);

			this.layout.setActiveItem(2);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(2).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.content.page.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(3);

		}, this);


		var submited = function(){

			amun.content.page.store.load();

			this.panel_receive.reset();

			this.layout.setActiveItem(0);

			this.fireEvent('reloadtree');

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



