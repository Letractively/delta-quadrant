/*
 *  $Id: calendar.js 117 2010-11-24 20:31:42Z k42b3.x $
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

Ext.ns('psx.plugin.calendar');

/**
 * psx.plugin.calendar
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 117 $
 */

psx.plugin.calendar.store_event = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/calendar/event',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		totalRecords: 'results',
		record: 'item',
		id: 'id'

	},[

		{name: 'id',          type: 'int'},
		{name: 'calendar_id', type: 'int'},
		{name: 'user_id',     type: 'int'},
		{name: 'title',       type: 'string'},
		{name: 'on',          type: 'date',    dateFormat: 'Y-m-d H:i:s'},
		{name: 'repeat',      type: 'int'},
		{name: 'date',        type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


psx.plugin.calendar.store_comment = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/calendar/comment',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		totalRecords: 'results',
		record: 'item',
		id: 'id'

	},[

		{name: 'id',      type: 'int'},
		{name: 'user_id', type: 'int'},
		{name: 'text',    type: 'string'},
		{name: 'date',    type: 'date',     dateFormat: 'Y-m-d H:i:s'}

	])

});


psx.plugin.calendar.store_page = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/calendar/util/page',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.calendar.store_calendar = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/calendar/util/lists',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.calendar.store_at = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/calendar/event/util/at',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'string'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.calendar.store_repeat = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/calendar/event/util/repeat',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'string'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.calendar.index_center = Ext.extend(psx.common.grid, {

	store: psx.plugin.calendar.store_event,
	columns: [

		{header: 'id',          width: 75,  dataIndex: 'id',          menuDisabled: true, sortable: true},
		//{header: 'calendar_id', width: 75,  dataIndex: 'calendar_id', menuDisabled: true, sortable: true},
		//{header: 'user_id',     width: 75,  dataIndex: 'user_id',     menuDisabled: true, sortable: true},
		{header: 'title',       width: 325, dataIndex: 'title',       menuDisabled: true, sortable: true},
		{header: 'on',          width: 100, dataIndex: 'on',          menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y')},
		//{header: 'repeat',      width: 350, dataIndex: 'repeat',      menuDisabled: true, sortable: true},
		{header: 'date',        width: 170, dataIndex: 'date',        menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

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


psx.plugin.calendar.index_east = Ext.extend(Ext.tree.TreePanel, {

	row_id: -1,

	initComponent: function(){

		var bbar = new Ext.Toolbar({

			items: [{

				text: 'add',
				iconCls: 'add',

			},{

				text: 'edit',
				iconCls: 'edit',
				disabled: true,

			},{

				text: 'delete',
				iconCls: 'delete',
				disabled: true,

			}]

		});

		var config = {

			border: false,
			collapsible: false,
			rootVisible: true,
			lines: true,
			autoScroll: true,
			containerScroll: true,
			singleExpand: false,
			useArrows: false,
			bbar: bbar,
			dataUrl: psx_url + '/index.php?x=admin/plugin/calendar',
			root: new Ext.tree.AsyncTreeNode({

				text: 'calendar',
				id: '0'

			})

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		psx.plugin.calendar.index_east.superclass.initComponent.apply(this, arguments);

	},

	onRender:function(){

		this.expandAll();

		psx.plugin.calendar.index_east.superclass.onRender.apply(this, arguments);

	},

	reload: function(){

		var r = this.getRootNode();

		this.getLoader().load(r);

		this.expandAll();

	}

});


psx.plugin.calendar.index_south = Ext.extend(psx.common.grid, {

	store: psx.plugin.calendar.store_comment,
	columns: [

		{header: 'id',      width: 75,  dataIndex: 'id',      menuDisabled: true, sortable: true},
		{header: 'user_id', width: 75,  dataIndex: 'user_id', menuDisabled: true, sortable: true},
		{header: 'text',    width: 350, dataIndex: 'text',    menuDisabled: true, sortable: true},
		{header: 'date',    width: 170, dataIndex: 'date',    menuDisabled: true, sortable: true,  renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: [{

		text: 'edit',
		iconCls: 'edit',
		disabled: true

	},{

		text: 'delete',
		iconCls: 'delete',
		disabled: true

	}]

});


psx.plugin.calendar.index = Ext.extend(Ext.Panel, {

	center: null,
	east: null,
	south: null,

	initComponent: function(){

		this.center = new psx.plugin.calendar.index_center();
		this.east   = new psx.plugin.calendar.index_east();
		this.south  = new psx.plugin.calendar.index_south();

		var config = {

			layout: 'border',
			defaults: {

				collapsible: false,
				split: true,
				border: false

			},
			border: false,
			items: [{

				id: 'center',
				region: 'center',
				layout: 'fit',
				items: [this.center]

			},{

				id: 'east',
				region: 'east',
				layout: 'fit',
				width: 200,
				minSize: 200,
				maxSize: 200,
				collapsible: true,
				items: [this.east]

			},{

				id: 'south',
				title: 'comment',
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

		psx.plugin.calendar.index.superclass.initComponent.apply(this, arguments);

	},

	handler_index: function(){

		this.center.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			psx.plugin.calendar.store_comment.load({params:{event_id: id, start: 0, limit: 16}});

			grid.row_id = id;

		}, this);

		this.east.on('click', function(n){

			this.center.reset();

			if(n.leaf){

				var id = n.id;

				var bbar = this.east.getBottomToolbar();

				bbar.get(1).enable();
				bbar.get(2).enable();

				this.east.row_id = id;

				psx.plugin.calendar.store_event.load({params:{calendar_id: id, start: 0, limit: 16}});

			}
			else
			{
				var bbar = this.east.getBottomToolbar();

				bbar.get(1).disable();
				bbar.get(2).disable();

				this.east.row_id = -1;

				psx.plugin.calendar.store_event.load({params:{start: 0, limit: 16}});
			}

		}, this);

		this.south.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			grid.row_id = id;

		}, this);

	}

});


psx.plugin.calendar.form = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'page',
		hiddenName: 'page_id',
		store: psx.plugin.calendar.store_page,
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

		fieldLabel: 'description',
		name: 'description',
		allowBlank: false,
		height: 200

	})]

});


psx.plugin.calendar.form_event = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'calendar',
		hiddenName: 'calendar_id',
		store: psx.plugin.calendar.store_calendar,
		valueField: 'value',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'select a calendar ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),{

		fieldLabel: 'title',
		name: 'title',
		allowBlank: false

	},{

		fieldLabel: 'on',
		name: 'on',
		xtype: 'datefield',
		format: 'Y-m-d',
		allowBlank: false

	},new Ext.form.ComboBox({

		fieldLabel: 'repeat',
		hiddenName: 'repeat',
		store: psx.plugin.calendar.store_repeat,
		valueField: 'value',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'select a repeat ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),new Ext.form.TextArea({

		fieldLabel: 'description',
		name: 'description',
		allowBlank: false,
		height: 200

	})]

});


