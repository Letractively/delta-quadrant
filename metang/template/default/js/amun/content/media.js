/*
 *  $Id: media.js 191 2011-03-04 22:53:27Z k42b3.x $
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

Ext.ns('amun.content.media');

/**
 * amun.content.media
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 191 $
 */


amun.content.media.url = amun_url + '/index.php/api/content/media?format=json';


amun.content.media.store = new Ext.data.Store({

	url: get_proxy_url(amun.content.media.url),
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
		{name: 'authorDisplayName', type: 'string'},
		{name: 'size', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.content.media.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.content.media.store,
	columns: [

		//{header: 'id', width: 75,  dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'Title', width: 100, dataIndex: 'title', menuDisabled: true, sortable: true},
		{header: 'Author', width: 150, dataIndex: 'authorDisplayName', menuDisabled: true, sortable: true},
		{header: 'Size', width: 100, dataIndex: 'size', menuDisabled: true, sortable: true, renderer: Ext.util.Format.fileSize},
		{header: 'Date', width: 170, dataIndex: 'date', menuDisabled: true, sortable: true, renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}

	],
	options: [{

		text: 'Edit',
		iconCls: 'edit',
		disabled: true

	},{

		text: 'Delete',
		iconCls: 'delete',
		disabled: true

	}]

});


amun.content.media.panel_update = Ext.extend(amun.common.form, {

	url: amun.content.media.url,
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

	},{

		fieldLabel: 'Path',
		name: 'path',
		allowBlank: false

	},{

		fieldLabel: 'Type',
		name: 'type',
		allowBlank: false

	},{

		fieldLabel: 'MIME Type',
		name: 'mime_type',
		allowBlank: false

	}]

});


amun.content.media.panel_delete = Ext.extend(amun.common.form, {

	url: amun.content.media.url,
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

	},{

		fieldLabel: 'Path',
		name: 'path',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'Type',
		name: 'type',
		allowBlank: false,
		disabled: true

	},{

		fieldLabel: 'MIME Type',
		name: 'mime_type',
		allowBlank: false,
		disabled: true

	}]

});


amun.content.media.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,
	panel_update: null,
	panel_delete: null,

	initComponent: function(){

		this.panel_receive = new amun.content.media.panel_receive();
		this.panel_update  = new amun.content.media.panel_update();
		this.panel_delete  = new amun.content.media.panel_delete();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive, this.panel_update, this.panel_delete]

		};

		this.register_events();

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.content.media.panel.superclass.initComponent.apply(this, arguments);

	},

	register_events: function(){

		// edit
		this.panel_receive.getTopToolbar().get(0).on('click', function(){

			this.panel_update.load_data(get_proxy_url(amun.content.media.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(1);

		}, this);

		// del
		this.panel_receive.getTopToolbar().get(1).on('click', function(){

			this.panel_delete.load_data(get_proxy_url(amun.content.media.url + '&filterBy=id&filterOp=equals&filterValue=' + this.panel_receive.row_id));

			this.layout.setActiveItem(2);

		}, this);


		var submited = function(){

			amun.content.media.store.load();

			this.panel_receive.reset();

			this.layout.setActiveItem(0);

		}

		var canceled = function(){

			this.panel_receive.reset();

			this.layout.setActiveItem(0);

		}


		// update
		this.panel_update.on('submited', submited, this);

		this.panel_update.on('canceled', canceled, this);


		// delete
		this.panel_delete.on('submited', submited, this);

		this.panel_delete.on('canceled', canceled, this);
	}

});



