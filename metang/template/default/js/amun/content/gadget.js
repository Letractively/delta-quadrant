/*
 *  $Id: gadget.js 191 2011-03-04 22:53:27Z k42b3.x $
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

Ext.ns('amun.content.gadget');

/**
 * amun.content.gadget
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 191 $
 */


amun.content.gadget.url = amun_url + '/index.php/api/content/gadget?format=json';


amun.content.gadget.store = new Ext.data.Store({

	url: get_proxy_url(amun.content.gadget.url),
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
		{name: 'title', type: 'string'},
		{name: 'path', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.content.gadget.store_path = new Ext.data.Store({

	url: get_proxy_url(amun_url + '/index.php/api/content/gadget/listGadget?format=json'),
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
		idProperty: 'path',
		root: 'entry',
		messageProperty: 'message'

	},[

		{name: 'title', type: 'string'},
		{name: 'path', type: 'string'}

	])

});


amun.content.gadget.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.content.gadget.store,
	columns: [

		//{header: 'id', width: 75,  dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Title', width: 200, dataIndex: 'title', menuDisabled: true, sortable: true},
		{header: 'Path', width: 300, dataIndex: 'path', menuDisabled: true, sortable: true},
		{header: 'Date', width: 170, dataIndex: 'date', menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

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


amun.content.gadget.panel_create = Ext.extend(amun.common.form, {

	url: amun.content.gadget.url,
	request_method: 'POST',
	fields: [{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},new Ext.form.ComboBox({

		fieldLabel: 'Path',
		hiddenName: 'path',
		store: amun.content.gadget.store_path,
		valueField: 'path',
		displayField: 'title',
		editable: true,
		mode: 'remote',
		emptyText: 'Select a path ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16

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

});


amun.content.gadget.panel_update = Ext.extend(amun.common.form, {

	url: amun.content.gadget.url,
	request_method: 'PUT',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false

	},new Ext.form.ComboBox({

		fieldLabel: 'Path',
		hiddenName: 'path',
		store: amun.content.gadget.store_path,
		valueField: 'path',
		displayField: 'title',
		editable: true,
		mode: 'remote',
		emptyText: 'Select a path ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16

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

});


amun.content.gadget.panel_delete = Ext.extend(amun.common.form, {

	url: amun.content.gadget.url,
	request_method: 'DELETE',
	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'Title',
		name: 'title',
		allowBlank: false,
		disabled: true

	},new Ext.form.ComboBox({

		fieldLabel: 'Path',
		hiddenName: 'path',
		store: amun.content.gadget.store_path,
		valueField: 'path',
		displayField: 'title',
		editable: true,
		mode: 'remote',
		emptyText: 'Select a path ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250,
		pageSize: 16,
		disabled: true

	}),{

		fieldLabel: 'Cache',
		xtype: 'checkbox',
		name: 'cache',
		disabled: true

	},{

		fieldLabel: 'Expire',
		name: 'expire',
		allowBlank: true,
		value: '',
		disabled: true

	}]

});


amun.content.gadget.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_create: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.content.gadget.panel_receive();
		this.panel_create  = new amun.content.gadget.panel_create();
		this.panel_update  = new amun.content.gadget.panel_update();
		this.panel_delete  = new amun.content.gadget.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_create, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.content.gadget.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// add
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			this.panel_update.load_data(get_proxy_url(amun.content.gadget.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(2);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(2).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.content.gadget.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(3);

		}, this);


		var submited = function(){

			amun.content.gadget.store.load();

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





/*
 *  $Id: gadget.js 191 2011-03-04 22:53:27Z k42b3.x $

Ext.ns('amun.content.gadget');

amun.content.gadget.store = new Ext.data.Store({

	url: amun_url + '/index.php/api/content/gadget?format=json',
	remoteSort: true,
	paramNames: {

		start: 'startIndex',
		limit: 'count',
		sort: 'sortBy',
		dir: 'sortOrder'

	},
	reader: new Ext.data.JsonReader({

		totalProperty: 'totalResults',
		root: 'entry',
		idProperty: 'id'

	},[

		{name: 'id',      type: 'int'},
		{name: 'sort_id', type: 'int'},
		{name: 'title',   type: 'string'},
		{name: 'href',    type: 'string'},
		{name: 'token',   type: 'string'},
		{name: 'date',    type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.content.gadget.store_list = new Ext.data.Store({

	url: amun_url + '/index.php/api/content/gadget/listGadget?format=json',
	remoteSort: true,
	reader: new Ext.data.JsonReader({

		totalProperty: 'totalResults',
		root: 'entry',
		idProperty: 'href'

	},[

		{name: 'href',  type: 'string'},
		{name: 'title', type: 'string'}

	])

});


amun.content.gadget.index = Ext.extend(amun.common.grid, {

	store: amun.content.gadget.store,
	columns: [

		{header: 'id',      width: 75,  dataIndex: 'id',      menuDisabled: true, sortable: true},
		{header: 'sort_id', width: 75,  dataIndex: 'sort_id', menuDisabled: true, sortable: true},
		{header: 'title',   width: 200, dataIndex: 'title',   menuDisabled: true, sortable: true},
		{header: 'href',    width: 300, dataIndex: 'href',    menuDisabled: true, sortable: true},
		//{header: 'token',   width: 200, dataIndex: 'token',   menuDisabled: true, sortable: true},
		{header: 'date',    width: 170, dataIndex: 'date',    menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

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

		text: 'appdata',
		iconCls: 'install',
		disabled: true

	},'->',{

		text: 'help',
		iconCls: 'help'

	}]

});


amun.content.gadget.form = Ext.extend(amun.common.form, {

	fields: [{

		fieldLabel: 'sort',
		name: 'sort_id'

	},{

		fieldLabel: 'title',
		name: 'title'

	},new Ext.form.ComboBox({

		fieldLabel: 'href',
		hiddenName: 'href',
		store: amun.content.gadget.store_list,
		valueField: 'href',
		displayField: 'title',
		editable: true,
		mode: 'remote',
		emptyText: 'select a gadget or enter a url ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	})]

});


amun.content.gadget.form_detail = Ext.extend(amun.common.form, {

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
			defaults: {

				width: 250

			}

		},
		items: [{

			title: 'General',
			items: [{

				fieldLabel: 'id',
				name: 'id',
				allowBlank: false,
				readOnly: true

			},{

				fieldLabel: 'sort',
				name: 'sort_id'

			},{

				fieldLabel: 'title',
				name: 'title',
				allowBlank: false,
				readOnly: true

			},{

				fieldLabel: 'href',
				name: 'href',
				allowBlank: false,
				readOnly: true

			},{

				fieldLabel: 'token',
				name: 'token',
				allowBlank: false,
				readOnly: true

			},{

				fieldLabel: 'cache',
				xtype: 'checkbox',
				name: 'cache'

			},{

				fieldLabel: 'expire',
				name: 'expire',
				allowBlank: true

			}]

		},{

			title: 'Sourcecode',
			items: [{

				xtype: 'textarea',
				width: 600,
				height: 400,
				fieldLabel: 'xml',
				cls: 'sourcecode',
				name: 'xml',
				readOnly: true

			}]

		}]

	}],

	set_active_tab: function(index){

		this.get(0).setActiveTab(index);

	},

	reset: function(){

		this.get(0).items.each(function(item, i){

			item.items.each(function(item, i){

				item.setValue(null);

			});

		});

		this.getForm().clearInvalid();

	}

});

amun.content.gadget.form_appdata = Ext.extend(amun.common.form, {

	appdata: [],

	fields: [],

	request_appdata: function(id){

		var conn = new Ext.data.Connection();

		conn.request({

			url: amun_url + '/index.php?x=admin/content/gadget/util/appdata',
			method: 'POST',
			params: {id: id},
			scope: this,
			success: function(responseObject){

				var appdata = Ext.decode(responseObject.responseText);
				var appdata = Ext.util.Format.defaultValue(appdata, []);


				// remove old gadgets
				if(this.appdata.length > 0)
				{
					for(var i = 0; i < this.appdata.length; i++)
					{
						this.remove(this.appdata[i], true);
					}
				}


				if(appdata.length > 0)
				{
					for(var i = 0; i < appdata.length; i++)
					{
						var x = this.add(appdata[i]);

						this.appdata.push(x);
					}
				}
				else
				{
					var x = this.add(new Ext.Panel({

						border: false,
						cls: 'content',
						html: 'No appdata available'

					}));

					this.appdata.push(x);
				}


				this.doLayout();

			},
			failure: function(responseObject){

				Ext.MessageBox.alert('failure', 'couldnt request appdata');

			}

		});

	}

});

amun.content.gadget.panel = Ext.extend(Ext.Panel, {

	o_index: null,
	o_form: null,
	o_form_detail: null,
	o_form_appdata: null,

	initComponent: function(){

		this.o_index        = new amun.content.gadget.index();
		this.o_form         = new amun.content.gadget.form();
		this.o_form_detail  = new amun.content.gadget.form_detail();
		this.o_form_appdata = new amun.content.gadget.form_appdata();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.o_index, this.o_form, this.o_form_detail, this.o_form_appdata]

		};

		this.handler_index();
		this.handler_form();
		this.handler_form_detail();
		this.handler_form_appdata();


		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.content.gadget.panel.superclass.initComponent.apply(this, arguments);

		this.addEvents('help');

	},

	handler_index: function(){

		this.o_index.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			grid.row_id = id;

		});

		// add
		this.o_index.getTopToolbar().get(0).on('click', function(){

			this.o_form.url = amun_url + '/index.php/api/content/gadget/add?format=json';

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.o_index.getTopToolbar().get(1).on('click', function(){

			this.o_form_detail.load_data(amun_url + '/index.php?x=admin/content/gadget/util/item&id=' + this.o_index.row_id);

			this.o_form_detail.url = amun_url + '/index.php/api/content/gadget/edit?format=json';

			this.layout.setActiveItem(2);

		}, this);

		// del
		this.o_index.getTopToolbar().get(2).on('click', function(){

			this.o_form_detail.load_data(amun_url + '/index.php?x=admin/content/gadget/util/item&id=' + this.o_index.row_id);

			this.o_form_detail.url = amun_url + '/index.php/api/content/gadget/delete?format=json';

			this.layout.setActiveItem(2);

		}, this);

		// appdata
		this.o_index.getTopToolbar().get(3).on('click', function(){

			this.o_form_appdata.request_appdata(this.o_index.row_id);

			this.o_form_appdata.url = amun_url + '/index.php/api/content/gadget/appdata';

			this.layout.setActiveItem(3);

		}, this);

		// help
		this.o_index.getTopToolbar().get(4).on('click', function(){

			this.fireEvent('help', 'content_gadget');

		}, this);

	},

	handler_form: function(){

		this.o_form.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						amun.content.gadget.store.load({params:{start:0, limit:16}});

						this.o_index.reset();

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

			this.o_index.reset();

			this.layout.setActiveItem(0);

			this.o_form.reset();

		}, this);

	},

	handler_form_detail: function(){

		this.o_form_detail.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						amun.content.gadget.store.load({params:{start:0, limit:16}});

						this.o_index.reset();

						this.layout.setActiveItem(0);

						this.o_form_detail.reset();

						this.o_form_detail.set_active_tab(0);

					}, this);
				}

			}

		}, this);

		this.o_form_detail.on('actionfailed', function(form, action){

			var msg = Ext.util.Format.defaultValue(action.result.message, '');

			if(msg != '')
			{
				Ext.MessageBox.alert('failure', msg);
			}

		}, this);

		this.o_form_detail.on('submited', function(){

			this.o_form_detail.getForm().submit({

				url: this.o_form_detail.url,
				waitMsg: 'sending data ...'

			});

		}, this);

		this.o_form_detail.on('canceled', function(){

			this.o_index.reset();

			this.layout.setActiveItem(0);

			this.o_form_detail.reset();

			this.o_form_detail.set_active_tab(0);

		}, this);

	},

	handler_form_appdata: function(){

		this.o_form_appdata.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						amun.content.gadget.store.load({params:{start:0, limit:16}});

						this.o_index.reset();

						this.layout.setActiveItem(0);

					}, this);
				}

			}

		}, this);

		this.o_form_appdata.on('actionfailed', function(form, action){

			var msg = Ext.util.Format.defaultValue(action.result.message, '');

			if(msg != '')
			{
				Ext.MessageBox.alert('failure', msg);
			}

		}, this);

		this.o_form_appdata.on('submited', function(){

			this.o_form_appdata.getForm().submit({

				url: this.o_form_appdata.url,
				waitMsg: 'sending data ...'

			});

		}, this);

		this.o_form_appdata.on('canceled', function(){

			this.o_index.reset();

			this.layout.setActiveItem(0);

			this.o_form_appdata.reset();

		}, this);

	}

});
*/