psx.plugin.calendar.form_comment = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},{

		fieldLabel: 'event_id',
		name: 'event_id',
		allowBlank: false,

	},new Ext.form.TextArea({

		fieldLabel: 'text',
		name: 'text',
		allowBlank: false,
		height: 200

	})]

});


psx.plugin.calendar.panel = Ext.extend(Ext.Panel, {

	o_index: null,
	o_form: null,
	o_form_event: null,
	o_form_comment: null,

	initComponent: function(){

		this.o_index        = new psx.plugin.calendar.index();
		this.o_form         = new psx.plugin.calendar.form();
		this.o_form_event   = new psx.plugin.calendar.form_event();
		this.o_form_comment = new psx.plugin.calendar.form_comment();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.o_index, this.o_form, this.o_form_event, this.o_form_comment]

		};

		this.handler_index();
		this.handler_form();
		this.handler_form_event();
		this.handler_form_comment();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		psx.plugin.calendar.panel.superclass.initComponent.apply(this, arguments);

	},

	handler_index: function(){

		var center = this.o_index.center.getTopToolbar();
		var east   = this.o_index.east.getBottomToolbar();
		var south  = this.o_index.south.getTopToolbar();

		// add
		center.get(0).on('click', function(){

			this.o_form_event.url = psx_url + '/index.php?x=admin/plugin/calendar/event/add';

			this.layout.setActiveItem(2);

		}, this);

		// edit
		center.get(1).on('click', function(){

			this.o_form_event.url = psx_url + '/index.php?x=admin/plugin/calendar/event/edit';

			this.o_form_event.load_data(psx_url + '/index.php?x=admin/plugin/calendar/event/util/item&id=' + this.o_index.center.row_id);

			this.layout.setActiveItem(2);

		}, this);

		// del
		center.get(2).on('click', function(){

			this.o_form_event.url = psx_url + '/index.php?x=admin/plugin/calendar/event/delete';

			this.o_form_event.load_data(psx_url + '/index.php?x=admin/plugin/calendar/event/util/item&id=' + this.o_index.center.row_id);

			this.layout.setActiveItem(2);

		}, this);


		// add
		east.get(0).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/calendar/add';

			this.layout.setActiveItem(1);

		}, this);

		// edit
		east.get(1).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/calendar/edit';

			this.o_form.load_data(psx_url + '/index.php?x=admin/plugin/calendar/util/item&id=' + this.o_index.east.row_id);

			this.layout.setActiveItem(1);

		}, this);

		// del
		east.get(2).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/calendar/delete';

			this.o_form.load_data(psx_url + '/index.php?x=admin/plugin/calendar/util/item&id=' + this.o_index.east.row_id);

			this.layout.setActiveItem(1);

		}, this);


		// edit
		south.get(0).on('click', function(){

			this.o_form_comment.url = psx_url + '/index.php?x=admin/plugin/calendar/comment/edit';

			this.o_form_comment.load_data(psx_url + '/index.php?x=admin/plugin/calendar/comment/util/item&id=' + this.o_index.south.row_id);

			this.layout.setActiveItem(3);

		}, this);

		// del
		south.get(1).on('click', function(){

			this.o_form_comment.url = psx_url + '/index.php?x=admin/plugin/calendar/comment/delete';

			this.o_form_comment.load_data(psx_url + '/index.php?x=admin/plugin/calendar/comment/util/item&id=' + this.o_index.south.row_id);

			this.layout.setActiveItem(3);

		}, this);

	},

	handler_form: function(){

		this.o_form.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						psx.plugin.calendar.store_event.load({params:{start:0, limit:16}});

						this.o_index.east.reload();

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

			this.layout.setActiveItem(0);

			this.o_form.reset();

		}, this);

	},

	handler_form_event: function(){

		this.o_form_event.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						psx.plugin.calendar.store_event.load({params:{start:0, limit:16}});

						this.o_index.center.reset();

						this.layout.setActiveItem(0);

						this.o_form_event.reset();

					}, this);
				}

			}

		}, this);

		this.o_form_event.on('actionfailed', function(form, action){

			var msg = Ext.util.Format.defaultValue(action.result.message, '');

			if(msg != '')
			{
				Ext.MessageBox.alert('failure', msg);
			}

		}, this);

		this.o_form_event.on('submited', function(){

			this.o_form_event.getForm().submit({

				url: this.o_form_event.url,
				waitMsg: 'sending data ...'

			});

		}, this);

		this.o_form_event.on('canceled', function(){

			this.o_index.center.reset();

			this.layout.setActiveItem(0);

			this.o_form_event.reset();

		}, this);

	},

	handler_form_comment: function(){

		this.o_form_comment.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						psx.plugin.calendar.store_comment.load({params:{event_id: this.o_index.center.row_id, start:0, limit:16}});

						this.o_index.south.reset();

						this.layout.setActiveItem(0);

						this.o_form_comment.reset();

					}, this);
				}

			}

		}, this);

		this.o_form_comment.on('actionfailed', function(form, action){

			var msg = Ext.util.Format.defaultValue(action.result.message, '');

			if(msg != '')
			{
				Ext.MessageBox.alert('failure', msg);
			}

		}, this);

		this.o_form_comment.on('submited', function(){

			this.o_form_comment.getForm().submit({

				url: this.o_form_comment.url,
				waitMsg: 'sending data ...'

			});

		}, this);

		this.o_form_comment.on('canceled', function(){

			this.o_index.south.reset();

			this.layout.setActiveItem(0);

			this.o_form_comment.reset();

		}, this);

	}

});


