/*
 *  $Id: log.js 209 2011-03-16 13:47:21Z k42b3.x $
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

Ext.ns('amun.system.log');

/**
 * amun.system.log
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://amun.phpsx.org
 * @category   js
 * @version    $Revision: 209 $
 */


amun.system.log.url = amun_url + '/index.php/api/system/log?format=json';


amun.system.log.store = new Ext.data.Store({

	url: get_proxy_url(amun.system.log.url),
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
		{name: 'refId', type: 'int'},
		{name: 'authorDisplayName', type: 'string'},
		{name: 'type', type: 'string'},
		{name: 'table', type: 'string'},
		{name: 'date', type: 'date', dateFormat: 'Y-m-d H:i:s'}

	])

});


amun.system.log.panel_receive = Ext.extend(amun.common.grid, {

	store: amun.system.log.store,
	columns: [

		//{header: 'id', width: 75,  dataIndex: 'id', menuDisabled: true, sortable: true},
		{header: 'RefId', width: 75,  dataIndex: 'refId', menuDisabled: true, sortable: true},
		{header: 'User', width: 150, dataIndex: 'authorDisplayName', menuDisabled: true, sortable: true},
		{header: 'Type', width: 80, dataIndex: 'type', menuDisabled: true, sortable: true},
		{header: 'Table', width: 200, dataIndex: 'table', menuDisabled: true, sortable: true},
		{header: 'Date', width: 170, dataIndex: 'date', menuDisabled: true, sortable: true, renderer: Ext.util.Format.dateRenderer('d-m-Y / H:i:s')}
	],
	options: []

});


amun.system.log.panel = Ext.extend(Ext.Panel, {

	panel_receive: null,

	initComponent: function(){

		this.panel_receive = new amun.system.log.panel_receive();

		var config = {

			layout: 'card',
			margins: '0 0 0 0',
			activeItem: 0,
			border: false,
			items: [this.panel_receive]

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		amun.system.log.panel.superclass.initComponent.apply(this, arguments);

	}

});


