/*
 *  $Id: page.js 182 2011-02-26 21:27:21Z k42b3.x $
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

Ext.ns('amun.service.page');

/**
 * amun.service.page
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 182 $
 */


amun.service.page.url = amun_url + '/index.php/api/service/page?format=json';


amun.service.page.store = new Ext.data.Store({

	url: get_proxy_url(amun.service.page.url),
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
		{name: 'pageTitle', type: 'string'},
		{name: 'authorDisplayName', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


psx.plugin.page.store_page = new Ext.data.Store({

	url: amun_url + '/index.php?x=admin/plugin/page/util/page',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.page.index = Ext.extend(psx.common.grid, {

	store: psx.plugin.page.store,
	columns: [

		{header: 'id',      width: 75,  dataIndex: 'id',      menuDisabled: true, sortable: true},
		{header: 'page_id', width: 75,  dataIndex: 'page_id', menuDisabled: true, sortable: true},
		{header: 'user_id', width: 75,  dataIndex: 'user_id', menuDisabled: true, sortable: true},
		{header: 'title',   width: 350, dataIndex: 'title',   menuDisabled: true, sortable: true},
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

	}]

});


psx.plugin.page.form = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'page',
		hiddenName: 'page_id',
		store: psx.plugin.page.store_page,
		valueField: 'value',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'select a page ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'title',
		name: 'title',
		allowBlank: false

	},new Ext.form.TextArea({

		fieldLabel: 'content',
		name: 'content',
		allowBlank: false,
		height: 200

	})]

});


psx.plugin.page.panel = Ext.extend(Ext.Panel, {

	o_index: null,
	o_form: null,

	initComponent: function(){

		this.o_index = new psx.plugin.page.index();
		this.o_form  = new psx.plugin.page.form();

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

		psx.plugin.page.panel.superclass.initComponent.apply(this, arguments);

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

			this.o_form.url = amun_url + '/index.php?x=admin/plugin/page/add';

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.o_index.getTopToolbar().get(1).on('click', function(){

			this.o_form.url = amun_url + '/index.php?x=admin/plugin/page/edit';

			this.o_form.load_data(amun_url + '/index.php?x=admin/plugin/page/util/item&id=' + this.o_index.row_id);

			this.layout.setActiveItem(1);

		}, this);

		// del
		this.o_index.getTopToolbar().get(2).on('click', function(){

			this.o_form.url = amun_url + '/index.php?x=admin/plugin/page/delete';

			this.o_form.load_data(amun_url + '/index.php?x=admin/plugin/page/util/item&id=' + this.o_index.row_id);

			this.layout.setActiveItem(1);

		}, this);

	},

	handler_form: function(){

		this.o_form.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						psx.plugin.page.store.load({params:{start:0, limit:16}});

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

	}

});


