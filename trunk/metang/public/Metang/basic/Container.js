/*
 *  $Id$
 *
 * metang
 * An web application to access the API of amun.
 *
 * Copyright (c) 2011 Christoph Kappestein <k42b3.x@gmail.com>
 *
 * This file is part of metang. metang is free software: you can
 * redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 *
 * metang is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with metang. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Metang.basic.Container
 *
 * @author     Christoph Kappestein <k42b3.x@gmail.com>
 * @license    http://www.gnu.org/licenses/gpl.html GPLv3
 * @link       http://code.google.com/p/delta-quadrant/
 * @category   javascript
 * @version    $Revision$
 */
Ext.define('Metang.basic.Container', {
	extend: 'Ext.tab.Panel',

	view: null,
	selectedId: 0,

	initComponent: function(){

		var config = {

			title: 'Content',
			region: 'center',
			margins: '0 0 0 0',
			border: true,
			items: []

		};

		Ext.apply(this, Ext.apply(this.initialConfig, config));

		Metang.basic.Container.superclass.initComponent.apply(this, arguments);


		this.on('tabchange', function(el, newCard, oldCard, opt){

			newCard.load(this.selectedId);

		}, this);

	},

	onLoad: function(uri){

		this.view = this.add(Ext.create('Metang.basic.Grid', {

			title: 'View',
			uri: uri

		}));

		this.view.on('itemclick', function(id){

			this.selectedId = id;

			this.items.get(2).setDisabled(false);
			this.items.get(3).setDisabled(false);

		}, this);

		this.add(Ext.create('Metang.basic.Form', {

			title: 'Create',
			uri: uri + '/form?method=create'

		}));

		this.add(Ext.create('Metang.basic.Form', {

			title: 'Update',
			disabled: true,
			uri: uri + '/form?method=update'

		}));

		this.add(Ext.create('Metang.basic.Form', {

			title: 'Delete',
			disabled: true,
			uri: uri + '/form?method=delete'

		}));

	}

});
