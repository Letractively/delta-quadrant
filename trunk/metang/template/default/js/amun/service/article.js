/*
 *  $Id: article.js 117 2010-11-24 20:31:42Z k42b3.x $
 *
 * psx cms
 * A content managment system based on the psx framework. For the
 * current version and informations visit <http://cms.phpsx.org>
 *
 * Copyright (c) 2009 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of psx cms. psx cms is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * psx cms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with psx cms. If not, see <http://www.gnu.org/licenses/>.
 */

Ext.ns('psx.plugin.article');

/**
 * psx.plugin.article
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://cms.phpsx.org
 * @package    js
 * @version    $Revision: 117 $
 */

psx.plugin.article.store = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/article',
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


psx.plugin.article.store_page = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/article/util/page',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.article.store_comment = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/article/comment',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		totalRecords: 'results',
		record: 'item',
		id: 'id'

	},[

		{name: 'id',      type: 'int'},
		{name: 'user_id', type: 'int'},
		{name: 'text',    type: 'string'},
		{name: 'rate',    type: 'int'},
		{name: 'date',    type: 'date',    dateFormat: 'Y-m-d H:i:s'}

	])

});


psx.plugin.article.store_article = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/article/util/articles',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.article.store_rate = new Ext.data.Store({

	url: psx_url + '/index.php?x=admin/plugin/article/util/rate',
	remoteSort: true,
	reader: new Ext.data.XmlReader({

		record: 'item',
		id: 'value'

	},[

		{name: 'value', type: 'int'},
		{name: 'name',  type: 'string'}

	])

});


psx.plugin.article.index_center = Ext.extend(psx.common.grid, {

	store: psx.plugin.article.store,
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


psx.plugin.article.index_south = Ext.extend(psx.common.grid, {

	store: psx.plugin.article.store_comment,
	columns: [

		{header: 'id',      width: 75,  dataIndex: 'id',      menuDisabled: true, sortable: true},
		{header: 'user_id', width: 75,  dataIndex: 'user_id', menuDisabled: true, sortable: true},
		{header: 'rate',    width: 75,  dataIndex: 'rate',    menuDisabled: true, sortable: true},
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


psx.plugin.article.index = Ext.extend(Ext.Panel, {

	center: null,
	south: null,

	initComponent: function(){

		this.center = new psx.plugin.article.index_center();
		this.south  = new psx.plugin.article.index_south();

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

		psx.plugin.article.index.superclass.initComponent.apply(this, arguments);

	},

	handler_index: function(){

		this.center.on('rowclick', function(grid, row, e){

			grid.enable();

			var data   = grid.getStore();
			var record = data.getAt(row);
			var id     = record.get('id');

			psx.plugin.article.store_comment.load({params:{article_id: id, start:0, limit:16}});

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


psx.plugin.article.form = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'page',
		hiddenName: 'page_id',
		store: psx.plugin.article.store_page,
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

		fieldLabel: 'text',
		name: 'text',
		allowBlank: false,
		height: 200

	})]

});


psx.plugin.article.form_comment = Ext.extend(psx.common.form, {

	fields: [{

		xtype: 'hidden',
		fieldLabel: 'id',
		name: 'id',
		readOnly: true

	},new Ext.form.ComboBox({

		fieldLabel: 'article',
		hiddenName: 'article_id',
		store: psx.plugin.article.store_article,
		valueField: 'value',
		displayField: 'name',
		editable: false,
		mode: 'remote',
		emptyText: 'select a article ...',
		selectOnFocus: true,
		triggerAction: 'all',
		allowBlank: false,
		listWidth: 250

	}),new Ext.form.TextArea({

		fieldLabel: 'text',
		name: 'text',
		allowBlank: false,
		height: 200

	})]

});


psx.plugin.article.panel = Ext.extend(Ext.Panel, {

	o_index: null,
	o_form: null,
	o_form_comment: null,

	initComponent: function(){

		this.o_index       = new psx.plugin.article.index();
		this.o_form        = new psx.plugin.article.form();
		this.o_form_comment = new psx.plugin.article.form_comment();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.o_index, this.o_form, this.o_form_comment]

		};

		this.handler_index();
		this.handler_form();
		this.handler_form_comment();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		psx.plugin.article.panel.superclass.initComponent.apply(this, arguments);

	},

	handler_index: function(){

		var center = this.o_index.center.getTopToolbar();
		var south  = this.o_index.south.getTopToolbar();

		// add
		center.get(0).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/article/add';

			this.layout.setActiveItem(1);

		}, this);

		// edit
		center.get(1).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/article/edit';

			this.o_form.load_data(psx_url + '/index.php?x=admin/plugin/article/util/item&id=' + this.o_index.center.row_id);

			this.layout.setActiveItem(1);

		}, this);

		// del
		center.get(2).on('click', function(){

			this.o_form.url = psx_url + '/index.php?x=admin/plugin/article/delete';

			this.o_form.load_data(psx_url + '/index.php?x=admin/plugin/article/util/item&id=' + this.o_index.center.row_id);

			this.layout.setActiveItem(1);

		}, this);


		// edit
		south.get(0).on('click', function(){

			this.o_form_comment.url = psx_url + '/index.php?x=admin/plugin/article/comment/edit';

			this.o_form_comment.load_data(psx_url + '/index.php?x=admin/plugin/article/comment/util/item&id=' + this.o_index.south.row_id);

			this.layout.setActiveItem(2);

		}, this);

		// del
		south.get(1).on('click', function(){

			this.o_form_comment.url = psx_url + '/index.php?x=admin/plugin/article/comment/delete';

			this.o_form_comment.load_data(psx_url + '/index.php?x=admin/plugin/article/comment/util/item&id=' + this.o_index.south.row_id);

			this.layout.setActiveItem(2);

		}, this);

	},

	handler_form: function(){

		this.o_form.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						psx.plugin.article.store.load({params:{start:0, limit:16}});

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

	},

	handler_form_comment: function(){

		this.o_form_comment.on('actioncomplete', function(form, action){

			if(action.type == 'submit'){

				var msg = Ext.util.Format.defaultValue(action.result.message, '');

				if(msg != '')
				{
					Ext.MessageBox.alert('success', msg, function(btn) {

						psx.plugin.article.store_comment.load({params:{article_id: this.o_index.center.row_id, start:0, limit:16}});

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


