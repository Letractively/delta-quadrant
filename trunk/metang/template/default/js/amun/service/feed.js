/*
 *  $Id: feed.js 117 2010-11-24 20:31:42Z k42b3.x $
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

Ext.ns('psx.plugin.feed');

/**
 * psx.plugin.feed
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 117 $
 */

psx.plugin.feed.store = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/feed',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		totalRecords: 'results',
		record: 'item',
		id: 'id'

	},[

		{name: 'id',      type: 'int'},
		{name: 'page_id', type: 'int'},
		{name: 'user_id', type: 'int'},
		{name: 'title',   type: 'string'},
		{name: 'date',    type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


psx.plugin.feed.store_page = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/feed/util/page',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.feed.index = Ext.extend(psx.common.grid, {

	store: psx.plugin.feed.store,
	columns: [

		{header: 'id',       width: 75,  dataIndex: 'id',       menuDisabled: true, sortable: true},
		{header: 'page_id',  width: 75,  dataIndex: 'page_id',  menuDisabled: true, sortable: true},
		{header: 'user_id',  width: 75,  dataIndex: 'user_id',  menuDisabled: true, sortable: true},
		{header: 'title',    width: 350, dataIndex: 'title',    menuDisabled: true, sortable: true},
		{header: 'date',     width: 170, dataIndex: 'date',     menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

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


psx.plugin.feed.form = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'page',
		hiddenName: 'page_id',
		store: psx.plugin.feed.store_page,
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

	},new Ext.form.ComboBox({

		fieldLabel: 'type',
		hiddenName: 'type',
		store: [['rss2', 'RSS 2'], ['atom1', 'Atom 1']],
		emptyText: 'select a type ...',
		selectOnFocus: true,
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'topic',
		name: 'topic',
		allowBlank: false

	}]

});


psx.plugin.feed.panel = Ext.extend(Ext.Panel, {

	o_index: null,
	o_form: null,

	initComponent: function(){

		this.o_index = new psx.plugin.feed.index();
		this.o_form  = new psx.plugin.feed.form();

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

		psx.plugin.feed.panel.superclass.initComponent.apply(this, arguments);

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

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/feed/add';

			this.layout.setActiveItem(1);

		}, this);

		// edit
		this.o_index.getTopToolbar().get(1).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/feed/edit';

			this.o_form.load_data(psx_url + '/index.php?x=admin/plugin/feed/util/item&id=' + this.o_index.row_id);

			this.layout.setActiveItem(1);

		}, this);

		// del
		this.o_index.getTopToolbar().get(2).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/feed/delete';

			this.o_form.load_data(psx_url + '/index.php?x=admin/plugin/feed/util/item&id=' + this.o_index.row_id);

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

						psx.plugin.feed.store.load({params:{start:0, limit:16}});

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


